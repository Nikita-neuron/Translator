package com.example.translator;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

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
    TextView textView;

    Spinner spinner;

    String result;

    private final String KEY = "pdct.1.1.20210412T141130Z.7fe24c3464fb114c.a91bd86212d96a2ed24c9e0a44fc721cd4e118ea";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.word);
        textView = findViewById(R.id.predict);
        spinner = findViewById(R.id.language);
        button = findViewById(R.id.send);
        button.setOnClickListener(this);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void afterTextChanged(Editable s) {
                String line = editText.getText().toString();

                String language = spinner.getSelectedItem().toString();
                if (language.equals("Русский")) language = "ru";
                if (language.equals("English")) language = "en";

                test(line, language);
            }
        });
    }

    void test(String text, String language) {
        final String[] line = {text};
        new Thread() {
            @Override
            public void run() {
                String word = line[0].replaceAll(" ", "+");
                if (word.equals("")) return;
                String request = String.format("https://predictor.yandex.net/api/v1/predict.json/complete?key=%s&q=%s&lang=%s", KEY, word, language);
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
                                int pos = (int) jsonObject.get("pos");
                                String text = (String) jsonArray.get(0);

                                System.out.println("pos: " + pos + " text: " + text);
                                if (pos > 0) {
                                    for (int i = 0; i < pos; i++) {
                                        line[0] += " ";
                                    }
                                    line[0] += text;
                                } else if (pos < 0) {
                                    line[0] = line[0].substring(0, line[0].length() + pos);
                                    line[0] += text;
                                }
                                result = line[0];
                                textView.setText(text);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.send) {
            if (result != null && !result.equals(" ")) {
                editText.setText(result);
            }
        }
    }
}