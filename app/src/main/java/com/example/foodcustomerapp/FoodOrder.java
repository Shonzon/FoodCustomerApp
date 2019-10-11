package com.example.foodcustomerapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.foodcustomerapp.Model.FoodItemModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class FoodOrder extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    String [][] foodItem={{"Polaw","20"},{"Plane Rice","10"},{"Mixed Vegetable","20"},{"Alu vaji","20"},{"Begun vaji","20"},
            {"Vendi vaji","20"},{"Shak","20"},{"Ilish","150"},{"Rui","80"},{"Shing","110"},{"Pabda","100"},
            {"Koi","60"},{"Telapia","60"},{"Soto Mach","100"},{"Chicken","80"},{"Beef","120"},{"Mutton","150"},
            {"Duck","130"},{"Alu vorta","15"},{"Begun","15"},{"Dim","15"},{"Sutki","15"},{"Masur Dal","20"},{"Mug Dal","20"},
            {"cholar Dal","20"},{"Maskolai Dal","20"}
    };

    TableLayout foodlayout;
    Button confirmButton;
    CheckBox[] checkBoxes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_order);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_custom);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        foodlayout=(TableLayout)findViewById(R.id.foodLayout);
        confirmButton=(Button)findViewById(R.id.button_confirm);
        checkBoxes=new CheckBox[foodItem.length];
        foodItemInitialize();



        foodItemInitialize();

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(FoodOrder.this,Cart.class);
                i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK |i.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
            }
        });
    }

    private void foodItemInitialize(){
        for (int i=0;i<foodItem.length;i++){
            switch (i){
                case 0:
                    foodlayout.addView(setTableRowText("Rice"));
                    break;
                case 2:
                    foodlayout.addView(setTableRowText("Vegetable"));
                    break;
                case 7:
                    foodlayout.addView(setTableRowText("Fish"));
                    break;
                case 14:
                    foodlayout.addView(setTableRowText("Meat"));
                    break;
                case 18:
                    foodlayout.addView(setTableRowText("Vorta"));
                    break;
                case 22:
                    foodlayout.addView(setTableRowText("Dal"));
                    break;
            }

            TableRow tableRow = new TableRow(getApplicationContext());
            TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.MATCH_PARENT);
            tableRow.setLayoutParams(layoutParams);

            checkBoxes[i]= new CheckBox(getApplicationContext());
            checkBoxes[i].setText(foodItem[i][0]);
            checkBoxes[i].setId(i);
            checkBoxes[i].setButtonTintList(ColorStateList.valueOf(Color.WHITE));
            checkBoxes[i].setTextColor(Color.WHITE);
            checkBoxes[i].setOnCheckedChangeListener(this);
            tableRow.addView(checkBoxes[i],0);

            TextView textView = new TextView(getApplicationContext());
            textView.setText(foodItem[i][1]+" Tk");
            textView.setPadding(50,0,0,0);
            textView.setTextColor(Color.WHITE);
            tableRow.addView(textView, 1);

            foodlayout.addView(tableRow);
        }

    }
    private TableRow setTableRowText(String name){
        TableRow tbrow = new TableRow(getApplicationContext());
        TableRow.LayoutParams lparams = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT);
        tbrow.setLayoutParams(lparams);

        TextView tv = new TextView(getApplicationContext());
        tv.setText(name);
        tv.setTextColor(Color.WHITE);
        tv.setTextSize(18);
        tv.setTypeface(null, Typeface.BOLD);
        tbrow.addView(tv);
        return tbrow;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(FoodOrder.this,CustomerHome.class);
        i.addFlags(i.FLAG_ACTIVITY_CLEAR_TOP | i.FLAG_ACTIVITY_CLEAR_TASK |i.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if(compoundButton.isChecked()){
            FoodItemModel food=new FoodItemModel();
            food.setItemid(compoundButton.getId());
            food.setItemName(compoundButton.getText().toString());
            food.setItemPrice(foodItem[compoundButton.getId()][1]);
            CustomerHome.foodItemModels.add(food);
        } else {
            if (CustomerHome.foodItemModels!=null){
                for (int i=0;i<CustomerHome.foodItemModels.size();i++){
                    if (CustomerHome.foodItemModels.get(i).getItemid()==compoundButton.getId()){
                        CustomerHome.foodItemModels.remove(CustomerHome.foodItemModels.get(i));
                    }
                }
            }
        }
    }
}
