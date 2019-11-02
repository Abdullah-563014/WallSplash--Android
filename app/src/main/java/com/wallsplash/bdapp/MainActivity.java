package com.wallsplash.bdapp;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.StrictMode;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import com.wallsplash.bdapp.explore.ExploreFragment;
import com.wallsplash.bdapp.exploredetail.ExploreDetailFragment;
import com.wallsplash.bdapp.favourite.FavouriteFragment;
import com.wallsplash.bdapp.home.HomeFragment;
import com.wallsplash.bdapp.retrofit.Config;
import com.wallsplash.bdapp.utils.AppUtils;
import com.wallsplash.bdapp.utils.SharedObjects;
import com.wallsplash.bdapp.wallsplash.BuildConfig;
import com.wallsplash.bdapp.wallsplash.R;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.ivDrawer)
    ImageView ivDrawer;
    @BindView(R.id.txtX)
    TextView txtX;
    @BindView(R.id.ivProfile)
    CircleImageView ivProfile;
    @BindView(R.id.ivlogout)
    ImageView ivlogout;
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
        ivProfile.setVisibility(View.VISIBLE);
        ivlogout.setVisibility(View.GONE);
        bottomNavigation.setOnNavigationItemSelectedListener(this);


        ivProfile.setVisibility(View.VISIBLE);
        Intent intent = getIntent();


//        getprofileImage();
    }

    //sign out method
//    private void getprofileImage() {
//
//        storage = FirebaseStorage.getInstance();
//
//        uid = "images/" + sharedObjects.getUserID();
//        // if (intent.hasExtra("uid")) {
//        //   String uid = intent.getStringExtra("uid");
//        storageRef = storage.getReferenceFromUrl(Config.DatabasePATH).child((uid));
//        // }
//
//
//        try {
//            final File localFile = File.createTempFile("images", "jpg");
//            storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
//                    bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
//                    ivProfile.setImageBitmap(bitmap);
//
//
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception exception) {
//                    exception.printStackTrace();
//                }
//            });
//        } catch (IOException e) {
//        }
//    }

    @OnClick({R.id.ivDrawer, R.id.ivlogout, R.id.bottom_navigation})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivDrawer:

                break;
            case R.id.ivlogout:
                sharedObjects.setCode(0);
                Intent intent = new Intent(MainActivity.this, StratupActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
                finish();
                break;
            case R.id.bottom_navigation:
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            // Home
            case R.id.navigation_home:
//                getprofileImage();
                ivlogout.setVisibility(View.GONE);
                ivProfile.setVisibility(View.VISIBLE);
                loadFragment(new HomeFragment());
                break;

            case R.id.navigation_hot:
//                getprofileImage();
                ivlogout.setVisibility(View.GONE);
                ivProfile.setVisibility(View.VISIBLE);
                loadFragment(new ExploreDetailFragment());
                break;

            case R.id.navigation_popular:
//                getprofileImage();
                ivlogout.setVisibility(View.GONE);
                ivProfile.setVisibility(View.VISIBLE);
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
//            getprofileImage();
            ivlogout.setVisibility(View.VISIBLE);
            ivProfile.setVisibility(View.GONE);
        }else if (resultCode==2){
//            getprofileImage();
            ivlogout.setVisibility(View.GONE);
            ivProfile.setVisibility(View.VISIBLE);
            loadFragment(new FavouriteFragment());
        }else if (resultCode==0) {
//            getprofileImage();
            ivProfile.setVisibility(View.VISIBLE);
            ivlogout.setVisibility(View.GONE);
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
        ivProfile.setVisibility(View.VISIBLE);
        ivlogout.setVisibility(View.GONE);
//        getprofileImage();
        FragmentManager fragmentManager = getSupportFragmentManager();
        // fragmentManager.popBackStackImmediate();
        if (fragmentManager.getBackStackEntryCount() == 1) {
//            getprofileImage();
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

}
