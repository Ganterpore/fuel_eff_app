//package com.example.mitchell.UI;
//
//import android.app.DatePickerDialog;
//import android.arch.persistence.room.Room;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Color;
//import android.graphics.drawable.ColorDrawable;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.DatePicker;
//import android.widget.EditText;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import java.text.DateFormat;
//import java.text.ParseException;
//import java.util.Calendar;
//import java.util.Date;
//
//import application.Controller;
//import Models.Entry;
//import application.EntryWrapper;
//import database.AppDatabase;
//
///**
// * activity is used for the creation of an entry
// */
//public class addEntry extends AppCompatActivity {
//    //where the entry is stored in the intent passed from this activity
////    public static final String ENTRY = "com.example.mitchell.test1.ENTRY";
//    private TextView displayDate;
//    private DatePickerDialog.OnDateSetListener dateSetListener;
//    private boolean isEdit = false; //determines whether an edit or add is being performed
//    private EntryWrapper editEntry;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        //default creation items
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_entry);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
//
//        //sets up back arrow on action bar
//        android.support.v7.app.ActionBar ab = getSupportActionBar();
//        ab.setDisplayHomeAsUpEnabled(true);
//
//        //checks if we are editing an item, if so, set the text fields to what they currently are
//        if(getIntent().hasExtra("eid")) {
//            int eid = getIntent().getIntExtra("eid", 0);
//            editEntry = Controller.getCurrentController().entryC.getEntry(eid);
//            isEdit = true;
//            DateFormat format = DateFormat.getDateInstance();
//            ((TextView) findViewById(R.id.Date)).setText(format.format(editEntry.getDate()));
//            ((TextView) findViewById(R.id.Trip)).setText(String.valueOf(editEntry.getTrip()));
//            ((TextView) findViewById(R.id.Litres)).setText(String.valueOf(editEntry.getLitres()));
//            ((TextView) findViewById(R.id.Price)).setText(String.valueOf(editEntry.getPrice()));
//        }
//
//        //checks for the date field to be clicked, and opens a dialog calender selector,
//        // automatically set up to todays date
//        displayDate = (TextView) findViewById(R.id.Date);
//        displayDate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //getting current date
//                Calendar cal = Calendar.getInstance();
//                int year = cal.get(Calendar.YEAR);
//                int month = cal.get(Calendar.MONTH);
//                int day = cal.get(Calendar.DAY_OF_MONTH);
//
//                //opens dialog for calendar
//                DatePickerDialog dialog = new DatePickerDialog(
//                        addEntry.this,
//                        android.R.style.Theme_DeviceDefault_Light_DarkActionBar,
//                        dateSetListener,
//                        year, month, day);
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE  ));
//                dialog.show();
//            }
//        });
//
//        //sets the text value
//        dateSetListener = new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
//                DateFormat format = DateFormat.getDateInstance();
//                String date = format.format(new Date(year, month, day));
//                displayDate.setText(date);
//            }
//        };
//    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_add_entry, menu);
//        return true;
//    }
//
//    //when an item from the menu is selected
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            //if create entry is selected, create the entry
//            case R.id.create_entry:
//                createEntry();
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//
//        }
//    }
//
//    //converts the information in the addEntry page to an Entry object.
//    public void createEntry() {
//        Context context = getApplicationContext();
//        //if the entry is invalid, show a toast message.
//        int duration = Toast.LENGTH_SHORT;
//        Toast toast = Toast.makeText(context, "Fill in all required fields", duration);
//
//        long date;
//        Double trip, litres, price, cost;
//        price = null;
//        //checks to make sure the data is valid
//        //TODO extract method
//        try {
//            DateFormat format = DateFormat.getDateInstance();
//            date = format.parse(((EditText) findViewById(R.id.Date)).getText().toString());
//        } catch(ParseException e) {
//            toast.show();
//            return;
//        }
//        try {
//            trip = Double.parseDouble(((EditText) findViewById(R.id.Trip)).getText().toString());
//        } catch(NumberFormatException e) {
//            toast.show();
//            return;
//        }
//
//        try {
//            litres = Double.parseDouble(((EditText) findViewById(R.id.Litres)).getText().toString());
//        } catch(NumberFormatException f) {
//            try {
//                price = Double.parseDouble(((EditText) findViewById(R.id.Price)).getText().toString());
//                cost = Double.parseDouble(((EditText) findViewById(R.id.Cost)).getText().toString());
//
//                litres = (cost * 100) / price;
//            } catch (NumberFormatException e) {
//                toast.show();
//                return;
//            }
//        }
//        if (price == null) {
//            try {
//                price  = Double.parseDouble(((EditText) findViewById(R.id.Price)).getText().toString());
//            } catch(NumberFormatException e) {
//                try {
//                    cost = Double.parseDouble(((EditText) findViewById(R.id.Cost)).getText().toString());
//
//                    price = (cost*100)/litres;
//                } catch (NumberFormatException g) {
//                    toast.show();
//                    return;
//                }
//            }
//        }
//        //creating entry and beginning the entry activity
//        Intent intent = new Intent(this, EntryActivity.class);
//        //TODO add car and fuel to this page
//        EntryWrapper entry = Controller.getCurrentController().entryC.newEntry(date, trip, litres, price, 0, 0);
//        intent.putExtra("eid", entry.getEid());
//
//        //TODO head to add tags screens next
//        startActivity(intent);
//        finish();
//    }
//
//}
