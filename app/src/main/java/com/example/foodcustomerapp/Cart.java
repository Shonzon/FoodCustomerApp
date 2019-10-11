package com.example.foodcustomerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.foodcustomerapp.Adapter.FoodItemAdapter;
import com.example.foodcustomerapp.Model.FoodItemModel;

import java.util.List;

public class Cart extends AppCompatActivity implements FoodItemAdapter.OnAdapterItemClickListener {

    private RecyclerView recyclerView;
    FoodItemAdapter foodItemAdapter;
    TextView total_taka;
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

        loadRecycleView(CustomerHome.foodItemModels);
        foodItemAdapter.setMlistener(this);
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
            total_taka.setText(getTotal_Taka(tlist));
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
        CustomerHome.foodItemModels.remove(position);
        total_taka.setText(getTotal_Taka(CustomerHome.foodItemModels));
        foodItemAdapter.notifyItemRemoved(position);
        foodItemAdapter.notifyItemRangeChanged(position, CustomerHome.foodItemModels.size());

    }
}
