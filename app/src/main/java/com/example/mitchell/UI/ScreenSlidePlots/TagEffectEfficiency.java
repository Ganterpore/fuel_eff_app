package com.example.mitchell.UI.ScreenSlidePlots;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.androidplot.xy.BarFormatter;
import com.androidplot.xy.BarRenderer;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.StepMode;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.example.mitchell.UI.R;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Controller.Controller;
import Controller.EntryWrapper;
import Models.EntryTag;

public class TagEffectEfficiency extends Fragment {
    private XYPlot plot;

    private static class PlotItems {
        public ArrayList<String> tagNames;
        public ArrayList<Double> efficiencyFactors;

        public PlotItems(ArrayList<String> tagNames, ArrayList<Double> efficiencyFactors) {
            this.tagNames = tagNames;
            this.efficiencyFactors = efficiencyFactors;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_tag_effect_efficiency, container, false);

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
                double totalAverageEfficiency = Controller.getCurrentController().entryC.getAverageEfficiency(carID);
                ArrayList<String> tagNames = new ArrayList<>();
                ArrayList<Double> efficiencyFactors = new ArrayList<>();

                //iterating through all tags and collecting the efficiency factor
                List<EntryTag> tags = Controller.getCurrentController().getAllTags();
                for (EntryTag tag : tags) {
                    //collecting the tag name
                    tagNames.add(tag.getName());

                    //finding the average efficiency
                    int tid = tag.getTid();
                    List<EntryWrapper> entriesOnTag = Controller.getCurrentController().entryC.getAllEntriesWithTag(carID, tid);
                    double efficiencySum = 0;
                    int count = 0;
                    for (EntryWrapper entry : entriesOnTag) {
                        count++;
                        efficiencySum+= entry.getEfficiency();
                    }

                    //calculating the efficiency factor
                    double efficiencyFactor;
                    if(count > 0 & totalAverageEfficiency > 0) {
                        double averageEfficiency = efficiencySum/count;
                        efficiencyFactor = ((averageEfficiency/totalAverageEfficiency) - 1)*100;
                    } else {
                        efficiencyFactor = 0;
                    }
                    efficiencyFactors.add(efficiencyFactor);
                }
                return new PlotItems(tagNames, efficiencyFactors);
            }
            @Override
            /**
             * puts all entries in a series and plits the graph
             */
            protected void onPostExecute(final PlotItems plotItems) {
                XYSeries efficiencyFactors = new SimpleXYSeries(plotItems.efficiencyFactors, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "EfficiencyFactor");
                int colour = ContextCompat.getColor(context, R.color.colorPrimary);
                BarFormatter efficiencyFactorFormat = new BarFormatter(colour, Color.BLACK);

                plot.addSeries(efficiencyFactors, efficiencyFactorFormat);
                plot.setDomainStep(StepMode.INCREMENT_BY_VAL, 1);
//                plot.setDomainLowerBoundary(-0.5, BoundaryMode.FIXED);
                plot.setPlotMarginLeft(100);
                plot.setRangeStep(StepMode.INCREMENT_BY_VAL, 1);
                plot.setUserRangeOrigin(0);
                plot.getLayoutManager().remove(plot.getLegend());
                plot.getGraph().getDomainGridLinePaint().setAlpha(0);

                BarRenderer renderer = plot.getRenderer(BarRenderer.class);
                renderer.setBarGroupWidth(BarRenderer.BarGroupWidthMode.FIXED_GAP, 50);

                plot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM).setFormat(new Format() {
                    @Override
                    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
                        int i = Math.round(((Number) obj).floatValue());
                        return toAppendTo.append(plotItems.tagNames.get(i));
                    }

                    @Override
                    public Object parseObject(String source, ParsePosition pos) {
                        return Arrays.asList(plotItems.tagNames).indexOf(source);
                    }

                });
                plot.redraw();
            }
        }.execute();
    }
}