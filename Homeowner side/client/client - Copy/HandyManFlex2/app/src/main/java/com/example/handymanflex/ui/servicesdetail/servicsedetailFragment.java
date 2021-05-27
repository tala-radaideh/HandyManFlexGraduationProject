package com.example.handymanflex.ui.servicesdetail;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.example.handymanflex.ChatActivity;
import com.example.handymanflex.Common.Common;
import com.example.handymanflex.Model.CommentModel;
import com.example.handymanflex.Model.ServiceModel;
import com.example.handymanflex.ui.comments.CommentFragment;
import com.example.handymanflex.ui.servicesdetail.servicesdetailViewModel;
import com.example.handymanflex.R;
import com.example.handymanflex.ui.Serviceslist.serviceslistViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import EventBus.CounterCartEvent;
import Database.CartDatabase;
import Database.CartItem;
import Database.LocalCartDataSource;
import Database.cartDataSource;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import EventBus.MenuItemBack;
public class servicsedetailFragment extends Fragment {
    private servicesdetailViewModel servicesdetailViewModel;

    private AlertDialog waitingdialog;
    private Unbinder unbinder;
    private cartDataSource cartdataSource;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @BindView(R.id.img_service_detail)
    ImageView serviceimage;
    @BindView(R.id.btnCart)
    CounterFab btncart;
    @BindView(R.id.rating_btn)
    FloatingActionButton ratingbtn;
    @BindView(R.id.service_name_detail)
    TextView servicename;
    @BindView(R.id.service_descreption)
    TextView servicedes;
    @BindView(R.id.rating_bar)
    RatingBar ratingbar;
    @BindView(R.id.btnshowcomment)
    Button showcomment;

    @BindView(R.id.fab_chat)
    CounterFab fab_chat;



    @OnClick(R.id.fab_chat)
    void onFabChatClick() {
        startActivity(new Intent(getContext(), ChatActivity.class));
    }

    @OnClick(R.id.btnCart)
    void onCartItemAdd() {
        CartItem cartItem = new CartItem();
        cartItem.setUid(Common.currentUser.getUid());
        cartItem.setUserPhone(Common.currentUser.getPhone());

        cartItem.setServiceId(Common.selectedService.getId());
        cartItem.setServiceName(Common.selectedService.getName());
        cartItem.setServiceImage(Common.selectedService.getImage());

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
                                    Toast.makeText(getContext(), "Update Success", Toast.LENGTH_SHORT).show();
                                    EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                }

