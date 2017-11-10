package com.mibaldi.playvideos;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mibaldi.playvideos.models.MyVideo;

import java.util.List;



public class VideoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<MyVideo> listItem;
    private OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClickListener(View view, String url);
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
        public Boolean exist= false;
        public Button descargar;
        public OnItemClickListener listener;


        public MyHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.info_text);
            descargar = (Button) itemView.findViewById(R.id.download);
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
            title.setText(url);



        }

        public void onClickItem() {
            this.listener.onItemClickListener(itemView, url);
        }
    }

    public class MyHolder2 extends RecyclerView.ViewHolder {


        public TextView title;
        public String url;
        public Boolean exist= false;
        public Button reproducir;
        public OnItemClickListener listener;


        public MyHolder2(View itemView, OnItemClickListener listener) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.info_text);
            reproducir = (Button) itemView.findViewById(R.id.play);
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
            title.setText(url);



        }
        public void onPlayItem() {
            this.listener.onPlayClickListener(itemView, url);
        }




    }

}
