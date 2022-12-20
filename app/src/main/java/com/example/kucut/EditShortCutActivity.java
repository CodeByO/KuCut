package com.example.kucut;

import com.example.kucut.SqlHandle;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
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

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.example.kucut.SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_IMAGE;
import static com.example.kucut.SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_LINK;
import static com.example.kucut.SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_NAME;
import static com.example.kucut.SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_TYPE;


public class EditShortCutActivity extends AppCompatActivity {

    Button NewShortCut;
    GridView gridView;
    ListItemAdapter adapter;
    Dialog NewShortCutDialog;
    DbHelper dbHelper;
    String img_url = "null";
    EditText NewImg;
    Uri url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editshortcut);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("바로가기 편집");
        actionBar.setDisplayHomeAsUpEnabled(true);
        dbHelper = new DbHelper(EditShortCutActivity.this);
        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();


        gridView = (GridView) findViewById(R.id.edit_gridView);
        adapter = new ListItemAdapter();

        NewShortCutDialog = new Dialog(EditShortCutActivity.this);
        NewShortCutDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        NewShortCutDialog.setContentView(R.layout.new_shortcut_dialog);

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_NAME,
                SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_LINK,
                SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_IMAGE
        };

        String selection = SHORTCUT_COLUMN_NAME_TYPE + " = ?";
        String[] selectionArgs = {"0"};
        String sortOrder =
                SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_NAME + " DESC";

        Cursor cursor = db.query(
                SqlHandle.FeedShortCut.SHORTCUT_TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        while (cursor.moveToNext()) {
            String name = cursor.getString(
                    cursor.getColumnIndexOrThrow(SHORTCUT_COLUMN_NAME_NAME)

            );
            String link = cursor.getString(
                    cursor.getColumnIndexOrThrow(SHORTCUT_COLUMN_NAME_LINK)
            );
            String img = cursor.getString(
                    cursor.getColumnIndexOrThrow(SHORTCUT_COLUMN_NAME_IMAGE)
            );
            adapter.addItem(new ListItem(name, link, img));

        }
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                final ListItem item = (ListItem) adapter.getItem(i);
                final String name = item.getName();
                showDeleteDialog(name, editor, item, adapter);

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


        Button confirm = (Button) NewShortCutDialog.findViewById(R.id.addNewShortCutBtn);
        Button cancel = (Button) NewShortCutDialog.findViewById(R.id.cancelNewShortCutBtn);
        Button find = (Button) NewShortCutDialog.findViewById(R.id.openFileExploreBtn);
        EditText NewName = (EditText) NewShortCutDialog.findViewById(R.id.shortcut_name);
        EditText NewLink = (EditText) NewShortCutDialog.findViewById(R.id.shortcut_link);
        NewImg = (EditText) NewShortCutDialog.findViewById(R.id.shortcut_imgPath);
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1);

            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.getCount() <= 25) {
                    SQLiteDatabase db = dbHelper.getWritableDatabase();
                    SharedPreferences.Editor ed = editor;
                    String name = NewName.getText().toString();
                    String link = NewLink.getText().toString();

                    ContentValues values = new ContentValues();
                    values.put(SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_NAME, name);
                    values.put(SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_LINK, link);
                    values.put(SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_IMAGE, url.toString());
                    values.put(SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_TYPE, "0");

                    db.insert(SqlHandle.FeedShortCut.SHORTCUT_TABLE_NAME, null, values);

                    db.close();
                    adapter.addItem(new ListItem(name, link, url.toString()));
                    Toast.makeText(getApplicationContext(), "새로운 바로가기가 등록되었습니다.", Toast.LENGTH_SHORT).show();
                    //ed.apply();
                    if(!img_url.equals("null")){
                        img_url="null";
                    }
                    Intent intent = new Intent(EditShortCutActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "더 이상 추가가 불가능합니다.", Toast.LENGTH_SHORT).show();
                    NewShortCutDialog.dismiss();
                }


            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewShortCutDialog.dismiss();
            }
        });

        NewShortCutDialog.show();
    }


    public void showDeleteDialog(String ShortCutName, SharedPreferences.Editor editor, ListItem item, ListItemAdapter adapter) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("'" + ShortCutName + "'" + " 삭제");
        builder.setMessage("정말로 삭제 하시겠습니까?");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String name = ShortCutName;

                        SQLiteDatabase db = dbHelper.getWritableDatabase();
                        String selection = SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_NAME + " LIKE ?";
                        String[] selectionArgs = {name};
                        db.delete(SqlHandle.FeedShortCut.SHORTCUT_TABLE_NAME, selection, selectionArgs);
                        db.close();

                        Intent intent = new Intent(EditShortCutActivity.this, MainActivity.class);
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

    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode != 1 || resultCode != Activity.RESULT_OK) {
            Toast.makeText(getApplicationContext(), "이미지를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();

            return;
        }
        Uri SelectedFile = data.getData();
        img_url="check";

        url = SelectedFile;
        NewImg.setText(url.toString());
        int flag = Intent.FLAG_GRANT_READ_URI_PERMISSION;
        ContentResolver resolver = this.getContentResolver();

        resolver.takePersistableUriPermission(data.getData(),flag);
    }

    private String uri_path(Uri uri){
        String res = null;
        String[] image_data = {MediaStore.Images.Media.DATA};
        Cursor cur = getContentResolver().query(uri,image_data,null,null,null);

        if(cur.moveToFirst()){
            int col = cur.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cur.getString(col);
        }
        cur.close();
        return res;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private String getRealPathFromURI(Uri contentUri) {
        if (contentUri.getPath().startsWith("/storage")) {
            return contentUri.getPath();
        }

        String id = DocumentsContract.getDocumentId(contentUri).split(":")[1];
        String[] columns = { MediaStore.Files.FileColumns.DATA };
        String selection = MediaStore.Files.FileColumns._ID + " = " + id;
        Cursor cursor = getContentResolver().query(MediaStore.Files.getContentUri("external"), columns, selection, null, null);
        try {
            int columnIndex = cursor.getColumnIndex(columns[0]);
            if (cursor.moveToFirst()) {
                return cursor.getString(columnIndex);
            }
        } finally {
            cursor.close();
        }
        return "null";
    }


}

