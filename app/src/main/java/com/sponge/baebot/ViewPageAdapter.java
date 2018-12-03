package com.sponge.baebot;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;

public class ViewPageAdapter extends PagerAdapter {
    Activity activity;
    String[] images;
    LayoutInflater inflater;

    public ViewPageAdapter(Activity activity, String[] images){
        this.activity = activity;
        this.images = images;

    }
    @Override
    public int getCount(){
        return images.length;
    }
    @Override
    public boolean isViewFromObject(View view, Object object){
       return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position){
        inflater = (LayoutInflater) activity.getApplication().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.tutorial_item,container, false);

        ImageView image;
        image = (ImageView) itemView.findViewById(R.id.imageView);
        DisplayMetrics dis = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dis);
        int height = dis.heightPixels;
        int width = dis.widthPixels;
//        image.getLayoutParams().height = (int) (height - 0.25*height);
//        image.getLayoutParams().width = (int) (width - 0.25*width);

        try{
            if (position == 0){
                Picasso.get()
                        .load(images[0])
                        .into(image);
            } else {
                Picasso.get()
                        .load(images[position])
                        //.placeholder(R.mipmap.ic_launcher)
                        //.error(R.mipmap.ic_launcher)
                        .into(image);
            }

        }
        catch(Exception ex){

        }

        container.addView(itemView);
        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object){

//        super.destroyItem(container, position, object);
        ((ViewPager) container).removeView((View) object);
    }
}
