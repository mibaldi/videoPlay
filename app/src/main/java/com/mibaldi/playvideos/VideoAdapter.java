package com.mibaldi.playvideos;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mibaldi.playvideos.models.MyVideo;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;



public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MyVideo> listItem;
    private OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClickListener(View view,MyVideo myVideo);
        void onPlayClickListener(View view, String url);
    }


    public VideoAdapter(){
    }

    @Override
    public int getItemViewType(int position) {
       return (!listItem.get(position).isDownload())? 0: 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_text_view, parent, false);
        View view2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_text_view2, parent, false);
        switch (viewType){
            case 0:
                return new MyHolder(view,listener);
            case 1:
                return new MyHolder2(view2,listener);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 0:
                MyHolder viewHolder0 = (MyHolder)holder;
                viewHolder0.bindItem(listItem.get(position));
                break;

            case 1:
                MyHolder2 viewHolder2 = (MyHolder2)holder;
                viewHolder2.bindItem2(listItem.get(position));
                break;
        }
    }


    @Override
    public int getItemCount() {
        return this.listItem.size();
    }

    public void setDataAndListener(List<MyVideo> urlList, OnItemClickListener listener) {
        this.listItem = urlList;
        this.listener = listener;
        this.notifyDataSetChanged();
    }

    public class MyHolder extends RecyclerView.ViewHolder {


        public TextView title;
        public String url;
        public String fullUrl;
        public Boolean exist= false;
        public ImageButton descargar;
        public OnItemClickListener listener;

        public MyVideo myVideo;


        public MyHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.info_text);
            descargar = (ImageButton) itemView.findViewById(R.id.download);
            descargar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickItem();
                }
            });
            this.listener = listener;
        }

        public void bindItem(MyVideo myVideo) {
            this.url = myVideo.getUrl();
            this.exist = myVideo.isDownload();
            this.fullUrl = myVideo.getFullUrl();
            this.myVideo = myVideo;
            title.setText(myVideo.getName());
        }

        public void onClickItem() {
            this.listener.onItemClickListener(itemView,myVideo);
        }
    }

    public class MyHolder2 extends RecyclerView.ViewHolder {


        public TextView title;
        public String url;
        public Boolean exist= false;
        public ImageButton reproducir;
        public OnItemClickListener listener;


        public MyHolder2(View itemView, OnItemClickListener listener) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.info_text);
            reproducir = (ImageButton) itemView.findViewById(R.id.play);
            reproducir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPlayItem();
                }
            });
            this.listener = listener;
        }

        public void bindItem2(MyVideo myVideo) {
            this.url = myVideo.getUrl();
            this.exist = myVideo.isDownload();
            title.setText(myVideo.getName());



        }
        public void onPlayItem() {
            this.listener.onPlayClickListener(itemView, url);
        }

    }
    public void sortAndNotify(){
        Collections.sort(listItem, new Comparator<MyVideo>() {
            @Override
            public int compare(MyVideo o1, MyVideo o2) {
                return (Integer.parseInt(o1.getUrl()) > Integer.parseInt(o2.getUrl()))? 0: -1;
            }
        });
        this.notifyDataSetChanged();
    }
}
