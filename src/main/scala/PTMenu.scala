package com.honey

//import org.dom4j.{DocumentHelper, Element}

import aa.com.util.LogUtil
import android.app.{Dialog, Activity}
import android.view.{Gravity, View}
import android.widget._
import android.view.View.OnClickListener
import android.graphics.{Bitmap, BitmapFactory}
import android.util.Log
import com.loopj.android.http.{RequestParams, AsyncHttpResponseHandler, BinaryHttpResponseHandler}
import android.content.{BroadcastReceiver, Context, Intent}
import java.net.{URL, URLEncoder}
import android.webkit.{HttpAuthHandler, SslErrorHandler, WebViewClient, WebView}
import android.net.http.SslError
import android.os.Environment
import com.honey.util.{ImageUtil, Util}
import com.honey.http.HttpUtil
import scala.xml.Node

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 13-6-25
 * Time: 上午10:12
 * To change this template use File | Settings | File Templates.
 */


object PTMenu {

  val logTabId = "4"

  def filterItem(item:Node, itemId:String):Node = {
    var iii: Node = null
    if(searchItem(item,itemId))
      iii= item
    else{
      val subs = (item\"item").iterator
      while(iii == null && subs.hasNext){
        val s = subs.next()
        if(searchItem(item,itemId))
          iii = item
        else
          iii = filterItem(s,itemId)
      }
    }
    return iii
  }

  def searchItem(p:Node, id:String):Boolean = {
    val idd = p.attribute("id")
    var rrr = false
    if(idd != None && id != null && id.equals(idd.get.toString()))
      rrr = true
    else
      rrr = false
    rrr
  }

//  def elementWithId(root: Element, s: String) : Element = {
//    var menu: Element = null
//      if(root != null && s != null){
//        val iter = root.elementIterator()
//        while(iter.hasNext && menu == null){
//          val ee = iter.next()
//          val eeId = noteValue(ee,"id")
//          if(eeId != null && eeId.equals(s)){
//            menu = ee
//          }
//          else if(ee.elements().size() > 0){
//            menu = elementWithId(ee,s)
//          }
//        }
//      }
//    menu
//  }

//  def menuWithId(id: String) = {
//    var reuse = App.tabs.get(id)
//    if(reuse == null){
//      val t1 = System.currentTimeMillis()
//      val a = elementWithId(App.document,id)
////        App.document.selectSingleNode("//item/item[@id='"+ id + "']");     //TODO, null here
//      val t2 = System.currentTimeMillis()
//      Log.d("aa.com","menuWithId " + (t2 - t1))
//      reuse = a.asInstanceOf[Element]
//    }
//    reuse
//  }
//
//  def menuWithId(root:Element, id: String) = {
//    root.selectSingleNode("//item[@id='"+ id + "']");
//  }

  def findItemWithId(itemId: String) : Node = {
    ((App.ptDocument \\ "item") find((p: scala.xml.Node) => {
      (p \"@id").text == itemId
    })) match {
      case Some(node) =>  node
      case _ => null
    }
  }

  def nodeChild(node: Node) = (node \ "item").toList
  def noteValue(node: Node, key: String) : String = (node \ ("@"+key)).text
//  def noteValue(element: Any, name: String) = {
//    val b = element.asInstanceOf[Element]
//    val s = new String((b attributeValue(name,"")))
//    s
//  }

//  def initHelp(c: Activity,e: Element){
//    val btnBack = c.findViewById(R.id.gBtnGoBack)
//    val btnRelogin = c.findViewById(R.id.gBtnGoRelogin)
//    val title = c.findViewById(R.id.gTvMenuTitle).asInstanceOf[TextView]
//    if (btnBack != null) btnBack.setOnClickListener(new OnClickListener {
//      def onClick(p1: View) {
//        c.finish()
//      }
//    })
//
//    if (btnRelogin != null)  btnRelogin.setOnClickListener(new OnClickListener {
//      def onClick(p1: View) {
//        //TODO
//      }
//    })
//
//    if(title != null){
//      title.setText(PTMenu.noteValue(e,"text"))
//    }
//  }

