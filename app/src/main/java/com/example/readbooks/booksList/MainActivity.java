package com.example.readbooks.booksList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.readbooks.R;
import com.example.readbooks.bookForm.BookForm;
import com.example.readbooks.models.Book;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final ArrayList<Book> list = new ArrayList<>();
        final MyListAdapter adapter = new MyListAdapter(this, list);

        listView = findViewById(R.id.books_list_view);
        listView.setAdapter(adapter);

        // TODO sistema e sistema anche in book form
        databaseReference = FirebaseDatabase.getInstance("https://read-books-908e8-default-rtdb.europe-west1.firebasedatabase.app").getReference("books");
        // TODO togliere listener on destroy?
        this.databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Log.i("BooksListViewModel", "snapshot.value: " + snapshot.getValue());
                    Book book = snapshot.getValue(Book.class);
                    book.setKey(snapshot.getKey());
                    list.add(book);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Called if the listener is unsuccessful for any reason
                // TODO fare qualcosa per questo caso?
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Book book = list.get(position);
                Log.i("BooksListFragment", "book cliccato: " + book.getAuthor());

                openEditForm(book);
            }
        });
    }

    public void openEditForm(Book book) {
        Intent intent = new Intent(MainActivity.this, BookForm.class);
        intent.putExtra("book", book);
        startActivity(intent);
    }

    public void openNewForm(View view) {
        Intent intent = new Intent(MainActivity.this, BookForm.class);
        startActivity(intent);
    }
}