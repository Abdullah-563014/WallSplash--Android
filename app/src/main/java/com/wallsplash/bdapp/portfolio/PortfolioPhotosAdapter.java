package com.wallsplash.bdapp.portfolio;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;

import com.wallsplash.bdapp.bean.PhotosBean;
import com.wallsplash.bdapp.bean.PortfolioBean;
import com.wallsplash.bdapp.wallsplash.R;

public class PortfolioPhotosAdapter extends RecyclerView.Adapter<PortfolioPhotosAdapter.RecyclerVH> {
    Context context;
    ArrayList<PortfolioBean> relatedList = new ArrayList<>();
    OnPhotoSelectedListner onPhotoSelectedListner;
    PhotosBean catbean;



    public PortfolioPhotosAdapter(Context context, ArrayList<PortfolioBean> portfolioBeans) {
        this.context = context;
        this.relatedList = portfolioBeans;
    }

    public void setOnCategorySelectedListner(OnPhotoSelectedListner onPhotoSelectedListner) {

        this.onPhotoSelectedListner = onPhotoSelectedListner;
    }

    public interface OnPhotoSelectedListner {
        void setOnPhotoSelatedListner(int position, PortfolioBean portfolioBean);

    }

    @Override
    public RecyclerVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_related, parent, false);
        return new RecyclerVH(v);
    }

    @Override
    public void onBindViewHolder(final RecyclerVH holder, final int position) {
      //  holder.tvTitle.setText(Html.fromHtml(arrCateList.get(position).getTitle()));
        Glide.with(context).load(relatedList.get(position).getRegular())
                .thumbnail(0.5f)
                .placeholder(R.drawable.ic_placeholder_photos)
                .error(R.drawable.ic_placeholder_photos)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.imgview);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            //  private RecyclerView rvevenment;
            @Override
            public void onClick(View view) {
                onPhotoSelectedListner.setOnPhotoSelatedListner(position, relatedList.get(position));

            }
        });
    }

    @Override
    public int getItemCount() {
        return relatedList.size();
    }


    public class RecyclerVH extends RecyclerView.ViewHolder {
        ImageView imgview;


        public RecyclerVH(View itemView) {
            super(itemView);
            imgview = (ImageView) itemView.findViewById(R.id.imgview);

        }
    }
}