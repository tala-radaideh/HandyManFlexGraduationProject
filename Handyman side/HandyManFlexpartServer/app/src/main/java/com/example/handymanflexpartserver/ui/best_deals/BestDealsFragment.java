package com.example.handymanflexpartserver.ui.best_deals;

import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.handymanflexpartserver.Common.Common;
import com.example.handymanflexpartserver.Common.MySwipeHelper;
import com.example.handymanflexpartserver.Model.BestDealsModel;
import com.example.handymanflexpartserver.Model.CategoryModel;
import com.example.handymanflexpartserver.Model.EventBus.ToastEvent;
import com.example.handymanflexpartserver.R;
import com.example.handymanflexpartserver.adapter.MyBestDealsAdapter;
import com.example.handymanflexpartserver.adapter.MyCategoriesAdapter;
import com.example.handymanflexpartserver.ui.category.CategoryViewModel;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class BestDealsFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1234;
    private BestDealsViewModel mViewModel;
    Unbinder unbinder;
    @BindView(R.id.recycler_best_deal)
    RecyclerView recycler_best_deals;
    AlertDialog dialog;
    LayoutAnimationController layoutAnimationController;
    MyBestDealsAdapter adapter;
    List<BestDealsModel> bestDealsModels;

    ImageView img_best_deals;
    private Uri imageuri= null;

    FirebaseStorage storage;
    StorageReference storageReference;




    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mViewModel =
                new  ViewModelProvider(this).get(BestDealsViewModel.class);
        View root = inflater.inflate(R.layout.best_deals_fragment, container, false);
        unbinder= ButterKnife.bind(this,root);
        initViews();
        mViewModel.getMessageError().observe(getViewLifecycleOwner(), s -> {
            Toast.makeText( getContext(),""+s,Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        mViewModel.getBestDealsListMutable().observe(getViewLifecycleOwner(),list -> {

            dialog.dismiss();
            bestDealsModels=list;
            adapter = new MyBestDealsAdapter(getContext(),bestDealsModels);
            recycler_best_deals.setAdapter(adapter);
            recycler_best_deals.setLayoutAnimation(layoutAnimationController);
        });
        
        return root;

    }

    private void initViews() {

        storage=FirebaseStorage.getInstance();
        storageReference=storage.getReference();


        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        //  dialog.show(); Remove it to fix loading show when resume  fragment
        layoutAnimationController= AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_item_from_left);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        // layoutManager.setOrientation(RecyclerView.VERTICAL);
        recycler_best_deals.setLayoutManager(layoutManager);
        recycler_best_deals.addItemDecoration(new DividerItemDecoration(getContext(),layoutManager.getOrientation()));

        MySwipeHelper mySwipeHelper = new MySwipeHelper(getContext(),recycler_best_deals,200) {
            @Override
            protected void instantiatmybutton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer) {

                buffer.add(new MyButton(getContext(),"Delete",50,0, Color.parseColor("#F6EF61")
                        ,pos -> {
                    Common.bestDealsSelected= bestDealsModels.get(pos);
                    showDeleteDialog();
                }));


                buffer.add(new MyButton(getContext(),"Update",50,0, Color.parseColor("#6F9AE8")
                        ,pos -> {
                    Common.bestDealsSelected= bestDealsModels.get(pos);
                    showUpdateDialog();
                }));

            }
        };
    }

    private void showDeleteDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder= new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete this ?");
        builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                      dialogInterface.dismiss();
            }
        }).setPositiveButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteBestDeals();
            }
        });

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteBestDeals() {
        FirebaseDatabase.getInstance()
                .getReference(Common.BEST_DEALS)
                .child(Common.bestDealsSelected.getKey())
                .removeValue()
                .addOnFailureListener(e -> Toast.makeText(getContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    mViewModel.loadBestDeals();

                    EventBus.getDefault().postSticky(new ToastEvent(Common.ACTION.DELETE,true) );

                });
    }

    private void showUpdateDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder= new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Update");
        builder.setMessage("Please fill the information");


        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_update_category,null);
        EditText edt_category_name= (EditText) itemView.findViewById(R.id.edt_category_name);
        img_best_deals = (ImageView) itemView.findViewById(R.id.img_category_edt);


        edt_category_name.setText(new StringBuilder("").append(Common.bestDealsSelected.getName()));
        Glide.with(getContext()).load(Common.bestDealsSelected.getImage()).into(img_best_deals);

        img_best_deals.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "select picture"), PICK_IMAGE_REQUEST);


        });


        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.setPositiveButton("Update", (dialogInterface, i) -> {


            Map<String,Object> updateData= new HashMap<>();
            updateData.put("name",edt_category_name.getText().toString());

            if(imageuri!=null){
                dialog.setMessage("Uploading");
                dialog.show();
                String unique_name = UUID.randomUUID().toString();
                StorageReference imageFolder = storageReference.child("images/"+unique_name);


                imageFolder.putFile(imageuri)
                        .addOnFailureListener(e -> {
                            dialog.dismiss();
                            Toast.makeText(getContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }).addOnCompleteListener(task -> {
                    dialog.dismiss();
                    imageFolder.getDownloadUrl().addOnSuccessListener(uri -> {
                        updateData.put("image",uri.toString());
                        updateBestDeals(updateData);
                    });
                }).addOnProgressListener(taskSnapShot->{
                    double progress =(100.0 * taskSnapShot.getBytesTransferred()/taskSnapShot.getTotalByteCount());
                    dialog.setMessage(new StringBuilder("Uploading").append(progress).append("%"));
                });
            }
            else
            {
                updateBestDeals(updateData);
            }
        });
        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateBestDeals(Map<String, Object> updateData) {
        FirebaseDatabase.getInstance()
                .getReference(Common.BEST_DEALS)
                .child(Common.bestDealsSelected.getKey())
                .updateChildren(updateData)
                .addOnFailureListener(e -> Toast.makeText(getContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    mViewModel.loadBestDeals();

                    EventBus.getDefault().postSticky(new ToastEvent(Common.ACTION.UPDATE,true));

                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode== Activity.RESULT_OK){
            if(data!=null && data.getData()!=null){
                imageuri=data.getData();
                img_best_deals.setImageURI(imageuri);
            }
        }
    }

}