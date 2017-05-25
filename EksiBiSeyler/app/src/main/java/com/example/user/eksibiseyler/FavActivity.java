package com.example.user.eksibiseyler;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FavActivity extends AppCompatActivity {



    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;
    CustomAdapter adapter;
    ProgressDialog pDialog;

    ArrayList<Product> arrayList = new ArrayList<>();

    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> stats = new ArrayList<>();
    ArrayList<String> pic_url = new ArrayList<>();
    ArrayList<String> domain = new ArrayList<>();
    ArrayList<String> IDs = new ArrayList<>();
    public final String select_URL = "http://192.168.1.25:8080/EksiSeyler/get_fav.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_favActivity);
        gridLayoutManager = new GridLayoutManager(getApplicationContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        getFavorites();

        adapter = new CustomAdapter(FavActivity.this,getApplicationContext(), R.layout.activity_fav, arrayList);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public void getFavorites() {

        final StringRequest myReq = new StringRequest( select_URL, new Response.Listener<String>() {
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
                JSONObject jsonObject,j_object;
                try {


                    jsonObject = new JSONObject(response);
                    int success = jsonObject.getInt("success");


                    if(success == 1)
                    {
                        jsonArray = jsonObject.getJSONArray("favori");
                        for(int i=0;i<jsonArray.length();i++) {

                            j_object = jsonArray.getJSONObject(i);
                            IDs.add(j_object.getString("ID"));
                            domain.add(j_object.getString("Url"));
                            pic_url.add(j_object.getString("Pic_Url"));
                            titles.add(j_object.getString("Title"));
                            stats.add(j_object.getString("Stats"));

                            arrayList.add(new Product(
                                    pic_url.get(pic_url.size()-1),
                                    titles.get(pic_url.size()-1),
                                    stats.get(pic_url.size()-1),
                                    domain.get(pic_url.size()-1)
                            ));
                        }
                        adapter.notifyDataSetChanged();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Henüz Favori Eklenmemiş!",Toast.LENGTH_LONG).show();
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
            }};

        myReq.setShouldCache(true); //cache kapatıyoruz.

        AppController.getInstance().addToRequestQueue(myReq);
        pDialog = new ProgressDialog(FavActivity.this);
        pDialog.setMessage("Favoriler Getiriliyor...");
        pDialog.setCancelable(false);
        pDialog.show();


    }
}
