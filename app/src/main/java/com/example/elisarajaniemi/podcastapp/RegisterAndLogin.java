package com.example.elisarajaniemi.podcastapp;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Elisa Rajaniemi on 10.11.2016.
 */

public class RegisterAndLogin {

    private boolean loggedIn;
    private Thread t;
    private boolean exists;
    private boolean registered;
    private String encryptedPassword, encryptedUsername, encryptedEmail;
    private MyCrypt myCrypt = new MyCrypt();
    private String token;


    public boolean registerUser(String username, String password, String password2, String email) {

        t = new Thread(connectToUsers);
        t.start();
        testIfExists(username, password);

        if (password.equals(password2)) {
            testIfExists(username, password);
            System.out.println("Password match");

            if (this.exists == false) {
                System.out.println("Register User didn't exist");
                this.encryptedUsername = myCrypt.doEncoding(username).toString();
                this.encryptedPassword = myCrypt.doEncoding(password).toString();
                this.encryptedEmail = myCrypt.doEncoding(email).toString();

                t = new Thread(r);
                t.start();

                //here send encrypted userdata to database
                loggedIn = true;
            } else {
                System.out.println("Register User existed");
                loggedIn = false;
            }
        } else {
            System.out.println("Register Password didn't match");
            loggedIn = false;
        }
        return loggedIn;
    }

    public String login(String username, String password) {
        testIfExists(username, password);

        System.out.println("Log In exists = " + exists + ", loggedIn = " + loggedIn);

        if (exists == true && loggedIn != true) {
            this.encryptedUsername = myCrypt.doEncoding(username).toString();
            this.encryptedPassword = myCrypt.doEncoding(password).toString();
            t = new Thread(r2);
            t.start();
            loggedIn = true;
        } else {
            System.out.println("Login User doesn't exist");
            loggedIn = false;
        }
        return username;

    }

    public boolean logout() {
        token = null;
        loggedIn = false;
        exists = false;
        System.out.println("Logged out, Token = " + token + ", loggedIn = " + loggedIn);
        return loggedIn;
    }

    public boolean testIfExists(String username, String email) {
        //here check if user is not already registered
        this.encryptedUsername = myCrypt.doEncoding(username).trim();
        this.encryptedEmail = myCrypt.doEncoding(email).toString();

        //exists = false;

        for (int i = 0; i < Users.getInstance().getUsers().size(); i++){
            System.out.println("FOR Username: " + Users.getInstance().getUsers().get(i).username.length() + ", " + encryptedUsername.length());
            if (Users.getInstance().getUsers().get(i).username.equalsIgnoreCase(this.encryptedUsername)){
                System.out.println("----true---");
                this.exists = true;
            }
            else {
                System.out.println("ELSE");
            }
        }
        System.out.println("Test exists: " + exists);
        return this.exists;
    }

    Runnable r = new Runnable() {
        public void run() {
            try {
                URL url = new URL("http://media.mw.metropolia.fi/arsu/users?token=" + token);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                String input = "{\"username\":\"" + encryptedUsername + "\",\"password\":\"" + encryptedPassword + "\",\"email\":\"" + encryptedEmail + "\"}";
                input = input.replace("\n", "");
                //System.out.println(input);
                //String input = "{\"username\":\"kana\",\"password\":\"\",\"email\":\"\"}";
                System.out.println(input);

                OutputStream os = conn.getOutputStream();
                os.write(input.getBytes());
                os.flush();

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                String output;

                while ((output = br.readLine()) != null) {
                    try {
                        JSONObject jObject = new JSONObject(output);
                        String message = jObject.getString("message");
                        System.out.println("Message: " + message);
                    } catch (JSONException e) {
                        System.out.println(e);
                    }
                }

                conn.disconnect();
            } catch (
                    MalformedURLException e
                    ) {
                e.printStackTrace();

            } catch (
                    IOException e
                    )

            {
                e.printStackTrace();
            }
        }

    };

    Runnable r2 = new Runnable() {
        public void run() {
            try {
                URL url = new URL("http://media.mw.metropolia.fi/arsu/login");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                String input = "{\"username\":\"" + encryptedUsername + "\",\"password\":\"" + encryptedPassword + "\"}";
                input = input.replace("\n", "");
                System.out.println(input);

                OutputStream os = conn.getOutputStream();
                os.write(input.getBytes());
                os.flush();

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                String output;

                while ((output = br.readLine()) != null) {
                    try {
                        JSONObject jObject = new JSONObject(output);
                        token = jObject.getString("token");
                        System.out.println("Token: " + token);
                    } catch (JSONException e) {
                        System.out.println(e);
                    }
                }

                conn.disconnect();
            } catch (
                    MalformedURLException e
                    ) {
                e.printStackTrace();

            } catch (
                    IOException e
                    )

            {
                e.printStackTrace();
            }
        }

    };

    Runnable connectToUsers = new Runnable() {
        public void run() {
            try {
                URL url = new URL("http://media.mw.metropolia.fi/arsu/login");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                String input = "{\"username\":\"moi\", \"password\":\"heps\"}";

                OutputStream os = conn.getOutputStream();
                os.write(input.getBytes());
                os.flush();

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                String output;

                while ((output = br.readLine()) != null) {
                    try {
                        JSONObject jObject = new JSONObject(output);
                        token = jObject.getString("token");
                        new GetUsersHelper().execute("http://media.mw.metropolia.fi/arsu/users?token=" + token);
                    } catch (JSONException e) {
                        System.out.println(e);
                    }
                }

                conn.disconnect();
            } catch (
                    MalformedURLException e
                    ) {
                e.printStackTrace();

            } catch (
                    IOException e
                    )

            {
                e.printStackTrace();
            }
        }

    };
}