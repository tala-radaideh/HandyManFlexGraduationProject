package com.example.handymanflexpartserver;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.handymanflexpartserver.Common.Common;
import com.example.handymanflexpartserver.Model.EventBus.CategoryClick;
import com.example.handymanflexpartserver.Model.EventBus.ChangeMenuClick;
import com.example.handymanflexpartserver.Model.EventBus.ToastEvent;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessaging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;
import io.paperdb.Paper;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private NavController navController;
    private int menuClick= -1;

    @Optional
    @OnClick(R.id.fab_chat)
    void onOpenChatList() {

        startActivity(new Intent(this,ChatListActivity.class));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        subscribeToTopic(Common.createTopicOrder());
        ButterKnife.bind(this);

// put update token here later
        checkStartTrip();


         drawer = findViewById(R.id.drawer_layout);
         navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_category,R.id.nav_service_list,
                R.id.nav_order)
                .setOpenableLayout(drawer)
                .build();
         navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
       navigationView.setNavigationItemSelectedListener(this);
       navigationView.bringToFront();


       View headerView = navigationView.getHeaderView(0);
        TextView txt_user = (TextView)headerView.findViewById(R.id.txt_user);
        Common.setSpanString("Hey ,",Common.currentServerUser.getName(),txt_user);

       menuClick = R.id.nav_category; // default
    }

    private void checkStartTrip() {
        Paper.init(this);
        if(!TextUtils.isEmpty(Paper.book().read(Common.TRIP_START)))
            startActivity(new Intent(this,Map.class));


    }

    private void subscribeToTopic(String topicOrder) {
        FirebaseMessaging.getInstance()
                .subscribeToTopic(topicOrder)
                .addOnFailureListener(e ->{
                    Toast.makeText(this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                })
                .addOnCompleteListener(task -> {
                    if(!task.isSuccessful())
                        Toast.makeText(this,"Failed: "+task.isSuccessful(),Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true ,threadMode = ThreadMode.MAIN)
    public void ponCategoryClick(CategoryClick event) {
        if (event.isSuccess()) {
            if (menuClick != R.id.nav_service_list) {
                navController.navigate(R.id.nav_service_list);
                menuClick = R.id.nav_service_list;
            }
        }
    }




    @Subscribe(sticky = true ,threadMode = ThreadMode.MAIN)
    public void onToastEvent(ToastEvent event) {
        if (event.getAction()== Common.ACTION.CREATE) {
            Toast.makeText(this,"Create success" , Toast.LENGTH_SHORT).show();

        }
        else if (event.getAction()== Common.ACTION.UPDATE) {
            Toast.makeText(this,"Update success" , Toast.LENGTH_SHORT).show();

        }
        else
        {
            Toast.makeText(this,"Delete success" , Toast.LENGTH_SHORT).show();
        }

        EventBus.getDefault().postSticky(new ChangeMenuClick(event.isFromServiceList()));
    }




    @Subscribe(sticky = true ,threadMode = ThreadMode.MAIN)
    public void onChangeMenuClick(ChangeMenuClick event)
    {
        if(event.isFromServiceList()) {

            //Clear
            navController.popBackStack(R.id.nav_category, true);
            navController.navigate(R.id.nav_category);
        }
        else
        {

            navController.popBackStack(R.id.nav_service_list, true);
            navController.navigate(R.id.nav_service_list);
        }
        menuClick = -1;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        item.setChecked(true);
        drawer.closeDrawers();
        switch (item.getItemId())
        {
            case R.id.nav_category:
                if(item.getItemId() !=menuClick)
                {
                    navController.popBackStack(); // Remove all back stack
                    navController.navigate(R.id.nav_category);
                }
                break;
            case R.id.nav_order:
                if(item.getItemId() !=menuClick)
                {
                    navController.popBackStack(); // Remove all back stack
                    navController.navigate(R.id.nav_order);
                }
                break;

            case R.id.nav_best_deals:
                if(item.getItemId() !=menuClick)
                {
                    navController.popBackStack();
                    navController.navigate(R.id.nav_best_deals);

                }
                break;

            case R.id.nav_most_popular:
                if(item.getItemId() !=menuClick)
                {
                    navController.popBackStack();
                    navController.navigate(R.id.nav_most_popular);

                }
                break;



            case R.id.nav_sign_out:
                signOut();
                break;
            default:
                menuClick= -1;
                break;
        }
        menuClick = item.getItemId();
        return true;
    }

    private void signOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sing out")
                .setMessage("Do you really want to sign out ? ")
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }) .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Common.selectedService= null;
                Common.categorySelected=null;
                Common.currentServerUser=null;

                FirebaseAuth.getInstance().signOut();
                Intent intent= new Intent(HomeActivity.this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
