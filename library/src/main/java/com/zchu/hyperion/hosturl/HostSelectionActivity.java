package com.zchu.hyperion.hosturl;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class HostSelectionActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView listHost;
    private SharedPreferences mPreferences;
    private HostSelectionAdapter adapter;
    private Button tvRestart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_selection);
        initView();
        this.setSupportActionBar(toolbar);
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HostSelectionActivity.this.onBackPressed();
                }
            });
            actionBar.setTitle("主机地址选择");
        }
        mPreferences = getSharedPreferences("host-selection", Context.MODE_PRIVATE);
        Set<String> list = mPreferences.getStringSet("list", null);
        String selectedItem = mPreferences.getString("selected-item", null);
        if (list != null && list.size() > 0) {
            if (!TextUtils.isEmpty(selectedItem)) {
                if (!list.contains(selectedItem)) {
                    list.add(selectedItem);
                    mPreferences.edit().putStringSet("list", list).apply();
                }
            }
            Object[] objects = list.toArray();
            ArrayList<String> strings = new ArrayList<>();
            for (Object object : objects) {
                strings.add((String) object);
            }
            setData(strings);
        } else {
            if (!TextUtils.isEmpty(selectedItem)) {
                list = new HashSet<>();
                list.add(selectedItem);
                mPreferences.edit().putStringSet("list", list).apply();
                ArrayList<String> strings = new ArrayList<>();
                strings.add(selectedItem);
                setData(strings);
            } else {
                setData(new ArrayList<String>());
            }

        }
        adapter.setSelectedItem(selectedItem);

    }

    private void setData(ArrayList<String> data) {
        adapter = new HostSelectionAdapter(this, mPreferences, data);
        listHost.setAdapter(adapter);
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        listHost = findViewById(R.id.list_host);

        tvRestart = findViewById(R.id.tv_restart);
        tvRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_host, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_add) {
            showAddDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddDialog() {
        final EditText editText = new EditText(this);
        editText.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup parent = (ViewGroup) editText.getParent();
                if (parent instanceof FrameLayout) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) editText.getLayoutParams();
                    layoutParams.leftMargin = dp2px(24);
                    layoutParams.rightMargin = dp2px(24);
                    editText.requestLayout();
                }
            }
        });
        editText.setText("https://");
        editText.setSelection(editText.getText().length());
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("添加主机地址")
                .setView(editText)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", null)
                .show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(editText.getText())) {
                    String host = editText.getText().toString();
                    if (isURL(host) && isHttpUrl(host)) {
                        if (!adapter.getData().contains(host)) {
                            adapter.addData(host);
                            mPreferences
                                    .edit()
                                    .putStringSet("list", new HashSet<>(adapter.getData()))
                                    .apply();
                        } else {
                            Toast.makeText(HostSelectionActivity.this, "主机地址已存在", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(HostSelectionActivity.this, "主机地址不合法", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                dialog.dismiss();
            }
        });
        ;
    }

    private int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, this.getResources().getDisplayMetrics());
    }

    public static boolean isHttpUrl(String url) {
        if (url == null) {
            return false;
        }
        String scheme = Uri.parse(url).getScheme();
        return "http".equals(scheme) || "https".equals(scheme);
    }

    public static boolean isURL(CharSequence input) {
        return isMatch("[a-zA-z]+://[^\\s]*", input);
    }

    public static boolean isMatch(String regex, CharSequence input) {
        return input != null && input.length() > 0 && Pattern.matches(regex, input);
    }

}