  def getString(url: String,handler: AsyncHttpResponseHandler)
  {
    Log.d("get getString from host","url is: " + url);
    HttpUtil.get(url,null,handler)
  }

  def ToCast(c: Activity)(s: String){
    Toast.makeText(c,s,Toast.LENGTH_SHORT).show()
    Log.d("DebugToCast",s)
  }

  def qrCodeView(c: Activity with FindView)(e : Node) = {
    val view = c.getLayoutInflater.inflate(R.layout.qrencode,null)
    val text = view.findViewById(R.id.editTextQR).asInstanceOf[EditText]
    val qrBtn = view.findViewById(R.id.btnQrCode)
    val qrImg = view.findViewById(R.id.image_view).asInstanceOf[ImageView]
    var enCode = false
    qrBtn setOnClickListener(new OnClickListener {
      def onClick(p1: View) {
        val s:String = text.getText.toString

        val enCodeText = URLEncoder.encode(s)
        PTGet.imageGetAndRetry(3)(URLS.ptQRen + enCodeText)(null){
          b: Array[Byte] =>
            if(b.length != 0){
              enCode = true
              qrImg.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length))
              Util.saveToInternal(c,"QRCode.Png",b)
              ImageUtil.saveImage(Environment.getExternalStorageDirectory.getPath+"/QRCode.Png",b)
//              if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
//                Environment.getExternalStorageDirectory.getPath
//                Environment.getDownloadCacheDirectory
//
//              }
            }
        } (null)(null)

      }
    })
    qrImg.setOnClickListener(new OnClickListener {
      def onClick(p1: View) {
        if(enCode){
          alertDialog(c)("分享二维码图片？"){
            Util.shareMsg(c,"二维码","二维码","这是我的二维码图片",Environment.getExternalStorageDirectory.getPath+"/QRCode.Png")
          }.show()
        }
      }
    })
    view
  }

