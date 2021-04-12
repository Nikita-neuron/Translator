package com.example.translator;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText editText;
    Button button;

    private String KEY = "pdct.1.1.20210412T141130Z.7fe24c3464fb114c.a91bd86212d96a2ed24c9e0a44fc721cd4e118ea";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.word);
        button = findViewById(R.id.send);
        button.setOnClickListener(this);
    }

    void test(String word) {
        new Thread() {
            @Override
            public void run() {
                String lang = "en";
                String request = String.format("https://predictor.yandex.net/api/v1/predict.json/complete?key=%s&q=%s&lang=%s", KEY, word, lang);
                try {
                    URLConnection connection = new URL(request).openConnection();
                    Scanner in = new Scanner(connection.getInputStream());
                    String response = in.nextLine();
                    runOnUiThread(new Thread() {
                        @Override
                        public void run() {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = (JSONArray) jsonObject.get("text");
                                String text = (String) jsonArray.get(0);
//                                int pos = response.indexOf("\"text\":[\"");
//                                if (pos >= 0) {
//                                    int pos2 = response.indexOf("\"]", pos + 1);
//                                    editText.setText(response.substring(pos + 9, pos2));
//                                }
                                editText.setText(text);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send) {
            String word = editText.getText().toString();
            word = word.replaceAll(" ", "+");
            test(word);

        }
    }
}