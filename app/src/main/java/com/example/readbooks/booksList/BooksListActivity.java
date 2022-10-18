package com.example.readbooks.booksList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.readbooks.R;
import com.example.readbooks.bookForm.BookForm;
import com.example.readbooks.login.LoginActivity;
import com.example.readbooks.models.Book;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class BooksListActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_list);

        final ArrayList<Book> list = new ArrayList<>();
        final MyListAdapter adapter = new MyListAdapter(this, list);

        listView = findViewById(R.id.books_list_view);
        listView.setAdapter(adapter);

        // TODO sistema e sistema anche in book form
        databaseReference = FirebaseDatabase
                .getInstance("https://read-books-908e8-default-rtdb.europe-west1.firebasedatabase.app").getReference("books");
        // TODO togliere listener on destroy?
        databaseReference.addValueEventListener(new ValueEventListener() {
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

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> arg0, View v, int position, long l) {
                Log.i("", "Sono nel on LONG click listener");
                Book book = list.get(position);

                AlertDialog alertDialog = new AlertDialog.Builder(BooksListActivity.this).create(); //Read Update
                alertDialog.setTitle(getText(R.string.delete_dialog_title));
                // TODO: approfondisci questo get resources, si può usare altro?
                alertDialog.setMessage(getResources().getString(R.string.delete_dialog_content, book.getTitle()));

                alertDialog.setButton(Dialog.BUTTON_POSITIVE, getText(R.string.delete_dialog_btn_delete), (DialogInterface dialog, int which) -> {
                    deleteItem(book);
                    alertDialog.dismiss();
                });
                alertDialog.setButton(Dialog.BUTTON_NEGATIVE, getText(R.string.delete_dialog_btn_cancel),
                        (DialogInterface dialog, int which) -> alertDialog.dismiss());

                alertDialog.show();
                return false;
            }
        });
    }

    public void deleteItem(Book book) {
        databaseReference.child(book.getKey()).removeValue().addOnCompleteListener((Task<Void> task) -> {
            int toastTextId = task.isSuccessful() ? R.string.delete_success : R.string.delete_error;
            Toast.makeText(BooksListActivity.this, getString(toastTextId), Toast.LENGTH_LONG).show();
        });
    }

    public void openNewForm(View view) {
        Intent intent = new Intent(BooksListActivity.this, BookForm.class);
        startActivity(intent);
    }
}