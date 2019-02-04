package edu.nyu.cs.pa.project;

public class ExoplanetExplorerApp {

    private void run() {
        ExoplanetAnalyzer necAnalyzer = new ExoplanetAnalyzer();
        necAnalyzer.prepData();
        necAnalyzer.clusterData();
//        necAnalyzer.graphClusters();
        necAnalyzer.classifyAndExamineSolarParameters();
        necAnalyzer.clusterAndClassifyPortionOfDataForTesting();
        System.out.println("\nFinished");
    }

    /**
     * Read in data from multiple TIC files. Combine and reduce data so that
     * only stars with all seven parameters of interest are retained.
     */
    private void generateFilteredStarData() {
        ExoplanetAnalyzer necAnalyzer = new ExoplanetAnalyzer();
        necAnalyzer.filterAndCombineStarData();
    }

    public static void main(String[] args) {
//        new ExoplanetExplorerApp().generateFilteredStarData();
        new ExoplanetExplorerApp().run();
    }
}
