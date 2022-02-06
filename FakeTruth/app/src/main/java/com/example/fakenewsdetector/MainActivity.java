package com.example.fakenewsdetector;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.net.SocketTimeoutException;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;


public class MainActivity extends AppCompatActivity {
    TextInputEditText url_find;
    Button scrape;
    TextView text;
    String final_text="hi";
    public interface AsyncResponse {
        void processFinish(String output);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        url_find = findViewById(R.id.url);
        scrape = findViewById(R.id.button);
        text = findViewById(R.id.textView2);
        url_find.setText("");
        scrape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = String.valueOf(url_find.getText());
                if (url.length() > 10) {
                    doIT obj = new doIT(new AsyncResponse() {

                        @Override
                        public void processFinish(String output) {
                            Log.d("Response", (String) output);

                            text.setText((String) output);
                            Intent i= new Intent(MainActivity.this, SecondActivity.class);
                            i.putExtra("words", (String)output);
                            startActivity(i);
                            finish();
                        }
                    });
                    obj.execute(url);
                   // Log.e("Myapp",words);

                } else {
                    Toast.makeText(getApplicationContext(), "Enter Valid URL", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


    public class doIT extends AsyncTask<String, String, String> {
        String words;
        public AsyncResponse delegate = null;//Call back interface

        public doIT(AsyncResponse asyncResponse) {
            delegate = asyncResponse;//Assigning call back interface through constructor
        }
        @Override
        protected String doInBackground(String... params) {
            try {
                Document document = Jsoup.connect(params[0]).get();
                words = document.text();
            } catch (IOException e) {
                e.printStackTrace();
                words = "Error (check input)";
            }
            return words;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            delegate.processFinish(aVoid);



        }
    }
}



