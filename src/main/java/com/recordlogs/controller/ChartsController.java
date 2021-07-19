package com.recordlogs.controller;

import com.recordlogs.SceneData;
import com.recordlogs.charts.DottedChart;
import com.recordlogs.charts.TimeSeriesChart;
import com.recordlogs.model.SourceData;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.gillius.jfxutils.chart.JFXChartUtil;

import java.io.IOException;
import java.util.*;


public class ChartsController {

    @FXML
    private VBox charts;

    @FXML
    private MenuBar menuBar;

    @FXML
    private Menu measurementsMenu;

    @FXML
    private Menu casesMenu;

    @FXML
    private MenuItem selectAllCases;

    @FXML
    private MenuItem deselectAllCases;

    @FXML
    private MenuItem selectAllDatasets;

    @FXML
    private MenuItem deselectAllDatasets;

    @FXML
    private Menu datasetsMenu;

    @FXML
    private StackPane ChartsPane;

    private String activeMeasurement;
    private List<String> activeCases;
    private List<String> activeDataSets;
    private ScatterChart<Number,String> dottedChart;
    private LineChart<Number, Number> timeSeriesChart;

    private SourceData sourceData = SceneData.sourceData;
    private Set<String> selectedMeasurements = SceneData.selectedMeasurements;

    @FXML
    void closeButtonPushed(ActionEvent event) {
        Stage window = (Stage) menuBar.getScene().getWindow();
        window.close();
    }

    @FXML
    void goToStart(ActionEvent event) throws IOException {
        Parent loadParent = FXMLLoader.load(getClass().getResource("/Start.fxml"));
        Scene load = new Scene(loadParent);
        Stage window = (Stage) menuBar.getScene().getWindow();
        window.setScene(load);
        window.show();
    }

