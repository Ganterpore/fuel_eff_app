package Models;

import java.util.concurrent.TimeUnit;

/**
 * Class for trips between refuels
 */
public class Trip {
    private double distance;
    private double efficiency;
    private int days;
    private double litres;
    private int car;
    private int fuel;
    private long startDate;

    /**
     * Creator for the Trip class
     * @param start, the Entry to start the trip
     * @param finish, the Entry which finishes the trip
     */
    public Trip(Entry start, Entry finish) {
        distance = finish.getTrip();
        efficiency = finish.getEfficiency();
        days = getDaysBetween(start.getDate(), finish.getDate());
        litres = finish.getLitres();
        car = start.getCar();
        fuel = start.getFuel();
        startDate = start.getDate();
    }

    /**
     * finds the days between two given dates, used to find the trip length.Z
     */
    private int getDaysBetween(long startDate, long finishDate) {
        return (int) TimeUnit.DAYS.convert(finishDate-startDate, TimeUnit.MILLISECONDS);
    }

    public long getStartDate() {
        return startDate;
    }

    public double getDistance() {
        return distance;
    }

    public double getEfficiency() {
        return efficiency;
    }

    public int getDays() {
        return days;
    }

    public double getLitres() {
        return litres;
    }

    public int getCar() {
        return car;
    }

    public int getFuel() {
        return fuel;
    }
}
