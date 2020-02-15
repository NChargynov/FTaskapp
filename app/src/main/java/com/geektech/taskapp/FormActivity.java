package com.geektech.taskapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

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
        ImageView imageView = findViewById(R.id.imageView_phone);
        imageView.setImageResource(R.drawable.ic_phone_iphone_black_24dp);
        ImageView imageViewFlag = findViewById(R.id.imageViewFlag);
        Glide.with(this).load(
                "https://lh3.googleusercontent.com/proxy/Bf4InAT8L9Kzzbav3ywu7tg-JeCu-J8GHdRWWGkGN7QgbjccxjICAhVI6KxWcN4TQtiR7KS5CaHivHMj6HmZNlJ-1H8j4qXByL0_Ag")
                .into(imageViewFlag);
        secondIntent();
        

//        Intent intent = getIntent();
//        intent.getSerializableExtra("task");
//        title = intent.getStringExtra("title");
//        desc = intent.getStringExtra("desc");
//        editText.setText(title);
//        editDesc.setText(desc);
    }

    private void secondIntent() {
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
            }
            finish();
        }
    }
}