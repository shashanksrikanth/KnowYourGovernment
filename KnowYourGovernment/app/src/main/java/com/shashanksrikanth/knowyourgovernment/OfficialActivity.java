package com.shashanksrikanth.knowyourgovernment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.squareup.picasso.Picasso;

public class OfficialActivity extends AppCompatActivity {

    TextView locationBarOfficial;
    TextView officialOffice;
    TextView officialName;
    TextView officialParty;
    ImageView officialImage;
    ImageView officialPartyImage;
    TextView officialAddress;
    TextView officialPhone;
    TextView officialEmail;
    TextView officialWebsite;
    ImageView officialFacebook;
    ImageView officialTwitter;
    ImageView officialYoutube;
    View layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official);

        // Set instance variables
        locationBarOfficial = findViewById(R.id.locationBarOfficial);
        officialOffice = findViewById(R.id.officialOfficePhotoLayout);
        officialName = findViewById(R.id.officialNamePhotoLayout);
        officialParty = findViewById(R.id.officialParty);
        officialImage = findViewById(R.id.officialImage);
        officialPartyImage = findViewById(R.id.officialPartyImage);
        officialAddress = findViewById(R.id.officialAddress);
        officialPhone = findViewById(R.id.officialPhone);
        officialEmail = findViewById(R.id.officialEmail);
        officialWebsite = findViewById(R.id.officialWebsite);
        officialFacebook = findViewById(R.id.officialFacebook);
        officialTwitter = findViewById(R.id.officialTwitter);
        officialYoutube = findViewById(R.id.officialYoutube);
        layout = findViewById(R.id.constraintLayout);

        // Extract official from intent
        Intent intent = getIntent();
        final Official official = (Official) intent.getSerializableExtra("official");

        locationBarOfficial.setText(intent.getStringExtra("locationBar"));
        assert official != null;
        officialOffice.setText(official.getOffice());
        officialName.setText(official.getName());
        officialParty.setText(official.getParty());
        if(!official.getPhotoUrl().equals("NULL"))
            Picasso.get().load(official.getPhotoUrl()).error(R.drawable.missing).placeholder(R.drawable.placeholder).into(officialImage);
        else officialImage.setImageResource(R.drawable.missing);
        if (official.getParty().equals("Democratic Party") || official.getParty().equals("Democratic")) {
            officialPartyImage.setImageResource(R.drawable.dem_logo);
            layout.setBackgroundResource(R.color.colorDemocrat);
        } else if (official.getParty().equals("Republican Party") || official.getParty().equals("Republican")) {
            officialPartyImage.setImageResource(R.drawable.rep_logo);
            layout.setBackgroundResource(R.color.colorRepublican);
        } else {
            officialPartyImage.setVisibility(View.INVISIBLE);
            layout.setBackgroundResource(R.color.colorUnknown);
        }
        officialAddress.setText(official.getAddress());
        Linkify.addLinks(officialAddress, Linkify.ALL);
        if(!official.getPhone().equals("NULL")) {
            officialPhone.setText(official.getPhone());
            Linkify.addLinks(officialPhone, Linkify.ALL);
        }
        if(!official.getEmail().equals("NULL")) {
            officialEmail.setText(official.getEmail());
            Linkify.addLinks(officialEmail, Linkify.ALL);
        }
        if (!official.getWebsite().equals("NULL")) {
            officialWebsite.setText(official.getWebsite());
            Linkify.addLinks(officialWebsite, Linkify.ALL);
        }
        if (!official.getFacebook().equals("NULL")) officialFacebook.setImageResource(R.drawable.facebook);
        else officialFacebook.setVisibility(View.INVISIBLE);
        if (!official.getTwitter().equals("NULL")) officialTwitter.setImageResource(R.drawable.twitter);
        else officialTwitter.setVisibility(View.INVISIBLE);
        if (!official.getYoutube().equals("NULL")) officialYoutube.setImageResource(R.drawable.youtube);
        else officialYoutube.setVisibility(View.INVISIBLE);

        // Set listeners
        officialPartyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                partyImageClicked(v);
            }
        });
        officialFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookClicked(v);
            }
        });
        officialTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                twitterClicked(v);
            }
        });
        officialYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                youtubeClicked(v);
            }
        });
        officialImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                officialImageClicked(v);
            }
        });
    }

    public void twitterClicked(View v) {
        Intent officialIntent = getIntent();
        Official official = (Official) officialIntent.getSerializableExtra("official");
        String name = official.getTwitter();
        if (!name.equals("NULL")) {
            Intent intent = null;
            try {
                getPackageManager().getPackageInfo("com.twitter.android", 0);
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=" + name));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            } catch (Exception e) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + name));
            }
            startActivity(intent);
        }
    }

    public void facebookClicked(View v) {
        Intent officialIntent = getIntent();
        Official official = (Official) officialIntent.getSerializableExtra("official");
        String name = official.getFacebook();
        if (!name.equals("NULL")) {
            Intent intent = null;
            String FACEBOOK_URL = "https://www.facebook.com/" + name;
            String urlToUse;
            try {
                getPackageManager().getPackageInfo("com.facebook.katana", 0);

                int versionCode = getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
                if (versionCode >= 3002850) {
                    urlToUse = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
                } else {
                    urlToUse = "fb://page/" + name;
                }
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(urlToUse));
            } catch (Exception e) {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(FACEBOOK_URL));
            }
            startActivity(intent);
        }
    }

    public void youtubeClicked(View v) {
        Intent officialIntent = getIntent();
        Official official = (Official) officialIntent.getSerializableExtra("official");
        String name = official.getYoutube();
        if (!name.equals("NULL")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setPackage("com.google.android.youtube");
                intent.setData(Uri.parse("https://www.youtube.com" + name));
                startActivity(intent);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com" + name)));
            }
        }
    }

    public void partyImageClicked(View v) {
        Intent officialIntent = getIntent();
        Official official = (Official) officialIntent.getSerializableExtra("official");
        String party = official.getParty();
        if (party.equals("Democratic Party") || party.equals("Democratic")) {
            String url = "https://democrats.org";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } else if (party.equals("Republican Party") || party.equals("Republican")) {
            String url = "https://gop.com";
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        }
    }

    public void officialImageClicked(View v) {
        Intent officialIntent = getIntent();
        Official official = (Official) officialIntent.getSerializableExtra("official");
        String photoUrl = official.getPhotoUrl();
        if(!photoUrl.equals("NULL")) {
            Intent photoActivityIntent = new Intent(this, PhotoDetailActivity.class);
            photoActivityIntent.putExtra("official", official);
            photoActivityIntent.putExtra("locationBar", locationBarOfficial.getText().toString());
            startActivity(photoActivityIntent);
        }
    }
}