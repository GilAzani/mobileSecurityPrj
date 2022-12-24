package com.example.racehw1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import callbacks.OnClickCallback;
import model.RecordHolder;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.RankViewHolder> {
    private ArrayList<RecordHolder> recordHolders;
    private OnClickCallback onClickCallback;

    public RankAdapter(OnClickCallback onClickCallback, ArrayList<RecordHolder> recordHolders) {
        this.recordHolders = recordHolders;
        this.onClickCallback = onClickCallback;
    }

    public RankAdapter setOnClickCallback(OnClickCallback onClickCallback) {
        this.onClickCallback = onClickCallback;
        return this;
    }


    @NonNull
    @Override
    public RankViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_item, parent, false);
        RankViewHolder rankViewHolder = new RankViewHolder(view);
        return rankViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RankAdapter.RankViewHolder holder, int position) {
        RecordHolder recordHolder = getItem(position);
        holder.leaderboard_LBL_rank.setText("" + recordHolder.getRank());
        holder.leaderboard_LBL_name.setText(recordHolder.getName());
        holder.leaderboard_LBL_score.setText("" + recordHolder.getScore());
    }

    @Override
    public int getItemCount() {
        return recordHolders == null ? 0 : recordHolders.size();
    }

    private RecordHolder getItem(int position) {
        return recordHolders.get(position);
    }

    public class RankViewHolder extends RecyclerView.ViewHolder {
        private MaterialTextView leaderboard_LBL_rank;
        private MaterialTextView leaderboard_LBL_name;
        private MaterialTextView leaderboard_LBL_score;

        public RankViewHolder(@NonNull View itemView) {
            super(itemView);
            leaderboard_LBL_rank = itemView.findViewById(R.id.leaderboard_LBL_rank);
            leaderboard_LBL_name = itemView.findViewById(R.id.leaderboard_LBL_name);
            leaderboard_LBL_score = itemView.findViewById(R.id.leaderboard_LBL_score);
            itemView.setOnClickListener(v -> onClickCallback.focusOnPoint(getAdapterPosition()));
        }
    }

}
