package com.example.handymanflexpartserver.ui.category;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.handymanflexpartserver.Common.Common;
import com.example.handymanflexpartserver.Common.MySwipeHelper;
import com.example.handymanflexpartserver.Common.SpacesItemDecoration;
import com.example.handymanflexpartserver.Model.CategoryModel;
import com.example.handymanflexpartserver.Model.EventBus.ToastEvent;
import com.example.handymanflexpartserver.R;
import com.example.handymanflexpartserver.adapter.MyCategoriesAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class CategoryFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1234;
    private CategoryViewModel categoryViewModel;

    Unbinder unbinder;
    @BindView(R.id.recycler_menu)
    RecyclerView recycler_menu;
    AlertDialog dialog;
    LayoutAnimationController layoutAnimationController;
    MyCategoriesAdapter adapter;


    List<CategoryModel> categoryModels;
    ImageView imagecategory;
     private Uri imageuri= null;

    FirebaseStorage storage;
    StorageReference storageReference;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        categoryViewModel =
                new  ViewModelProvider(this).get(CategoryViewModel.class);
        View root = inflater.inflate(R.layout.fragment_category, container, false);
        unbinder= ButterKnife.bind(this,root);
        initViews();
        categoryViewModel.getMessageError().observe(getViewLifecycleOwner(), s -> {
            Toast.makeText( getContext(),""+s,Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        categoryViewModel.getCategoryListMultable().observe(getViewLifecycleOwner(),categoryModelList -> {

            dialog.dismiss();
            categoryModels=categoryModelList;
            adapter = new MyCategoriesAdapter(getContext(),categoryModelList);
            recycler_menu.setAdapter(adapter);
            recycler_menu.setLayoutAnimation(layoutAnimationController);
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
        recycler_menu.setLayoutManager(layoutManager);
        recycler_menu.addItemDecoration(new DividerItemDecoration(getContext(),layoutManager.getOrientation()));


        MySwipeHelper mySwipeHelper = new MySwipeHelper(getContext(),recycler_menu,200) {
            @Override
            protected void instantiatmybutton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer) {


                buffer.add(new MyButton(getContext(),"Delete",50,0, Color.parseColor("#F6EF61")
                        ,pos -> {
                    Common.categorySelected= categoryModels.get(pos);
                    showDeleteDialog();
                }));




                buffer.add(new MyButton(getContext(),"Update",50,0, Color.parseColor("#6F9AE8")
                     ,pos -> {
                      Common.categorySelected= categoryModels.get(pos);
                      showUpdateDialog();
}));
            }
        };

          setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.action_bar_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.action_create)
        {
            showAddDialog();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder= new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete this category ?");
        builder.setNegativeButton("CANCEL",(dialog, which) ->
                dialog.dismiss() );
        builder.setPositiveButton("DELETE",( (dialog, which) ->
                deleteCategory()) );

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteCategory() {
        FirebaseDatabase.getInstance()
                .getReference(Common.CATEGORY_REF)
                .child(Common.categorySelected.getMenu_id())
                .removeValue()
                .addOnFailureListener(e -> Toast.makeText(getContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    categoryViewModel.loadCategories();

                    EventBus.getDefault().postSticky(new ToastEvent(Common.ACTION.DELETE,false));

                });


    }

    private void showUpdateDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder= new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Update");
        builder.setMessage("Please fill the information");


        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_update_category,null);
        EditText edt_category_name= (EditText) itemView.findViewById(R.id.edt_category_name);
        imagecategory = (ImageView) itemView.findViewById(R.id.img_category_edt);


        edt_category_name.setText(new StringBuilder("").append(Common.categorySelected.getName()));
        Glide.with(getContext()).load(Common.categorySelected.getImage()).into(imagecategory);

        imagecategory.setOnClickListener(view -> {
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
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
            dialog.dismiss();
            imageFolder.getDownloadUrl().addOnSuccessListener(uri -> {
                updateData.put("image", uri.toString());
                CategoryFragment.this.updateCategory(updateData);
            });
        }
    }).addOnProgressListener(taskSnapShot->{
                double progress =(100.0 * taskSnapShot.getBytesTransferred()/taskSnapShot.getTotalByteCount());
              dialog.setMessage(new StringBuilder("Uploading").append(progress).append("%"));
    });
}
else
{
    updateCategory(updateData);
}
        });
builder.setView(itemView);
androidx.appcompat.app.AlertDialog dialog = builder.create();
dialog.show();
    }

    private void updateCategory(Map<String, Object> updateData) {
        FirebaseDatabase.getInstance()
                .getReference(Common.CATEGORY_REF)
                .child(Common.categorySelected.getMenu_id())
                .updateChildren(updateData)
                .addOnFailureListener(e -> Toast.makeText(getContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    categoryViewModel.loadCategories();

                    EventBus.getDefault().postSticky(new ToastEvent(Common.ACTION.UPDATE,false));

                });
    }

    private void showAddDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder= new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Create");
        builder.setMessage("Please fill the information");


        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_update_category,null);
        EditText edt_category_name= (EditText) itemView.findViewById(R.id.edt_category_name);
        imagecategory = (ImageView) itemView.findViewById(R.id.img_category_edt);


        Glide.with(getContext()).load(R.drawable.ic_baseline_image_24).into(imagecategory);

        imagecategory.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "select picture"), PICK_IMAGE_REQUEST);


        });


        builder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
        builder.setPositiveButton("Create", (dialogInterface, i) -> {





            CategoryModel categoryModel= new CategoryModel();
            categoryModel.setName(edt_category_name.getText().toString());
            categoryModel.setServices(new ArrayList<>());


            if(imageuri!=null){
                dialog.setMessage("Uploading");
                dialog.show();
                String unique_name = UUID.randomUUID().toString();
                StorageReference imageFolder = storageReference.child("images/"+unique_name);


                imageFolder.putFile(imageuri)
                        .addOnFailureListener(e -> {
                            dialog.dismiss();
                            Toast.makeText(getContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        dialog.dismiss();
                        imageFolder.getDownloadUrl().addOnSuccessListener(uri -> {

                            categoryModel.setImage((uri.toString()));
                            addCategory(categoryModel);
                            //CategoryFragment.this.updateCategory(categoryModel);
                        });
                    }
                }).addOnProgressListener(taskSnapShot->{
                    double progress =(100.0 * taskSnapShot.getBytesTransferred()/taskSnapShot.getTotalByteCount());
                    dialog.setMessage(new StringBuilder("Uploading").append(progress).append("%"));
                });
            }
            else
            {
                addCategory(categoryModel);
            }
        });
        builder.setView(itemView);
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }


    private void addCategory(CategoryModel categoryModel) {
        FirebaseDatabase.getInstance()
                .getReference(Common.CATEGORY_REF)
                .push()
                .setValue(categoryModel)
                .addOnFailureListener(e -> Toast.makeText(getContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show())
                .addOnCompleteListener(task -> {
                    categoryViewModel.loadCategories();

                    EventBus.getDefault().postSticky(new ToastEvent(Common.ACTION.CREATE,false));

                });
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode== Activity.RESULT_OK){
            if(data!=null && data.getData()!=null){
                imageuri=data.getData();
                imagecategory.setImageURI(imageuri);
            }
        }
    }
}