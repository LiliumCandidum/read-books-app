package com.example.readbooks.models;

import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

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
        databaseReference = FirebaseDatabase.getInstance(DATABASE_URL).getReference("books");
        storageRef = FirebaseStorage.getInstance(STORAGE_URL).getReference("covers");
    }
}
