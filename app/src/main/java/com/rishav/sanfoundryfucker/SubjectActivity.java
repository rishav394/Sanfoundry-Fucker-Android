package com.rishav.sanfoundryfucker;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Objects;

public class SubjectActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private ListView listView;
    private String url;
    Elements elements;
    String[] my;

    @Override
    public void finish() {
        super.finish();
        onLeaveThisActivity();
    }

    protected void onLeaveThisActivity() {
        overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        onStartNewActivity();
    }

    @Override
    public void startActivity(Intent intent, Bundle options) {
        super.startActivity(intent, options);
        onStartNewActivity();
    }

    protected void onStartNewActivity() {
        overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();
        Objects.requireNonNull(actionBar).setDisplayShowHomeEnabled(true);
        actionBar.setLogo(R.mipmap.ic_launcher_foreground);
        actionBar.setDisplayUseLogoEnabled(true);
        url = getIntent().getStringExtra("URL");
        new GetSubject().execute();
        listView = findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("test", String.valueOf(position));
                Element ele = elements.get(position);
                String link = ele.attr("href");
                Intent intent = new Intent(getBaseContext(), TopicActivity.class);
                intent.putExtra("URL", link);
                Log.d("test", link);
                startActivity(intent);
            }
        });
    }


    class CustomAdaptor extends BaseAdapter {

        @Override
        public int getCount() {
            return elements.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint({"ViewHolder", "InflateParams"})
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.custom_layout, null);
            TextView textView = convertView.findViewById(R.id.textView);
            textView.setText(my[position]);
            return convertView;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class GetSubject extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(SubjectActivity.this);
            mProgressDialog.setTitle("Getting subjects");
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            CustomAdaptor adaptor = new CustomAdaptor();
            listView.setAdapter(adaptor);
            mProgressDialog.dismiss();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Document myDoc = null;
            try {
                myDoc = Jsoup.connect(url).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            elements = Objects.requireNonNull(myDoc).select("tr > td > li > a");
            my = new String[elements.size()];
            for (int i = 0; i <  elements.size(); i++){
                my[i] = elements.get(i).text();
            }

            return null;
        }
    }
}
