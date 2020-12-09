package com.shashanksrikanth.knowyourgovernment;

import java.io.Serializable;

public class Official implements Serializable {
    String office;
    String name;
    String address;
    String party;
    String phone;
    String website;
    String email;
    String photoUrl;
    String facebook;
    String twitter;
    String youtube;

    public Official(String office, String name, String address, String party, String phone, String website,
                    String email, String photoUrl, String facebook, String twitter, String youtube) {
        this.office = office;
        this.name = name;
        this.address = address;
        this.party = party;
        this.phone = phone;
        this.website = website;
        this.email = email;
        this.photoUrl = photoUrl;
        this.facebook = facebook;
        this.twitter = twitter;
        this.youtube = youtube;
    }

    public String getOffice() {
        return office;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getParty() {
        return party;
    }

    public String getPhone() {
        return phone;
    }

    public String getWebsite() {
        return website;
    }

    public String getEmail() {
        return email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public String getFacebook() {
        return facebook;
    }

    public String getTwitter() {
        return twitter;
    }

    public String getYoutube() {
        return youtube;
    }
}
