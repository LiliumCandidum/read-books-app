package com.example.readbooks.registration;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.readbooks.R;
import com.example.readbooks.booksList.BooksListActivity;
import com.google.firebase.auth.FirebaseAuth;

public class RegistrationActivity extends AppCompatActivity {
    private FirebaseAuth auth;

    private EditText emailInput;
    private EditText passInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        auth = FirebaseAuth.getInstance();

        emailInput = (EditText) findViewById(R.id.emailInputRegistration);
        passInput = (EditText) findViewById(R.id.passwordInputRegistration);
    }

    public void register(View view) {
        String email = emailInput.getText().toString();
        String password = passInput.getText().toString();

        if(email != null && password != null) {
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {
                if(task.isSuccessful()) {
                    Toast.makeText(RegistrationActivity.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(RegistrationActivity.this, BooksListActivity.class));
                } else {
                    Toast.makeText(RegistrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }
}