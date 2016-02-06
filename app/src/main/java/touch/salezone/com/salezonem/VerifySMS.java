package touch.salezone.com.salezonem;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.digits.sdk.android.AuthCallback;
import com.digits.sdk.android.Digits;
import com.digits.sdk.android.DigitsAuthButton;
import com.digits.sdk.android.DigitsException;
import com.digits.sdk.android.DigitsSession;
import com.google.gson.Gson;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;

import io.fabric.sdk.android.Fabric;
import objects.FeebObject;
import util.Alert;
import util.ConnectionClass;
import util.Vars;

public class VerifySMS extends AppCompatActivity {
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "s0HCvFwyb6zRYv2vvdwfuX3XV";
    private static final String TWITTER_SECRET = "LbtNhO1yLPAxJyQZ9KAO5rpZV8QfGKJZiRFXHjoz0DWj3wtAyl";

    DigitsAuthButton digitsButton;
    TextView view_text;
    Vars vars;
    Bundle extras;
    String mynumber,location,latitudes,longitude,name,deciceID;
    FloatingActionButton fab;
    Alert alert;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vars = new Vars(this);
        alert = new Alert(this);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new TwitterCore(authConfig), new Digits());

        setContentView(R.layout.activity_verify_sms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        view_text = (TextView) findViewById(R.id.view_text);
        extras = getIntent().getExtras();
        if (extras!=null){
            mynumber = extras.getString("mobile");
            location = extras.getString("location");
            latitudes = extras.getString("latitudes");
            longitude = extras.getString("longitude");
            name = extras.getString("name");
            deciceID = extras.getString("deciceID");

            view_text.setText("You will be verifying "+mynumber);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent man = new Intent(VerifySMS.this,MainActivity.class);
                startActivity(man);
                finish();
            }
        });


        digitsButton = (DigitsAuthButton) findViewById(R.id.auth_button);

        digitsButton.setCallback(new AuthCallback() {
            @Override
            public void success(DigitsSession session, final String phoneNumber) {
                // TODO: associate the session userID with your user model

                if (phoneNumber != null) {
                    String phone = phoneNumber.substring(phoneNumber.length() - 9);
                    String myphone = mynumber.substring(mynumber.length() - 9);
                    if(phone.equalsIgnoreCase(myphone)){
                        progressDialog = ProgressDialog.show(VerifySMS.this,"Updating account","please wait...",false,false);
                        String[] parameter = {"mobile"};
                        String[] values ={mynumber};

                        ConnectionClass.ConnectionClass(VerifySMS.this, Vars.server+"checknumber.php", parameter, values, "Verify", new ConnectionClass.VolleyCallback() {
                            @Override
                            public void onSuccess(String result) {
                                progressDialog.dismiss();
                                vars.log("here===" + result);
                                FeebObject feebObject = new Gson().fromJson(result,FeebObject.class);
                                if(feebObject.getMobile().equalsIgnoreCase(mynumber)){
                                    vars.edit.putString("location", feebObject.getLocation());
                                    vars.edit.putString("latitudes", feebObject.getLatitude());
                                    vars.edit.putString("longitude", feebObject.getLongititude());
                                    vars.edit.putString("name", feebObject.getName());
                                    vars.edit.putString("mobile", feebObject.getMobile());
                                    vars.edit.putString("deciceID", feebObject.getId());
                                    vars.edit.commit();

                                    Intent man = new Intent(VerifySMS.this,MainActivity.class);
                                    startActivity(man);
                                    finish();

                                }else{
                                    fab.setVisibility(View.VISIBLE);
                                    alert.go_to_activity(VerifySMS.this, "Error", feebObject.getMobile());
                                }
                            }
                        });

                    }else {
                        fab.setVisibility(View.VISIBLE);
                        alert.go_to_activity(VerifySMS.this, "Error", "The number verified do not match with " + mynumber);
                    }


                }
            }

            @Override
            public void failure(DigitsException exception) {
                Log.d("Digits", "Sign in with Digits failure", exception);
            }
        });


    }

}
