package app.deepakbharti.com.wallpapersworld.activities;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.MobileAds;

import java.util.Calendar;

import app.deepakbharti.com.wallpapersworld.R;
import app.deepakbharti.com.wallpapersworld.fragments.DownloadedWallpapersFragment;
import app.deepakbharti.com.wallpapersworld.fragments.FavouritesFragment;
import app.deepakbharti.com.wallpapersworld.fragments.HomeFragment;
import app.deepakbharti.com.wallpapersworld.fragments.SettingsFragment;
import app.deepakbharti.com.wallpapersworld.services.NotificationReciever;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener{

    Toolbar toolbar;
    BottomNavigationView bottomNavigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        MobileAds.initialize(this,"ca-app-pub-4951572640443447~3766559316");

        startNotificationDaily();

        initToolbar();

        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottoom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        displayFragment(new HomeFragment());
    }

    private void initToolbar(){
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.showOverflowMenu();
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Fragment fragment;
        switch (item.getItemId()){
            case R.id.nav_app_share:
                shareWallpapersWorldApplication();
                break;
            case R.id.nav_show_download_wall:
                fragment = new DownloadedWallpapersFragment();
                displayFragment(fragment);
                break;
            default:return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void shareWallpapersWorldApplication(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out this cool Wallpaper's World application at: https://play.google.com/store/apps/details?id=app.deepakbharti.com.wallpapersworld");
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void displayFragment(Fragment fragment){
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.content_area, fragment)
                .commit();
    }

    private void startNotificationDaily(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), NotificationReciever.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar alarmStartTime = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        alarmStartTime.set(Calendar.HOUR_OF_DAY, 8);
        alarmStartTime.set(Calendar.MINUTE, 00);
        alarmStartTime.set(Calendar.SECOND, 0);
        if (now.after(alarmStartTime)) {
            alarmStartTime.add(Calendar.DATE, 1);
        }

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, alarmStartTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment;
        switch (item.getItemId()){
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_favourites:
                fragment = new FavouritesFragment();
                break;
            case R.id.nav_settings:
                fragment = new SettingsFragment();
                break;
            default:
                fragment = new HomeFragment();
        }
        displayFragment(fragment);
        return true;
    }

}
