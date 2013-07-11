package com.honey

import _root_.com.honey.http._
import _root_.com.honey.util._
import aa.com.util.LogUtil
import android.app.{ActivityGroup, Activity, TabActivity}
import android.widget._
import android.content.res.{Configuration, Resources}
import android.os.{Message, Handler, Bundle}
import android.content.{IntentFilter, Intent}
import android.view._
import org.dom4j.Element
import java.util.Observable
import android.util.Log
import android.graphics.BitmapFactory
import android.view.View.OnClickListener
import java.io.{InputStreamReader, BufferedReader}
import com.laomo.zxing.CaptureActivity
import org.dom4j.io.SAXReader
import android.webkit.WebView

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 13-6-24
 * Time: 下午2:50
 * To change this template use File | Settings | File Templates.
 */
trait FindView extends Activity {

  def findView[WidgetType](id: Int): WidgetType = {
    findViewById(id).asInstanceOf[WidgetType]
  }
  def button(id: Int): Button = findView[Button](id)
  def textView(id: Int): TextView = findView[TextView](id)

  def button(id: Int,v: View): Button = v.findViewById(id).asInstanceOf[Button]
  def textView(id: Int,v: View): TextView = v.findViewById(id).asInstanceOf[TextView]

  //not work
//  implicit def onClickWrapper(view: Button) = new {
//    def onClick(action: => Any) = {
//      view.setOnClickListener(new OnClickListener() {
//        def onClick(p1: View) = {
//          action
//        }
//      })
//    }
//  }
}


object App{
  var shareContext: PTApp =_
  var shareTab: PTTab = _
  var document: Element =_
  var tabs: java.util.Map[String,Element] =_

}

class PTApp extends android.app.Application{

  var session: java.util.Map[String, String]= _

  override def onCreate() {
    super.onCreate()
    App.shareContext = this;
    App.tabs = new java.util.HashMap[String,Element];
    session = new java.util.HashMap[String, String]
//    menuInit()
    LogUtil.log(this.getClass.getName + "on onCreate")
  }



  override def onLowMemory() {
    super.onLowMemory()
    LogUtil.log(this.getClass.getName + "on onLowMemory")
    LogUtil.logObj(this.getClass.getName + "on onLowMemory")(Array{App.document ; App.shareTab ; App.shareContext})
  }

  override def onConfigurationChanged(newConfig: Configuration) {
    super.onConfigurationChanged(newConfig)
  }

  override def onTerminate() {
    super.onTerminate()
    LogUtil.log("PTApp on onTerminate")
    LogUtil.logObj("PTApp on onLowMemory")(Array{App.document ; App.shareTab ; App.shareContext})
  }

  def menuInit(){
    val input = Util.getFromInternal(App.shareContext, "CommonMenu.xml");
    if(input != null){
      val saxReader = new SAXReader();
      App.document = saxReader.read(input).getRootElement;
    }
  }

  def sessionClean {
    session.clear()
  }

  def putToSession(key: String,value: String )
  {
    if(key != null && value != null)
      session.put(key,value);
  }

  def getFromSession(key: String)=
  {
    session.get(key);
  }

  def isSessionValidid() =
  {
    if (session.get("token") != null)
      true;
    else
      false;
  }
}

class PTActivity extends ActivityGroup{
  override def onStart() {
    super.onStart()
    LogUtil.log(this.getClass.getName + "on onStart")
  }

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    LogUtil.log(this.getClass.getName + "on Create")
  }

  override def onResume() {
    super.onResume()
    LogUtil.log(this.getClass.getName + "on onResume")
  }

  override def onPause() {
    super.onPause()
    LogUtil.log(this.getClass.getName + "on onPause")
  }

  override def onStop() {
    super.onStop()
    LogUtil.log(this.getClass.getName + "on onStop")
  }

  override def onDestroy() {
    super.onDestroy()
    LogUtil.log(this.getClass.getName + "on onDestroy")
  }

  override def onLowMemory() {
    super.onLowMemory()
    LogUtil.log(this.getClass.getName + "on onLowMemory")
  }
}

class PTLoading extends PTActivity{

  var menuMD5: String= _
  var localMenuMD5: String= _
  var handler: LoadHandler =_

