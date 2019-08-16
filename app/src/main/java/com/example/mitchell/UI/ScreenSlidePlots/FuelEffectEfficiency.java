package com.example.mitchell.UI.ScreenSlidePlots;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.example.mitchell.UI.R;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import Controller.Controller;
import Controller.EntryWrapper;
import Models.PetrolType;

public class FuelEffectEfficiency extends Fragment {

    private XYPlot plot;

    private static class PlotItems {
        public ArrayList<String> fuelNames;
        public ArrayList<Double> efficiencyFactors;

        public PlotItems(ArrayList<String> fuelNames, ArrayList<Double> efficiencyFactors) {
            this.fuelNames = fuelNames;
            this.efficiencyFactors = efficiencyFactors;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_fuel_effect_efficiency, container, false);

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

        new AsyncTask<Void, Void, PlotItems>() {
            @Override
            /**
             * gets all the entries which will be added to the graph
             */
            protected PlotItems doInBackground(Void... voids) {
                //initialising values
                double averageEfficency = Controller.getCurrentController().entryC.getAverageEfficiency(carID);
                ArrayList<String> fuelNames = new ArrayList<>();
                ArrayList<Double> efficiencyFactors = new ArrayList<>();

                //iterating through all fuels and collecting the efficiency factor
                List<PetrolType> fuels = Controller.getCurrentController().getAllFuels();
                for (PetrolType fuel : fuels) {
                    //collecting the fuel name
                    fuelNames.add(fuel.getName());

                    //finding the average efficiency
                    List<EntryWrapper> entriesOnFuel = Controller.getCurrentController().entryC.getAllEntriesWithFuel(carID, fuel.getPid());
                    double efficiencySum = 0;
                    int count = 0;
                    for (EntryWrapper entry : entriesOnFuel) {
                        count++;
                        efficiencySum+= entry.getEfficiency();
                    }

                    //calculating the efficiency factor
                    double efficiencyFactor;
                    if(count > 0 & averageEfficency > 0) {
                        double averageEfficiency = efficiencySum/count;
                         efficiencyFactor = ((averageEfficiency/averageEfficency) - 1)*100;
                    } else {
                        efficiencyFactor = 0;
                    }
                    efficiencyFactors.add(efficiencyFactor);
                }
                return new PlotItems(fuelNames, efficiencyFactors);
            }
            @Override
            /**
             * puts all entries in a series and plits the graph
             */
            protected void onPostExecute(final PlotItems plotItems) {
                XYSeries efficiencyFactors = new SimpleXYSeries(plotItems.efficiencyFactors, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "EfficiencyFactor");
                BarFormatter efficiencyFactorFormat = new BarFormatter(R.color.colorPrimary, Color.BLACK);

                plot.addSeries(efficiencyFactors, efficiencyFactorFormat);
                plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);

                plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
                    @Override
                    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                        int i = Math.round(((Number) obj).floatValue());
                        return toAppendTo.append(plotItems.fuelNames.get(i));
                    }

                    @Override
                    public Object parseObject(String source, ParsePosition pos) {
                        return java.util.Arrays.asList(plotItems.fuelNames).indexOf(source);
                    }
                });
                plot.redraw();

//                Log.d("W", "onPostExecute: "+ Arrays.toString(plotItems.efficiencyFactors.toArray()));
//                Log.d("W", "onPostExecute: "+ Arrays.toString(plotItems.fuelNames.toArray()));
            }
        }.execute();
    }
}