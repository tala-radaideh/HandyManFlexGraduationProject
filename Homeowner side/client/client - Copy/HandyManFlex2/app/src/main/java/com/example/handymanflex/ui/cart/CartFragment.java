package com.example.handymanflex.ui.cart;



import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.handymanflex.Common.Common;
import com.example.handymanflex.Common.MySwipeHelper;
import com.example.handymanflex.HomeActivity;
import com.example.handymanflex.Model.OrderModel;
import com.example.handymanflex.R;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import EventBus.MenuItemBack;
import Adapter.MyCartAdapter;
import com.example.handymanflex.Callback.ILoadTimeFromFirebaseListener;
import Database.CartDatabase;
import Database.CartItem;
import Database.LocalCartDataSource;
import EventBus.CounterCartEvent;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class CartFragment extends Fragment implements ILoadTimeFromFirebaseListener {
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location currentLocation;
    private CartViewModel cartViewModel;
    private Parcelable recyclerViewState;
    private MyCartAdapter adapter;
    private Database.cartDataSource cartDataSource;


    private Place placeSelected;
    private AutocompleteSupportFragment places_fragment;
    private PlacesClient placesClient;
    private List<Place.Field> placeFields = Arrays.asList(Place.Field.ID,
            Place.Field.NAME,
            Place.Field.ADDRESS,
            Place.Field.LAT_LNG);

    List<CartItem> cc;
    @BindView(R.id.recycler_requested_services)
    RecyclerView recycler_requested_services;
    @BindView(R.id.group_place_holder)
    CardView group_place_holder;
    @BindView(R.id.txt_empty_cart)
    TextView txt_empty_cart;


    private Unbinder unbinder;
    private LocationServices LocationService;
    ILoadTimeFromFirebaseListener listener;



    @OnClick(R.id.btn_place_order)
    void onPlaceOrderClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("One more step!");


        View view = LayoutInflater.from(getContext()).inflate(R.layout.layout_place_order, null);


        TextView txt_address = (TextView) view.findViewById(R.id.txt_address_details);
        RadioButton rdi_home_address = (RadioButton) view.findViewById(R.id.rdi_home_address);
        RadioButton rdi_other_address = (RadioButton) view.findViewById(R.id.rdi_other_address);
        RadioButton rdi_ship_to_this = (RadioButton) view.findViewById(R.id.rdi_ship_this_address);
        RadioButton rdi_cash = (RadioButton) view.findViewById(R.id.rdi_cash);


        places_fragment = (AutocompleteSupportFragment)getActivity().getSupportFragmentManager()
                .findFragmentById(R.id.places_autocomplete_fragment);
        places_fragment.setPlaceFields(placeFields);
        places_fragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                placeSelected = place;
                txt_address.setText(place.getAddress());
            }

            @Override
            public void onError(@NonNull Status status) {
                Toast.makeText(getContext(), "Please order at least one service ! ", Toast.LENGTH_SHORT).show();
            }
        });




        String address = txt_address.toString();
        txt_address.setText(Common.currentUser.getAddress());
        rdi_home_address.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                txt_address.setText(Common.currentUser.getAddress());
                txt_address.setVisibility(View.VISIBLE);
                places_fragment.setHint(Common.currentUser.getAddress());
            }
        });
        rdi_other_address.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b)
            {
                txt_address.setVisibility(View.VISIBLE);
            }

        });
        rdi_ship_to_this.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
              if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) { return;}
                fusedLocationProviderClient.getLastLocation()
                  .addOnFailureListener(e -> {
                      Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                      txt_address.setVisibility(view.GONE);
                  })
                   .addOnCompleteListener(task -> {
                    String coordinates = new StringBuilder()
                    .append(task.getResult().getLatitude())
                     .append("/")
                     .append(task.getResult().getLongitude()).toString();


                    Single<String> singleAddress = Single.just(getAddressFormLatLng(task.getResult().getLatitude(),
                    task.getResult().getLongitude()));

                    Disposable disposable = singleAddress.subscribeWith(new DisposableSingleObserver<String>() {
                        @Override
                        public void onSuccess(@io.reactivex.annotations.NonNull String s) {
                            txt_address.setText(s);
                            txt_address.setVisibility(view.VISIBLE);
                            places_fragment.setHint(s);
                        }

                        @Override
                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                            txt_address.setText(e.getMessage());
                            txt_address.setVisibility(View.VISIBLE);
                        }
                    });


                });

            }
        });

        builder.setView(view);
        builder.setNegativeButton("NO", (dialog, which) -> {
            dialog.dismiss();
        }).setPositiveButton("YES", (dialog, which) -> {
            if (rdi_cash.isChecked()) {
                order(address);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void order(String address) {

        compositeDisposable.
                add(cartDataSource.getAllCart(Common.currentUser.getUid()).
                        subscribeOn(Schedulers.io()).
                        observeOn(AndroidSchedulers.mainThread())
                        .subscribe((List<CartItem> cartItems) -> {
                            OrderModel order = new OrderModel();
                            order.setUserId(Common.currentUser.getUid());
                            order.setUserName(Common.currentUser.getName());
                            order.setUserPhone(Common.currentUser.getPhone());
                            order.setShippingAddress(address);

                            if (currentLocation != null) {
                                order.setLat(currentLocation.getLatitude());
                                order.setLng(currentLocation.getLongitude());
                            } else {
                                order.setLat(-0.1f);
                                order.setLng(-0.1f);
                            }
                            if(cartItems.isEmpty()){
                                CartItem c = new CartItem();
                                c.setServiceId("");
                                c.setServiceName("");
                                c.setUserPhone("");
                                c.setServiceImage("");
                                c.setUid("");
                                cc.add(c);
                                order.setCartItemList(cc);
                            }
                            else {
                                order.setCartItemList(cartItems);
                            }
                            order.setCod(true);
                            order.setTransactionId("Cash or Delivery");

//                writeOrderToFirebase(order);
                            syncLocalTimeWithGlobaltime(order); // error create method

                        }, throwable -> {

                            Toast.makeText(getContext(), "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                        }));

    }

    private void syncLocalTimeWithGlobaltime(OrderModel order) {
        final DatabaseReference offsetRef = FirebaseDatabase.getInstance().getReference(".info/serverTimeOffset");
        offsetRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long offset = snapshot.getValue(Long.class);
                long estimatedServerTimeMs = System.currentTimeMillis() + offset;
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
                Date resultDate = new Date(estimatedServerTimeMs);
                Log.d("TEST_DATE", "" + sdf.format(resultDate));

                listener.onLoadTimeSuccess(order, estimatedServerTimeMs);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                listener.onLoadTimeFailed(error.getMessage());
            }
        });
    }

    private void writeOrderToFirebase(OrderModel order) {
        FirebaseDatabase.getInstance().getReference(Common.ORDER_REF)
                .child(Common.createOrderNumber())
                .setValue(order)
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }).addOnCompleteListener(task -> {
            cartDataSource.cleanCart(Common.currentUser.getUid())
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Integer>() {
                        @Override
                        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                        }

                        @Override
                        public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {

                         Toast.makeText(getContext(),"Placed Successfully !",Toast.LENGTH_SHORT).show();
                         EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                    }

                        @Override
                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                            Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });
        });

    }

    private String getAddressFormLatLng(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        String result = "";
        try {
            List<Address> addressList = geocoder.getFromLocation(latitude, longitude, 1);
            if (addressList != null && addressList.size() > 0) {
                Address address = addressList.get(0);
                StringBuilder sb = new StringBuilder(address.getAddressLine(0));
                result = sb.toString();
            } else
                result = "Address not found";
        } catch (IOException e) {
            e.printStackTrace();
            result = e.getMessage();
        }
        return result;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        cartViewModel =
                //ViewModelProviders.of(this).get(CartViewModel.class);
                new ViewModelProvider(this).get(CartViewModel.class);
        listener = this;

        View root = inflater.inflate(R.layout.fragment_cart, container, false);
        cartViewModel.initCartDataSource(getContext());
        cartViewModel.getMutableLiveDataItems().observe(getViewLifecycleOwner(), new Observer<List<CartItem>>() {
            @Override
            public void onChanged(List<CartItem> cartItems) {

                if (cartItems == null || cartItems.isEmpty()) {
                    recycler_requested_services.setVisibility(View.GONE);
                    //     group_place_holder.setVisibility(View.GONE);
                    txt_empty_cart.setVisibility(View.VISIBLE);
                } else {
                    recycler_requested_services.setVisibility(View.VISIBLE);
                    //       group_place_holder.setVisibility(View.VISIBLE);
                    txt_empty_cart.setVisibility(View.GONE);


                    adapter = new MyCartAdapter(getContext(), cartItems);
                    recycler_requested_services.setAdapter(adapter);
                }
            }
        });

        unbinder = ButterKnife.bind(this, root);
        initViews();
        initLocation();
        return root;
    }

    private void initLocation() {
        buildLocationRequest();
        buildlocationCallback();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext()); // import LocationServices
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());

    }





    private void buildlocationCallback() {

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                currentLocation = locationResult.getLastLocation();
            }
        };
    }

    private void buildLocationRequest() {

        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setSmallestDisplacement(10f);

    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.cart_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_clear_cart) {
            cartDataSource.cleanCart(Common.currentUser.getUid()).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<Integer>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onSuccess(Integer integer) {
                    Toast.makeText(getContext(), " All Cleared !", Toast.LENGTH_SHORT).show();
                    EventBus.getDefault().postSticky(new CounterCartEvent(true));
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    private void initViews() {
         initPlaceClient();
        setHasOptionsMenu(true);

        cartDataSource = new LocalCartDataSource(CartDatabase.getInstance(getContext()).cartDAO());

        recycler_requested_services.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recycler_requested_services.setLayoutManager(linearLayoutManager);
        recycler_requested_services.addItemDecoration(new DividerItemDecoration(getContext(), linearLayoutManager.getOrientation()));


        MySwipeHelper mySwipeHelper = new MySwipeHelper(getContext(), recycler_requested_services, 200) {
            @Override
            protected void instantiatmybutton(RecyclerView.ViewHolder viewHolder, List<MyButton> buffer) {
                buffer.add(new MyButton(getContext(), "Delete", 25, 0, Color.parseColor("#FF3C30"),
                        pos -> {
//                            Toast.makeText(getContext(),"Delete item", Toast.LENGTH_SHORT).show();
                            CartItem cartItem = adapter.getItemAtPosition(pos);
                            cartDataSource.deleteCartItem(cartItem).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new SingleObserver<Integer>() {
                                @Override
                                public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {

                                }

                                @Override
                                public void onSuccess(@io.reactivex.annotations.NonNull Integer integer) {
                                    adapter.notifyItemRemoved(pos);
                                    EventBus.getDefault().postSticky(new CounterCartEvent(true));
                                    Toast.makeText(getContext(), "Delete item from Cart successful!", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                                    Toast.makeText(getContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }));
            }
        };
    }

    private void initPlaceClient() {
        Places.initialize(getContext(),getString(R.string.google_maps_key));
        placesClient = Places.createClient(getContext());

    }

    @Override
    public void onStop() {
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        compositeDisposable.clear();
        super.onStop();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (fusedLocationProviderClient != null)
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
    }

    @Override
    public void onLoadTimeSuccess(OrderModel order, long estimateTimeInMs) {
        order.setCreateDate(estimateTimeInMs);
        order.setOrderStatus(0);
        writeOrderToFirebase(order);
    }

    @Override
    public void onLoadOnlyTimeSuccess(long estimateTimeInMs) {
        // Do nothing
    }

    @Override
    public void onLoadTimeFailed(String message) {

        Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
    }
}