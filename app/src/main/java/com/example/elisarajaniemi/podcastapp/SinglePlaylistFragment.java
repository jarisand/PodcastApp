package com.example.elisarajaniemi.podcastapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Elisa Rajaniemi on 31.10.2016.
 */

public class SinglePlaylistFragment extends Fragment {

    private ListView listView;
    private SerieArrayAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_playlist_layout, container, false);


        /**
        listView = (ListView) view.findViewById(R.id.single_playlist_list);
        adapter = new SerieArrayAdapter(getContext(), PodcastItems.getInstance().getItems());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int position, long rowId) {
                //String value = list.get(position).toString();
                //System.out.println(value);
                PlayerFragment pf = new PlayerFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                       .replace(R.id.frag_container, pf).addToBackStack( "tag" ).commit();
            }

        });
         */

        return view;
    }
}
