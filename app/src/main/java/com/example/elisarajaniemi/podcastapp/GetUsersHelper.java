package com.example.elisarajaniemi.podcastapp;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jari on 11/11/2016.
 */

public class GetUsersHelper extends AsyncTask<String, String, String> {

    String result = "";
    public Users users = Users.getInstance();

    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {


        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(params[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();


            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }
            result = buffer.toString();
            try {
                JSONArray jArray = new JSONArray(result);
                for (int i = 0; i < jArray.length(); i++) {

                    JSONObject jsonObject = jArray.getJSONObject(i);

                    User user = new User(jsonObject.getString("username"), jsonObject.getString("email"));

                    users.addUser(user);

                   /** for (int j = 0; j < finalArray.length(); j++) {

                        JSONObject jObject = finalArray.getJSONObject(j);

                        PodcastItem podcastItem = new PodcastItem(jObject.getString("Title"), jObject.getString("Download link"), jObject.getString("Description"),
                                jObject.getInt("Length (sec)"), jObject.getString("Tags"), jObject.getString("Tags"), jObject.getString("Collection name"),
                                jObject.getInt("Collection ID"), jObject.getString("Location - longitude"));

                        podcastItems.addPodcastItem(podcastItem);
                        System.out.println("Added " + podcastItem.title);
                        if (serieItems.getSerieItems().size() == 0) serieItems.addSerieItem(podcastItem);
                        else {
                            boolean idFound = false;
                            for (int k = 0; k < serieItems.getSerieItems().size(); k++) {
                                if (serieItems.getSerieItems().get(k).collectionID == podcastItem.collectionID) idFound = true;
                            }
                            if (idFound == false) serieItems.addSerieItem(podcastItem);

                        }
                    }*/

                }// End Loop

                System.out.println("Users: " + users.getUsers());

                //System.out.println("SeriID array size: " + serieItems.getSerieItems().size());

            } catch (JSONException e) {
                Log.e("JSONException", "Error: " + e.toString());
            }


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    @Override
    protected void onPostExecute(String result) {
        //System.out.println("Users Array: " + result);
        super.onPostExecute(result);
    }

}