  def getLocalMenuMD5
  {
     val input = Util.getFromInternal(this,"CommonMenuMD5")
    if(input != null){
      val is = new InputStreamReader(input);
      val sb = new java.lang.StringBuilder();
      val br = new BufferedReader(is);
      var read = br.readLine();
      while(read != null) {
        sb.append(read);
        read = br.readLine();
      }
      localMenuMD5 = sb.toString
    }
    else
      localMenuMD5 = null
  }

  def tabInit{
    PTMenu.menuWithId("1")
    PTMenu.menuWithId("2")
    PTMenu.menuWithId("4")
    PTMenu.menuWithId("9")
  }

  def home{
    val i = new Intent(this,classOf[PTTab])
    startActivity(i)
//    finish()
  }

  override def onResume() {
    super.onResume()
//    handler.post(new MyThread)
//    if(URLUtil.isNetworkUrl())
    handler.postDelayed(new MyThread,1*1000)
    //    getLocalMenuMD5
//    getMenuMD5
//    home
  }

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.splash)
    handler = new LoadHandler
  }

  class LoadHandler extends Handler{
    override def handleMessage(msg: Message) {
      if(msg.what == 1)
        home
      else
        PTMenu.alertDialog(PTLoading.this)("获取菜单信息失败，是否重试？"){
//          handler.post(new MyThread)
          handler.postDelayed(new MyThread,1*1000)
        }.show()
      super.handleMessage(msg)
    }
  }

  class MyThread extends Thread{
    override def run() {

      val message = new Message()
      message.what = 0
      getLocalMenuMD5

      PTGet.ptGet(URLS.ptMMD5){
        s: String =>
          menuMD5 = s
          Log.d("ptGet","ptMMD5" + s)
          if(s == null){
            message.what = 0
          }
          else if(!s.equals(localMenuMD5)){
//            PTMenu.ToCast(PTLoading.this) ("Menu changed.Success get Menu MD5 value from host" + s)
            // get menu
            PTGet.ptGet(URLS.ptMenu){
              menu: String =>
                Util.saveToInternal(PTLoading.this,"CommonMenu.xml",menu.getBytes)
                if(menuMD5 != null)
                  Util.saveToInternal(PTLoading.this,"CommonMenuMD5",menuMD5.getBytes)
                message.what = 1
            }
          } else{
            message.what = 1
          }
          handler.sendMessage(message)
      }
    }
  }



}


class PTTab extends TabActivity{

  var tabHost: TabHost = _
  var res: Resources = _

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.main)
    App.shareContext.menuInit()
    res     = getResources()
    App.shareTab = this
    tabHost = getTabHost()
    tabHost.addTab(loginTab)
    tabHost.addTab(newSpecWithId("1"))
    tabHost.addTab(newSpecWithId("2"))
    tabHost.addTab(newSpecWithId("9"))
    tabHost.setCurrentTab(0)
  }



  def newSpecWithId(id: String) = {
    val intent = new Intent().setClass(this, classOf[PTUI])
    val menu1 = PTMenu.menuWithId(id)
    val name1 = PTMenu.noteValue(menu1,"Name")
    val text1 = PTMenu.noteValue(menu1,"text")
    val img = PTMenu.noteValue(menu1,"img")
    intent.putExtra("superId",id)
    val tabInd = getLayoutInflater.inflate(R.layout.tab_indicator, null);
    tabInd.findViewById(R.id.icon).setBackgroundResource(getImgIdWithName(img))
    tabInd.findViewById(R.id.title).asInstanceOf[TextView].setText(text1)
    val spec = tabHost.newTabSpec(name1).setIndicator(tabInd).setContent(intent)
    spec
  }

  def loginTab = {
    val intent = new Intent().setClass(this, classOf[PTLogin])
    val menu1 = PTMenu.menuWithId("4")
    val name1 = PTMenu.noteValue(menu1,"Name")
    val text1 = PTMenu.noteValue(menu1,"text")
    val img = PTMenu.noteValue(menu1,"img")
    val tabInd = getLayoutInflater.inflate(R.layout.tab_indicator, null);
    tabInd.findViewById(R.id.icon).setBackgroundResource(getImgIdWithName(img))
    tabInd.findViewById(R.id.title).asInstanceOf[TextView].setText(text1)
    val spec = tabHost.newTabSpec("login").setIndicator(tabInd).setContent(intent)
    spec
  }



  def updateTab(id: Int,intent: Intent){

  }

  def getImgIdWithName(imgName: String) = {
    res.getIdentifier(imgName.toLowerCase(),
      "drawable", this.getPackageName())
  }

  override def onCreateOptionsMenu(menu: Menu): Boolean = {
    super.onCreateOptionsMenu(menu)
    getMenuInflater.inflate(R.menu.containermenu,menu)
    true
  }


  def exit{
    PTMenu.alertDialog(this)("您确定要退出吗？"){
      finish()
    }.show()
  }

  def about{
    PTMenu.showAboutAlertDialog(this)
  }

  override def onOptionsItemSelected(item: MenuItem): Boolean = {
    item.getItemId match {
      case R.id.about => about
      case R.id.exit  => exit
    }
    true
  }

  def reset{
//    tabHost.clearAllTabs()
//    tabHost.addTab(loginTab)
//    tabHost.addTab(newSpecWithId("1"))
//    tabHost.addTab(newSpecWithId("2"))
//    tabHost.addTab(newSpecWithId("9"))
    //    tabHost.addTab(tabWithId("4"))
    tabHost.setCurrentTab(0)
  }
}


