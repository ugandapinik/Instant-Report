package com.wohhie.www.myworksafe.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.wohhie.www.myworksafe.R;
import com.wohhie.www.myworksafe.model.Item;

import java.util.ArrayList;

public class GridViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Item> items;

    public GridViewAdapter(){}

    public GridViewAdapter(Context context, ArrayList<Item> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.gridview_layout, parent, false);
        ImageView ivItem = itemView.findViewById(R.id.gridview_image);
        TextView textView = itemView.findViewById(R.id.gridview_title);

        ivItem.setImageResource(items.get(position).getImage());
        textView.setText(items.get(position).getTitle());

        return itemView;
    }
}
