package fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.isseiaoki.simplecropview.CropImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import objects.FeebObject;
import objects.ResultsOb;
import touch.salezone.com.salezonem.MainActivity;
import touch.salezone.com.salezonem.PreviewActivity;
import touch.salezone.com.salezonem.R;
import util.Alert;
import util.AndroidMultiPartEntity;
import util.SoftKeyboard;
import util.Vars;

public class UploadFragment extends Fragment implements View.OnClickListener{
	EditText editText_startdate,editText_enddate,editText_description,editText_title,editText_oldpx,editText_newpx;
	CheckBox checkBox_image;
	Button upload,cancel;
	//AlertDialog alertDialog;
	ProgressDialog prog;
	File file;
	ImageView uploadimage;
	RelativeLayout grouplayout;
	Gson gson;
	Alert alerter;
	String URL_FEED="http://salezone.co/salezonem/SaleZone/uploadposts.php";
	Vars vars;
	Bitmap bitmap,finalbitmap;
	String imagestring = null;
	ResultsOb gsonMes ;
	private static int RESULT_LOAD_IMAGE = 1;
	static int CROP_FROM_MEDIA = 2;
	AlertDialog alertDialog_crop;
	SoftKeyboard softKeyboard;
	LinearLayout button_layout;
	InputMethodManager im;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_upload, container, false);
		vars = new Vars(getActivity());
		gsonMes = new ResultsOb();
		alerter = new Alert(getActivity());
		prog= new ProgressDialog(getActivity());
		prog.setMessage("Please wait......");
		prog.setCancelable(false);
		prog.setIndeterminate(true);
		prog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

		checkBox_image = (CheckBox) rootView.findViewById(R.id.checkBox_image);
		uploadimage = (ImageView) rootView.findViewById(R.id.imageView_upload);
		uploadimage.setVisibility(View.GONE);
		uploadimage.setOnClickListener(this);

		editText_title = (EditText) rootView.findViewById(R.id.editText_title);
		editText_oldpx = (EditText) rootView.findViewById(R.id.editText_oldpx);
		editText_newpx = (EditText) rootView.findViewById(R.id.editText_newpx);

		editText_startdate = (EditText) rootView.findViewById(R.id.editText_startdate);
		editText_enddate = (EditText) rootView.findViewById(R.id.editText_enddate);
		editText_description = (EditText) rootView.findViewById(R.id.editText_description);
		upload = (Button) rootView.findViewById(R.id.button_upload);
		gson = new Gson();
		upload.setOnClickListener(this);
		cancel  = (Button) rootView.findViewById(R.id.button_cancel);
		cancel.setOnClickListener(this);
		editText_enddate.setFocusable(false);
		editText_startdate.setFocusable(false);
		editText_startdate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//Toast.makeText(getActivity(),"fire",Toast.LENGTH_SHORT).show();
				DialogFragment newFragment = new DatePickerFragment() {
					@Override
					public void onDateSet(DatePicker view, int year, int month, int day) {
						int monthadd = month + 1;
						editText_startdate.setText("" + year + "-" + monthadd + "-" + day);
						super.onDateSet(view, year, month, day);
					}
				};
				newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");

			}
		});
		editText_enddate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//Toast.makeText(getActivity(),"fire",Toast.LENGTH_SHORT).show();
				DialogFragment newFragment = new DatePickerFragment() {

					@Override
					public void onDateSet(DatePicker view, int year, int month, int day) {
						int monthadd = month + 1;
						editText_enddate.setText(year + "-" + monthadd + "-" + day);
						super.onDateSet(view, year, month, day);

					}
				};
				newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");

			}
		});
		checkBox_image.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (isChecked) {
					uploadimage.setVisibility(View.VISIBLE);
				} else {
					uploadimage.setVisibility(View.GONE);
				}

			}
		});
		button_layout= (LinearLayout) rootView.findViewById(R.id.button_layout);
		grouplayout = (RelativeLayout) rootView.findViewById(R.id.grouplayout);
		im = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
		softKeyboard = new SoftKeyboard(grouplayout, im);
		softKeyboard.setSoftKeyboardCallback(new SoftKeyboard.SoftKeyboardChanged()
		{

			@Override
			public void onSoftKeyboardHide()
			{
				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						button_layout.setVisibility(View.VISIBLE);
					}
				});

			}

			@Override
			public void onSoftKeyboardShow()
			{

				new Handler(Looper.getMainLooper()).post(new Runnable() {
					@Override
					public void run() {
						button_layout.setVisibility(View.GONE);
					}
				});



			}
		});

		return rootView;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.imageView_upload:
				//Toast.makeText(getActivity(),"bebbeeb",Toast.LENGTH_SHORT).show();
				Intent i = new Intent(Intent.ACTION_PICK,
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(i, RESULT_LOAD_IMAGE);
				break;
			case R.id.button_cancel:
				editText_startdate.setText("");
				editText_description.setText("");
				editText_enddate.setText("");
				uploadimage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_upload_image));
				imagestring=null;

				break;
			case R.id.button_upload:
				if(editText_title.getText().toString().equalsIgnoreCase("")){
					editText_title.setError("Tittle required");
				}
				else if (editText_description.getText().toString().equalsIgnoreCase("")) {
					editText_description.setError("Please enter some description");
				} else if (editText_startdate.getText().toString().equalsIgnoreCase("")) {
					editText_startdate.setError("Please select start date");
				} else if (editText_enddate.getText().toString().equalsIgnoreCase("")) {
					editText_enddate.setError("Please select end date");
				}else if (editText_oldpx.getText().toString().equalsIgnoreCase("")){
					editText_oldpx.setError("Original price required");
				}
				else if (editText_newpx.getText().toString().equalsIgnoreCase("")){
					editText_newpx.setError("New price required");
				}
				else if (checkBox_image.isChecked() && imagestring == null) {
					alerter.alerterSuccess("Error *Image", "Please add an image");
				} else {
				Intent previw = new Intent(getActivity(), PreviewActivity.class);
					previw.putExtra("title",editText_title.getText().toString().trim());
					previw.putExtra("description",editText_description.getText().toString());
					previw.putExtra("start_date",editText_startdate.getText().toString().trim());
					previw.putExtra("end_date",editText_enddate.getText().toString().trim());
					previw.putExtra("original_px",editText_oldpx.getText().toString().trim());
					previw.putExtra("new_px",editText_newpx.getText().toString().trim());

					if (checkBox_image.isChecked()) {
						previw.putExtra("filepath",file.getAbsolutePath());
					}

					getActivity().startActivity(previw);
					//UploadFileToServer task = new UploadFileToServer();
					//task.execute();

				}

			break;
			default:
				break;
		}

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Bundle extras = data.getExtras();
		try{
			if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK) {
				bitmap = getBitmapFromCameraData(data, this.getActivity());
				cropimage(bitmap);
			//	file = saveImage(bitmap, "delete", this.getActivity());
			//	doCrop(Uri.fromFile(file));

			}
			if (requestCode == CROP_FROM_MEDIA) {

				if (extras != null) {
					bitmap = extras.getParcelable("data");
					uploadimage.setImageBitmap(bitmap);
					file = saveImage(bitmap, "delete", this.getActivity());

				}

			}
		}catch (Exception e){
			e.printStackTrace();
		}
	}
	public static Bitmap getBitmapFromCameraData(Intent data, Context context){
		Uri selectedImage = data.getData();
		String[] filePathColumn = { MediaStore.Images.Media.DATA };
		Cursor cursor = context.getContentResolver().query(selectedImage,filePathColumn, null, null, null);
		cursor.moveToFirst();
		int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
		String picturePath = cursor.getString(columnIndex);
		cursor.close();
		return BitmapFactory.decodeFile(picturePath);
	}
	public String uniquecode() {
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		String finalstring = uuid.substring(4);
		return finalstring;
	}
	private class TestTask extends AsyncTask<Bitmap, Void, String> {
		@Override
		protected String doInBackground(Bitmap... params) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			finalbitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] imageBytes = baos.toByteArray();
			imagestring = Base64.encodeToString(imageBytes,
					Base64.DEFAULT);

			return imagestring;
		}

		@Override
		protected void onPostExecute(String result) {
			if(result!=null){
				vars.log("String not null");
				imagestring =	result;
			}

		}

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
				entity.addPart("description", new StringBody(editText_description.getText().toString()));
				entity.addPart("startdate", new StringBody(editText_startdate.getText().toString()));
				entity.addPart("enddate", new StringBody(editText_enddate.getText().toString()));
				entity.addPart("userid", new StringBody(vars.deciceID));
				entity.addPart("code", new StringBody(uniquecode()));

				if (imagestring == null) {
					entity.addPart("data", new StringBody("no"));
				} else {
					entity.addPart("data", new StringBody("yes"));
					entity.addPart("image", new FileBody(file));
					vars.log("ima==========" + imagestring);
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

				alerterSuccess("Success", "Post uploaded successfully");

			}else{
				//alertDialog.dismiss();
				prog.dismiss();
				alerter.alerterSuccess("Error",fine_results.getId_post());

			}

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
		vars.log("output directory:" + strDirectory);
		try {
			fOut = new FileOutputStream(f);

			/** Compress image **/
			thePic.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
			fOut.flush();
			fOut.close();

			/** Update image to gallery **/
			MediaStore.Images.Media.insertImage(context.getContentResolver(),
					f.getAbsolutePath(), f.getName(), f.getName());
			vars.log("IMAGE SAVED........");

			//SAVE FACE IMAGE LOCATION
			vars.log("ABSOLUTE PATH:" + f.getAbsolutePath());
			vars.log("FILE NAME:" + f.getName());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}

	public void alerterSuccess(String header, String message){

		final String header2 = header;

		LayoutInflater li = LayoutInflater.from(getActivity());
		View promptsView;
		promptsView = li.inflate(R.layout.dialog_success_simple, null);

		TextView headerTxt = (TextView) promptsView.findViewById(R.id.success_simple_header);
		TextView messageTxt = (TextView) promptsView.findViewById(R.id.success_simple_message);

		headerTxt.setText(header);
		messageTxt.setText(message);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);
		// set dialog message
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("OK",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int id) {
								//dialog.cancel();
								editText_startdate.setText("");
								editText_description.setText("");
								editText_enddate.setText("");
								uploadimage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_upload_image));
								if(imagestring!=null){
									file.delete();
									imagestring = null;
								}else {
									imagestring = null;
								}

							}
						});


		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	public void cropimage (final Bitmap bitmap){
		LayoutInflater li = LayoutInflater.from(getActivity());
		View promptsView;
		promptsView = li.inflate(R.layout.cropping, null);

		final CropImageView cropImageView = (CropImageView) promptsView.findViewById(R.id.cropImageView);
		cropImageView.setCropMode(CropImageView.CropMode.RATIO_FREE);
		cropImageView.setImageBitmap(bitmap);

		FloatingActionButton fab_rotate = (FloatingActionButton) promptsView.findViewById(R.id.fab_rotate);
		FloatingActionButton fab_done = (FloatingActionButton) promptsView.findViewById(R.id.fab_tick);
		FloatingActionButton fab_delete = (FloatingActionButton) promptsView.findViewById(R.id.fab_delte);
		fab_done.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {


				uploadimage.setImageBitmap(cropImageView.getCroppedBitmap());
				finalbitmap = cropImageView.getCroppedBitmap();
				TestTask task = new TestTask();
				task.execute(finalbitmap);

				file = saveImage(cropImageView.getCroppedBitmap(), "profilepic", getActivity());


				alertDialog_crop.dismiss();
			}
		});
		fab_delete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog_crop.dismiss();
			}
		});
		fab_rotate.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				cropImageView.rotateImage(CropImageView.RotateDegrees.ROTATE_90D);
			}
		});

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
		alertDialogBuilder.setView(promptsView);
		alertDialog_crop = alertDialogBuilder.create();
		alertDialog_crop.show();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		softKeyboard.unRegisterSoftKeyboardCallback();
	}
}