class PTUI extends PTActivity with FindView with java.util.Observer{

//  var current: Element = _
//  var currentView: View = _
  var flipper: ViewFlipper = _
  var receiver: MyReceiver =_
  var superMenuId: String=_
  var loginReceiver: MyReceiver =_
  var navigations: java.util.List[Element]=_

  def update(p1: Observable, p2: Any) {

  }

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.ptmain)
    navigations = new java.util.ArrayList[Element]()
    val superId = getIntent.getStringExtra("superId")
    superMenuId = superId
    val e = PTMenu.menuWithId(superId).asInstanceOf[Element]
    flipper = findViewById(R.id.flipper).asInstanceOf[ViewFlipper]
    navigate(e)
    receiver = new MyReceiver(sessionHandler)
    registerReceiver(receiver, new IntentFilter("cc.ptlogout"))
  }


  override def onDestroy() {
    super.onDestroy()
    unregisterReceiver(receiver)
    if(loginReceiver != null)
      unregisterReceiver(loginReceiver)
  }

  override def onResume() {
    initTopBar
    super.onResume()
  }

  val sessionHandler = (intent:Intent) => {
    if(PTMenu.logTabId.equals(superMenuId))
    {
      //do nothing,can't finish
    }
    else if(flipper.getChildCount - 1 > 0){
      flipper.removeViews(1,flipper.getChildCount - 1)

      val e = navigations.get(0)
      navigations.clear()
      navigations.add(e)

      flipper.setAnimation(null)
      showPrex()
    }
  }
  def initTopBar{
      if(flipper.getChildCount > 0 && navigations.size() > 0)
        navTopBar(navigations.get(navigations.size() - 1))(flipper.getCurrentView)
  }

  def navTopBar(e1: Element)(v: View){
    if(e1 != null && v != null){
      val superId = PTMenu.noteValue(e1,"id")
      val toplogo = v.findViewById(R.id.view_titlebar_logo)
      val topnav = v.findViewById(R.id.view_titlebar_func)

      // is sub zxing.view?
      if(toplogo != null && topnav!= null){
        if(App.shareContext.isSessionValidid){
          topnav.setVisibility(View.VISIBLE)
          toplogo.setVisibility(View.GONE)
        }
        else if(superId.length == 1){
          topnav.setVisibility(View.GONE)
          toplogo.setVisibility(View.VISIBLE)
        }
        else {
          topnav.setVisibility(View.VISIBLE)
          toplogo.setVisibility(View.GONE)
        }
      }

      val btnBack = button(R.id.gBtnGoBack,v)
      val btnRelogin = button(R.id.gBtnGoRelogin,v)
      //TODO
      if(btnBack != null && superId.length == 1 ){
        btnBack.setVisibility(View.GONE)
      }
      if (btnBack != null)
        btnBack.setOnClickListener(PTMenu.onClickListener({showPrex()}))
      if (btnRelogin != null){
        if(!App.shareContext.isSessionValidid)
          btnRelogin.setText("登录")
        else
          btnRelogin.setText("退出")

        btnRelogin.setOnClickListener(PTMenu.onClickListener({
          if(App.shareContext.isSessionValidid){
            logout
          } else{
            App.shareTab.tabHost.setCurrentTab(0)
          }
        }))
      }

      val title = textView(R.id.gTvMenuTitle,v)
      if(title != null){
        title.setText(PTMenu.noteValue(e1,"text"))
      }
    }
  }

  def logout{
    PTMenu.alertDialog(App.shareTab)("您确定需要重新登录吗?"){
      sendBroadcast(new Intent("cc.ptlogout"))
      App.shareContext.sessionClean
      App.shareTab.reset
    }.show()
  }

  override def onKeyDown(keyCode: Int, event: KeyEvent): Boolean = {
    if(keyCode == KeyEvent.KEYCODE_BACK)
     {
       showPrex()
       return true
    }
    false
  }

  def navigate(e: Element)
  {
    val t1 = System.currentTimeMillis()
    val t =  PTMenu.noteValue(e,"type")
    val id =  PTMenu.noteValue(e,"id")
    val text1 = PTMenu.noteValue(e,"text")

    var toView: View = null
    Log.d("click item ",t + text1)
    if("0".equals(id))
    {
      //log out?
      logout
      return
    }

    t match {
      case "grid" =>
        if(PTMenu.logTabId.equals(id))
          toView = welcomeView(e)
        else
          toView = gridView(e)
        flipper.addView(toView)
      case "list" =>
        toView = listView(e)
        flipper.addView(toView)
      case "func" =>
        toView = PTMenu.webView(this)(e){
          ee: Element =>
            showPrex()
        }
        flipper.addView(toView)
      case "funcc" =>
        toView = PTMenu.webView(this)(e){
          ee: Element =>
            showPrex()
        }
        flipper.addView(toView)
      case "web" =>
        // need login
        if(!App.shareContext.isSessionValidid()){
            PTMenu.alertDialog(PTUI.this)("该功能要求使用登陆，您当前还没有登陆，您需要登陆吗？"){
              val currentTab = App.shareTab.tabHost.getCurrentTab
              val toE = e
              App.shareTab.tabHost.setCurrentTab(0)
              loginReceiver = new MyReceiver({
                action:Intent =>
                  unregisterReceiver(loginReceiver)
                  App.shareTab.tabHost.setCurrentTab(currentTab)
                  toView = PTMenu.webView(this)(toE){
                    ee: Element=>
                      showPrex()
                  }
                  flipper.addView(toView)
                  navigations.add(toE)
                  showNext
              })
              registerReceiver(loginReceiver,new IntentFilter("cc.login"))
            }.show()
        } else{
          toView = PTMenu.webView(this)(e){
            ee: Element=>
              showPrex()
          }
          flipper.addView(toView)
        }
      case "qrencode" =>
        toView = PTMenu.qrCodeView(this)(e)
        flipper.addView(toView)
      case "qrdecode" =>
        val intent = new Intent(this,classOf[CaptureActivity])
//        toView = getLocalActivityManager().startActivity("qrdecode", intent).getDecorView()
//        flipper.addView(toView)
        startActivity(intent)
      case _ =>
        PTMenu.messageAlertDialog(this)("该功能还未实现"){}.show()
        return

    }
    if(toView != null){
      navigations.add(e)
      showNext
    }
    val t2 = System.currentTimeMillis()
    Log.d("process time: ",""+ (t2-t1))
  }

  def showPrex() {
    val web = findViewById(R.id.wWebview).asInstanceOf[WebView]
    if(web !=null && web.canGoBack){
      web.goBack()
    }
    else if(flipper.getChildCount == 1){
      //do nothing
      initTopBar
    }
    else{
      flipper.setInAnimation(this, R.anim.push_right_in)
      flipper.setOutAnimation(this, R.anim.push_right_out)
      flipper.showPrevious()
      flipper.removeViewAt(flipper.getChildCount - 1)
      navigations.remove(navigations.size()-1)
      initTopBar
    }
  }

  def showNext() {
    if(flipper.getChildCount > 1){
      flipper.setInAnimation(this, R.anim.push_left_in)
      flipper.setOutAnimation(this, R.anim.push_left_out)
    }
    flipper.showNext()
    initTopBar
  }

  def welcomeView(e: Element) = {
    val view = getLayoutInflater.inflate(R.layout.welcome,null)
    val gridView = view.findViewById(R.id.gGridView).asInstanceOf[GridView]
    gridView.setAdapter(new GridViewAdapter(e.elements()))
    gridView.setOnItemClickListener(new MenuItemListener(e.elements()))

    val webView = view.findViewById(R.id.welcomeWebview).asInstanceOf[WebView]
    PTMenu.webViewFormat(webView)(HttpUtil.BASE_URL+"mwelcome"){
      view.findViewById(R.id.welcomeLayout).setVisibility(View.VISIBLE)
    }
    view
  }

  def gridView(e: Element) = {
    val view = getLayoutInflater.inflate(R.layout.gridviewtemplate,null)
    val gridView = view.findViewById(R.id.gGridView).asInstanceOf[GridView]
    gridView.setAdapter(new GridViewAdapter(e.elements()))
    gridView.setOnItemClickListener(new MenuItemListener(e.elements()))
    view
  }

  def listView(e: Element) = {
    val view = getLayoutInflater.inflate(R.layout.listviewtemplate,null)
    val lv = view.findViewById(R.id.llistview).asInstanceOf[ListView]
    lv.setAdapter(new ListViewAdapter(e.elements()))
    lv.setOnItemClickListener(new MenuItemListener(e.elements()))
    view
  }

  class GridViewAdapter(list: java.util.List[Element]) extends BaseAdapter{
    def getView(p1: Int, p2: View, p3: ViewGroup): View = {
      val t1 = System.currentTimeMillis()

      if(p2 == null){
        val view = LayoutInflater.from(PTUI.this).inflate(R.layout.gridview_itemtemplate,null)
        val imgV = view.findViewById(R.id.gridview_item_funcIcon).asInstanceOf[ImageView]
        val tv:TextView = view.findViewById(R.id.gridview_item_funcName).asInstanceOf[TextView]
        val e = list.get(p1)
        val img = PTMenu.noteValue(e,"img")

        imgV.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        imgV.setPadding(5, 5, 5, 0)
        imgV.setAdjustViewBounds(true)
        tv.setSingleLine()
        tv.setTextSize(12.0f)
        tv.setGravity(17)
        tv.setShadowLayer(1.0f,2.0f,2.0f,-16777216)

        PTMenu.getIcon(PTUI.this)(img)(imgV)
        tv.setText(PTMenu.noteValue(e,"text"))
        val t3 = System.currentTimeMillis()
        Log.d("aa.com","GridViewAdapter t3 " + (t3 - t1))
        view
      }else{
        p2
      }
    }
    def getItem(p1: Int): AnyRef = Nil

    def getItemId(p1: Int): Long = 0

    def getCount: Int = list.size
  }


  class ListViewAdapter(list: java.util.List[Element]) extends BaseAdapter{
    def getView(p1: Int, p2: View, p3: ViewGroup): View = {

      if(p2 == null){
        val t1 = System.currentTimeMillis()
        val view = LayoutInflater.from(PTUI.this).inflate(R.layout.listitemtemplate,null)
        val t2 = System.currentTimeMillis()
        Log.d("aa.com","GridViewAdapter t2 " + (t2 - t1))
        val tv:TextView = view.findViewById(R.id.lItemTitle).asInstanceOf[TextView]
        val e = list.get(p1)
        tv.setText(PTMenu.noteValue(e,"text"))
        val t3 = System.currentTimeMillis()
        Log.d("aa.com","ListViewAdapter t3 " + (t3 - t1))
        view
      }else{
        p2
      }
    }
    def getItem(p1: Int): AnyRef = Nil

    def getItemId(p1: Int): Long = 0

    def getCount: Int = list.size
  }
  class MenuItemListener(list: java.util.List[Element]) extends android.widget.AdapterView.OnItemClickListener{
    def onItemClick(p1: AdapterView[_], p2: View, p3: Int, p4: Long) {
      //      <item id="1.1" upid="1" type="list" img="account_mgr.png" msg="goListMenu(1.1)"
      //            text="账户管理">
      val e = list.get(p3);
      navigate(e)
    }
  }
}

