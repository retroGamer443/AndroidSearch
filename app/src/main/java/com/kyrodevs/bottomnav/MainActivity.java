package com.kyrodevs.bottomnav;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    final private String API_URL = "http://192.168.43.157/api";
    Handler handler = new Handler(Looper.getMainLooper());
    Runnable makeApiCallRunnable;
    private EditText searchQ;
    private RecyclerView searchRes;

    private List<SearchResult> list = new ArrayList<SearchResult>();
    SearchAdapter adapter = new SearchAdapter(list);

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchQ = findViewById(R.id.search_q);
        searchRes = findViewById(R.id.search_res);

        searchQ.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(final Editable editable) {
                Log.d(TAG, "afterTextChanged: "+ editable.toString());
                if(editable.length() == 0) { adapter.clearSearch(); }
                handler.removeCallbacks(makeApiCallRunnable);
                makeApiCallRunnable = new Runnable() {
                    @Override
                    public void run() {
                        makeApiCall(editable.toString());
                    }
                };
                handler.postDelayed(makeApiCallRunnable, 500);
            }
        });

        searchRes.setAdapter(adapter);
        searchRes.setLayoutManager(new LinearLayoutManager(this));
    }

    private void makeApiCall(String searchq) {
        String bodyString = "";
        if (searchq.length() == 0) {return;}
        try {
            bodyString = new JSONObject().put("userkey", searchq).toString();
        } catch (Exception ex) { ex.printStackTrace(); }

        ApiHandler apiCall = new ApiHandler();
        apiCall.execute(API_URL, bodyString);
    }


    public void setSearchResults(String res) {
        adapter.clearSearch();
        JSONArray resJson;
        try {
            resJson = new JSONObject(res).getJSONArray("searchRes");
            for (int i = 0; i < resJson.length(); i++) {
                JSONObject item = resJson.getJSONObject(i);
                adapter.addSearchResult(new SearchResult(item.getString("username"),
                        item.getString("userid")));
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    public class ApiHandler extends AsyncTask<String, Void, String> {

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();

        @Override
        protected String doInBackground(String... params) {
            RequestBody body = RequestBody.create(JSON, params[1]);
            Request.Builder reqBuilder = new Request.Builder()
                .url(params[0])
                .addHeader("Accept", "application/json")
                .method("POST", body);

            Request req = reqBuilder.build();
            try {
                Response res = client.newCall(req).execute();
                return res.body().string();
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            setSearchResults(s);
        }
    }

}