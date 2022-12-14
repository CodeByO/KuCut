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
        // ??? TextView ????????????
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
            student_number.setText("?????? : ????????? ????????? ????????????!");
        }else{
            student_number.setText("?????? : "+pref.getString("studentNumber",""));
        }
        if(pref.getString("userid","").isEmpty()){
            userid.setText("????????? : ????????? ???????????? ????????????!");
        }else{
            userid.setText("????????? : "+pref.getString("userid",""));
        }
        if(pref.getString("department","").isEmpty()){
            department.setText("????????? ????????? ????????????!");
        }else{
            department.setText(pref.getString("department",""));
        }
        if(pref.getString("college","").isEmpty()){
            collegeImage.setImageResource(R.mipmap.ic_launcher);
        }else{
            String college = pref.getString("college","");
            Log.d("selected college : ",college);
                if(college.equals("??????????????????")){
                    collegeImage.setImageResource(R.drawable.science);
                }else if(college.equals("????????????")){
                    collegeImage.setImageResource(R.drawable.madicine);
                }else if(college.equals("???????????????????????????")){
                    collegeImage.setImageResource(R.drawable.global_bussiness);
                }else if(college.equals("??????????????????")){
                    collegeImage.setImageResource(R.drawable.public_policy);
                }else if(college.equals("?????????????????????")){
                    collegeImage.setImageResource(R.drawable.culture_and_sports);
                }else if(college.equals("?????????????????????")){
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

        // ????????? TextView?????? setText()?????? ????????? ???????????? ?????? sharedPreference??? ????????? None??? ?????????
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
                //????????? ??????????????? ???????????? ??????????????? ?????????
                showPasswordDialog();

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getContext(),"?????? ????????? ???????????? ??????????????? ???????????? ??? ????????????.",Toast.LENGTH_SHORT).show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("?????? ??????")
                .setSubtitle("????????? ????????? ????????? ???????????? ????????? ????????? ?????????.")
                .setNegativeButtonText("??????")
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
    // ?????? ?????? ?????? ??? ?????? ??? ?????? ???????????????
    public void showPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setTitle("????????????");
        //????????? ??? ?????? ??? ??????;
        SharedPreferences pref = this.getActivity().getSharedPreferences("pref",MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        final TextView vi = new TextView(this.getActivity());
        final EditText et = new EditText(this.getActivity());
        et.setText(pref.getString("userpw",""));
        builder.setView(vi);
        builder.setView(et);
        builder.setPositiveButton("??????",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //EditText??? ?????? password ???????????? SharedPreference??? ??????
                        if(!et.getText().toString().equals(pref.getString("userpw",""))){
                            editor.putString("userpw",et.getText().toString());
                            editor.apply();
                        }
                    }
                });
        builder.show();
    }
}

