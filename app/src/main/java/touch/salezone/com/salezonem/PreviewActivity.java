package touch.salezone.com.salezonem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.UUID;

import objects.ResultsOb;
import util.Alert;
import util.AndroidMultiPartEntity;
import util.CircleTransform;
import util.Vars;

public class PreviewActivity extends AppCompatActivity {
    TextView name,old_px,timestamp,titletv,new_price,discount_img,descrip;
    String title,description,start_date,end_date,original_px,new_px,filepath;
    Bundle extras;
    ImageView profileimage,feedImage1;
    Vars vars;
    ProgressDialog prog;
    ResultsOb gsonMes ;
    Alert alerter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        extras = getIntent().getExtras();
        alerter = new Alert(this);
        vars = new Vars(this);
        gsonMes = new ResultsOb();
        setContentView(R.layout.activity_preview);

        prog= new ProgressDialog(this);
        prog.setMessage("Please wait......");
        prog.setCancelable(false);
        prog.setIndeterminate(true);
        prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        name = (TextView) findViewById(R.id.name);
        descrip = (TextView) findViewById(R.id.txtStatusMsg);
        timestamp = (TextView) findViewById(R.id.timestamp);
        discount_img = (TextView) findViewById(R.id.discount_img);
        titletv = (TextView) findViewById(R.id.title);
        new_price= (TextView) findViewById(R.id.new_px);
        profileimage = (ImageView) findViewById(R.id.profilePic);
        feedImage1 = (ImageView) findViewById(R.id.feedImage1);
        feedImage1.setVisibility(View.GONE);
        if(extras!=null){
            title = extras.getString("title");
            description = extras.getString("description");
            start_date = extras.getString("start_date");
            end_date = extras.getString("end_date");
            original_px = extras.getString("original_px");
            new_px = extras.getString("new_px");
            filepath = extras.getString("filepath");
            descrip.setText(description);
            timestamp.setText("End date: "+end_date);
            titletv.setText(title);
            new_price.setText(new_px);
            discount_img.setText(getdiscount(Double.valueOf(original_px),Double.valueOf(new_px))+"%");
            if(filepath!=null){
                feedImage1.setVisibility(View.VISIBLE);
                feedImage1.setImageBitmap(BitmapFactory.decodeFile(filepath));
            }else{
                feedImage1.setVisibility(View.GONE);
            }
        }

        name.setText(vars.name);
        old_px = (TextView) findViewById(R.id.old_px);
        SpannableString text = new SpannableString(original_px);
        text.setSpan(new ForegroundColorSpan(Color.RED), 0, text.length(), 0);

        old_px.setText(text);
        old_px.setPaintFlags(old_px.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();


        Picasso.with(this)
                .load(Vars.server+"profilepic/"+vars.deciceID+".png")
                .error(R.mipmap.ic_error_image)
                .placeholder(R.mipmap.ic_error_image)
                .resize(200, 200)
                .transform(new CircleTransform())
                .into(profileimage);
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
              //  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
              //          .setAction("Action", null).show();
            }
        });
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public String getdiscount (Double oldpx, Double newpx){
        Double disco = (((oldpx-newpx)/oldpx)*100);

        DecimalFormat df = new DecimalFormat("#.0");
        String string_disc = df.format(disco);
        return string_disc;

    }

    public void upload(View view) {
        UploadFileToServer upload = new UploadFileToServer();
        upload.execute();
    }
    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {


        @Override
        protected void onPreExecute() {
            // setting progress bar to zero
            //alertDialog = ProgressDialog.show(getActivity(), "", "Please wait...");
            prog.show();
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
            String urlpath = vars.server + "uploadposts.php";
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

//				vars.log("filename=============================" + file.getName());
                entity.addPart("description", new StringBody(description));
                entity.addPart("startdate", new StringBody(start_date));

                entity.addPart("title", new StringBody(title));
                entity.addPart("discout", new StringBody(discount_img.getText().toString().trim()));
                entity.addPart("old_price", new StringBody(original_px));
                entity.addPart("new_price", new StringBody(new_px));

                entity.addPart("enddate", new StringBody(end_date));
                entity.addPart("userid", new StringBody(vars.deciceID));
                entity.addPart("code", new StringBody(uniquecode()));

                if (filepath.equalsIgnoreCase("")) {
                    entity.addPart("data", new StringBody("no"));
                } else {
                    entity.addPart("data", new StringBody("yes"));
                    File file = new File(filepath);
                    entity.addPart("image", new FileBody(file));
                    vars.log("ima==========" + filepath);
                }


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

                    gsonMes.setCode("failed");
                    gsonMes.setEnddate("failed");
                    gsonMes.setId_post("Error occurred" +"check your internet"+ statusCode);
                    String error = new Gson().toJson(gsonMes);
                    responseString = error;
                }

            } catch (ClientProtocolException e) {
                gsonMes.setCode("failed");
                gsonMes.setEnddate("failed");
                gsonMes.setId_post("Error occurred connection");
                String error = new Gson().toJson(gsonMes);
                responseString = error;
            } catch (IOException e) {

                gsonMes.setCode("failed");
                gsonMes.setEnddate("failed");
                gsonMes.setId_post("Error occurred with connection");
                String error = new Gson().toJson(gsonMes);
                responseString = error;
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {
            ResultsOb fine_results = new Gson().fromJson(result,ResultsOb.class);
            if(fine_results.getId_post().equalsIgnoreCase("success")){
                //alertDialog.dismiss();
                prog.dismiss();
                //FeebObject feebObject = gson.fromJson(json.toString(), FeebObject.class);
                go_to_activity(PreviewActivity.this, "Success", "Your ad has been uploaded successfully");


            }else{
                //alertDialog.dismiss();
                prog.dismiss();

                alerter.go_to_activity(PreviewActivity.this,"Error", fine_results.getId_post());

            }

        }
    }
    public String uniquecode() {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        String finalstring = uuid.substring(4);
        return finalstring;
    }
    public void go_to_activity (Context context,String header,String content){

        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(header);
        builder.setMessage(content);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onBackPressed();
            }
        });
        //   builder.setNegativeButton("Cancel", null);
        builder.show();

    }
}
