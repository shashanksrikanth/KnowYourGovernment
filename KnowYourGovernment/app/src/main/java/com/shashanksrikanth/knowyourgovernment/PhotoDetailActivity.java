package com.shashanksrikanth.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

public class PhotoDetailActivity extends AppCompatActivity {

    TextView locationBarPhoto;
    TextView officialOfficePhotoLayout;
    TextView officialNamePhotoLayout;
    ImageView officialPicturePhotoLayout;
    ImageView officialPartyPhotoLayout;
    View layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);

        // Set instance variables
        locationBarPhoto = findViewById(R.id.locationBarPhoto);
        officialOfficePhotoLayout = findViewById(R.id.officialOfficePhotoLayout);
        officialNamePhotoLayout = findViewById(R.id.officialNamePhotoLayout);
        officialPicturePhotoLayout = findViewById(R.id.officialPicturePhotoLayout);
        officialPartyPhotoLayout = findViewById(R.id.officialPartyPictureLayout);
        layout = findViewById(R.id.photoLayout);

        // Extract intent
        Intent intent = getIntent();
        final Official official = (Official) intent.getSerializableExtra("official");

        locationBarPhoto.setText(intent.getStringExtra("locationBar"));
        officialOfficePhotoLayout.setText(official.getOffice());
        officialNamePhotoLayout.setText(official.getName());
        Picasso.get().load(official.getPhotoUrl()).error(R.drawable.missing).placeholder(R.drawable.placeholder).into(officialPicturePhotoLayout);
        if (official.getParty().equals("Democratic Party") || official.getParty().equals("Democratic")) {
            officialPartyPhotoLayout.setImageResource(R.drawable.dem_logo);
            layout.setBackgroundResource(R.color.colorDemocrat);
        } else if (official.getParty().equals("Republican Party") || official.getParty().equals("Republican")) {
            officialPartyPhotoLayout.setImageResource(R.drawable.rep_logo);
            layout.setBackgroundResource(R.color.colorRepublican);
        } else {
            officialPartyPhotoLayout.setVisibility(View.INVISIBLE);
            layout.setBackgroundResource(R.color.colorUnknown);
        }
    }
}