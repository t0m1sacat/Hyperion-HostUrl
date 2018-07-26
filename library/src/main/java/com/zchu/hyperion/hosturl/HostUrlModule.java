package com.zchu.hyperion.hosturl;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.willowtreeapps.hyperion.plugin.v1.PluginModule;

public class HostUrlModule extends PluginModule implements SharedPreferences.OnSharedPreferenceChangeListener {


    private TextView textView;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View createPluginView(@NonNull LayoutInflater layoutInflater, @NonNull ViewGroup parent) {
        final Context context = layoutInflater.getContext();
        View view = layoutInflater.inflate(R.layout.hosturl_item_plugin_layout, parent, false);
        textView = view.findViewById(R.id.tv_host_url);
        sharedPreferences = context.getSharedPreferences("host-selection", Context.MODE_PRIVATE);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        String string = sharedPreferences.getString("selected-item", null);
        if (!TextUtils.isEmpty(string)) {
            textView.setText(string);
        } else {
            textView.setText("暂未配置");
        }
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, HostSelectionActivity.class);
                context.startActivity(intent);
            }
        });
        return view;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if ("selected-item".equals(key)) {
            String string = sharedPreferences.getString("selected-item", null);
            if (!TextUtils.isEmpty(string)) {
                textView.setText(string);
            } else {
                textView.setText("暂未配置");
            }
        }
    }
}
