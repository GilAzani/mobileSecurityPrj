package fragments;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.racehw1.R;
import com.example.racehw1.RankAdapter;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import callbacks.OnClickCallback;
import model.RecordHolder;

public class ListFragment extends Fragment {

    //private MaterialTextView [] leaderboard_LBL_record;
    private ArrayList<RecordHolder> recordHolders;
    private OnClickCallback onClickCallback;
    private RecyclerView leaderboard_LST_ranking;

    private RankAdapter rankAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list,container,false);
        initViews(view);


        return view;

    }

    public void setCallback(OnClickCallback callback) {
         onClickCallback= callback;
        rankAdapter = new RankAdapter(callback, recordHolders);
        leaderboard_LST_ranking.setAdapter(rankAdapter);
    }

    private void initViews(View view) {

        leaderboard_LST_ranking = view.findViewById(R.id.leaderboard_LST_ranking);


        leaderboard_LST_ranking.setLayoutManager(new LinearLayoutManager(view.getContext()));

    }

    public void setList(ArrayList<RecordHolder> recordHolders) {
        this.recordHolders = recordHolders;
    }
}