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
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.text.DecimalFormat;

import Controller.Controller;
import Controller.EntryController;
import Models.Car;
import Models.EntryTag;
import Models.PetrolType;

/**
 * initial class created on startup
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

    /**
     * automatically called when activity created.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //name of view
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
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
        fab = (FloatingActionButton) findViewById(R.id.fab);

        carChoices = findViewById(R.id.car_chooser);
        ArrayAdapter<Car> carArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        carChoices.setAdapter(carArrayAdapter);

        int carIndex = carArrayAdapter.getPosition(currentCar);
        carChoices.setSelection(carIndex);

        carChoices.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                CarSetter carSetter = new CarSetter(activity, cars.get(i));
                carSetter.execute();
                Log.d("R", "onItemSelected: "+cars.get(i));

//                carChoices.setVisibility(View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
//                carChoices.setVisibility(View.GONE);
            }

        });

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

    private static void updateDetails(final MainActivity activity) {
        final DecimalFormat decimalFormat = new DecimalFormat("#.00");
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {

                activity.fuels = Controller.getCurrentController().getAllFuels();
                activity.cars = Controller.getCurrentController().getAllCars();
                activity.tags = Controller.getCurrentController().getAllTags();
                if(activity.cars.size()==0) { //if there are currently no cars, we must create one
//                    activity.createCarDialogue();
                    return false;
                } else {
                    int defaultCid = activity.cars.get(0).getCid();
                    Log.d("A", "doInBackground: "+ Arrays.toString(activity.cars.toArray()));

                    SharedPreferences preferences = activity.getSharedPreferences(SHARED_PREFS_LOC, Context.MODE_PRIVATE);
                    if(!preferences.contains("car")) {
                        preferences.edit().putInt("car", defaultCid).apply();
                    }
                    activity.carID = preferences.getInt("car", defaultCid);

                    activity.currentCar = Controller.getCurrentController().getCar(activity.carID);

                    //setting the views
                    EntryController entryC = activity.controller.entryC;
                    activity.make.setText(activity.currentCar.getMake());
                    activity.model.setText(activity.currentCar.getModel());
                    activity.license.setText(activity.currentCar.getLicensePlate());
                    activity.efficiency.setText(decimalFormat.format(entryC.getAverageEfficiency(activity.carID)));
                    activity.distance.setText(decimalFormat.format(entryC.getTotalDistance(activity.carID)));
                    activity.cost.setText(decimalFormat.format(entryC.getTotalCost(activity.carID)));
                    activity.litres.setText(decimalFormat.format(entryC.getTotalLitres(activity.carID)));

                    

                    ArrayAdapter<Car> carArrayAdapter = (ArrayAdapter<Car>) activity.carChoices.getAdapter();
                    carArrayAdapter.clear();
                    carArrayAdapter.addAll(activity.cars);
                    int carIndex = carArrayAdapter.getPosition(activity.currentCar);
                    activity.carChoices.setSelection(carIndex);
                 }

                return true;
            }

            @Override
            protected void onPostExecute(Boolean carExists) {
                activity.showProgress(false);
                if(!carExists) {
                    CreateCarDialogueBuilder.createCarDialogue(activity, activity, new ArrayList<Car>() {
                    });
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId(); //id of the option selected
        switch (id) {
            case R.id.action_settings:
                return true;
            case R.id.action_add_car:
                CreateCarDialogueBuilder.createCarDialogue(this, this, cars);
                break;
            case  R.id.action_delete_car:
                final MainActivity activity = this;

                AlertDialog.Builder deleteCarDialogue = new AlertDialog.Builder(this);
                deleteCarDialogue.setTitle("Are you sure?");
                deleteCarDialogue.setMessage("Would you really like to delete the car \""+currentCar.getName()+"\"?");
                deleteCarDialogue.setNegativeButton("Cancel", null);
                deleteCarDialogue.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showProgress(true);
                        new AsyncTask<Void, Void, Void>() {

                            @Override
                            protected Void doInBackground(Void... voids) {
                                Controller.getCurrentController().deleteCar(currentCar);
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                cars.remove(currentCar);
                                new CarSetter(activity, cars.get(0)).execute();
                                showProgress(false);
                            }
                        }.execute();
                    }
                });
                deleteCarDialogue.create().show();
        }

        return super.onOptionsItemSelected(item);
    }

    public void openHistory(View view) {
        //action when history button is pressed
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


    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        final View mProgressView = findViewById(R.id.progress);
        final View content = findViewById(R.id.content);
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
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
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            fab.setVisibility(show ? View.GONE : View.VISIBLE);
            content.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void notifyChange(Object object, String type) {
        switch (type) {
            case DatabaseObserver.FUEL:
                fuels.add((PetrolType) object);
                break;
            case DatabaseObserver.CAR:
                Car car = (Car) object;
                cars.add(car);
                CarSetter setCar = new CarSetter(this, car);
                setCar.execute();
                break;
            case DatabaseObserver.TAG:
                tags.add((EntryTag) object);
                break;
            case DatabaseObserver.ENTRY:
                CarSetter updateDetails = new CarSetter(this, currentCar);
                updateDetails.execute();
                break;
        }
    }

    public class CarSetter extends AsyncTask<Void, Void, Void> {
        private MainActivity activity;
        private Car car;
        private double averageEfficiency;
        private double totalDistance;
        private double totalCost;
        private double totalLitres;
        final DecimalFormat decimalFormat = new DecimalFormat("#.00");

        public CarSetter(MainActivity activity, Car car) {
            this.activity = activity;
            this.car = car;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            averageEfficiency = activity.controller.entryC.getAverageEfficiency(car.getCid());
            totalDistance = activity.controller.entryC.getTotalDistance(car.getCid());
            totalCost = activity.controller.entryC.getTotalCost(car.getCid());
            totalLitres = activity.controller.entryC.getTotalLitres(car.getCid());



            SharedPreferences preferences = activity.getSharedPreferences(SHARED_PREFS_LOC, MODE_PRIVATE);
            preferences.edit().putInt("car", car.getCid()).apply();

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {

            activity.currentCar = car;
            activity.carID = car.getCid();

                //update the view
                activity.make.setText(car.getMake());
                activity.model.setText(car.getModel());
                activity.license.setText(car.getLicensePlate());
                activity.efficiency.setText(decimalFormat.format(averageEfficiency));
                activity.distance.setText(decimalFormat.format(totalDistance));
                activity.cost.setText(decimalFormat.format(totalCost));
                activity.litres.setText(decimalFormat.format(totalLitres));

            ArrayAdapter<Car> carArrayAdapter = (ArrayAdapter<Car>) activity.carChoices.getAdapter();
            carArrayAdapter.clear();
            carArrayAdapter.addAll(activity.cars);
            int carIndex = carArrayAdapter.getPosition(activity.currentCar);
            activity.carChoices.setSelection(carIndex);
        }
    }
}
