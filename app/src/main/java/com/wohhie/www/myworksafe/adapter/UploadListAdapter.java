package com.wohhie.www.myworksafe.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wohhie.www.myworksafe.R;

import java.util.List;

public class UploadListAdapter extends RecyclerView.Adapter<UploadListAdapter.ViewHolder> {

    public List<String> fileNameList;
    public List<String> fileDoneList;


    public UploadListAdapter(List<String> fileNameList, List<String> fileDoneList){
        this.fileNameList = fileNameList;
        this.fileDoneList = fileDoneList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.file_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        String filename = fileNameList.get(position);
        viewHolder.filenameView.setText(filename);
    }

    @Override
    public int getItemCount() {
        return fileNameList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        View view;
        public TextView filenameView;
        public ImageView fileDoneView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            filenameView = view.findViewById(R.id.file_name);
            fileDoneView = view.findViewById(R.id.file_done_check);

        }
    }
}
