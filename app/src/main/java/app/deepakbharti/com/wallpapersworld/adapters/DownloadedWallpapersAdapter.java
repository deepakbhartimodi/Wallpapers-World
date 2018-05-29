package app.deepakbharti.com.wallpapersworld.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import app.deepakbharti.com.wallpapersworld.R;
import app.deepakbharti.com.wallpapersworld.activities.Single_wallpaper_popup;
import app.deepakbharti.com.wallpapersworld.models.Wallpaper;

public class DownloadedWallpapersAdapter extends RecyclerView.Adapter<DownloadedWallpapersAdapter.WallViewHolder> {

    private Context mCtx;
    private List<Wallpaper> wallpaperList;
    private BottomNavigationView bottomNavigationView;

    public DownloadedWallpapersAdapter(Context mCtx, List<Wallpaper> wallpaperList) {
        this.mCtx = mCtx;
        this.wallpaperList = wallpaperList;
    }

    @NonNull
    @Override
    public WallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recylesviews_download_wallpapers, parent, false);
        return new WallViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WallViewHolder holder, int position) {
        Wallpaper w = wallpaperList.get(position);
        Glide.with(mCtx)
                .load(w.wallpaper)
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return wallpaperList.size();
    }

    class WallViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imageView;
        Button setWall;
        ImageButton buttonDelete;

        public WallViewHolder(View itemView) {
            super(itemView);

            imageView = itemView.findViewById(R.id.image_view);
            setWall = itemView.findViewById(R.id.set_wall);
            buttonDelete = itemView.findViewById(R.id.button_delete);

            imageView.setOnClickListener(this);
            setWall.setOnClickListener(this);
            buttonDelete.setOnClickListener(this);
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
                    intent.putExtra("wallpaper",w.wallpaper);
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
                case R.id.button_delete:
                    boolean flag = deleteWallpaper(wallpaperList.get(getAdapterPosition()));
                    if(flag){
                        removeAt(getAdapterPosition());
                    }
                    break;
            }
        }

        private void setWallpaper(final Wallpaper w){
            ((Activity) mCtx).findViewById(R.id.progressbar).setVisibility(View.VISIBLE);

            Glide.with(mCtx)
                    .asBitmap()
                    .load(w.wallpaper)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            ((Activity) mCtx).findViewById(R.id.progressbar).setVisibility(View.GONE);

                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            Uri uri = Uri.parse(w.wallpaper);

                            if(uri != null){
                                Intent i = new Intent(Intent.ACTION_ATTACH_DATA);
                                i.setDataAndType(uri, "image/*");
                                i.putExtra("mimeType", "image/*");
                                ((Activity) mCtx).startActivityForResult(Intent.createChooser(i,"Set as:"),200);
                            }
                        }
                    });
            setWall.setEnabled(true);
        }

        private boolean deleteWallpaper(final Wallpaper w){
            boolean flag = false;
            Uri uri = Uri.parse(w.wallpaper);
            File file = new File(uri.getPath());
            if(file.exists()){
                if(file.delete()){
                    Toast.makeText(mCtx, "Wallpaper Deleted", Toast.LENGTH_SHORT).show();
                    flag = true;
                }else {
                    Toast.makeText(mCtx, "Wallpaper not Deleted", Toast.LENGTH_SHORT).show();
                    flag = false;
                }
            }
            return flag;
        }

        private void removeAt(int position) {
            wallpaperList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, wallpaperList.size());
            if(wallpaperList.size() == 0){
                ((Activity)mCtx).findViewById(R.id.msg).setVisibility(View.VISIBLE);
            }
        }

    }
}
