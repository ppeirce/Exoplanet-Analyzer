package edu.nyu.cs.pa.plotting;

import java.util.ArrayList;
import java.util.List;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle;

public class twoDimensionalScatterPlot {

    /**
     * One Double[][]
     * @param matrix
     */
    public void oneDataSource(Double[][] matrix, String xUnits, String yUnits) {
        // data prep
        List<Double> xData = new ArrayList<Double>();
        List<Double> yData = new ArrayList<Double>();
        for (Double[] point : matrix) {
            xData.add(point[0]);
            yData.add(point[1]);
        }

        // chart construction
        XYChart chart = new XYChartBuilder().width(800).height(600).title("Exoplanets").xAxisTitle("Mass").yAxisTitle("Radius").build();
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
        chart.getStyler().setMarkerSize(4);
        chart.getStyler().setXAxisLogarithmic(true);
        chart.getStyler().setLegendVisible(false);
        //        chart.getStyler().setYAxisLogarithmic(true);
        chart.addSeries("first group", xData, yData);

        new SwingWrapper<XYChart>(chart).displayChart();

    }

    /**
     * Two Double[][]
     * @param m1
     * @param m2
     */
    public void twoDataSources(Double[][] m1, Double[][] m2, String xUnits, String yUnits) {
        List<Double> x1Data = new ArrayList<Double>();
        List<Double> y1Data = new ArrayList<Double>();
        List<Double> x2Data = new ArrayList<Double>();
        List<Double> y2Data = new ArrayList<Double>();
        for (Double[] point : m1) {
            x1Data.add(point[0]);
            y1Data.add(point[1]);
        }
        for (Double[] point : m2) {
            x2Data.add(point[0]);
            y2Data.add(point[1]);
        }
        // chart construction
        XYChart chart = new XYChartBuilder().width(800).height(600).title("Exoplanets").xAxisTitle("Mass").yAxisTitle("Radius").build();
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
        chart.getStyler().setMarkerSize(4);
        chart.getStyler().setXAxisLogarithmic(true);
        chart.getStyler().setLegendVisible(false);
//        chart.getStyler().setYAxisLogarithmic(true);
        chart.addSeries("first group", x1Data, y1Data);
        chart.addSeries("second group", x2Data, y2Data);

        new SwingWrapper<XYChart>(chart).displayChart();

    }

    public void twoListDataSources(List<Double[]> m1, List<Double[]> m2, String xUnits, String yUnits) {
        List<Double> x1Data = new ArrayList<Double>();
        List<Double> y1Data = new ArrayList<Double>();
        List<Double> x2Data = new ArrayList<Double>();
        List<Double> y2Data = new ArrayList<Double>();
        for (Double[] point : m1) {
            x1Data.add(point[0]);
            y1Data.add(point[1]);
        }
        for (Double[] point : m2) {
            x2Data.add(point[0]);
            y2Data.add(point[1]);
        }
        // chart construction
        XYChart chart = new XYChartBuilder().width(800).height(600).title("Exoplanet Clusters  (blue: Earth-like orange: Jupiter-like)").xAxisTitle(xUnits).yAxisTitle(yUnits).build();
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
        chart.getStyler().setMarkerSize(4);
        chart.getStyler().setXAxisLogarithmic(true);
        chart.getStyler().setLegendVisible(false);
//        chart.getStyler().setYAxisLogarithmic(true);
        chart.addSeries("first group", x1Data, y1Data);
        chart.addSeries("second group", x2Data, y2Data);

        new SwingWrapper<XYChart>(chart).displayChart();

    }

    public void threeListDataSources(List<Double[]> m1, List<Double[]> m2, List<Double[]> m3) {
        List<Double> x1Data = new ArrayList<Double>();
        List<Double> y1Data = new ArrayList<Double>();
        List<Double> x2Data = new ArrayList<Double>();
        List<Double> y2Data = new ArrayList<Double>();
        List<Double> x3Data = new ArrayList<Double>();
        List<Double> y3Data = new ArrayList<Double>();
        for (Double[] point : m1) {
            x1Data.add(point[0]);
            y1Data.add(point[1]);
        }
        for (Double[] point : m2) {
            x2Data.add(point[0]);
            y2Data.add(point[1]);
        }
        for (Double[] point : m3) {
            x3Data.add(point[0]);
            y3Data.add(point[1]);
        }
        // chart construction
        XYChart chart = new XYChartBuilder().width(800).height(600).title("Exoplanets").xAxisTitle("Mass").yAxisTitle("Radius").build();
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
        chart.getStyler().setMarkerSize(4);
        chart.getStyler().setXAxisLogarithmic(true);
        chart.getStyler().setLegendVisible(false);
//        chart.getStyler().setYAxisLogarithmic(true);
        chart.addSeries("first group", x1Data, y1Data);
        chart.addSeries("second group", x2Data, y2Data);
        chart.addSeries("third group", x3Data, y3Data);

        new SwingWrapper<XYChart>(chart).displayChart();

    }

