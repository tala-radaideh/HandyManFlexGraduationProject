package com.example.handymanflex.ui.view_orders;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handymanflex.Common.Common;
import com.example.handymanflex.Common.MySwipeHelper;
import com.example.handymanflex.Model.OrderModel;
import com.example.handymanflex.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Adapter.MyOrdersAdapter;
import com.example.handymanflex.Callback.ILoadOrderCallbackListener;

import org.greenrobot.eventbus.EventBus;

import Database.CartItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ViewOrdersFragment extends Fragment implements ILoadOrderCallbackListener {

    private ViewOrdersViewModel viewOrdersViewModel;
    @BindView(R.id.recycler_orders)
    RecyclerView recycler_orders;
    AlertDialog dialog;

    private Unbinder unbinder;
    private ILoadOrderCallbackListener listener;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewOrdersViewModel =
                new ViewModelProvider(this).get(ViewOrdersViewModel.class);
        View root = inflater.inflate(R.layout.fragment_view_order,container,false);

        unbinder= ButterKnife.bind(this,root);

        initViews(root); 
        loadOrdersFromFirebase();
        viewOrdersViewModel.getMutableLiveDataOrderList().observe(getViewLifecycleOwner(),orderList -> {
            MyOrdersAdapter adapter = new MyOrdersAdapter(getContext(),orderList);
            recycler_orders.setAdapter(adapter);

        });
        return root;
    }


    private void loadOrdersFromFirebase() {
        List<OrderModel> orderList = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
                .orderByChild("userId")
                .equalTo(Common.currentUser.getUid())
                .limitToLast(100)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                      for(DataSnapshot orderSnapShot : dataSnapshot.getChildren())
                      {
                          OrderModel orderModel= orderSnapShot.getValue(OrderModel.class);
                          orderModel.setOrderNumber(orderSnapShot.getKey());
                          orderList.add(orderModel);
                      }
                      listener.onLoadOrderSuccess(orderList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseerror) {
                   listener.onLoadOrderFailed(databaseerror.getMessage());
                    }
                });
    }



    private void initViews(View root) {
        listener = this;

        dialog = new SpotsDialog.Builder().setCancelable(false).setContext(getContext()).build();

        recycler_orders.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler_orders.setLayoutManager(layoutManager);
        recycler_orders.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));



        MySwipeHelper mySwipeHelper = new MySwipeHelper(getContext(), recycler_orders, 200) {
            @Override
            protected void instantiatmybutton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer) {
                buffer.add(new MyButton(getContext(), "Cancel", 25, 0, Color.parseColor("#FF3C30"),
                        pos -> {
//                            Toast.makeText(getContext(),"Delete item", Toast.LENGTH_SHORT).show();
        OrderModel orderModel=((MyOrdersAdapter) recycler_orders.getAdapter() ).getItemAtPosition(pos);
        if(orderModel.getOrderStatus()== 0)
        {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
            builder.setTitle("Cancel Order")
                    .setMessage("Are you sure you want to cancel your order ? ")
                    .setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss())
                    .setPositiveButton("YES", (dialogInterface, i) -> {

                        Map<String,Object> update_data = new HashMap<>();
                        update_data.put("orderStatus",-1);
                        FirebaseDatabase.getInstance()
                                .getReference(Common.ORDER_REF)
                                .child(orderModel.getOrderNumber())
                                .updateChildren(update_data)
                                .addOnFailureListener(e -> Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show())
                              .addOnSuccessListener(aVoid -> {

                                  orderModel.setOrderStatus(-1);
                                  (  (MyOrdersAdapter) recycler_orders.getAdapter() ).setItemAtPosition(pos,orderModel);
                                  recycler_orders.getAdapter().notifyItemChanged(pos);
                                  Toast.makeText(getContext(), "Order is Cancelled ", Toast.LENGTH_SHORT).show();
                              });
                    });
            androidx.appcompat.app.AlertDialog dialog = builder.create();
            dialog.show();
        }

                        }));
            }
        };
    }

    @Override
    public void onLoadOrderSuccess(List<OrderModel> orderList) {

        dialog.dismiss();
        viewOrdersViewModel.setMutableLiveDataOrderList(orderList);
    }

    @Override
    public void onLoadOrderFailed(String message) {
        dialog.dismiss();
        Toast.makeText(getContext(), message,Toast.LENGTH_SHORT).show();
    }
}
