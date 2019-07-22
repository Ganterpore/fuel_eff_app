package com.example.mitchell.UI;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Debug;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;
import android.util.Log;

import com.androidplot.util.FastNumber;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

import Controller.Controller;
import Controller.EntryWrapper;
import database.AppDatabase;


public class EfficiencyVTimePlotActivity extends AppCompatActivity {

    private XYPlot plot;
    private Integer carID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efficiency_vtime_plot);

        // initialize our XYPlot reference:
        plot = findViewById(R.id.plot);

        //get the carID and build the graph for it
        carID = getIntent().getIntExtra("carID", 0);
        buildGraph(plot, carID, this);
    }

    private static void buildGraph(final XYPlot plot, final Integer carID, final Context context) {
        new AsyncTask<Void, Void, List<EntryWrapper>>() {
            @Override
            /**
             * gets all the entries which will be added to the graph
             */
            protected List<EntryWrapper> doInBackground(Void... voids) {
                return Controller.getCurrentController().entryC.getAllEntries(carID);
            }
            @Override
            /**
             * puts all entries in a series and plits the graph
             */
            protected void onPostExecute(List<EntryWrapper> entries) {
                //initiasing variables
                final ArrayList<String> dateStrings = new ArrayList<>();
                ArrayList<Long> dateValues = new ArrayList<>();
                ArrayList<Double> efficiency = new ArrayList<>();

                //getting values for the lists
                for(EntryWrapper entry : entries) {
                    dateStrings.add(entry.getDateAsString());
                    dateValues.add(entry.getDate());
                    efficiency.add(entry.getEfficiency());
                }

                //creating series
                XYSeries efficiencySeries = new SimpleXYSeries(dateValues, efficiency, "Efficiency");
//                LineAndPointFormatter efficiencyFormat =
//                        new LineAndPointFormatter(context, R.xml.line_point_formatter_with_labels);
                int graphColor = ContextCompat.getColor(context, R.color.colorPrimary);
                graphColor = Color.argb(95, Color.red(graphColor), Color.green(graphColor), Color.blue(graphColor));
                LineAndPointFormatter efficiencyFormat = new LineAndPointFormatter(R.color.colorPrimary, R.color.colorPrimaryDark, graphColor, null);
                efficiencyFormat.setInterpolationParams(
                        new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

                //updating plot
                plot.addSeries(efficiencySeries, efficiencyFormat);
                plot.getLayoutManager().remove(plot.getLegend());
                plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
                    @Override
                    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                        Log.d("G", "format: "+obj);
                        return toAppendTo.append(new SimpleDateFormat("dd/MM/yy").format(new Date(((Number) obj).longValue())));
                    }

                    @Override
                    public Object parseObject(String source, ParsePosition pos) {
                        return null;
                    }
                });
                plot.redraw();
            }
        }.execute();
    }
}
