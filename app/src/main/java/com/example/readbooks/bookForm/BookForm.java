package com.example.readbooks.bookForm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.readbooks.R;
import com.example.readbooks.models.Book;
import com.example.readbooks.models.DatabaseActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.UUID;

public class BookForm extends DatabaseActivity {
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

    private boolean isPictureChanged = false;

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
            formTitle.setText(getString(R.string.new_form_title));
            initialiseDates();
        }

        cameraActivityResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
                (ActivityResultCallback<ActivityResult>) result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                        pictureView.setImageBitmap(photo);

                        isPictureChanged = true;
                    }
                });
    }

    private void fillFields() {
        getPicture(book.getKey());

        formTitle.setText(getString(R.string.edit_form) + " " + book.getTitle());
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        reviewField.setText(book.getReview());

        String[] dateStart = book.getDateStart().split("/");
        String[] dateEnd = book.getDateEnd().split("/");
        dateStartPickerButton.setText(makeDateString(Integer.parseInt(dateStart[0]), Integer.parseInt(dateStart[1]), Integer.parseInt(dateStart[2])));
        dateEndPickerButton.setText(makeDateString(Integer.parseInt(dateEnd[0]), Integer.parseInt(dateEnd[1]), Integer.parseInt(dateEnd[2])));

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
            Log.i("DatePicker", "DAY SELECTED d " + day + " m " + month + " y " + year);
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

    private void getPicture(String book) {
        StorageReference imgRef = storageRef.child(book + ".jpg");
        final long ONE_MEGABYTE = 1024 * 1024;
        imgRef.getBytes(ONE_MEGABYTE).addOnSuccessListener((byte[] bytes) -> {
            Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            pictureView.setImageBitmap(bmp);
        }).addOnFailureListener(exception -> {
            pictureView.setImageResource(R.drawable.no_image_icon);
        });
    }

    private void savePicture() {
        StorageReference imgRef = storageRef.child(book.getKey() + ".jpg");
        pictureView.setDrawingCacheEnabled(true);
        pictureView.buildDrawingCache();
        Bitmap bitmap = pictureView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = imgRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                openErrorToast(R.string.saving_error);
            }
        }).addOnSuccessListener((UploadTask.TaskSnapshot taskSnapshot) -> {
            // TODO close the loading modal
            BookForm.super.onBackPressed();
        });
    }

    public void saveBook(View view) {
        String title = titleField.getText().toString();
        String author = authorField.getText().toString();

        if(title == null || author == null || book.getVote() == 0) {
            openErrorToast(R.string.saving_empty_field);
        } else {
            book.setTitle(title);
            book.setAuthor(author);
            book.setReview(reviewField.getText().toString());

            // TODO create and open a loading modal
            databaseReference.child(book.getKey()).setValue(book).addOnCompleteListener((Task<Void> task) -> {
                if(task.isSuccessful()) {
                    if(isPictureChanged) {
                        savePicture();
                    } else {
                        // TODO close the loading modal
                        BookForm.super.onBackPressed();
                    }
                } else {
                    openErrorToast(R.string.saving_error_img);
                }
            });
        }
    }

    private void openErrorToast(int stringResId) {
        Toast.makeText(BookForm.this, getString(stringResId), Toast.LENGTH_LONG).show();
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

    private void initialiseDates() {
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        String datePickerDate = makeDateString(day, month, year);
        dateStartPickerButton.setText(datePickerDate);
        dateEndPickerButton.setText(datePickerDate);

        book.setDateStart(day, month + 1, year);
        book.setDateEnd(day, month + 1, year);
    }

    private String makeDateString(int day, int month, int year) {
       return day + " " + getMonthFormat(month) + " " + year;
    }

    private String getMonthFormat(int month) {
        switch (month) {
            case 0: return getString(R.string.jan);
            case 1: return getString(R.string.feb);
            case 2: return getString(R.string.mar);
            case 3: return getString(R.string.apr);
            case 4: return getString(R.string.may);
            case 5: return getString(R.string.jun);
            case 6: return getString(R.string.jul);
            case 7: return getString(R.string.aug);
            case 8: return getString(R.string.sep);
            case 9: return getString(R.string.oct);
            case 10: return getString(R.string.nov);
            case 11: return getString(R.string.dec);
            default: return "";
        }
    }
}