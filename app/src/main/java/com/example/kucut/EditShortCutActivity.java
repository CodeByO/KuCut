package com.example.kucut;

import com.example.kucut.SqlHandle;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class EditShortCutActivity extends AppCompatActivity {
    SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
    SharedPreferences.Editor editor = pref.edit();
    Button NewShortCut;
    ImageButton deleteShortCut;
    TextView name;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_editshortcut);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("바로가기 편집");
        actionBar.setDisplayHomeAsUpEnabled(true);


        NewShortCut = (Button) findViewById(R.id.btnNewShortCut);
        deleteShortCut = (ImageButton) findViewById(R.id.deleteShortCut);
        name = (TextView) findViewById(R.id.name);



        NewShortCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewShortCutDialog(editor);
            }
        });

        deleteShortCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialog(name.getText().toString(),editor);

            }
        });

    }
    public void showNewShortCutDialog(SharedPreferences.Editor editor) {
        final EditText NewName  = new EditText(getApplicationContext());
        final EditText NewLink = new EditText(getApplicationContext());

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("새로운 바로가기");
        builder.setMessage("이름");
        builder.setView(NewName);
        builder.setMessage("주소");
        builder.setView(NewLink);
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ListView listView = (ListView) findViewById(R.id.listView);
                        ListItemAdapter adapter = new ListItemAdapter();
                        if(adapter.getCount() <= 60){
                            editor.putString(NewName.getText().toString(),NewLink.getText().toString());


                            adapter.addItem(new ListItem(NewName.getText().toString(), NewLink.getText().toString()));
                            Toast.makeText(getApplicationContext(), "확인", Toast.LENGTH_LONG).show();
                            editor.apply();
                        }else{
                            Toast.makeText(getApplicationContext(),"더 이상 추가가 불가능합니다.",Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();

                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }
    public void showDeleteDialog(String ShortCutName,SharedPreferences.Editor editor) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("'" + ShortCutName + "'"+ " 삭제");
        builder.setMessage("정말로 삭제 하시겠습니까?");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        editor.remove(ShortCutName);
                        editor.apply();


                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "취소", Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }

}
