package com.jinhaihan.hydrogenlivewallpaper.ViewPager;


import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.PagerAdapter;

import com.jinhaihan.hydrogenlivewallpaper.R;

import java.util.ArrayList;
import java.util.List;

public class CardPagerAdapter extends PagerAdapter implements CardAdapter{

    private List<CardView> mViews;
    private List<Bitmap> bitmaps;
    private float mBaseElevation;

    public CardPagerAdapter() {
        mViews = new ArrayList<>();
        mViews.add(null);
        mViews.add(null);
        bitmaps = new ArrayList<>();
    }

    public void SetBitmaps(Bitmap first,Bitmap second){
        if(bitmaps.size() == 0){
            bitmaps.add(first);
            bitmaps.add(second);
        }
        else {
            bitmaps.set(0,first);
            bitmaps.set(1,second);
        }

        notifyDataSetChanged();
    }

    public void Loading(){
        bitmaps.clear();
        notifyDataSetChanged();
    }

    public float getBaseElevation() {
        return mBaseElevation;
    }

    @Override
    public CardView getCardViewAt(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext())
                .inflate(R.layout.adapter, container, false);
        container.addView(view);

        CardView cardView = (CardView) view.findViewById(R.id.cardView);

        if(bitmaps.size() !=0 ){
            ImageView imageView = view.findViewById(R.id.AdapterImage);
            imageView.setImageBitmap(bitmaps.get(position));
            View preview = view.findViewById(R.id.preview);
            preview.setVisibility(View.VISIBLE);
        }
        else {
            ImageView imageView = view.findViewById(R.id.AdapterImage);
            imageView.setImageBitmap(null);
            View preview = view.findViewById(R.id.preview);
            preview.setVisibility(View.INVISIBLE);
        }

        TextView name = view.findViewById(R.id.name);
        switch (position){
            case 0:
                name.setText("主图");
                break;
            case 1:
                name.setText("副图");
                break;
        }

        if (mBaseElevation == 0) {
            mBaseElevation = cardView.getCardElevation();
        }

        cardView.setMaxCardElevation(mBaseElevation * MAX_ELEVATION_FACTOR);
        mViews.set(position, cardView);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
        mViews.set(position, null);
    }
}
