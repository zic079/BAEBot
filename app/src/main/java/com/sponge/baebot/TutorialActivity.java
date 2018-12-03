package com.sponge.baebot;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import com.sponge.baebot.ViewPageAdapter;

import java.io.File;

public class TutorialActivity extends AppCompatActivity {
    ViewPager viewPager;
    ViewPageAdapter adapter;

    //File f = new File("drawable/tutorial1.png");



    private String[] images = {
            "https://firebasestorage.googleapis.com/v0/b/baebot-b798f.appspot.com/o/tutorial1.png?alt=media&token=3dbb52eb-3e48-42dd-9b17-5d69e2663e6e",
                    "https://firebasestorage.googleapis.com/v0/b/baebot-b798f.appspot.com/o/tutorial2.png?alt=media&token=882fb5ed-9998-4d35-b0ca-14c6e20a28d7",
                    "https://firebasestorage.googleapis.com/v0/b/baebot-b798f.appspot.com/o/tutorial3.png?alt=media&token=04ed6270-b4d3-4631-8f51-177a62ef3c06",
                    "https://firebasestorage.googleapis.com/v0/b/baebot-b798f.appspot.com/o/tutorial4.png?alt=media&token=c00fca16-f0ba-4fc6-805e-466e25b17468"
//        "drawable/tutorial1.png",
//        "drawable/tutorial2.png",
//            "drawable/tutorial3.png",
//            "drawable/tutorial4.png"


    };


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new ViewPageAdapter(TutorialActivity.this, images);
        viewPager.setAdapter(adapter);
    }

}
