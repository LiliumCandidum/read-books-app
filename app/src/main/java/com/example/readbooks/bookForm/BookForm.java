package com.example.readbooks.bookForm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.readbooks.R;
import com.example.readbooks.models.Book;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

import java.util.Calendar;

public class BookForm extends AppCompatActivity {
    private DatePickerDialog datePickerDialog;
    private Button dateStartPickerButton;
    private Button dateEndPickerButton;
    private String datePickerClicked; // start or end

    private TextView formTitle;

    private EditText titleField;
    private EditText authorField;
    private EditText reviewField;
    private RadioGroup voteRadioGroup;

    private Book book;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i("Book form", "ON CREATE");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_form);

        initDatePickerDialog();

        setViews();

        dateStartPickerButton.setText(getTodaysDate());
        dateEndPickerButton.setText(getTodaysDate());

        book = (Book) getIntent().getSerializableExtra("book");
        if(book != null) {
            fillFields();
        } else {
            book = new Book(UUID.randomUUID().toString());
            formTitle.setText(getString(R.string.book_new_form_title));
        }

        databaseReference = FirebaseDatabase.getInstance("https://read-books-908e8-default-rtdb.europe-west1.firebasedatabase.app").getReference("books");
    }

    private void fillFields() {
        formTitle.setText(getString(R.string.book_edit_form_title) + " " + book.getTitle());

        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        reviewField.setText(book.getReview());

        RadioButton radioButton;
        switch(book.getVote()) {
            default:
            case 1: radioButton = (RadioButton) findViewById(R.id.buttonVote1); break;
            case 2: radioButton = (RadioButton) findViewById(R.id.buttonVote2); break;
            case 3: radioButton = (RadioButton) findViewById(R.id.buttonVote3); break;
            case 4: radioButton = (RadioButton) findViewById(R.id.buttonVote4); break;
            case 5: radioButton = (RadioButton) findViewById(R.id.buttonVote5); break;
        }
        radioButton.setChecked(true);
    }

    private void setViews() {
        formTitle = (TextView) findViewById(R.id.formTitle);

        dateStartPickerButton = (Button) findViewById(R.id.dateStartPicker);
        dateEndPickerButton = (Button) findViewById(R.id.dateEndPicker);

        titleField = (EditText) findViewById(R.id.titleText);
        authorField = (EditText) findViewById(R.id.authorText);
        reviewField = (EditText) findViewById(R.id.reviewText);

        voteRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        voteRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch(checkedId) {
                    case R.id.buttonVote1: book.setVote(1); break;
                    case R.id.buttonVote2:  book.setVote(2); break;
                    case R.id.buttonVote3:  book.setVote(3); break;
                    case R.id.buttonVote4:  book.setVote(4); break;
                    case R.id.buttonVote5:  book.setVote(5);
                }
            }
        });
    }

    private void initDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String date = makeDateString(day, month, year);
                if(datePickerClicked == "start") {
                    dateStartPickerButton.setText(date);
                    book.setDateStart(day, month, year);
                } else if(datePickerClicked == "end") {
                    dateEndPickerButton.setText(date);
                    book.setDateEnd(day, month, year);
                }
                datePickerClicked = "";
            }
        };

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;
        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
    }

    public void saveBook(View view) {
        // controlla campi
        book.setTitle(titleField.getText().toString());
        book.setAuthor(authorField.getText().toString());
        book.setReview(reviewField.getText().toString());

        databaseReference.child(book.getKey()).setValue(book).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    BookForm.super.onBackPressed();
                } else {
                    Toast.makeText(BookForm.this, getString(R.string.saving_error), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void openDateStartPicker(View view) {
        datePickerClicked = "start";
        datePickerDialog.show();
    }

    public void openDateEndPicker(View view) {
        datePickerClicked = "end";
        datePickerDialog.show();
    }

    private String getTodaysDate() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private String makeDateString(int day, int month, int year) {
        Log.i("", "makeDateString: " +  day + " " + getMonthFormat(month) + " " + year);
       return day + " " + getMonthFormat(month) + " " + year;
    }

    private String getMonthFormat(int month) {
        switch (month) {
            case 0: return "JAN";
            case 1: return "FEB";
            case 2: return "MAR";
            case 3: return "APR";
            case 4: return "MAY";
            case 5: return "JUN";
            case 6: return "JUL";
            case 7: return "AUG";
            case 8: return "SEP";
            case 9: return "OCT";
            case 10: return "NOV";
            case 11: return "DEC";
            default: return "";
        }
    }
}