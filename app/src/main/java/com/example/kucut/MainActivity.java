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
    Button editShortCut;//바로가기 편집
    TextView EditProfile;// 수정버튼
    ImageButton ShowPassword;

    Spinner spinner;
    ArrayAdapter spinnerAdapter;
    //ListView Adapter
    FragmentPagerAdapter adapterViewPager;
    private long presstime = 0;
    private CircleIndicator indicator;

    //데이터베이스 설정
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
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(selectedLink));
                    startActivity(intent);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //ListView 및 마이페이지 스와이프 화면 구성

        ViewPager vpPager = (ViewPager) findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);
        indicator = findViewById(R.id.indicator);
        indicator.setViewPager(vpPager);


        //지문 인식 사용 가능 여부 확인
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(getApplicationContext(),"지문 사용이 불가한 기기입니다.",Toast.LENGTH_SHORT).show();
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(getApplicationContext(),"현재 지문을 사용할 수 없습니다.",Toast.LENGTH_SHORT).show();
                android.os.Process.killProcess(android.os.Process.myPid());
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                break;
        }
        //바로가기 편집 버튼 제어

        final Intent editIntent = new Intent(MainActivity.this, EditShortCutActivity.class);
        editShortCut = (Button) findViewById(R.id.btnEdit);
        editShortCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(editIntent);
            }
        });


        //앱 설치 후 최초 실행시 동작(Use SharedPreference
        SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);

        if(pref.getString("check","").isEmpty()){
            SQLiteDatabase db = dbHelper.getWritableDatabase();



            String SQL_INSERT_MANY = "INSERT INTO shortcut (name,link,image,type) SELECT ";

            SQL_INSERT_MANY = SQL_INSERT_MANY + "\"홈페이지\", \""+BasicLink.FeedLink.HOME_PAGE+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"포탈\", \""+BasicLink.FeedLink.PORTAL_PAGE+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"쿠세움\", \""+BasicLink.FeedLink.KUSEUM_PAGE+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"블랙보드\", \""+BasicLink.FeedLink.BLACKBOARD_PAGE+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"세종학술정보원\", \""+BasicLink.FeedLink.ACADEMIC_INFO+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"웹메일\", \""+BasicLink.FeedLink.WEB_MAIL+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"성적조회\", \""+BasicLink.FeedLink.GRADE_LOOKUP+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"학사일정\", \""+BasicLink.FeedLink.ACADEMIC_CALENDAR+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"증명서 신청\", \""+BasicLink.FeedLink.CERTIFICATE_APPLICATION+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"참여마당\", \""+BasicLink.FeedLink.PARTICIPATION_YARD+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"세종학생 상담센터\", \""+BasicLink.FeedLink.COUNSELING_CENTER+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"교수학습 지원센터\", \""+BasicLink.FeedLink.TEACHING_LEARNING+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"세종창업 교육센터\", \""+BasicLink.FeedLink.ENTREPRENEURSHIP_EDUCATION+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"대학 일자리 센터\", \""+BasicLink.FeedLink.JOB_CENTER+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"세종 사회 봉사단\", \""+BasicLink.FeedLink.COMMUNITY_SERVICE+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"호연학사\", \""+BasicLink.FeedLink.DORMITORY_PAGE+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"학사 일반 공지\", \""+BasicLink.FeedLink.GENERAL_NOTICE+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"교내 행사 공지\", \""+BasicLink.FeedLink.EVENT_NOTICE+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"코로나 19 공지\", \""+BasicLink.FeedLink.COVID19_NOTICE+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"열람실 현황/배정\", \""+BasicLink.FeedLink.READING_ROOM+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"셔틀버스 시간표\", \""+BasicLink.FeedLink.SHUTTLE_BUS+"\",\"null\","+"\"0\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"교내 식당 식단표\", \""+BasicLink.FeedLink.CAMPUS_CAFETERIA+"\",\"null\","+"\"0\"";


            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"데이터계산과학전공\", \""+BasicLink.DepartmentLink.DATA_CALCULATE+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"인공지능사이버보안학과\", \""+BasicLink.DepartmentLink.ARTIFICIAL_SECURITY+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"디스플레이·반도체학부\", \""+BasicLink.DepartmentLink.DISPLAY_SEMICONDUCTOR+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"신소재화학과\", \""+BasicLink.DepartmentLink.MATERIAL_CHEMISTRY+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"컴퓨터융합소프트웨어학과\", \""+BasicLink.DepartmentLink.COMPUTER_SOFTWARE+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"전자정보공학과\", \""+BasicLink.DepartmentLink.ELECTRONIC_INFORMATION+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"생명정보공학과\", \""+BasicLink.DepartmentLink.LIFE_INFORMATION+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"식품생명공학과\", \""+BasicLink.DepartmentLink.FOOD_LIFE+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"잔자·기계융합공학과\", \""+BasicLink.DepartmentLink.ELECTRONIC_MACHINE+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"환경시스템공학과\", \""+BasicLink.DepartmentLink.ENVIRONMENT_SYSTEM+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"자유공학부\", \""+BasicLink.DepartmentLink.FREE_ENGINEER+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"미래모빌리티학과\", \""+BasicLink.DepartmentLink.FUTURE_MOBILITY+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"지능형반도체공학과\", \""+BasicLink.DepartmentLink.INTELLIGENT_SEMICONDUCTOR+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"정부행정학부\", \""+BasicLink.DepartmentLink.GOVERNMENT_ADMINISTRATION+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"공공사회학전공\", \""+BasicLink.DepartmentLink.PUBLIC_SOCIAL+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"통일외교안보전공\", \""+BasicLink.DepartmentLink.UNIFICATION_DIPLOMACY+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"경제정책학전공\", \""+BasicLink.DepartmentLink.ECONOMIC_POLICY+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"빅데이터사이언스학부\", \""+BasicLink.DepartmentLink.BIG_DATA+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"국제스포츠학부\", \""+BasicLink.DepartmentLink.INTERNATIONAL_SPORT+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"문화유산융합학부\", \""+BasicLink.DepartmentLink.CULTURE_HERITAGE+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"문화컨텐츠전공\", \""+BasicLink.DepartmentLink.CULTURE_CONTENT+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"한국학전공\", \""+BasicLink.DepartmentLink.KOREA_STUDIES+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"중국학전공\", \""+BasicLink.DepartmentLink.CHINA_STUDIES+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"영미학전공\", \""+BasicLink.DepartmentLink.ENGLISH_STUDIES+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"독일학전공\", \""+BasicLink.DepartmentLink.GERMAN_STUDIES+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"글로벌경영전공\", \""+BasicLink.DepartmentLink.GLOBAL_MANAGEMENT+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"디지털경영전공\", \""+BasicLink.DepartmentLink.DIGITAL_MANAGEMENT+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"표준지식학과\", \""+BasicLink.DepartmentLink.BASIC_INTELLIGENCE+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"약학과\", \""+BasicLink.DepartmentLink.MEDICINE_STUDIES+"\",\"null\","+"\"1\"";
            SQL_INSERT_MANY = SQL_INSERT_MANY + " UNION SELECT " +"\"스마트도시학부\", \""+BasicLink.DepartmentLink.SMART_CITY+"\",\"null\","+"\"1\"";

            db.execSQL(SQL_INSERT_MANY);
            SharedPreferences.Editor editor = pref.edit();
//            //ListItem에 추가될 요소들
//            editor.putString("홈페이지_**",BasicLink.FeedLink.HOME_PAGE);
//            editor.putString("포탈_**",BasicLink.FeedLink.PORTAL_PAGE);
//            editor.putString("쿠세움_**",BasicLink.FeedLink.KUSEUM_PAGE);
//            editor.putString("블랙보드_**",BasicLink.FeedLink.BLACKBOARD_PAGE);
//            editor.putString("세종 학술 정보원_**",BasicLink.FeedLink.ACADEMIC_INFO);
//            editor.putString("웹메일_**",BasicLink.FeedLink.WEB_MAIL);
//            editor.putString("성적조회_**",BasicLink.FeedLink.GRADE_LOOKUP);
//            editor.putString("학사일정_**",BasicLink.FeedLink.ACADEMIC_CALENDAR);
//            editor.putString("증성서 신청_**",BasicLink.FeedLink.CERTIFICATE_APPLICATION);
//            editor.putString("참여마당_**",BasicLink.FeedLink.PARTICIPATION_YARD);
//            editor.putString("세종학생 상담센터_**",BasicLink.FeedLink.COUNSELING_CENTER);
//            editor.putString("교수학습 지원센터_**",BasicLink.FeedLink.TEACHING_LEARNING);
//            editor.putString("세종창업 교육센터_**",BasicLink.FeedLink.ENTREPRENEURSHIP_EDUCATION);
//            editor.putString("대학 일자리 센터_**",BasicLink.FeedLink.JOB_CENTER);
//            editor.putString("세종 사회 봉사단_**",BasicLink.FeedLink.COMMUNITY_SERVICE);
//            editor.putString("호연학사_**",BasicLink.FeedLink.DORMITORY_PAGE);
//            editor.putString("학사 일반 공지_**",BasicLink.FeedLink.GENERAL_NOTICE);
//            editor.putString("교내 행사 공지_**",BasicLink.FeedLink.EVENT_NOTICE);
//            editor.putString("코로나 19 공지_**",BasicLink.FeedLink.COVID19_NOTICE);
//            editor.putString("열람실 현황/배정_**",BasicLink.FeedLink.READING_ROOM);
//            editor.putString("셔틀버스 시간표_**",BasicLink.FeedLink.SHUTTLE_BUS);
//            editor.putString("교내 식당 식단표_**",BasicLink.FeedLink.CAMPUS_CAFETERIA);
//
//            //Spinner에 추가될 요소들
//            editor.putString("데이터계산과학전공",BasicLink.DepartmentLink.DATA_CALCULATE);
//            editor.putString("인공지능사이버보안학과",BasicLink.DepartmentLink.ARTIFICIAL_SECURITY);
//            editor.putString("디스플레이·반도체물리학부",BasicLink.DepartmentLink.DISPLAY_SEMICONDUCTOR);
//            editor.putString("신소재화학과",BasicLink.DepartmentLink.MATERIAL_CHEMISTRY);
//            editor.putString("컴퓨터융합소프트웨어학과",BasicLink.DepartmentLink.COMPUTER_SOFTWARE);
//            editor.putString("전자정보공학과",BasicLink.DepartmentLink.ELECTRONIC_INFORMATION);
//            editor.putString("생명정보공학과",BasicLink.DepartmentLink.LIFE_INFORMATION);
//            editor.putString("식품생명공학과",BasicLink.DepartmentLink.FOOD_LIFE);
//            editor.putString("전자·기계융합공학과",BasicLink.DepartmentLink.ELECTRONIC_MACHINE);
//            editor.putString("환경시스템공학과",BasicLink.DepartmentLink.ENVIRONMENT_SYSTEM);
//            editor.putString("자유공학부",BasicLink.DepartmentLink.FREE_ENGINEER);
//            editor.putString("미래모빌리티학과",BasicLink.DepartmentLink.FUTURE_MOBILITY);
//            editor.putString("지능형반도체공학과",BasicLink.DepartmentLink.INTELLIGENT_SEMICONDUCTOR);
//            editor.putString("정부행정학부",BasicLink.DepartmentLink.GOVERNMENT_ADMINISTRATION);
//            editor.putString("공공사회학전공",BasicLink.DepartmentLink.PUBLIC_SOCIAL);
//            editor.putString("통일외교안보전공",BasicLink.DepartmentLink.UNIFICATION_DIPLOMACY);
//            editor.putString("경제정책학정공",BasicLink.DepartmentLink.ECONOMIC_POLICY);
//            editor.putString("빅테이터사이언스학부",BasicLink.DepartmentLink.BIG_DATA);
//            editor.putString("국제스포츠학부",BasicLink.DepartmentLink.INTERNATIONAL_SPORT);
//            editor.putString("문화유산융합학부",BasicLink.DepartmentLink.CULTURE_HERITAGE);
//            editor.putString("문화컨텐츠전공",BasicLink.DepartmentLink.CULTURE_CONTENT);
//            editor.putString("한국학전공",BasicLink.DepartmentLink.KOREA_STUDIES);
//            editor.putString("중국학전공",BasicLink.DepartmentLink.CHINA_STUDIES);
//            editor.putString("영미학전공",BasicLink.DepartmentLink.ENGLISH_STUDIES);
//            editor.putString("독일학전공",BasicLink.DepartmentLink.GERMAN_STUDIES);
//            editor.putString("글로벌경영전공",BasicLink.DepartmentLink.GLOBAL_MANAGEMENT);
//            editor.putString("디지털경영전공",BasicLink.DepartmentLink.DIGITAL_MANAGEMENT);
//            editor.putString("표준지식학과",BasicLink.DepartmentLink.BASIC_INTELLIGENCE);
//            editor.putString("약학과",BasicLink.DepartmentLink.MEDICINE_STUDIES);
//            editor.putString("스마트도시학부",BasicLink.DepartmentLink.SMART_CITY);


            //개인 정보
            editor.putString("userid","");
            editor.putString("userpw","");
            editor.putString("department","");
            editor.putString("college","");
            editor.putString("userName","");
            editor.putString("studentNumber","");



            //중복실행을 막기 위해 추가
            editor.putString("check","exist");

            editor.commit();
            db.close();
        }

    }

    // 지문 인식 통과 후 수정 및 확인 다이알로그
    public void showPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("비밀번호");
        //입력창 및 기본 값 추가;
        /*
            final EditText et = new EditText(getApplicationContext());
            .setVIew(et);
            et.setText(password);
            확인 버튼 틀릭시
            et.getText().toString();
            디비에 업데이트
         */
        builder.setMessage("확인하였습니까?");
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "확인", Toast.LENGTH_LONG).show();
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
    //하단 뒤로 가기 버튼 제어
    @Override
    public void onBackPressed() {

        if(System.currentTimeMillis() > presstime + 2000){
            presstime = System.currentTimeMillis();
            Toast.makeText(getApplicationContext(), "한번더 누르시면 앱이 종료됩니다", Toast.LENGTH_SHORT).show();
            return;
       }
        if((System.currentTimeMillis() <= presstime + 2000)){
            finish();
        }
    }
    //swap 뷰 어댑터
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