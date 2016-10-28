package com.example.elisarajaniemi.podcastapp;

import android.app.ActionBar;
import android.app.FragmentManager;
import android.os.Bundle;
import android.preference.PreferenceFragment;
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

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageButton menuBtn;
    private MenuFragment mf;
    private TextView title;
    private boolean categoryOpen, menuOpen;
    private CategoryFragment cf;
    private SerieFragment sf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //kommentti
        //kommentti 2
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        title = (TextView) toolbar.findViewById(R.id.title);

        menuOpen = false;
        categoryOpen = false;

        mf = new MenuFragment();
        cf = new CategoryFragment();
        sf = new SerieFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.frag_container, sf).commit();

        menuBtn = (ImageButton) findViewById(R.id.menuBtn);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(menuOpen == false) {
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


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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



}

