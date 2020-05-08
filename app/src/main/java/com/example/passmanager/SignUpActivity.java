package com.example.passmanager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.passmanager.util.Util;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    private TextInputLayout textInputConfirmPassword;
    private Button signUpButton;
    private Button login;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference dbReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        dbReference = FirebaseDatabase.getInstance().getReference();
        setupUIView();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendUserToLogin();
            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });
    }
    private void setupUIView(){
        textInputPassword = findViewById(R.id.password);
        textInputConfirmPassword = findViewById(R.id.confirm_password);
        textInputEmail = findViewById(R.id.email);
        signUpButton = findViewById(R.id.sign_up_button);
        login = findViewById(R.id.have_account);
    }

    private void createNewAccount(){
        String email = textInputEmail.getEditText().getText().toString();
        String password = textInputPassword.getEditText().getText().toString();

        if(confirmInput()){

            progressDialog.setTitle("Creating The Account");
            progressDialog.setMessage("Please wait...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                        String userId = firebaseAuth.getCurrentUser().getUid();
                        dbReference.child("Users").child(userId).setValue("")
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        sendUserToMain();
                                        Toast.makeText(SignUpActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignUpActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                        });
                    }

                }
            });
        }
    }

    private void sendUserToLogin(){
        Intent loginIntent = new Intent(SignUpActivity.this, LoginActivity.class);
        startActivity(loginIntent);
    }

    private void sendUserToMain(){
        Intent mainIntent = new Intent(SignUpActivity.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private boolean validateEmail() {
        String emailInput = textInputEmail.getEditText().getText().toString().trim();

        if(emailInput.isEmpty()){
            textInputEmail.getEditText().setError("Field can't be empty");
            return false;
        }

        else if(!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            textInputEmail.getEditText().setError("Please enter a valid email address");
            return false;
        }
        else {
            textInputEmail.getEditText().setError(null);
            return true;
        }
    }

    private boolean validatePasswords() {
        String passwordInput = textInputPassword.getEditText().getText().toString().trim();
        String passwordInputConfirm = textInputConfirmPassword.getEditText().getText().toString().trim();

        if (passwordInput.isEmpty()) {
            textInputPassword.getEditText().setError("Field can't be empty");
            return false;
        }
        if(!Util.PASSWORD_PATTERN.matcher(passwordInput).matches()){
            textInputPassword.getEditText().setError("Low password - the password need to be at least 6 characters and contain at least 1 digit");
            return false;
        }
        if(passwordInputConfirm.isEmpty()){
            textInputConfirmPassword.getEditText().setError("Field can't be empty");
            return false;
        }

        if(!passwordInput.equals(passwordInputConfirm) && !passwordInput.isEmpty() && !passwordInputConfirm.isEmpty()){
            textInputConfirmPassword.getEditText().setError("Passwords not match");
            return false;
        }
        else {
            textInputPassword.getEditText().setError(null);
            textInputConfirmPassword.getEditText().setError(null);
            return true;
        }
    }

    public boolean confirmInput() {
        if (!validateEmail() | !validatePasswords()){
            return false;
        }
        return true;
    }
}
