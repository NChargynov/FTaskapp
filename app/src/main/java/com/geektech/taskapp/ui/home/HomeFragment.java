package com.geektech.taskapp.ui.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.geektech.taskapp.App;
import com.geektech.taskapp.FormActivity;
import com.geektech.taskapp.OnItemClickListener;
import com.geektech.taskapp.R;
import com.geektech.taskapp.Task;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class HomeFragment extends Fragment {

    private TaskAdapter adapter;
    private List<Task> list;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

//        list.add("Задача Номер 1");
//        list.add("Задача Номер 2");
//        list.add("Задача Номер 3");
//        list.add("Задача Номер 4");
//        list.add("Задача Номер 5");
//        list.add("Задача Номер 6");
//        list.add("Задача Номер 7");
        RecyclerView recyclerView = root.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));

        list = new ArrayList<>();
        getDataFromFireStore();
//        App.getDataBase().taskDao().getAllLive().observe(getViewLifecycleOwner(), new Observer<List<Task>>() {
//            @Override
//            public void onChanged(List<Task> tasks) {
//                list.clear();
//                list.addAll(tasks);
//                adapter.notifyDataSetChanged();
//            }
//        });
//        list = App.getDataBase().taskDao().getAll();
        adapter = new TaskAdapter(list);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Task task = list.get(position);
                Intent intent = new Intent(getContext(), FormActivity.class);
                intent.putExtra("task", task);
//                intent.putExtra("title", list.get(position).getTitle());
//                intent.putExtra("desc", list.get(position).getDesc());
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(final int position) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Внимание!!!")
                        .setMessage("Вы действительно хотите удалить запись?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                App.getDataBase().taskDao().delete(list.get(position));
                            }
                        }).setNegativeButton("Отмена", null).show();
            }
        });
        return root;
    }

    private void getDataFromFireStore() {
        FirebaseFirestore.getInstance().collection("tasks").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                list.clear();
                list.addAll(queryDocumentSnapshots.toObjects(Task.class));
                adapter.notifyDataSetChanged();
            }
        });

    }
}