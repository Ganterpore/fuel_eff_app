package com.example.mitchell.UI;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import Controller.Controller;
import Controller.EntryWrapper;
import Models.Car;
import Models.PetrolType;

public class NewEntryDialogueBuilder implements DatabaseObserver {
    private static NewEntryDialogueBuilder currentInstance;
    private DatabaseObserver observer;
    private ArrayAdapter<Car> carArrayAdapter;
    private ArrayAdapter<PetrolType> fuelArrayAdapter;

    public static void newEntry(final DatabaseObserver observer, final MainActivity activity, final List<Car> cars, List<PetrolType> fuels) {
        NewEntryDialogueBuilder.currentInstance = new NewEntryDialogueBuilder();
        currentInstance.observer = observer;

        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View addEntryLayout = layoutInflater.inflate(R.layout.new_entry_dialogue_box, null);

        final EditText dateET = addEntryLayout.findViewById(R.id.entry_date);
        final EditText tripLengthET = addEntryLayout.findViewById(R.id.trip_length);
        final EditText litresFilledET = addEntryLayout.findViewById(R.id.fill_up_litres);
        final EditText priceET = addEntryLayout.findViewById(R.id.entry_price);
        final Spinner carChoices = addEntryLayout.findViewById(R.id.car_used);
        final Button addCarButton = addEntryLayout.findViewById(R.id.add_car_button);
        final Spinner fuelChoices = addEntryLayout.findViewById(R.id.fuel_used);
        final Button addFuelButton = addEntryLayout.findViewById(R.id.add_fuel_button);

        currentInstance.carArrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item);
        currentInstance.carArrayAdapter.addAll(cars);
        carChoices.setAdapter(currentInstance.carArrayAdapter);

        addCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateCarDialogueBuilder.createCarDialogue(currentInstance, activity, cars);
            }
        });

        currentInstance.fuelArrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item);
        currentInstance.fuelArrayAdapter.addAll(fuels);

        fuelChoices.setAdapter(currentInstance.fuelArrayAdapter);
        addFuelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FuelTypeDialogueBuilder.createFuelTypeDialogue(currentInstance, activity);
            }
        });


        dateET.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                //hide keyboard
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                //open date picker
                DatePickerDialog datePicker = new DatePickerDialog(activity);
                datePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        dateET.setText(day+"/"+(month+1)+"/"+year);
                    }
                });
                datePicker.show();
            }
        });

        //building the alert dialogue
        AlertDialog.Builder addEntryAlert = new AlertDialog.Builder(activity);
        addEntryAlert.setTitle("New Entry");
        addEntryAlert.setView(addEntryLayout);
        addEntryAlert.setNegativeButton("Cancel", null);
        addEntryAlert.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                final long startDate;
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = sdf.parse(dateET.getText().toString());
                    startDate = date.getTime();

                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, "incorrect Date format given", Toast.LENGTH_SHORT).show();
                    return;
                }
                new AsyncTask<Void, Void, EntryWrapper>() {

                    @Override
                    protected EntryWrapper doInBackground(Void... voids) {
                        EntryWrapper entry = Controller.getCurrentController().entryC.newEntry(
                                startDate,
                                Double.parseDouble(tripLengthET.getText().toString()),
                                Double.parseDouble(litresFilledET.getText().toString()),
                                Double.parseDouble(priceET.getText().toString()),
                                ((Car) carChoices.getSelectedItem()).getCid(),
                                ((PetrolType) fuelChoices.getSelectedItem()).getPid()
                        );
                        return entry;
                    }

                    @Override
                    protected void onPostExecute(EntryWrapper entry) {
                        addPrevTripTagsToEntry(entry, observer, activity);
                    }
                }.execute();

            }
        });
        //displaying the dialogue to the UI
        addEntryAlert.create().show();
    }

    private static void addPrevTripTagsToEntry(final EntryWrapper entry, final DatabaseObserver observer, final Activity activity) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View addEntryLayout = layoutInflater.inflate(R.layout.add_prev_tags_to_entry, null);

        AlertDialog.Builder addEntryAlert = new AlertDialog.Builder(activity);
        addEntryAlert.setTitle("Add tags for the previous trip");
        addEntryAlert.setView(addEntryLayout);
//        addEntryAlert.setNegativeButton("Cancel", null);
        addEntryAlert.setPositiveButton("Next", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                addNextTripTagsToEntry(entry, observer, activity);
            }
        });
        addEntryAlert.create().show();
    }

    private static void addNextTripTagsToEntry(final EntryWrapper entry, final DatabaseObserver observer, final Activity activity) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View addEntryLayout = layoutInflater.inflate(R.layout.add_next_tags_to_entry, null);

        AlertDialog.Builder addEntryAlert = new AlertDialog.Builder(activity);
        addEntryAlert.setTitle("Add tags about the fill up");
        addEntryAlert.setView(addEntryLayout);
//        addEntryAlert.setNegativeButton("Cancel", null);
        addEntryAlert.setPositiveButton("Next", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addNotesToEntry(entry, observer, activity);
            }
        });
        addEntryAlert.create().show();
    }

    private static void addNotesToEntry(final EntryWrapper entry, final DatabaseObserver observer, final Activity activity) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View addEntryLayout = layoutInflater.inflate(R.layout.add_notes_to_entry, null);

        final EditText noteET = addEntryLayout.findViewById(R.id.entry_note);

        AlertDialog.Builder addEntryAlert = new AlertDialog.Builder(activity);
        addEntryAlert.setTitle("Add a note to the entry");
        addEntryAlert.setView(addEntryLayout);
//        addEntryAlert.setNegativeButton("Cancel", null);
        addEntryAlert.setPositiveButton("Create", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        entry.addNote(noteET.getText().toString());
                        return null;
                    }
                }.execute();

            }
        });
        addEntryAlert.create().show();
    }

    @Override
    public void notifyChange(Object object, String type) {

        switch (type) {
            case DatabaseObserver.CAR:
                Car car = (Car) object;
                carArrayAdapter.add(car);
                break;
            case DatabaseObserver.FUEL:
                PetrolType petrol = (PetrolType) object;
                fuelArrayAdapter.add(petrol);
                break;
        }

        observer.notifyChange(object, type);
    }
}
