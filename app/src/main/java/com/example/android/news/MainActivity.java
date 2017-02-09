package com.example.android.news;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<NewsFeed> mNewsFeeds;
    NewsFeedAdapter adapter;
    ListView newsList;
    ArrayList<String> url;
    Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mNewsFeeds = new ArrayList<NewsFeed>();
        url = new ArrayList<String>();
        adapter = new NewsFeedAdapter(this, mNewsFeeds);
        newsList = (ListView) findViewById(R.id.newsList);
        newsList.setAdapter(adapter);
        newsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent openBrowser = new Intent(Intent.ACTION_VIEW);
                openBrowser.setData(Uri.parse(url.get(position)));
                startActivity(openBrowser);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FetchNews fetchNews = new FetchNews();
        fetchNews.execute();
    }

    public class FetchNews extends AsyncTask<Void, Void, Void> {

        public void parseNewsJson(String newsJson) throws JSONException {
            JSONObject response = new JSONObject(newsJson);
            JSONObject response1 = response.getJSONObject("response");
            JSONArray results = response1.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject story = results.getJSONObject(i);
                String webTitle = story.getString("webTitle");
                String webUrl = story.getString("webUrl");
                url.add(webUrl);
                JSONObject fields = story.getJSONObject("fields");
                String thumbnail = fields.getString("thumbnail");
                image = null;
                try {
                    InputStream in = new java.net.URL(thumbnail).openStream();
                    image = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                JSONArray tags = story.getJSONArray("tags");
                JSONObject contributor = tags.getJSONObject(0);
                String contributorName = contributor.getString("firstName");

                mNewsFeeds.add(new NewsFeed(webTitle, image, contributorName));
                update();

            }
        }

        @Override
        protected Void doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String news = null;
            String BASE_URL = "http://content.guardianapis.com/search?show-tags=contributor&show-fields=thumbnail&q=barclays%20premier%20league&api-key=test";
            try {

                URL url = new URL(BASE_URL);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream input = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (input == null) {
                    news = null;
                }
                reader = new BufferedReader(new InputStreamReader(input));
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    news = null;
                }
                news = buffer.toString();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) urlConnection.disconnect();
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                    }
                }
                try {
                    parseNewsJson(news);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }

    public void update() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
}
