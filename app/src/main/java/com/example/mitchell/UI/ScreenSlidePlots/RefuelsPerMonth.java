package com.example.mitchell.UI.ScreenSlidePlots;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.example.mitchell.UI.R;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Controller.Controller;
import Controller.EntryWrapper;

public class RefuelsPerMonth extends Fragment {

    private XYPlot plot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_refuels_per_month, container, false);

        // initialize our XYPlot reference:
        plot = rootView.findViewById(R.id.plot);

        buildGraph(plot, getArguments().getInt("carID"), getContext());

        return rootView;
    }

    /**
     * Builds the plot on a background thread
     * @param plot, the plot to draw on
     * @param carID, the carID to get the data for the plot from
     * @param context, the application context for the plotting
     */
    public static void buildGraph(final XYPlot plot, final Integer carID, final Context context) {

        new AsyncTask<Void, Void, List<EntryWrapper>>() {
            @Override
            /**
             * gets all the entries which will be added to the graph
             */
            protected List<EntryWrapper> doInBackground(Void... voids) {
                return Controller.getCurrentController().entryC.getAllEntriesOnCar(carID);
            }
            @Override
            /**
             * puts all entries in a series and plots the graph
             */
            protected void onPostExecute(List<EntryWrapper> entries) {
                //initiasing variables
                ArrayList<Long> months = new ArrayList<>();
                ArrayList<Integer> refuelsCount = new ArrayList<>();

                //getting values for the lists
                int monthIndices = -1; //used to track the index we are at in the lists
                for(EntryWrapper entry : entries) {
                    Date date = new Date(entry.getDate()); //the date of the entry

                    //getting the date of the first day of the month of the entry
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date);
                    cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
                    long monthNumber = cal.getTimeInMillis();

                    if(!months.contains(monthNumber)) {
                        // if the month does not exist yet, create a new month, move the index
                        //  and up the count by 1
                        months.add(monthNumber);
                        monthIndices++;
                        refuelsCount.add(1);
                    } else {
                        refuelsCount.set(monthIndices, refuelsCount.get(monthIndices) + 1); //iterate refuels by 1
                    }
                }

                //creating series and formatter
                XYSeries refuelCountSeries = new SimpleXYSeries(months, refuelsCount, "Efficiency");
                int colour = ContextCompat.getColor(context, R.color.colorPrimary);
                BarFormatter refuelFactorFormat = new BarFormatter(colour, Color.BLACK);

                //updating plot
                plot.addSeries(refuelCountSeries, refuelFactorFormat);
                BarRenderer renderer = plot.getRenderer(BarRenderer.class);
                renderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_GAP, 0);
                plot.setRangeLowerBoundary(0, BoundaryMode.FIXED);
                plot.setRangeStep(StepMode.INCREMENT_BY_VAL, 1);
                plot.getLayoutManager().remove(plot.getLegend());
                plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
                    @Override
                    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                        return toAppendTo.append(new SimpleDateFormat("MMM-yyyy").format(new Date(((Number) obj).longValue())));
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