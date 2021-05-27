package com.example.handymanflexpartserver.ui.services_list;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.handymanflexpartserver.Common.Common;
import com.example.handymanflexpartserver.Common.MySwipeHelper;
import com.example.handymanflexpartserver.Model.EventBus.ChangeMenuClick;
import com.example.handymanflexpartserver.Model.EventBus.ToastEvent;
import com.example.handymanflexpartserver.Model.ServiceModel;
import com.example.handymanflexpartserver.R;
import com.example.handymanflexpartserver.adapter.MyServiceListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observer;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class ServiceListFragment extends Fragment {


    private static final  int PICK_IMAGE_REQUEST = 1234;
    private ImageView img_service;
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private android.app.AlertDialog dialog;
     private Uri imageuri = null;
    private ServiceListViewModel serviceListViewModel;
    private List<ServiceModel> serviceModelList;

    Unbinder unbinder;
    @BindView(R.id.recycler_services_list)
    RecyclerView recycler_services_list;

    LayoutAnimationController layoutAnimationController;
    MyServiceListAdapter adapter;


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.service_list_menu , menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager)getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getActivity().getComponentName()));

        //Event
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                startSearchService(s);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        // clear text when click on clear button on search view
        ImageView closeButton = (ImageView)searchView.findViewById(R.id.search_close_btn);
        closeButton.setOnClickListener(v -> {
              EditText ed = (EditText) searchView.findViewById(R.id.search_src_text);

              //clear text
            ed.setText("");
            //clear query
            searchView.setQuery("",false);
            //collapse the action view
            searchView.onActionViewCollapsed();
            // collapse the search widget
            menuItem.collapseActionView();
            //restore results to original
            serviceListViewModel.getMutableLiveDataServiceList().setValue(Common.categorySelected.getServices());
        });
    }

    private void startSearchService(String s) {
        List<ServiceModel>  resultService = new ArrayList<>();

            for (int i =0 ; i <Common.categorySelected.getServices().size() ; i++) {
                ServiceModel serviceModel = Common.categorySelected.getServices().get(i);
                if (serviceModel.getName().toLowerCase().contains(s.toLowerCase()))
                {
                    serviceModel.setPositionInList(i); // save index
                    resultService.add(serviceModel);
                }
        }
            serviceListViewModel.getMutableLiveDataServiceList().setValue(resultService); // set search results
    }

    public View onCreateView(@NonNull LayoutInflater inflater ,
                             ViewGroup container , Bundle savedInstanceState) {
        serviceListViewModel =
                new ViewModelProvider(this).get(ServiceListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_food_list, container, false);

        unbinder = ButterKnife.bind(this, root);


        initViews();



        serviceListViewModel.getMutableLiveDataServiceList().observe(getViewLifecycleOwner(),serviceModels ->
        {
            if(serviceModels != null ) {
                serviceModelList = serviceModels;
                adapter = new MyServiceListAdapter(getContext(), serviceModelList);
                recycler_services_list.setAdapter(adapter);
                recycler_services_list.setLayoutAnimation(layoutAnimationController);

            }
        });


        return root;
    }


    private void initViews() {
        setHasOptionsMenu(true); // Enable menu from fragment

    dialog= new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
storage = FirebaseStorage.getInstance();
storageReference = storage.getReference();


            ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(Common.categorySelected.getName());
            recycler_services_list.setHasFixedSize(true);
            recycler_services_list.setLayoutManager(new LinearLayoutManager(getContext()));

            layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(), R.anim.layout_item_from_left);

        MySwipeHelper mySwipeHelper = new MySwipeHelper(getContext(),recycler_services_list,200) {
            @Override
            protected void instantiatmybutton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer) {
                buffer.add(new MyButton(getContext(),"Delete",50,0, Color.parseColor("#F6EF61")
                        ,pos -> {
                    if(serviceModelList != null)
                     Common.selectedService = serviceModelList.get(pos);
                    AlertDialog.Builder builder= new AlertDialog.Builder(getContext());
                    builder.setTitle("DELETE")
                            .setMessage("Are you sure you want to delete this service ? ")
                            .setNegativeButton("CANCEL", ((dialogInterface, i) -> dialogInterface.dismiss()))
                            .setPositiveButton("DELETE",((dialogInterface, i) -> {
                                ServiceModel serviceModel = adapter.getItemAtPosition(pos); // get item in adapter

                                if(serviceModel.getPositionInList()== -1)
                                    Common.categorySelected.getServices().remove(pos);
                                else
                                    Common.categorySelected.getServices().remove(serviceModel.getPositionInList());
                                    updateService(Common.categorySelected.getServices(), Common.ACTION.DELETE );

                            }));
                    AlertDialog deleteDialog = builder.create();
                    deleteDialog.show();
                }));




                buffer.add(new MyButton(getContext(),"Update",50,0, Color.parseColor("#6F9AE8")
                        ,pos -> {
                    // Similar
                    ServiceModel serviceModel = adapter.getItemAtPosition(pos);
                    if(serviceModel.getPositionInList()== -1)
                        showUpdateDialog(pos,serviceModel);
                    else

                        showUpdateDialog(serviceModel.getPositionInList(),serviceModel);



                }));





            }
        };

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()== R.id.action_create)
       showAddDialog();
        return super.onOptionsItemSelected(item);
    }

    private void showAddDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder= new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Create");
        builder.setMessage("Please fill the information");

        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_update_food,null);
        EditText edt_service_name= (EditText)itemView.findViewById(R.id.edt_service_name);
        EditText edt_service_description= (EditText)itemView.findViewById(R.id.edt_service_description);
        img_service = (ImageView)itemView.findViewById(R.id.image_service_img);

        // set data

        Glide.with(getContext()).load(R.drawable.ic_baseline_image_24).into(img_service);

        //set event
        img_service.setOnClickListener(v -> {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "select picture"), PICK_IMAGE_REQUEST);


        });

        builder.setNegativeButton("CANCEL",((dialogInterface, i) -> dialogInterface.dismiss()))
                .setPositiveButton("CREATE",((dialogInterface, i) -> {


                    ServiceModel updateService = new ServiceModel();
                    updateService.setName(edt_service_name.getText().toString());
                    updateService.setDescription(edt_service_description.getText().toString());

                    if(imageuri != null)
                    {
                        // have image

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
                                updateService.setImage(uri.toString());

                         if(Common.categorySelected.getServices() == null)
                         Common.categorySelected.setServices(new ArrayList<>());
                         Common.categorySelected.getServices().add(updateService);
                                updateService(Common.categorySelected.getServices(), Common.ACTION.CREATE);

                            });
                        }).addOnProgressListener(taskSnapShot->{
                            double progress =(100.0 * taskSnapShot.getBytesTransferred()/taskSnapShot.getTotalByteCount());
                            dialog.setMessage(new StringBuilder("Uploading").append(progress).append("%"));
                        });

                    }
                    else
                    {
                        if(Common.categorySelected.getServices() == null)
                            Common.categorySelected.setServices(new ArrayList<>());
                        Common.categorySelected.getServices().add(updateService);
                        updateService(Common.categorySelected.getServices(), Common.ACTION.CREATE);
                    }
                }));


        builder.setView(itemView);
        AlertDialog updateDialog = builder.create();
        updateDialog.show();
    }



    private void showUpdateDialog(int pos , ServiceModel serviceModel) {
        androidx.appcompat.app.AlertDialog.Builder builder= new androidx.appcompat.app.AlertDialog.Builder(getContext());
        builder.setTitle("Update");
        builder.setMessage("Please fill the information");

        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_update_food,null);
        EditText edt_service_name= (EditText)itemView.findViewById(R.id.edt_service_name);
        EditText edt_service_description= (EditText)itemView.findViewById(R.id.edt_service_description);
       img_service = (ImageView)itemView.findViewById(R.id.image_service_img);

       // set data
        edt_service_name.setText(new StringBuilder("")
        .append(serviceModel.getName()));

        edt_service_description.setText(new StringBuilder("")
                .append(serviceModel.getDescription()));


        Glide.with(getContext()).load(serviceModel.getImage()).into(img_service);

        //set event
        img_service.setOnClickListener(v -> {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "select picture"), PICK_IMAGE_REQUEST);


        });

