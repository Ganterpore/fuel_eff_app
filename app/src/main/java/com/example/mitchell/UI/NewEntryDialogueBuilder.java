package com.example.mitchell.UI;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import Controller.Controller;
import Controller.EntryWrapper;
import Models.Car;
import Models.Entry;
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
        NewEntryDialogueBuilder.currentInstance = new NewEntryDialogueBuilder();
        currentInstance.observer = observer;

        currentInstance.prevEntry = prevEntry;

        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View addEntryLayout = layoutInflater.inflate(R.layout.new_entry_dialogue_box, null);

        final EditText dateET = addEntryLayout.findViewById(R.id.entry_date);
        final EditText tripLengthET = addEntryLayout.findViewById(R.id.trip_length);
        final EditText litresFilledET = addEntryLayout.findViewById(R.id.fill_up_litres);
        final EditText priceET = addEntryLayout.findViewById(R.id.entry_price);
        final Spinner carChoices = addEntryLayout.findViewById(R.id.car_used);
        final Button addCarButton = addEntryLayout.findViewById(R.id.add_car_button);
        final Spinner fuelChoices = addEntryLayout.findViewById(R.id.fuel_used);
        final Button addFuelButton = addEntryLayout.findViewById(R.id.add_fuel_button);

        if(prevEntry != null) {
            dateET.setText(prevEntry.getDateAsString());
            tripLengthET.setText(String.valueOf(prevEntry.getTrip()));
            litresFilledET.setText(String.valueOf(prevEntry.getLitres()));
            priceET.setText(String.valueOf(prevEntry.getPrice()));
        }

        currentInstance.carArrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item);
        currentInstance.carArrayAdapter.addAll(cars);
        carChoices.setAdapter(currentInstance.carArrayAdapter);

        int carIndex = currentInstance.carArrayAdapter.getPosition(currentCar);
        carChoices.setSelection(carIndex);

        addCarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateCarDialogueBuilder.createCarDialogue(currentInstance, activity);
            }
        });

        currentInstance.fuelArrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item);
        currentInstance.fuelArrayAdapter.addAll(fuels);

        fuelChoices.setAdapter(currentInstance.fuelArrayAdapter);
        addFuelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FuelTypeDialogueBuilder.createFuelTypeDialogue(currentInstance, activity);
            }
        });

        currentInstance.nextTagAdapter = new TagAdapter(tags, activity);
        currentInstance.prevTagAdapter = new TagAdapter(tags, activity);

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
        if(prevEntry == null) {
            addEntryAlert.setNegativeButton("Cancel", null);
        } else {
            addEntryAlert.setCancelable(false);
        }
        addEntryAlert.setPositiveButton("Next", new DialogInterface.OnClickListener() {
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
                new AsyncTask<Void, Void, EntryWrapper>() {

                    @Override
                    protected EntryWrapper doInBackground(Void... voids) {
                        EntryWrapper entry = Controller.getCurrentController().entryC.newEntry(
                                startDate,
                                Double.parseDouble(tripLengthET.getText().toString()),
                                Double.parseDouble(litresFilledET.getText().toString()),
                                Double.parseDouble(priceET.getText().toString()),
                                ((Car) carChoices.getSelectedItem()).getCid(),
                                ((PetrolType) fuelChoices.getSelectedItem()).getPid()
                        );
                        return entry;
                    }

                    @Override
                    protected void onPostExecute(EntryWrapper entry) {
                        addPrevTripTagsToEntry(entry, observer, activity);
                        observer.notifyChange(entry, DatabaseObserver.ENTRY );
                    }
                }.execute();

            }
        });
        //displaying the dialogue to the UI
        addEntryAlert.create().show();
    }

    /**
     * Creates a dialogue box for adding tags for the previous trip
     * @param entry, the entry to add the tags to
     * @param observer, the observer to notify of changes
     * @param activity, the activity to open the dialogue box in
     */
    private static void addPrevTripTagsToEntry(final EntryWrapper entry, final DatabaseObserver observer, final Activity activity) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View addEntryLayout = layoutInflater.inflate(R.layout.add_prev_tags_to_entry, null);

        TextView addTag = addEntryLayout.findViewById(R.id.add_tag);
        addTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewTagDialogueBuilder.createTagDialogue(currentInstance, activity);
            }
        });

        final RecyclerView allTags = addEntryLayout.findViewById(R.id.tag_list);
        allTags.setAdapter(currentInstance.prevTagAdapter);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(activity);
        allTags.setLayoutManager(manager);

        //TODO fix this
        if(currentInstance.prevEntry != null) {
            currentInstance.prevTagAdapter.setCheckedTags(currentInstance.prevEntry.getTags(false));
        }


        AlertDialog.Builder addEntryAlert = new AlertDialog.Builder(activity);
        addEntryAlert.setTitle("Add tags for the previous trip");
        addEntryAlert.setView(addEntryLayout);
