package Controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import Models.Car;
import Models.Entry;
import Models.EntryTag;
import Models.PetrolType;
import Models.TagsOnEntry;
import database.AppDatabase;

/**
 * Used to access and Control all the data in the dataabse
 */
public class Controller {
    private static Controller currController = null;
    private AppDatabase db;
    public final EntryController entryC;

    public static Controller getCurrentController() {
        return currController;
    }

    public Controller(Context context) {
        this.db = database.DatabaseFactory.get(context);
        this.entryC = new EntryController(db);
        currController = this;
    }

    /*********Tag Code************/
    public long addTag(String name) {
        EntryTag tag = new EntryTag(name);
        return db.entryDao().addTag(tag);
    }

    public EntryTag getTag(int tid) {
        return db.entryDao().getTag(tid);
    }

    public List<EntryTag> getAllTags() {
        return db.entryDao().getAllTags();
    }
    /***********Car Code**********/
    public String newCar(@NonNull String carName, String licensePlate, String make, String model) {
        Car car = new Car();

        car.setLicensePlate(licensePlate);
        car.setMake(make);
        car.setModel(model);
        car.setName(carName);

        return Long.toString(db.entryDao().addCar(car));
    }

    public Car getCar(int cid) {
        return db.entryDao().getCar(cid);
    }

    public List<Car> getAllCars() {
        return db.entryDao().getAllCars();
    }

    public void deleteCar(Car car) {
        db.entryDao().deleteCar(car);
    }

    /***********Fuel Code*************/
    public long addFuel(String name, int percent) {
        PetrolType fuel = new PetrolType(name, percent);
        return db.entryDao().addFuel(fuel);
    }

    public PetrolType getFuel(long pid) {
        return db.entryDao().getFuel(pid);
    }

    public List<PetrolType> getAllFuels() {
        return db.entryDao().getAllFuels();
    }
}
