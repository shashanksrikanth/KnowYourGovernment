package com.shashanksrikanth.knowyourgovernment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class OfficialAdapter extends RecyclerView.Adapter<OfficialViewHolder>{
    private ArrayList<Official> officials;
    private MainActivity mainActivity;

    public OfficialAdapter(ArrayList<Official> officials, MainActivity mainActivity) {
        this.officials = officials;
        this.mainActivity = mainActivity;
    }

    @Override
    public OfficialViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View officialView = LayoutInflater.from(parent.getContext()).inflate(R.layout.official_list_row, parent, false);
        officialView.setOnClickListener(mainActivity);
        OfficialViewHolder holder = new OfficialViewHolder(officialView);
        return holder;
    }

    @Override
    public void onBindViewHolder(OfficialViewHolder holder, int position) {
        Official official = officials.get(position);
        String office = official.getOffice();
        String name = official.getName();
        String party = official.getParty();
        String nameAndParty = name + " (" + party + ")";
        holder.officialTitle.setText(office);
        holder.officialDetails.setText(nameAndParty);
    }

    @Override
    public int getItemCount() {
        return officials.size();
    }
}
