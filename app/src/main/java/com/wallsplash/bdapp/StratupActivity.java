package com.wallsplash.bdapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.wallsplash.bdapp.bean.User;
import com.wallsplash.bdapp.utils.SharedObjects;
import com.wallsplash.bdapp.wallsplash.R;

public class StratupActivity extends AppCompatActivity {

    @BindView(R.id.tvSkip)
    TextView tvSkip;
    @BindView(R.id.tvLogin)
    TextView tvLogin;
    @BindView(R.id.tvFbLogin)
    TextView tvFbLogin;
    @BindView(R.id.tvJoin)
    TextView tvJoin;
    //------------Facebook integration start--------------------------------

    String name;
    String location;
    String uid;
    SharedObjects sharedObjects;
    private DatabaseReference mDatabase;
    Uri photoUrl;
    StorageReference storageReference;
    StorageReference storageRef;
    FirebaseStorage storage;
    //------------Facebook integration end---------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stratup);
        ButterKnife.bind(this);
        sharedObjects = new SharedObjects(StratupActivity.this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        storageRef = storage.getReference();


        getFbKeyHash("com.wallsplash.bdapp");

    }





    //target to save
    private Target getTarget(final String uid) {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                uploadImage(uid, bitmapToBase64(bitmap));

                                // Stuff that updates the UI
                            }
                        });
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
        return target;
    }

    public static byte[] bitmapToBase64(Bitmap bitmap) {
        byte[] byteArray = new byte[0];
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byteArray = byteArrayOutputStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return byteArray;
    }

    private void uploadImage(final String uid, byte[] bytes) {

        if (photoUrl != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("images/" + uid);

            ref.putBytes(bytes)
                    /* ref.putFile(photoUrl)*/
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(StratupActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            sharedObjects.setUserID(uid);
                            Intent intent = new Intent(StratupActivity.this, MainActivity.class);
                            intent.putExtra("uid", uid);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(StratupActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    public void getFbKeyHash(String packageName) {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("YourKeyHash :", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                System.out.println("YourKeyHash: " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            photoUrl = data.getData();
            try {
                if (photoUrl == null) {
                    Toast.makeText(StratupActivity.this, "Please upload profile image", Toast.LENGTH_SHORT).show();
                } else {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUrl);
                    //  ivProfile.setImageBitmap(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnClick({R.id.tvSkip, R.id.tvLogin, R.id.tvFbLogin, R.id.tvJoin})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {

            case R.id.tvSkip:
                intent = new Intent(StratupActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);

                break;
            case R.id.tvLogin:

                break;
            case R.id.tvFbLogin:

                break;
            case R.id.tvJoin:

                break;
        }
    }
}
