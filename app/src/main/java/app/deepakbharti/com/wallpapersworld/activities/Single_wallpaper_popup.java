package app.deepakbharti.com.wallpapersworld.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import app.deepakbharti.com.wallpapersworld.R;
import app.deepakbharti.com.wallpapersworld.models.Wallpaper;

public class Single_wallpaper_popup extends AppCompatActivity {

    private ImageView imageView;
    private TextView textView;
    private Button button;
    private Wallpaper w;
    private ProgressBar progressBar;
    private Context mCtx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_wallpaper_popup);

        mCtx = this;
        Intent intent = getIntent();
        w = new Wallpaper();
        w.id = intent.getStringExtra("id");
        w.title = intent.getStringExtra("title");
        w.wallpaper = intent.getStringExtra("wallpaper");

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.8),(int)(height*.7));

        textView = (TextView) findViewById(R.id.single_title);
        imageView = (ImageView) findViewById(R.id.single_image);
        progressBar = (ProgressBar) findViewById(R.id.single_progressbar);

        textView.setText(w.title);
        Glide.with(this).load(w.wallpaper).into(imageView);

        button = (Button) findViewById(R.id.set_single_wall);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setEnabled(false);
                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        long futuretime = System.currentTimeMillis();
                        while (System.currentTimeMillis()<futuretime){
                            synchronized (this){
                                try{
                                    wait(futuretime-System.currentTimeMillis());
                                }catch (Exception e){

                                }
                            }
                        }
                        sethandler.sendEmptyMessage(0);
                    }
                });
            }
        });
    }

    Handler sethandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            setWallpaper(w);
        }
    };

    private void setWallpaper(final Wallpaper w){
        progressBar.setVisibility(View.VISIBLE);

        Glide.with(mCtx)
                .asBitmap()
                .load(w.wallpaper)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                        progressBar.setVisibility(View.GONE);

                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        Uri uri = saveWallpaperAndGetUri(resource,w.id);

                        if(uri != null){
                            Intent i = new Intent(Intent.ACTION_ATTACH_DATA);
                            i.setDataAndType(uri, "image/*");
                            i.putExtra("mimeType", "image/*");
                            ((Activity) mCtx).startActivityForResult(Intent.createChooser(i,"Set as:"),200);
                        }
                    }
                });
        button.setEnabled(true);
    }

    private Uri saveWallpaperAndGetUri(Bitmap bmp, String id){
        if (ContextCompat.checkSelfPermission(mCtx, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) mCtx,new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
            return null;
        }

        File folder = new File(Environment.getExternalStorageDirectory().toString() + "/wallpapers World");
        folder.mkdirs();

        File file = new File(folder, id + ".jpg");
        try {
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG,100,out);
            out.flush();
            out.close();
            return Uri.fromFile(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }
}
