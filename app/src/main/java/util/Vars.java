package util;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.content.SharedPreferences.Editor;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by MOBICASH on 05-May-15.
 */
public class Vars {
   boolean logg = true;
   public  String longitude;
   public  String latitudes;
   public String location;
   public String name;
   public Editor edit;
   public String mobile;
   public  String deciceID;
   public static String server;


   public SharedPreferences prefs;

   public Vars (Context context){
      server ="http://salezone.co/salezonem/SaleZone/";
      prefs = PreferenceManager.getDefaultSharedPreferences(context);
      location = prefs.getString("location", null);
      longitude = prefs.getString("longitude", null);
      latitudes = prefs.getString("latitudes", null);
      deciceID = prefs.getString("deciceID", null);
      name = prefs.getString("name", null);
      edit = prefs.edit();
      mobile = prefs.getString("mobile",null);

   }
   public void log(String string) {
      if (logg) {
         System.out.println(string);
      } else {

      }
   }

   public File saveImage(Bitmap thePic, String fileName, Context context) {
      OutputStream fOut = null;


      File m = new File(Environment.getExternalStorageDirectory(), "/SaleZone/media");
      if (!m.exists()) {
         m.mkdirs();

      }else if (m.exists()) {
         m.delete();
         m.mkdirs();
      }
      String strDirectory = m.toString();
      File f = new File(m, fileName);
      log("output directory:" + strDirectory);
      try {
         fOut = new FileOutputStream(f);

         /** Compress image **/
         thePic.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
         fOut.flush();
         fOut.close();

         /** Update image to gallery **/
         MediaStore.Images.Media.insertImage(context.getContentResolver(),
                 f.getAbsolutePath(), f.getName(), f.getName());
         log("IMAGE SAVED........");

         //SAVE FACE IMAGE LOCATION
         log("ABSOLUTE PATH:" + f.getAbsolutePath());
         log("FILE NAME:" + f.getName());

      } catch (Exception e) {
         e.printStackTrace();
      }
      return f;
   }


}
