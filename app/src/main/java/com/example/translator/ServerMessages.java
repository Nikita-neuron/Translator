package com.example.translator;

import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

public class ServerMessages extends Thread{

    EditText editText;

    public ServerMessages(EditText editText) {
        this.editText = editText;
    }

    private String KEY = "pdct.1.1.20210412T141130Z.7fe24c3464fb114c.a91bd86212d96a2ed24c9e0a44fc721cd4e118ea";

    @Override
    public void run() {
        String word = "hello+wo";
        String lang = "en";
        String request = String.format("https://predictor.yandex.net/api/v1/predict.json/complete?key=%s&q=%s&lang=%s", KEY, word, lang);
        try {
            URLConnection connection = new URL(request).openConnection();
            Scanner in = new Scanner(connection.getInputStream());
            String response = in.nextLine();
            editText.setText(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
