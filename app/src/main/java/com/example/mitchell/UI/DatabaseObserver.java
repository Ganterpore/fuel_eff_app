package com.example.mitchell.UI;

/**
 * Interface used for reporting to observers when changes are made to the database
 */
public interface DatabaseObserver {
    ///The Types of objects which can be modified
    String FUEL = "fuel";
    String CAR = "car";
    String ENTRY = "entry";
    String TAG = "tag";

    /**
     * Class used to notify objects when the database ahs changed
     * @param object, the object added to the database
     * @param type, the type of the object.
     */
    void notifyChange(Object object, String type);
}
