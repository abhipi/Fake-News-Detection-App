package com.example.fakenewsdetector;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.HashMap;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.tensorflow.lite.Interpreter;

public class SecondActivity extends AppCompatActivity {
    TextView txt;
    JSONObject j;
    String data;
    Map<String, Integer> map = new HashMap<String, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Bundle extras = getIntent().getExtras();
        data = extras.getString("words");
        txt = findViewById(R.id.one);
        txt.setText(data);
        Log.e("hi", data);
        try {
            j = new JSONObject(loadJSONFromAsset());


        } catch (JSONException e) {
            e.printStackTrace();
        }
        String jsonstr = j.toString();
        map = new Gson().fromJson(
                jsonstr, new TypeToken<HashMap<String, Integer>>() {
                }.getType()
        );
        int[] tokenized = tokenize(data.toLowerCase());
        Log.e("value", Arrays.toString(tokenized));
        int[] padded_seq = padsequence(tokenized);
        int c;
        int[][] in = new int[1][500];

        for (c = 0; c < 500; c++) {
            in[0][c] = padded_seq[c];

        }
        Log.e("h", Arrays.deepToString(in));
        float out[][] ={{0.0f}};
        try{
           Interpreter tflite= new Interpreter(loadModelFile());
           Log.e("jjj",tflite.toString());
            tflite.run(in,out);
        }catch (IOException e){
            e.printStackTrace();
            Log.e("h","It's null");
        }
        String s;
        if(out[0][0]>0.5){
            s="The news story is True";
            txt.setText(s);
        }
        else{
            s="The news story is Fake ";
            txt.setText(s);
        }
    }


    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = this.getAssets().open("word_dict.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    public int[] tokenize(String s) {
        List<String> parts = new ArrayList<String>(Arrays.asList(s.split(" ")));
        List<Integer> tokenizedMessage = new ArrayList<Integer>();
        Log.e("h", tokenizedMessage.toString());
        Log.e("l", parts.toString());
        int i, index;
        int[] u;
        for (i = 0; i < parts.size(); i++) {

            if (!(parts.get(i).trim().equals(""))) {
                index = 0;
                if (parts.get(i) == null) {
                    index = 0;
                } else {
                    if (map.get(parts.get(i)) != null) {
                        index = map.get(parts.get(i));
                    }

                }
                tokenizedMessage.add(index);
            }
        }
        u = tokenizedMessage.stream()
                .mapToInt(Integer::intValue)
                .toArray();
        return u;
    }

    public int[] padsequence(int[] a) {
        int maxlen = 500, i;
        int b[] = new int[500];
        if (a.length > maxlen) {
            for (i = 0; i < 500; i++) {
                b[i] = a[i];
            }
            return b;
        } else if (a.length < maxlen) {
            for (i = 0; i < a.length; i++) {
                b[i] = a[i];

            }
            for (; i < 500; i++) {
                b[i] = 0;
            }
            return b;
        } else {
            return a;
        }


    }
    public MappedByteBuffer loadModelFile() throws IOException {
        AssetFileDescriptor fileDescriptor = this.getAssets().openFd("model.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        Log.e("kk",fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength).toString());
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }


}