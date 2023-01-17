package com.example.readbooks.booksList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.readbooks.R;
import com.example.readbooks.bookForm.BookForm;
import com.example.readbooks.login.LoginActivity;
import com.example.readbooks.models.Book;
import com.example.readbooks.models.DatabaseActivity;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class BooksListActivity extends DatabaseActivity {
    private ListView listView;


    MyListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_list);

        final ArrayList<Book> list = new ArrayList<>();
        adapter = new MyListAdapter(this, list);

        listView = findViewById(R.id.booksListView);
        listView.setAdapter(adapter);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                list.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Book book = snapshot.getValue(Book.class);
                    book.setKey(snapshot.getKey());
                    list.add(book);
                }
                list.sort(((o1, o2) -> compareDates(o1.getDateStart(), o2.getDateStart())));
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

        listView.setOnItemLongClickListener((AdapterView<?> arg0, View v, int position, long l) -> {
            Book book = list.get(position);

            AlertDialog alertDialog = new AlertDialog.Builder(BooksListActivity.this).create(); //Read Update
            alertDialog.setTitle(getText(R.string.delete_dialog_title));
            alertDialog.setMessage(getResources().getString(R.string.delete_dialog_content, book.getTitle()));

            alertDialog.setButton(Dialog.BUTTON_POSITIVE, getText(R.string.delete_dialog_btn_delete), (DialogInterface dialog, int which) -> {
                deleteItem(book);
                alertDialog.dismiss();
            });
            alertDialog.setButton(Dialog.BUTTON_NEGATIVE, getText(R.string.delete_dialog_btn_cancel),
                    (DialogInterface dialog, int which) -> alertDialog.dismiss());

            alertDialog.show();
            return false;
        });
    }

    public void deleteItem(Book book) {
        databaseReference.child(book.getKey()).removeValue().addOnCompleteListener((Task<Void> task) -> {
            int toastTextId = R.string.delete_error;
            if(task.isSuccessful()) {
                // TODO handle unsuccessful delete
                StorageReference imgRef = storageRef.child(book.getKey() + ".jpg");
                imgRef.delete();

                toastTextId = R.string.delete_success;
            }

            Toast.makeText(BooksListActivity.this, getString(toastTextId), Toast.LENGTH_LONG).show();
        });
    }

    public void openNewForm(View view) {
        Intent intent = new Intent(BooksListActivity.this, BookForm.class);
        startActivity(intent);
    }

    /**
     * For descending order
     * @return 0 if the date is equal to the other date,
     * -1 if the date2 is less than the date1 and
     * 1 if the date2 is greater than the date1
     */
    private int compareDates(String date1, String date2) {
        // 0 -> day , 1 -> month, 2 -> year
        String[] date1Splitted = date1.split("/");
        String[] date2Splitted = date2.split("/");

        if(Integer.parseInt(date2Splitted[2]) > Integer.parseInt(date1Splitted[2])) {
            return 1;
        } else if(Integer.parseInt(date2Splitted[2]) < Integer.parseInt(date1Splitted[2])) {
            return -1;
        } else {
            if(Integer.parseInt(date2Splitted[1]) > Integer.parseInt(date1Splitted[1])) {
                return 1;
            } else if(Integer.parseInt(date2Splitted[1]) < Integer.parseInt(date1Splitted[1])) {
                return -1;
            } else {
                if(Integer.parseInt(date2Splitted[0]) > Integer.parseInt(date1Splitted[0])) {
                    return 1;
                } else if(Integer.parseInt(date2Splitted[0]) < Integer.parseInt(date1Splitted[0])) {
                    return -1;
                } else {
                    return 0;
                }
            }
        }
    }
}