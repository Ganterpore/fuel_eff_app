package com.example.mitchell.UI;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import Controller.Controller;
import Models.PetrolType;

public class FuelTypeDialogueBuilder {

    /**
     * Class used to create a dialogue box for the creation of a fuel type
     * @param observer, the observer who needs to be notified if/when a fuel is added to the database
     * @param activity, the activity from which to open the dialogue.
     */
    public static void createFuelTypeDialogue(final DatabaseObserver observer, Activity activity) {
        //inflating the views
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View addFuelLayout  =   layoutInflater.inflate(R.layout.new_fuel_dialogue_box, null);
        //getting the Text
        final EditText fuelName = addFuelLayout.findViewById(R.id.fuel_name);
        final EditText fuelPercent = addFuelLayout.findViewById(R.id.fuel_percent);

        //creating the dialogue box
        AlertDialog.Builder addFuelAlert = new AlertDialog.Builder(activity);
        addFuelAlert.setTitle("New fuel type");
        addFuelAlert.setView(addFuelLayout);
        addFuelAlert.setNegativeButton("Cancel", null);
        addFuelAlert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addFuelType(fuelName, fuelPercent, observer);
            }
        });

        addFuelAlert.create().show();
    }

    /**
     * Used to add a fuel type to the database
     * @param fuelName, the name of the fuel
     * @param fuelPercent, the percent of the fuel
     * @param observer,  the observer to inform of the updatee
     */
    private static void addFuelType(final EditText fuelName, final EditText fuelPercent, final DatabaseObserver observer) {
        new AsyncTask<Void, Void, PetrolType>() {

            @Override
            protected PetrolType doInBackground(Void... voids) {
                //adding the fuel to the database and getting the ID
                long fuelID = Controller.getCurrentController().addFuel(
                        fuelName.getText().toString(),
                        Integer.parseInt(fuelPercent.getText().toString())
                );
                //getting the fuel back out of the database, to get the full object
                return Controller.getCurrentController().getFuel(fuelID);
            }

            @Override
            protected void onPostExecute(PetrolType petrolType) {
                //notifying the observer of the new fuel
                observer.notifyChange(petrolType, DatabaseObserver.FUEL);
            }
        }.execute();
    }

}
