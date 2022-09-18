package com.example.readbooks.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.readbooks.R;
import com.example.readbooks.bookForm.BookForm;
import com.example.readbooks.booksList.BooksListActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View view) {
        String email = "4473086@studenti.unige.it";
        String password = "prova1";

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, getString(R.string.login_success), Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this, BooksListActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, getString(R.string.login_error), Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}