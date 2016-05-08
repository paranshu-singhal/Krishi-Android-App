package com.majors.paranshusinghal.krishi;

import android.app.Activity;
import android.content.Context;

import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.support.v7.widget.Toolbar;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class NewsActivity extends Activity {

    private static final String TAG = "com.majors.paranshusinghal.krishi";
    private static final String TAGlog = "myTAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setCollapsedTitleTextColor(Color.WHITE);
        collapsingToolbar.setExpandedTitleColor(Color.WHITE);
        collapsingToolbar.setTitle("Current News");

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final FloatingActionButton floatingActionButton = (FloatingActionButton)findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_rotate));
            }
        });

        DownloadXmlTask task = new DownloadXmlTask();
        task.execute();
    }

    private class DownloadXmlTask extends AsyncTask<Void, Void, List<newselement>> {

        @Override
        protected List<newselement> doInBackground(Void... urls) {

            InputStream stream = null;
            List<newselement> entries = new ArrayList<>();
            try {
                String urlString = "http://economictimes.indiatimes.com/news/economy/agriculture/rssfeeds/1202099874.cms";
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                stream = conn.getInputStream();
                StackOverflowXmlParser objParse = new StackOverflowXmlParser();
                entries = objParse.parse(stream);
            } catch (Throwable t) {
                t.printStackTrace();
            } finally {
                if (stream != null) {
                    try {
                        stream.close();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
            return entries;
        }

        @Override
        protected void onPostExecute(List<newselement> list) {

            //Log.d(TAGlog, "onPostExecute");
            //Toast.makeText(getApplicationContext(), "onPostExecute", Toast.LENGTH_SHORT).show();
            try {
                RecyclerView news_list = (RecyclerView) findViewById(R.id.recyclerview);
                NewsAdaptor adaptor1 = new NewsAdaptor(list, NewsActivity.this);
                news_list.setAdapter(adaptor1);
                news_list.setLayoutManager(new LinearLayoutManager(NewsActivity.this));
            } catch (Throwable t) {
                Log.d(TAGlog, t.getMessage());
                t.printStackTrace();
            }
        }
    }

    public class StackOverflowXmlParser {
        private final String ns = null;

        public List<newselement> parse(InputStream in) throws XmlPullParserException, IOException {

            XmlPullParser parser = null;
            try {
                parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);
                parser.nextTag();
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return readFeed(parser);
        }

        private List<newselement> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
            List<newselement> entries = new ArrayList<>();
            parser.require(XmlPullParser.START_TAG, ns, "rss");
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals("channel")) {
                    entries = readChannel(parser);
                } else {
                    skip(parser);
                }
            }
            return entries;
        }

        private List<newselement> readChannel(XmlPullParser parser) throws XmlPullParserException, IOException {
            List<newselement> entries = new ArrayList<>();
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals("item")) {
                    newselement news = readEntry(parser);
                    entries.add(news);
                } else {
                    skip(parser);
                }
            }
            return entries;
        }

        private newselement readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
            String title = null;
            String summary = null;
            String link = null;
            String pubDate = null;
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                switch (parser.getName()) {
                    case "title":
                        title = readTitle(parser);
                        break;
                    case "description":
                        summary = readSummary(parser);
                        break;
                    case "link":
                        link = readLink(parser);
                        break;
                    case "pubDate":
                        pubDate = readDate(parser);
                        break;
                    default:
                        skip(parser);
                }
            }
            return new newselement(title, summary, link, pubDate);
        }

        private String readTitle(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, ns, "title");
            String title = readText(parser);
            parser.require(XmlPullParser.END_TAG, ns, "title");
            return title;
        }

        // Processes link tags in the feed.
        private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, ns, "link");
            String link = readText(parser);
            parser.require(XmlPullParser.END_TAG, ns, "link");
            return link;
        }

        // Processes summary tags in the feed.
        private String readSummary(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, ns, "description");
            String summary = readText(parser);
            parser.require(XmlPullParser.END_TAG, ns, "description");
            summary = summary.replaceAll("\\<.*\\>", "");
            return summary;
        }

        private String readDate(XmlPullParser parser) throws IOException, XmlPullParserException {
            parser.require(XmlPullParser.START_TAG, ns, "pubDate");
            String summary = readText(parser);
            parser.require(XmlPullParser.END_TAG, ns, "pubDate");
            return summary;
        }

        // For the tags title and summary, extracts their text values.
        private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
            String result = "";
            if (parser.next() == XmlPullParser.TEXT) {
                result = parser.getText();
                parser.nextTag();
            }
            return result;
        }

        private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                throw new IllegalStateException();
            }
            int depth = 1;
            while (depth != 0) {
                switch (parser.next()) {
                    case XmlPullParser.END_TAG:
                        depth--;
                        break;
                    case XmlPullParser.START_TAG:
                        depth++;
                        break;
                }
            }
        }

    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private JSONArray convert2JSON(List<newselement> list) {

        JSONArray array = new JSONArray();
        for (newselement element : list) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("title", element.getTitle());
                obj.put("description", element.getDescription());
                obj.put("link", element.getLink());
                obj.put("pubDate", element.getPubDate());
            } catch (Throwable t) {
                Log.d(TAGlog,"convert2JSON  "+t.getMessage());
                t.printStackTrace();
            }
            array.put(obj);
        }
        return array;
    }

    private List<newselement> convert2List(JSONArray array) {

        List<newselement> list = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                newselement element = new newselement(obj.getString("title"), obj.getString("description"),
                            obj.getString("link"), obj.getString("pubDate"));
                    list.add(element);
            }
        } catch (Throwable t) {
            t.printStackTrace();
            Log.d(TAGlog, "convert2List "+t.getMessage());
        }
        return list;
    }

}
