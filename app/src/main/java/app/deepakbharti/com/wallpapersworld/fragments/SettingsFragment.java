package app.deepakbharti.com.wallpapersworld.fragments;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import app.deepakbharti.com.wallpapersworld.R;
import app.deepakbharti.com.wallpapersworld.services.SensorService;

public class SettingsFragment extends Fragment {

    private static final int GOOGLE_SIGN_IN_CODE = 212;
    private GoogleSignInClient mGoogleSignInClient;
    BottomNavigationView bottomNavigationView;
    private DatabaseReference dbChangeWall;
    String changeWall;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            return inflater.inflate(R.layout.fragment_settings_default, container, false);
        }
        return inflater.inflate(R.layout.fragment_settings_logged_in, container, false);
    }


    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GoogleSignInOptions gso =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            ImageView imageView = view.findViewById(R.id.image_view);
            TextView textViewName = view.findViewById(R.id.username);
            TextView textViewEmail = view.findViewById(R.id.email);
            Button startChangeWall = view.findViewById(R.id.start_change_wall);

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            Glide.with(getActivity()).load(user.getPhotoUrl().toString()).into(imageView);
            textViewName.setText(user.getDisplayName());
            textViewEmail.setText(user.getEmail());

            view.findViewById(R.id.logout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseAuth.getInstance().signOut();
                    mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_area,new SettingsFragment())
                                    .commit();
                        }
                    });
                }
            });

            dbChangeWall = FirebaseDatabase.getInstance().getReference("users")
                    .child(user.getUid())
                    .child("settings")
                    .child("change_wall_daily");

            dbChangeWall.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.exists()){
                        changeWall = dataSnapshot.getValue(String.class);
                    }else{
                        changeWall = "No";
                    }
                    Button btn = view.findViewById(R.id.start_change_wall);
                    btn.setText(changeWall);
                    //startChangeWall.setText(changeWall);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            startChangeWall.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    if(changeWall.equals("No")){
                        dbChangeWall.setValue("Yes");
                        getActivity().startService(new Intent(getActivity(), SensorService.class));

                        Calendar cal = Calendar.getInstance();
                        Intent intent = new Intent(getActivity(), SensorService.class);
                        PendingIntent pintent = PendingIntent
                                .getService(getActivity(), 0, intent, 0);

                        AlarmManager alarm = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
                        // Start service every hour
                        alarm.setRepeating(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),
                                216000*1000, pintent);
                        Button btn = view.findViewById(R.id.start_change_wall);
                        btn.setText("Yes");
                        //startChangeWall.setText("Yes");
                    }else{
                        getActivity().stopService(new Intent(getActivity(), SensorService.class));

                        Intent intent = new Intent(getActivity(), SensorService.class);
                        PendingIntent pintent = PendingIntent
                                .getService(getActivity(), 0, intent, 0);

                        AlarmManager alarm = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
                        alarm.cancel(pintent);
                        dbChangeWall.setValue("No");
                        Button btn = view.findViewById(R.id.start_change_wall);
                        btn.setText("No");
                        //startChangeWall.setText("No");
                    }
                }
            });
        } else {
            view.findViewById(R.id.button_google_sign_in).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(intent, GOOGLE_SIGN_IN_CODE);
                }
            });
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN_IN_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);

        mAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(),
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            bottomNavigationView = (BottomNavigationView)getActivity().findViewById(R.id.bottoom_navigation);
                            int selectedItemId = bottomNavigationView.getSelectedItemId();
                            MenuItem selectedItem = bottomNavigationView.getMenu().findItem(selectedItemId);

                            if(selectedItem.toString().equals("Favourites")){
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.content_area,new FavouritesFragment())
                                        .commit();
                            }else{
                                getActivity().getSupportFragmentManager().beginTransaction()
                                        .replace(R.id.content_area,new SettingsFragment())
                                        .commit();
                            }
                        } else {
                            Toast.makeText(getActivity(), "Login Failure", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}