package com.example.handymanflexpartserver.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.handymanflexpartserver.Common.Common;
import com.example.handymanflexpartserver.Map;
import com.example.handymanflexpartserver.Model.OrderModel;
import com.example.handymanflexpartserver.Model.RequestedOrderModel;
import com.example.handymanflexpartserver.Model.ServerUserModel;
import com.example.handymanflexpartserver.R;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyViewHolder>{
    Context context;
    List<OrderModel> orderModelList;
    SimpleDateFormat simpleDateFormat;
ServerUserModel serverUserModel = Common.currentServerUser;
    public MyOrderAdapter(Context context, List<OrderModel> orderModelList) {
        this.context = context;
        this.orderModelList = orderModelList;
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return  new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_order_item,parent,false));
    }


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        OrderModel orderModel = orderModelList.get(position);
        CreateOrder(serverUserModel, orderModel);
        Glide.with(context)
                .load(orderModelList.get(position).getCartItemList().get(0).getServiceImage())
                .into(holder.img_service_image);

        holder.txt_date.setText(new StringBuilder(simpleDateFormat.format(orderModelList.get(position).getCreatedate())));

        holder.txt_order_number.setText(orderModelList.get(position).getKey());
        Common.setSpanStringColor("Order date ",simpleDateFormat.format(orderModelList.get(position).getCreatedate()),
                holder.txt_order_number, Color.parseColor("#333639"));
//
//        Paper.book().write(Common.Order_Data,new Gson().toJson(orderModelList.get(position)));

        holder.accept_service.setOnClickListener(v -> {

            context.startActivity(new Intent(context, Map.class));
        });

    }



    @Override
    public int getItemCount() {
        return orderModelList.size();
    }

    public OrderModel getItemAtPosition(int pos) {
        return orderModelList.get(pos);

    }

    public void removeItem(int pos) {
        orderModelList.remove(pos);
    }

    private void CreateOrder(ServerUserModel serverUserModel, OrderModel orderModel) {
        RequestedOrderModel requestedOrder = new RequestedOrderModel();
        requestedOrder.setHandyManName(serverUserModel.getName());
        requestedOrder.setHandyManPhone(serverUserModel.getPhone());
        requestedOrder.setOrderModel(orderModel);
        requestedOrder.setStart(false);
        requestedOrder.setCurrrentLng(-1.0);
        requestedOrder.setCurrentLat(-1.0);
        FirebaseDatabase.getInstance().getReference(Common.Order_Data).push().setValue(requestedOrder);


    }
    public class MyViewHolder  extends RecyclerView.ViewHolder{
        private Unbinder unbinder;
        @BindView(R.id.image_service_img)
        ImageView img_service_image;
        @BindView(R.id.txt_date)
        TextView txt_date;
        @BindView(R.id.txt_order_number)
        TextView txt_order_number;
        @BindView(R.id.accept_service)
        MaterialButton accept_service;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder= ButterKnife.bind(this,itemView);
        }
    }


}
