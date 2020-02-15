package com.geektech.taskapp.onBoard;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.geektech.taskapp.MainActivity;
import com.geektech.taskapp.R;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class BoardFragment extends Fragment {


    public BoardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_board, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView textView = view.findViewById(R.id.textView);
        ImageView imageView = view.findViewById(R.id.imageView);
        Button button = view.findViewById(R.id.buttonStart);
        LinearLayout fragmentBoard = view.findViewById(R.id.fragment_board);

        int pos = getArguments().getInt("pos");
        switch (pos) {
            case 0:
                textView.setText("Google");
                imageView.setImageResource(R.drawable.onboard_page1);
                button.setVisibility(View.INVISIBLE);
                fragmentBoard.setBackground(getResources().getDrawable(R.drawable.gradient_onboard));
                break;
            case 1:
                textView.setText("Apple");
                textView.setTextColor(getResources().getColor(R.color.colorWhite));
                button.setVisibility(View.INVISIBLE);
                imageView.setImageResource(R.drawable.onboard_page2);
                fragmentBoard.setBackground(getResources().getDrawable(R.drawable.gradient_onboard2));
                break;
            case 2:
                textView.setText("Android");
                button.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.onboard_page3);
                fragmentBoard.setBackground(getResources().getDrawable(R.drawable.gradient_onboard3));
                break;
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getActivity().getSharedPreferences("settings",
                        Context.MODE_PRIVATE);
                preferences.edit().putBoolean("isShown", true).apply();
                startActivity(new Intent(getContext(), MainActivity.class));
                getActivity().finish();
            }
        });
    }
}
