package com.geektech.taskapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private EditText editName;
    private EditText editEmail;
    private ImageView imageView;
    private boolean justUploaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        imageView = findViewById(R.id.imageViewForAvatar);
//        getData();
        getDataListener();
    }

    private void getDataListener() {
        String userID = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore.getInstance().collection("users")
                .document(userID).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot snapshot, @Nullable FirebaseFirestoreException e) {
                if (snapshot != null && snapshot.exists()) {
                    String name = snapshot.getString("name");
                    String email = snapshot.getString("email");
                    editName.setText(name);
                    editEmail.setText(email);
                    String avatarUrl = snapshot.getString("avatarUrl");
                    if (avatarUrl != null)
                        Glide.with(ProfileActivity.this).load(avatarUrl).into(imageView);
                }
            }
        });
    }

    private void getData() {
        String userID = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore.getInstance().collection("users").document(userID).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            String name = task.getResult().getString("name");
                            editName.setText(name);
                        }
                    }
                });
    }


    public void onClick(View view) {
        String name = editName.getText().toString().trim();
        String email = editEmail.getText().toString().trim();
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("email", email);

        SharedPreferences.Editor editor = getSharedPreferences("MyPrefs", MODE_PRIVATE).edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.apply();

        String userID = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore.getInstance().collection("users")
                .document(userID)
                .update(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toaster.show("Успешно добавлено");
                        } else {
                            Toaster.show("Ошибка: " + task.getException().getMessage());
                        }
                    }
                });
    }

    public void onClickImage(View view) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 1);
//        Intent i = new Intent(Intent.ACTION_PICK,
//              android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//        final int ACTIVITY_SELECT_IMAGE = 1234;
//        startActivityForResult(i, ACTIVITY_SELECT_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1 && data != null) {
            Uri uri = data.getData();
            imageView.setImageURI(uri);
            uploadImage(uri);
        }
    }

    private void uploadImage(Uri uri) {
        String userIDforAvatar = FirebaseAuth.getInstance().getUid();
        final StorageReference reference = FirebaseStorage.getInstance()
                .getReference().child("images").child(userIDforAvatar + ".jpg");
        UploadTask uploadTask = reference.putFile(uri);
        uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                Log.d("TAG", "onProgress: + " + progress);
            }
        }).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                return reference.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful())
                    update(task.getResult());
                else
                    Toaster.show("Ошибка загрузки");
            }
        });

    }

    private void update(Uri result) {
        String userIDforUrl = FirebaseAuth.getInstance().getUid();
        FirebaseFirestore.getInstance().collection("users").document(userIDforUrl)
                .update("avatarUrl", result.toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toaster.show("Загрузка выполнена");
            }
        });

    }
}
