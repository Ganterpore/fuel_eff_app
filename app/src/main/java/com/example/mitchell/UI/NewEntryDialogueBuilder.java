package com.example.mitchell.UI;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Controller.Controller;
import Controller.EntryWrapper;
import Models.Car;
import Models.EntryTag;
import Models.PetrolType;

public class NewEntryDialogueBuilder implements DatabaseObserver {
    private static NewEntryDialogueBuilder currentInstance;
    private DatabaseObserver observer;
    private ArrayAdapter<Car> carArrayAdapter;
    private ArrayAdapter<PetrolType> fuelArrayAdapter;
    private TagAdapter prevTagAdapter;
    private TagAdapter nextTagAdapter;
    private EntryWrapper prevEntry;

    /**
     * Used to create a Dialogue box to aid the user in the creation of a new Entry
     * @param observer, the observer to notify when a change has been made
     * @param activity, the activity from which to start the dialogue box from
     * @param cars, the cars from which to select from
     * @param fuels, the fuels in which to select from
     * @param tags, the tags in which to select from
     * @param prevEntry, optional previous entry that has been used. This is for editing purposes, and can be left blank if a new entry
     * @param currentCar, the currently selected car by the user.
     */
    public static void newEntry(final DatabaseObserver observer, final Activity activity,
                                final List<Car> cars, List<PetrolType> fuels, List<EntryTag> tags,
                                EntryWrapper prevEntry, final Car currentCar) {
        //setting instance variables
        NewEntryDialogueBuilder.currentInstance = new NewEntryDialogueBuilder();
        currentInstance.observer = observer;
        currentInstance.prevEntry = prevEntry;

        //inflating the views
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View addEntryLayout = layoutInflater.inflate(R.layout.new_entry_dialogue_box, null);

        //getting the view elements
        final EditText dateET = addEntryLayout.findViewById(R.id.entry_date);
        final EditText tripLengthET = addEntryLayout.findViewById(R.id.trip_length);
        final EditText litresFilledET = addEntryLayout.findViewById(R.id.fill_up_litres);
        final EditText priceET = addEntryLayout.findViewById(R.id.entry_price);
        final Spinner carChoices = addEntryLayout.findViewById(R.id.car_used);
        final Button addCarButton = addEntryLayout.findViewById(R.id.add_car_button);
        final Spinner fuelChoices = addEntryLayout.findViewById(R.id.fuel_used);
        final Button addFuelButton = addEntryLayout.findViewById(R.id.add_fuel_button);

        //if there was a previous entry, aka this is an edit, set the values in the edit entry
        if(prevEntry != null) {
            dateET.setText(prevEntry.getDateAsString());
            tripLengthET.setText(String.valueOf(prevEntry.getTrip()));
            litresFilledET.setText(String.valueOf(prevEntry.getLitres()));
            priceET.setText(String.valueOf(prevEntry.getPrice()));
        }

        //setting the spinner car choices
        currentInstance.carArrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item);
        currentInstance.carArrayAdapter.addAll(cars);
        carChoices.setAdapter(currentInstance.carArrayAdapter);

        //set current car to selected car
        int carIndex = currentInstance.carArrayAdapter.getPosition(currentCar);
        carChoices.setSelection(carIndex);

        addCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateCarDialogueBuilder.createCarDialogue(currentInstance, activity);
            }
        });

        //setting the spinner fuel choices
        currentInstance.fuelArrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item);
        currentInstance.fuelArrayAdapter.addAll(fuels);

        fuelChoices.setAdapter(currentInstance.fuelArrayAdapter);
        addFuelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FuelTypeDialogueBuilder.createFuelTypeDialogue(currentInstance, activity);
            }
        });

        //creating adapters for the tags
        currentInstance.nextTagAdapter = new TagAdapter(tags, activity);
        currentInstance.prevTagAdapter = new TagAdapter(tags, activity);

        //when the date eddit text is selected, open a calender to choose the date
        dateET.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                //hide keyboard
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(MainActivity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                //open date picker
                DatePickerDialog datePicker = new DatePickerDialog(activity);
                datePicker.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        dateET.setText(day+"/"+(month+1)+"/"+year);
                    }
                });
                datePicker.show();
            }
        });

        //building the alert dialogue
        AlertDialog.Builder addEntryAlert = new AlertDialog.Builder(activity);
        addEntryAlert.setTitle("New Entry");
        addEntryAlert.setView(addEntryLayout);
        //if this is an edit, remove the cancel button. Because it will have deleted the old entry
        if(prevEntry == null) {
            addEntryAlert.setNegativeButton("Cancel", null);
        } else {
            addEntryAlert.setCancelable(false);
        }
        addEntryAlert.setPositiveButton("Next", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //getting the text from the views
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
                //adding entry to database
                addEntry(startDate, tripLengthET.getText().toString(),
                        litresFilledET.getText().toString(),
                        priceET.getText().toString(),
                        (Car) carChoices.getSelectedItem(),
                        (PetrolType) fuelChoices.getSelectedItem(),
                        observer, activity);

            }
        });
        //displaying the dialogue to the UI
        addEntryAlert.create().show();
    }

    /**
     * Used to add an entry to the database
     */
    private static void addEntry(final long startDate, final String tripLength,
                                 final String litresFilled, final String price,
                                 final Car selectedCar, final PetrolType selectedFuel,
                                 final DatabaseObserver observer, final Activity activity) {
        new AsyncTask<Void, Void, EntryWrapper>() {

            @Override
            protected EntryWrapper doInBackground(Void... voids) {
                return Controller.getCurrentController().entryC.newEntry(
                        startDate,
                        Double.parseDouble(tripLength),
                        Double.parseDouble(litresFilled),
                        Double.parseDouble(price),
                        (selectedCar).getCid(),
                        (selectedFuel).getPid()
                );
            }

            @Override
            protected void onPostExecute(EntryWrapper entry) {
                //Start adding Tags to entry, and notify observer of change
                addPrevTripTagsToEntry(entry, observer, activity);
                observer.notifyChange(entry, DatabaseObserver.ENTRY );
            }
        }.execute();
    }

    /**
     * Creates a dialogue box for adding tags for the previous trip
     * @param entry, the entry to add the tags to
     * @param observer, the observer to notify of changes
     * @param activity, the activity to open the dialogue box in
     */
    private static void addPrevTripTagsToEntry(final EntryWrapper entry,
                                               final DatabaseObserver observer, final Activity activity) {
        //inflating views
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View addEntryLayout = layoutInflater.inflate(R.layout.add_prev_tags_to_entry, null);

        //getting View elements
        TextView addTag = addEntryLayout.findViewById(R.id.add_tag);
        addTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewTagDialogueBuilder.createTagDialogue(currentInstance, activity);
            }
        });
        //Building RecyclerView
        final RecyclerView allTags = addEntryLayout.findViewById(R.id.tag_list);
        allTags.setAdapter(currentInstance.prevTagAdapter);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(activity);
        allTags.setLayoutManager(manager);

        //TODO fix this
        //sets checked items from edited entry
        if(currentInstance.prevEntry != null) {
            currentInstance.prevTagAdapter.setCheckedTags(currentInstance.prevEntry.getTags(false));
        }

        //creating the Dialogue box
        AlertDialog.Builder addEntryAlert = new AlertDialog.Builder(activity);
        addEntryAlert.setTitle("Add tags for the previous trip");
        addEntryAlert.setView(addEntryLayout);
        addEntryAlert.setPositiveButton("Next", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //adding to database
                addTagsToDatabase(entry, observer, currentInstance.prevTagAdapter.getCheckedTags(), false);
                //starting dialogue for next choices
                addNextTripTagsToEntry(entry, observer, activity);
            }
        });
        addEntryAlert.create().show();
    }

    /**
     * Creates a dialogue box for adding tags for the next trip
     * @param entry, the entry to add the tags to
     * @param observer, the observer to notify of changes
     * @param activity, the activity to open the dialogue box in
     */
    private static void addNextTripTagsToEntry(final EntryWrapper entry, final DatabaseObserver observer,
                                               final Activity activity) {
        //inflating the views
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View addEntryLayout = layoutInflater.inflate(R.layout.add_next_tags_to_entry, null);

        //getting and building the view elements
        TextView addTag = addEntryLayout.findViewById(R.id.add_tag);
        addTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewTagDialogueBuilder.createTagDialogue(currentInstance, activity);
            }
        });

        final RecyclerView allTags = addEntryLayout.findViewById(R.id.tag_list);
        allTags.setAdapter(currentInstance.nextTagAdapter);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(activity);
        allTags.setLayoutManager(manager);

        //TODO fix this
        //sets checked items from edited entry
        if(currentInstance.prevEntry != null) {
            currentInstance.nextTagAdapter.setCheckedTags(currentInstance.prevEntry.getTags(true));
        }

        //Building the Dialogue box
        AlertDialog.Builder addEntryAlert = new AlertDialog.Builder(activity);
        addEntryAlert.setTitle("Add tags for the next trip");
        addEntryAlert.setView(addEntryLayout);
        addEntryAlert.setPositiveButton("Next", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //adding tags to the databse
                addTagsToDatabase(entry, observer, currentInstance.nextTagAdapter.getCheckedTags(), true);
                //moving to the next dialogue box
                addNotesToEntry(entry, observer, activity);
            }
        });
        addEntryAlert.create().show();
    }

    /**
     * Used to add tags to the database
     * @param entry, teh entry to atach the tags to
     * @param observer, the observer to notify of the changes
     * @param tags, the tags to add to the database
     * @param nextTrip, whether to attach the tags to the next or previous trip
     */
    private static void addTagsToDatabase(final EntryWrapper entry, final DatabaseObserver observer,
                                          final List<EntryTag> tags , final boolean nextTrip) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                //adding to tags to the database
                entry.addTags(nextTrip, tags);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                //notifying observer
                observer.notifyChange(entry, DatabaseObserver.ENTRY);
            }
        }.execute();
    }

    /**
     * Creates a dialogue box for adding a note to the entry
     * @param entry, the entry to add the note to
     * @param observer, the observer to notify of changes
     * @param activity, the activity to open the dialogue box in
     */
    private static void addNotesToEntry(final EntryWrapper entry, final DatabaseObserver observer,
                                        final Activity activity) {
        //inflating the views
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View addEntryLayout = layoutInflater.inflate(R.layout.add_notes_to_entry, null);

        //getting view elements
        final EditText noteET = addEntryLayout.findViewById(R.id.entry_note);
        if(currentInstance.prevEntry != null) {
            noteET.setText(currentInstance.prevEntry.getNote());
        }

        //building dialogue box
        AlertDialog.Builder addEntryAlert = new AlertDialog.Builder(activity);
        addEntryAlert.setTitle("Add a note to the entry");
        addEntryAlert.setView(addEntryLayout);
        addEntryAlert.setPositiveButton("Create", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                addNoteToDatabase(entry, noteET.getText().toString(), observer);
            }
        });
        addEntryAlert.create().show();
    }

    private static void addNoteToDatabase(final EntryWrapper entry, final String note,
                                          final DatabaseObserver observer) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                //adding note to database
                entry.addNote(note);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                //notifying observer of change
                observer.notifyChange(entry, DatabaseObserver.ENTRY );
            }
        }.execute();
    }

    /**
     * The RecyclerView ViewHolder for tags
     */
    public static class TagViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        EntryTag tag;

        TagViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.setIsRecyclable(false);
        }

        /**
         * Builds the view based on the tag given
         * @param tag, the tag to build around
         */
        void build(EntryTag tag) {
            this.tag = tag;
            TextView tagName = itemView.findViewById(R.id.list_tag_name);
            tagName.setText(tag.getName());
        }

        /**
         * Used to inform whether the current view is set as checked or not
         * @return whether the check box is checked
         */
        public boolean isChecked() {
            CheckBox checkBox = itemView.findViewById(R.id.check_box);
            return checkBox.isChecked();
        }
    }

    /**
     * The RecyclerView Adapter for tags
     */
    public static class TagAdapter extends RecyclerView.Adapter<TagViewHolder> {
        private ArrayList<EntryTag> tags;
        private Activity activity;
        private ArrayList<TagViewHolder> tagViewHolders;

        TagAdapter(List<EntryTag> tags, Activity activity) {
            this.activity = activity;
            this.tags = new ArrayList<>();
            this.tagViewHolders = new ArrayList<>();
            this.addAll(tags);
        }

        /**
         * Add a list of tags to the tag options on the adapter
         * @param newTags, the tags to add
         */
        void addAll(List<EntryTag> newTags) {
            tags.addAll(newTags);
        }

        /**
         * Add a tag to the adapter
         * @param tag, the tag to add
         */
        void addTag(EntryTag tag) {
            tags.add(tag);
        }

        @Override
        public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(activity).inflate(R.layout.tag_list_view, parent, false);

            TagViewHolder tagVH = new TagViewHolder(v);
            tagViewHolders.add(tagVH);
            return tagVH;
        }

        @Override
        public void onBindViewHolder(TagViewHolder holder, int position) {
            holder.build(tags.get(position));
        }

        @Override
        public int getItemCount() {
            return tags.size();
        }

        /**
         * Sets all the given tags to checked on the list
         * @param checkedTags, the tags to check
         */
        void setCheckedTags(List<EntryTag> checkedTags) {
            //TODO fix
            for(EntryTag checkedTag : checkedTags) {
                for(TagViewHolder tagViewHolder : tagViewHolders) {
                    if(checkedTag.equals(tagViewHolder.tag)) {
                        ((CheckBox) tagViewHolder.itemView.findViewById(R.id.check_box)).setChecked(true);
                    }
                }
            }
        }

        /**
         * Gets all the tags which have been selected on the adapter
         * @return the checked tags
         */
        List<EntryTag> getCheckedTags() {
            ArrayList<EntryTag> checkedTags = new ArrayList<>();
            //loop through tags and get the ones that have been checked
            for(TagViewHolder tagViewHolder : tagViewHolders) {
                if(tagViewHolder.isChecked()) {
                    checkedTags.add(tagViewHolder.tag);
                }
            }
            return checkedTags;
        }
    }

    @Override
    public void notifyChange(Object object, String type) {
        switch (type) {
            case DatabaseObserver.CAR:
                Car car = (Car) object;
                carArrayAdapter.add(car);
                break;
            case DatabaseObserver.FUEL:
                PetrolType petrol = (PetrolType) object;
                fuelArrayAdapter.add(petrol);
                break;
            case DatabaseObserver.TAG:
                EntryTag tag = (EntryTag) object;
                prevTagAdapter.addTag(tag);
                prevTagAdapter.notifyDataSetChanged();
                nextTagAdapter.addTag(tag);
                nextTagAdapter.notifyDataSetChanged();
                break;
        }
        //after reacting to the change, notify the entry observer of the change that occurred
        observer.notifyChange(object, type);
    }
}
