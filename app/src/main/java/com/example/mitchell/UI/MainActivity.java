package com.example.mitchell.UI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import Controller.Controller;
import Models.Car;
import Models.EntryTag;
import Models.PetrolType;

/**
 * initial Activity created on startup. Holds the main screen
 */
public class MainActivity extends AppCompatActivity implements DatabaseObserver {
    public static final String SHARED_PREFS_LOC = "com.ganterpore.fuel_eff";
    Controller controller = null;
    int carID;
    private List<Car> cars;
    private List<PetrolType> fuels;
    private List<EntryTag> tags;
    private Car currentCar;

    private TextView make;
    private TextView model;
    private TextView license;
    private TextView efficiency;
    private TextView distance;
    private TextView cost;
    private TextView litres;
    private FloatingActionButton fab;
    private Spinner carChoices;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //name of view
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final MainActivity activity = this;

        controller = new Controller(getApplicationContext());

        //getting the views
        make = findViewById(R.id.make);
        model = findViewById(R.id.model);
        license = findViewById(R.id.license_plate);

        efficiency = findViewById(R.id.AverageEfficiency);
        distance = findViewById(R.id.TotalDistance);
        cost = findViewById(R.id.TotalCost);
        litres = findViewById(R.id.TotalLitres);
        fab = findViewById(R.id.fab);