//  class GridViewAdapter(c:Activity,list: java.util.List[Element]) extends BaseAdapter{
//    def getView(p1: Int, p2: View, p3: ViewGroup): View = {
//
//      if(p2 == null){
//        val view = c.getLayoutInflater.inflate(R.layout.gridview_itemtemplate,null)
//        val imgV:ImageView = view.findViewById(R.id.gridview_item_funcIcon).asInstanceOf[ImageView]
//        val tv:TextView = view.findViewById(R.id.gridview_item_funcName).asInstanceOf[TextView]
//        val e = list.get(p1)
//        val img = PTMenu.noteValue(e,"img")
//        getIcon(c)(img)(imgV)
//        tv.setText(PTMenu.noteValue(e,"text"))
//        view
//      }else{
//        p2
//      }
//    }
//    def getItem(p1: Int): AnyRef = Nil
//
//    def getItemId(p1: Int): Long = 0
//
//    def getCount: Int = list.size
//  }
//
//
//  class ListViewAdapter(c:Activity, list: java.util.List[Element]) extends BaseAdapter{
//    def getView(p1: Int, p2: View, p3: ViewGroup): View = {
//
//      if(p2 == null){
//        val view = c.getLayoutInflater.inflate(R.layout.listitemtemplate,null)
//        val tv:TextView = view.findViewById(R.id.lItemTitle).asInstanceOf[TextView]
//        val e = list.get(p1)
//        tv.setText(PTMenu.noteValue(e,"text"))
//        view
//      }else{
//        p2
//      }
//    }
//    def getItem(p1: Int): AnyRef = Nil
//
//    def getItemId(p1: Int): Long = 0
//
//    def getCount: Int = list.size
//  }
//
//  class MenuItemListener(list: java.util.List[Element])(navigate: Element => Any) extends android.widget.AdapterView.OnItemClickListener{
//    def onItemClick(p1: AdapterView[_], p2: View, p3: Int, p4: Long) {
//      //      <item id="1.1" upid="1" type="list" img="account_mgr.png" msg="goListMenu(1.1)"
//      //            text="账户管理">
//      val e = list.get(p3);
//      navigate(e)
//    }
//  }

  def getButtonIcon(c: Activity)(icon: String)(forImg: ImageButton)
  {
    val input = Util.getFromInternal(c,icon)
    if (input != null){
      forImg setImageBitmap BitmapFactory.decodeStream(input)
    } else{
      PTGet.imageGetAndRetry(3)(URLS.ptIcon+icon)(null){
        b: Array[Byte] =>
          if(b.length != 0){
            forImg.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length))
            Util.saveToInternal(c,icon,b)
          }
      }(null)(null)
    }
  }

  def getIcon(c: Activity)(icon: String)(forImg: ImageView)
  {
    val input = Util.getFromInternal(c,icon)
    if (input != null){
      forImg setImageBitmap BitmapFactory.decodeStream(input)
    } else{
      PTGet.imageGetAndRetry(3)(URLS.ptIcon+icon)(null){
        b: Array[Byte] =>
          if(b.length != 0){
            forImg.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length))
            Util.saveToInternal(c,icon,b)
          }
      }(null)(null)
    }
  }

  def messageAlertDialog(c: Activity)(message: String)(action: => Any) = {
    val ppd = new Dialog(c,R.style.CustomProgressDialog)
    ppd.setContentView(R.layout.alterdialog)
    ppd.getWindow().getAttributes().gravity = Gravity.CENTER
    ppd.findViewById(R.id.alertTitle).asInstanceOf[TextView].setText("提示")
    ppd.findViewById(R.id.message).asInstanceOf[TextView].setText(message)
    ppd.findViewById(R.id.buttonOK).setVisibility(View.GONE)
    ppd.findViewById(R.id.buttonPositive).asInstanceOf[Button].setText("确定")
    ppd.findViewById(R.id.buttonPositive).setOnClickListener(new OnClickListener {
      def onClick(p1: View) {
        ppd.dismiss()
        action
      }
    });
    ppd.findViewById(R.id.buttonNegative).setVisibility(View.GONE)
    ppd
  }

  def ptAlertDialog(c: Activity)(message: String)(action: => Any)(cancel: => Any) = {
    val ppd = new Dialog(c,R.style.CustomProgressDialog)
    ppd.setContentView(R.layout.alterdialog)
    ppd.getWindow().getAttributes().gravity = Gravity.CENTER
    ppd.findViewById(R.id.alertTitle).asInstanceOf[TextView].setText("提示")
    ppd.findViewById(R.id.message).asInstanceOf[TextView].setText(message)
    ppd.findViewById(R.id.buttonOK).setVisibility(View.GONE)
    ppd.findViewById(R.id.buttonPositive).asInstanceOf[Button].setText("确定")
    ppd.findViewById(R.id.buttonPositive).setOnClickListener(new OnClickListener {
      def onClick(p1: View) {
        ppd.dismiss()
        action
      }
    });
    ppd.findViewById(R.id.buttonNegative).asInstanceOf[Button].setText("取消")
    ppd.findViewById(R.id.buttonNegative).setOnClickListener(new OnClickListener {
      def onClick(p1: View) {
        ppd.dismiss()
        cancel
      }
    });
    ppd
  }

  def alertDialog(c: Activity)(message: String)(action: => Any) = {
    val ppd = new Dialog(c,R.style.CustomProgressDialog)
    ppd.setContentView(R.layout.alterdialog)
    ppd.getWindow().getAttributes().gravity = Gravity.CENTER
    ppd.findViewById(R.id.alertTitle).asInstanceOf[TextView].setText("提示")
    ppd.findViewById(R.id.message).asInstanceOf[TextView].setText(message)
    ppd.findViewById(R.id.buttonOK).setVisibility(View.GONE)
    ppd.findViewById(R.id.buttonPositive).asInstanceOf[Button].setText("确定")
    ppd.findViewById(R.id.buttonPositive).setOnClickListener(new OnClickListener {
      def onClick(p1: View) {
        ppd.dismiss()
        action
      }
    });
    ppd.findViewById(R.id.buttonNegative).asInstanceOf[Button].setText("取消")
    ppd.findViewById(R.id.buttonNegative).setOnClickListener(new OnClickListener {
      def onClick(p1: View) {
        ppd.dismiss()
      }
    });
    ppd
  }

  def showAboutAlertDialog(c: Activity){
    val ppd = new Dialog(c,R.style.CustomProgressDialog)
    ppd.setContentView(R.layout.about)
    ppd.findViewById(R.id.buttonOK).setOnClickListener(new OnClickListener {
      def onClick(p1: View) {
        ppd.dismiss()
      }
    })
    ppd.show()
  }

  def progressAlertDialog(c: Activity)(message: String) = {
    val ppd = new Dialog(c,R.style.CustomProgressDialog)
    ppd.setContentView(R.layout.alert_dialog_progress)
    ppd.setCancelable(false)
    ppd.findViewById(R.id.message).asInstanceOf[TextView].setText(message)
    ppd.getWindow().getAttributes().gravity = Gravity.CENTER
    ppd
  }

  def onClickListener(action: => Any) = {
    new OnClickListener() {
      def onClick(p1: View) = {
        action
      }
    }
  }

  def webView(c:Activity)
             (node : Node)
             (goBack: Node => Any) = {
    val view = c.getLayoutInflater.inflate(R.layout.webviewtemplate,null)
    val web = view.findViewById(R.id.wWebview).asInstanceOf[WebView]
    val load = view.findViewById(R.id.webLoadingLayout)
    val top = view.findViewById(R.id.gTopFrame)
    var url = PTMenu.noteValue(node,"msg")
    val t = PTMenu.noteValue(node,"type")
    if("func".equals(t) || "funcc".equals(t)){
      url = url.replace("Post(","")
      url = url.replace(")","")
//      top.setVisibility(View.GONE)
    }
    else if("web".equals(t)){
      url = url.replace("PostRequest(","")
      url = url.replace(")","")
      url = HttpUtil.BASE_URL + url
    }
    web.requestFocus()
    WebView.enablePlatformNotifications();
    web.setScrollBarStyle(0);
    val localWebSettings = web.getSettings();
    localWebSettings.setJavaScriptEnabled(true);
    localWebSettings.setBuiltInZoomControls(true);
    localWebSettings.setSupportZoom(true);
    localWebSettings.setAllowFileAccess(true);
    localWebSettings.setCacheMode(1)
    web.setSaveEnabled(false)
    web.setWebViewClient(new WebViewClient()
    {
      override def shouldOverrideUrlLoading(view: WebView, url: String): Boolean = {

        var flag = false
        if(url.startsWith("mailto:") || url.startsWith("tel:"))
        {
          flag = true;
        } else if ("http://cmbandroid/tool".equals(url))
        {
          val upId = PTMenu.noteValue(node,"upid")
          val ee = PTMenu.findItemWithId(upId)
          goBack(ee)
          flag = true;
        }
        return flag;

        super.shouldOverrideUrlLoading(view, url)
      }

      override def onPageStarted(webview1: WebView, url: String, favicon: Bitmap) {
        if(load != null)
        {
          webview1.setVisibility(View.GONE);
          load.setVisibility(View.VISIBLE);
        }
        else webview1.setVisibility(View.GONE);
        super.onPageStarted(webview1, url, favicon)
      }

      override def onPageFinished(webview1: WebView, url: String) {
        if(load != null)
        {
          load.setVisibility(View.GONE)
          webview1.setVisibility(View.VISIBLE)
          webview1.requestFocus()
        }
        super.onPageFinished(webview1, url)
      }

      override def onReceivedError(webview1: WebView, errorCode: Int, description: String, failingUrl: String) {
        super.onReceivedError(webview1, errorCode, description, failingUrl)
      }

      override def onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        super.onReceivedSslError(view, handler, error)
      }

      override def onReceivedHttpAuthRequest(view: WebView, handler: HttpAuthHandler, host: String, realm: String) {
        super.onReceivedHttpAuthRequest(view, handler, host, realm)
      }
    })
    web.loadUrl(url)
    view
  }


  def welcomeView(c:Activity)= {
    val view = c.getLayoutInflater.inflate(R.layout.welcome,null)
    val webView = view.findViewById(R.id.wWebview).asInstanceOf[WebView]
    webViewFormat(webView)("")(null)
    view
  }

  def webViewFormat(web: WebView)(url: String)(loadFinish: => Any) {
    web.requestFocus()
    WebView.enablePlatformNotifications();
    web.setScrollBarStyle(0);
    val localWebSettings = web.getSettings();
    localWebSettings.setJavaScriptEnabled(true);
    localWebSettings.setBuiltInZoomControls(true);
    localWebSettings.setSupportZoom(true);
    localWebSettings.setAllowFileAccess(true);
    localWebSettings.setCacheMode(1)
    web.setSaveEnabled(false)
    web.setWebViewClient(new WebViewClient()
    {
      override def shouldOverrideUrlLoading(view: WebView, url: String): Boolean = {
        super.shouldOverrideUrlLoading(view, url)
      }

      override def onPageStarted(webview1: WebView, url: String, favicon: Bitmap) {
        super.onPageStarted(webview1, url, favicon)
      }

      override def onPageFinished(webview1: WebView, url: String) {
        if(loadFinish != null)
          loadFinish
        super.onPageFinished(webview1, url)
      }

      override def onReceivedError(webview1: WebView, errorCode: Int, description: String, failingUrl: String) {
        super.onReceivedError(webview1, errorCode, description, failingUrl)
      }

      override def onReceivedSslError(view: WebView, handler: SslErrorHandler, error: SslError) {
        super.onReceivedSslError(view, handler, error)
      }

      override def onReceivedHttpAuthRequest(view: WebView, handler: HttpAuthHandler, host: String, realm: String) {
        super.onReceivedHttpAuthRequest(view, handler, host, realm)
      }
    })
    web.loadUrl(url)
  }

}



