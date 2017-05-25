package com.example.user.eksibiseyler;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by filipp on 9/16/2016.
 */
public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    ImageView iv;
    private Context context;
    private ArrayList<Product> my_data;
    int resource;
    String pic_toURL;
    private Activity activity;
    private static String insertURL = "http://192.168.1.25:8080/eksiseyler/insert_fav.php";
    private static String deleteURL = "http://192.168.1.25:8080/eksiseyler/delete_fav.php";
    ProgressDialog pDialog;
    String picURL;
    String title;
    String stats;
    FavActivity fa;

    public CustomAdapter(Activity a, Context context, int resource, ArrayList<Product> my_data) {
        this.activity = a;
        this.context = context;
        this.resource = resource;
        this.my_data = my_data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card,parent,false);
        iv = (ImageView)  itemView.findViewById(R.id.imageView);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        holder.title.setText(my_data.get(position).getTitle());
        holder.stats.setText(my_data.get(position).getStats());
        Picasso.with(context).load(my_data.get(position).getImage()).into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("tiklama","Resme tiklandi");

                //WebViewActivity wwa = new WebViewActivity();

                pic_toURL = my_data.get(position).getDomain().toString();
                Intent intent = new Intent(context, WebViewActivity.class);
                intent.putExtra("URL_Add",pic_toURL);
                activity.startActivity(intent);
            }
        });



        holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                //Veritabanına kayıt!
                pic_toURL = my_data.get(position).getDomain().toString();
                title = my_data.get(position).getTitle();
                stats = my_data.get(position).getStats();
                picURL = my_data.get(position).getImage();



                if(activity.getClass().toString().contains("FavActivity"))
                    deleteFav();
                else if(activity.getClass().getSimpleName().contains("MainActivity"))
                    insertFav();
                return true;
            }
        });
    }

    public void deleteFav()
    {
        final StringRequest myReq = new StringRequest(Request.Method.POST, deleteURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }


                try {
                    response = URLDecoder.decode(response, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                JSONObject jsonObject;
                try {


                    jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("success");

                    if(success == 1)
                    {
                        fa = new FavActivity();
                        fa.getFavorites();
                        Toast.makeText(context,"Favori Silindi!",Toast.LENGTH_SHORT).show();

                    }
                    else if(success == 0)
                    {
                        Toast.makeText(context,"Favori Silinemedi!",Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println(error.getMessage());
            }
        })

        {

            @Override
            protected Response<String> parseNetworkResponse(
                    NetworkResponse response) {

                String strUTF8 = null;
                try {
                    strUTF8 = new String(response.data, "UTF-8");

                } catch (UnsupportedEncodingException e) {

                    e.printStackTrace();
                }
                return Response.success(strUTF8,
                        HttpHeaderParser.parseCacheHeaders(response));
            }

            // TODO let put params to volley request
            // Burada göndereceğimiz request parametrelerini(birden fazla olabilir) set'liyoruz

            protected Map<String, String> getParams()
                    throws com.android.volley.AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();
                params.put("url", pic_toURL);
                Log.e("params", String.valueOf(params));
                return params;
            }
        };

        myReq.setShouldCache(true); //cache kapatıyoruz.

        AppController.getInstance().addToRequestQueue(myReq);
        pDialog = new ProgressDialog(activity);
        pDialog.setMessage("Favori Siliniyor...");
        pDialog.setCancelable(false);
        pDialog.show();
    }

    public void insertFav() {


        final StringRequest myReq = new StringRequest(Request.Method.POST, insertURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }


                try {
                    response = URLDecoder.decode(response, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String jsonTitle;
                JSONArray jsonArray;
                JSONObject jsonObject;
                try {


                    //jsonArray = new JSONArray(response);
                    jsonObject = new JSONObject(response);
                    //jsonObject = jsonArray.getJSONObject(0);
                    int success = jsonObject.getInt("success");

                    if(success==2)
                    {
                        Toast.makeText(context,"Zaten Favori!",Toast.LENGTH_SHORT).show();
                    }

                    else if(success == 1)
                    {
                        Toast.makeText(context,"Favorilere Eklendi!",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(context,"Favorilere Eklenemedi!",Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                System.out.println(error.getMessage());
            }
        })

        {

            @Override
            protected Response<String> parseNetworkResponse(
                    NetworkResponse response) {

                String strUTF8 = null;
                try {
                    strUTF8 = new String(response.data, "UTF-8");

                } catch (UnsupportedEncodingException e) {

                    e.printStackTrace();
                }
                return Response.success(strUTF8,
                        HttpHeaderParser.parseCacheHeaders(response));
            }

            // TODO let put params to volley request
            // Burada göndereceğimiz request parametrelerini(birden fazla olabilir) set'liyoruz

            protected Map<String, String> getParams()
                    throws com.android.volley.AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                params.put("url", pic_toURL);
                params.put("pic_url", picURL);
                params.put("title", title);
                if(stats == "")
                    params.put("stats", "0");
                else
                    params.put("stats", stats);

                Log.e("params", String.valueOf(params));
                return params;
            }
        };

        myReq.setShouldCache(true); //cache kapatıyoruz.

        AppController.getInstance().addToRequestQueue(myReq);
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Favorilere Ekleniyor...");
        pDialog.setCancelable(false);
        pDialog.show();
    }


    @Override
    public int getItemCount() {
        return my_data.size();
    }

    public  class ViewHolder extends  RecyclerView.ViewHolder{

        public TextView stats;
        public TextView title;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_baslik);
            imageView = (ImageView) itemView.findViewById(R.id.imageView);
            stats = (TextView) itemView.findViewById(R.id.tv_stats);
        }
    }
}