class PTLogin extends PTActivity{

  var cpImg: ImageView = _
  var receiver: MyReceiver =_

  override def onCreate(savedInstanceState: Bundle) {
    super.onCreate(savedInstanceState)
    setContentView(loginView)
    receiver = new MyReceiver(sessionHandler)
    registerReceiver(receiver, new IntentFilter("cc.ptlogout"))
    PTGet.ptGet(URLS.ptUUID){
      uuid: String =>
        App.shareContext.putToSession("uuid",uuid)
        Log.d("ptGet","uuid" + uuid)
    }
  }


  override def onDestroy() {
    super.onDestroy()
    unregisterReceiver(receiver)
  }

  val sessionHandler = (intent:Intent) => {
    setContentView(loginView)
    if(findViewById(R.id.ImageViewXykVerifyCode) != null){
      getCpCode(cpImg)
    }
    PTGet.ptGet(URLS.ptUUID){
      uuid: String =>
        App.shareContext.putToSession("uuid",uuid)
        Log.d("ptGet","uuid" + uuid)
    }
  }

  def loginView = {
    val view = getLayoutInflater.inflate(R.layout.login,null)
    cpImg = view.findViewById(R.id.ImageViewXykVerifyCode).asInstanceOf[ImageView]
    cpImg.setOnClickListener(new OnClickListener {
      def onClick(p1: View) {
        getCpCode(cpImg)
      }
    })
    view.findViewById(R.id.gBtnLogin).setOnClickListener(new OnClickListener {
      def onClick(p1: View) {
        val uid = findViewById(R.id.editUserId).asInstanceOf[EditText]
        val upd = findViewById(R.id.editPasswd).asInstanceOf[EditText]
        val ucp = findViewById(R.id.editCpCode).asInstanceOf[EditText]
        val userInfo =
          Map("mobileNo" -> uid.getText.toString,
            "passWord" -> upd.getText.toString,
            "captCode" -> ucp.getText.toString,
            "uuid" -> App.shareContext.getFromSession("uuid"))
        PTPost.ptPostWithProcess(PTLogin.this)(URLS.ptPostLogin)(userInfo)(func)
      }
    })
    view
  }


