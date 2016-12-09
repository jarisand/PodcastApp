package com.example.elisarajaniemi.podcastapp;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

/**
 * Created by Elisa Rajaniemi on 31.10.2016.
 */

public class CollectionFragment extends Fragment {

    private ListView listView;
    private GetMetropoliaPodcastHelper getMetropoliaPodcastHelper;
    private String message;
    private ArrayList<PodcastItem> list;
    private PodcastItem pi;
    private MainActivity ma;
    private AlertDialog alertDialog;
    private LinearLayout headerBox;
    private PlayerFragment pf;
    public PodcastItems podcastItems = PodcastItems.getInstance();
    public PlaylistPodcastItems playlistPodcastItems = PlaylistPodcastItems.getInstance();
    public PodcastIDArray podcastIDArray = PodcastIDArray.getInstance();
    public FavoritePodcastItems favoritePodcastItems = FavoritePodcastItems.getInstance();
    public SearchItems searchItems = SearchItems.getInstance();
    public HistoryPodcastItems historyPodcastItems = HistoryPodcastItems.getInstance();
    //private ArrayList<PodcastItem> listAll = podcastItems.getItems();
    private int playlistID = 0;
    private ExpandableListView simpleExpandableListView;
    private ExpandableListViewAdapter listAdapter;
    private FavoritesFragment favoritesFragment;
    private ImageView imageView;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private TextView textView;
    private LinearLayout header;

    private boolean fromFavorites, fromSearch, fromHistory;
    private History historyClass;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.single_playlist_layout, container, false);
        pi = (PodcastItem) getArguments().getSerializable("message");
        playlistID = getArguments().getInt("playlistID");
        fromFavorites = getArguments().getBoolean("fromFavorites");
        fromSearch = getArguments().getBoolean("fromSearch");
        fromHistory = getArguments().getBoolean("fromHistory");

        historyClass = new History();
        list = new ArrayList<>();

        System.out.println("FromSearch value: " + fromSearch);
        System.out.println("FromFavorites value: " + fromFavorites);
        System.out.println("PlaylistID value: " + playlistID);
        favoritesFragment = new FavoritesFragment();

        imageView = (ImageView) view.findViewById(R.id.collectionImage);
        textView = (TextView) view.findViewById(R.id.title);
        header = (LinearLayout) view.findViewById(R.id.headerBox);


        if(playlistID != 0 && fromFavorites == false && fromHistory == false) {
            try {
                new GetYlePodcastHelper((MainActivity) getContext()).execute("https://external.api.yle.fi/v1/programs/items/", ".json?app_key=2acb02a2a89f0d366e569b228320619b&app_id=950fdb28", "fromplaylist").get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else if(playlistID == 0 && fromFavorites == true && fromHistory == false){
            try {
                new GetYlePodcastHelper((MainActivity) getContext()).execute("https://external.api.yle.fi/v1/programs/items/", ".json?app_key=2acb02a2a89f0d366e569b228320619b&app_id=950fdb28", "fromfavorites").get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        } else if(playlistID == 0 && fromHistory == true && fromFavorites == false){
            try {
                new GetYlePodcastHelper((MainActivity) getContext()).execute("https://external.api.yle.fi/v1/programs/items/", ".json?app_key=2acb02a2a89f0d366e569b228320619b&app_id=950fdb28", "fromHistory").get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }



        simpleExpandableListView = (ExpandableListView) view.findViewById(R.id.expandable_listview);
        simpleExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                PodcastItem podcastItem = list.get(groupPosition);
                return false;
            }
        });

        simpleExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                return true;
            }
        });

        if (fromFavorites == true){
            simpleExpandableListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                    final int favoriteID = position;
                    final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(getContext(), R.style.AlertDialogCustom));
                    //AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.CustomDialog));
                    alertDialogBuilder.setTitle("Delete favorite");

                    System.out.println("PlaylistID: " + position);

                    LinearLayout lp = new LinearLayout(getContext());
                    lp.setOrientation(LinearLayout.VERTICAL);
                    lp.setPadding(30,0,30,30);


                    final TextView toQueue = new TextView(getContext());
                    toQueue.setText("Do you really want to delete this favorite?");
                    toQueue.setTextColor(Color.BLACK);
                    toQueue.setPadding(30, 20, 20, 20);
                    toQueue.setTextSize(20);
                    lp.addView(toQueue);

                    alertDialogBuilder.setView(lp);
                    final AlertDialog alertDialog = alertDialogBuilder.create();

                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            try {
                                favoritesFragment.deleteFavorites("http://media.mw.metropolia.fi/arsu/favourites/", podcastIDArray.getItems().get(favoriteID).id, "?token=" + PreferenceManager.getDefaultSharedPreferences(getContext()).getString("token", "0"));
                                favoritePodcastItems.deletePodcast(favoriteID);
                                listAdapter.notifyDataSetChanged();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    });

                    alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

                    alertDialog.show();

                    return true;
                }
            });
        }

        return view;
    }

    //method to expand all groups
    private void expandAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++){
            simpleExpandableListView.expandGroup(i);
        }
    }

    //method to collapse all groups
    private void collapseAll() {
        int count = listAdapter.getGroupCount();
        for (int i = 0; i < count; i++){
            simpleExpandableListView.collapseGroup(i);
        }
    }


    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onResume() {
        super.onResume();
        fillList();
        simpleExpandableListView.deferNotifyDataSetChanged();
        listAdapter.getGroupCount();
        listAdapter.notifyDataSetChanged();



    }


    public void fillList(){

        if(list.size() != 0 || list != null) {
            list.clear();
        }

        if(list.size() == 0 && playlistID == 0 && !fromFavorites && !fromSearch && !fromHistory) {
            System.out.println("From frontpage");
            for (int i = 0; i < podcastItems.getItems().size(); i++) {
                if (podcastItems.getItems().get(i).collectionName.equals(pi.collectionName) && !list.contains(podcastItems.getItems().get(i))) {
                    list.add(podcastItems.getItems().get(i));
                }
            }

        } else if(list.size() == 0 && playlistID != 0 && !fromFavorites && !fromSearch && !fromHistory){
            System.out.println("PlaylistPodcastItems size: " + playlistPodcastItems.getItems().size());
            list = playlistPodcastItems.getItems();

        } else if(list.size() == 0 && playlistID == 0 && fromFavorites && !fromSearch && !fromHistory){
            System.out.println("FavoritePodcastItems array size: " + favoritePodcastItems.getItems().size());
            list = favoritePodcastItems.getItems();
        } else if(list.size() == 0 && playlistID == 0 && fromSearch && !fromFavorites && !fromHistory){
            System.out.println("From Search: " + fromSearch + ", search size: " + list.size());
            list = searchItems.getSearchItems();
        } else if(list.size() == 0 && playlistID == 0 && !fromSearch && !fromFavorites && fromHistory){
            System.out.println("From History: " + fromHistory + ", History size: " + list.size());
            list = historyPodcastItems.getItems();
        }

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = (getResources().getDisplayMetrics().heightPixels)/3;

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getContext())
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .threadPoolSize(3) // default
                .threadPriority(Thread.NORM_PRIORITY - 1) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileCount(100)
                .imageDownloader(new BaseImageDownloader(getContext())) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .build();

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                //.showStubImage(R.drawable.ic_add_black_24dp)
                //.showImageForEmptyUri(R.drawable.ic_add_black_24dp)
                //.showImageOnFail(R.drawable.ic_add_black_24dp)
                .cacheOnDisc(true)
                .build();

        if(list != null && list.size() > 0) {
            if (!list.get(0).collectionName.contains("Metropolia")) {
                imageLoader.displayImage("http://images.cdn.yle.fi/image/upload//w_" + width + ",h_" + height + ",c_fill/" + list.get(0).imageURL + ".jpg", imageView, options);            //w_705,h_520,c_fill,g_auto
            } else {
                textView.setText("Metropolia");
                header.requestLayout();
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
                header.setLayoutParams(layoutParams);

            }
        }
        else if(playlistID != 0){
            textView.setText("Empty");
            header.requestLayout();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width, height);
            header.setLayoutParams(layoutParams);

        }

        if(!list.get(0).collectionName.toLowerCase().contains("metropolia")) {
            Collections.sort(list, new Comparator<PodcastItem>() {
                public int compare(PodcastItem pod1, PodcastItem pod2) {
                    return pod1.programID.compareToIgnoreCase(pod2.programID); // To compare string values

                }
            });
        }

        for(int i = 0; i < list.size(); i++){
        }
        listAdapter = new ExpandableListViewAdapter(getContext(), list);
        listAdapter.notifyDataSetChanged();
        simpleExpandableListView.deferNotifyDataSetChanged();
        simpleExpandableListView.setAdapter(listAdapter);

    }

}

