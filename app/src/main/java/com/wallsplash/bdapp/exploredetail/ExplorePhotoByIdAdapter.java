package com.wallsplash.bdapp.exploredetail;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import com.wallsplash.bdapp.bean.ExploreBean;
import com.wallsplash.bdapp.wallsplash.R;

public class ExplorePhotoByIdAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    Context c;
    private LayoutInflater inflater;
    ArrayList<ExploreBean> exploreList = new ArrayList<>();
    OnCategorybyidSelectedListner onCategorybyidSelectedListner;
    ExploreBean exploreBean;
    RecyclerView mRecyclerView;
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private OnLoadMoreListener mOnLoadMoreListener;
    public boolean isLoading;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    public interface OnLoadMoreListener {
        void onLoadMore();
    }
    public ExplorePhotoByIdAdapter(final Context c, ArrayList<ExploreBean> exploreList, RecyclerView recyclerView) {
        this.c = c;
        this.exploreList = exploreList;
        this.mRecyclerView = recyclerView;
        inflater = LayoutInflater.from(c);

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }
            }
        });

    }
    @Override
    public int getItemViewType(int position) {
        return exploreList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }
    public void setOnCategorybyidSelectedListner(OnCategorybyidSelectedListner onCategorybyidSelectedListner) {

        this.onCategorybyidSelectedListner = onCategorybyidSelectedListner;
    }

    public interface OnCategorybyidSelectedListner {
        void setOnCategorybyidSelatedListner(int position, ExploreBean exploreBean);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        if (viewType == VIEW_TYPE_ITEM) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_explorephotosbyid, parent, false);
            return new MyViewholder(view);
        } else if (viewType == VIEW_TYPE_LOADING) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_loading_item, parent, false);
            return new LoadingViewHolder(view);
        }
        return null;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        //holder.tvTrendingName.setText(Html.fromHtml(trendingList.get(position).getTitle()));

        if (holder instanceof MyViewholder) {
            final MyViewholder myHolder = (MyViewholder) holder;
            Glide.with(c).load(exploreList.get(position).getRegular())
                    .thumbnail(0.5f)
                    .placeholder(R.drawable.ic_placeholder_photos)
                    .error(R.drawable.ic_placeholder_photos)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(myHolder.imgview);

//            String title=exploreList.get(position).getTitle();
//            if (title==null || TextUtils.isEmpty(title) || title.equalsIgnoreCase("null")){
//                title="No Title";
//            }
//            myHolder.title.setText(title);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                //  private RecyclerView rvevenment;
                @Override
                public void onClick(View view) {
                    onCategorybyidSelectedListner.setOnCategorybyidSelatedListner(position, exploreList.get(position));

                }
            });
        } else {

            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);

            if(!isLoading)
                ((LoadingViewHolder) holder).progressBar.setVisibility(View.GONE);
        }




    }

    @Override
    public int getItemCount() {
        return exploreList.size();
    }


    public class MyViewholder extends RecyclerView.ViewHolder {
        ImageView imgview;
//        TextView title;


        public MyViewholder(View itemView) {
            super(itemView);
            imgview = (ImageView) itemView.findViewById(R.id.imgview);
//            title=itemView.findViewById(R.id.trendingPhotoByIdTitleTextViewId);
        }
    }


    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar1);
        }
    }

    public void setLoaded() {
        isLoading = false;
    }
}