package com.example.shareall.RecyclerViewAdapter;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shareall.Interface.DeviceSelection;
import com.example.shareall.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    Context context;
     DeviceSelection selection;
    List<WifiP2pDevice> list;

    public RecyclerViewAdapter(Context context, List<WifiP2pDevice> list, DeviceSelection selection) {
        this.context = context;
        this.list = list;
        this.selection=selection;
    }



    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);

        return new ViewHolder(view);
    }

    @Override

    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {

        holder.name.setText(list.get(position).deviceName.toString());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name=itemView.findViewById(R.id.peer_name);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            selection.onDeviceSelected(list.get(getAdapterPosition()));
        }
    }
    public void update(List<WifiP2pDevice> list){
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }
}
