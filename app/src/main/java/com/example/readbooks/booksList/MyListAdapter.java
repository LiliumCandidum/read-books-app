package com.example.readbooks.booksList;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.readbooks.R;
import com.example.readbooks.models.Book;
import java.util.ArrayList;

public class MyListAdapter extends ArrayAdapter<Book> {
    private final Activity context;
    private final ArrayList<Book> books;

    public MyListAdapter(@NonNull Activity context, ArrayList<Book> books) {
        super(context, R.layout.list_book_item, books);
        this.context = context;
        this.books = books;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View itemListView = inflater.inflate(R.layout.list_book_item, null, true);

        TextView titleText = itemListView.findViewById(R.id.item_list_title);
        TextView authorText = itemListView.findViewById(R.id.item_list_author);

        titleText.setText(books.get(position).getTitle());
        authorText.setText(books.get(position).getAuthor());

        return itemListView;
    }
}
