package remote;

import com.example.handymanflexpartserver.Model.FCMResponse;
import com.example.handymanflexpartserver.Model.FCMSendData;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.*;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMService {
    @Headers({"Content-Type:application/json"
            ,"Authorization:key=AAAA-wuCTXE:APA91bGxx9hz5IrSpwigHk11C581LrShXCnJY_jWS8jPPePULgSraeBGDStkukS6nPks6gLgop7MXET2KOC5rqmDz7fusF6LdJ2QC6go6MtAbKrGrhFQJtz0j-Pqfa32LJtyrvSKb9ka\t\n"})

    @POST("fcm/send")
    Observable<FCMResponse> sendNotification(@Body FCMSendData body);


}
