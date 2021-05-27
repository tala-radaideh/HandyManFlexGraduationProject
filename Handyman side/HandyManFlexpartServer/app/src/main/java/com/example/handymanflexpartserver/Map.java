package com.example.handymanflexpartserver;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.example.handymanflexpartserver.Common.Common;
import com.example.handymanflexpartserver.Model.OrderModel;
import com.github.aakira.expandablelayout.ExpandableLayout;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import butterknife.BindView;

public class Map extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @BindView(R.id.homeowner_name)
    TextView homeowner_name;
    @BindView(R.id.homeowner_phone)
    TextView homeowner_phone;
    @BindView(R.id.order_address)
    TextView order_address;

    @BindView(R.id.expandable_layout)
    ExpandableLayout expandable_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        DatabaseReference orderdataRef = FirebaseDatabase.getInstance().getReference(Common.ORDER_REF);
        orderdataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.child("Orders").getChildren()) {
                    OrderModel order = snapshot.getValue(OrderModel.class);
                    String latitude = child.child("latitude").getValue().toString();
                    String longitude = child.child("longitude").getValue().toString();
                    double loclatitude = Double.parseDouble(latitude);
                    double loclongitude = Double.parseDouble(longitude);
                    LatLng cod = new LatLng(loclatitude, loclongitude);
                    googleMap.addMarker(new MarkerOptions().position(cod).title(""));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        LatLng origin = new LatLng(32.5357, 35.8657);
        LatLng destination = new LatLng(32.5576, 35.8393
        );

        mMap.addMarker(new MarkerOptions().position(origin).title("you"));
        mMap.addMarker(new MarkerOptions().position(destination).title("homeowner"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        openGoogleMap(this, 32.5576, 35.8393);


    }

    void openGoogleMap(Context context, double latitude, double longitude) {
        //WITHOUT PIN
//        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", restaurantLat, restaurantLng);

        //WITH PIN
        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?q=loc:%f,%f", latitude, longitude
        );
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        context.startActivity(intent);
    }


}