package touch.salezone.com.salezonem;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.orm.query.Condition;
import com.orm.query.Select;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import adapter.TabsPagerAdapter;
import dbstuff.Codesdb;
import util.Alert;
import util.AndroidMultiPartEntity;
import util.IntentIntegrator;
import util.IntentResult;
import util.Vars;


public class MainActivity extends ActionBarActivity {
   private ActionBar actionBar;
   private Menu mOptionsMenu;

   TabsPagerAdapter mSectionsPagerAdapter;
  // private String[] tabs = { "Scan ", "Review Logs", "Upload Ads" };
    private String[] tabs = { "Scan ", "Upload Ads" };
   ViewPager mViewPager;
   Vars vars;
   Alert alert;


   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      vars= new Vars(this);
      vars.log("location===========" + vars.location);
     if (vars.location==null){
       //  Intent login = new Intent(this, First_Activity.class);
         Intent login = new Intent(this, MerchantProfile.class);
         startActivity(login);
         finish();

      }
      setContentView(R.layout.mainlayout);
      alert = new Alert(this);


      // Create the adapter that will return a fragment for each of the three
      // primary sections of the activity.
      mSectionsPagerAdapter = new TabsPagerAdapter(getSupportFragmentManager());

      // Set up the ViewPager with the sections adapter.
      mViewPager = (ViewPager) findViewById(R.id.pager);
      actionBar = getSupportActionBar();
      mViewPager.setAdapter(mSectionsPagerAdapter);

      actionBar.setHomeButtonEnabled(false);
       actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);



      for (String tab_name : tabs) {
         actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(new ActionBar.TabListener() {
            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

               mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

            }
             @Override
             public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {

             }
         }));
      }
      mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

         @Override
         public void onPageSelected(int position) {
            // on changing the page
            // make respected tab selected
            actionBar.setSelectedNavigationItem(position);
         }

         @Override
         public void onPageScrolled(int arg0, float arg1, int arg2) {
         }

         @Override
         public void onPageScrollStateChanged(int arg0) {
         }
      });

   }


   @Override
   public boolean onCreateOptionsMenu(Menu menu) {
       mOptionsMenu = menu;
      // Inflate the menu; this adds items to the action bar if it is present.
      getMenuInflater().inflate(R.menu.menu_main, menu);
      return true;
   }

   @Override
   public boolean onOptionsItemSelected(MenuItem item) {

      int id = item.getItemId();

      if (id == R.id.action_settings) {
         return true;
      }
       if(id==R.id.download){
           setRefreshActionButtonState(true);
           Codesdb.deleteAll(Codesdb.class);
           update();
           return true;
       }

      return super.onOptionsItemSelected(item);
   }
   public void scan_code(View view){
     // IntentIntegrator scanIntegrator = new IntentIntegrator(this);
  //    scanIntegrator.initiateScan();

   }


   public void onActivityResult(int requestCode, int resultCode, Intent intent) {
      //retrieve scan result
       try {

           super.onActivityResult(requestCode, resultCode, intent);
           IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
//      vars.log("scanningResult===============" + scanningResult.toString());
           if (scanningResult != null) {

               //we have a result
               String scanContent = scanningResult.getContents();

               String scanFormat = scanningResult.getFormatName();

               if (scanContent != null && trueCode(scanContent)) {

                       alert.alerterSuccess("Success", "Success code accepted");



               } else {
                   alert.alerterSuccess("Error Code", "Scanned Code does not exists");
                  // Intent back = new Intent(this, MainActivity.class);
                 //  startActivity(back);
               }
       }
       }catch (Exception e){
           e.printStackTrace();
       }
   }
    public void setRefreshActionButtonState(boolean refreshing) {
        if (mOptionsMenu == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return;
        }

        final MenuItem refreshItem = mOptionsMenu.findItem(R.id.download);
        if (refreshItem != null) {
            if (refreshing) {
                refreshItem.setActionView(R.layout.actionbar_indeterminate_progress);
            } else {
                refreshItem.setActionView(null);
            }
        }
    }
    public boolean trueCode (String chkcode){
        List<Codesdb> allcontacts =  Select.from(Codesdb.class)
                .where(new Condition[]{new Condition("code").eq(chkcode)}).list();
        vars.log("number ===="+allcontacts.size());
        if(allcontacts.size()>0){
            return true;

        }else{
            return false;
        }

    }
    public void update () {
     UploadFileToServer savecode = new UploadFileToServer();
        savecode.execute();

    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {


        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            //alertDialog = ProgressDialog.show(getActivity(), "", "Please wait...");
          //  prog.show();
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {

        }

        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        @SuppressWarnings("deprecation")
        private String uploadFile() {
            String urlpath = vars.server + "code.php";
            String responseString = null;
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(urlpath);

            try {

                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(new AndroidMultiPartEntity.ProgressListener() {

                    @Override
                    public void transferred(long num) {
                        // TODO Auto-generated method stub
                        //publishProgress((int) ((num / (float) totalSize) * 100));
                    }
                });
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String currentDateandTime = sdf.format(new Date());
//				vars.log("filename=============================" + file.getName());
                entity.addPart("userid", new StringBody(vars.deciceID));
                entity.addPart("enddate", new StringBody(currentDateandTime));


                //totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity).toString();
                    vars.log("=====down========" + responseString);
                } else {

                 //   gsonMes.setCode("failed");
                 //   gsonMes.setEnddate("failed");
                 //   gsonMes.setId_post("Error occurred" +"check your internet"+ statusCode);
                  //  String error = new Gson().toJson(gsonMes);
                    responseString = null;
                }

            } catch (ClientProtocolException e) {
                e.printStackTrace();
               // gsonMes.setCode("failed");
              //  gsonMes.setEnddate("failed");
              //  gsonMes.setId_post("Error occurred connection");
               // String error = new Gson().toJson(gsonMes);
                responseString = null;
            } catch (IOException e) {
                e.printStackTrace();
            //    gsonMes.setCode("failed");
            //    gsonMes.setEnddate("failed");
            //    gsonMes.setId_post("Error occurred with connection");
            //    String error = new Gson().toJson(gsonMes);
                responseString = null;
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            setRefreshActionButtonState(false);
            JSONArray jsonArray;
            //ResultsOb fine_results = new Gson().fromJson(result,ResultsOb.class);
            if(result!=null){
                vars.log("post========"+result);
                try {
                    jsonArray = new JSONArray(result);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Codesdb codesdb = new Gson().fromJson(jsonArray.getString(i),Codesdb.class);
                        vars.log("code===="+codesdb.getCode());
                        Codesdb savecode = new Codesdb(codesdb.getCode(),codesdb.getEnddate(),codesdb.getId_post());
                        savecode.save();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //alertDialog.dismiss();
            //    prog.dismiss();
                //FeebObject feebObject = gson.fromJson(json.toString(), FeebObject.class);

               // alerterSuccess("Success", "Post uploaded successfully");

            }else{
                //alertDialog.dismiss();
            //    prog.dismiss();
            //    alerter.alerterSuccess("Error",fine_results.getId_post());

            }

        }
    }
    }


