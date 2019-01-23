package com.example.mitchell.UI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import Controller.Controller;
import Models.Car;

import static java.lang.System.exit;

/**
 * initial class created on startup
 */
public class MainActivity extends AppCompatActivity {
    Controller controller = null;
    int carID;
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


                List<Car> cars = Controller.getCurrentController().getAllCars();
                if(cars.size()==0) { //if there are currently no cars, we must create one
//                    activity.createCarDialogue();
                    return false;
                } else {
                    String defaultCid = String.valueOf(cars.get(0).getCid());

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
    }

    public void createCarDialogue() {
        //TODO
        efficiency.setText("new car idiot");

        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View addContactLayout = layoutInflater.inflate(R.layout.new_car_dialogue_box, null);
        final EditText carNameET = addContactLayout.findViewById(R.id.car_name);
        final EditText licensePlateET = addContactLayout.findViewById(R.id.license_plate);
        final EditText makeET = addContactLayout.findViewById(R.id.make);
        final EditText modelET = addContactLayout.findViewById(R.id.model);
        final MainActivity activity = this;
        //building the alert dialogue
        AlertDialog.Builder addContactAlert = new AlertDialog.Builder(this);
        addContactAlert.setTitle("New car");
        addContactAlert.setView(addContactLayout);
        addContactAlert.setNegativeButton("Cancel", null);
        addContactAlert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
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
        addContactAlert.create().show();
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
                    properties.load(new FileInputStream("Assets/settings.properties"));
                } catch (IOException e) {
                    e.printStackTrace();
                    exit(1);
                }
                properties.setProperty("car", cid);

                //refresh the page so the newest details are there
                activity.updateDetails(activity);
            }
        }.execute();

    }

    public void openHistory(View view) {
        //action when history button is pressed
//        Intent intent = new Intent(this, EntryHistoryActivity.class);
//        startActivity(intent);
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
