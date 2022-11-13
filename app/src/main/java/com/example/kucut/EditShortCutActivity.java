package com.example.kucut;

import com.example.kucut.SqlHandle;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

public class EditShortCutActivity extends AppCompatActivity {

    Button NewShortCut;
    GridView gridView;
    ListItemAdapter adapter;
    Dialog NewShortCutDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editshortcut);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("바로가기 편집");
        actionBar.setDisplayHomeAsUpEnabled(true);

        SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();



        gridView = (GridView) findViewById(R.id.edit_gridView);
        adapter = new ListItemAdapter();

        NewShortCutDialog = new Dialog(EditShortCutActivity.this);
        NewShortCutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        NewShortCutDialog.setContentView(R.layout.new_shortcut_dialog);



        // SharedPreferences에서 모든 데이터 값을 가져와 정규식 이용 조건(한글만, 학과,학부,전공,과 문자 제외
        Map<String,?> keys = pref.getAll();
        for(Map.Entry<String,?> entry : keys.entrySet()){
            if(entry.getKey().endsWith("_**")){
                adapter.addItem(new ListItem(entry.getKey().replace("_**",""), (String)entry.getValue()));
            }

        }
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final ListItem item = (ListItem) adapter.getItem(i);
                final String name = item.getName();
                showDeleteDialog(name,editor,item,adapter);

            }
        });




        NewShortCut = (Button) findViewById(R.id.btnNewShortCut);


        NewShortCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewShortCutDialog(editor);
            }
        });


    }

    public void showNewShortCutDialog(SharedPreferences.Editor editor) {

        NewShortCutDialog.show();
        Button confirm = (Button) findViewById(R.id.addNewShortCutBtn);
        Button cancel = (Button) findViewById(R.id.btn_cancel);
        EditText NewName = (EditText) findViewById(R.id.shortcut_name);
        EditText NewLink = (EditText) findViewById(R.id.shortcut_link);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(adapter.getCount() <= 25){
                    String name = NewName.getText().toString();
                    name = name+"_**";
                    editor.putString(name,NewLink.getText().toString());
                    adapter.addItem(new ListItem(NewName.getText().toString(), NewLink.getText().toString()));
                    Toast.makeText(getApplicationContext(), "새로운 바로가기가 등록되었습니다.", Toast.LENGTH_LONG).show();
                    editor.apply();
                }else{
                    Toast.makeText(getApplicationContext(),"더 이상 추가가 불가능합니다.",Toast.LENGTH_SHORT).show();
                }
                NewShortCutDialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewShortCutDialog.dismiss();
            }
        });
    }


    public void showDeleteDialog(String ShortCutName,SharedPreferences.Editor editor,ListItem item,ListItemAdapter adapter) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("'" + ShortCutName + "'"+ " 삭제");
        builder.setMessage("정말로 삭제 하시겠습니까?");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String name = ShortCutName;
                        name = name + "_**";
                        editor.remove(name);
                        editor.apply();
                        adapter.removeItem(item);

                        Intent intent = new Intent(EditShortCutActivity.this,MainActivity.class);
                        startActivity(intent);
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.show();
    }

}
