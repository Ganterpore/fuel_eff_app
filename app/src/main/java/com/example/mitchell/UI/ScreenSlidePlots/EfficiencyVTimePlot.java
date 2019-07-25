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

import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.example.mitchell.UI.R;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Controller.Controller;
import Controller.EntryWrapper;

public class EfficiencyVTimePlot extends Fragment {

    private XYPlot plot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_efficiency_v_time_plot, container, false);

        // initialize our XYPlot reference:
        plot = rootView.findViewById(R.id.plot);

        buildGraph(plot, getArguments().getInt("carID"), getContext());

        return rootView;
    }

    public static void buildGraph(final XYPlot plot, final Integer carID, final Context context) {

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