//        addEntryAlert.setNegativeButton("Cancel", null);
        addEntryAlert.setPositiveButton("Next", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        List<EntryTag> checkedTags = currentInstance.prevTagAdapter.getCheckedTags();
                        Log.d("R", "doInBackground: adding tags"+ Arrays.toString(checkedTags.toArray()));
                        entry.addTags(false, checkedTags);

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        observer.notifyChange(entry, DatabaseObserver.ENTRY );
                    }
                }.execute();

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
    private static void addNextTripTagsToEntry(final EntryWrapper entry, final DatabaseObserver observer, final Activity activity) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View addEntryLayout = layoutInflater.inflate(R.layout.add_next_tags_to_entry, null);

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
        if(currentInstance.prevEntry != null) {
            currentInstance.nextTagAdapter.setCheckedTags(currentInstance.prevEntry.getTags(true));
        }


        AlertDialog.Builder addEntryAlert = new AlertDialog.Builder(activity);
        addEntryAlert.setTitle("Add tags for the next trip");
        addEntryAlert.setView(addEntryLayout);
//        addEntryAlert.setNegativeButton("Cancel", null);
        addEntryAlert.setPositiveButton("Next", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        List<EntryTag> checkedTags = currentInstance.nextTagAdapter.getCheckedTags();
                        Log.d("R", "doInBackground: adding tags"+ Arrays.toString(checkedTags.toArray()));
                        entry.addTags(true, checkedTags);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        observer.notifyChange(entry, DatabaseObserver.ENTRY );
                    }
                }.execute();
                Log.d("R", "onClick: notes dialogue being created");
                addNotesToEntry(entry, observer, activity);
            }
        });
        addEntryAlert.create().show();
    }

    /**
     * Creates a dialogue box for adding a note to the entry
     * @param entry, the entry to add the note to
     * @param observer, the observer to notify of changes
     * @param activity, the activity to open the dialogue box in
     */
    private static void addNotesToEntry(final EntryWrapper entry, final DatabaseObserver observer, final Activity activity) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View addEntryLayout = layoutInflater.inflate(R.layout.add_notes_to_entry, null);

        final EditText noteET = addEntryLayout.findViewById(R.id.entry_note);
        if(currentInstance.prevEntry != null) {
            noteET.setText(currentInstance.prevEntry.getNote());
        }

        AlertDialog.Builder addEntryAlert = new AlertDialog.Builder(activity);
        addEntryAlert.setTitle("Add a note to the entry");
        addEntryAlert.setView(addEntryLayout);
        addEntryAlert.setPositiveButton("Create", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        entry.addNote(noteET.getText().toString());
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        observer.notifyChange(entry, DatabaseObserver.ENTRY );
                    }
                }.execute();

            }
        });
        addEntryAlert.create().show();
    }

    /**
     * The RecyclerView ViewHolder for tags
     */
    public static class TagViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        EntryTag tag;

        public TagViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.setIsRecyclable(false);
        }

        /**
         * Builds the view based on the tag given
         * @param tag, the tag to build around
         */
        public void build(EntryTag tag) {
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

        public TagAdapter(List<EntryTag> tags, Activity activity) {
            this.activity = activity;
            this.tags = new ArrayList<>();
            this.tagViewHolders = new ArrayList<>();
            this.addAll(tags);
        }

        /**
         * Add a list of tags to the tag options on the adapter
         * @param newTags, the tags to add
         */
        public void addAll(List<EntryTag> newTags) {
            tags.addAll(newTags);
            Log.d("F", "addAll: tags added");
        }

        /**
         * Add a tag to the adapter
         * @param tag, the tag to add
         */
        public void addTag(EntryTag tag) {
            tags.add(tag);
        }

        @Override
        public TagViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(activity).inflate(R.layout.tag_list_view, parent, false);
            Log.d("F", "addAll: View inflated");

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
        public void setCheckedTags(List<EntryTag> checkedTags) {
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
        public List<EntryTag> getCheckedTags() {
            ArrayList<EntryTag> checkedTags = new ArrayList<>();
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

        observer.notifyChange(object, type);
    }
}