                                @Override
                                public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                    Toast.makeText(getContext(), "[UPDATE CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                } else {
                    compositeDisposable.add(cartdataSource.insertOrReplaceAll(cartItem).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                        Toast.makeText(getContext(), "Saved to Bookmark !", Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                    }, throwable -> {
                                        Toast.makeText(getContext(), "[Cart Error]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                            )

                    );
                }
            }

            @Override
            public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                if (e.getMessage().contains("empty")) {
                    compositeDisposable.add(cartdataSource.insertOrReplaceAll(cartItem).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                        Toast.makeText(getContext(), "Added Successfully", Toast.LENGTH_SHORT).show();
                                        EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                    }, throwable -> {
                                        Toast.makeText(getContext(), "[Cart Error]" + throwable.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                            )

                    );
                } else

                    Toast.makeText(getContext(), "[UPDATE CART]" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }


    @OnClick(R.id.rating_btn)
    void onRatingButtonClick() {
        showRatingDialog();
    }

    @OnClick(R.id.btnshowcomment)
    void OnShowComment() {
        CommentFragment commentFragment = CommentFragment.getInstance();
        commentFragment.show(getActivity().getSupportFragmentManager(), "CommentFragment");
    }

    public servicsedetailFragment() {
    }

    private void showRatingDialog() {

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Rating Service");
        builder.setMessage("Please fill the below info ");


        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_rating, null);
        RatingBar ratingBar = (RatingBar) itemView.findViewById(R.id.rating_bar);
        EditText editcomment = (EditText) itemView.findViewById(R.id.edt_comment);

        builder.setView(itemView);
        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> {
            dialogInterface.dismiss();
        });
        builder.setPositiveButton("OK", (dialogInterface, i) -> {

            CommentModel commentModel = new CommentModel();
            commentModel.setName(Common.currentUser.getName());
            commentModel.setUid(Common.currentUser.getUid());
            commentModel.setComment(editcomment.getText().toString());
            commentModel.setRatingValue(ratingBar.getRating());
            Map<String, Object> serverTimeStamp = new HashMap<>();
            serverTimeStamp.put("timestamp", ServerValue.TIMESTAMP);
            commentModel.setCommentTimeStamp(serverTimeStamp);

            servicesdetailViewModel.setcommentModel(commentModel);


        });
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }


    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // ServicesdetailViewModel = ViewModelProviders.of(this).get(servicesdetailViewModel.class);
        servicesdetailViewModel =
                new ViewModelProvider(requireActivity()).get(servicesdetailViewModel.class);

        View root = inflater.inflate(R.layout.fragment_servicedetail, container, false);
        unbinder = ButterKnife.bind(this, root);
        initViews();
        servicesdetailViewModel.getMutableLiveDataservicedetail().observe(getViewLifecycleOwner(), serviceModel -> {
            displayService(serviceModel);
        });

        servicesdetailViewModel.getMutableLiveDatascomment().observe(getViewLifecycleOwner(), commentModel -> {
            sumbitRatingFireBase(commentModel);

        });
        return root;

    }

    private void initViews() {
        cartdataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());
        waitingdialog = new SpotsDialog.Builder().setCancelable(false).setContext(getContext()).build();



    }

    private void sumbitRatingFireBase(CommentModel commentModel) {
        waitingdialog.show();
        FirebaseDatabase.getInstance()
                .getReference(Common.COMMENT_REF)
                .child(Common.selectedService.getId())
                .push()
                .setValue(commentModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        addratingtoservice(commentModel.getRatingValue());
                    }
                    waitingdialog.dismiss();
                });
    }

    private void addratingtoservice(float ratingValue) {
        FirebaseDatabase.getInstance()
                .getReference(Common.CATEGORY_REF)
                .child(Common.categorySelected.getMenu_id())
                .child("services")
                .child(Common.selectedService.getKey())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            ServiceModel serviceModel = snapshot.getValue(ServiceModel.class);
                            serviceModel.setKey(Common.selectedService.getKey());


                            if (serviceModel.getRatingValue() == null)
                                serviceModel.setRatingValue(0d); // d lower case
                            if (serviceModel.getRatingCount() == 0)
                                serviceModel.setRatingCount(01); // 1 =lower case
                            double sumRating = serviceModel.getRatingValue() + ratingValue;
                            long ratingCount = serviceModel.getRatingCount() + 1;
                            double result = sumRating / ratingCount;


                            Map<String, Object> updateData = new HashMap<>();
                            updateData.put("ratingValue", sumRating);
                            updateData.put("ratingCount", ratingCount);


                            serviceModel.setRatingValue(sumRating);
                            serviceModel.setRatingCount((int) ratingCount);

                            snapshot.getRef().updateChildren(updateData).addOnCompleteListener(task -> {
                                waitingdialog.dismiss();
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Thank You!", Toast.LENGTH_SHORT).show();
                                    Common.selectedService = serviceModel;
                                    servicesdetailViewModel.setServiceModel(serviceModel);
                                }
                            });
                        } else {
                            waitingdialog.dismiss();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        waitingdialog.dismiss();
                        Toast.makeText(getContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }


    private void displayService(ServiceModel serviceModel) {
        Glide.with(getContext()).load(serviceModel.getImage()).into(serviceimage);
        servicename.setText(new StringBuilder(serviceModel.getName()));
        servicedes.setText(new StringBuilder(serviceModel.getDescription()));

        if (serviceModel.getRatingValue() != null)
            ratingbar.setRating(serviceModel.getRatingValue().floatValue() / serviceModel.getRatingCount());
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(Common.selectedService.getName());
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
    @Override
    public void onDestroy()
    {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
    }

}