builder.setNegativeButton("CANCEL",((dialogInterface, i) -> dialogInterface.dismiss()))
        .setPositiveButton("UPDATE",((dialogInterface, i) -> {
            ServiceModel updateService = serviceModel;
            updateService.setName(edt_service_name.getText().toString());
            updateService.setDescription(edt_service_description.getText().toString());

            if(imageuri != null)
            {
                // have image

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
                        updateService.setImage(uri.toString());


                        Common.categorySelected.getServices().set(pos,updateService);
                        updateService(Common.categorySelected.getServices(), Common.ACTION.UPDATE);

                    });
                }).addOnProgressListener(taskSnapShot->{
                    double progress =(100.0 * taskSnapShot.getBytesTransferred()/taskSnapShot.getTotalByteCount());
                    dialog.setMessage(new StringBuilder("Uploading").append(progress).append("%"));
                });

            }
            else
            {
                Common.categorySelected.getServices().set(pos,updateService);
                updateService(Common.categorySelected.getServices(), Common.ACTION.UPDATE);
            }
        }));


           builder.setView(itemView);
           AlertDialog updateDialog = builder.create();
           updateDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode== Activity.RESULT_OK){
            if(data!=null && data.getData()!=null){
                imageuri=data.getData();
                img_service.setImageURI(imageuri);
            }
        }
    }


    private void updateService(List<ServiceModel> services , Common.ACTION action) {

        Map<String,Object> updateData = new HashMap<>();
        updateData.put("services",services);

        FirebaseDatabase.getInstance()
                .getReference(Common.CATEGORY_REF)
                .child(Common.categorySelected.getMenu_id())
                .updateChildren(updateData)
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                })
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        serviceListViewModel.getMutableLiveDataServiceList();
                        EventBus.getDefault().postSticky(new ToastEvent(action,true));


                    }
                });
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new ChangeMenuClick(true));
        super.onDestroy();
    }
}