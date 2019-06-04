package com.wohhie.www.myworksafe.view;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.wohhie.www.myworksafe.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NotificationActivity extends AppCompatActivity {


    // Array of strings for ListView Title
    String[] listviewTitle = new String[]{
            "Danger! Danger!", "Incident Happen!", "ListView Title 3", "ListView Title 4"
    };


    int[] listviewImage = new int[]{
            R.drawable.icon_notification, R.drawable.icon_notification, R.drawable.icon_notification, R.drawable.icon_notification
    };

    String[] listviewShortDescription = new String[]{
            "A snow storm with very strong winds Comming. ", "Major incident happen in sector - 1", "Android ListView Short Description", "Android ListView Short Description",
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        // attach this activity with butterknife
        //ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Notifications");
        toolbar.setTitleTextColor(Color.WHITE);

        List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();

        for (int i = 0; i < listviewTitle.length; i++) {
            HashMap<String, String> hm = new HashMap<String, String>();
            hm.put("listview_title", listviewTitle[i]);
            hm.put("listview_discription", listviewShortDescription[i]);
            hm.put("listview_image", Integer.toString(listviewImage[i]));
            aList.add(hm);
        }

        String[] from = {"listview_image", "listview_title", "listview_discription"};
        int[] to = {R.id.listview_image, R.id.listview_item_title, R.id.listview_item_short_description};

        SimpleAdapter simpleAdapter = new SimpleAdapter(getBaseContext(), aList, R.layout.notification_item_layout, from, to);
        ListView androidListView = (ListView) findViewById(R.id.list_view);
        androidListView.setAdapter(simpleAdapter);
    }
}
