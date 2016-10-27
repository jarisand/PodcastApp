package com.example.elisarajaniemi.podcastapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

/**
 * Created by Elisa Rajaniemi on 27.10.2016.
 */

public class SerieFragment extends Fragment implements AdapterView.OnItemSelectedListener{

    private ListView listView;
    private SerieArrayAdapter adapter;
    private ImageButton menuBtn;
    private Spinner spinner;
    private Button categoryBtn;
    private boolean categoryOpen;
    private CategoryFragment cf;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final ArrayList<String> list = new ArrayList<>();
        list.add("item1");
        list.add("item2");
        list.add("item3");
        list.add("item4");
        list.add("item5");

        View view = inflater.inflate(R.layout.serie_layout, container, false);
        spinner = (Spinner) view.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        addItemsOnSpinner();

        cf = new CategoryFragment();

        categoryBtn = (Button) view.findViewById(R.id.categoryBtn);
        categoryBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(categoryOpen == false) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.frag_container, cf).commit();
                    categoryOpen = true;
                }
                else{
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .remove(cf).commit();
                    categoryOpen = false;
                   // getActivity().getSupportFragmentManager().beginTransaction()
                    //        .add(R.id.frag_container, sf).commit();
                }

                System.out.println("menu clicked");

            }
        });



        listView = (ListView) view.findViewById(R.id.serieList);
        adapter = new SerieArrayAdapter(getContext(),list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> av, View v, int position, long rowId) {
                String value = list.get(position);
                System.out.println(value);
            }

        });

        return view;
    }

    public void addItemsOnSpinner() {
        ArrayAdapter<CharSequence> dataAdapter = ArrayAdapter.createFromResource(getContext(), R.array.sort_array, android.R.layout.simple_spinner_item);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        System.out.println("added items to spinner");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String value = parent.getItemAtPosition(position).toString();
        if(value.contains("NAME")){
            System.out.println("SORT: NAME");
        }
        else if(value.contains("NEW")){
            System.out.println("SORT: NEW");
        }
    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }


}
