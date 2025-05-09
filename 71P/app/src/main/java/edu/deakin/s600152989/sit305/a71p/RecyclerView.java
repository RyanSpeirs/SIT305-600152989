package edu.deakin.s600152989.sit305.a71p;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.List;

public class LostFoundAdapter extends ListAdapter<LostFoundItem, LostFoundAdapter.LostFoundViewHolder> {

    protected LostFoundAdapter() {
        super(DIFF_CALLBACK);
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
        holder.descriptionTextView.setText(currentItem.getDescription());
    }

    public static class LostFoundViewHolder extends RecyclerView.ViewHolder {
        private final MaterialTextView titleTextView;
        private final MaterialTextView descriptionTextView;

        public LostFoundViewHolder(View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            descriptionTextView = itemView.findViewById(R.id.descriptionTextView);
        }
    }
}