object PTGet{

  import scala.concurrent.future
  import scala.concurrent.ExecutionContext.Implicits.global

  def ptFutureGet(url: String,
              success: String => Any,
                 fail: String => Any){
  val f : scala.concurrent.Future[String] = future {
    commonGet(url)
//    val conn = new URL(HttpUtil.BASE_URL+url).openConnection()
//    conn.connect()
//    Util.convertStreamToString(conn.getInputStream)
  }
    f onFailure {
      case t => fail(t.getMessage)
    }

    f onSuccess {
      case s => success(s)
    }
  }


  def imageGetAndRetry(retry: Int)(url: String)(start: Any)(success: Array[Byte] => Any) (fail: String => Any) (finish: String => Any)
  {
    val args = Array("image/png","image/jpeg","jepg/png")
    var isOnSuccess = false;

    HttpUtil.get(url, null, new BinaryHttpResponseHandler(args){
      override def onStart() {
        super.onStart()
        isOnSuccess = true
        if(start != null)
          start
      }

      override def onSuccess(s: Array[Byte]) {
        super.onSuccess(s)
        if(success != null)
          success(s)
      }
      override def onFailure(error: Throwable, content: String) {
        super.onFailure(error, content)
        if(fail != null && retry <= 0)
          fail("pendding fail reason")
        else {
          Log.d("imageGetAndRetry Retry","retry is" + retry)
          imageGetAndRetry(retry - 1)(url)(start)(success)(fail)(finish)
        }
      }
      override def onFinish() {
        super.onFinish()
        if(isOnSuccess && finish != null)
          finish("success")
        else if(finish != null && retry <= 0)
          finish("retry end")
      }
    })
  }

