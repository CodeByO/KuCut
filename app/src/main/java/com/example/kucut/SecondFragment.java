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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import java.util.Map;
import java.util.concurrent.Executor;
import android.content.SharedPreferences;
import static android.content.Context.MODE_PRIVATE;


public class SecondFragment  extends Fragment {

    private String title;
    private int page;
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;


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
        TextView userName = (TextView) view.findViewById(R.id.userName);
        TextView student_number = (TextView) view.findViewById(R.id.student_number);
        TextView userid = (TextView) view.findViewById(R.id.userid);
        TextView department = (TextView) view.findViewById(R.id.department);
        Button editProfile = (Button) view.findViewById(R.id.editProfile);
        ImageView collegeImage = (ImageView) view.findViewById(R.id.collegeImage);
        Button confirmPassword = (Button) view.findViewById(R.id.confirmPassword);
        SharedPreferences pref = this.getActivity().getSharedPreferences("pref",MODE_PRIVATE);

        if(pref.getString("userName","").isEmpty()){
            userName.setText("NoName");
        }else{
            userName.setText(pref.getString("userName",""));
        }
        if(pref.getString("studentNumber","").isEmpty()){
            student_number.setText("학번 : 저장된 학번이 없습니다!");
        }else{
            student_number.setText("학번 : "+pref.getString("studentNumber",""));
        }
        if(pref.getString("userid","").isEmpty()){
            userid.setText("아이디 : 저장된 아이디가 없습니다!");
        }else{
            userid.setText("아이디 : "+pref.getString("userid",""));
        }
        if(pref.getString("department","").isEmpty()){
            department.setText("저장된 학과가 없습니다!");
        }else{
            department.setText(pref.getString("department",""));
        }
        if(pref.getString("college","").isEmpty()){
            collegeImage.setImageResource(R.mipmap.ic_launcher);
        }else{
            String college = pref.getString("college","");
            Log.d("selected college : ",college);
                if(college.equals("과학기술대학")){
                    collegeImage.setImageResource(R.drawable.science);
                }else if(college.equals("약학대학")){
                    collegeImage.setImageResource(R.drawable.madicine);
                }else if(college.equals("글로벌비즈니스대학")){
                    collegeImage.setImageResource(R.drawable.global_bussiness);
                }else if(college.equals("공공정책대학")){
                    collegeImage.setImageResource(R.drawable.public_policy);
                }else if(college.equals("문화스포츠대학")){
                    collegeImage.setImageResource(R.drawable.culture_and_sports);
                }else if(college.equals("스마트도시학부")){
                    collegeImage.setImageResource(R.mipmap.ic_launcher);
                }else{
                    collegeImage.setImageResource(R.mipmap.ic_launcher);
                }
        }

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),EditProfileActivity.class);
                startActivity(intent);
            }
        });

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
        confirmPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                biometricPrompt.authenticate(promptInfo);
            }
        });



        return view;
    }
    // 지문 인식 통과 후 수정 및 확인 다이알로그
    public void showPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setTitle("비밀번호");
        //입력창 및 기본 값 추가;
        SharedPreferences pref = this.getActivity().getSharedPreferences("pref",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        final TextView vi = new TextView(this.getActivity());
        final EditText et = new EditText(this.getActivity());
        et.setText(pref.getString("userpw",""));
        builder.setView(vi);
        builder.setView(et);
        builder.setPositiveButton("확인",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
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

