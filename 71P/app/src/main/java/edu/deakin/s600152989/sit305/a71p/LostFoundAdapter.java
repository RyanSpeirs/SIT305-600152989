package edu.deakin.s600152989.sit305.a71p;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class LostFoundAdapter extends ListAdapter<LostFoundItem, LostFoundAdapter.LostFoundViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(LostFoundItem item);
    }

    private final OnItemClickListener listener;

    public LostFoundAdapter(OnItemClickListener listener) {
        super(DIFF_CALLBACK);
        this.listener = listener;
    }

    private static final DiffUtil.ItemCallback<LostFoundItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<LostFoundItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull LostFoundItem oldItem, @NonNull LostFoundItem newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull LostFoundItem oldItem, @NonNull LostFoundItem newItem) {
            return oldItem.equals(newItem);
        }
    };

    @NonNull
    @Override
    public LostFoundViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lost_found, parent, false);
        return new LostFoundViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LostFoundViewHolder holder, int position) {
        LostFoundItem currentItem = getItem(position);
        holder.titleTextView.setText(currentItem.getTitle());
        holder.dateTextView.setText(currentItem.getDate());
        holder.statusTextView.setText(currentItem.getType());

        // Set the color based on the status
        if ("Found".equalsIgnoreCase(currentItem.getType())) {
            holder.statusTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.blue));  // Found items will be blue
        } else {
            holder.statusTextView.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));  // Lost items will be red
        }

        // Set click listener
        holder.itemView.setOnClickListener(v -> listener.onItemClick(currentItem));
    }


    public static class LostFoundViewHolder extends RecyclerView.ViewHolder {
        private final TextView titleTextView;
        private final TextView dateTextView;
        private final TextView statusTextView;

        public LostFoundViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.itemTitle);
            dateTextView = itemView.findViewById(R.id.itemDate);
            statusTextView = itemView.findViewById(R.id.itemStatus);
        }
    }
}
