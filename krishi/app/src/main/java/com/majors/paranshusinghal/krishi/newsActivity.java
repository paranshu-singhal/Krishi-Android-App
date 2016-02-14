package com.majors.paranshusinghal.krishi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class newsActivity extends Activity {

    private static final String TAG = "com.majors.paranshusinghal.krishi";
    private static final String TAGlog = "myTAG";
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        /*
        final ImageView newsImageView = (ImageView) findViewById(R.id.news_activity_imageView);
        newsImageView.post(new Runnable() {
            @Override
            public void run() {
                int ht = newsImageView.getMeasuredHeight();
                int wd = newsImageView.getMeasuredWidth();
                int resID = getResources().getIdentifier("flat_hulk", "drawable", TAG);
                Bitmap unscaledBitmap = BitmapFactory.decodeResource(getResources(), resID);
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(unscaledBitmap, wd, ht, true);
                newsImageView.setImageBitmap(scaledBitmap);
            }
        });
        */
        progressBar = (ProgressBar)findViewById(R.id.news_progressBar);
        progressBar.setVisibility(View.GONE);

        DownloadXmlTask task = new DownloadXmlTask();
        task.execute();

        ListView news_list = (ListView)findViewById(R.id.news_activity_listView);
        news_list.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        newselement ns = (newselement)parent.getItemAtPosition(position);
                        Bundle bundle = new Bundle();
                        bundle.putString("link", ns.getLink());
                        Intent intent = new Intent(newsActivity.this, web_view.class);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                }
        );
    }

    private class DownloadXmlTask extends AsyncTask<Void,Void,List<newselement> > {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<newselement> doInBackground(Void... urls) {

            InputStream stream = null;
            List<newselement> entries = new ArrayList<>();
            try {
                String urlString = "http://economictimes.indiatimes.com/news/economy/agriculture/rssfeeds/1202099874.cms";
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                stream = conn.getInputStream();
                StackOverflowXmlParser objParse = new StackOverflowXmlParser();
                entries = objParse.parse(stream);
            } catch (Throwable t) {
                t.printStackTrace();}
              finally {
                if (stream != null) {
                    try {stream.close();} catch (Throwable t) {t.printStackTrace();}
                }
            }
            return entries;
        }
        @Override
        protected void onPostExecute(List<newselement> list) {
            progressBar.setVisibility(View.GONE);
            Log.d(TAGlog, "onPostExecute");
            try {
                ListView listView = (ListView) findViewById(R.id.news_activity_listView);
                customNewsAdaptor adaptor = new customNewsAdaptor(newsActivity.this, R.layout.listview_raw, list);
                listView.setAdapter(adaptor);
            }
            catch (Throwable t){
                Log.d(TAGlog,t.getMessage());
                t.printStackTrace();
            }
        }
    }
    public class StackOverflowXmlParser {
        private final String ns = null;
        public List<newselement> parse(InputStream in) throws XmlPullParserException, IOException {

            XmlPullParser parser=null;
            try {
                parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(in, null);
                parser.nextTag();
            }
            catch (Throwable t){t.printStackTrace();}
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
                    entries=readChannel(parser);
                }else{
                    skip(parser);
                }
            }
            return entries;
        }
        private  List<newselement> readChannel(XmlPullParser parser) throws XmlPullParserException, IOException {
            List<newselement> entries = new ArrayList<>();
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();
                if (name.equals("item")) {
                    newselement news = readEntry(parser);
                    entries.add(news);
                }else{
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
                String name = parser.getName();
                if (name.equals("title")) {
                    title = readTitle(parser);
                } else if (name.equals("description")) {
                    summary = readSummary(parser);
                } else if (name.equals("link")) {
                    link = readLink(parser);
                } else if (name.equals("pubDate")) {
                    pubDate = readDate(parser);
                }
                else{
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
}

