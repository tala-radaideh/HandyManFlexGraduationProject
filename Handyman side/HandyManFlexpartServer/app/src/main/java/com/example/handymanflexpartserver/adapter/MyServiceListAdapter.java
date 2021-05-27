package com.example.handymanflexpartserver.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.handymanflexpartserver.Common.Common;
import com.example.handymanflexpartserver.Model.ServiceModel;
import com.example.handymanflexpartserver.R;
import com.example.handymanflexpartserver.callback.IRecyclerClickListener;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyServiceListAdapter extends RecyclerView.Adapter<MyServiceListAdapter.MyViewHolder> {

    private Context context;
    private List<ServiceModel> serviceModelList;


    public MyServiceListAdapter(Context context, List<ServiceModel> serviceModelList) {
        this.context = context;
        this.serviceModelList = serviceModelList;


    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.layout_service_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(serviceModelList.get(position)
                .getImage()).into(holder.image_service_img);
        holder.txt_service_name.setText(new StringBuilder("")
                .append(serviceModelList.get(position).getName()));

        // Event
        holder.setListener((view, pos) -> {
            Common.selectedService = serviceModelList.get(pos);
            Common.selectedService.setKey(String.valueOf(pos));

        });


    }


    @Override
    public int getItemCount() {
        return serviceModelList.size();
    }

    public ServiceModel getItemAtPosition(int pos)
    {
        return serviceModelList.get(pos);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Unbinder unbinder;
        @BindView(R.id.txt_service_name)
        TextView txt_service_name;
        @BindView(R.id.image_service_img)
        ImageView image_service_img;




        IRecyclerClickListener listener;




        public void setListener(IRecyclerClickListener listener) { this.listener = listener; }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClickListener(v, getAdapterPosition());
        }
    }
}

