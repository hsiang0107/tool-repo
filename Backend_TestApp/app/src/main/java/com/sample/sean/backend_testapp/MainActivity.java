package com.sample.sean.backend_testapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    protected TextView entry_textv;
    protected EditText add_entry;
    protected EditText del_entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.entry_textv = (TextView) findViewById(R.id.txv_showentries);
        this.entry_textv.setMovementMethod(new ScrollingMovementMethod());
        this.add_entry = (EditText) findViewById(R.id.edv_addentry);
        this.del_entry = (EditText) findViewById(R.id.edv_delentry);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }

    public void onButtonTap(View v) throws IOException, ExecutionException, InterruptedException {
        //On click of show entries button. List the current items in DB
        String http_method = "GET";
        URL url = new URL("http://192.168.1.14:5000");
        //New a AsyncTask for Http get method
        String task_output = new HttpTask(url, http_method).execute().get();
        //Return returned JSON to text view
        this.entry_textv.setText(task_output);

    }

    public void onButtonPost(View v) throws IOException, ExecutionException, InterruptedException, JSONException {
        //On click of add entries button. Add new entry to DB, and list the current items
        String http_method = "POST";
        URL url = new URL("http://192.168.1.14:5000/add");
        String new_entry = this.add_entry.getText().toString();
        //Create json object
        JSONObject dict = new JSONObject();
        String task_output = null;
        //put new entry to the json object
        try {
            dict.put("title", new_entry);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // If new entry is not null new a AsyncTask for Http post method
        if (dict.getString("title").length() > 0) {
            task_output = new HttpTask(url, http_method, String.valueOf(dict)).execute().get();
        }
        //Return returned JSON to text view
        this.entry_textv.setText(task_output);

    }

    public void onButtonDelete(View v) throws IOException, ExecutionException, InterruptedException {
        //On click of delete entries button. Delete entry that match the ID from DB, and list the current items
        String http_method = "DELETE";
        //Get the ID to delete from UI
        String id = this.del_entry.getText().toString();
        String task_output = null;

        String del_url = "http://192.168.1.14:5000/delete/" + id;
        URL url = new URL(del_url);
        // If ID is not null new a AsyncTask for Http delete method
        if (id.length() > 0) {
            task_output = new HttpTask(url, http_method).execute().get();
        }
        //Return returned JSON to text view
        this.entry_textv.setText(task_output);

    }


    class HttpTask extends AsyncTask<Void,Void,String> {
        protected URL url;
        protected String method;
        protected String title;
        //constructor for get and delete methods
        public HttpTask(URL url, String method){
            this.url = url;
            this.method = method;
        }
        //constructor for get and post method
        public HttpTask(URL url, String method, String title){
            this.url = url;
            this.method = method;
            this.title = title;
        }

        protected void onPreExecute() {

        }

        protected String doInBackground(Void... params) {
            HttpURLConnection conn = null;
            JSONArray json_arr = null;
            String entry_str = null;
            try {
                // open connection
                conn = (HttpURLConnection) this.url.openConnection();
                //Set Http request depends on which method is preferred for the Async task
                switch (this.method) {
                    case "POST":
                        conn.setDoOutput(true);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Content-Type", "application/json");
                        conn.setRequestProperty("Accept", "application/json");
                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                        writer.write(this.title);
                        writer.close();
                        break;
                    case "DELETE":
                        conn.setRequestMethod("DELETE");
                        break;
                    default:
                        conn.setRequestMethod("GET");
                        break;
                }
                conn.connect();
                //Print http return code to log cat
                int res_code = conn.getResponseCode();
                Log.i("MainActivity", "Backend server return code: " + String.valueOf(res_code));

                //read returned json
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        conn.getInputStream(), "UTF-8"));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null)
                    sb.append(line);
                reader.close();
                //Log.i("MainActivity", sb.toString());
                JSONObject payload = new JSONObject(sb.toString());
                json_arr = payload.getJSONArray("items");
                //Log.i("MainActivity", String.valueOf(json_arr));

                //Print the json to log cat
                Log.i("MainActivity", json_arr.toString(2));
                //JSONObject obj1 = json_arr.getJSONObject(0);
                //Log.i("MainActivity", obj1.toString(2));

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
            }
            try {
                // convert json to string with 2 indentSpaces
                entry_str = json_arr.toString(2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return entry_str;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
