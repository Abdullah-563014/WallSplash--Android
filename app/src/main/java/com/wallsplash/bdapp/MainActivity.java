package com.wallsplash.bdapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.wallsplash.bdapp.exploredetail.ExploreDetailFragment;
import com.wallsplash.bdapp.favourite.FavouriteFragment;
import com.wallsplash.bdapp.home.HomeFragment;
import com.wallsplash.bdapp.utils.AppUtils;
import com.wallsplash.bdapp.utils.SharedObjects;
import com.wallsplash.bdapp.wallsplash.R;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.txtX)
    TextView txtX;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.AppBar)
    LinearLayout AppBar;
    @BindView(R.id.fl_container)
    FrameLayout flContainer;
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigation;

    boolean clickAgainToExit = false;
    FirebaseStorage storage;
    private StorageReference storageRef;
    private String userId;
    private String username;
    SharedObjects sharedObjects;
    String uid;
    Bitmap bitmap;
    private boolean activityStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


        // OneSignal.setSubscription(true);
        if (activityStarted
                && getIntent() != null
                && (getIntent().getFlags() & Intent.FLAG_ACTIVITY_REORDER_TO_FRONT) != 0) {
            finish();
            return;
        }

        activityStarted = true;


        sharedObjects = new SharedObjects(MainActivity.this);
        loadFragment(new HomeFragment());
        bottomNavigation.setOnNavigationItemSelectedListener(this);


        Intent intent = getIntent();


    }



    @OnClick({R.id.bottom_navigation})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bottom_navigation:
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            // Home
            case R.id.navigation_home:
                loadFragment(new HomeFragment());
                break;

            case R.id.navigation_hot:
                loadFragment(new ExploreDetailFragment());
                break;

            case R.id.navigation_popular:
                loadFragment(new FavouriteFragment());

                break;
            case R.id.navigation_share:
                shareMyApplication();
                break;
        }
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1) {
        }else if (resultCode==2){
            loadFragment(new FavouriteFragment());
        }else if (resultCode==0) {
            loadFragment(new HomeFragment());
        }
    }

    public void loadFragment(Fragment fragment) {

        String backStateName = fragment.getClass().getName();
        String fragmentTag = backStateName;

        FragmentManager manager = getSupportFragmentManager();
        boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

        if (!fragmentPopped && manager.findFragmentByTag(fragmentTag) == null) { //fragment not in back stack, create it.
            FragmentTransaction ft = manager.beginTransaction();
            ft.replace(R.id.fl_container, fragment, fragmentTag);
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
      //  drawer.closeDrawers();
    }

    public void onBackPressed() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() == 1) {
            if (clickAgainToExit) {

                super.onBackPressed();
                finish();
                return;
            }
            clickAgainToExit = true;
            AppUtils.ShortToast(MainActivity.this, getResources().getString(R.string.app_backpress));
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    clickAgainToExit = false;
                }
            }, 2000);
        } else {
//            getprofileImage();
            super.onBackPressed();
        }
    }


    private void shareMyApplication() {
        try {
            StrictMode.VmPolicy.Builder builder=new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            PackageManager pm = getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(getPackageName(), 0);
            File srcFile = new File(ai.publicSourceDir);
            Intent share = new Intent();
            share.setAction(Intent.ACTION_SEND);
            share.setType("*/*");
            share.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(srcFile));
            startActivity(Intent.createChooser(share, "Share App"));
        } catch (Exception e) {
            Toast.makeText(this, "failed for "+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!SharedObjects.haveInternet(getApplicationContext())){
            noInternetAlertDialog();
        }
    }

    private void noInternetAlertDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder(this)
                .setCancelable(false)
                .setMessage("Sorry, You have no any internet connection. Please check your internet connection and try again.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        if (fragmentManager.getBackStackEntryCount() == 1){
                            MainActivity.super.onBackPressed();
                            finish();
                        }else {
                            finish();
                        }
                    }
                });
        AlertDialog alertDialog=builder.create();
        if (!isFinishing()){
            alertDialog.show();
        }
    }
}
