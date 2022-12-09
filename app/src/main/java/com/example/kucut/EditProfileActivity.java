package com.example.kucut;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

public class EditProfileActivity extends AppCompatActivity {

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    Button Btn_confirm; //확인 버튼
    EditText editUserName;
    EditText editUserid;
    EditText editUserpw;
    EditText editStudentNumber;
    Button btnEditProfilePasswordVisible;

    Spinner collegeSpinner;
    ArrayAdapter collegeSpinnerAdapter;

    Spinner departmentSpinner;
    ArrayAdapter departmentSpinnerAdapter;
    String college_text;
    String department_text;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("프로필 수정");
        actionBar.setDisplayHomeAsUpEnabled(true);
        editUserName = findViewById(R.id.editUserName);
        editUserid = findViewById(R.id.editUserid);
        editStudentNumber = findViewById(R.id.editStudentNumber);
        btnEditProfilePasswordVisible = findViewById(R.id.btnEditProfilePasswordVisible);

        Btn_confirm = (Button) findViewById(R.id.btnEditProfileConfirm);
        SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        String[] collegeItems = getResources().getStringArray(R.array.colleges);
        String[] departmentItems = getResources().getStringArray(R.array.departments2);
        List<String> collegeList = Arrays.asList(getResources().getStringArray(R.array.colleges));
        List<String> departmentList = Arrays.asList(getResources().getStringArray(R.array.departments));
        collegeSpinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,collegeItems);
        collegeSpinner = (Spinner) findViewById(R.id.collegeSpinner);
        collegeSpinner.setAdapter(collegeSpinnerAdapter);

        editUserid.setText(pref.getString("userid",""));
        editUserName.setText(pref.getString("userName",""));
        editStudentNumber.setText(pref.getString("studentNumber",""));

        departmentSpinnerAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,departmentItems);
        departmentSpinner = (Spinner) findViewById(R.id.departmentSpinner2);
        departmentSpinner.setAdapter(departmentSpinnerAdapter);
        if(!pref.getString("department","").isEmpty()){
            int index = departmentList.indexOf(pref.getString("department",""));

            departmentSpinner.setSelection(index);
        }
        if(!pref.getString("college","").isEmpty()){
            int index = collegeList.indexOf(pref.getString("college",""));
            collegeSpinner.setSelection(index);
        }
        collegeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
                final String selectedText = (String) adapterView.getItemAtPosition(i);
                college_text = selectedText;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        departmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
                final String selectedText = (String) adapterView.getItemAtPosition(i);
                department_text = selectedText;

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "확인", Toast.LENGTH_SHORT).show();



                editor.putString("userid",editUserid.getText().toString());
                editor.putString("userName",editUserName.getText().toString());
                Log.d("studentNumber : ",editStudentNumber.getText().toString() );
                editor.putString("studentNumber",editStudentNumber.getText().toString());
                editor.putString("college",college_text);
                editor.putString("department",department_text);
                editor.apply();




                Intent intent = new Intent(EditProfileActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

            }

        });



        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);

            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                //성공시 비밀번호를 가져와서 팝업창으로 가져옴
                showPasswordDialog();

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(),"지문 인식에 실패하여 비밀번호를 확인하실 수 없습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("지문 인증")
                .setSubtitle("기기에 등록된 지문을 이용하여 지문을 인증해 주세요.")
                .setNegativeButtonText("취소")
                .setDeviceCredentialAllowed(false)
                .build();

        btnEditProfilePasswordVisible.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                biometricPrompt.authenticate(promptInfo);
            }
        });
    }
    public void showPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("비밀번호");
        //입력창 및 기본 값 추가;
        SharedPreferences pref = getSharedPreferences("pref",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        final TextView vi = new TextView(this);
        final EditText et = new EditText(this);
        et.setText(pref.getString("userpw",""));
        builder.setView(vi);
        builder.setView(et);
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "확인", Toast.LENGTH_LONG).show();
                        //EditText에 있는 password 가져와서 SharedPreference에 저장
                        if(!et.getText().toString().equals(pref.getString("userpw",""))){
                            editor.putString("userpw",et.getText().toString());
                            editor.apply();
                        }
                    }
                });
        builder.show();
    }

}
