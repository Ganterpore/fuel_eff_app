package com.example.mitchell.UI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import Controller.Controller;
import Models.Car;
import Models.PetrolType;

import static java.lang.System.exit;

/**
 * initial class created on startup
 */
public class MainActivity extends AppCompatActivity {
    Controller controller = null;
    int carID;
    private List<Car> cars;
    private List<PetrolType> fuels;
    private TextView efficiency;
    private TextView distance;
    private TextView cost;
    private TextView litres;
    private FloatingActionButton fab;

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

        controller = new Controller(getApplicationContext());

        efficiency = findViewById(R.id.AverageEfficiency);
        distance = findViewById(R.id.TotalDistance);
        cost = findViewById(R.id.TotalCost);
        litres = findViewById(R.id.TotalLitres);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        showProgress(true);
        updateDetails(this);

        //creating the add entry button

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            //when clicked a new entry will be created
            public void onClick(View view) {
                newEntry(view);
            }
        });
    }

    private static void updateDetails(final MainActivity activity) {
        new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... voids) {

                activity.fuels = Controller.getCurrentController().getAllFuels();
                activity.cars = Controller.getCurrentController().getAllCars();
                if(activity.cars.size()==0) { //if there are currently no cars, we must create one
//                    activity.createCarDialogue();
                    return false;
                } else {
                    String defaultCid = String.valueOf(activity.cars.get(0).getCid());
                    Log.d("A", "doInBackground: "+ Arrays.toString(activity.cars.toArray()));

                    Properties properties = new Properties();
                    try {
                        properties.load(activity.getAssets().open("settings.properties"));
                    } catch (IOException e) {
                        e.printStackTrace();
                        exit(1);
                    }
                    activity.carID = Integer.parseInt(properties.getProperty("car", defaultCid));

                    activity.efficiency.setText(String.format("%3.2f", activity.controller.entryC.getAverageEfficiency(activity.carID)));
                    activity.distance.setText(String.format("%3.2f", activity.controller.entryC.getTotalDistance(activity.carID)));
                    activity.cost.setText(String.format("%3.2f", activity.controller.entryC.getTotalCost(activity.carID)));
                    activity.litres.setText(String.format("%3.2f", activity.controller.entryC.getTotalLitres(activity.carID)));

                 }

                return true;
            }

            @Override
            protected void onPostExecute(Boolean carExists) {
                activity.showProgress(false);
                if(!carExists) {
                    activity.createCarDialogue();
                }
            }
        }.execute();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //TODO when a new car activity returns, recieve the carID, and make it the new default/current cid and
        //update the averages etc to be this car
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
                createCarDialogue();
                break;
        }
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //if settings is clicked... do nothing
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void newEntry(View view) {
        //action when entry button is pressed
//        Intent intent = new Intent(this, addEntry.class);
//        startActivity(intent);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View addEntryLayout = layoutInflater.inflate(R.layout.new_entry_dialogue_box, null);

        final EditText dateET = addEntryLayout.findViewById(R.id.entry_date);
        final EditText tripLengthET = addEntryLayout.findViewById(R.id.trip_length);
        final EditText litresFilledET = addEntryLayout.findViewById(R.id.fill_up_litres);
        final EditText priceET = addEntryLayout.findViewById(R.id.entry_price);
        final Spinner carChoices = addEntryLayout.findViewById(R.id.car_used);
        final Button addCarButton = addEntryLayout.findViewById(R.id.add_car_button);
        final Spinner fuelChoices = addEntryLayout.findViewById(R.id.fuel_used);
        final Button addFuelButton = addEntryLayout.findViewById(R.id.add_fuel_button);



        ArrayAdapter<Car> carsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        carsAdapter.addAll(cars);
        carChoices.setAdapter(carsAdapter);

        addCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createCarDialogue();
            }
        });

        ArrayAdapter<PetrolType> fuelsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item);
        fuelsAdapter.addAll(fuels);

        fuelChoices.setAdapter(fuelsAdapter);

        addFuelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createFuelTypeDialogue();
            }
        });


        dateET.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                //hide keyboard
                InputMethodManager imm = (InputMethodManager) MainActivity.this.getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                //open date picker
                DatePickerDialog datePicker = new DatePickerDialog(MainActivity.this);
                datePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        dateET.setText(day+"/"+(month+1)+"/"+year);
                    }
                });
                datePicker.show();
            }
        });
        final MainActivity activity = this;
        //building the alert dialogue
        AlertDialog.Builder addEntryAlert = new AlertDialog.Builder(this);
        addEntryAlert.setTitle("New Entry");
        addEntryAlert.setView(addEntryLayout);
        addEntryAlert.setNegativeButton("Cancel", null);
        addEntryAlert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
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
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        controller.entryC.newEntry(
                                startDate,
                                Double.parseDouble(tripLengthET.getText().toString()),
                                Double.parseDouble(litresFilledET.getText().toString()),
                                Double.parseDouble(priceET.getText().toString()),
                                ((Car) carChoices.getSelectedItem()).getCid(),
                                ((PetrolType) fuelChoices.getSelectedItem()).getPid()
                        );
                        return null;
                    }
                }.execute();

            }
        });
        //displaying the dialogue to the UI
        addEntryAlert.create().show();
    }

    public void createFuelTypeDialogue() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View addFuelLayout  =   layoutInflater.inflate(R.layout.new_fuel_dialogue_box, null);

        final EditText fuelName = addFuelLayout.findViewById(R.id.fuel_name);
        final EditText fuelPercent = addFuelLayout.findViewById(R.id.fuel_percent);

        AlertDialog.Builder addFuelAlert = new AlertDialog.Builder(this);
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
                        fuels.add(petrolType);
                    }
                }.execute();
            }
        });

        addFuelAlert.create().show();
    }

    public void createCarDialogue() {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View addCarLayout = layoutInflater.inflate(R.layout.new_car_dialogue_box, null);
        final EditText carNameET = addCarLayout.findViewById(R.id.car_name);
        final EditText licensePlateET = addCarLayout.findViewById(R.id.license_plate);
        final EditText makeET = addCarLayout.findViewById(R.id.make);
        final EditText modelET = addCarLayout.findViewById(R.id.model);
        final MainActivity activity = this;
        //building the alert dialogue
        AlertDialog.Builder addCarAlert = new AlertDialog.Builder(this);
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
                        modelET.getText().toString());

            }
        });
        //displaying the dialogue to the UI
        addCarAlert.create().show();
    }

    public static void addCarToDatabaseAndUpdate(final MainActivity activity, final String carName,
                                                 final String licensePlate, final String make, final String model) {
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
                activity.cars.add(car);

                //refresh the page so the newest details are there
//                MainActivity.updateDetails(activity);
            }
        }.execute();

    }

    public void openHistory(View view) {
        //action when history button is pressed
        Intent intent = new Intent(this, EntryHistoryActivity.class);
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

}
