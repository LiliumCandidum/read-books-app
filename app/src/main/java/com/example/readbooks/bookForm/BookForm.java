package com.example.readbooks.bookForm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;

import com.example.readbooks.R;
import com.example.readbooks.models.Book;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.UUID;

public class BookForm extends AppCompatActivity {
    ActivityResultLauncher<Intent> cameraActivityResult;

    private DatePickerDialog datePickerDialog;
    private Button dateStartPickerButton;
    private Button dateEndPickerButton;
    private String datePickerClicked; // start or end

    private TextView formTitle;

    private EditText titleField;
    private EditText authorField;
    private EditText reviewField;
    private RadioGroup voteRadioGroup;
    private ImageView pictureView;

    private Book book;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_form);

        initDatePickerDialog();

        setViews();

        book = (Book) getIntent().getSerializableExtra("book");
        if(book != null) {
            fillFields();
        } else {
            book = new Book(UUID.randomUUID().toString());
            formTitle.setText(getString(R.string.book_new_form_title));

            dateStartPickerButton.setText(getTodaysDate());
            dateEndPickerButton.setText(getTodaysDate());
        }

        databaseReference = FirebaseDatabase.getInstance("https://read-books-908e8-default-rtdb.europe-west1.firebasedatabase.app").getReference("books");

        cameraActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
//                        Bitmap photo = (Bitmap)data.getExtras().get("data");
//                        Intent data = result.getData().getExtras();
                        // TODO non so se va
                        Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                        pictureView.setImageBitmap(photo);
                    }
                }
            });
    }

    private void fillFields() {
        formTitle.setText(book.getTitle());
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        reviewField.setText(book.getReview());

        dateStartPickerButton.setText(book.getDateStart().replaceAll("/", " "));
        dateEndPickerButton.setText(book.getDateEnd().replaceAll("/", " "));

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

        titleField = (EditText) findViewById(R.id.titleInput);
        authorField = (EditText) findViewById(R.id.authorInput);
        reviewField = (EditText) findViewById(R.id.reviewInput);

        voteRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
        voteRadioGroup.setOnCheckedChangeListener((RadioGroup radioGroup, int checkedId) -> {
            switch(checkedId) {
                case R.id.buttonVote1: book.setVote(1); break;
                case R.id.buttonVote2:  book.setVote(2); break;
                case R.id.buttonVote3:  book.setVote(3); break;
                case R.id.buttonVote4:  book.setVote(4); break;
                case R.id.buttonVote5:  book.setVote(5);
            }
        });

        pictureView = findViewById(R.id.bookPicture);
    }

    private void initDatePickerDialog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (DatePicker datePicker, int year, int month, int day) -> {
            String date = makeDateString(day, month, year);
            if(datePickerClicked.equals("start")) {
                dateStartPickerButton.setText(date);
                book.setDateStart(day, month, year);
            } else if(datePickerClicked.equals("end")) {
                dateEndPickerButton.setText(date);
                book.setDateEnd(day, month, year);
            }
            datePickerClicked = "";
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
        // TODO check fields
        book.setTitle(titleField.getText().toString());
        book.setAuthor(authorField.getText().toString());
        book.setReview(reviewField.getText().toString());

        databaseReference.child(book.getKey()).setValue(book).addOnCompleteListener((Task<Void> task) -> {
            if(task.isSuccessful()) {
                BookForm.super.onBackPressed();
            } else {
                Toast.makeText(BookForm.this, getString(R.string.saving_error), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void runCamera(View v) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraActivityResult.launch(cameraIntent);
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
       return day + " " + getMonthFormat(month) + " " + year;
    }

    private String getMonthFormat(int month) {
        // TODO add strings to strings.xml
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