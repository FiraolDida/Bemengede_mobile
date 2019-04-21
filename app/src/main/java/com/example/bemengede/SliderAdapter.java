package com.example.bemengede;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;
    ImageView slideImageView;
    TextView slideHeader;
    TextView slideDesc;

    public SliderAdapter (Context context){
        this.context = context;
    }

    public int[] slideImages = {
            R.drawable.buss_icon,
            R.drawable.map_marker,
            R.drawable.appointment,
            R.drawable.time
    };

    public String[] slideHeading = {
            "BUS",
            "PRIORITIZE",
            "NOTIFY",
            "STAY INFORMED"
    };

    public String[] slideDescription = {
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."
    };

    @Override
    public int getCount() {
        return slideHeading.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == (RelativeLayout)o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        slideImageView = (ImageView) view.findViewById(R.id.slideImage);
        slideHeader = (TextView) view.findViewById(R.id.heading);
        slideDesc = (TextView) view.findViewById(R.id.description);

        slideImageView.setImageResource(slideImages[position]);
        slideHeader.setText(slideHeading[position]);
        slideDesc.setText(slideDescription[position]);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
