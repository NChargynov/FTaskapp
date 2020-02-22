package com.geektech.taskapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.geektech.taskapp.auth.PhoneActivity;
import com.geektech.taskapp.onBoard.OnBoardActivity;
import com.geektech.taskapp.room.AppDataBase;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Environment;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.prefs.Preferences;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private String nameForProfile, emailForProfile;
    private TextView editNameFromNavH, editEmailFromNavH;
    private View header;
    private ImageView imageViewAvatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("settings", Context.MODE_PRIVATE);
        boolean isShown = preferences.getBoolean("isShown", false);


        if (!isShown) {
            startActivity(new Intent(this, OnBoardActivity.class));
            finish();
            return;
        }

        if (FirebaseAuth.getInstance().getCurrentUser() == null){
            startActivity(new Intent(this, PhoneActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, FormActivity.class);
                startActivityForResult(intent, 1);
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        header = navigationView.getHeaderView(0);
        editNameFromNavH = header.findViewById(R.id.editHeaderName);
        editEmailFromNavH = header.findViewById(R.id.editHeaderEmail);
        imageViewAvatar = header.findViewById(R.id.imageViewAvatar);

        SharedPreferences pref = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        nameForProfile = pref.getString("name", null);
        emailForProfile = pref.getString("email", null);

        String userId = FirebaseAuth.getInstance().getUid();
        StorageReference storage = FirebaseStorage.getInstance().getReference();
        storage.child("images").child(userId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(MainActivity.this).load(uri).circleCrop().into(imageViewAvatar);
            }
        });

        editNameFromNavH.setText(nameForProfile);
        editEmailFromNavH.setText(emailForProfile);

        header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));

            }
        });
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        initFile();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        nameForProfile = pref.getString("name", null);
        emailForProfile = pref.getString("email", null);

        String userId = FirebaseAuth.getInstance().getUid();
        StorageReference storage = FirebaseStorage.getInstance().getReference();
        storage.child("images").child(userId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(MainActivity.this).load(uri).circleCrop().into(imageViewAvatar);
            }
        });

        editNameFromNavH.setText(nameForProfile);
        editEmailFromNavH.setText(emailForProfile);
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 1){
//            SharedPreferences pref = getSharedPreferences("MyPrefs", MODE_PRIVATE);
//            nameForProfile = pref.getString("name", null);
//            emailForProfile = pref.getString("email", null);
//
//            editNameFromNavH.setText(nameForProfile);
//            editEmailFromNavH.setText(emailForProfile);
//        }
//    }

    @AfterPermissionGranted(200)
    private void initFile() {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(this, perms)) {
            File folder = new File(Environment.getExternalStorageDirectory(), "MyTaskApp");
            File mediaFile = new File(folder, "Media");
            folder.mkdirs();
            File file = new File(folder, "note2.txt");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            File imageFile = new File(mediaFile, "image.png");
            downloadFile(imageFile);
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    "Разрешите пожалуйста дать доступ к файлам вашего телефона?",
                    200,
                    perms);
        }
    }

    private void downloadFile(final File imageFile) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("https://square.github.io/picasso/static/debug.png");
                    FileUtils.copyURLToFile(url, imageFile);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }


//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
//        fragment.getChildFragmentManager().getFragments().get(0).onActivityResult(requestCode, resultCode, data);
//    }
}
