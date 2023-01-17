package com.example.readbooks.booksList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.readbooks.R;
import com.example.readbooks.bookForm.BookForm;
import com.example.readbooks.models.Book;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;

public class MyListAdapter extends ArrayAdapter<Book> {
    private final Activity context;
    private final ArrayList<Book> books;
    private ArrayList<Book> originalBooks;

    public MyListAdapter(@NonNull Activity context, ArrayList<Book> books) {
        super(context, R.layout.list_book_item, books);
        this.context = context;
        this.books = books;
        this.originalBooks = books;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View itemListView = inflater.inflate(R.layout.list_book_item, null, true);

        TextView titleText = itemListView.findViewById(R.id.itemListTitle);
        TextView authorText = itemListView.findViewById(R.id.item_list_author);

        titleText.setText(books.get(position).getTitle());
        authorText.setText(books.get(position).getAuthor());

        Button editButton = (Button) itemListView.findViewById(R.id.editBookBtn);
        editButton.setOnClickListener(view -> {
            Book book = books.get(position);
            openEditForm(book);
        });

        return itemListView;
    }

    public void openEditForm(Book book) {
        Intent intent = new Intent(context, BookForm.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("book", book);
        context.startActivity(intent);
    }
}
