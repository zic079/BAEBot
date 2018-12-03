package com.sponge.baebot;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import com.sponge.baebot.ViewPageAdapter;

import java.io.File;

public class TutorialActivity extends AppCompatActivity {
    ViewPager viewPager;
    ViewPageAdapter adapter;

    private String[] images = {
            "https://firebasestorage.googleapis.com/v0/b/baebot-b798f.appspot.com/o/Welcome%20to%20BaeBot%20Swipe%20to%20see%20the%20features..png?alt=media&token=5d231b54-4c9c-4b40-a68f-29ac16bffecd",
            "https://firebasestorage.googleapis.com/v0/b/baebot-b798f.appspot.com/o/Group%205.png?alt=media&token=3d686b34-6ad7-49a9-8e7f-8c875fc7fbac",
                    "https://firebasestorage.googleapis.com/v0/b/baebot-b798f.appspot.com/o/Group%206.png?alt=media&token=8599afc0-d376-4547-aaa0-4a64c1320767",
                    "https://firebasestorage.googleapis.com/v0/b/baebot-b798f.appspot.com/o/Group%203.png?alt=media&token=d82a0bf9-34d1-4e4f-96a5-2f8292b93bb0",
                    "https://firebasestorage.googleapis.com/v0/b/baebot-b798f.appspot.com/o/Group%208.png?alt=media&token=18c8b930-0980-41fa-9ac7-9a62d547fc8c"

    };


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new ViewPageAdapter(TutorialActivity.this, images);
        viewPager.setAdapter(adapter);
    }

}
