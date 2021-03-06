package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.FtsOptions;

import com.bumptech.glide.Glide;
import com.example.handymanflex.Common.Common;
import com.example.handymanflex.Model.OrderModel;
import com.example.handymanflex.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.MyViewHolder> {

   private Context context;
   private List<OrderModel> ordersList;
   private Calendar calendar;
   private SimpleDateFormat simpleDateFormat;


    public MyOrdersAdapter(Context context, List<OrderModel> ordersList) {
        this.context = context;
        this.ordersList = ordersList;
        calendar = Calendar.getInstance();
        simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
    }

    public OrderModel getItemAtPosition(int pos)
    {
        return ordersList.get(pos);
    }



    public void setItemAtPosition(int pos , OrderModel item)
    {
        ordersList.set(pos, item);
    }





    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context)
        .inflate(R.layout.layout_order_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(ordersList.get(position).getCartItemList().get(0).getServiceImage())
                .into(holder.img_order);
        calendar.setTimeInMillis(ordersList.get(position).getCreateDate() );
        Date date = new Date(ordersList.get(position).getCreateDate() );
        holder.txt_order_date.setText(new StringBuilder(Common.getDateOfWeek(calendar.get(Calendar.DAY_OF_WEEK) ))
          .append(" ")
                .append(simpleDateFormat.format(date)) );

        holder.txt_order_number.setText(new StringBuilder("Order NO : ").append(ordersList.get(position).getOrderNumber() ));




    }



    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{


        @BindView(R.id.txt_order_number)
        TextView txt_order_number;

        @BindView(R.id.txt_order_date)
        TextView txt_order_date;

        @BindView(R.id.img_order)
        ImageView img_order;


        Unbinder unbinder;
        public MyViewHolder(@NonNull View itemView) {

            super(itemView);
            unbinder= ButterKnife.bind(this,itemView);
        }
    }
}
