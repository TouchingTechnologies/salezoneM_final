package adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

import objects.FeedItem;
import touch.salezone.com.salezonem.R;
import util.AppController;
import util.CircleTransform;
import util.Vars;





/**
 * Created by baia on 14-9-13.
 */
public class FeedListAdapter extends BaseAdapter {
	AlertDialog alertDialog ;
    private Activity activity;
    private LayoutInflater inflater;
    private List<FeedItem> feedItems;
    Vars vars;

    public FeedListAdapter(Activity activity, List<FeedItem> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
        vars = new Vars(activity);
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
    }


    public void setData(List<FeedItem> data) {
        this.feedItems = data;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.feed_item, null);

        TextView name = (TextView) convertView.findViewById(R.id.name);

        TextView discout = (TextView) convertView.findViewById(R.id.discount_img);
        TextView new_price = (TextView) convertView.findViewById(R.id.new_px);
        TextView title = (TextView) convertView.findViewById(R.id.title);
        TextView old_price = (TextView) convertView.findViewById(R.id.old_px);

        TextView timestamp = (TextView) convertView
                .findViewById(R.id.timestamp);
        final TextView statusMsg = (TextView) convertView
                .findViewById(R.id.txtStatusMsg);

        ImageView discount = (ImageView) convertView
                .findViewById(R.id.get_discount);
        ImageView share = (ImageView) convertView
                .findViewById(R.id.share);
        final ImageView star = (ImageView) convertView
                .findViewById(R.id.star);
        ImageView location = (ImageView) convertView.findViewById(R.id.findlocation);
       // TextView url = (TextView) convertView.findViewById(R.id.txtUrl);
        ImageView profilePic = (ImageView) convertView
                .findViewById(R.id.profilePic);
        ImageView feedImageView = (ImageView) convertView
                .findViewById(R.id.feedImage1);

        final FeedItem item = feedItems.get(position);

        title.setText(item.getTitle());
        name.setText(item.getName());
        discout.setText(item.getDiscout());
        new_price.setText("sh. "+item.getNew_price());
        old_price.setText("sh. "+item.getOld_price());

        SpannableString text = new SpannableString(old_price.getText());
        text.setSpan(new ForegroundColorSpan(Color.RED), 0, text.length(), 0);

        old_price.setText(text);
        old_price.setPaintFlags(old_price.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        // Converting timestamp into x ago format
  //      final CharSequence timeAgo = DateUtils.getRelativeTimeSpanString(
    //            Long.parseLong(item.getTimeStamp()),
    //            System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS);
        timestamp.setText("End date: "+item.getTimeStamp());

        // Chcek for empty status message
        if (!TextUtils.isEmpty(item.getStatus())) {
            statusMsg.setText(item.getStatus());
            statusMsg.setVisibility(View.VISIBLE);

        } else {
            // status is empty, remove from view
            statusMsg.setVisibility(View.GONE);
        }

        // Checking for null feed url
      /*  if (item.getUrl() != null) {
            url.setText(Html.fromHtml("<a href=\"" + item.getUrl() + "\">"
                    + item.getUrl() + "</a> "));

            // Making url clickable
            url.setMovementMethod(LinkMovementMethod.getInstance());
            url.setVisibility(View.VISIBLE);
        } else {
            // url is null, remove from the view
            url.setVisibility(View.GONE);
        }*/
        //url.setVisibility(View.GONE);
        // user profile pic

        Picasso.with(activity)
                .load(item.getProfilePic())
                .error(R.drawable.app)
                .transform(new CircleTransform())
                .into(profilePic);
    //    profilePic.setImageUrl(item.getProfilePic(), imageLoader);

        // Feed image
        if (item.getImageUrl().equalsIgnoreCase("")) {

            feedImageView.setVisibility(View.GONE);

        } else {
            feedImageView.setVisibility(View.VISIBLE);
            vars.log("URL========================"+item.getImageUrl());
               Picasso.with(activity)
                    .load(item.getImageUrl())
                    .error(R.color.white)
                    .into(feedImageView);
        }
        star.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                star.setImageDrawable(activity.getResources().getDrawable(R.drawable.star_fav));
            }
        });
        share.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, item.getName());
                shareIntent.putExtra(Intent.EXTRA_TEXT, item.getStatus());

                Intent chooserIntent = Intent.createChooser(shareIntent, "Share with");
                chooserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(chooserIntent);
            }
        });
        location.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        discount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LayoutInflater li = LayoutInflater.from(activity);
				View promptsView;
				/*vars.log("========layinf=====");

				 promptsView = li.inflate(R.layout.getdiscount_inflator, null);
				 TextView header = (TextView) promptsView.findViewById(R.id.header);
				 final TextView desc = (TextView) promptsView.findViewById(R.id.decription);
				 header.setText(item.getName());
				// desc.setText(item.getStatus());
				 Button cancle = (Button) promptsView.findViewById(R.id.cancle);
				 Button confirm = (Button) promptsView.findViewById(R.id.confirm);
				 confirm.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						alertDialog.dismiss();
						 if (TextUtils.isEmpty(item.getStatus())) {
					            statusMsg.setText(item.getStatus());
						 }
                        List<DiscountDB> all_items = DiscountDB.listAll(DiscountDB.class);
                        if (all_items.isEmpty()){

                            Toast.makeText(activity, "Your discount has been reserved", Toast.LENGTH_LONG).show();
                            DiscountDB save = new DiscountDB(item.getName(), desc.getText().toString(), item.getCode(), item.getTimeStamp(),item.getLatitude(),item.getLongitude());
                            save.save();

                        }else {
                            vars.log("in db========and==getitem=="+item.getCode());
                            List<DiscountDB> all_contacts = DiscountDB.listAll(DiscountDB.class);

                         //   List<DiscountDB> all_contacts = Select.from(DiscountDB.class).where(new Condition[]{new Condition("unqicode").eq(item.getCode())}).list();
                            for(DiscountDB disc:all_contacts){
                                vars.log("in db==========="+disc.getUnqicode()+"====and==getitem=="+item.getCode());
                                if(disc.getUnqicode().equalsIgnoreCase(item.getCode())){
                                    Toast.makeText(activity, "You already saved this discount ", Toast.LENGTH_LONG).show();
                                }else{
                                    Toast.makeText(activity, "Your discount has been reserved", Toast.LENGTH_LONG).show();
                                    DiscountDB save = new DiscountDB(item.getName(), desc.getText().toString(), item.getCode(), item.getTimeStamp(),item.getLatitude(),item.getLongitude());
                                    save.save();
                                    break;
                                }
                            }

                        }
					}
				});
				 cancle.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							alertDialog.dismiss();
						}
					});
				 AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
					alertDialogBuilder.setView(promptsView);
					alertDialog = alertDialogBuilder.create();
					alertDialog.show();*/

			}
		});
        return convertView;
    }


}
