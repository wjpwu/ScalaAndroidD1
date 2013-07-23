package com.honey

import _root_.android.app.Activity
import _root_.android.os.Bundle

class Index extends Activity with TypedActivity {
  override def onCreate(bundle: Bundle) {
    super.onCreate(bundle)
    setContentView(R.layout.main)

//    val dd = DocumentHelper.parseText("<a>hello</a>")
//    findView(TR.contents_supplement_text_view).setText("hello, world!" + dd.asXML())
  }
}


object Hello{
  def main(args: Array[String]) {
    println("Hello world")
  }
}
