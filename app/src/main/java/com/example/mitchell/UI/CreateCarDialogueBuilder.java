package com.example.mitchell.UI;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import Controller.Controller;
import Models.Car;

public class CreateCarDialogueBuilder {

    /**
     * Used to create a Diaologue box for the creation of a car instance in the database
     * @param observer, the observer to nmotify when the instance has been made
     * @param activity, the activity from which to originate the dialougue from
     */
    public static void createCarDialogue(final DatabaseObserver observer, final Activity activity) {
        //creating view
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View addCarLayout = layoutInflater.inflate(R.layout.new_car_dialogue_box, null);
        final EditText carNameET = addCarLayout.findViewById(R.id.car_name);
        final EditText licensePlateET = addCarLayout.findViewById(R.id.license_plate);
        final EditText makeET = addCarLayout.findViewById(R.id.make);
        final EditText modelET = addCarLayout.findViewById(R.id.model);

        //building the alert dialogue
        AlertDialog.Builder addCarAlert = new AlertDialog.Builder(activity);
        addCarAlert.setTitle("New car");
        addCarAlert.setView(addCarLayout);
        addCarAlert.setNegativeButton("Cancel", null);
        addCarAlert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //when the create button is pressed, add car to the database
                addCarToDatabase(carNameET.getText().toString(),
                        licensePlateET.getText().toString(),
                        makeET.getText().toString(),
                        modelET.getText().toString(),
                        observer);

            }
        });
        //displaying the dialogue to the UI
        addCarAlert.create().show();
    }

    /**
     * Class for placing the car into the database
     */
    private static void addCarToDatabase(final String carName, final String licensePlate,
                                         final String make, final String model,
                                         final DatabaseObserver observer) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                //add car to the database
                return Controller.getCurrentController().newCar(carName, licensePlate, make, model);
            }

            @Override
            protected void onPostExecute(String cid) {
                //notify the observer of the new car
                Car car = new Car(Integer.parseInt(cid), licensePlate, model, make, carName);
                observer.notifyChange(car, DatabaseObserver.CAR);
            }
        }.execute();

    }

}
