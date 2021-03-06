package com.example.elisarajaniemi.podcastapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

/**
 * Created by Elisa Rajaniemi on 27.10.2016.
 */

public class FrontPageFragment extends Fragment implements AdapterView.OnItemSelectedListener, Serializable {

    private GridViewAdapter adapter;
    private Spinner spinner;
    private Button categoryBtn;
    private boolean all, viihde, musiikki, draama, asia, kulttuuri, historia, luonto, hartaudet, lapset, ajankohtaisohjelmat, uutiset, urheilu, metropolia;
    private CollectionFragment collectionFragment;
    private ArrayList<PodcastItem> categoryList;
    private ArrayList<Boolean> prefCategoryList;
    private String[] backendCategories;
    private String sortValue;
    private SharedPreferences prefs;
    private GridView gridView;
    private PlaylistsFragment playlistsFragment;
    private AddToLists addToLists;
    private Favorites favorites;
    private static final String LIST_STATE = "listState";
    private Parcelable mListState = null;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.frontpage_layout, container, false);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        addItemsOnSpinner();
        categoryList = new ArrayList<>();
        prefCategoryList = new ArrayList<>();
        sortValue = "";
        addToLists = new AddToLists();
        playlistsFragment = new PlaylistsFragment();
        favorites = new Favorites();

        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        prefs.edit().putBoolean("kulttuuri", true);

        categoryBtn = (Button) view.findViewById(R.id.categoryBtn);
        categoryBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(getContext(), MyPreferencesActivity.class);
                startActivity(i);
            }
        });

        gridView = (GridView) view.findViewById(R.id.episodeGridview);
        categoryList = getListByCategories();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                PodcastItem pi = categoryList.get(position);
                collectionFragment = new CollectionFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("message", pi);
                collectionFragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction().addToBackStack("sf")
                        .replace(R.id.frag_container, collectionFragment).commit();
            }
        });

        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                PodcastItem pi = categoryList.get(position);
                addToLists.addToListsDialog(getContext(), pi, playlistsFragment, favorites);
                return true;
            }
        });

        return view;
    }



    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    public void addItemsOnSpinner() {
        ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(getContext(), R.array.sort_array, R.layout.simple_spinner_item);
        dataAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String value = parent.getItemAtPosition(position).toString();

        if (value.contains("TOP")) {
            sortValue = value;
            getListByCategories();
            System.out.println("TOP");
        }

        else if (value.contains("NAME")) {
            Collections.sort(categoryList, new Comparator<PodcastItem>() {
                public int compare(PodcastItem pod1, PodcastItem pod2) {
                    return pod1.title.compareToIgnoreCase(pod2.title); // To compare string values
                }
            });

        }

        else if (value.contains("LENGTH")) {
            Collections.sort(categoryList, new Comparator<PodcastItem>() {
                public int compare(PodcastItem pod1, PodcastItem pod2) {
                    return Integer.valueOf(pod1.length).compareTo(pod2.length);

                }
            });

        }
        if(!value.equals("TOP")) {
            adapter = new GridViewAdapter(getContext(), categoryList);
            sortValue = value;
            adapter.notifyDataSetChanged();
            gridView.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getListByCategories();
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onPause(){
        super.onPause();
    }

    public ArrayList<PodcastItem> getListByCategories() {
        if (categoryList.size() != 0) {
            categoryList.clear();
        }
        if(prefCategoryList.size() != 0){
            prefCategoryList.clear();
        }
        all = prefs.getBoolean("all", true);
        viihde = prefs.getBoolean("viihde", true);
        musiikki = prefs.getBoolean("musiikki", true);
        asia = prefs.getBoolean("asia", true);
        draama = prefs.getBoolean("draama", true);
        hartaudet = prefs.getBoolean("hartaudet", true);
        luonto = prefs.getBoolean("luonto", true);
        historia = prefs.getBoolean("historia", true);
        kulttuuri = prefs.getBoolean("kulttuuri", true);
        lapset = prefs.getBoolean("lapset", true);
        ajankohtaisohjelmat = prefs.getBoolean("ajankohtaisohjelmat", true);
        uutiset = prefs.getBoolean("uutiset", true);
        urheilu = prefs.getBoolean("urheilu", true);
        metropolia = prefs.getBoolean("metropolia", true);

        if(metropolia == true){
            String apiKey = "495i4orWwXCqiW5IuOQUzuAlGmfFeky7BzMPe-X19inh9MRm5RqGhQDUEh5avkZNFjC6mYT6w2xGXdQjm9XfakwHloH027i-tkLX77yFMZJlC3wGWqIjyHIXnvPzvHzW";
            try {
                new GetMetropoliaPodcastHelper((MainActivity) getContext()).execute("http://dev.mw.metropolia.fi/aanimaisema/plugins/api_audio_search/index.php/?key=" + apiKey + "&category=%20&link=true").get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        prefCategoryList.add(viihde);
        prefCategoryList.add(musiikki);
        prefCategoryList.add(asia);
        prefCategoryList.add(draama);
        prefCategoryList.add(hartaudet);
        prefCategoryList.add(luonto);
        prefCategoryList.add(historia);
        prefCategoryList.add(kulttuuri);
        prefCategoryList.add(lapset);
        prefCategoryList.add(ajankohtaisohjelmat);
        prefCategoryList.add(uutiset);
        prefCategoryList.add(urheilu);
        prefCategoryList.add(metropolia);

        backendCategories = new String[] {"viihde", "musiikki", "asia", "draama", "hartaudet", "luonto", "historia", "kulttuuri", "lapset", "ajankohtais", "uutiset", "urheilu", "metropolia"};


        for(int i = 0; i < PodcastItems.getInstance().getItems().size(); i++) {
            for (int u = 0; u < PodcastItems.getInstance().getItems().get(i).categorys.size(); u++) {
                for (int o = 0; o < backendCategories.length; o++) {

                    if (PodcastItems.getInstance().getItems().get(i).categorys.get(u).toLowerCase().contains(backendCategories[o]) && prefCategoryList.get(o) == true && !categoryList.contains(PodcastItems.getInstance().getItems().get(i))) {
                        categoryList.add(0, PodcastItems.getInstance().getItems().get(i));
                    }else if(PodcastItems.getInstance().getItems().get(i).tags.toLowerCase().contains(backendCategories[o]) && prefCategoryList.get(o) == true && !categoryList.contains(PodcastItems.getInstance().getItems().get(i))) {
                        categoryList.add(0, PodcastItems.getInstance().getItems().get(i));
                    }else if (all == true && !categoryList.contains(PodcastItems.getInstance().getItems().get(i))) {
                        categoryList.add(0, PodcastItems.getInstance().getItems().get(i));
                    }
                }
            }
        }

        if(sortValue.contains("NAME")){
            Collections.sort(categoryList, new Comparator<PodcastItem>() {
                public int compare(PodcastItem pod1, PodcastItem pod2) {
                    return pod1.title.compareToIgnoreCase(pod2.title); // To compare string values
                }
            });
        }
        else if(sortValue.contains("NEW")){
            Collections.sort(categoryList, new Comparator<PodcastItem>() {
                public int compare(PodcastItem pod1, PodcastItem pod2) {
                    return Integer.valueOf(pod2.collectionID).compareTo(pod1.collectionID);
                }
            });
        }

        adapter = new GridViewAdapter(getContext(), categoryList);
        adapter.notifyDataSetChanged();
        gridView.setAdapter(adapter);
        return categoryList;

    }

}


