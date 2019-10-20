package com.example.foodcustomerapp.Adapter;

import android.content.Context;
import android.graphics.Color;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodcustomerapp.Model.FoodItemModel;
import com.example.foodcustomerapp.R;

import java.util.List;

public class FoodItemAdapter extends RecyclerView.Adapter<FoodItemAdapter.FoodViewHolder> {
    private Context context;
    private List<FoodItemModel> pInfolist;
    private final static int FADE_DURATION = 500;

    private OnAdapterItemClickListener mlistener;
    public interface OnAdapterItemClickListener{
        void OnItemClick(View v,int position);
    }

    public OnAdapterItemClickListener getMlistener() {
        return mlistener;
    }

    public void setMlistener(OnAdapterItemClickListener mlistener) {
        this.mlistener = mlistener;
    }
    public List<FoodItemModel> getCasetlist() {
        return pInfolist;
    }

    public void setCasetlist(List<FoodItemModel> foodlist) {
        this.pInfolist = foodlist;
    }

    public FoodItemAdapter(List<FoodItemModel> pmodels){
        this.pInfolist=pmodels;
    }

    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item_layout,parent,false);
        context=parent.getContext();
        return new FoodItemAdapter.FoodViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FoodViewHolder holder, final int position) {

        if(position %2 == 1)
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#f9f9f9"));
        }
        else
        {
            holder.itemView.setBackgroundColor(Color.parseColor("#e5e5e5"));
        }

        holder.foodName.setText(pInfolist.get(position).getItemName());
        holder.foodPrice.setText(pInfolist.get(position).getItemPrice()+" Tk");
        setFadeAnimation(holder.itemView);

    }

    @Override
    public int getItemCount() {
        return pInfolist.size();
    }

    public class FoodViewHolder extends RecyclerView.ViewHolder {

        TextView foodName,foodPrice;
        Button clear;

        public FoodViewHolder(View itemView) {
            super(itemView);
            clear=(Button)itemView.findViewById(R.id.btn_clear);
            foodName=(TextView)itemView.findViewById(R.id.foodName);
            foodPrice=(TextView)itemView.findViewById(R.id.foodPrice);
            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getMlistener().OnItemClick(view,getAdapterPosition());
                }
            });
        }
    }
    private void setFadeAnimation(View view) {
        ScaleAnimation anim = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }
}
