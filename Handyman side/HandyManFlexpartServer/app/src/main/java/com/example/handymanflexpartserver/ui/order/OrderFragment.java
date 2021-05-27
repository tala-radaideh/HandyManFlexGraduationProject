package com.example.handymanflexpartserver.ui.order;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handymanflexpartserver.Common.Common;
import com.example.handymanflexpartserver.Common.MySwipeHelper;
import com.example.handymanflexpartserver.Model.EventBus.ChangeMenuClick;
import com.example.handymanflexpartserver.Model.EventBus.LoadOrderEvent;
import com.example.handymanflexpartserver.Model.FCMResponse;
import com.example.handymanflexpartserver.Model.FCMSendData;
import com.example.handymanflexpartserver.Model.OrderModel;
import com.example.handymanflexpartserver.Model.RequestedOrderModel;
import com.example.handymanflexpartserver.Model.TokenModel;
import com.example.handymanflexpartserver.R;
import com.example.handymanflexpartserver.adapter.MyOrderAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;


import remote.IFCMService;
import remote.RetrofitFCMClient;

public class OrderFragment extends Fragment {
    @BindView(R.id.recycler_order_item)
    RecyclerView recycler_order;

    @BindView(R.id.txt_order_filter)
    TextView txt_order_filter;

    private OrderViewModel orderViewModel;
    Unbinder unbinder;
    LayoutAnimationController layoutAnimationController;
    MyOrderAdapter adapter;
  //  ServerUserModel serverUserModel = Common.currentServerUser;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private IFCMService ifcmService;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        orderViewModel =
                new ViewModelProvider(this).get(OrderViewModel.class);
        View root = inflater.inflate(R.layout.fragment_order, container, false);
        unbinder = ButterKnife.bind(this, root); //17:56
        initViews();
        orderViewModel.getMessageError().observe(getViewLifecycleOwner(), s -> {
            Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
        });
        orderViewModel.getOrderModelMutableLiveData().observe(getViewLifecycleOwner(), (List<OrderModel> orderModels) -> {
            if (orderModels != null) {
                adapter = new MyOrderAdapter(getContext(), orderModels);
                recycler_order.setAdapter(adapter);
                recycler_order.setLayoutAnimation(layoutAnimationController);

                updateTextCounter();

            }
        });
        return root;
    }


    private void initViews() {

        ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);

        setHasOptionsMenu(true);

        recycler_order.setHasFixedSize(true);
        recycler_order.setLayoutManager(new LinearLayoutManager(getContext()));
        layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);

// i want to get the specific shpper


        MySwipeHelper mySwipeHelper = new MySwipeHelper(getContext(), recycler_order, 200) { // was 300
            @Override
            protected void instantiatmybutton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer) {
              //  buffer.add(new MyButton(getContext(), "Accept Order", 15, 0, Color.parseColor("#F6EF61") // was 30
                    //    , (int pos) -> {
//                    OrderModel orderModel = adapter.getItemAtPosition(pos);
//                    CreateOrder(serverUserModel, orderModel);


                buffer.add(new MyButton(getContext(), "Call", 50, 0, Color.parseColor("#6F9AE8")
                        , pos -> {

                    Dexter.withContext(getActivity())
                            .withPermission(Manifest.permission.CALL_PHONE)
                            .withListener(new PermissionListener() {
                                @Override
                                public void onPermissionGranted(PermissionGrantedResponse response) {
                                    //   OrderModel orderModel = adapter.getItemAtPosition(pos);

                                    OrderModel orderModel = adapter.getItemAtPosition(pos);

                                    Intent intent = new Intent();
                                    intent.setAction(Intent.ACTION_DIAL);

                                    intent.setData(Uri.parse(new StringBuilder("tel: ")
                                            .append(orderModel.getUserPhone()).toString()));
                                    startActivity(intent);

                                }

                                @Override
                                public void onPermissionDenied(PermissionDeniedResponse response) {
                                    Toast.makeText(getContext(), "You must accept " + response.getPermissionName(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                                }
                            }).check();  // Dont forget to call check()


                }));


                buffer.add(new MyButton(getContext(), "Edit", 50, 0, Color.parseColor("#F6EF61")
                        , pos -> {

                }));


            }


        };

    }
//
//    private void CreateOrder(ServerUserModel serverUserModel, OrderModel orderModel) {
//        RequestedOrder requestedOrder = new RequestedOrder();
//        requestedOrder.setHandyManName(serverUserModel.getName());
//        requestedOrder.setHandyManPhone(serverUserModel.getPhone());
//        requestedOrder.setOrderModel(orderModel);
//        requestedOrder.setStart(false);
//        requestedOrder.setCurrrentLng(-1.0);
//        requestedOrder.setCurrentLat(-1.0);
//        FirebaseDatabase.getInstance().getReference(Common.Order_Data).push().setValue(requestedOrder).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if(task.isSuccessful()){
//                    Toast.makeText(getContext(),"Done ",Toast.LENGTH_SHORT).show();
//                }
//
//            }
//        });
//    }







    private void updateTextCounter() {
        txt_order_filter.setText(new StringBuilder("Orders (")
                .append(adapter.getItemCount())
                .append(") "));
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.order_filter_menu, menu);
    }


    @Override
    public void onStart() {
        super.onStart();
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this);

    }


    @Override
    public void onStop() {
        if (EventBus.getDefault().hasSubscriberForEvent(LoadOrderEvent.class))
            EventBus.getDefault().removeStickyEvent(LoadOrderEvent.class);
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this);
        compositeDisposable.clear();
        super.onStop();

    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new ChangeMenuClick(true));
        super.onDestroy();
    }


    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onLoadOrderEvent(LoadOrderEvent event) {
        orderViewModel.loadOrderByStatus(event.getStatus());
    }
}

