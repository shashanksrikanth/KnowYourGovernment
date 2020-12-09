package com.shashanksrikanth.knowyourgovernment;

import android.view.View;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

public class OfficialViewHolder extends RecyclerView.ViewHolder{
    TextView officialTitle;
    TextView officialDetails;

    public OfficialViewHolder(View view) {
        super(view);
        officialTitle = view.findViewById(R.id.officialTitle);
        officialDetails = view.findViewById(R.id.officialDetails);
    }
}
