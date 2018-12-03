package com.earl;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.earl.scan.RiskLevel;
import com.earl.scan.Verdict;

class FullScanAdapter extends ArrayAdapter<Verdict> {
    private static final String TAG = FullScanAdapter.class.getName();

    FullScanAdapter(Context context, Verdict[] results) {
        super(context, R.layout.row_full_scan, results);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            convertView = layoutInflater.inflate(R.layout.row_full_scan, parent, false);
            holder.icon = convertView.findViewById(R.id.image_view_icon);
            holder.name = convertView.findViewById(R.id.text_view_app_name);
            holder.risk = convertView.findViewById(R.id.text_view_risk);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Verdict result = getItem(position);
        if (result == null) {
            return convertView;
        }

        holder.icon.setImageDrawable(result.getApkIcon());
        holder.name.setText(result.getApkName());
        holder.risk.setText(String.valueOf(result.getRiskAsPercentage()));
        int color = result.getColor();
        holder.risk.setTextColor(color);
        holder.name.setTextColor(color);

        return convertView;
    }

    static private class ViewHolder {
        ImageView icon;
        TextView name;
        TextView risk;
    }
}
