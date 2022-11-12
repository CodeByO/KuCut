package com.example.kucut;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.concurrent.Executor;
import android.content.SharedPreferences;
import static android.content.Context.MODE_PRIVATE;


public class SecondFragment  extends Fragment {
    // Store instance variables
    private String title;
    private int page;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

   // SharedPreferences pref = getActivity().getSharedPreferences("pref", Context.MODE_PRIVATE);
   // SharedPreferences.Editor editor = pref.edit();
    // newInstance constructor for creating fragment with arguments
    public static SecondFragment newInstance(int page, String title) {
        SecondFragment fragment = new SecondFragment();
        Bundle args = new Bundle();
        args.putInt("1", page);
        args.putString("profile", title);
        fragment.setArguments(args);
        return fragment;
    }

    // Store instance variables based on arguments passed
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("1", 0);
        title = getArguments().getString("profile");
    }

    // Inflate the view for the fragment based on layout XML
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_second, container, false);
        ImageButton ShowPassword;
        // 각 TextView 가져오기
        TextView EditProfile;
        ImageView departmentImage = (ImageView) view.findViewById(R.id.departmentImage);

        // 가져온 TextView에서 setText()해서 프로필 업데이트 만약 sharedPreference에 없으면 None을 붙이기
        executor = ContextCompat.getMainExecutor(getContext());
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
                Toast.makeText(getContext(),"지문 인식에 실패하여 비밀번호를 확인하실 수 없습니다.",Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("지문 인증")
                .setSubtitle("기기에 등록된 지문을 이용하여 지문을 인증해 주세요.")
                .setNegativeButtonText("취소")
                .setDeviceCredentialAllowed(false)
                .build();



        ShowPassword = (ImageButton) view.findViewById(R.id.showPassword);
        EditProfile = (TextView) view.findViewById(R.id.editProfile);

        ShowPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                biometricPrompt.authenticate(promptInfo);
            }
        });
        EditProfile.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                //EditShortCutActivity 실행

                return false;
            }
        });

      //바로가기 편집 버튼 제어
        /*
        final Intent editIntent = new Intent(MainActivity.this, EditShortCutActivity.class);
        editShortCut = (Button) findViewById(R.id.btnEdit);
        editShortCut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(editIntent);
            }
        });
        final Intent profileIntent = new Intent(MainActivity.this, EditProfileActivity.class);
        EditProfile = (TextView) findViewById(R.id.editProfile);
        EditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(profileIntent);
            }
        });

         */
        return view;
    }
    // 지문 인식 통과 후 수정 및 확인 다이알로그
    public void showPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
                        Toast.makeText(getContext(), "확인", Toast.LENGTH_LONG).show();
                        //EditText에 있는 password 가져와서 SharedPreference에 저장
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getContext(), "취소", Toast.LENGTH_LONG).show();
                    }
                });
        builder.show();
    }
}

