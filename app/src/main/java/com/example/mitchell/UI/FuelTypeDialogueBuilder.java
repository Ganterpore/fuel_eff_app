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

    public static void createFuelTypeDialogue(final DatabaseObserver observer, Activity activity) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View addFuelLayout  =   layoutInflater.inflate(R.layout.new_fuel_dialogue_box, null);

        final EditText fuelName = addFuelLayout.findViewById(R.id.fuel_name);
        final EditText fuelPercent = addFuelLayout.findViewById(R.id.fuel_percent);

        AlertDialog.Builder addFuelAlert = new AlertDialog.Builder(activity);
        addFuelAlert.setTitle("New fuel type");
        addFuelAlert.setView(addFuelLayout);
        addFuelAlert.setNegativeButton("Cancel", null);
        addFuelAlert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new AsyncTask<Void, Void, PetrolType>() {

                    @Override
                    protected PetrolType doInBackground(Void... voids) {
                        long fuelID = Controller.getCurrentController().addFuel(
                                fuelName.getText().toString(),
                                Integer.parseInt(fuelPercent.getText().toString())
                        );
                        PetrolType fuel = Controller.getCurrentController().getFuel(fuelID);
                        return fuel;
                    }

                    @Override
                    protected void onPostExecute(PetrolType petrolType) {
                        observer.notifyChange(petrolType, DatabaseObserver.FUEL);
                    }
                }.execute();
            }
        });

        addFuelAlert.create().show();
    }

}
