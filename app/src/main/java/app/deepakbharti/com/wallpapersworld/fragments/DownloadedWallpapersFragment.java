package app.deepakbharti.com.wallpapersworld.fragments;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import app.deepakbharti.com.wallpapersworld.R;
import app.deepakbharti.com.wallpapersworld.adapters.DownloadedWallpapersAdapter;
import app.deepakbharti.com.wallpapersworld.models.Wallpaper;

public class DownloadedWallpapersFragment extends Fragment {

    List<Wallpaper> savedWalls;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView msg;
    DownloadedWallpapersAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_downloaded_wallpapers,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        savedWalls = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recyler_view);
        progressBar = view.findViewById(R.id.progressbar);
        msg = view.findViewById(R.id.msg);

        adapter = new DownloadedWallpapersAdapter(getActivity(), savedWalls);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        progressBar.setVisibility(View.VISIBLE);

        getSavedWallpapers();

    }

    private void getSavedWallpapers(){
        if (ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(),new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},100);
        }
        File folder = new File(Environment.getExternalStorageDirectory().toString() + "/wallpapers World");
        folder.mkdirs();

        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null || listOfFiles.length == 0) {
            adapter.notifyDataSetChanged();
            msg.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        }else{
            for(File file: listOfFiles){
                Uri uri = Uri.fromFile(file);
                Wallpaper w = new Wallpaper();
                int index = file.getName().lastIndexOf('/');
                w.id = file.getName().substring(index+1);
                w.wallpaper = uri.toString();
                savedWalls.add(w);
            }
            adapter.notifyDataSetChanged();
            msg.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
        }
    }
}
