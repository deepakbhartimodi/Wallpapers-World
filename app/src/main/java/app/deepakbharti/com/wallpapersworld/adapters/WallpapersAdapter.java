package app.deepakbharti.com.wallpapersworld.adapters;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import app.deepakbharti.com.wallpapersworld.Functions.UsefulFunctions;
import app.deepakbharti.com.wallpapersworld.R;
import app.deepakbharti.com.wallpapersworld.activities.Single_wallpaper_popup;
import app.deepakbharti.com.wallpapersworld.models.Wallpaper;

public class WallpapersAdapter extends RecyclerView.Adapter<WallpapersAdapter.WallpaperViewHolder> {

    private Context mCtx;
    private List<Wallpaper> wallpaperList;

    public WallpapersAdapter(Context mCtx, List<Wallpaper> wallpaperList) {
        this.mCtx = mCtx;
        this.wallpaperList = wallpaperList;
    }

    @NonNull
    @Override
    public WallpaperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recylesviews_wallpapers, parent, false);
        return new WallpaperViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WallpaperViewHolder holder, int position) {
        Wallpaper w = wallpaperList.get(position);
        holder.textView.setText(w.title);
        Glide.with(mCtx)
                .load(w.wallpaper)
                .into(holder.imageView);

        if(w.isFavourite){
            holder.checkBoxFav.setChecked(true);
        }
    }

    @Override
    public int getItemCount() {
        return wallpaperList.size();
    }

    class WallpaperViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        TextView textView;
        ImageView imageView;

        CheckBox checkBoxFav;
        Button setWall;
        ImageButton buttonShare, buttonDownload;

        public WallpaperViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.text_view_title);
            imageView = itemView.findViewById(R.id.image_view);

            checkBoxFav = itemView.findViewById(R.id.checkbox_fav);
            setWall = itemView.findViewById(R.id.set_wall);
            buttonShare = itemView.findViewById(R.id.button_share);
            buttonDownload = itemView.findViewById(R.id.button_download);

            checkBoxFav.setOnCheckedChangeListener(this);
            imageView.setOnClickListener(this);
            setWall.setOnClickListener(this);
            buttonShare.setOnClickListener(this);
            buttonDownload.setOnClickListener(this);
        }

        Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                setWallpaper(wallpaperList.get(getAdapterPosition()));
            }
        };
        @Override
        public void onClick(View view) {

            switch (view.getId()){
                case R.id.image_view:
                    Intent intent = new Intent(mCtx,Single_wallpaper_popup.class);
                    Wallpaper w = wallpaperList.get(getAdapterPosition());
                    intent.putExtra("id",w.id);
                    intent.putExtra("wallpaper",w.wallpaper);
                    intent.putExtra("title",w.title);
                    mCtx.startActivity(intent);
                    ((Activity)mCtx).overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
                    break;
                case R.id.set_wall:
                    setWall.setEnabled(false);
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
                            handler.sendEmptyMessage(0);
                        }
                    });
                    break;
                case R.id.button_share:
                    shareWallpaper(wallpaperList.get(getAdapterPosition()));
                    break;
                case R.id.button_download:
                    downloadWallpaper(wallpaperList.get(getAdapterPosition()));
                    break;
            }
        }

        private void shareWallpaper(Wallpaper w){
            ((Activity) mCtx).findViewById(R.id.progressbar).setVisibility(View.VISIBLE);

            Glide.with(mCtx)
                    .asBitmap()
                    .load(w.wallpaper)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            ((Activity) mCtx).findViewById(R.id.progressbar).setVisibility(View.GONE);

                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("image/*");
                            intent.putExtra(Intent.EXTRA_STREAM,getLocalBitmapUri(resource));

                            mCtx.startActivity(Intent.createChooser(intent,"Wallpapers World"));
                        }
                    });
        }

        private Uri getLocalBitmapUri(Bitmap bmp){
            Uri bmpUri = null;
            try {
                File file = new File(mCtx.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "wallpapersWorld"+System.currentTimeMillis()+".png");
                FileOutputStream out = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG,90,out);
                out.close();
                bmpUri = Uri.fromFile(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bmpUri;
        }

        public void setWallpaper(final Wallpaper w){
            ((Activity) mCtx).findViewById(R.id.progressbar).setVisibility(View.VISIBLE);

            Glide.with(mCtx)
                    .asBitmap()
                    .load(w.wallpaper)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            ((Activity) mCtx).findViewById(R.id.progressbar).setVisibility(View.GONE);

                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            Uri uri = saveWallpaperAndGetUri(resource,w.id);

                            if(uri != null){
                                WallpaperManager wallpaperManager = WallpaperManager.getInstance(mCtx);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    File wallFile = new File(uri.getPath());
                                    Uri contentURI = UsefulFunctions.getImageContentUri(mCtx, wallFile);
                                    try {
                                        mCtx.startActivity(wallpaperManager.getCropAndSetWallpaperIntent(contentURI));
                                    }catch (Exception e){
                                    }
                                } else {
                                    try {
                                        wallpaperManager.setStream(mCtx.getContentResolver().openInputStream(uri));
                                    } catch (Exception e) {
                                    }
                                }
                            }
                        }
                    });
            setWall.setEnabled(true);
        }

        public void downloadWallpaper(final Wallpaper w){
            ((Activity) mCtx).findViewById(R.id.progressbar).setVisibility(View.VISIBLE);

            Glide.with(mCtx)
                    .asBitmap()
                    .load(w.wallpaper)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            ((Activity) mCtx).findViewById(R.id.progressbar).setVisibility(View.GONE);

                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            Uri uri = saveWallpaperAndGetUri(resource,w.id);

                            if(uri != null){
                                intent.setDataAndType(uri,"image/*");
                                mCtx.startActivity(Intent.createChooser(intent,"Wallpapers World"));
                            }
                        }
                    });
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
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            if(FirebaseAuth.getInstance().getCurrentUser() == null){
                Toast.makeText(mCtx, "Please Login First", Toast.LENGTH_LONG).show();
                compoundButton.setChecked(false);
                return;
            }

            int position = getAdapterPosition();
            Wallpaper w = wallpaperList.get(position);

            DatabaseReference dbfavs = FirebaseDatabase.getInstance().getReference("users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child("favourites")
                    .child(w.category);

            if(b){
                dbfavs.child(w.id).setValue(w);
            }else {
                dbfavs.child(w.id).setValue(null);
            }
        }
    }

}
