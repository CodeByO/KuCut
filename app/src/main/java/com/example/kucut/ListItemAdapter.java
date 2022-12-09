package com.example.kucut;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.net.URI;
import java.util.ArrayList;

public class ListItemAdapter extends BaseAdapter {
    ArrayList<ListItem> items= new ArrayList<ListItem>();
    Context context;
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
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        context = parent.getContext();
        ListItem listItem = items.get(position);

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_item,parent,false);

        }
        TextView nameText = convertView.findViewById(R.id.name);
        ImageView Image = convertView.findViewById(R.id.shortcutImg);
        String img_path = listItem.getImg();
       if(!img_path.equals("null")){
          Uri img =  Uri.parse("file:///" + Environment.getExternalStorageDirectory() + img_path);
            Image.setImageURI(img);
        }else{
            Image.setImageResource(R.mipmap.ic_launcher);
       }
        nameText.setText(listItem.getName());

        return convertView;
    }
    public void addItem(ListItem item){
        items.add(item);
    }
    public void removeItem(ListItem item){
        items.remove(item);
    }
}