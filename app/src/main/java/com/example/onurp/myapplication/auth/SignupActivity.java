package com.example.onurp.myapplication.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onurp.myapplication.ConnectionCheck;
import com.example.onurp.myapplication.MainActivity;
import com.example.onurp.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.joda.time.LocalDate;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by onurp on 14.09.2017.
 */

public class SignupActivity extends AppCompatActivity {
    @BindView(R.id.signEmail)EditText textEmail;
    @BindView(R.id.signPassword)EditText textPassword;
    @BindView(R.id.btn_signup)AppCompatButton buttonSignup;
    @BindView(R.id.link_login)TextView linkLogin;
    @BindView(R.id.signName)EditText textName;

    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String uID;
    private ConnectionCheck check;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        check = new ConnectionCheck(SignupActivity.this);

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(check.getState() && check.isOnline()){
                    final String email = textEmail.getText().toString().trim();
                    final String password = textPassword.getText().toString().trim();
                    final String name = textName.getText().toString().trim();

                    if(authentication(email,password,name)){
                        final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this,
                                R.style.AppTheme_Dark_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Kayıt olunuyor...");
                        progressDialog.show();

                        auth.createUserWithEmailAndPassword(email, password)
                                .addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        Toast.makeText(SignupActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(SignupActivity.this, "Kayıt olma işlemi başarısız." + task.getException(),
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            //startActivity(new Intent(SignupActivity.this, MainActivity.class));
                                            FirebaseUser user = auth.getCurrentUser();
                                            uID = user.getUid();
                                            Users users=new Users(email,name);
                                            databaseReference.child("users").child(uID).setValue(users);
                                            finish();
                                        }
                                    }
                                });
                    }
                }

            }
        });
    }

    public boolean authentication(String email,String password,String name){
        boolean valid = true;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            textEmail.setError("Düzgün bir mail adresi giriniz.");
            valid = false;
        } else {
            textEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            textPassword.setError("4-10 uzunluk arası bir şifre giriniz.");
            valid = false;
        } else {
            textPassword.setError(null);
        }

        if(name.isEmpty()){
            textName.setError("İsim kısmı boş bırakılamaz.");
        } else {
            textName.setError(null);
        }

        return valid;
    }
}
