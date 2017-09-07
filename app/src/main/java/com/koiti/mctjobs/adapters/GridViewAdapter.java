package com.koiti.mctjobs.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.koiti.mctjobs.R;
import com.koiti.mctjobs.models.Image;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;

public class GridViewAdapter extends ArrayAdapter {
    private Context context;
    private int layoutResourceId;
    private ArrayList<Image> data = new ArrayList();
    private RelativeLayout.LayoutParams mImageViewLayoutParams;

    public GridViewAdapter(Context context, int layoutResourceId, ArrayList<Image> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;

        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.gallery_image);
            holder.image.setScaleType(ImageView.ScaleType.CENTER_CROP);

            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        // Image
        final Image image = data.get(position);

        // onClickListener
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setDataAndType (Uri.parse("file://" + image.getPath()), "image/*" );
                context.startActivity(intent);
            }
        });

        // imageLoader
        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .cacheOnDisc(true).resetViewBeforeLoading(true).build();
//                .showImageForEmptyUri(fallback)
//                .showImageOnFail(position).build();
//                .showImageOnLoading(fallback).build();


        imageLoader.displayImage("file://" + image.getPath() , holder.image, options);
        return row;
    }

    static class ViewHolder {
        ImageView image;
    }
}
