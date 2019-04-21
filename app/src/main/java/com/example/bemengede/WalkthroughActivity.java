package com.example.bemengede;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WalkthroughActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private SliderAdapter sliderAdapter;
    private TextView[] dots;
    private Button btnNext;
    private Button btnPrevious;
    private RelativeLayout walkThrough;

    private int currentPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walkthrough);

        viewPager = (ViewPager)findViewById(R.id.viewPager);
        dotsLayout = (LinearLayout)findViewById(R.id.dotsLayout);

        sliderAdapter = new SliderAdapter(this);
        viewPager.setAdapter(sliderAdapter);

        btnNext = (Button)findViewById(R.id.next);
        btnPrevious = (Button)findViewById(R.id.previous);

        walkThrough = (RelativeLayout)findViewById(R.id.walkThrough);

        addDotsIndicator(0);
        viewPager.addOnPageChangeListener(viewListener);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(currentPage + 1);
                if (btnNext.getText().equals("Finish")){
                    Intent intent = new Intent(WalkthroughActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(currentPage - 1);
            }
        });
    }

    public void addDotsIndicator(int position){
        dots = new TextView[4];
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorTransparentWhite));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[position].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            addDotsIndicator(i);
            currentPage = i;

            if (i == 0){
                btnNext.setEnabled(true);
                btnPrevious.setEnabled(false);
                btnPrevious.setVisibility(View.INVISIBLE);
                btnPrevious.setText("");
            } else if (i == dots.length - 1){
                btnNext.setEnabled(true);
                btnPrevious.setEnabled(true);
                btnPrevious.setVisibility(View.VISIBLE);
                btnNext.setText("Finish");
                btnPrevious.setText("Back");
            }else {
                btnNext.setEnabled(true);
                btnPrevious.setEnabled(true);
                btnPrevious.setVisibility(View.VISIBLE);
                btnNext.setText("Next");
                btnPrevious.setText("Back");
            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };
}