  def commonGet2(url: String,success: String => Any,fail: String => Any,finish: String => Any)
  {
    commonGetAndRetry(0)(url)(success)(fail)(finish)
  }

  /** *
    * common get methods to host,set retry times to 3
    */
  def commonGet(url: String)(success: String => Any) (fail: String => Any) (finish: String => Any)
  {
    commonGetAndRetry(0)(url)(success)(fail)(finish)
  }

  def commonGetAndRetry(retry: Int)(url: String)(success: String => Any) (fail: String => Any) (finish: String => Any)
  {
    var isOnSuccess = false;
    HttpUtil.get(url, null, new AsyncHttpResponseHandler(){
      override def onSuccess(s: String) {
        super.onSuccess(s)
        isOnSuccess = true
        if(success != null)
          success(s)
      }
      override def onFailure(error: Throwable, content: String) {
        super.onFailure(error, content)
        if(retry <= 0) {
          Log.d("commonGetAndRetry Retry","fail")
          if(fail != null)  fail("pendding fail reason")
        }
        else {
          Log.d("commonGetAndRetry Retry","retry is" + retry)
          commonGetAndRetry(retry - 1)(url)(success)(fail)(finish)
        }
      }
      override def onFinish() {
        super.onFinish()
        if(isOnSuccess && finish != null)
          finish("success")
        else if(finish != null && retry <= 0)
          finish("retry end")
      }
    })
  }
}

