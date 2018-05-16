package app.deepakbharti.com.wallpapersworld.services;

import android.app.Service;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class SensorService extends Service {

    private Bitmap bitmap;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        File folder = new File(Environment.getExternalStorageDirectory().toString() + "/wallpapers World");
        folder.mkdir();

        File[] listOfFiles = folder.listFiles();
        if (listOfFiles == null) {
            Toast.makeText(this, "Please donwload few wallpapers first.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Change Wallpaper Started...", Toast.LENGTH_SHORT).show();
            int rnd = new Random().nextInt(listOfFiles.length);
            Uri uri = Uri.fromFile(listOfFiles[rnd]);
            if (uri != null) {
                WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    wallpaperManager.setBitmap(bitmap);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Change Wallpaper Stopped...", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
