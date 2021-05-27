package Database;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName="Cart",primaryKeys={"uid","ServiceId"})

public class CartItem {
    @NonNull
    @ColumnInfo(name="ServiceId")

    private String ServiceId;

    @ColumnInfo(name="ServiceName")
    private String ServiceName;

    @ColumnInfo(name="ServiceImage")
    private String ServiceImage;

    @ColumnInfo(name="userPhone")
    private String userPhone;
    @NonNull
    @ColumnInfo(name="uid")
    private String uid;

    @NonNull
    public String getServiceId() {
        return ServiceId;
    }

    public void setServiceId(@NonNull String serviceId) {
        ServiceId = serviceId;
    }

    public String getServiceName() {
        return ServiceName;
    }

    public void setServiceName(String serviceName) {
        ServiceName = serviceName;
    }

    public String getServiceImage() {
        return ServiceImage;
    }

    public void setServiceImage(String serviceImage) {
        ServiceImage = serviceImage;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
       if(obj==this)
           return  true;
       if(!(obj instanceof CartItem))
           return false;
       CartItem cartItem = (CartItem) obj;
       return cartItem.getServiceId().equals(this.ServiceId);
    }
}
