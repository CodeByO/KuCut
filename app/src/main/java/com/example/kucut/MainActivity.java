package com.example.kucut;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import me.relex.circleindicator.CircleIndicator;

import static android.hardware.biometrics.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static android.hardware.biometrics.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import static com.example.kucut.SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_NAME;
import static com.example.kucut.SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_LINK;
import static com.example.kucut.SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_IMAGE;
import static com.example.kucut.SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_IMAGE;
import static com.example.kucut.SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_TYPE;
import static com.example.kucut.SqlHandle.FeedShortCut.SHORTCUT_TABLE_NAME;

public class MainActivity extends AppCompatActivity {
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    Button editShortCut;//???????????? ??????
    TextView EditProfile;// ????????????
    ImageButton ShowPassword;

    Spinner spinner;
    ArrayAdapter spinnerAdapter;
    //ListView Adapter
    FragmentPagerAdapter adapterViewPager;
    private long presstime = 0;
    private CircleIndicator indicator;

    //?????????????????? ??????
    DbHelper dbHelper = new DbHelper(MainActivity.this);

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Spinner
        String[] items = getResources().getStringArray(R.array.departments);
        spinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,items);
        spinner = (Spinner) findViewById(R.id.departmentSpinner);
        spinner.setAdapter(spinnerAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                final String selectedText = (String) adapterView.getItemAtPosition(i);
                String selectedLink="null";
                SQLiteDatabase db = dbHelper.getReadableDatabase();

                String[] projection = {
                        SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_NAME,
                        SqlHandle.FeedShortCut.SHORTCUT_COLUMN_NAME_LINK,
                };

                String selection = SHORTCUT_COLUMN_NAME_NAME + " = ?";
                String[] selectionArgs = {selectedText};
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

                if(cursor.moveToFirst()){
                    selectedLink = cursor.getString(
                            cursor.getColumnIndexOrThrow(SHORTCUT_COLUMN_NAME_LINK)
                    );

                }


                SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
                //final String selectedLink = pref.getString(selectedText,"");
                if(!selectedLink.equals("null")){


                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(selectedLink));
                        startActivity(intent);
                    }catch (Exception e){

                        Toast.makeText(getApplicationContext(),"????????? ?????? ?????????.",Toast.LENGTH_SHORT).show();

                    }



                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //ListView ??? ??????????????? ???????????? ?????? ??????

        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        indicator = findViewById(R.id.indicator);
        indicator.setViewPager(vpPager);


        //?????? ?????? ?????? ?????? ?????? ??????
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(getApplicationContext(),"?????? ????????? ????????? ???????????????.",Toast.LENGTH_SHORT).show();
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(getApplicationContext(),"?????? ????????? ????????? ??? ????????????.",Toast.LENGTH_SHORT).show();
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                break;
        }
        //???????????? ?????? ?????? ??????

        final Intent editIntent = new Intent(MainActivity.this, EditShortCutActivity.class);
        editShortCut = (Button) findViewById(R.id.btnEdit);
        editShortCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(editIntent);
            }
        });


        //??? ?????? ??? ?????? ????????? ??????(Use SharedPreference
        SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);

        if(pref.getString("check","").isEmpty()){
            SQLiteDatabase db = dbHelper.getWritableDatabase();


            // ??? ?????? ????????? ?????? ???????????? URL ????????? ?????? DB??? ?????? ?????????

            String SQL_INSERT_MANY = "INSERT INTO shortcut (name,link,image,type) SELECT ";

            SQL_INSERT_MANY = SQL_INSERT_MANY + "\"????????????\", \""+BasicLink.FeedLink.HOME_PAGE+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"??????\", \""+BasicLink.FeedLink.PORTAL_PAGE+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"?????????\", \""+BasicLink.FeedLink.KUSEUM_PAGE+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"????????????\", \""+BasicLink.FeedLink.BLACKBOARD_PAGE+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"?????????????????????\", \""+BasicLink.FeedLink.ACADEMIC_INFO+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"?????????\", \""+BasicLink.FeedLink.WEB_MAIL+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"????????????\", \""+BasicLink.FeedLink.GRADE_LOOKUP+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"????????????\", \""+BasicLink.FeedLink.ACADEMIC_CALENDAR+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"????????? ??????\", \""+BasicLink.FeedLink.CERTIFICATE_APPLICATION+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"????????????\", \""+BasicLink.FeedLink.PARTICIPATION_YARD+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"???????????? ????????????\", \""+BasicLink.FeedLink.COUNSELING_CENTER+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"???????????? ????????????\", \""+BasicLink.FeedLink.TEACHING_LEARNING+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"???????????? ????????????\", \""+BasicLink.FeedLink.ENTREPRENEURSHIP_EDUCATION+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"?????? ????????? ??????\", \""+BasicLink.FeedLink.JOB_CENTER+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"?????? ?????? ?????????\", \""+BasicLink.FeedLink.COMMUNITY_SERVICE+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"????????????\", \""+BasicLink.FeedLink.DORMITORY_PAGE+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"?????? ?????? ??????\", \""+BasicLink.FeedLink.GENERAL_NOTICE+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"?????? ?????? ??????\", \""+BasicLink.FeedLink.EVENT_NOTICE+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"????????? 19 ??????\", \""+BasicLink.FeedLink.COVID19_NOTICE+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"????????? ??????/??????\", \""+BasicLink.FeedLink.READING_ROOM+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"???????????? ?????????\", \""+BasicLink.FeedLink.SHUTTLE_BUS+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"?????? ?????? ?????????\", \""+BasicLink.FeedLink.CAMPUS_CAFETERIA+"\",\"null\","+"\"0\"";


            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"???????????????????????????\", \""+BasicLink.DepartmentLink.DATA_CALCULATE+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"?????????????????????????????????\", \""+BasicLink.DepartmentLink.ARTIFICIAL_SECURITY+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"????????????????????????????????\", \""+BasicLink.DepartmentLink.DISPLAY_SEMICONDUCTOR+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"??????????????????\", \""+BasicLink.DepartmentLink.MATERIAL_CHEMISTRY+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"????????????????????????????????????\", \""+BasicLink.DepartmentLink.COMPUTER_SOFTWARE+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"?????????????????????\", \""+BasicLink.DepartmentLink.ELECTRONIC_INFORMATION+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"?????????????????????\", \""+BasicLink.DepartmentLink.LIFE_INFORMATION+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"?????????????????????\", \""+BasicLink.DepartmentLink.FOOD_LIFE+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"?????????????????????????????\", \""+BasicLink.DepartmentLink.ELECTRONIC_MACHINE+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"????????????????????????\", \""+BasicLink.DepartmentLink.ENVIRONMENT_SYSTEM+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"???????????????\", \""+BasicLink.DepartmentLink.FREE_ENGINEER+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"????????????????????????\", \""+BasicLink.DepartmentLink.FUTURE_MOBILITY+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"???????????????????????????\", \""+BasicLink.DepartmentLink.INTELLIGENT_SEMICONDUCTOR+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"??????????????????\", \""+BasicLink.DepartmentLink.GOVERNMENT_ADMINISTRATION+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"?????????????????????\", \""+BasicLink.DepartmentLink.PUBLIC_SOCIAL+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"????????????????????????\", \""+BasicLink.DepartmentLink.UNIFICATION_DIPLOMACY+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"?????????????????????\", \""+BasicLink.DepartmentLink.ECONOMIC_POLICY+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"??????????????????????????????\", \""+BasicLink.DepartmentLink.BIG_DATA+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"?????????????????????\", \""+BasicLink.DepartmentLink.INTERNATIONAL_SPORT+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"????????????????????????\", \""+BasicLink.DepartmentLink.CULTURE_HERITAGE+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"?????????????????????\", \""+BasicLink.DepartmentLink.CULTURE_CONTENT+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"???????????????\", \""+BasicLink.DepartmentLink.KOREA_STUDIES+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"???????????????\", \""+BasicLink.DepartmentLink.CHINA_STUDIES+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"???????????????\", \""+BasicLink.DepartmentLink.ENGLISH_STUDIES+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"???????????????\", \""+BasicLink.DepartmentLink.GERMAN_STUDIES+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"?????????????????????\", \""+BasicLink.DepartmentLink.GLOBAL_MANAGEMENT+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"?????????????????????\", \""+BasicLink.DepartmentLink.DIGITAL_MANAGEMENT+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"??????????????????\", \""+BasicLink.DepartmentLink.BASIC_INTELLIGENCE+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"?????????\", \""+BasicLink.DepartmentLink.MEDICINE_STUDIES+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"?????????????????????\", \""+BasicLink.DepartmentLink.SMART_CITY+"\",\"null\","+"\"1\"";

            db.execSQL(SQL_INSERT_MANY);
            SharedPreferences.Editor editor = pref.edit();


            //?????? ??????
            editor.putString("userid","");
            editor.putString("userpw","");
            editor.putString("department","");
            editor.putString("college","");
            editor.putString("userName","");
            editor.putString("studentNumber","");



            //??????????????? ?????? ?????? ??????
            editor.putString("check","exist");

            editor.commit();
            db.close();
        }

    }

    // ?????? ?????? ?????? ??? ?????? ??? ?????? ???????????????
    public void showPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("????????????");

        builder.setMessage("??????????????????????");
        builder.setPositiveButton("??????",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "??????", Toast.LENGTH_LONG).show();
                    }
                });
        builder.setNegativeButton("??????",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "??????", Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }
    //?????? ?????? ?????? ?????? ??????
    @Override
    public void onBackPressed() {

        if(System.currentTimeMillis() > presstime + 2000){
            presstime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "????????? ???????????? ?????? ???????????????", Toast.LENGTH_SHORT).show();
            return;
       }
        if((System.currentTimeMillis() <= presstime + 2000)){
            finish();
        }
    }
    //swap ??? ?????????
    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return FirstFragment.newInstance(0, "ListView");
                case 1:
                    return SecondFragment.newInstance(1, "profile");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }

    }
}