package com.geektech.taskapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FormActivity extends AppCompatActivity {
    private EditText editText;
    private EditText editDesc;
    private Task task;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        editText = findViewById(R.id.editText);
        editDesc = findViewById(R.id.edit_desk);
        secondOpen();


//        Intent intent = getIntent();
//        intent.getSerializableExtra("task");
//        title = intent.getStringExtra("title");
//        desc = intent.getStringExtra("desc");
//        editText.setText(title);
//        editDesc.setText(desc);
    }

    private void secondOpen() {
        task = (Task) getIntent().getSerializableExtra("task");
        if (task != null) {
            editText.setText(task.getTitle());
            editDesc.setText(task.getDesc());
        }
    }


    public void onClick(View view) {
        String textTitle = editText.getText().toString().trim();
        String textDesc = editDesc.getText().toString().trim();
        if (textTitle.isEmpty() || textDesc.isEmpty()) {
            Toaster.show("Вы не ввели данные");
            editDesc.setError("Пустое поле");
            editText.setError("Пустое поле");
        } else {
            if (task != null) {
                task.setTitle(textTitle);
                task.setDesc(textDesc);
                App.getDataBase().taskDao().update(task);
            } else {
                task = new Task(textTitle, textDesc);
                App.getDataBase().taskDao().insert(task);
                addDatabase();
            }
            finish();
        }
    }

    private void addDatabase() {
        FirebaseFirestore.getInstance().collection("tasks")
                .add(task).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    Toaster.show("Ваши задачи успешно добавлены в базу данных");
                } else {
                    Toaster.show("Ошибка: " + task.getException().getMessage());
                }
            }
        });
    }
}