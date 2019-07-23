package com.example.takeimageapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    ArrayList<String> itemList = new ArrayList<String>();

    public ImageAdapter(Context c){
        mContext = c;
    }
    void add(String path){
        itemList.add(path);
    }
    @Override
    public int getCount() {
        //jumlah total gambar
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //Mengambil satu gambar dari gallery
        ImageView imageView;
        if (convertView==null) {//if it's not recrycled, initialize some attributes
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        }else {
            imageView = (ImageView) convertView;
        }
        Bitmap bitmap = decodeSampledBitmapFormUri(itemList.get(position), 200,200);
        imageView.setImageBitmap(bitmap);
        return imageView;
    }

    private Bitmap decodeSampledBitmapFormUri(String path, int reqWidth, int reqHeight) {
        Bitmap bm = null;
        //First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options= new BitmapFactory.Options();
        BitmapFactory.decodeFile(path, options);

        //Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        //Decode bitmap with inSampleSize set
        options.inJustDecodeBounds=false;
        bm = BitmapFactory.decodeFile(path, options);

        return bm;
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        //Rav height with of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height>reqHeight || width>reqWidth){
            if (width>height){
                inSampleSize=Math.round((float)height/(float)reqHeight);
            }else {
                inSampleSize=Math.round((float)width/(float)reqWidth);
            }
        }
        return inSampleSize;
    }

}
