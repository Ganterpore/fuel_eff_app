package com.example.mitchell.UI;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import application.Controller;

/**
 * initial class created on startup
 */
public class MainActivity extends AppCompatActivity {
    Controller controller = null;
    int car;
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

        //collecting text fields and setting values to display
        final TextView efficiency = findViewById(R.id.AverageEfficiency);
        final TextView distance   = findViewById(R.id.TotalDistance);
        final TextView cost       = findViewById(R.id.TotalCost);
        final TextView litres     = findViewById(R.id.TotalLitres);

//        efficiency.setText(String.format("%3.2f", controller.entryC.getAverageEfficiency()));
//        distance.setText(String.format("%3.2f", controller.entryC.getTotalDistance()));
//        cost.setText(String.format("%3.2f", controller.entryC.getTotalCost()));
//        litres.setText(String.format("%3.2f", controller.entryC.getTotalLitres()));

        //creating the add entry button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            //when clicked a new entry will be created
            public void onClick(View view) {
                newEntry(view);
            }
        });
        //TODO add a "if there is no car in the database, create one" action
//        List<Car> cars = Controller.getCurrentController().getAllCars();
//        if(cars.size()==0) {
//            newCar();
//            cars = Controller.getCurrentController().getAllCars();
//        }
//        String defaultCid = String.valueOf(cars.get(0).getCid());
//        Properties properties = new Properties();
//        try {
//            properties.load(new FileInputStream("Assets/settings.properties"));
//        } catch(IOException e) {
//            e.printStackTrace();
//            exit(1);
//        }
//        car = Integer.parseInt(properties.getProperty("car", defaultCid));
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

    public void newCar() {
        //TODO
    }

    public void openHistory(View view) {
        //action when history button is pressed
//        Intent intent = new Intent(this, EntryHistoryActivity.class);
//        startActivity(intent);
    }

}
