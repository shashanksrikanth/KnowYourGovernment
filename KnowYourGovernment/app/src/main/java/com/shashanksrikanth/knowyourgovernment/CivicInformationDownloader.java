package com.shashanksrikanth.knowyourgovernment;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class CivicInformationDownloader implements Runnable {

    private String query;
    private MainActivity mainActivity;
    private String sourceUrl = "https://www.googleapis.com/civicinfo/v2/representatives?key=AIzaSyCs-DtxxV0UEFNZdxK_v3SQBaj4kMdpLww&address=";
    private static final String TAG = "CivicInformationDownloader";

    public CivicInformationDownloader(MainActivity mainActivity, String query) {
        this.mainActivity = mainActivity;
        this.query = query;
    }
    @Override
    public void run() {
        Uri.Builder builder = Uri.parse(sourceUrl + query).buildUpon();
        String urlToUse = builder.toString();
        Log.d(TAG, "urlToUse: " + urlToUse);
        StringBuilder sb = new StringBuilder();
        try {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            if(conn.getResponseCode() != HttpURLConnection.HTTP_OK) return;
            InputStream is = conn.getInputStream();
            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));
            String line;
            while ((line = reader.readLine()) != null) sb.append(line).append('\n');
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        processData(sb.toString());
    }

    public void processData(String string) {
        try {
            final ArrayList<Official> officialsList = new ArrayList<>();
            JSONObject jsonResult = new JSONObject(string);
            JSONObject normalizedInput = jsonResult.getJSONObject("normalizedInput");
            final String location = normalizedInput.getString("city") + ", " +
                    normalizedInput.getString("state") + " " +
                    normalizedInput.getString("zip");
            JSONArray offices = jsonResult.getJSONArray("offices");
            JSONArray officials = jsonResult.getJSONArray("officials");

            for(int i = 0; i<offices.length(); i++) {
                JSONObject officesItem = offices.getJSONObject(i);
                String officialOffice = officesItem.getString("name");
                JSONArray officialIndices = officesItem.getJSONArray("officialIndices");

                for(int index = 0; index<officialIndices.length(); index++) {
                    int officialIndex = officialIndices.getInt(index);
                    JSONObject officialsItem = (JSONObject) officials.get(officialIndex);
                    String officialName = officialsItem.getString("name");
                    JSONArray address = officialsItem.getJSONArray("address");

                    String officialAddress = "";
                    JSONObject addressObj = (JSONObject) address.get(0);
                    officialAddress = addressObj.getString("line1") + ", " + addressObj.getString("city") + ", "
                            + addressObj.getString("state") + " " + addressObj.getString("zip");
                    Log.d(TAG, "officialAddress: " + officialAddress);

                    String officialParty;
                    if(officialsItem.has("party"))
                        officialParty = officialsItem.getString("party");
                    else officialParty = "Unknown";

                    String officialPhone;
                    if(officialsItem.has("phones") && officialsItem.getJSONArray("phones").length()!=0){
                        officialPhone = officialsItem.getJSONArray("phones").getString(0);
                    }
                    else officialPhone = "NULL";

                    String officialWebsite;
                    if(officialsItem.has("urls") && officialsItem.getJSONArray("urls").length()!=0){
                        officialWebsite = officialsItem.getJSONArray("urls").getString(0);
                    }
                    else officialWebsite = "NULL";

                    String officialEmail;
                    if(officialsItem.has("emails") && officialsItem.getJSONArray("emails").length()!=0){
                        officialEmail = officialsItem.getJSONArray("emails").getString(0);
                    }
                    else officialEmail = "NULL";

                    String officialPhotoUrl;
                    if(officialsItem.has("photoUrl")) officialPhotoUrl = officialsItem.getString("photoUrl");
                    else officialPhotoUrl = "NULL";

                    String officialFacebook = "NULL";
                    String officialTwitter = "NULL";
                    String officialYoutube = "NULL";
                    if(officialsItem.has("channels")){
                        JSONArray channels = officialsItem.getJSONArray("channels");
                        for(int channel_index = 0; channel_index < channels.length(); channel_index++) {
                            JSONObject obj = (JSONObject) channels.get(channel_index);
                            if(obj.getString("type").equals("Facebook")) officialFacebook = obj.getString("id");
                            else if(obj.getString("type").equals("Twitter")) officialTwitter = obj.getString("id");
                            else if(obj.getString("type").equals("YouTube")) officialYoutube = obj.getString("id");
                        }
                    }

                    Official official = new Official(officialOffice, officialName, officialAddress, officialParty,
                            officialPhone, officialWebsite, officialEmail, officialPhotoUrl, officialFacebook,
                            officialTwitter, officialYoutube);
                    officialsList.add(official);
                }

            }
            mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainActivity.setLocationBar(location);
                    mainActivity.addOfficials(officialsList);
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
