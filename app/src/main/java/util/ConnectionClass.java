package util;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.JsonObject;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import objects.FeebObject;

/**
 * Created by mobicash on 9/9/15.
 */
public class ConnectionClass {
    static Vars vars;
    static String finalstring;
    static  Gson gson;

    public static String ConnectionClass(final Context context,String URL_FEED, final String[] parameters
            ,final String[] values, final String tag, final VolleyCallback callback){
        vars = new Vars(context);
        gson = new Gson();



        CustomRequest postRequest = new CustomRequest(Request.Method.POST, URL_FEED, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    VolleyLog.v("Response:", response.toString(4));
                    if(response!=null){
                        vars.log("respo==we have==" + response.toString());
                        finalstring =  response.toString();
                        callback.onSuccess(finalstring);

                        AppController.getInstance(context).cancelPendingRequests(tag);
                    }else{

                        vars.log("null=="+ response.toString());

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                FeebObject transerror = new FeebObject();
                String errormsg;
                vars.log("==================ERROR============");
                if(error instanceof TimeoutError || error instanceof NoConnectionError){
                    vars.log("error TimeoutError");
                    transerror.setError("Connection error,check your connection");

                    transerror.setStatus("failed");

                    errormsg = gson.toJson(transerror);
                    finalstring = errormsg;
                    callback.onSuccess(finalstring);
                    // AppController.getInstance(context).cancelPendingRequests(tag);
                }
                else if(error instanceof NetworkError){
                    vars.log("error NetworkError");
                    transerror.setError("Connection error,check your connection");

                    transerror.setStatus("failed");
                    errormsg = gson.toJson(transerror);
                    finalstring = errormsg;
                    callback.onSuccess(finalstring);
                    //   AppController.getInstance(context).cancelPendingRequests(tag);
                }
                else if(error instanceof ServerError){
                    vars.log("error ServerError");
                    transerror.setError("Could not get response from the server");
                    transerror.setStatus("failed");

                    errormsg = gson.toJson(transerror);
                    finalstring = errormsg;
                    callback.onSuccess(finalstring);
                    //    AppController.getInstance(context).cancelPendingRequests(tag);
                }
                else if(error instanceof ParseError){
                    vars.log("error ServerError");
                    vars.log("error ServerError");
                    transerror.setError("Could not get response from the server");
                    transerror.setStatus("failed");
                    errormsg = gson.toJson(transerror);
                    finalstring = errormsg;
                    callback.onSuccess(finalstring);
                    //    AppController.getInstance(context).cancelPendingRequests(tag);
                }
                else if(error instanceof AuthFailureError){
                    vars.log("error ServerError");
                    vars.log("error ServerError");
                    transerror.setError("Could not get response from the server");
                    transerror.setStatus("failed");
                    errormsg = gson.toJson(transerror);
                    finalstring = errormsg;
                    callback.onSuccess(finalstring);
                    //    AppController.getInstance(context).cancelPendingRequests(tag);
                }else{
                    vars.log("error Unknown error====");
                    vars.log("error ServerError");
                    transerror.setError("Could not get response from the server");
                    transerror.setStatus("failed");
                    errormsg = gson.toJson(transerror);
                    finalstring = errormsg;
                    callback.onSuccess(finalstring);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                for(int y =0;y<parameters.length;y++){
                    params.put(parameters[y], values[y]);
                    vars.log("para "+y+"==="+parameters[y]+"====and =="+"values "+y+"==="+values[y]);
                }

                return params;
            }
        };
        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance(context).addToRequestQueue(postRequest);

        //VolleySingleton.getInstance().addToRequestQueue(request);




      /*  HashMap<String, String> params = new HashMap<>();
       // params.put("token", "AbCdEfGh123456");
        for(int y =0;y<parameters.length;y++){
            params.put(parameters[y], values[y]);
            vars.log("para "+y+"==="+parameters[y]+"====and =="+"values "+y+"==="+values[y]);
        }

        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST,
                URL_FEED, new JSONObject(params), new Response.Listener<JSONObject>() {


                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            VolleyLog.v("Response:", response.toString(4));
                            if(response!=null){
                                vars.log("respo==we have==" + response.toString());
                                finalstring =  response.toString();
                                callback.onSuccess(finalstring);

                                AppController.getInstance(context).cancelPendingRequests(tag);
                            }else{

                                vars.log("null=="+ response.toString());

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());


                TransactionObj transerror = new TransactionObj();
                String errormsg;
                vars.log("==================ERROR============");
                if(error instanceof TimeoutError || error instanceof NoConnectionError){
                    vars.log("error TimeoutError");
                    transerror.setResult("Failed");
                    transerror.setError("No connection could be established");
                    errormsg = gson.toJson(transerror);
                    finalstring = errormsg;
                    callback.onSuccess(finalstring);
                   // AppController.getInstance(context).cancelPendingRequests(tag);
                }
                else if(error instanceof NetworkError){
                    vars.log("error NetworkError");
                    transerror.setResult("Failed");
                    transerror.setError("Network error check internet connection");
                    errormsg = gson.toJson(transerror);
                    finalstring = errormsg;
                    callback.onSuccess(finalstring);
                 //   AppController.getInstance(context).cancelPendingRequests(tag);
                }
                else if(error instanceof ServerError){
                    vars.log("error ServerError");
                    transerror.setResult("Failed");
                    transerror.setError("Server error please contact our help center");
                    errormsg = gson.toJson(transerror);
                    finalstring = errormsg;
                    callback.onSuccess(finalstring);
                //    AppController.getInstance(context).cancelPendingRequests(tag);
                }
                else if(error instanceof ParseError){
                    vars.log("error ServerError");
                    transerror.setResult("Failed");
                    transerror.setError("Network error check internet connection");
                    errormsg = gson.toJson(transerror);
                    finalstring = errormsg;
                    callback.onSuccess(finalstring);
                    //    AppController.getInstance(context).cancelPendingRequests(tag);
                }
                else if(error instanceof AuthFailureError){
                    vars.log("error ServerError");
                    transerror.setResult("Failed");
                    transerror.setError("Network error check internet connection");
                    errormsg = gson.toJson(transerror);
                    finalstring = errormsg;
                    callback.onSuccess(finalstring);
                    //    AppController.getInstance(context).cancelPendingRequests(tag);
                }
            }
        }


        );

        postRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance(context).addToRequestQueue(postRequest);


*//*




        StringRequest postRequest = new StringRequest(Request.Method.POST, URL_FEED,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String respo) {
                        // TODO Auto-generated method stub

                        if(respo!=null){
                            vars.log("respo==we have==" + respo);
                            finalstring = respo;
                            callback.onSuccess(finalstring);

                            AppController.getInstance(context).cancelPendingRequests(tag);
                        }else{

                            vars.log("null=="+respo);

                        }

                    }

                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        TransactionObj transerror = new TransactionObj();
                        String errormsg;
                        vars.log("==================ERROR============");
                        if(error instanceof TimeoutError || error instanceof NoConnectionError){
                            transerror.setResult("Failed");
                            transerror.setError("No connection could be established");
                            errormsg = gson.toJson(transerror);
                            finalstring = errormsg;
                            callback.onSuccess(finalstring);
                            AppController.getInstance(context).cancelPendingRequests(tag);
                        }
                        else if(error instanceof NetworkError){
                            transerror.setResult("Failed");
                            transerror.setError("Network error check internet connection");
                            errormsg = gson.toJson(transerror);
                            finalstring = errormsg;
                            callback.onSuccess(finalstring);
                            AppController.getInstance(context).cancelPendingRequests(tag);
                        }
                        else if(error instanceof ServerError){
                            transerror.setResult("Failed");
                            transerror.setError("Server error please contact our help center");
                            errormsg = gson.toJson(transerror);
                            finalstring = errormsg;
                            callback.onSuccess(finalstring);
                            AppController.getInstance(context).cancelPendingRequests(tag);
                        }
                        vars.log("Error:===== " + error.networkResponse);
                        vars.log("Error:===== " + error.getMessage());

                    }

                })*//**//*{
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                for(int y =0;y<parameters.length;y++){
                    params.put(parameters[y], values[y]);
                    vars.log("para "+y+"==="+parameters[y]+"====and =="+"values "+y+"==="+values[y]);
                }
               // params.put("userid", vars.chk);
              //  params.put("location", vars.location);

                return params;
            }

           *//**//* @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                return super.parseNetworkResponse(response);
            }*//**//*

        };*//*
      *//*  postRequest.setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance(context).addToRequestQueue(postRequest);*//*
*/
        return URL_FEED;
    }
 /*   public static void returnString(final Context context, String URL_FEED, final String tag, final VolleyCallback callback){
        vars = new Vars(context);
        gson = new Gson();
        StringRequest postRequeststring = new StringRequest(Request.Method.POST, URL_FEED,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String respo) {
                        // TODO Auto-generated method stub

                        if(respo!=null){
                          TransactionObj transerror = new TransactionObj();
                            String errormsg;
                            transerror.setResult("Success");
                            transerror.setError("no error");
                            errormsg = gson.toJson(transerror);
                            finalstring = errormsg;
                            callback.onSuccess(finalstring);

                            AppController.getInstance(context).cancelPendingRequests(tag);
                        }else{

                            vars.log("null=="+respo);

                        }

                    }

                },
                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        TransactionObj transerror = new TransactionObj();
                        String errormsg;
                        vars.log("==================ERROR============");
                        if(error instanceof TimeoutError || error instanceof NoConnectionError){
                            transerror.setResult("Failed");
                            transerror.setError("No connection could be established");
                            errormsg = gson.toJson(transerror);
                            finalstring = errormsg;
                            callback.onSuccess(finalstring);
                            AppController.getInstance(context).cancelPendingRequests(tag);
                        }
                        else if(error instanceof NetworkError){
                            transerror.setResult("Failed");
                            transerror.setError("Network error check internet connection");
                            errormsg = gson.toJson(transerror);
                            finalstring = errormsg;
                            callback.onSuccess(finalstring);
                            AppController.getInstance(context).cancelPendingRequests(tag);
                        }
                        else if(error instanceof ServerError){
                            transerror.setResult("Failed");
                            transerror.setError("Server error please contact our help center");
                            errormsg = gson.toJson(transerror);
                            finalstring = errormsg;
                            callback.onSuccess(finalstring);
                            AppController.getInstance(context).cancelPendingRequests(tag);
                        }
                        vars.log("Error:===== " + error.networkResponse);
                        vars.log("Error:===== " + error.getMessage());

                    }

                });

       *//* {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };*//*


        postRequeststring.setRetryPolicy(new DefaultRetryPolicy(30000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance(context).addToRequestQueue(postRequeststring);
    }*/

    public interface VolleyCallback{
        void onSuccess(String result);
    }
}
