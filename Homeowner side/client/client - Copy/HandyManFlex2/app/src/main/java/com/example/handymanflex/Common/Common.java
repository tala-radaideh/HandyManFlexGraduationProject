package com.example.handymanflex.Common;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.handymanflex.Model.CategoryModel;
import com.example.handymanflex.Model.ServiceModel;
import com.example.handymanflex.Model.TokenModel;
import com.example.handymanflex.Model.UserModel;

import java.util.Random;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.database.FirebaseDatabase;

public class Common {
    public static final String USER_REFERENCES ="Users" ;
    public static final String POPULAR_CATEGORY_REF = "MostPopular";
    public static final String BEST_DEALS_REF ="BestDeals" ;
    public static final int DEFAULT_COLUMN_COUNT =0 ;
    public static final int FULL_WIDTH_COLUMN = 1;
    public static final String CATEGORY_REF ="Category" ;
    public static final String COMMENT_REF = "Comments";
    public static final String ORDER_REF = "Orders";
    public static final String CHAT_REF ="Chat" ;
    public static final String CHAT_DETAIL_REF ="ChatDetail" ;
    private static final String TOKEN_REF ="Tokens" ;

    public static UserModel currentUser;
    public static CategoryModel categorySelected;
    public  static ServiceModel selectedService;
    public static String currentToken="";


    public static String createOrderNumber() {
        return new StringBuilder().append(System.currentTimeMillis()).append(Math.abs(new Random().nextInt())).toString();

    }


  /*  public static void updateToken(Context context,  String newToken) {
        FirebaseDatabase.getInstance().getReference(Common.TOKEN_REF).child(Common.currentUser.getUid()).setValue(new TokenModel(Common.currentUser.getPhone(),newToken))
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

    }

   */

    public static String getDateOfWeek(int i) {
        switch (i)
        {
            case 1 :
                return "Monday";

            case 2 :
                return"Tuesday";

            case 3 :
                return"Wednesday";

            case 4 :
                return"Thursday";

            case 5 :
                return"Friday";

            case 6 :
                return"Saturday";

            case 7 :
                return"Sunday";

            default:
                return "Unknown !";



        }
    }



    public static void setSpanString(String welcome, String name, TextView textView) {
        SpannableStringBuilder builder= new SpannableStringBuilder();
        builder.append(welcome);
        SpannableString spannableString = new SpannableString(name);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        spannableString.setSpan(boldSpan,0,name.length(),Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        builder.append(spannableString);
        textView.setText(builder,TextView.BufferType.SPANNABLE);
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