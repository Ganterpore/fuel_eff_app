package com.example.mitchell.UI;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import Controller.Controller;
import Models.EntryTag;

public class NewTagDialogueBuilder {

    /**
     * Used to create a Dialogue to assist the user in creating a new Tag
     * @param observer, the observer to notify when a change is made
     * @param activity, the activity from which the dialogue is instantiated
     */
    public static void createTagDialogue(final DatabaseObserver observer, Activity activity) {
        //inflating views
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View addTagLayout = layoutInflater.inflate(R.layout.new_tag_dialogue_box, null);

        //getting views
        final EditText tagName = addTagLayout.findViewById(R.id.tag_name);

        //creating Dialogue
        AlertDialog.Builder addTagAlert = new AlertDialog.Builder(activity);
        addTagAlert.setTitle("New Tag");
        addTagAlert.setView(addTagLayout);
        addTagAlert.setNegativeButton("Cancel", null);
        addTagAlert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                createTag(tagName.getText().toString(), observer);
            }
        });

        addTagAlert.create().show();
    }

    /**
     * Used to add tag to the database
     * @param tagName, the name of the tag
     * @param observer, the observer to notify
     */
    private static void createTag(final String tagName, final DatabaseObserver observer) {
        new AsyncTask<Void, Void, EntryTag>() {
            @Override
            protected EntryTag doInBackground(Void... voids) {
                //add tag to the database
                long tagId = Controller.getCurrentController().addTag(tagName);
                return Controller.getCurrentController().getTag((int) tagId);
            }

            @Override
            protected void onPostExecute(EntryTag tag) {
                //notify observer of change
                observer.notifyChange(tag, DatabaseObserver.TAG);
            }
        }.execute();
    }
}
