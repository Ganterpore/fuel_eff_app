package com.example.mitchell.UI;

public interface DatabaseObserver {
    String FUEL = "fuel";
    String CAR = "car";
    String ENTRY = "entry";
    String TAG = "tag";

    void notifyChange(Object object, String type);
}
