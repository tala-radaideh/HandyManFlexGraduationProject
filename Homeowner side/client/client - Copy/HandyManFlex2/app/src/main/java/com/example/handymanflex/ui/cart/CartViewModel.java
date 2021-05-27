package com.example.handymanflex.ui.cart;

import android.content.Context;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.handymanflex.Common.Common;

import java.util.List;

import Database.CartDatabase;
import Database.CartItem;
import Database.LocalCartDataSource;
import Database.cartDataSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class CartViewModel extends ViewModel {

    private CompositeDisposable compositeDisposable ;
    private Database.cartDataSource cartDataSource;
    private MutableLiveData<List<CartItem>> mutableLiveDataItems;
    public CartViewModel() {

        compositeDisposable = new CompositeDisposable();
    }


    public void initCartDataSource(Context context)
    {
        cartDataSource= new LocalCartDataSource(CartDatabase.getInstance(context).cartDAO());
    }




    public void onStop()
    {
        compositeDisposable.clear();
    }





    public MutableLiveData<List<CartItem>> getMutableLiveDataItems() {
        if( mutableLiveDataItems == null)
            mutableLiveDataItems=new MutableLiveData<>();
        getAllCartItems();
        return mutableLiveDataItems;
    }





    private void getAllCartItems()
    {
        compositeDisposable.add(cartDataSource.getAllCart(Common.currentUser.getUid())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cartItems -> {
                    mutableLiveDataItems.setValue(cartItems);

                }, throwable -> {
                    mutableLiveDataItems.setValue(null);
                }));
    }
}

