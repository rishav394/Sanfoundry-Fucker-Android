package com.rishav.sanfoundryfucker;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

public class QuestionActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private ListView listView;
    private String url;
    Elements elements;
    String[] my;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        url = getIntent().getStringExtra("URL");
        try {
            new GetQuestions().execute();
        }
        catch (Exception e){

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(),"gg", Toast.LENGTH_LONG).show();
                }
            });
        }

        listView = findViewById(R.id.list);

    }

    class CustomAdaptor extends BaseAdapter {

        @Override
        public int getCount() {
            return elements.size() - 5;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @SuppressLint("ViewHolder")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.custom_layout_ques, null);
            TextView textView = convertView.findViewById(R.id.textView);
            textView.setText(my[position]);
            return convertView;
        }
    }

    private class GetQuestions extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(QuestionActivity.this);
            mProgressDialog.setTitle("Getting questions");
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

            elements = myDoc.select("div > p");
            Elements ans = myDoc.select("div > div > div > article > div > div.collapseomatic_content");
            my = new String[elements.size() - 5];
            for (int i = 1; i <  elements.size() - 4; i++){
                String temp = (elements.get(i).text());
                temp = "\n" + temp;
                temp = temp.replaceAll("a\\)","\na)");
                temp = temp.replaceAll("b\\)","\nb)");
                temp = temp.replaceAll("c\\)","\nc)");
                temp = temp.replaceAll("d\\)","\nd)");
                temp = temp.replaceAll("View Answer",
                        "\n\n"+ans.get(i-1).text().replaceAll("Explanation:", "\nExplanation:"));
                my[i-1] = temp;
            }
            return null;
        }
    }
}
