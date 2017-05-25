package com.example.user.eksibiseyler;


import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.example.user.eksibiseyler.HttpHandler;
import com.example.user.eksibiseyler.R;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItem;
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItemAdapter;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentKanallar extends Fragment {

    RecyclerView recyclerView;
    GridLayoutManager gridLayoutManager;

    String kategori = "kultur";
    public static String URL = "http://192.168.1.25:8080/eksiseyler/getKategoriler.php";

    ArrayList<Product> arrayList = new ArrayList<>();

    ArrayList<String> titles = new ArrayList<>();
    ArrayList<String> stats = new ArrayList<>();
    ArrayList<String> channels = new ArrayList<>();
    ArrayList<String> pic_url = new ArrayList<>();
    ArrayList<String> domain = new ArrayList<>();
    CustomAdapter adapter;
    ProgressDialog pDialog;
    public FragmentKanallar() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_kanallar, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        gridLayoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager);

        adapter = new CustomAdapter(getActivity(),getContext(), R.layout.fragment_kanallar, arrayList);
        recyclerView.setAdapter(adapter);

        return view;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        int position = FragmentPagerItem.getPosition(getArguments());

        switch (position){
            case 0:
                kategori = "kultur";
                break;
            case 1:
                kategori = "bilim";
                break;
            case 2:
                kategori = "eglence";
                break;
            case 3:
                kategori = "yasam";
                break;
            case 4:
                kategori = "spor";
                break;
            case 5:
                kategori = "haber";
                break;
            default:
                break;
        }
        getData();
    }

    public void getData() {

//TODO adding request to POST method and URL
        //Burada isteğimizi oluşturuyoruz, method parametresi olarak post seçiyoruz ve url'imizi const'dan alıyoruz.

        final StringRequest myReq = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (pDialog.isShowing()) {
                    pDialog.dismiss();
                }

                pic_url.clear();
                titles.clear();
                channels.clear();
                stats.clear();

                try {
                    response = URLDecoder.decode(response,"UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                String jsonTitle;
                JSONArray jsonArray;
                JSONObject jsonObject;
                try
                {
                    jsonArray = new JSONArray(response);


                    for(int i=0;i<jsonArray.length();i++)
                    {
                        jsonObject = (JSONObject) jsonArray.get(i);
                        jsonTitle = Html.fromHtml(jsonObject.getString("alt")).toString();
                        if(!(titles.contains(jsonTitle)))
                        {
                            if(jsonObject.getString("src") != "false" || jsonObject.getString("style") != "false")
                            {
                                if(jsonObject.getString("src") == "false")
                                {
                                    pic_url.add(jsonObject.getString("style").replace("background-image: url('","").replace("')",""));
                                    titles.add(jsonTitle);
                                    domain.add(jsonObject.getString("domain"));
                                    channels.add("-");
                                    stats.add("");
                                }
                                else
                                {
                                    pic_url.add(jsonObject.getString("src"));
                                    titles.add(jsonTitle);
                                    domain.add(jsonObject.getString("domain"));
                                    channels.add(jsonObject.getString("category").trim());
                                    stats.add(jsonObject.getString("stats").trim());
                                }
                                Log.e("SONUC:", pic_url.get(pic_url.size()-1) +" -- "+ titles.get(titles.size()-1) +" -- "+
                                                domain.get(pic_url.size()-1) + " -- " +
                                                channels.get(channels.size()-1) +" -- "+ stats.get(stats.size()-1) +" -- ");

                                arrayList.add(new Product(
                                        pic_url.get(pic_url.size()-1).replace("ekstat","eksisozluk"),
                                        titles.get(pic_url.size()-1),
                                        stats.get(pic_url.size()-1),
                                        domain.get(pic_url.size()-1)
                                ));
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();

                }
                catch (JSONException e)
                {
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
            };



            // TODO let put params to volley request
            // Burada göndereceğimiz request parametrelerini(birden fazla olabilir) set'liyoruz

            protected Map<String, String> getParams()
                    throws com.android.volley.AuthFailureError {

                Map<String, String> params = new HashMap<String, String>();

                params.put("kategori", kategori );
                Log.e("kategori",kategori);
                Log.e("params", String.valueOf(params));
                return params;

            };
        };


        myReq.setShouldCache(true); //cache kapatıyoruz.

        AppController.getInstance().addToRequestQueue(myReq);
        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage("Lütfen Bekleyiniz...");
        pDialog.setCancelable(false);
        pDialog.show();
        }



}