    public void fourListDataSources(List<Double[]> m1, List<Double[]> m2, List<Double[]> m3, List<Double[]> m4) {
        List<Double> x1Data = new ArrayList<Double>();
        List<Double> y1Data = new ArrayList<Double>();
        List<Double> x2Data = new ArrayList<Double>();
        List<Double> y2Data = new ArrayList<Double>();
        List<Double> x3Data = new ArrayList<Double>();
        List<Double> y3Data = new ArrayList<Double>();
        List<Double> x4Data = new ArrayList<Double>();
        List<Double> y4Data = new ArrayList<Double>();
        for (Double[] point : m1) {
            x1Data.add(point[0]);
            y1Data.add(point[1]);
        }
        for (Double[] point : m2) {
            x2Data.add(point[0]);
            y2Data.add(point[1]);
        }
        for (Double[] point : m3) {
            x3Data.add(point[0]);
            y3Data.add(point[1]);
        }
        for (Double[] point : m4) {
            x4Data.add(point[0]);
            y4Data.add(point[1]);
        }
        // chart construction
        XYChart chart = new XYChartBuilder().width(800).height(600).title("Exoplanets").xAxisTitle("Mass").yAxisTitle("Radius").build();
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
        chart.getStyler().setMarkerSize(4);
        chart.getStyler().setXAxisLogarithmic(true);
        chart.getStyler().setLegendVisible(false);
//        chart.getStyler().setYAxisLogarithmic(true);
        chart.addSeries("first group", x1Data, y1Data);
        chart.addSeries("second group", x2Data, y2Data);
        chart.addSeries("third group", x3Data, y3Data);
        chart.addSeries("fourth group", x4Data, y4Data);

        new SwingWrapper<XYChart>(chart).displayChart();

    }

    public void fiveListDataSources(List<Double[]> m1, List<Double[]> m2, List<Double[]> m3, List<Double[]> m4, List<Double[]> m5) {
        List<Double> x1Data = new ArrayList<Double>();
        List<Double> y1Data = new ArrayList<Double>();
        List<Double> x2Data = new ArrayList<Double>();
        List<Double> y2Data = new ArrayList<Double>();
        List<Double> x3Data = new ArrayList<Double>();
        List<Double> y3Data = new ArrayList<Double>();
        List<Double> x4Data = new ArrayList<Double>();
        List<Double> y4Data = new ArrayList<Double>();
        List<Double> x5Data = new ArrayList<Double>();
        List<Double> y5Data = new ArrayList<Double>();
        for (Double[] point : m1) {
            x1Data.add(point[0]);
            y1Data.add(point[1]);
        }
        for (Double[] point : m2) {
            x2Data.add(point[0]);
            y2Data.add(point[1]);
        }
        for (Double[] point : m3) {
            x3Data.add(point[0]);
            y3Data.add(point[1]);
        }
        for (Double[] point : m4) {
            x4Data.add(point[0]);
            y4Data.add(point[1]);
        }
        for (Double[] point : m5) {
            x5Data.add(point[0]);
            y5Data.add(point[1]);
        }
        // chart construction
        XYChart chart = new XYChartBuilder().width(800).height(600).title("Exoplanets").xAxisTitle("Mass").yAxisTitle("Radius").build();
        chart.getStyler().setDefaultSeriesRenderStyle(XYSeriesRenderStyle.Scatter);
        chart.getStyler().setMarkerSize(4);
        chart.getStyler().setXAxisLogarithmic(true);
        chart.getStyler().setLegendVisible(false);
//        chart.getStyler().setYAxisLogarithmic(true);
        chart.addSeries("first group", x1Data, y1Data);
        chart.addSeries("second group", x2Data, y2Data);
        chart.addSeries("third group", x3Data, y3Data);
        chart.addSeries("fourth group", x4Data, y4Data);
        chart.addSeries("fifth group", x5Data, y5Data);

        new SwingWrapper<XYChart>(chart).displayChart();

    }
}
