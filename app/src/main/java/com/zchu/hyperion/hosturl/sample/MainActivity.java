package com.zchu.hyperion.hosturl.sample;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text_view);
        if (BuildConfig.DEBUG) {
            SharedPreferences sharedPreferences = getSharedPreferences("host-selection", Context.MODE_PRIVATE);
            String selectedItem = sharedPreferences.getString("selected-item", null);
            textView.setText(selectedItem);
        }
    }


}
