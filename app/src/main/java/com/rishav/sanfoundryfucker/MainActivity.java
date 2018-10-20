package com.rishav.sanfoundryfucker;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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

public class MainActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private ListView listView;
    private String[] my = new String[19];
    Elements elements;

    @Override
    public void finish() {
        super.finish();
        onLeaveThisActivity();
    }

    protected void onLeaveThisActivity() {
        // Dont want an animation here
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

        boolean connected;
        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        connected = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED;
        if (!connected){
            AlertDialog.Builder dlgAlert = new AlertDialog.Builder(this);
            dlgAlert.setTitle("No Internet");
            dlgAlert.setMessage("Make sure you are connecting to the internet and try again");
            dlgAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            dlgAlert.setCancelable(false);
            AlertDialog alert = dlgAlert.create();
            alert.show();
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorAccent));
        }
        else {

            ActionBar actionBar = getSupportActionBar();
            Objects.requireNonNull(actionBar).setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.mipmap.ic_launcher_foreground);
            actionBar.setDisplayUseLogoEnabled(true);
            new GetCourse().execute();
            listView = findViewById(R.id.list);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d("test", String.valueOf(position));
                    Element ele = elements.get(position + 5);
                    String link = ele.attr("href");
                    Intent intent = new Intent(getBaseContext(), SubjectActivity.class);
                    intent.putExtra("URL", link);
                    Log.d("test", link);
                    startActivity(intent);
                }
            });
        }
    }

    class CustomAdaptor extends BaseAdapter{

        @Override
        public int getCount() {
            return 17;
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
    private class GetCourse extends AsyncTask<Void,Void,Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setTitle("Getting courses");
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
            String url = "https://www.sanfoundry.com/";
            Document myDoc = null;
            try {
                myDoc = Jsoup.connect(url).get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            elements = Objects.requireNonNull(myDoc).select("li > a");

            for (int i = 5; i <  23; i++){
                my[i-5] = elements.get(i).text();
            }

            return null;
        }
    }
}
