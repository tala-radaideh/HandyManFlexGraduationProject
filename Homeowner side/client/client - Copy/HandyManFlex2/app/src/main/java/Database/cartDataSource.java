package Database;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface  cartDataSource {


    Flowable<List<CartItem>> getAllCart(String uid);


    Single<Integer> countItemCart(String uid);


    Single<CartItem> getItemInCart(String uid);


    Completable insertOrReplaceAll(CartItem cartItems);


    Single<Integer> updateCartItem(CartItem cartItems);


    Single<Integer> deleteCartItem(CartItem cartItems);

    Single<Integer> cleanCart(String uid);
    Single<CartItem> getdetailItemInCart(String uid, String ServiceId);

}
