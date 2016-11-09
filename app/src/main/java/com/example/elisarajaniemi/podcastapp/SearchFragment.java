package com.example.elisarajaniemi.podcastapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by jari on 09/11/2016.
 */

public class SearchFragment extends Fragment {

    private ListView listView;
    private EpisodeListArrayAdapter episodeAdapter;
    private ArrayList<PodcastItem> list;
    private PlayerFragment pf;
    public PodcastItems podcastItems = PodcastItems.getInstance();
    private ArrayList<PodcastItem> listAll = podcastItems.getItems();
    private AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.search_layout, container, false);

        list = new ArrayList<>();

        listView = (ListView) view.findViewById(R.id.search_list);
        episodeAdapter = new EpisodeListArrayAdapter(getContext() ,PodcastItems.getInstance().getItems());
        listView.setAdapter(episodeAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int position, long rowId) {
                PodcastItem pi = PodcastItems.getInstance().getItems().get(position);
                pf = new PlayerFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("podcastItem", pi);
                pf.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("searchf")
                        .replace(R.id.frag_container, pf).commit();

            }

        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> av, View v, final int position, long rowId) {

                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getContext());
                alertDialogBuilder.setTitle("Description");

                LinearLayout lp = new LinearLayout(getContext());
                lp.setOrientation(LinearLayout.VERTICAL);
                lp.setPadding(30, 30, 30, 60);

                final TextView description = new TextView(getActivity());
                description.setText(PodcastItems.getInstance().getItems().get(position).description);
                lp.addView(description);

                alertDialogBuilder.setView(lp);

                alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                return true;
            }
        });
        return view;
    }
}