object PTPost{


  def ptPost(cc: Activity)(url: String)(map: Map[String,String])(success: Node => Any){
    val form = new RequestParams
    for(k <- map.keys){
      form.put(k,map.get(k).get)
    }
    commonPostAndRetry(0)(url)(form)(null)(success)(null)(null)
  }

  def ptPostWithProcess(cc: Activity)(url: String)(map: Map[String,String])(success: Node => Any){

    val form = new RequestParams
     for(k <- map.keys){
         form.put(k,map.get(k).get)
    }
    var pop: Dialog = null
    val start = {
      pop = PTMenu.progressAlertDialog(cc)("通讯中...")
      pop.show()
    }
    val fail = (s: String) => {
      pop.dismiss()
      PTMenu.messageAlertDialog(cc)(s){}.show()
    }
    val finish = (s: String) => {
      pop.dismiss()
    }
    commonPostAndRetry(0)(url)(form)(start)(success)(fail)(finish)
  }

  def commonPostAndRetry(retry: Int)(url: String)(form: RequestParams)(start: => Any)(success: Node => Any) (fail: String => Any) (finish: String => Any)
  {
    HttpUtil.post(url,form,new AsyncHttpResponseHandler()
    {
      var isOnSuccess: Boolean = false;
      override def onStart() {
        super.onStart()
        if(start != null)
          start
      }
      override def onSuccess(p1: String) {
        val response = scala.xml.XML.loadString(p1)
        isOnSuccess = true
        if(success != null)
          success(response)
      }

      override def onFailure(error: Throwable, content: String) {
        super.onFailure(error, content)
        if(retry <= 0) {
          Log.d("commonPostAndRetry Retry","fail")
          if(fail != null)  fail("pendding fail reason")
        }
        else {
          Log.d("commonPostAndRetry Retry","retry is" + retry)
          commonPostAndRetry(retry - 1)(url)(form)(start)(success)(fail)(finish)
        }
      }
      override def onFinish() {
        super.onFinish()
        if(isOnSuccess && finish != null)
          finish("success")
        else if(finish != null && retry <= 0)
          finish("retry end")
      }
    })
  }
}


object URLS{
  val ptUUID = "mobileuuid"
  val ptMMD5 = "mobileitemmd5?client=Android&file=CommonMenu.xml"
  val ptMenu = "mobileitem?client=Android&file=CommonMenu.xml"
  val ptIcon = "mobileitem?client=Android&file="
  val ptQRen = "QREncode?text="
  val ptCapt = "mobilecaptcha/"
  val ptPostLogin = "mobilelogin"
}

class MyReceiver(action:Intent => Any) extends BroadcastReceiver {
  def onReceive(p1: Context, p2: Intent) {
    action(p2)
  }
}
