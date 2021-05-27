package com.example.handymanflex;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.example.handymanflex.Common.Common;
import com.example.handymanflex.Model.CategoryModel;
import com.example.handymanflex.Model.ServiceModel;
import com.example.handymanflex.Model.UserModel;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import Database.CartDatabase;
import Database.LocalCartDataSource;
import Database.cartDataSource;
import EventBus.ServiceItemClick;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModel;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Database;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import EventBus.CategoryClick;
import EventBus.*;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    int menuClickId=-1;
    private DrawerLayout drawer;
    private NavController navController;
    private cartDataSource cartDataSource;
    AlertDialog dialog;

    private Place placeSelected;
    private AutocompleteSupportFragment places_fragment;
    private PlacesClient placesClient;
    private List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG);

    @BindView(R.id.fab)
    CounterFab fab;


    @Override
    protected void onResume() {
        super.onResume();
        //countCartItem();

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        initPlaceClient();


        dialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        ButterKnife.bind(this);
        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(this).cartDAO());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navController.navigate((R.id.nav_requested_services));

            }
        });
        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_menu, R.id.nav_services_list,R.id.nav_sign_out,
                R.id.nav_view_orders, R.id.nav_services_detail, R.id.nav_requested_services)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.bringToFront(); // the problem to demo between home and gallery is fixed !

        View headerView = navigationView.getHeaderView(0);
        TextView txt_user = (TextView) headerView.findViewById(R.id.txt_user);
        Common.setSpanString("Hello , ",Common.currentUser.getName(),txt_user);



    }

    private void initPlaceClient() {

        Places.initialize(this,getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);





    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        drawer.closeDrawers();
        // Here we Demo between home and gallery
        switch (item.getItemId()) {

            case R.id.nav_home:
                if(item.getItemId()!=menuClickId)
                navController.navigate(R.id.nav_home);
                break;
            case R.id.nav_menu:
                if(item.getItemId()!=menuClickId)
                navController.navigate((R.id.nav_menu));
                break;
            case R.id.nav_requested_services:
                if(item.getItemId()!=menuClickId)
                navController.navigate((R.id.nav_requested_services));
                break;
            case R.id.nav_view_orders:
                if(item.getItemId()!=menuClickId)
                    navController.navigate((R.id.nav_view_orders));
                break;
            case R.id.nav_sign_out:
                   signOut();
                break;
            case R.id.nav_update_info:
                ShowUpdateInfoDialog();
                break;

        }
        menuClickId=item.getItemId();
        return true;
    }

    private void ShowUpdateInfoDialog() {

        androidx.appcompat.app.AlertDialog.Builder builder= new androidx.appcompat.app.AlertDialog.Builder(this) ;
        builder.setTitle("Update Info");
        builder.setMessage("Please fill the below info ");


        View itemView = LayoutInflater.from(this).inflate(R.layout.layout_register, null);
        EditText edt_name = (EditText) itemView.findViewById(R.id.edt_name);
        TextView txt_address_details = (TextView) itemView.findViewById(R.id.txt_address_details);
        EditText edt_phone = (EditText) itemView.findViewById(R.id.edt_phone);

        places_fragment = (AutocompleteSupportFragment)getSupportFragmentManager()
                .findFragmentById(R.id.places_autocomplete_fragment);
        places_fragment.setPlaceFields(placeFields);
        places_fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                placeSelected = place;
                txt_address_details.setText(place.getAddress());
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(HomeActivity.this, ""+status.getStatusMessage(), Toast.LENGTH_SHORT).show();
            }
        });



        edt_name.setText(Common.currentUser.getName());
        txt_address_details.setText(Common.currentUser.getAddress());
        edt_phone.setText(Common.currentUser.getPhone());



       // builder.setView(itemView);
        builder.setNegativeButton("CANCEL", (dialogInterface, i) -> {
            dialogInterface.dismiss();

        });
        builder.setPositiveButton("UPDATE", (dialogInterface, i) -> {
          if(placeSelected !=null)
          {
              if (TextUtils.isEmpty(edt_name.getText().toString())) {
                  Toast.makeText(HomeActivity.this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
                  return;
              }
              Map<String,Object> update_data = new HashMap<>();
              update_data.put("name" ,edt_name.getText().toString());
              update_data.put("address" ,txt_address_details.getText().toString());
              update_data.put("lat",placeSelected.getLatLng().latitude);
              update_data.put("lng",placeSelected.getLatLng().longitude);

              FirebaseDatabase.getInstance()
                      .getReference(Common.USER_REFERENCES)
                      .child(Common.currentUser.getUid())
                      .updateChildren(update_data)
                      .addOnFailureListener(e -> {
                          dialogInterface.dismiss();
                          Toast.makeText(HomeActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                      }).addOnSuccessListener(aVoid -> {
                          dialogInterface.dismiss();
                          Toast.makeText(HomeActivity.this, "Info Updated Successfully", Toast.LENGTH_SHORT).show();
                          Common.currentUser.setName(update_data.get("name").toString());
                          Common.currentUser.setAddress(update_data.get("address").toString());
                          Common.currentUser.setLat(Double.parseDouble(update_data.get("lat").toString()) );
                          Common.currentUser.setLng(Double.parseDouble(update_data.get("name").toString()) );


              });


          }
          else
          {
              Toast.makeText(this, "Please Select Address", Toast.LENGTH_SHORT).show();
          }
        });
        builder.setView(itemView);


        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.setOnDismissListener(dialog1 -> {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.remove(places_fragment);
            fragmentTransaction.commit();
        });
        dialog.show();
    }


    private void signOut() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Sing out")
                .setMessage("Do you really want to sign out ? ")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                    }
                }) .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Common.selectedService= null;
                Common.categorySelected=null;
                Common.currentUser=null;

                FirebaseAuth.getInstance().signOut();
                Intent intent= new Intent(HomeActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        });
        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }
    //EventBus


    @Override
    protected void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }



    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);

        super.onStop();
    }




    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCategorySelected(CategoryClick event) {
        if (event.isSuccess()) {

            navController.navigate(R.id.nav_services_list);
        }
    }



    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onPopularCategoryClick(PopularCategoryClick event) {
        if (event.getPopularCategoryModel() != null) {
            dialog.show();
            FirebaseDatabase.getInstance()
                    .getReference("Category")
                    .child(event.getPopularCategoryModel().getMenu_id())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Common.categorySelected = snapshot.getValue(CategoryModel.class);
                                Common.categorySelected.setMenu_id(snapshot.getKey());


                                //load services
                                FirebaseDatabase.getInstance()
                                        .getReference("Category")
                                        .child(event.getPopularCategoryModel().getMenu_id())
                                        .child("services").orderByChild("id")
                                        .equalTo(event.getPopularCategoryModel().getServices_id())
                                        .limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                                                Common.selectedService = itemSnapshot.getValue(ServiceModel.class);
                                                Common.selectedService.setKey(itemSnapshot.getKey());

                                            }
navController.navigate(R.id.nav_services_detail);
                                        } else {

                                            Toast.makeText(HomeActivity.this, "Item Doesn't Exist", Toast.LENGTH_SHORT).show();
                                        }
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        dialog.dismiss();
                                        Toast.makeText(HomeActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                dialog.dismiss();
                                Toast.makeText(HomeActivity.this, "Item Doesn't Exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            dialog.dismiss();
                            Toast.makeText(HomeActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }




    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onPopularBestDealClick(BestDealsItemClick event) {
        if (event.getBestDealModel() != null) {
            dialog.show();
            FirebaseDatabase.getInstance()
                    .getReference("Category")
                    .child(event.getBestDealModel().getMenu_id())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                Common.categorySelected = snapshot.getValue(CategoryModel.class);
                                Common.categorySelected.setMenu_id(snapshot.getKey());
                                //load services
                                FirebaseDatabase.getInstance()
                                        .getReference("Category")
                                        .child(event.getBestDealModel().getMenu_id())
                                        .child("services").orderByChild("id")
                                        .equalTo(event.getBestDealModel().getServices_id())
                                        .limitToFirst(1).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                                                Common.selectedService = itemSnapshot.getValue(ServiceModel.class);
                                                Common.selectedService.setKey(itemSnapshot.getKey());

                                            }
                                            navController.navigate(R.id.nav_services_detail);
                                        } else {

                                            Toast.makeText(HomeActivity.this, "Item Doesn't Exist", Toast.LENGTH_SHORT).show();
                                        }
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        dialog.dismiss();
                                        Toast.makeText(HomeActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            } else {
                                dialog.dismiss();
                                Toast.makeText(HomeActivity.this, "Item Doesn't Exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            dialog.dismiss();
                            Toast.makeText(HomeActivity.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }





    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onServiceItemClick(ServiceItemClick event) {
        if (event.isSuccess()) {

            navController.navigate(R.id.nav_services_detail);
        }
    }



    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onCartClick(ServiceItemClick event) {
        if (event.isSuccess()) {
            countCartItem();

        }
    }






    @Subscribe(sticky=true,threadMode=ThreadMode.MAIN)
    public void onMenuItemBack(MenuItemBack event)
    {
        menuClickId=-1;
        if(getSupportFragmentManager().getBackStackEntryCount()>0)
            getSupportFragmentManager().popBackStack();
    }



    private void countCartItem() {
        cartDataSource.countItemCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Integer>() {
                    @Override
                    public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {
                        fab.setCount(integer);
                    }

                    @Override
                    public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                        if (!e.getMessage().contains("Query returned empty"))
                            Toast.makeText(HomeActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        else
                            fab.setCount(0);
                    }
                });

    }


}
