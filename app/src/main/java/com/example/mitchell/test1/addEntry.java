package com.example.mitchell.test1;

import android.app.DatePickerDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

public class addEntry extends AppCompatActivity {
    //where the entry is stored in the intent passed from this activity
    public static final String ENTRY = "com.example.mitchell.test1.ENTRY";
    private TextView displayDate;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private boolean isEdit = false;
    private Entry editEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //sets up back arrow on action bar
        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        editEntry = getIntent().getParcelableExtra(EntryActivity.PREVIOUS_ENTRY);
        if(editEntry != null) {
            isEdit = true;
            ((TextView) findViewById(R.id.Date)).setText(editEntry.getDate());
            ((TextView) findViewById(R.id.Trip)).setText(String.valueOf(editEntry.getTrip()));
            ((TextView) findViewById(R.id.Litres)).setText(String.valueOf(editEntry.getLitres()));
            ((TextView) findViewById(R.id.Price)).setText(String.valueOf(editEntry.getPrice()));
        }

        //checks for the date field to be clicked, and opens a dialog calender selector,
        // automatically set up to todays date
        displayDate = (TextView) findViewById(R.id.Date);
        displayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //getting current year
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                //opens dialog
                DatePickerDialog dialog = new DatePickerDialog(
                        addEntry.this,
                        android.R.style.Theme_DeviceDefault_Light_DarkActionBar,
                        dateSetListener,
                        year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE  ));
                dialog.show();
            }
        });

        //sets the text value
        dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month += 1;
                String date = day + "/" + month + "/" + year;
                displayDate.setText(date);
            }
        };


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_entry, menu);
//        if (isEdit) {
//                //TODO change button to edit from create.
////            (R.string.create);
//        }
        return true;
    }

    //when an item from the menu is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //if create entry is selected, create the entry
            case R.id.create_entry:
                createEntry();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    //converts the information in the addEntry page to an Entry object.
    public void createEntry() {
        Intent intent = new Intent(this, EntryActivity.class);
        Context context = getApplicationContext();
        //if the entry is invalid, show a toast message.
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, "Fill in all required fields", duration);

        String date;
        Double trip, litres, price, cost;
        price = null;
        //checks to make sure the data is valid
        //TODO check for empty date field
        try {
            date = ((EditText) findViewById(R.id.Date)).getText().toString();
            trip = Double.parseDouble(((EditText) findViewById(R.id.Trip)).getText().toString());
        } catch(NumberFormatException e) {
            toast.show();
            return;
        }

        try {
            litres = Double.parseDouble(((EditText) findViewById(R.id.Litres)).getText().toString());
        } catch(NumberFormatException f) {
            try {
                price = Double.parseDouble(((EditText) findViewById(R.id.Price)).getText().toString());
                cost = Double.parseDouble(((EditText) findViewById(R.id.Cost)).getText().toString());

                litres = (cost * 100) / price;
            } catch (NumberFormatException e) {
                toast.show();
                return;
            }
        }
        if (price == null) {
            try {
                price  = Double.parseDouble(((EditText) findViewById(R.id.Price)).getText().toString());
            } catch(NumberFormatException e) {
                try {
                    cost = Double.parseDouble(((EditText) findViewById(R.id.Cost)).getText().toString());

                    price = (cost*100)/litres;
                } catch (NumberFormatException g) {
                    toast.show();
                    return;
                }
            }
        }
        //creating entry and begining the entry activity
        final Entry entry = new Entry(date, trip, litres, price);
        intent.putExtra(ENTRY, entry);

        final AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name").build();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... entries) {
                db.entryDao().insert(entry);
                //if this entry is coming from an edit, then the old entry should be deleted.
                if (isEdit) {
                    db.entryDao().deleteId(editEntry.getEid());
                }
                return null;
            }
        }.execute();
        startActivity(intent);
        finish();
    }

}
