package Database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;
import retrofit2.http.DELETE;
@Dao
public interface CartDAO {
    @Query("SELECT * FROM Cart WHERE uid=:uid")
    Flowable<List<CartItem>> getAllCart(String uid);

    @Query("SELECT COUNT(*) FROM Cart WHERE uid=:uid")
    Single<Integer> countItemCart(String uid);

    @Query("SELECT * FROM Cart WHERE uid=:uid")
    Single<CartItem> getItemInCart( String uid);



    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertOrReplaceAll(CartItem  cartItems);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    Single<Integer> updateCartItem(CartItem cartItems);

    @Delete
    Single<Integer> deleteCartItem(CartItem cartItems);

    @Query("DELETE FROM Cart WHERE uid=:uid")
    Single<Integer> cleanCart(String uid);


    @Query("SELECT * FROM Cart WHERE ServiceId=:ServiceId AND uid=:uid")
    Single<CartItem> getdetailItemInCart(String uid, String ServiceId);

}



