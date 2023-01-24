package com.example.readbooks.models;

import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.readbooks.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class DatabaseActivity extends AppCompatActivity {
    protected final String DATABASE_URL = "https://read-books-908e8-default-rtdb.europe-west1.firebasedatabase.app";
    protected final String STORAGE_URL = "gs://read-books-908e8.appspot.com";

    protected DatabaseReference databaseReference;
    protected StorageReference storageRef;

    public DatabaseActivity() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            databaseReference = FirebaseDatabase.getInstance(DATABASE_URL).getReference(user.getUid());
            storageRef = FirebaseStorage.getInstance(STORAGE_URL).getReference("covers");
        } else {
            Toast.makeText(DatabaseActivity.this, getString(R.string.erorr_user), Toast.LENGTH_LONG).show();
        }
    }
}
