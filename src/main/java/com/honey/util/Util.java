package com.honey.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: Aaron
 * Date: 13-6-26
 * Time: 下午3:06
 * To change this template use File | Settings | File Templates.
 */
public class Util {

    private static String Tag = "Util";
    public File getTempFile(Context context, String url) {
        File file = null;
        try {
            String fileName = Uri.parse(url).getLastPathSegment();
            file = File.createTempFile(fileName, null, context.getCacheDir());
        }
            catch (IOException e) {
                // Error while creating file
            }
        return file;
    }


    public static void saveToInternal(Context context,String fileName,byte[] content){
        try {
            FileOutputStream outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(content);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(Tag,"save to internal failed.");
        }
    }

    public static FileInputStream getFromInternal(Context context,String fileName){

        FileInputStream input = null;
        try{
            input = context.openFileInput(fileName);
        } catch (FileNotFoundException e){
             // do nothing
        }
        return input;
    }

    public static int getDrawable(Context context,String fileName){
        return context.getResources().getIdentifier(fileName,"drawable",context.getPackageName());
    }


    public static void shareMsg(Context context, String activityTitle, String msgTitle, String msgText,
                                String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/png");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, activityTitle));
    }

    //TODO
    public static void shareMMS(Context context, String activityTitle, String msgTitle, String msgText,
                                String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setClassName("com.android.mms", "com.android.mms.ui.ComposeMessageActivity");
        if (imgPath == null || imgPath.equals("")) {
            intent.setType("text/plain"); // 纯文本
        } else {
            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/png");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, activityTitle));
    }

}
