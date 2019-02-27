package com.example.mitchell.UI;

import android.app.Activity;
import android.content.DialogInterface;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import Controller.Controller;
import Models.Entry;
import Models.EntryTag;
import Models.PetrolType;

public class NewTagDialogueBuilder {

    /**
     * Used to create a Dialogue to assist the user in creating a new Tag
     * @param observer, the observer to notify when a change is made
     * @param activity, the activity from which the dialogue is instantiated
     */
    public static void createTagDialogue(final DatabaseObserver observer, Activity activity) {
        LayoutInflater layoutInflater = LayoutInflater.from(activity);
        View addTagLayout  =   layoutInflater.inflate(R.layout.new_tag_dialogue_box, null);

        final EditText tagName = addTagLayout.findViewById(R.id.tag_name);

        AlertDialog.Builder addTagAlert = new AlertDialog.Builder(activity);
        addTagAlert.setTitle("New Tag");
        addTagAlert.setView(addTagLayout);
        addTagAlert.setNegativeButton("Cancel", null);
        addTagAlert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                new AsyncTask<Void, Void, EntryTag>() {

                    @Override
                    protected EntryTag doInBackground(Void... voids) {
                        long tagId = Controller.getCurrentController().addTag(tagName.getText().toString());
                        return Controller.getCurrentController().getTag((int) tagId);
                    }

                    @Override
                    protected void onPostExecute(EntryTag tag) {
                        Log.d("G", "onPostExecute: tag created, notifying");
                        observer.notifyChange(tag, DatabaseObserver.TAG);
                    }
                }.execute();
            }
        });

        addTagAlert.create().show();
    }
}
