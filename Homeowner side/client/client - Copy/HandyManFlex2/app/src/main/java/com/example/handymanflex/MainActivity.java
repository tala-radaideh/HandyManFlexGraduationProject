package com.example.handymanflex;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.handymanflex.Common.Common;
import com.example.handymanflex.Model.UserModel;
import com.example.handymanflex.databinding.ActivityMainBinding;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.InstallationTokenResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import dmax.dialog.SpotsDialog;
import io.reactivex.disposables.CompositeDisposable;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String mVerificationId;
    private static final String TAG = "MAIN_TAG";
    private ProgressDialog pd;

    private static int APP_REQUEST_CODE = 1717; // anynum
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;
    private AlertDialog dialog;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private DatabaseReference userRef;
    private List<AuthUI.IdpConfig> providers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.phoneL1.setVisibility(View.VISIBLE);
        binding.codeL1.setVisibility(View.GONE);
        firebaseAuth = FirebaseAuth.getInstance();

        pd = new ProgressDialog(this);
        pd.setTitle("Please wait ...");
        pd.setCanceledOnTouchOutside(false);

        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
  signInWithPhoneAuth(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
   pd.dismiss();
                Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, forceResendingToken);
                Log.d(TAG,"onCodeSent"+verificationId);
                mVerificationId=verificationId;
                forceResendingToken=token;
                pd.dismiss();
                
                binding.phoneL1.setVisibility(View.GONE);
                binding.codeL1.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, "Verification code sent...", Toast.LENGTH_SHORT).show();

            }

        };
        binding.phoneContB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = binding.phoneEt.getText().toString().trim();
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(MainActivity.this, "enter your phone number", Toast.LENGTH_SHORT).show();
                } else {
                    startPhoneVerification(phone);
                }
            }
        });
        binding.codeSubB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String code = binding.codeEt.getText().toString().trim();
                if (TextUtils.isEmpty(code)) {
                    Toast.makeText(MainActivity.this, "enter verification code", Toast.LENGTH_SHORT).show();
                } else {
                    VerifyWithCode(mVerificationId, code);
                }
            }
        });


    }



    private void startPhoneVerification(String phone) {
        pd.setMessage("Verifying phone num ");
        pd.show();
        PhoneAuthOptions options=
                PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phone)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallback)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);

    }


    private void VerifyWithCode(String verificationId, String code) {
        pd.setMessage("Verifying code ...  ");
        pd.show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId,code);
        signInWithPhoneAuth(credential);
    }

    private void signInWithPhoneAuth(PhoneAuthCredential credential) {
        pd.setMessage("Logging in ...");
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        pd.dismiss();
                        String phone = firebaseAuth.getCurrentUser().getDisplayName();
                        Toast.makeText(MainActivity.this, "Logging in as "+phone, Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                  pd.dismiss();
                       Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}