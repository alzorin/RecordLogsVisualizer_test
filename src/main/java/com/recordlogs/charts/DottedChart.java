package com.recordlogs.charts;

import com.recordlogs.model.Event;
import com.recordlogs.model.SourceData;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import org.gillius.jfxutils.chart.FixedFormatTickFormatter;
import org.gillius.jfxutils.chart.StableTicksAxis;

import java.text.SimpleDateFormat;
import java.util.*;

public class DottedChart {

    public static ScatterChart<Number,String> getDottedChart(SourceData sourceData) {
        double min = (double)sourceData.getMeasurements().stream().findFirst().get().getTimestamp().getTime();
        long count = sourceData.getMeasurements().stream().count();
        double max = (double)sourceData.getMeasurements().stream().skip(count - 1).findFirst().get().getTimestamp().getTime();
        var xAxis = new StableTicksAxis(min,max);
        xAxis.setLabel("time");
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS");
        format.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
        xAxis.setAxisTickFormatter(new FixedFormatTickFormatter( format ));
        var yAxis = new CategoryAxis();
        yAxis.setLabel("case");
        var chart = new ScatterChart<>(xAxis, yAxis);
        chart.setAnimated(false);
        return chart;
    }

    public static Map<String, XYChart.Series<Number, String>> getDataset(SourceData sourceData, List<String> activeCases, List<String> activeDataSets) {
        Map<String, XYChart.Series<Number,String>> activityTypeTimeSeries = new LinkedHashMap<>(); // We create a map containing time series data for each type of an activity
        sourceData.getActivityTypes()
                .forEach(type -> activityTypeTimeSeries.put(type, new XYChart.Series<Number, String>())); // We have a DataSet for each type of an activity
        for (Event event : sourceData.getEvents()) {
            if (activeCases.contains(event.getCaseID())) {
                activityTypeTimeSeries.get(event.getActivity())
                        .getData().add(new XYChart.Data(event.getTimestamp().getTime(), event.getCaseID()));
            }
        }
        for (String activity : sourceData.getActivityTypes()) {
            if (!(activeDataSets.contains(activity))) {
                activityTypeTimeSeries.remove(activity);
            }
        }
        return activityTypeTimeSeries;
    }

}