package com.example.travelguide.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.travelguide.R;
import com.example.travelguide.model.TravelGuide;

import java.util.List;

/**
 * 攻略列表适配器
 */
public class GuideAdapter extends RecyclerView.Adapter<GuideAdapter.ViewHolder> {

    private List<TravelGuide> guideList;
    private OnItemClickListener listener;

    public GuideAdapter(List<TravelGuide> guideList, OnItemClickListener listener) {
        this.guideList = guideList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_guide, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TravelGuide guide = guideList.get(position);
        holder.bind(guide, listener);
    }

    @Override
    public int getItemCount() {
        return guideList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(TravelGuide guide, int position);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAuthor, tvViews, tvLikes, tvDuration;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvViews = itemView.findViewById(R.id.tvViews);
            tvLikes = itemView.findViewById(R.id.tvLikes);
            tvDuration = itemView.findViewById(R.id.tvDuration);
        }

        void bind(TravelGuide guide, OnItemClickListener listener) {
            tvTitle.setText(guide.getTitle());
            tvAuthor.setText("作者: " + guide.getAuthor());
            tvViews.setText("浏览: " + guide.getViews());
            tvLikes.setText("点赞: " + guide.getLikes());
            tvDuration.setText("行程: " + (guide.getTravelDuration() != null ? guide.getTravelDuration() : "未知"));

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(guide, getAdapterPosition());
                }
            });
        }
    }
}
