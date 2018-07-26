package com.zchu.hyperion.hosturl;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;

public class HostSelectionAdapter extends BaseAdapter {
    private ArrayList<String> data;
    private Context context;
    private LayoutInflater layoutInflater;
    private int selectedPosition = -1;
    private String selectedItem = null;
    private SharedPreferences preferences;
    private int colorAccent;

    public HostSelectionAdapter(Context context, SharedPreferences preferences, ArrayList<String> data) {
        this.context = context;
        this.preferences = preferences;
        this.data = data;
        layoutInflater = LayoutInflater.from(context);
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        colorAccent = typedValue.data;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public String getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data.hashCode();
    }

    @Override
    public View getView(final int position, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = layoutInflater.inflate(R.layout.list_item_host, viewGroup, false);
        }
        TextView tv_host_url = view.findViewById(R.id.tv_host_url);
        RadioButton rb_check = view.findViewById(R.id.rb_check);
        tv_host_url.setText(getItem(position));
        if (isSelected(position)) {
            rb_check.setChecked(true);
            tv_host_url.setTextColor(colorAccent);
            view.setOnLongClickListener(null);
        } else {
            tv_host_url.setTextColor(Color.GRAY);
            rb_check.setChecked(false);
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showDeleteDialog(position);
                    return true;
                }
            });
        }
        rb_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    setSelectedPosition(position);
                    notifyDataSetChanged();
                }
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedPosition(position);
                notifyDataSetChanged();
            }
        });
        return view;
    }

    private void showDeleteDialog(final int position) {
        new AlertDialog.Builder(context)
                .setMessage("确定要删除 (" + getItem(position) + ")  该主机地址吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        data.remove(position);
                        preferences
                                .edit()
                                .putStringSet("list", new HashSet<>(data))
                                .apply();
                        if(position<selectedPosition) {
                            selectedPosition = -1;
                        }
                        notifyDataSetChanged();
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private boolean isSelected(int position) {
        if (selectedItem == null) {
            return false;
        }
        if (selectedPosition == -1) {
            selectedPosition = data.indexOf(selectedItem);
        }
        return position == selectedPosition;
    }

    public void setSelectedItem(String selectedItem) {
        if (selectedItem == null) {
            selectedPosition = -1;
            return;
        }
        this.selectedItem = selectedItem;
        selectedPosition = data.indexOf(selectedItem);
        preferences.edit().putString("selected-item", selectedItem).apply();
    }

    public void setSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        selectedItem = data.get(selectedPosition);
        preferences.edit().putString("selected-item", selectedItem).apply();
    }

    public void addData(String item) {
        data.add(item);
        notifyDataSetChanged();
    }

    public ArrayList<String> getData() {
        return data;
    }
}
