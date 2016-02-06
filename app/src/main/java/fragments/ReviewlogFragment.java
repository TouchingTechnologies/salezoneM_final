package fragments;


import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.view.ViewGroup.LayoutParams;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.FeedListAdapter;
import dbstuff.Codesdb;
import objects.FeedItem;
import objects.FeedResult;
import touch.salezone.com.salezonem.R;
import util.AppController;
import util.ConnectionClass;
import util.GsonRequest;
import util.Vars;

public class ReviewlogFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
		Vars vars;
		private ListView listView;
		private FeedListAdapter listAdapter;
		private List<FeedItem> feedItems;
		private SwipeRefreshLayout swipeRefreshLayout;
		LinearLayout offline;

		String URL_FEED = "http://salezone.co/salezonem/SaleZone/myadsupload.php";

		private boolean mSearchCheck;
		private static final String TEXT_FRAGMENT = "TEXT_FRAGMENT";

		public static ReviewlogFragment newInstance(String text){
			ReviewlogFragment mFragment = new ReviewlogFragment();
			Bundle mBundle = new Bundle();
			mBundle.putString(TEXT_FRAGMENT, text);
			mFragment.setArguments(mBundle);
			return mFragment;
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
								 Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			View rootView = inflater.inflate(R.layout.review_main, container, false);
			listView = (ListView) rootView.findViewById(R.id.feed_list);
			offline = (LinearLayout) rootView.findViewById(R.id.offline);
			offline.setVisibility(View.GONE);
			swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.main_list);
			vars = new Vars(getActivity());
			vars.log("Seen this:...... ");
			// fetchitems();
			swipeRefreshLayout.setOnRefreshListener(this);
			swipeRefreshLayout.setColorSchemeColors(R.color.colorAccent,R.color.colorPrimaryDark,R.color.colorPrimary);
			swipeRefreshLayout.post(new Runnable() {
										@Override
										public void run() {
											swipeRefreshLayout.setRefreshing(true);
											fetchitems();
											//put something here
										}
									}
			);

			//  TextView mTxtTitle = (TextView) rootView.findViewById(R.id.txtTitle);
			//  mTxtTitle.setText(getArguments().getString(TEXT_FRAGMENT));

			rootView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT ));
			return rootView;
		}

		@Override
		public void onRefresh() {
			fetchitems();
		}

		private void fetchitems() {
			String paramter[] ={"id_post"};
			String values[] ={vars.deciceID};

			ConnectionClass.ConnectionClass(getActivity(), URL_FEED, paramter, values, "Review", new ConnectionClass.VolleyCallback() {
				@Override
				public void onSuccess(String result) {
					vars.log("STRING REQUEST:...... ");
					if (result!=null){
						swipeRefreshLayout.setRefreshing(false);
						offline.setVisibility(View.GONE);

					}
					FeedResult response = new Gson().fromJson(result, FeedResult.class);
					feedItems = response.getFeedItems();

					Collections.sort(feedItems, new Comparator<FeedItem>() {
						public int compare(FeedItem o1, FeedItem o2) {
							if (o1.getDateupload() == null || o2.getDateupload() == null)
								return 0;
							return o2.getDateupload().compareTo(o1.getDateupload());

						}
					});

					listAdapter.setData(feedItems);
					listAdapter.notifyDataSetChanged();
				}
			});

/*
			GsonRequest<FeedResult> gsonRequest = new GsonRequest<FeedResult>(URL_FEED, objects.FeedResult.class,
					new Response.Listener<objects.FeedResult>() {

						@Override
						public void onResponse(objects.FeedResult response) {

							vars.log("STRING REQUEST:...... ");
							if (response!=null){
								swipeRefreshLayout.setRefreshing(false);
								offline.setVisibility(View.GONE);

							}
							feedItems = response.getFeedItems();

							Collections.sort(feedItems, new Comparator<FeedItem>() {
								public int compare(FeedItem o1, FeedItem o2) {
									if (o1.getDateupload() == null || o2.getDateupload() == null)
										return 0;
									return o2.getDateupload().compareTo(o1.getDateupload());

								}
							});

							listAdapter.setData(feedItems);
							listAdapter.notifyDataSetChanged();
						}

					},
					new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							vars.log("Error: " + error.getMessage());

							if (error instanceof TimeoutError || error instanceof NoConnectionError) {
								vars.log("error TimeoutError");
								swipeRefreshLayout.setRefreshing(false);
								offline.setVisibility(View.VISIBLE);

							} else if (error instanceof NetworkError) {
								vars.log("error NetworkError");
								offline.setVisibility(View.VISIBLE);
								swipeRefreshLayout.setRefreshing(false);

							} else if (error instanceof ServerError) {
								vars.log("error ServerError");
								//    swipeRefreshLayout.setRefreshing(false);
							} else if (error instanceof ParseError) {
								vars.log("error ServerError");
								//    swipeRefreshLayout.setRefreshing(false);
							} else if (error instanceof AuthFailureError) {
								vars.log("error ServerError");
								//    swipeRefreshLayout.setRefreshing(false);
								//    AppController.getInstance(context).cancelPendingRequests(tag);
							}
						}
					});
			// Adding request to volley request queue
			AppController.getInstance(getActivity()).addRequest(gsonRequest, "MainActivity");*/

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				listView.setNestedScrollingEnabled(true);
			}

			feedItems = new ArrayList<>();
			listAdapter = new FeedListAdapter(getActivity(), feedItems);
			listView.setAdapter(listAdapter);
		}

	}