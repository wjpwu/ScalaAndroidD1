package aa.com.util

import android.util.Log

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 13-7-9
 * Time: 上午9:52
 * To change this template use File | Settings | File Templates.
 */
object LogUtil {

  def log(s: String){
    Log.d("aa.com",s);
  }

  def logObj(s: String)(obj: Array[Any]){
    var i = 0
    val objss = for(v <- obj)
      yield "number:" + i + " is " + (v == null)
        Log.d("aa.com",s + objss );
  }
}
