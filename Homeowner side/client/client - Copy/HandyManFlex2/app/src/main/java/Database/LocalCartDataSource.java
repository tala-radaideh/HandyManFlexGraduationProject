package Database;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class LocalCartDataSource implements cartDataSource {
    private CartDAO cartDAO;

    public LocalCartDataSource(CartDAO cartDAO) {
        this.cartDAO = cartDAO;
    }

    @Override
    public Flowable<List<CartItem>> getAllCart(String uid) {
        return cartDAO.getAllCart(uid);
    }

    @Override
    public Single<Integer> countItemCart(String uid) {
        return cartDAO.countItemCart(uid);
    }



    @Override
    public Single<CartItem> getItemInCart(String uid) {
        return cartDAO.getItemInCart(uid);
    }

    @Override
    public Completable insertOrReplaceAll(CartItem cartItems) {
        return cartDAO.insertOrReplaceAll(cartItems);
    }

    @Override
    public Single<Integer> updateCartItem(CartItem cartItems) {
        return cartDAO.updateCartItem(cartItems);
    }

    @Override
    public Single<Integer> deleteCartItem(CartItem cartItems) {
        return cartDAO.deleteCartItem(cartItems);
    }

    @Override
    public Single<Integer> cleanCart(String uid) {
        return cartDAO.cleanCart(uid);
    }

    @Override
    public Single<CartItem> getdetailItemInCart(String uid, String ServiceId) {
        return cartDAO.getdetailItemInCart(uid,ServiceId);
    }

}
