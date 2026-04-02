package com.example.travelguide.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelguide.R;
import com.example.travelguide.model.Destination;

import java.util.List;

/**
 * 目的地列表适配器
 */
public class DestinationAdapter extends RecyclerView.Adapter<DestinationAdapter.ViewHolder> {

    private List<Destination> destinationList;
    private OnItemClickListener listener;

    public DestinationAdapter(List<Destination> destinationList, OnItemClickListener listener) {
        this.destinationList = destinationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_destination, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Destination destination = destinationList.get(position);
        holder.bind(destination, listener);
    }

    @Override
    public int getItemCount() {
        return destinationList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(Destination destination, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvLocation, tvRating, tvDescription;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvDescription = itemView.findViewById(R.id.tvDescription);
        }

        void bind(Destination destination, OnItemClickListener listener) {
            tvName.setText(destination.getName());
            tvLocation.setText(destination.getLocation());
            tvRating.setText(String.format("评分: %.1f", destination.getRating()));
            tvDescription.setText(destination.getDescription());

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(destination, getAdapterPosition());
                }
            });
        }
    }
}
