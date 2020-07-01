package com.example.gaid;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

class MyAdapter extends BaseAdapter {
    Context context;
    int layout;
    int img[];
    LayoutInflater inf;
    private Node node[];
    private ArrayList<Integer> path;

    public MyAdapter(Context context, int layout, int[] img, Node[] node, ArrayList<Integer> path) {
        this.context = context;
        this.layout = layout;
        this.img = img;
        this.path = path;
        this.node = node;
        inf = (LayoutInflater) context.getSystemService
                (Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return img.length;
    }

    @Override
    public Object getItem(int position) {
        return img[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = inf.inflate(layout, null);
        convertView.setLayoutParams(new GridView.LayoutParams(160, 160));

        ImageView iv = (ImageView) convertView.findViewById(R.id.imageView1);
        TextView tv = (TextView) convertView.findViewById(R.id.textView1);
        Log.d("pathsize", Integer.toString(path.size()));

        for (int i = 0; i < img.length; i++) {
            if (i == position) {
                if (node[i].getKind().equals("P")) {
                    iv.setImageResource(R.drawable.path);
                }
                if (node[i].getKind().equals("R")) {
                    iv.setImageResource(R.drawable.room);
                    tv.setText(Integer.toString(i)+"호실");
                }
                if (node[i].getKind().equals("E")) {
                    iv.setImageResource(R.drawable.elevator);
                    tv.setText("E");

                }
                if(i==39){
                    iv.setImageResource(R.drawable.current);
                }

                if (node[i].getKind().equals("W")) {
                    iv.setImageResource(R.drawable.black);
                }
                if(i==51){
                    iv.setImageResource(R.drawable.enter);
                }
            }
        }
        //l
        for (int i = path.size() - 1; i >= 0; i--) {
            Log.d("i", Integer.toString(i));
            if (path.get(i) == position) {
                    if(i==path.size()-1)
                    {
                        iv.setImageResource(R.drawable.current);
                    }
                    else if(i==0){
                        iv.setImageResource(R.drawable.destination);
                    }
                    else{
                        iv.setImageResource(R.drawable.road);

                    }


            }
        }





        return convertView;
    }
}


