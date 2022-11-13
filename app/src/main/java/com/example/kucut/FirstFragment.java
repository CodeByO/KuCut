package com.example.kucut;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
        import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Map;


public class FirstFragment extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    private GridView gridView = null;
   // SharedPreferences pref = getActivity().getSharedPreferences("pref",Context.MODE_PRIVATE);
   // SharedPreferences.Editor editor = pref.edit();
   // SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
    // newInstance constructor for creating fragment with arguments
    public static FirstFragment newInstance(int page, String title) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putInt("0", page);
        args.putString("ListView", title);
        fragment.setArguments(args);
        return fragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("0", 0);
        title = getArguments().getString("ListView");

    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        SharedPreferences pref = getActivity().getSharedPreferences("pref",Context.MODE_PRIVATE);
        View view = inflater.inflate(R.layout.fragment_first, container, false);
        GridView gridView = (GridView) view.findViewById(R.id.gridView);
        ListItemAdapter adapter = new ListItemAdapter();



        Map<String,?> keys = pref.getAll();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            if(entry.getKey().endsWith("_**")){
                adapter.addItem(new ListItem(entry.getKey().replace("_**",""), (String)entry.getValue()));
            }

        }

        // SharedPreferences에서 모든 데이터 값을 가져와 정규식 이용 조건(한글만, 학과,학부,전공,과 문자 제외
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final ListItem item = (ListItem) adapter.getItem(i);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getLink()));
                startActivity(intent);
            }
        });
        return view;
    }

}