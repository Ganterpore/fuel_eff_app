package Controller;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import Models.Car;
import Models.EntryTag;
import Models.PetrolType;
import database.AppDatabase;

public class Controller {
    private static Controller currController = null;
    AppDatabase db;
    public final EntryController entryC;

    public static Controller getCurrentController() {
        return currController;
    }

    public Controller(Context context) {
        this.db = database.DatabaseFactory.get(context);
        this.entryC = new EntryController(db);
        currController = this;
    }

    public List<EntryTag> getAllTags() {
        return db.entryDao().getAllTags();
    }

    public List<Car> getAllCars() {
        return db.entryDao().getAllCars();
    }

    public void addTag(String name) {
        EntryTag tag = new EntryTag(name);
        db.entryDao().addTag(tag);
    }

    public long addFuel(String name, int percent) {
        PetrolType fuel = new PetrolType(name, percent);
        return db.entryDao().addFuel(fuel);
    }

    public List<PetrolType> getAllFuels() {
        return db.entryDao().getAllFuels();
    }

    public EntryTag getTag(int tid) {
        return db.entryDao().getTag(tid);
    }

    public Car getCar(int cid) {
        return db.entryDao().getCar(cid);
    }

    public String newCar(@NonNull String carName, String licensePlate, String make, String model) {
        Car car = new Car();

        car.setLicensePlate(licensePlate);
        car.setMake(make);
        car.setModel(model);
        car.setName(carName);

        String cid = Long.toString(db.entryDao().addCar(car));
        Log.d("A", "newCar: "+cid);
        return cid;

    }

    public PetrolType getFuel(long pid) {
        return db.entryDao().getFuel(pid);
    }

    public List<TripWrapper> getAllTrips(int cid) {
        List<Integer> entries = db.entryDao().getOrderedEid(cid);
        List<TripWrapper> trips = new ArrayList<>();
        for(int i=1;i<entries.size();i++) {
            trips.add(new TripWrapper(entries.get(i-1), entries.get(i), db));
        }
        return trips;
    }

    public List<TripWrapper> getTripsWithTags(int cid, int tid) {
        List<TripWrapper> trips = getAllTrips(cid);
        List<TripWrapper> tripsTrimmed = new ArrayList<>();
        EntryTag tag = this.getTag(tid);
        for(TripWrapper trip : trips) {
            if(trip.getTags().contains(tag)) {
                tripsTrimmed.add(trip);
            }
        }
        return tripsTrimmed;
    }
}
