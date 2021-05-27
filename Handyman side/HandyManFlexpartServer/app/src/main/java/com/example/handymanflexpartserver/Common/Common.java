package com.example.handymanflexpartserver.Common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.example.handymanflexpartserver.Model.BestDealsModel;
import com.example.handymanflexpartserver.Model.CategoryModel;
import com.example.handymanflexpartserver.Model.MostPopularModel;
import com.example.handymanflexpartserver.Model.ServerUserModel;
import com.example.handymanflexpartserver.Model.ServiceModel;
import com.example.handymanflexpartserver.Model.TokenModel;
import com.example.handymanflexpartserver.R;
import com.example.handymanflexpartserver.services.MyFCMServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Common {
    public static final String SERVER_REF = "Server";
    public static final String ORDER_REF = "Orders";
    public static final String NOTI_TITLE = "title";
    public static final String NOTI_CONTENT = "content";
    public static final String TOKEN_REF = "Tokens";
    public static final String BEST_DEALS = "BestDeals"; // same name as firebase
    public static final String MOST_POPULAR = "MostPopular";
    public static final String CHAT_REF = "Chat";
    public static final String KEY_ROOM_ID ="CHAT_ROOM_ID" ;
    public static final String KEY_CHAT_USER ="CHAT_SENDER" ;
    public static final String CHAT_DETAIL_REF = "ChatDetail";
    public static final String TRIP_START = "TRIP";


    public static ServerUserModel currentServerUser;
    public static final String CATEGORY_REF = "Category";
    public static CategoryModel categorySelected;
    public static ServiceModel selectedService;
    public static final int DEFAULT_COLUMN_COUNT = 0;
    public static final int FULL_WIDTH_COLUMN = 1;
    public static BestDealsModel bestDealsSelected;
    public static MostPopularModel mostPopularSelected;
    public static String Order_Data = "OrderData";


    public enum ACTION{
        CREATE,
        UPDATE,
        DELETE
    }


    public static void setSpanString(String welcome, String name, TextView textView) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(welcome);
        SpannableString spannableString = new SpannableString(name);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(boldSpan, 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(spannableString);
        textView.setText(builder, TextView.BufferType.SPANNABLE);
    }

    public static void setSpanStringColor(String welcome, String name, TextView textView, int color) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(welcome);
        SpannableString spannableString = new SpannableString(name);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(boldSpan, 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new ForegroundColorSpan(color), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(spannableString);
        textView.setText(builder, TextView.BufferType.SPANNABLE);

    }

    public static String convertStatusToString(int orderStatus) {
        switch (orderStatus) {
            case 0:
                return "Placed";

            case 1:
                return "Shipping";

            case 2:
                return "Shipped";

            case -1:
                return "Cancelled";

            default:
                return "Error";
        }
    }

    public static void showNotification(Context context, int id, String title, String content, Intent intent) {
        PendingIntent pendingIntent = null;
        if (intent != null)
            pendingIntent = PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        String NOTIFICATION_CHANNEL_ID = "handymanflex";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "HandyManFlex", NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("HandyManFlex");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title).
                setContentText(content).
                setAutoCancel(true).
                setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.fui_ic_check_circle_black_128dp));

        if (pendingIntent != null)
            builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notificationManager.notify(id, notification);
    }


    public static void updateToken(Context context, String newToken) {
        FirebaseDatabase.getInstance()
                .getReference(Common.TOKEN_REF)
                .child(Common.currentServerUser.getUid())
                .setValue(new TokenModel(Common.currentServerUser.getPhone(), newToken))
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();

                });
    }

    public static String createTopicOrder() {
        return new StringBuilder("/topics/new_order").toString();
    }

    public static float getBearing(LatLng begin, LatLng end) {
        double lnt = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);
        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lnt)));


        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lnt))) + 90);


        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lnt)) + 180);


        if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lnt)) + 270);
        return -1;
    }

    public static List<LatLng> decoPoly(String encoded) {
        List poly = new ArrayList();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;
        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;  result |= (b & 0x1f) << shift;shift += 0;
            }
            while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat; shift=0; result=0;
            do {
                b=encoded.charAt(index++)-63;
                result |=(b &0x1f) <<shift;
                shift+=5;
            }while(b>=0x20);
            int dlng =((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng |= dlng;
            LatLng p = new LatLng(((double)lat/1E5),
            ((double)lng/1E5));
            poly.add(p);
        }
        return poly;
//
//    }
}


    public static String generateChatRoomId(String a, String b) {
        if(a.compareTo(b) > 0 )
            return new StringBuilder(a).append(b).toString();
        else if(a.compareTo(b) < 0)
            return new StringBuilder(b).append(a).toString();
        else
            return new StringBuilder("CharYourself_Error_")
                    .append(new Random().nextInt())
                    .toString();

    }

    public static String getFileName(ContentResolver contentResolver, Uri fileUri) {
        String result = null;
        if (fileUri.getScheme().equals("content")) {
            Cursor cursor = contentResolver.query(fileUri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst())
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
            } finally {
                cursor.close();
            }
        }
        if(result==null)
        {
            result= fileUri.getPath();
            int cut = result.lastIndexOf('/');
            if(cut != -1)
                result= result.substring(cut +1);
        }
        return result;
    }


}
