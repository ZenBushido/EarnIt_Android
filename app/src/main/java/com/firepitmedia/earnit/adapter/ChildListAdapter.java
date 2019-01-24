package com.firepitmedia.earnit.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.firepitmedia.earnit.MyApplication;
import com.firepitmedia.earnit.R;
import com.firepitmedia.earnit.model.Child;
import com.firepitmedia.earnit.model.Parent;
import com.firepitmedia.earnit.utils.AppConstant;
import com.firepitmedia.earnit.utils.ScreenSwitch;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.extras.Base64;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.firepitmedia.earnit.activity.ParentDashboard.parentObject;

/**
 * Created by mobile-di on 13/8/17.
 */

public class ChildListAdapter extends RecyclerView.Adapter<ChildListAdapter.ChildListHolder> {

    Activity activity;
    List<Child> childList;
    Parent parent;
    int childId;
    String switchFrom;

    public ChildListAdapter(Activity activity, List<Child> childList, Parent parent, String switchFrom, int childId) {
        this.childList = childList;
        this.activity = activity;
        this.parent = parent;
        this.switchFrom = switchFrom;
        this.childId = childId;

    }

    @Override
    public ChildListHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.child_list_single_view, parent, false);

        return new ChildListAdapter.ChildListHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ChildListHolder holder, final int position) {
        final Child child = childList.get(position);

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.override(350,350);
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);
        requestOptions.placeholder(R.drawable.icon);
        requestOptions.error(R.drawable.icon);

//        Glide.with(activity).applyDefaultRequestOptions(requestOptions).load(AppConstant.AMAZON_URL+child.getAvatar())
//                .into(holder.childImage);

        updateAvatar(child, holder.childImage);
      /*  try {
            Picasso.with(activity).load("https://s3-us-west-2.amazonaws.com/earnitapp-dev/new/" + child.getAvatar()).error(R.drawable.default_avatar).into(holder.childImage);
        } catch (Exception e) {
            Picasso.with(activity).load(R.drawable.default_avatar).into(holder.childImage);
        }
*/
        holder.childFullName.setText(child.getFirstName().substring(0, 1) + child.getFirstName().substring(1).toLowerCase());
        holder.childFullName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ScreenSwitch(activity).moveToAddChild(parent, childId, switchFrom, AppConstant.UPDATE, child);
            }
        });

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showDeleteChild(child, position);
            }
        });
    }

    private void updateAvatar(Child child, ImageView imageView) {
        String url = AppConstant.BASE_URL + "/" + child.getAvatar();
        Log.d("fsdfhkj", "list updateAvatar. url = " + url);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public okhttp3.Response intercept(Chain chain) throws IOException {
                        String emailPassword = MyApplication.getInstance().getEmail() + ":" + MyApplication.getInstance().getPassword();
                        String basic = "Basic " + Base64.encodeToString(emailPassword.getBytes(), Base64.NO_WRAP);
                        Request newRequest = chain.request().newBuilder()
                                .addHeader("Authorization", basic)
                                .build();
                        return chain.proceed(newRequest);
                    }
                })
                .build();

        Picasso picasso = new Picasso.Builder(activity)
                .downloader(new OkHttp3Downloader(client))
                .build();
        picasso
                .load(url)
                .error(Objects.requireNonNull(ContextCompat.getDrawable(activity, R.drawable.icon)))
                .placeholder(Objects.requireNonNull(ContextCompat.getDrawable(activity, R.drawable.icon)))
                .into(imageView);
    }

    public void showDeleteChild(final Child child, final int position) {

        AlertDialog.Builder builder;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(activity, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(activity);
        }

        builder.setTitle("Delete entry")
                .setMessage("Are you sure you want to delete " + child.getFirstName().substring(0, 1) + child.getFirstName().substring(1).toLowerCase() + " from your account? All data will be erased")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        deleteChildFromAccount(child, position);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();


    }

    private void deleteChildFromAccount(Child child, final int position) {
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        String namePassword = MyApplication.getInstance().getEmail().trim() + ":" + MyApplication.getInstance().getPassword().trim();
        final String basicAuth = "Basic " + Base64.encodeToString(namePassword.getBytes(), Base64.NO_WRAP);
        asyncHttpClient.addHeader("Authorization", basicAuth);
        asyncHttpClient.setBasicAuth(parentObject.getEmail(), parentObject.getPassword());
        asyncHttpClient.delete(activity, AppConstant.BASE_URL + AppConstant.CHILDREN_API + child.getId(), new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d("Success Code", String.valueOf(statusCode));
                Log.d("Success Response", response.toString());

                try {
                    String childResponse = response.getString("message");
                    String finalChildResponse = childResponse.substring(2, childResponse.length() - 2);

                     /*This method is used to refresh the adpter after removing item from list.*/

                    childList.remove(position);
                    notifyItemRemoved(position);
                    notifyItemRangeChanged(position, childList.size());

                    Toast.makeText(activity.getBaseContext(), finalChildResponse + "successfully.", Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String response) {
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Log.d("Failure Code", String.valueOf(statusCode));
                Log.d("Failure Response", throwable.toString());
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });
    }

    @Override
    public int getItemCount() {
        return childList.size();
    }

    public class ChildListHolder extends RecyclerView.ViewHolder {
        public TextView childFullName;
        public CircularImageView childImage;
        public ImageButton deleteButton;

        public ChildListHolder(View view) {
            super(view);
            childFullName = (TextView) view.findViewById(R.id.child_fullname_id);
            deleteButton = (ImageButton) view.findViewById(R.id.btnDelete);
            childImage = (CircularImageView) view.findViewById(R.id.chid_avater_id);
        }
    }
}
