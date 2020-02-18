package com.geektech.taskapp.auth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.geektech.taskapp.MainActivity;
import com.geektech.taskapp.R;
import com.geektech.taskapp.Toaster;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class PhoneActivity extends AppCompatActivity {
    private EditText editPhone, editCode;
    private TextView textViewNumber;
    private String id, smsCode;
    private boolean isCodeSend;
    private Button onSmsBtn;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;
    private ProgressBar progressBar;
    private Chronometer chronometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone);
        editPhone = findViewById(R.id.editPhone);
        editCode = findViewById(R.id.editCode);
        onSmsBtn = findViewById(R.id.onSmsBtn);
        textViewNumber = findViewById(R.id.textViewNumber);
        progressBar = findViewById(R.id.progressBar);
        chronometer = findViewById(R.id.time);

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Log.d("TAG", "onVerificationCompleted: ");
                smsCode = phoneAuthCredential.getSmsCode();
                if (isCodeSend) {
                    editCode.setText(smsCode);
                    signIn(phoneAuthCredential);
                } else {
                    Toaster.show("Не удалось отправить код на ваш номер");
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                Log.d("TAG", "onVerificationFailed: " + e.getMessage());

            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                Log.d("TAG", "onCodeSent: ");
                id = s;
                isCodeSend = true;
                Toaster.show("Смс с кодом отправлено на ваш телефон");
            }

            @Override
            public void onCodeAutoRetrievalTimeOut(String s) {
                super.onCodeAutoRetrievalTimeOut(s);
                Log.d("TAG", "onCodeAutoRetrievalTimeOut: ");
            }
        };
    }

    private void signIn(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toaster.show("Успешная авторизация");
                            startActivity(new Intent(PhoneActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toaster.show("Ошибка авторизации " + task.getException().getMessage());
                        }
                    }
                });
    }

    public void onClick(View view) {
//        hideKeyboard(PhoneActivity.this);
        String codeNumber = textViewNumber.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        phone = codeNumber + phone;
        if (phone.isEmpty()) {
            editPhone.setError("Номер не введен");
            editPhone.requestFocus();
            return;
        } else if (phone.length() < 10) {
            editPhone.setError("Неправильный номер телефона");
            editPhone.requestFocus();
            return;
        } else {
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phone, 60, TimeUnit.SECONDS, this, callbacks);
            view.setVisibility(View.INVISIBLE);
            editPhone.setVisibility(View.INVISIBLE);
            textViewNumber.setVisibility(View.INVISIBLE);
            editCode.setVisibility(View.VISIBLE);
            onSmsBtn.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.VISIBLE);
            chronometer.setVisibility(View.VISIBLE);
            chronometer.start();
        }
    }

    public void onCodeClick(View view) {
        String code = editCode.getText().toString();
        if (code.isEmpty()) {
            editCode.setError("Код не введен");
            editCode.requestFocus();
            return;
        } else {
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(id, code);
            signIn(phoneAuthCredential);
        }
    }

//    public static void hideKeyboard(Activity activity) {
//        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
//        //Find the currently focused view, so we can grab the correct window token from it.
//        View view = activity.getCurrentFocus();
//        //If no view currently has focus, create a new one, just so we can grab a window token from it
//        if (view == null) {
//            view = new View(activity);
//        }
//        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//    }
}
