package com.example.elisarajaniemi.podcastapp;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Elisa Rajaniemi on 30.11.2016.
 */

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<PodcastItem> groupList;
    private PlayerFragment playerFragment;
    private AddToLists addToLists;
    private Favorites favorites;
    private PlaylistsFragment playlistsFragment;
    public AutoplayItems autoplayItems = AutoplayItems.getInstance();
    public QueueItems queueItems =QueueItems.getInstance();

    private ImageButton playBtn;
    private TextView title, collectionName;


    public ExpandableListViewAdapter(Context context, ArrayList<PodcastItem> groupList) {
        this.context = context;
        this.groupList = groupList;
    }

    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        PodcastItem podcastItem = groupList.get(groupPosition);
        return podcastItem;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public int getGroupTypeCount() {
        return getGroupCount();
    }

/**
    @Override
    public int getViewType(int position) {

        item
        return position;
    }*/

    @SuppressWarnings("unchecked")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final DecodeYleURL decodeYleURL = new DecodeYleURL(context);
        View myView = null;
        convertView = null;

        if (myView == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            myView = inf.inflate(R.layout.expandablelistview_item, parent, false);
        }

        final PodcastItem podcastItem = (PodcastItem) getGroup(groupPosition);
        title = (TextView) myView.findViewById(R.id.episodeName);
        collectionName = (TextView) myView.findViewById(R.id.collection);
        playBtn = (ImageButton) myView.findViewById(R.id.episodeIcon);


        title.setText(podcastItem.title);
        collectionName.setText(podcastItem.collectionName);
        playBtn.setId(groupPosition);
        playBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                playerFragment = new PlayerFragment();
                Bundle bundle2 = new Bundle();
                if (podcastItem.fromYLE == true){
                    try {
                        //new DecodeYleURL(context).execute(podcastItem).get();
                        decodeYleURL.execute(podcastItem).get();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                }
                bundle2.putSerializable("episode", podcastItem);
                playerFragment.setArguments(bundle2);
                ((MainActivity) context).setFragment(playerFragment);
                autoplayItems.clearList();
                autoplayItems.addAll(queueItems.getItems());
                autoplayItems.addAll(groupList);
                System.out.println("Lisätty aotoplayhin");



            }
        });

        ImageButton button = (ImageButton) myView.findViewById(R.id.itemMenu);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                favorites = new Favorites();
                playlistsFragment = new PlaylistsFragment();
                addToLists = new AddToLists();

                addToLists.addToListsDialog(context, podcastItem, playlistsFragment, favorites);

            }
        });

        return myView;
    }


    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        PodcastItem podcastItem = (PodcastItem) getChild(groupPosition, childPosition);
        View myView = convertView;
        if (myView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            myView = infalInflater.inflate(R.layout.child_items, parent, false);
        }

        TextView descriptionView = (TextView) myView.findViewById(R.id.description_view);
        descriptionView.setText(podcastItem.description);

        TextView lengthView = (TextView) myView.findViewById(R.id.length);
        String length = DateUtils.formatElapsedTime(podcastItem.length);
        lengthView.setText(length);


        return myView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

}