class DecodeYleURL extends AsyncTask<PodcastItem, String, String> {
    //ProgressDialog pdLoading = new ProgressDialog(AsyncExample.this);
    String decryptedURL;
    String resultURL;
    MyCrypt myCrypt = new MyCrypt();
    BufferedReader br;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        //this method will be running on UI thread
        //pdLoading.setMessage("\tLoading...");
        //pdLoading.show();
    }
    @Override
    protected String doInBackground(PodcastItem... params) {

        try {
            System.out.println("URL " + params[0].decryptedURL);
            URL url = new URL(params[0].decryptedURL);
            URLConnection conn = url.openConnection();
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            conn.connect();
            BufferedReader r  = new BufferedReader(new InputStreamReader(conn.getInputStream(), Charset.forName("UTF-8")));
            String output;

            while ((output = r.readLine()) != null) {
                try {
                    JSONObject jObject = new JSONObject(output);
                    JSONArray jArray = jObject.getJSONArray("data");
                    for (int i = 0; i < jArray.length(); i++){
                        decryptedURL = jArray.getJSONObject(i).getString("url");
                    }
                    resultURL = myCrypt.decryptURL(decryptedURL);

                } catch (JSONException e) {
                    System.out.println(e);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (InvalidKeyException e) {
                    e.printStackTrace();
                } catch (InvalidAlgorithmParameterException e) {
                    e.printStackTrace();
                } catch (NoSuchPaddingException e) {
                    e.printStackTrace();
                } catch (BadPaddingException e) {
                    e.printStackTrace();
                } catch (IllegalBlockSizeException e) {
                    e.printStackTrace();
                }
            }

        } catch (
                MalformedURLException e
                ) {
            e.printStackTrace();

        } catch (
                IOException e
                )

        {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        params[0].setURL(resultURL);
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

    }


}
