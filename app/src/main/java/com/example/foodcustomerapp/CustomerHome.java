package com.example.foodcustomerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.foodcustomerapp.Model.FoodItemModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class CustomerHome extends AppCompatActivity {

    FloatingActionButton myFab;
    public static List<FoodItemModel> foodItemModels;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.navigation_home:
                    Intent home = new Intent(getApplicationContext(),CustomerHome.class);
                    home.addFlags(home.FLAG_ACTIVITY_CLEAR_TOP | home.FLAG_ACTIVITY_CLEAR_TASK |home.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(home);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    break;
                case R.id.navigation_cart:
                    Intent menu = new Intent(getApplicationContext(),Cart.class);
                    menu.addFlags(menu.FLAG_ACTIVITY_CLEAR_TOP | menu.FLAG_ACTIVITY_CLEAR_TASK |menu.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(menu);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    break;
                case R.id.navigation_zone:
                    Intent zone = new Intent(getApplicationContext(),Zone.class);
                    zone.addFlags(zone.FLAG_ACTIVITY_CLEAR_TOP | zone.FLAG_ACTIVITY_CLEAR_TASK |zone.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(zone);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    break;
            }
            return false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigation.setSelectedItemId(R.id.navigation_home);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if (foodItemModels==null){
            foodItemModels=new ArrayList<>();
        }


        myFab = (FloatingActionButton) findViewById(R.id.floating_button);
        myFab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(CustomerHome.this, Zone.class);
                i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK |i.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
    }
}
