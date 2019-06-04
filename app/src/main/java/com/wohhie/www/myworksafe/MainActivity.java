package com.wohhie.www.myworksafe;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.wohhie.www.myworksafe.adapter.GridViewAdapter;
import com.wohhie.www.myworksafe.firebase.FirebaseInstanceService;
import com.wohhie.www.myworksafe.model.Item;
import com.wohhie.www.myworksafe.view.NotificationActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private GridView homeGridView;
    private GridViewAdapter gridViewAdapter;
    private ArrayList<Item> items;

    private int[] images = new int[]{
            R.drawable.icon_emergency,
            R.drawable.icon_reports,
            R.drawable.icon_notification,
    };

    private String[] titles = new String[]{
            "Emergency",
            "Reports",
            "Notifications",
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // attach this activity with butterknife
        //ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");

        TextView mTitle = findViewById(R.id.toolbar_title);

        setSupportActionBar(toolbar);
        mTitle.setText("INSTANT REPORT");
        getSupportActionBar().setDisplayShowTitleEnabled(false);



        // intialize
        homeGridView = findViewById(R.id.homeGridView);
        // populate items in gridview
        items = populateList();
        gridViewAdapter = new GridViewAdapter(getApplicationContext(), items);
        homeGridView.setAdapter(gridViewAdapter);

        homeGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent;

                switch (position){
                    //TODO emergency contact
                    case 0:
                        intent = new Intent(getApplicationContext(), EmergencyActivity.class);
                        break;


                    //TODO  support / report
                    case 1:
                        intent = new Intent(getApplicationContext(), ReportIncidentActivity.class);
                        break;

                    //TODO  Notifications
                    case 2:
                        intent = new Intent(getApplicationContext(), NotificationActivity.class);
                        break;

                    default:
                        intent = new Intent(getApplicationContext(), EmergencyActivity.class);
                        break;

                }

                startActivity(intent);
            }
        });





    }

    private void showMessage(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private ArrayList<Item> populateList(){
        ArrayList<Item> list = new ArrayList<>();
        for (int i = 0; i < images.length; i++){
            Item item = new Item();
            item.setImage(images[i]);
            item.setTitle(titles[i]);
            list.add(item);
        }

        return list;
    }
}
