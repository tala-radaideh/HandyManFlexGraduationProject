package Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import Database.CartItem;
import Database.LocalCartDataSource;
import Database.cartDataSource;
import EventBus.ServiceItemClick;

import com.bumptech.glide.Glide;
import com.example.handymanflex.Common.Common;
import com.example.handymanflex.Model.ServiceModel;
import com.example.handymanflex.R;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import com.example.handymanflex.Callback.IRecyclerClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import Database.CartDatabase;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import EventBus.CounterCartEvent;

public class MyServiceListAdapter extends RecyclerView.Adapter<MyServiceListAdapter.MyViewHolder> {

    private Context context;
    private List<ServiceModel> serviceModelList;
    private CompositeDisposable compositeDisposable;
    private cartDataSource cartdataSource;


    public MyServiceListAdapter(Context context, List<ServiceModel> serviceModelList) {
        this.context = context;
        this.serviceModelList = serviceModelList;
        this.compositeDisposable = new CompositeDisposable();
        this.cartdataSource = new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).
                inflate(R.layout.layout_services_item, parent, false));
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
            EventBus.getDefault().postSticky(new ServiceItemClick(true, serviceModelList.get(pos)));
        });

        holder.image_cart.setOnClickListener(view -> {
            CartItem cartItem = new CartItem();
            cartItem.setUid(Common.currentUser.getUid());
            cartItem.setUserPhone(Common.currentUser.getPhone());
            cartItem.setServiceId(serviceModelList.get(position).getId());
            cartItem.setServiceName(serviceModelList.get(position).getName());
            cartItem.setServiceImage(serviceModelList.get(position).getImage());




            cartdataSource.getdetailItemInCart(Common.currentUser.getUid(),
                    cartItem.getUid()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<CartItem>() {
                @Override
                public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                }

                @Override
                public void onSuccess(@io.reactivex.annotations.NonNull CartItem cartItemfromDB) {
                    if (cartItemfromDB.equals(cartItem)) {
                        cartdataSource.updateCartItem(cartItemfromDB)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new SingleObserver<Integer>() {
                                    @Override
                                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                    }

                                    @Override
                                    public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {
                                        Toast.makeText(context, "Successfully Updated !", Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                    }

                                    @Override
                                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                        Toast.makeText(context, "[UPDATE CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        compositeDisposable.add(cartdataSource.insertOrReplaceAll(cartItem).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                            Toast.makeText(context, "Added to Bookmark !", Toast.LENGTH_SHORT).show();
                                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                        }, throwable -> {
                                            Toast.makeText(context, "[Cart Error]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                )

                        );
                    }
                }

                @Override
                public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                    if(e.getMessage().contains("empty")){
                        compositeDisposable.add(cartdataSource.insertOrReplaceAll(cartItem).subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(() -> {
                                            Toast.makeText(context, "Added To Bookmark", Toast.LENGTH_SHORT).show();
                                            EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                        }, throwable -> {
                                            Toast.makeText(context, "[Cart Error]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();

                                        }
                                )

                        );
                    }
                    else

                    Toast.makeText(context, "[UPDATE CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        });
    }


    @Override
    public int getItemCount() {
        return serviceModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Unbinder unbinder;
        @BindView(R.id.txt_service_name)
        TextView txt_service_name;
        @BindView(R.id.image_service_img)
        ImageView image_service_img;
        @BindView(R.id.img_fav)
        ImageView image_fav;
        @BindView(R.id.img_cart)
        ImageView image_cart;


        IRecyclerClickListener listener;

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

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

