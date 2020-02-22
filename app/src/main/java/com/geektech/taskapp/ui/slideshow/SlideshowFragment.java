package com.geektech.taskapp.ui.slideshow;

import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.geektech.taskapp.R;
import com.geektech.taskapp.Toaster;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SlideshowFragment extends Fragment implements View.OnClickListener {

    private SlideshowViewModel slideshowViewModel;
    private List<String> urls;
    private ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        slideshowViewModel =
                ViewModelProviders.of(this).get(SlideshowViewModel.class);
        View root = inflater.inflate(R.layout.fragment_slideshow, container, false);
        root.findViewById(R.id.btnDownload).setOnClickListener(this);
        progressBar = root.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        urls = new ArrayList<>();
        urls.add("https://klike.net/uploads/posts/2019-06/1560495323_2.jpg");
        urls.add("https://molbuk.ua/uploads/posts/2020-01/1580229658_futbol.jpg");
        urls.add("https://www.soccer.ru/sites/default/files/styles/content_image/public/blogs/records/news.88423.720x4071.jpg?itok=XvpSs4Ml");
        urls.add("https://chelseablues.ru/images/news8/c5c76407.jpg");
        urls.add("https://yaltafootball.com/ia/1567372265.jpg");
        return root;
    }

    @Override
    public void onClick(View v) {
        progressBar.setVisibility(View.VISIBLE);
        File folder = new File(Environment.getExternalStorageDirectory(), "TaskApp/Images");
        folder.mkdirs();
        downloadFiles(folder);
    }

    private void downloadFiles(final File folder) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < urls.size(); i++) {
                        String url = urls.get(i);
                        File file = new File(folder, "image" + i + ".jpg");
                        FileUtils.copyURLToFile(new URL(url), file);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toaster.show("Картинки успешно добавлены");
                    }
                });
            }
        });
        thread.start();
    }
}