package com.geektech.taskapp.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.geektech.taskapp.R;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneActivity extends AppCompatActivity {
    private EditText editPhone;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        editPhone = findViewById(R.id.editPhone);
        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.d("TAG", "onVerificationCompleted: ");
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d("TAG", "onVerificationFailed: " + e.getMessage());

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.d("TAG", "onCodeSent: ");

            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Log.d("TAG", "onCodeAutoRetrievalTimeOut: ");

            }
        };
    }

    public void onClick(View view) {
        String phone = editPhone.getText().toString().trim();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phone, 60, TimeUnit.SECONDS, this, callbacks);
    }
}
