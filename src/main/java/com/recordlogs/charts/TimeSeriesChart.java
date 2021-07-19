package com.recordlogs.charts;

import com.recordlogs.model.Measurement;
import com.recordlogs.model.SourceData;
import javafx.scene.chart.*;
import org.gillius.jfxutils.chart.FixedFormatTickFormatter;
import org.gillius.jfxutils.chart.StableTicksAxis;

import java.text.SimpleDateFormat;
import java.util.*;

public class TimeSeriesChart {

    public static LineChart<Number, Number> getTimeSeriesChart(SourceData sourceData) {
        double min = (double)sourceData.getMeasurements().stream().findFirst().get().getTimestamp().getTime();
        long count = sourceData.getMeasurements().stream().count();
        double max = (double)sourceData.getMeasurements().stream().skip(count - 1).findFirst().get().getTimestamp().getTime();
        var xAxis = new StableTicksAxis(min,max);
        xAxis.setLabel("time");
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:SS");
        format.setTimeZone( TimeZone.getTimeZone( "UTC" ) );
        xAxis.setAxisTickFormatter(new FixedFormatTickFormatter(format));
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("value");
        final LineChart<Number, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setAnimated(false);
        return chart;
    }


    public static Map<String, XYChart.Series<Number, Number>> getDatasetsPerCase(SourceData sourceData, List<String> activeCasesList, String activeMeasurement) {
        Map<String, XYChart.Series<Number, Number>> datasetsPerCase = new LinkedHashMap<>();
        Set<String> activeCases = new HashSet<>(activeCasesList);
        sourceData.getCaseIDs()
                .forEach(caseID -> datasetsPerCase.put(caseID+" "+activeMeasurement, new XYChart.Series<Number, Number>()));
        sourceData.getMeasurements()
                .stream()
                .filter(measurement -> activeCases.contains(measurement.getCaseID()))
                .forEach(measurement -> {
                    datasetsPerCase.get(measurement.getCaseID()+" "+activeMeasurement)
                            .getData().add(new XYChart.Data(measurement.getTimestamp().getTime(),getValue(measurement, activeMeasurement)));
                });
        return datasetsPerCase;
    }

    private static double getValue(Measurement measurement, String selectedMeasurement) {
        try {
            return Double.parseDouble(measurement.getMeasurementValue().get(selectedMeasurement));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
