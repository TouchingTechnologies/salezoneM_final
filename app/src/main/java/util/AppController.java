package util;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;


public class AppController {
    private AppController(){

    }
	
	private static AppController mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private static Context mCtx;
    
    public Gson gson;

    public boolean wsConnected = false;

    //TO STOP CHAT NOFITICAITON
    public boolean inChat = false;
	
	 private AppController(Context context) {
	        mCtx = context;
	        mRequestQueue = getRequestQueue();
	        
	        gson = new Gson();

	        mImageLoader = new ImageLoader(mRequestQueue,
	                new ImageLoader.ImageCache() {
	            private final LruCache<String, Bitmap>
	                    cache = new LruCache<String, Bitmap>(20);

	            @Override
	            public Bitmap getBitmap(String url) {
	                return cache.get(url);
	            }

	            @Override
	            public void putBitmap(String url, Bitmap bitmap) {
	                cache.put(url, bitmap);
	            }
	        });
	    }

	    public static synchronized AppController getInstance(Context context) {
	        if (mInstance == null) {
	            mInstance = new AppController(context);
	        }
	        return mInstance;
	    }

	    public RequestQueue getRequestQueue() {
	        if (mRequestQueue == null) {

	            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
	        }
	        return mRequestQueue;
	    }

	    public <T> void addToRequestQueue(Request<T> req) {
	        getRequestQueue().add(req);
	    }
	    public <T> void addRequest(Request<T> request, String tag) {
	        request.setTag(tag);
	        getRequestQueue().add(request);
	    }

	    public <T> void addRequest(Request<T> request) {
	        addRequest(request);
	    }
		public void cancelPendingRequests(Object tag) {
			if (mRequestQueue != null) {
				mRequestQueue.cancelAll(tag);
			}
		}


}