        //creating the spinner
        carChoices = findViewById(R.id.car_chooser);
        ArrayAdapter<Car> carArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        carChoices.setAdapter(carArrayAdapter);
        //when a selectrion is made on the spinner, set the current car to the selection
        carChoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                setCar(activity, cars.get(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //do nothing
            }
        });

        //shows progress spinner while page is updating
        showProgress(true);
        updateDetails(this);

        //creating the add entry button
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            //when clicked a new entry will be created
            public void onClick(View view) {
                NewEntryDialogueBuilder.newEntry(activity, activity, cars, fuels, tags, null, currentCar);
            }
        });
    }

    /**
     * Used to update the basic details on the main page of the app, including getting all the current information
     * from the database on first startup.
     * @param activity, the MainActivity to update
     */
    private static void updateDetails(final MainActivity activity) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {
                //getting the values for lists
                activity.fuels = Controller.getCurrentController().getAllFuels();
                activity.cars = Controller.getCurrentController().getAllCars();
                activity.tags = Controller.getCurrentController().getAllTags();
                if(activity.cars.size()==0) { //if there are currently no cars, we must create one
                    return false;
                } else {
                    //getting the last selected car. if nothing, revert to the first car
                    int defaultCid = activity.cars.get(0).getCid();
                    SharedPreferences preferences = activity.getSharedPreferences(SHARED_PREFS_LOC, Context.MODE_PRIVATE);
                    if(!preferences.contains("car")) {
                        //if there is no current car, set the current to the first car
                        preferences.edit().putInt("car", defaultCid).apply();
                    }
                    activity.carID = preferences.getInt("car", defaultCid);
                    activity.currentCar = Controller.getCurrentController().getCar(activity.carID);

                    setCar(activity, activity.currentCar);
                 }
                return true;
            }

            @Override
            protected void onPostExecute(Boolean carExists) {
                //once background tasks are finished, stop the progress spinner
                activity.showProgress(false);
                //if no car exists, create a dialogue to build a car
                if(!carExists) {
                    CreateCarDialogueBuilder.createCarDialogue(activity, activity);
                }
            }
        }.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId(); //id of the option selected
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_add_car:
                CreateCarDialogueBuilder.createCarDialogue(this, this);
                break;
            case  R.id.action_delete_car:
                final MainActivity activity = this;

                //before deleting the car, confirm with user
                AlertDialog.Builder deleteCarDialogue = new AlertDialog.Builder(this);
                deleteCarDialogue.setTitle("Are you sure?");
                deleteCarDialogue.setMessage("Would you really like to delete the car \""+currentCar.getName()+"\"?");
                deleteCarDialogue.setNegativeButton("Cancel", null);
                deleteCarDialogue.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showProgress(true);
                        deleteCar(activity, currentCar);
                    }
                });
                deleteCarDialogue.create().show();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Deletes a given car from the database
     * @param activity, the activity the car is being deleted from.
     * @param car, the car to delete
     */
    private static void deleteCar(final MainActivity activity, final Car car) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                //deleting the car from the database
                Controller.getCurrentController().deleteCar(car);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                //removing the car from local variables and resetting the views
                activity.cars.remove(car);
                setCar(activity, activity.cars.get(0));
                activity.showProgress(false);
            }
        }.execute();
    }

    /**
     * opens the activity for the historical data of the app
     * @param view, the view from which this action was selected
     */
    public void openHistory(View view) {
        Intent intent = new Intent(this, EntryHistoryActivity.class);
        intent.putExtra("carID", carID);
        startActivity(intent);
    }

    public void openData(View view) {
        //action when data button is pressed
        Intent intent = new Intent(this, DataPlotsActivity.class);
        intent.putExtra("carID", carID);
        startActivity(intent);
    }

   /**
     * Used to show/hide the progress spinner when information is loading.
     * @param show, whhether to show or hide.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        final View mProgressView = findViewById(R.id.progress);
        final View content = findViewById(R.id.content);
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

//            content.setAlpha(0.5f);
        content.setVisibility(show ? View.GONE : View.VISIBLE);

        fab.setVisibility(show ? View.GONE : View.VISIBLE);

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /**
     * Used to update the page when information on the database is changed
     * @param object, the object which ahs been added to the database
     * @param type, the type of the object. Types are contained in the DatabaseObserver interface
     */
    @Override
    public void notifyChange(Object object, String type) {
        switch (type) {
            case DatabaseObserver.FUEL:
                fuels.add((PetrolType) object);
                break;
            case DatabaseObserver.CAR:
                Car car = (Car) object;
                cars.add(car);
                //when a new car is created, make it the current car
                setCar(this, car);
                break;
            case DatabaseObserver.TAG:
                tags.add((EntryTag) object);
                break;
            case DatabaseObserver.ENTRY:
                setCar(this, currentCar);
                break;
        }
    }

    /**
     * Used to update the current car being used, and to update all the information on the page assosciated
     * with that car
     */
    public static void setCar(final MainActivity activity, final Car car) {
        final DecimalFormat decimalFormat = new DecimalFormat("#.00");
        //the stats to update. Stored as arrays to allow use as final
        final double[] averageEfficiency = new double[1];
        final double[] totalDistance = new double[1];
        final double[] totalCost = new double[1];
        final double[] totalLitres = new double[1];

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                //get the stats
                averageEfficiency[0] = activity.controller.entryC.getAverageEfficiency(car.getCid());
                totalDistance[0] = activity.controller.entryC.getTotalDistance(car.getCid());
                totalCost[0] = activity.controller.entryC.getTotalCost(car.getCid());
                totalLitres[0] = activity.controller.entryC.getTotalLitres(car.getCid());

                //update the car in preferences
                SharedPreferences preferences = activity.getSharedPreferences(SHARED_PREFS_LOC, MODE_PRIVATE);
                preferences.edit().putInt("car", car.getCid()).apply();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                //update the current car
                activity.currentCar = car;
                activity.carID = car.getCid();

                //update the view
                activity.make.setText(activity.currentCar.getMake());
                activity.model.setText(activity.currentCar.getModel());
                activity.license.setText(activity.currentCar.getLicensePlate());

                activity.efficiency.setText(decimalFormat.format(averageEfficiency[0]));
                activity.distance.setText(decimalFormat.format(totalDistance[0]));
                activity.cost.setText(decimalFormat.format(totalCost[0]));
                activity.litres.setText(decimalFormat.format(totalLitres[0]));

                //update the spinner
                ArrayAdapter<Car> carArrayAdapter = (ArrayAdapter<Car>) activity.carChoices.getAdapter();
                carArrayAdapter.clear();
                carArrayAdapter.addAll(activity.cars);
                int carIndex = carArrayAdapter.getPosition(activity.currentCar);
                activity.carChoices.setSelection(carIndex);
            }
        }.execute();
    }
}