  val func = (response:Element) => {
    if("0".equals(PTMenu.noteValue(response,"status")))
    {
      sendBroadcast(new Intent("cc.login"))

      if(PTMenu.noteValue(response,"token") != null)
        App.shareContext.putToSession("token",PTMenu.noteValue(response,"token"))

      val intent = new Intent().setClass(PTLogin.this, classOf[PTUI])
      intent.putExtra("superId",PTMenu.logTabId)
      intent.putExtra("name","welcome")
      val decorView = getLocalActivityManager().startActivity("welcome", intent).getDecorView()
//      val webView = PTMenu.simpleWebView(this)("")(null)

//      val root = decorView.getRootView.asInstanceOf[LinearLayout]
//      root.addView(webView)

      setContentView(decorView)
    }
    else{
      PTMenu.messageAlertDialog(PTLogin.this)(response.asXML()){}.show()
    }
  }

  override def onResume() {
    if(findViewById(R.id.ImageViewXykVerifyCode) != null){
      getCpCode(cpImg)
    }
    super.onResume()
  }

  override def onKeyDown(keyCode: Int, event: KeyEvent): Boolean = {
    if(keyCode == KeyEvent.KEYCODE_BACK)
    {
      return true
    }
    false
  }

  def getCpCode(cpImg: ImageView){

    val onSuccess =(b: Array[Byte]) =>{
      if(b.length != 0){
        cpImg.setImageBitmap(BitmapFactory.decodeByteArray(b, 0, b.length))
      }
    }
    val onStart = {
      cpImg.setImageResource(R.drawable.dcode)
    }
    PTGet.imageGetAndRetry(3)(URLS.ptCapt+App.shareContext.getFromSession("uuid"))(onStart)(onSuccess)(null)(null)
  }

}