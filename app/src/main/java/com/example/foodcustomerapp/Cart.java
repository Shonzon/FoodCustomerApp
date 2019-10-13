package com.example.foodcustomerapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.foodcustomerapp.Adapter.FoodItemAdapter;
import com.example.foodcustomerapp.Model.FoodItemModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class Cart extends AppCompatActivity implements FoodItemAdapter.OnAdapterItemClickListener {

    private RecyclerView recyclerView;
    FoodItemAdapter foodItemAdapter;
    TextView total_taka;
    TextView current_date;
    Button order_confirm;
    RadioGroup radioGroup;
    RadioButton radioButton;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    String zoneName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_custom);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        recyclerView = (RecyclerView) findViewById(R.id.recycle_view);
        total_taka = (TextView) findViewById(R.id.total_taka);
        current_date = (TextView) findViewById(R.id.current_date);
        order_confirm = (Button) findViewById(R.id.order_confirm);
        radioGroup = (RadioGroup) findViewById(R.id.time_Order_rad);
        mAuth = FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference();
        zoneName=getIntent().getStringExtra("zoneName");



        Calendar calendar = Calendar.getInstance();
        current_date.setText(new SimpleDateFormat("dd-MMM-yyyy").format(calendar.getTime()));

        if (FoodOrder.foodItemModels==null){
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Cart");
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    FoodOrder.foodItemModels=new ArrayList<>();
                    for (DataSnapshot ds:dataSnapshot.getChildren()){

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else {

        }

        loadRecycleView(FoodOrder.foodItemModels);
        foodItemAdapter.setMlistener(Cart.this);




        order_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (radioGroup.getCheckedRadioButtonId() == -1)
                {
                    // no radio buttons are checked
                }
                else
                {
                    if (FoodOrder.foodItemModels!=null){
                        radioButton = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());

                        for (int k=0;k<FoodOrder.foodItemModels.size();k++){
                            mDatabase.child("Orders")
                                    .child(zoneName)
                                    .child(current_date.getText().toString())
                                    .child(radioButton.getText().toString())
                                    .child(mAuth.getCurrentUser().getUid())
                                    .child("preOrders")
                                    .child(randomAlphaNumeric()).setValue(FoodOrder.foodItemModels.get(k));
                        }
                    }else {

                    }

                }

            }
        });

    }
    private void loadRecycleView(List<FoodItemModel> tlist){
        if(tlist != null){
            foodItemAdapter=new FoodItemAdapter(tlist);
            RecyclerView.LayoutManager rLayoutmanager=new LinearLayoutManager(getApplicationContext());
            recyclerView.setLayoutManager(rLayoutmanager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(foodItemAdapter);
            recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(),
                    DividerItemDecoration.VERTICAL));
            recyclerView.setItemAnimator(null);
            recyclerView.scrollToPosition(0);
            foodItemAdapter.notifyDataSetChanged();
            total_taka.setText(getTotal_Taka(tlist)+" Tk");
        }else {

        }
    }
    private String getTotal_Taka(List<FoodItemModel> tlist){
        int total=0;
        for (int i=0;i<tlist.size();i++){
            total+=Integer.parseInt(tlist.get(i).getItemPrice());
        }
        return String.valueOf(total);
    }

    @Override
    public void onBackPressed() {
        addToCart();
        Intent i = new Intent(Cart.this,CustomerHome.class);
        i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK |i.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }
    @Override
    public void OnItemClick(View v, int position) {
        removeAt(position);
    }
    public void removeAt(int position) {
        FoodOrder.foodItemModels.remove(position);
        total_taka.setText(getTotal_Taka(FoodOrder.foodItemModels)+" Tk");
        foodItemAdapter.notifyItemRemoved(position);
        foodItemAdapter.notifyItemRangeChanged(position, FoodOrder.foodItemModels.size());

    }

    public String randomAlphaNumeric() {
        int count=10;
        String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
    private void addToCart(){
        if (FoodOrder.foodItemModels.size()>0){
            for (int k=0;k<FoodOrder.foodItemModels.size();k++){
                mDatabase.child("Cart")
                        .child(zoneName)
                        .child(mAuth.getCurrentUser().getUid())
                        .child(randomAlphaNumeric()).setValue(FoodOrder.foodItemModels.get(k));
            }
        }
        FoodOrder.foodItemModels.clear();
    }

}
