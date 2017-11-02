package com.mibaldi.playvideos;

import android.content.Context;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mibaldi.playvideos.R;

import java.io.File;
import java.util.List;



public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.MyHolder> {

    private List<String> listItem;
    private OnItemClickListener listener;
    private Context mContext;

    public interface OnItemClickListener {
        void onItemClickListener(View view, String url);
        void onPlayClickListener(View view, String url);
    }


    public VideoAdapter(Context context) {
        mContext = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_text_view, parent, false);
        return new MyHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(MyHolder holder, int position) {
        holder.bindItem(listItem.get(position));
    }

    @Override
    public int getItemCount() {
        return this.listItem.size();
    }

    public void setDataAndListener(List<String> urlList, OnItemClickListener listener) {
        this.listItem = urlList;
        this.listener = listener;
        this.notifyDataSetChanged();
    }

    public class MyHolder extends RecyclerView.ViewHolder {


        public TextView title;
        public String url;
        public Button descargar,reproducir;
        public OnItemClickListener listener;


        public MyHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.info_text);
            descargar = (Button) itemView.findViewById(R.id.download);
            descargar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickItem();
                    reproducir.setEnabled(true);
                    reproducir.setVisibility(View.VISIBLE);
                }
            });
            reproducir = (Button) itemView.findViewById(R.id.play);
            reproducir.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onPlayItem();
                }
            });
            this.listener = listener;
        }

        public void bindItem(String url) {
            this.url = url;
            title.setText(url);

            if (fileExists(url)){
                reproducir.setEnabled(true);
                reproducir.setVisibility(View.VISIBLE);
                descargar.setVisibility(View.GONE);
            }

        }

        public void onClickItem() {
            this.listener.onItemClickListener(itemView, url);
        }
        public void onPlayItem() {
            this.listener.onPlayClickListener(itemView, url);
        }

        public boolean fileExists(String url){
            File file = new File(mContext.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),url+".mp4");
            return file.exists();
        }


    }

}
