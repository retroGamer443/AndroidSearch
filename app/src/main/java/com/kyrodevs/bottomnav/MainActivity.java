package com.kyrodevs.bottomnav;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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

    final private String API_URL = "http://192.168.43.157/api";
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

        // Adding listener for watching text change
        searchQ.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Leave this method alone
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String bodyString = "";
                adapter.clearSearch();

                // when text is changed this method is called and changed text is given in charSequence
                // If text is small enough then return
                if (charSequence.length() < 1) {
                    return;
                }

                try {
                    // Creating string from json object for request
                    bodyString = new JSONObject().put("userkey", charSequence).toString();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                // Custom ApiHandler class for managing all api calls Made by me.
                // It's job is to execute network request on separate thread
                // Sending the API_URL and body as a string parameters
                ApiHandler apiCall = new ApiHandler();
                apiCall.execute(API_URL, bodyString);
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Leave this method alone
            }
        });

        // Setting up adapter for recycler View
        searchRes.setAdapter(adapter);
        searchRes.setLayoutManager(new LinearLayoutManager(this));
    }


    public void setSearchResults(String res) {
        // This method takes response string from api call and parse into JSON
        // All the UI updates happen here
        // Always use try catch block for JSON parsing
        JSONArray resJson;

        try {
            // getting ArrayObject from parsing res string and getJSONArray() method
            resJson = new JSONObject(res).getJSONArray("searchRes");
            // looping over arrayobject
            for (int i = 0; i < resJson.length(); i++) {
                JSONObject item = resJson.getJSONObject(i);
                // Adding the data from JSONObject to Model class SearchResult and adding it to
                // recyclerView adapter
                adapter.addSearchResult(new SearchResult(item.getString("username"),
                        item.getString("userid")));

            }
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }


    }

    public class ApiHandler extends AsyncTask<String, Void, String> {
        // API handler class which extends AsyncTask takes String as input
        // Void as progress indicator, and String as output

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        // Creating new Instance of OkHttpClient for making requests
        OkHttpClient client = new OkHttpClient();

        @Override
        protected String doInBackground(String... params) {
            // This method will run in background when ApiHandler object runs execute() method
            // Creating request body for POST operation by Creating RequestBody object which takes
            // a MediaType object and String parameters from execute() method

            RequestBody body = RequestBody.create(JSON, params[1]);

            // RequestBuilder class is used to create RequestObject
            // It takes url() , addHeader(), method() to complete a post request
            Request.Builder reqBuilder = new Request.Builder()
                .url(params[0])
                .addHeader("Accept", "application/json")
                .method("POST", body);

            // Building Request object from RequestBuilder
            Request req = reqBuilder.build();
            try {
                // Getting Response back from client by invoking newCall() with request object
                Response res = client.newCall(req).execute();
                // Returning response in body as a string
                return res.body().string();
            } catch (Exception ex) {
                ex.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            // This method run after doInBackground() method finishes in background thread
            // It receives parameters from doInBackground()
            super.onPostExecute(s);

            // Calling setSearchResults to update UI
            setSearchResults(s);
        }
    }

}