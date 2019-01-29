package com.example.mitchell.UI;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import Controller.Controller;
import Models.Car;

import static java.lang.System.exit;

public class CreateCarDialogueBuilder {

    public static void createCarDialogue(final DatabaseObserver observer, final MainActivity activity, List<Car> cars) {
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
//                accountController.requestContact(emailEt.getText().toString());
                addCarToDatabaseAndUpdate(activity, carNameET.getText().toString(),
                        licensePlateET.getText().toString(),
                        makeET.getText().toString(),
                        modelET.getText().toString(),
                        observer);

            }
        });
        //displaying the dialogue to the UI
        addCarAlert.create().show();
    }

    private static void addCarToDatabaseAndUpdate(final MainActivity activity, final String carName,
                                                  final String licensePlate, final String make, final String model,
                                                  final DatabaseObserver observer) {
        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                String cid = Controller.getCurrentController().newCar(carName, licensePlate, make, model);
                return cid;
            }

            @Override
            protected void onPostExecute(String cid) {
                //first set the newly created car ID as the default
                Properties properties = new Properties();
                try {
                    properties.load(activity.getAssets().open("settings.properties"));
                } catch (IOException e) {
                    e.printStackTrace();
                    exit(1);
                }
                properties.setProperty("car", cid);
                Car car = new Car(Integer.parseInt(cid), licensePlate, model, make, carName);
                observer.notifyChange(car, DatabaseObserver.CAR);

                //refresh the page so the newest details are there
//                MainActivity.updateDetails(activity);
            }
        }.execute();

    }

}