    @FXML
    void openChartHelp(ActionEvent event) throws IOException {
        try{
            Parent loadParent = FXMLLoader.load(getClass().getResource("/StartHelp.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Help");
            stage.setScene(new Scene(loadParent));
            stage.show();}
        catch(Exception e)
        {
            System.out.println("Can not load the second window");
        }

    }
    @FXML
    void initialize() {
        activeCases = new ArrayList();
        activeDataSets = new ArrayList();
        activeMeasurement = selectedMeasurements.stream().findFirst().orElseThrow();
        fillMeasurementsMenu(selectedMeasurements, activeMeasurement);
        activeCases = fillCasesMenu(sourceData, activeCases);
        activeDataSets = fillDataSetsMenu(sourceData, activeDataSets);

        dottedChart = DottedChart.getDottedChart(sourceData);
        timeSeriesChart = TimeSeriesChart.getTimeSeriesChart(sourceData);

        measurementsMenu.setOnAction(event -> measurementChecked(event, timeSeriesChart, sourceData));
        casesMenu.setOnAction(event -> caseChecked(event, timeSeriesChart, sourceData));
        datasetsMenu.setOnAction(event -> dataSetChecked(event, sourceData));

//        JFXChartUtil.setupZooming(dottedChart);
//        JFXChartUtil.setupZooming(timeSeriesChart);
//
//        //JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(dottedChart);
//        //JFXChartUtil.addDoublePrimaryClickAutoRangeHandler(timeSeriesChart);

        refreshCharts(sourceData, timeSeriesChart, dottedChart);
//        bindAxis(timeSeriesChart, dottedChart);

//        dottedChart.getXAxis().lowerBoundProperty().bindBidirectional(lowerDateAxis.lowerBoundProperty());
//        upperDateAxis.upperBoundProperty().bindBidirectional(lowerDateAxis.upperBoundProperty());
//        upperDateAxis.autoRangingProperty().bindBidirectional(lowerDateAxis.autoRangingProperty());

        final VBox vbox = new VBox(dottedChart, timeSeriesChart);
        vbox.setVgrow(dottedChart, Priority.ALWAYS);
        vbox.setVgrow(timeSeriesChart, Priority.ALWAYS);
        ChartsPane.getChildren().add(vbox);
        System.out.println("Total loading time:" +((double) (System.currentTimeMillis() - SceneData.loadTime) / (1000))+"seconds");
    }
//    private static void bindAxis(final XYChart chartPane1, final XYChart chartPane2) {
//        final DefaultNumericAxis xAxis1 = (DefaultNumericAxis) chartPane1.getXAxis();
//        final DefaultNumericAxis xAxis2 = (DefaultNumericAxis) chartPane2.getXAxis();
//        xAxis1.autoRangingProperty().bindBidirectional(xAxis2.autoRangingProperty());
//        xAxis1.maxProperty().bindBidirectional(xAxis2.maxProperty());
//        xAxis1.minProperty().bindBidirectional(xAxis2.minProperty());
//    }

    private void fillMeasurementsMenu(Set<String> selectedMeasurements, String activeMeasurement) {
        selectedMeasurements.forEach(measurementValueName -> {
            CheckMenuItem item = new CheckMenuItem(measurementValueName);
            measurementsMenu.getItems().add(item);
            if (measurementValueName.equals(activeMeasurement)) {
                item.setSelected(true);
            }
        });
    }
    private List<String> fillCasesMenu(SourceData sourceData, List<String> activeCases) {
        sourceData.getCaseIDs().forEach(caseID -> {
            CheckMenuItem item = new CheckMenuItem(caseID);
            casesMenu.getItems().add(item);
            activeCases.add(caseID);
                item.setSelected(true);
        });
        return activeCases;
    }
    private List<String> fillDataSetsMenu(SourceData sourceData, List<String> activeDatasets) {

        for (String activity : sourceData.getActivityTypes())
        {
            activeDataSets.add(activity);
        }
        Collections.sort(activeDataSets);
        for (String activity : activeDatasets) {
            CheckMenuItem item = new CheckMenuItem(activity);
            datasetsMenu.getItems().add(item);
            item.setSelected(true);
        }

        return activeDatasets;
    }

    private void refreshCharts(SourceData sourceData, LineChart<Number, Number> timeSeriesChart,  ScatterChart<Number,String> dottedChart) {
       // Map<String, DoubleDataSet> datasetsPerCase = TimeSeriesChart.getDatasetsPerCase(sourceData, activeCases, activeMeasurement);
        timeSeriesChart.getData().clear();
        dottedChart.getData().clear();
       //timeSeriesChart.getDatasets().addAll(datasetsPerCase.values());
        Map<String, XYChart.Series<Number,Number>> timeSeriesChartData;
        Map<String, XYChart.Series<Number,String>> dottedChartData;
        timeSeriesChartData = TimeSeriesChart.getDatasetsPerCase(sourceData, activeCases, activeMeasurement);
        timeSeriesChartData.forEach((key,value) -> value.setName(key));
        timeSeriesChartData.forEach((key,value) -> timeSeriesChart.getData().add(value));
        dottedChartData = DottedChart.getDataset(sourceData, activeCases, activeDataSets);
        dottedChartData.forEach((key,value) -> value.setName(key));
        dottedChartData.forEach((key,value) -> dottedChart.getData().add(value));
    }

    private void measurementChecked(ActionEvent event, LineChart<Number, Number> chart, SourceData sourceData) {
        CheckMenuItem target = (CheckMenuItem) event.getTarget();
        String clickedMeasurement = target.getText();
        measurementsMenu
                .getItems()
                .forEach(item -> {
                    CheckMenuItem checkMenuItem = (CheckMenuItem) item;
                    boolean selected = target.getText().equals(checkMenuItem.getText());
                    checkMenuItem.setSelected(selected);
                });
        activeMeasurement = clickedMeasurement;
        refreshCharts(sourceData, chart, dottedChart);
    }

    private void caseChecked(ActionEvent event, LineChart<Number, Number> chart, SourceData sourceData) {
        if (!(event.getTarget() instanceof CheckMenuItem))
        {
            if (event.getTarget()==selectAllCases)
            {
                activeCases.clear();
                for (String caseID : sourceData.getCaseIDs())
                {
                    activeCases.add(caseID);
                }
                casesMenu
                        .getItems()
                        .stream().filter(item -> item instanceof CheckMenuItem)
                        .forEach(item -> {
                            CheckMenuItem checkMenuItem = (CheckMenuItem) item;
                            checkMenuItem.setSelected(true);
                        });
            }
            if (event.getTarget()==deselectAllCases)
            {
                activeCases.clear();
                casesMenu
                        .getItems()
                        .stream().filter(item -> item instanceof CheckMenuItem)
                        .forEach(item -> {
                            CheckMenuItem checkMenuItem = (CheckMenuItem) item;
                            checkMenuItem.setSelected(false);
                        });
            }
        }
        else {
            CheckMenuItem target = (CheckMenuItem) event.getTarget();
            if (target.isSelected()) {
                activeCases.add(target.getText());
            } else {
                activeCases.remove(target.getText());
            }
            casesMenu
                    .getItems()
                    .stream().filter(item -> item instanceof CheckMenuItem)
                    .forEach(item -> {
                        CheckMenuItem checkMenuItem = (CheckMenuItem) item;
                        checkMenuItem.setSelected(activeCases.stream().anyMatch(text -> text.equalsIgnoreCase(checkMenuItem.getText())));
                    });
        }
        refreshCharts(sourceData, chart, dottedChart);
    }
    private void dataSetChecked(ActionEvent event, SourceData sourceData) {
        if (!(event.getTarget() instanceof CheckMenuItem))
        {
            if (event.getTarget()==selectAllDatasets)
            {
                activeDataSets.clear();
                for (String activity : sourceData.getActivityTypes())
                {
                    activeDataSets.add(activity);
                }
                datasetsMenu
                        .getItems()
                        .stream().filter(item -> item instanceof CheckMenuItem)
                        .forEach(item -> {
                            CheckMenuItem checkMenuItem = (CheckMenuItem) item;
                            checkMenuItem.setSelected(true);
                        });
            }
            if (event.getTarget()==deselectAllDatasets)
            {
                activeDataSets.clear();
                datasetsMenu
                        .getItems()
                        .stream().filter(item -> item instanceof CheckMenuItem)
                        .forEach(item -> {
                            CheckMenuItem checkMenuItem = (CheckMenuItem) item;
                            checkMenuItem.setSelected(false);
                        });
            }
        }
        else {
            CheckMenuItem target = (CheckMenuItem) event.getTarget();
            if (target.isSelected()) {
                activeDataSets.add(target.getText());
            } else {
                activeDataSets.remove(target.getText());
            }

            datasetsMenu
                    .getItems()
                    .stream().filter(item -> item instanceof CheckMenuItem)
                    .forEach(item -> {
                        CheckMenuItem checkMenuItem = (CheckMenuItem) item;
                        checkMenuItem.setSelected(activeDataSets.stream().anyMatch(text -> text.equalsIgnoreCase(checkMenuItem.getText())));
                    });
        }
        refreshCharts(sourceData, timeSeriesChart, dottedChart);
    }
}
