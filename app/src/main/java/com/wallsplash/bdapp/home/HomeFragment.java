package com.wallsplash.bdapp.home;


import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.wallsplash.bdapp.MainActivity;
import com.wallsplash.bdapp.search.SearchFragment;
import com.wallsplash.bdapp.bean.PhotosBean;
import com.wallsplash.bdapp.bean.TrendingBean;
import com.wallsplash.bdapp.details.DetailFragment;
import com.wallsplash.bdapp.retrofit.Config;
import com.wallsplash.bdapp.retrofit.RestClient;
import com.wallsplash.bdapp.utils.SharedObjects;
import com.wallsplash.bdapp.wallsplash.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment implements NewPhotosAdapter.OnPhotoSelectedListner, TrendingAdapter.OnCategorySelectedListner, TrendingPhotoByIdAdapter.OnCategorybyidSelectedListner {


    private static final int EXTERNAL_STORAGE_PERMISSION_CONSTANT = 1;
    private static final int REQUEST_PERMISSION_SETTING = 0;
    Unbinder unbinder;
    @BindView(R.id.rvTrendingphotosbyId)
    RecyclerView rvTrendingphotosbyId;
    String trendId;
    private NewPhotosAdapter newPhotosAdapter;
    private ArrayList<PhotosBean> newPhotoslist = new ArrayList<>();

    private TrendingAdapter trendingAdapter;
    private ArrayList<TrendingBean> trendingList = new ArrayList<>();

    private TrendingPhotoByIdAdapter trendingPhotoByIdAdapter;
    private ArrayList<TrendingBean> trendingPhotosByIdList = new ArrayList<>();

    private ArrayList<PhotosBean> randomList = new ArrayList<>();
    private ProgressDialog progressDialog;
    String exploretitle;
    private AsyncTask mMyTask;
    private ProgressDialog mProgressDialog;
    private static final int PERMISSION_REQUEST_CODE = 1;
    String wantPermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private AdView mAdView;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        unbinder = ButterKnife.bind(this, view);
        mAdView = view.findViewById(R.id.adView);
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setTitle("AsyncTask");
        mProgressDialog.setMessage("Please wait, we are downloading your image file...");
        if (!checkPermission(wantPermission)) {
            requestPermission(wantPermission);
        }
        ProgressDialogSetup();
        getTrending();
        return view;
    }

    public void ProgressDialogSetup() {
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(getResources().getString(R.string.please_wait));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void loadFragment(Fragment fragment) {
        String backStateName = fragment.getClass().getName();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        boolean fragmentPopped = fragmentManager.popBackStackImmediate(backStateName, 0);
        if (!fragmentPopped) { //fragment not in back stack, create it.
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(R.id.fl_container, fragment, null);
            ft.hide(HomeFragment.this);
            ft.addToBackStack(backStateName);
            ft.commit();
        }
    }

    private boolean checkPermission(String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            int result = ContextCompat.checkSelfPermission(getActivity(), permission);
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private void requestPermission(String permission) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
            Toast.makeText(getActivity(), "Write external storage permission allows us to write data. \n" +
                    "                    Please allow in App Settings for additional functionality", Toast.LENGTH_LONG).show();
        }
        ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 /*   Toast.makeText(getActivity(), "Permission Granted. Now you can write data.",
                            Toast.LENGTH_LONG).show();*/
                } else {
                    Toast.makeText(getActivity(), "Permission Denied. You cannot write data.",
                            Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    private void getTrending() {

        progressDialog.show();
        Call<JsonElement> call1 = RestClient.post().getTrending(Config.unsplash_access_key);
        call1.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    trendingList.clear();
                    JSONArray jsonArr = null;
                    try {
                        jsonArr = new JSONArray(response.body().toString());


                        if (jsonArr.length() > 0) {
                            for (int i = 0; i < jsonArr.length(); i++) {
                                JSONObject json2 = jsonArr.getJSONObject(i);
                                String id = json2.getString("id");
                                String title = json2.getString("title");
                                trendingList.add(new TrendingBean(id, title, false));
                            }
                            bindCategoryAdapter();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                progressDialog.dismiss();

            }
        });

    }

    private void getTrendPhotosById() {

        Call<JsonElement> call1 = RestClient.post().getTrendingPhotosbyId(trendId, 1, 12, Config.unsplash_access_key);
        call1.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                //  progressDialog.dismiss();

                Log.e("FeatureNews", response.body().toString());
                if (response.isSuccessful()) {

                    trendingPhotosByIdList.clear();
                    JSONArray jsonArr = null;
                    try {
                        jsonArr = new JSONArray(response.body().toString());


                        if (jsonArr.length() > 0) {
                            for (int i = 0; i < jsonArr.length(); i++) {
                                JSONObject json2 = jsonArr.getJSONObject(i);
                                String id = json2.getString("id");

                                JSONObject object = json2.getJSONObject("urls");
                                String url = object.getString("regular");
                                trendingPhotosByIdList.add(new TrendingBean(id, url));
                            }
                            bindTrendPhotosByIdAdapternews();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                // progressDialog.dismiss();

            }
        });

    }

    private void bindCategoryAdapter() {
        if (trendingList.size() > 0) {
            trendingAdapter = new TrendingAdapter(getActivity(), trendingList);
//            rvTrending.setAdapter(trendingAdapter);
            trendId = trendingList.get(0).getId();
            trendingList.get(0).setSelected(true);
        }
        trendingAdapter.setOnCategorySelectedListner(this);
        trendingAdapter.notifyDataSetChanged();
        getTrendPhotosById();
    }

    private void bindTrendPhotosByIdAdapternews() {
        if (trendingPhotosByIdList.size() > 0) {
            trendingPhotoByIdAdapter = new TrendingPhotoByIdAdapter(getActivity(), trendingPhotosByIdList);
            trendingPhotoByIdAdapter.setOnCategorybyidSelectedListner(this);
            rvTrendingphotosbyId.setAdapter(trendingPhotoByIdAdapter);
        }
    }

    @Override
    public void setOnPhotoSelatedListner(int position, PhotosBean dataBean) {
        DetailFragment newsDetailsFragment = DetailFragment.newInstance(dataBean.getId());
        loadFragment(newsDetailsFragment);
        /*Bundle bundle=new Bundle();
        bundle.putString(Config.photoid,dataBean.getId());
        DetailFragment newsDetailsFragment  = new DetailFragment();
        newsDetailsFragment.setArguments(bundle);
        loadFragment(newsDetailsFragment);*/
    }

    @Override
    public void setOnCategorySelatedListner(int position, TrendingBean trendingBean) {
        for (int i = 0; i < trendingList.size(); i++) {
            trendingList.get(i).setSelected(false);
        }
        if (trendingList.size() > 0) {
            trendingAdapter.notifyDataSetChanged();
            trendId = trendingBean.getId();
            // setitem.setText(dataBean.getName());
            getTrendPhotosById();
            trendingBean.setSelected(true);
        }
    }

    @Override
    public void setOnCategorybyidSelatedListner(int position, TrendingBean trendingBean) {
        DetailFragment newsDetailsFragment = DetailFragment.newInstance(trendingBean.getId());
        // loadFragment(newsDetailsFragment);
        ((MainActivity) getActivity()).loadFragment(newsDetailsFragment);
       /* Bundle bundle=new Bundle();
        bundle.putString(Config.photoid,trendingBean.getId());
        DetailFragment newsDetailsFragment  = new DetailFragment();
        newsDetailsFragment.setArguments(bundle);
        loadFragment(newsDetailsFragment);*/
    }

}
