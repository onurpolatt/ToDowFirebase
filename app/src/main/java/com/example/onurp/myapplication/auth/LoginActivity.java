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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by onurp on 14.09.2017.
 */

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.email)EditText emailText;
    @BindView(R.id.password)EditText passwordText;
    @BindView(R.id.btn_login)AppCompatButton buttonLogin;
    @BindView(R.id.link_signup)TextView linkSignup;

    private FirebaseAuth auth;
    private static final int REQUEST_SIGNUP = 0;
    private ConnectionCheck check;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
        check = new ConnectionCheck(LoginActivity.this);
        if(auth.getCurrentUser() != null){
            startActivity(new Intent(this, MainActivity.class));
        }

        buttonLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(check.getState() && check.isOnline()){
                    if(authentication()){
                        String email = emailText.getText().toString();
                        String password = passwordText.getText().toString();

                        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                                R.style.AppTheme_Dark_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Giriş yapılıyor...");
                        progressDialog.show();

                        auth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        progressDialog.dismiss();
                                        if (!task.isSuccessful()) {
                                            Toast.makeText(LoginActivity.this, "Giriş bilgilerini kontrol ediniz...", Toast.LENGTH_LONG).show();
                                        } else {
                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "İnternet bağlantınızı kontrol ediniz...", Toast.LENGTH_LONG).show();
                }
            }
        });

        linkSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,SignupActivity.class);
                startActivityForResult(intent,REQUEST_SIGNUP);
            }
        });
    }

   public boolean authentication(){
       boolean valid = true;

       String email = emailText.getText().toString();
       String password = passwordText.getText().toString();

       if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
           emailText.setError("Düzgün bir mail adresi giriniz.");
           valid = false;
       } else {
           emailText.setError(null);
       }

       if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
           passwordText.setError("4-10 uzunluk arası bir şifre giriniz.");
           valid = false;
       } else {
           passwordText.setError(null);
       }

       return valid;
   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                if(auth.getCurrentUser()!=null){
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    this.finish();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

        moveTaskToBack(true);
    }
}
