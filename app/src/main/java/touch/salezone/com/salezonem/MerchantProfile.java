package touch.salezone.com.salezonem;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.isseiaoki.simplecropview.CropImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import objects.FeebObject;
import util.Alert;
import util.AndroidMultiPartEntity;
import util.ConnectionClass;
import util.Utils;
import util.Vars;

public class MerchantProfile extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    protected static final String TAG = "location-updates-sample";

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 10000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;


    String logitude,latitude;

    // Labels.
    protected String mLatitudeLabel;
    protected String mLongitudeLabel;
    protected String mLastUpdateTimeLabel;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;
    EditText business_name,mobile;
    ImageView iv_ProfilePic;
    private static int RESULT_LOAD_IMAGE = 1;
    AlertDialog alertDialog_crop ;
    AlertDialog alertDialog;
    protected static final int PICK_FROM_CAMERA = 2;
    protected static final int PICK_FROM_FILE = 3;
    protected Uri mImageCaptureUri;
    String lblLocation;

    Alert alert;
    Gson gson;
    boolean hasimage;
    Vars vars;
    File file;
    boolean waiting;
    ProgressDialog dialog_prog;
    private UploadFileToServer task;
    private final String TAGS = "MerchantProfile";
    CheckBox account;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        vars = new Vars(this);
        alert = new Alert(this);
        gson = new Gson();

        setContentView(R.layout.activity_merchant_profile);
        account = (CheckBox) findViewById(R.id.account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        hasimage = false;

        business_name = (EditText) findViewById(R.id.business_name);

        business_name.setFilters(new InputFilter[] {
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if(cs.equals("")){ // for backspace
                            return cs;
                        }
                        if(cs.toString().matches("[a-zA-Z ]+")){
                            return cs;
                        }
                        return "";
                    }
                }
        });
        iv_ProfilePic = (ImageView) findViewById(R.id.iv_profilePic);
        mobile = (EditText) findViewById(R.id.mobile);

        if(gps_is()) {

        }else{
            alertbox("","");
        }

        // Locate the UI widgets.

        // Set labels.
        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mLastUpdateTimeLabel = getResources().getString(R.string.last_update_time_label);

        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";

        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        buildGoogleApiClient();


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                vars.log("lotitiiissd===========" + latitude + "====" + logitude);

                if(gps_is()){
                    if(latitude!=null) {
                        vars.log("latitude!=null========" + logitude);
                        if (mRequestingLocationUpdates) {
                            mRequestingLocationUpdates = false;
                            stopLocationUpdates();
                        }
                        GetAddressTask getloc = new GetAddressTask();
                        getloc.execute();
                        if(account.isChecked()){
                            if(business_name.getText().toString().equalsIgnoreCase("")){
                                Snackbar.make(view, "Business name can't be empty", Snackbar.LENGTH_LONG)
                                        .setAction("", null).show();
                            }else if(!validnumber(mobile.getText().toString())){
                                Snackbar.make(view, "Phone number is invalid", Snackbar.LENGTH_LONG)
                                        .setAction("", null).show();
                            } else{
                                progressDialog = ProgressDialog.show(MerchantProfile.this,"Updating account","please wait...",false,false);
                                String[] parameter = {"mobile","name"};
                                String[] values ={mobile.getText().toString().trim(),business_name.getText().toString().trim()};

                                ConnectionClass.ConnectionClass(MerchantProfile.this, Vars.server + "checknumber.php", parameter, values, "Verify", new ConnectionClass.VolleyCallback() {
                                    @Override
                                    public void onSuccess(String result) {
                                        progressDialog.dismiss();
                                        vars.log("here===" + result);
                                        FeebObject feebObject = new Gson().fromJson(result, FeebObject.class);
                                        if (feebObject.getMobile().equalsIgnoreCase(mobile.getText().toString().trim())) {
                                            vars.edit.putString("location", feebObject.getLocation());
                                            vars.edit.putString("latitudes", feebObject.getLatitude());
                                            vars.edit.putString("longitude", feebObject.getLongititude());
                                            vars.edit.putString("name", feebObject.getName());
                                            vars.edit.putString("mobile", feebObject.getMobile());
                                            vars.edit.putString("deciceID", feebObject.getId());
                                            vars.edit.commit();

                                            Intent gomain = new Intent(MerchantProfile.this,MainActivity.class);
                                            startActivity(gomain);
                                            finish();
/*
                                            Intent man = new Intent(MerchantProfile.this, VerifySMS.class);
                                            man.putExtra("location", feebObject.getLocation());
                                            man.putExtra("latitudes", feebObject.getLatitude());
                                            man.putExtra("longitude", feebObject.getLongititude());
                                            man.putExtra("name", feebObject.getName());
                                            man.putExtra("mobile", feebObject.getMobile());
                                            man.putExtra("deciceID", feebObject.getId());
                                            startActivity(man);
                                            finish();*/

                                        } else {
                                            alert.go_to_activity(MerchantProfile.this, "Error", feebObject.getMobile());
                                        }
                                    }
                                });


                            }
                        }else {
                            if (!hasimage) {

                                Snackbar.make(view, "Please select profile image", Snackbar.LENGTH_LONG)
                                        .setAction("", null).show();
                            } else if (business_name.getText().toString().equalsIgnoreCase("")) {
                                Snackbar.make(view, "Business name can't be empty", Snackbar.LENGTH_LONG)
                                        .setAction("", null).show();
                            } else if (!validnumber(mobile.getText().toString())) {
                                Snackbar.make(view, "Phone number is invalid", Snackbar.LENGTH_LONG)
                                        .setAction("", null).show();
                            } else {
                                    waiting = true;
                                    task = new UploadFileToServer();
                                    task.startProcess(MerchantProfile.this);

                            }
                        }
                    }else{
                        vars.log("latitude==null========"+logitude);

                        if (!mRequestingLocationUpdates) {
                            mRequestingLocationUpdates = true;
                            startLocationUpdates();
                        }
                        }

                }else{
                    alertbox("","");
                }

            }
        });

        if(savedInstanceState!=null) {
            if(savedInstanceState.containsKey("task")) {
                Log.d(TAGS, "recovering task");
                task = (UploadFileToServer) savedInstanceState.getSerializable("task");
            }

        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        iv_ProfilePic.setImageBitmap((Bitmap) savedInstanceState.getParcelable("image"));
        hasimage= savedInstanceState.getBoolean("hasimage");
        waiting=savedInstanceState.getBoolean("waiting");
        String path = savedInstanceState.getString("file");

        file  = vars.saveImage(iv_ProfilePic.getDrawingCache(),"DEL",this);

        vars.log("OnRestore=============" + waiting);
       /* if (waiting && alertDialog!=null) {

        }else {
            waiting = true;

            alertDialog = ProgressDialog.show(MerchantProfile.this, "Registration in progress", "Please wait...");

        }
*/
        super.onRestoreInstanceState(savedInstanceState);
    }


    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);

            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
            updateUI();
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Handles the Start Updates button and requests start of location updates. Does nothing if
     * updates have already been requested.
     */
   /* public void startUpdatesButtonHandler(View view) {
        if (!mRequestingLocationUpdates) {
            mRequestingLocationUpdates = true;
            setButtonsEnabledState();
            startLocationUpdates();
        }
    }*/

    /**
     * Handles the Stop Updates button, and requests removal of location updates. Does nothing if
     * updates were not previously requested.
     */
   /* public void stopUpdatesButtonHandler(View view) {
        if (mRequestingLocationUpdates) {
            mRequestingLocationUpdates = false;
            setButtonsEnabledState();
            stopLocationUpdates();
        }
    }
*/
    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    /**
     * Ensures that only one button is enabled at any time. The Start Updates button is enabled
     * if the user is not requesting location updates. The Stop Updates button is enabled if the
     * user is requesting location updates.
     */
  /*  private void setButtonsEnabledState() {
        if (mRequestingLocationUpdates) {
            mStartUpdatesButton.setEnabled(false);
            mStopUpdatesButton.setEnabled(true);
        } else {
            mStartUpdatesButton.setEnabled(true);
            mStopUpdatesButton.setEnabled(false);
        }
    }*/

    /**
     * Updates the latitude, the longitude, and the last location time in the UI.
     */
    private void updateUI() {
        try {
            logitude = String.valueOf(mCurrentLocation.getLongitude());
            latitude =String.valueOf(mCurrentLocation.getLatitude());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(task!=null) {
            task.showDialogIfRunning(this);
        }

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
                startLocationUpdates();
            }

    }

    @Override
    protected void onPause() {
        super.onPause();

        if(task!=null)
            task.stopDialog();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {


            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();

        super.onStop();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            updateUI();
        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
        Toast.makeText(this, getResources().getString(R.string.location_updated_message),
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        savedInstanceState.putParcelable("image", ((BitmapDrawable) iv_ProfilePic.getDrawable()).getBitmap());
        savedInstanceState.putBoolean("hasimage", hasimage);

        vars.log("onsaveInstanceState=======so i save============="+waiting);
        savedInstanceState.putBoolean("waiting",waiting);

        if(task!=null && !task.isCancelled() && task.getStatus() != AsyncTask.Status.FINISHED) {
            Log.d(TAGS, "preserving task");
            savedInstanceState.putSerializable("task", task);
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    public boolean gps_is() {
        ContentResolver contentResolver = getBaseContext()
                .getContentResolver();
        boolean gpsStatus = Settings.Secure
                .isLocationProviderEnabled(contentResolver,
                        LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return true;

        } else {
            return false;
        }
    }
    protected void alertbox(String title, String mymessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please enable it once and we promise your location will never be shared with anyone.")
                .setCancelable(false)
                .setTitle("Your GPS is disabled")
                .setPositiveButton("Turn on",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {

                                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivity(intent);
                                dialog.cancel();
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // cancel the dialog box
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;

        switch (requestCode) {

            case PICK_FROM_CAMERA:
                try{
                    //    mImageCaptureUri = data.getData();
                 //   Bitmap bitmap = MediaStore.Images.Media.getBitmap(this
                //            .getContentResolver(), mImageCaptureUri);
                    cropimage(Utils.decodeUri(this,mImageCaptureUri));
                    // doCrop(mImageCaptureUri);

                }catch(ActivityNotFoundException ex){
                    String errorMessage = "Sorry - your device doesn't support the crop action!";
                    Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                    toast.show();

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                break;

            case PICK_FROM_FILE:
                /**
                 * After selecting image from files, save the selected path
                 */
                mImageCaptureUri = data.getData();

                try{
                  //  Bitmap bitmap = MediaStore.Images.Media.getBitmap(this
                   //         .getContentResolver(), mImageCaptureUri);
                    cropimage(Utils.decodeUri(this,mImageCaptureUri));
                    file = vars.saveImage(Utils.decodeUri(this,mImageCaptureUri),"profile",this);
                    //  File files = saveImage(bitmap, "delete", this);
                    //doCrop(Uri.fromFile(files));
                    //files.delete();
                }catch(ActivityNotFoundException | IOException ex){
                    String errorMessage = "Sorry - your device doesn't support the crop action!";
                    Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }

                break;


                }


    }
    public void cropimage (final Bitmap bitmap){
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView;
        promptsView = li.inflate(R.layout.cropping, null);

        final CropImageView cropImageView = (CropImageView) promptsView.findViewById(R.id.cropImageView);
        cropImageView.setCropMode(CropImageView.CropMode.RATIO_FREE);
        cropImageView.setImageBitmap(bitmap);

        FloatingActionButton fab_done = (FloatingActionButton) promptsView.findViewById(R.id.fab_tick);
        FloatingActionButton fab_delete = (FloatingActionButton) promptsView.findViewById(R.id.fab_delte);
        FloatingActionButton fab_rotation = (FloatingActionButton) promptsView.findViewById(R.id.fab_rotate);
        fab_rotation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
            }
        });
        fab_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iv_ProfilePic.setImageBitmap(cropImageView.getCroppedBitmap());
                hasimage = true;
               /* ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                //    files_profile = saveImage(photo, "users", context);
                byte[] imageBytes = baos.toByteArray();
                encodedFaceString = Base64.encodeToString(imageBytes,
                        Base64.DEFAULT);*/

                alertDialog_crop.dismiss();
            }
        });
        fab_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog_crop.dismiss();
            }
        });

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);
        alertDialog_crop = alertDialogBuilder.create();
        alertDialog_crop.show();

    }

    public void puticon(View v){
        Intent i = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, PICK_FROM_FILE);
    }

    private class UploadFileToServer extends AsyncTask<Void, Integer, String> implements Serializable {
        private ProgressDialog dialog;
        private final String TAGS = "Task";

        public void startProcess(Context context){
            execute();
            showDialogIfRunning(context);
        }

        public void showDialogIfRunning(Context context){
            if(!isCancelled() && getStatus()==Status.RUNNING) {
                startDialog(context);
            }else {
                stopDialog();
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            Log.d(TAGS, "Canceled");
            stopDialog();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAGS, "Processing");
        }


        @Override
        protected void onProgressUpdate(Integer... progress) {

        }


        @Override
        protected String doInBackground(Void... params) {
            return uploadFile();
        }

        private String uploadFile() {


            String urlpath =Vars.server+"register.php";
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
                entity.addPart("name", new StringBody(business_name.getText().toString()));
                if(lblLocation==null){
                    entity.addPart("location", new StringBody("location lost"));

                }else{
                    entity.addPart("location", new StringBody(lblLocation));
                }

                entity.addPart("latitude", new StringBody(latitude));
                entity.addPart("longititude", new StringBody(logitude));
                entity.addPart("mobile", new StringBody(mobile.getText().toString().trim()));
                entity.addPart("data", new FileBody(file));

                //totalSize = entity.getContentLength();
                httppost.setEntity(entity);

                // Making server call
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity r_entity = response.getEntity();

                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == 200) {
                    // Server response
                    responseString = EntityUtils.toString(r_entity);
                    vars.log("=====down========" + responseString);
                } else {


                    responseString = null;
                }

            } catch (IOException e) {


                responseString = null;
            }

            return responseString;

        }

        @Override
        protected void onPostExecute(String result) {

            if(result!=null){
                //alertDialog.dismiss();


                FeebObject feebObject = gson.fromJson(result ,FeebObject.class);

                if(feebObject.getStatus().equalsIgnoreCase("failed")) {
                    stopDialog();
                    twooption(MerchantProfile.this, "Error", feebObject.getError(), "New", "Use this account");
                 //   alert.go_to_activity(MerchantProfile.this, "Error", feebObject.getMobile());

                }else{
                    vars.edit.putString("location", feebObject.getLocation());
                    vars.edit.putString("latitudes", feebObject.getLatitude());
                    vars.edit.putString("longitude", feebObject.getLongititude());
                    vars.edit.putString("name", feebObject.getName());
                    vars.edit.putString("mobile", feebObject.getMobile());
                    vars.edit.putString("deciceID", feebObject.getId());
                    vars.edit.commit();

                    stopDialog();
                    alert.go_to_home(MerchantProfile.this, "Success", "Your business has been registered");



                }

            }else {

                stopDialog();
                alert.go_to_activity(MerchantProfile.this, "Error", "Check internet connection and try again");

            }



        }

        public void stopDialog() {
            if(dialog!=null && dialog.isShowing())
                dialog.dismiss();
            dialog = null;
        }

        private void startDialog(Context context) {
            dialog = new ProgressDialog(context);
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    cancel(false);
                }
            });
            dialog.setCancelable(false);
            dialog.setMessage("Please wait...");
            dialog.show();
        }
    }

    public String mylocation_is(){
        String mylocation= null;

        String locale = null;

        Geocoder geoCoder = new Geocoder(getBaseContext(), Locale.getDefault());
            try {
                List<Address> addresses = geoCoder.getFromLocation(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude(), 1);

                String add = "";
                if (addresses.size() > 0)
                {
                    locale = addresses.get(0).getCountryName();
                    add += addresses.get(0).getAddressLine(1);

                }
                return add+" "+locale;
                //   addressLabel.setText(add);
            }
            catch (IOException e1) {
                e1.printStackTrace();
                return  null;
            }




    }


    private class GetAddressTask extends AsyncTask<Location, String, String> {

        /*
         * When the task finishes, onPostExecute() displays the address.
         */
        @Override
        protected void onPostExecute(String address) {
            vars.log("my location is =========="+address);

            lblLocation=address;
        }
        @Override
        protected String doInBackground(Location... params) {
           lblLocation = mylocation_is();
            return  lblLocation;
        }
    }// AsyncTask class

    public boolean validnumber (String number){
        TelephonyManager manager = (TelephonyManager) this.getSystemService(this.TELEPHONY_SERVICE);
        String CountryID = manager.getSimCountryIso().toUpperCase();

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber phone_number = phoneUtil.parse(number, CountryID);

            boolean isValid = phoneUtil.isValidNumber(phone_number);
            return isValid;
        }catch (NumberParseException e){
            return false;
        }

    }


    public void twooption (final Context context,String header, final String content,String ok,String cancel){


        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(header);
        builder.setMessage(content);
        builder.setNegativeButton(cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(MerchantProfile.this,VerifySMS.class);
                i.putExtra("mynumber",mobile.getText().toString().trim());
                startActivity(i);
            }
        });
        builder.setPositiveButton(ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //   builder.setNegativeButton("Cancel", null);
        builder.show();

    }

}