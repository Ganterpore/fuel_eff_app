package Models;

import java.util.concurrent.TimeUnit;

public class Trip {
    private double distance;
    private double efficiency;
    private int days;
    private double litres;
    private int car;
    private int fuel;

    public Trip(Entry start, Entry finish) {
        distance = finish.getTrip();
        efficiency = finish.getEfficiency();
        days = getDaysBetween(start.getDate(), finish.getDate());
        litres = finish.getLitres();
        car = start.getCar();
        fuel = start.getFuel();
    }

    //finds the days between two given dates
    private int getDaysBetween(long startDate, long finishDate) {
//        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
//        float days;
//        long diff = startDate.getTime() - finishDate.getTime();
//        days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
//        return (int) days;

        return (int) TimeUnit.DAYS.convert(finishDate-startDate, TimeUnit.MILLISECONDS);
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
