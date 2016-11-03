package com.example.elisarajaniemi.podcastapp;

import android.app.ActionBar;
import android.app.ActivityManager;
import android.app.FragmentManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.apiKey;

public class MainActivity extends AppCompatActivity {

    private ImageButton menuBtn;
    private MenuFragment mf;
    private TextView title;
    private boolean categoryOpen, menuOpen;
    private SerieFragment sf;
    private EpisodesFragment ef;
    boolean mIsBound = false;
    private PlayerFragment pf;
    private PodcastItem pi, pi2;
    PlayService pServ;
    public ServiceConnection Scon =new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            pServ = ((PlayService.ServiceBinder) service).getService();
        }


        @Override
        public void onServiceDisconnected(ComponentName name) {
            pServ = null;
        }

    };
    private String apiKey;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        HttpGetHelper httpGetHelper = new HttpGetHelper();

        Thread t = new Thread(r);
        t.start();


        //System.out.println("Main activity arraylist: " + httpGetHelper.getResults());

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        title = (TextView) toolbar.findViewById(R.id.title);

        menuOpen = false;
        categoryOpen = false;

        mf = new MenuFragment();
        sf = new SerieFragment();
        pServ = new PlayService();
        //ef = new EpisodesFragment();
        pf = new PlayerFragment();
        doBindService();

        String playerFragment = getIntent().getStringExtra("fragment_name");
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (playerFragment != null) {
            if (playerFragment.equals("PlayerFragment")) {
                fragmentTransaction.add(R.id.frag_container, pf).addToBackStack("tag").commit();
            }

        }else{
            fragmentTransaction.add(R.id.frag_container, sf).addToBackStack("tag").commit();
        }




        menuBtn = (ImageButton) findViewById(R.id.menuBtn);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (menuOpen == false) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.menu_frag_container, mf).commit();
                    menuOpen = true;
                }
                else{
                    getSupportFragmentManager().beginTransaction()
                            .remove(mf).commit();
                    menuOpen = false;
                }
                System.out.println("menu clicked");

            }
        });

        Intent intent = getIntent();
        pi = (PodcastItem)intent.getSerializableExtra("message");
        ef = new EpisodesFragment();

        Bundle bundle = new Bundle();
        bundle.putSerializable("message", pi);
        ef.setArguments(bundle);

        Intent intent2 = getIntent();
        pi2 = (PodcastItem)intent2.getSerializableExtra("episode");
        Bundle bundle2 = new Bundle();
        bundle2.putSerializable("episode", pi2);
        pf.setArguments(bundle2);

        if(pi != null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frag_container, ef).addToBackStack("tag").commit();
        }

        if(pi2 != null){
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frag_container, pf).addToBackStack("tag").commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Category things
        sf.history = prefs.getBoolean("history", true);

        /**
        if (sf.history == false) {

        } else {

        }
            super.onResume();

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        doUnbindService();
        pServ.onDestroy();
         */
    }
    public boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())&&pServ.isStarted()) {

                return true;
            }
        }
        return false;
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    Runnable r = new Runnable() {
        public void run() {
            try {
                URL url = new URL("http://dev.mw.metropolia.fi/aanimaisema/plugins/api_auth/auth.php");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setDoOutput(true);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");

                String input = "{\"username\":\"podcast\",\"password\":\"podcast16\"}";

                OutputStream os = conn.getOutputStream();
                os.write(input.getBytes());
                os.flush();

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (conn.getInputStream())));

                String output;
                System.out.println("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    try {
                        JSONObject jObject = new JSONObject(output);
                        apiKey = jObject.getString("api_key");
                        System.out.println(apiKey);
                        new HttpGetHelper().execute("http://dev.mw.metropolia.fi/aanimaisema/plugins/api_audio_search/index.php/?key=" + apiKey + "&category=%20&link=true");
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


    void doBindService(){
        bindService(new Intent(this,PlayService.class),
                Scon,Context.BIND_AUTO_CREATE);
        mIsBound = true;
    }

    void doUnbindService()
    {
        if(mIsBound)
        {
            unbindService(Scon);
            mIsBound = false;
        }
    }


}

