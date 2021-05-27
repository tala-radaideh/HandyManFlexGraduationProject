package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.handymanflex.R;

import java.util.List;

import Database.CartItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import android.content.Context;
public class MyCartAdapter  extends RecyclerView.Adapter<MyCartAdapter.MyViewHolder>{
Context context;
List<CartItem> cartItemList;

    public MyCartAdapter(Context context, List<CartItem> cartItemList) {
        this.context = context;
        this.cartItemList = cartItemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_cart_item,parent,false));

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Glide.with(context).load(cartItemList.get(position).getServiceImage() )
                .into(holder.img_cart);

        holder.txt_service_name.setText(new StringBuilder(cartItemList.get(position).getServiceName()));

    }

    @Override
    public int getItemCount() {
        return cartItemList.size();
    }

    public CartItem getItemAtPosition(int pos) {
        return cartItemList.get(pos);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private Unbinder unbinder;
        @BindView(R.id.img_cartitem)
        ImageView img_cart;
        @BindView(R.id.txt_service_namebyitem)
        TextView txt_service_name;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder= ButterKnife.bind(this,itemView);
    }
}}
