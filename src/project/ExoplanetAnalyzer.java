package edu.nyu.cs.pa.project;

import java.io.*;
import java.util.*;

import edu.nyu.cs.pa.algorithms.Dbscan;
import edu.nyu.cs.pa.algorithms.KMeans;
import edu.nyu.cs.pa.algorithms.KNN;
import edu.nyu.cs.pa.plotting.twoDimensionalScatterPlot;

public class ExoplanetAnalyzer {
    private static final File NASA_CLEAN = new File("data/nasa_exoplanet_catalog2.tsv");

    private Double[][] npcMassRadiusMatrix;
    private Double[][] npcStandardizedMassRadiusMatrix;
    private Double[][] npcStellarParameterMatrix;
    private List<Double[]> combinedStellarParameterMatrix;
    private List<Double[]> planetsSubsetTraining;
    private List<Double[]> planetsSubsetTesting;
    private List<Double[]> parametersOfStarsPredictedToHaveEarthLikePlanets;
    private int[] planetLabelArray;
    private List<Integer> labelsOfPlanetsWithAllStellarProperties;
    private List<Integer> labelsWithStellarPropertiesSubset;
    private List<Integer> labelsOfRemainingTenPercentForComparison;
    private int earthLikeClusterId;
    private int jupiterLikeClusterId;

    /**
     * <p>
     *     List where each row represents a planet and each parameter is a stellar property of the
     *     star the planet orbits. Every planet in this list has a value for every stellar property.
     *     There are no empty, null or Double.MIN_VALUE entries.
     * </p>
     * <p>
     *     This list is based off of the npcStellarParameterMatrix.
     * </p>
     */
    private List<Double[]> npcStellarParameterList;

    public ExoplanetAnalyzer() {

    }












    /**
     * <ol><li> Fills the original planet list and definition map with data from the TSV files
     * obtained from NASA.
     * <li> Data reduction: filter the planet list to include only planets that have data
     * for mass, radius
     *     <ul><li>The size of this data is primarily limited by the number of entries
     *     with mass data. From the total data set (3826 planets) only 1416 have a mass
     *     measurement. 3022 have a radius measurement.</li>
     * </ol>
     */
    public void prepData() {
        List<String[]> npcPlanetsWithMassAndRadius = generateNpcPlanetsWithMassAndRadius();
        npcStandardizedMassRadiusMatrix = generateStandardizedNpcMatrix(npcPlanetsWithMassAndRadius);
        npcStellarParameterMatrix = generateNpcStellarParameterMatrix(npcPlanetsWithMassAndRadius);
        combinedStellarParameterMatrix = generateTicStellarParameterMatrix(new File("data/combined_tic.csv"));
    }

    private List<String[]> generateNpcPlanetsWithMassAndRadius() {
        List<String[]> npcPlanets = readTsvToListOfArrays(NASA_CLEAN);
        return filterPlanetsWithoutMassAndRadius(npcPlanets);
    }

    private Double[][] generateStandardizedNpcMatrix(List<String[]> planetList) {
        npcMassRadiusMatrix = generateMassRadiusMatrix(planetList);
        return standardizeMatrix(npcMassRadiusMatrix);
    }

    /**
     * <ol>
     * <li>Cluster the standardized data using the KMeans algorithm.</li>
     * <li>Determine which cluster represents the "Earth-like" planets</li>
     * <li>Graph the original data using the clusters obtained from the standardized data.</li>
     * <li>Optional: test DBSCAN to verify that 2 clusters is optimal.</li>
     * </ol>
     */
    public void clusterData() {
        planetLabelArray = generateLabelArray();
        setClusterLabels(planetLabelArray);
        npcStellarParameterList = generateListOfPlanetsWithAllStellarParameters();

    }
    
    public void graphClusters() {
        splitIntoTwoClustersAndGraph(npcMassRadiusMatrix, planetLabelArray);        
    }

    /**
     * Runs DBSCAN on the the npc standardized mass/radius data with multiple combinations
     * of eps and minpts values and prints the results of each run
     */
    public void dbscanTesting() {
        tryMultipleDbscanParameters(npcStandardizedMassRadiusMatrix);
        dbscanWith(npcMassRadiusMatrix, 2, 0.10);
    }

    private void setClusterLabels(int[] labels) {
        setEarthLikeClusterId(labels[1]);
        System.out.println("Earth-like is cluster " + labels[1]);
        System.out.println("Jupiter-like is cluster " + labels[0]);
        setJupiterLikeClusterId(labels[0]);

    }

    /**
     * Ensure the earth-like cluster has id 0
     * @return
     */
    private int[] generateLabelArray() {
        int[] labelArray;
        do {
            KMeans km = runKMeans(npcStandardizedMassRadiusMatrix);
            labelArray = km.getLabelArray();
        } while (labelArray[1] == 1);
        return labelArray;
    }

    /**
     * Performs clustering on the first ~90% of the mass/radius planet matrix.
     */
    public void clusterAndClassifyPortionOfDataForTesting() {
        int[] labels = performClusteringForTesting();
        performAnalysisForTesting(labels, labelsOfRemainingTenPercentForComparison);
    }

    private int[] performClusteringForTesting() {
        int[] classificationLabels = new KNN().classify(3, planetsSubsetTesting, planetsSubsetTraining, labelsWithStellarPropertiesSubset);
        System.out.println("\nActual labels:     " + labelsOfRemainingTenPercentForComparison.toString());
        System.out.println("Predicted labels:  " + Arrays.toString(classificationLabels));
        return classificationLabels;
    }

    /**
     * Generates confusion matrix, recall, precision, and f1 scores
     */
    private void performAnalysisForTesting(int[] prediction, List<Integer> actual) {
        final int numberOfClusters = 2;
        int[][] confusionMatrix = generateConfusionMatrix(prediction, actual, numberOfClusters);
        double[] recallScores = generateRecallScores(confusionMatrix);
        double[] precisionScores = generatePrecisionScores(confusionMatrix);
        double[] f1Scores = generateFMeasure(recallScores, precisionScores);
        printConfusionMatrixRecallPrecisionFMeasure(confusionMatrix, recallScores, precisionScores, f1Scores);
    }

    private void printConfusionMatrixRecallPrecisionFMeasure(int[][] confusionMatrix,
                                                             double[] recall,
                                                             double[] precision,
                                                             double[] fMeasure) {
        System.out.println("\nConfusion matrix [Earth | Jupiter]");
        for (int[] row : confusionMatrix) { System.out.println(Arrays.toString(row)); }
        System.out.printf("Recall:     %.2f %.2f\n", recall[0], recall[1]);
        System.out.printf("Precision:  %.2f %.2f\n", precision[0], precision[1]);
        System.out.printf("F1 Measure: %.2f %.2f\n", fMeasure[0], fMeasure[1]);
    }

    private double[] generateFMeasure(double[] recall, double[] precision) {
        double[] fMeasure = new double[2];
        fMeasure[0] = 2 * ( (precision[0] * recall[0]) / (precision[0] + recall[0]) );
        fMeasure[1] = 2 * ( (precision[1] * recall[1]) / (precision[1] + recall[1]) );
        return fMeasure;
    }

    /**
     * Generates the precision scores of a 2x2 confusion matrix with 2 clusters
     * @param confusionMatrix
     * @return
     */
    private double[] generatePrecisionScores(int[][] confusionMatrix) {
        double[] precisionScores = new double[2];
        precisionScores[0] = (double) confusionMatrix[0][0] / (confusionMatrix[0][0] + confusionMatrix[1][0]);
        precisionScores[1] = (double) confusionMatrix[0][1] / (confusionMatrix[0][1] + confusionMatrix[1][1]);
        return precisionScores;
    }

    /**
     * Generates the recall scores of a 2x2 confusion matrix with 2 clusters
     * @param confusionMatrix
     * @return
     */
    private double[] generateRecallScores(int[][] confusionMatrix) {
        double[] recallScores = new double[2];
        recallScores[0] = (double) confusionMatrix[0][0] / (confusionMatrix[0][0] + confusionMatrix[0][1]);
        recallScores[1] = (double) confusionMatrix[1][0] / (confusionMatrix[1][0] + confusionMatrix[1][1]);
        return recallScores;
    }

    /**
     * More or less hard-coded for a 2x2 confusion matrix with 2 clusters
     * @param prediction
     * @param actual
     * @param numberOfClusters
     * @return
     */
    private int[][] generateConfusionMatrix(int[] prediction, List<Integer> actual, int numberOfClusters) {
        int[][] matrix = new int[numberOfClusters][];
        for (int i = 0; i < numberOfClusters; i++) {
            matrix[i] = new int[numberOfClusters];
        }
        for (int i = 0; i < prediction.length; i++) {
            if (prediction[i] == 0 && actual.get(i) == 0) {
                matrix[0][0] += 1;
            } else if (prediction[i] == 1 && actual.get(i) == 0) {
                matrix[0][1] += 1;
            } else if (prediction[i] == 0 && actual.get(i) == 1) {
                matrix[1][0] += 1;
            } else if (prediction[i] == 1 && actual.get(i) == 1) {
                matrix[1][1] += 1;
            }
        }
        return matrix;
    }

    /**
     * Perform KNN Classification across the TESS Input Catalog
     * to predict which stars harbor Earth-like planets
     */
    public void classifyAndExamineSolarParameters() {
        // This removes all planets that do not have data for every stellar parameter. This ensures
        // that when the new stars are classified, they are being compared to the highest quality data.
        List<Double[]> stellarParametersOfEarthLikePlanets = generateListOfPlanetsWithAllStellarParameters(earthLikeClusterId);
        List<Double[]> stellarParametersOfJupiterLikePlanets = generateListOfPlanetsWithAllStellarParameters(jupiterLikeClusterId);

        int[] classifiedLabels = new KNN().classify(3, combinedStellarParameterMatrix, npcStellarParameterList, labelsOfPlanetsWithAllStellarProperties);
        parametersOfStarsPredictedToHaveEarthLikePlanets = new ArrayList<>();
        for (int i = 0; i < classifiedLabels.length; i++) {
            if (classifiedLabels[i] == earthLikeClusterId) {
                parametersOfStarsPredictedToHaveEarthLikePlanets.add(combinedStellarParameterMatrix.get(i));
            }
        }
        System.out.println("Number of stars examined: " + combinedStellarParameterMatrix.size());
        System.out.println("Number of star predicted to have Earth-like planets: " + parametersOfStarsPredictedToHaveEarthLikePlanets.size());
        System.out.println("\nProperties of stars predicted to have Earth-like planets.");
        double[] averages = new double[7];
        for (Double[] star : parametersOfStarsPredictedToHaveEarthLikePlanets) {
            for (int i = 0; i < star.length; i++) {
                averages[i] += star[i];
            }
        }
        for (int i = 0; i < averages.length; i++) {
            averages[i] = averages[i] / parametersOfStarsPredictedToHaveEarthLikePlanets.size();
        }

        printResults(averages);

    }

    private void printResults(double[] averages) {
        System.out.printf("Average distance:    %10.2f light years\n", averages[0]);
        System.out.printf("Average temperature: %10.2f degrees Kelvin\n", averages[1]);
        System.out.printf("Average mass:        %10.2f solar masses\n", averages[2]);
        System.out.printf("Average radius:      %10.2f solar radii\n", averages[3]);
        System.out.printf("Average gravity:     %10.2f log(Solar)\n", averages[4]);
        System.out.printf("Average luminosity:  %10.2f log(Solar)\n", averages[5]);
        System.out.printf("Average metallicity: %10.2f [dex]\n", averages[6]);
    }

    /**
     * <p>Calculate the average of each column of the matrix.</p>
     * <p>The calculations ignore values of Double.MIN_VALUE</p>
     * <p>In addition, print the standard deviations, min values, and max values</p>
     * @param matrix
     * @return an array of the average values for each parameter
     */
    private double[] calculateAverages(Double[][] matrix) {
        double[] avgs = new double[matrix[0].length];
        double[] sums = new double[matrix[0].length];
        double[] maxs = newMinDoubleArray(matrix[0].length);
        double[] mins = newMaxDoubleArray(matrix[0].length);
        int[] cts = new int[matrix[0].length];

        for (Double[] row : matrix) {
            for (int i = 0; i < row.length; i++) {
                if (row[i] != Double.MIN_VALUE) {
                    sums[i] += row[i];
                    if (maxs[i] < row[i]) maxs[i] = row[i];
                    if (mins[i] > row[i]) mins[i] = row[i];
                    cts[i]++;
                }
            }
        }

        for (int i = 0; i < sums.length; i++) {
            avgs[i] = sums[i] / cts[i];
        }

        double[][] squaredDifferences = new double[matrix.length][];

        for (int i = 0; i < matrix.length; i++) {
            squaredDifferences[i] = new double[matrix[i].length];
            for (int j = 0; j < matrix[i].length; j++) {
                if (matrix[i][j] != Double.MIN_VALUE) {
                    squaredDifferences[i][j] = (matrix[i][j] - avgs[j]) * (matrix[i][j] - avgs[j]);
                } else {
                    squaredDifferences[i][j] = Double.MIN_VALUE;
                }
            }
        }

        double[] squaredDifferenceMeans = new double[squaredDifferences[0].length];
        for (double[] row : squaredDifferences) {
            for (int i = 0; i < row.length; i++) {
                if (row[i] != Double.MIN_VALUE) {
                    squaredDifferenceMeans[i] += row[i];
                }
            }
        }
        double[] stdevs = new double[squaredDifferenceMeans.length];
        for (int i = 0; i < sums.length; i++) {
            squaredDifferenceMeans[i] = squaredDifferenceMeans[i] / cts[i];
            stdevs[i] = Math.sqrt(squaredDifferenceMeans[i]);
        }

//        System.out.println("Totals: " + Arrays.toString(sums));
//        System.out.println("Counts: " + Arrays.toString(cts));
        System.out.println("Means: " + Arrays.toString(avgs));
        System.out.println("Stdevs: " + Arrays.toString(stdevs));
        System.out.println("Max Values: " + Arrays.toString(maxs));
        System.out.println("Min Values: " + Arrays.toString(mins));
        return avgs;
    }

    private double[] newMinDoubleArray(int size) {
        double[] x = new double[size];
        for (int i = 0; i < size; i++) {
            x[i] = Double.MIN_VALUE;
        }
        return x;    }

    private double[] newMaxDoubleArray(int size) {
        double[] x = new double[size];
        for (int i = 0; i < size; i++) {
            x[i] = Double.MAX_VALUE;
        }
        return x;
    }

    private List<Double[]> generateListOfPlanetsWithAllStellarParameters() {
        labelsWithStellarPropertiesSubset = new ArrayList<>();
        planetsSubsetTraining = new ArrayList<>();
        planetsSubsetTesting = new ArrayList<>();
        labelsOfRemainingTenPercentForComparison = new ArrayList<>();
        List<Double[]> planets = new ArrayList<>();
        labelsOfPlanetsWithAllStellarProperties = new ArrayList<>();
        for (int i = 0; i < npcStellarParameterMatrix.length; i++) {
            if (allStellarParametersExist(npcStellarParameterMatrix[i])) {
                labelsOfPlanetsWithAllStellarProperties.add(planetLabelArray[i]);
                planets.add(npcStellarParameterMatrix[i]);

                // for testing
                if (i < 363) {
                    labelsWithStellarPropertiesSubset.add(planetLabelArray[i]);
                    planetsSubsetTraining.add(npcStellarParameterMatrix[i]);
                } else {
                    labelsOfRemainingTenPercentForComparison.add(planetLabelArray[i]);
                    planetsSubsetTesting.add(npcStellarParameterMatrix[i]);

                }
            }
        }
        return planets;
    }

    private List<Double[]> generateListOfPlanetsWithAllStellarParameters(int id) {
        List<Double[]> planets = new ArrayList<Double[]>();
        for (int i = 0; i < planetLabelArray.length; i++) {
            if (planetLabelArray[i] == id && allStellarParametersExist(npcStellarParameterMatrix[i])) {
                planets.add(npcStellarParameterMatrix[i]);
            }
        }
        return planets;
    }

    private Double[][] generatePlanetArraySubsetByCluster(int id) {
        int length = (id == earthLikeClusterId) ? 244 : 371;
        Double[][] planets = new Double[length][];
        int index = 0;
        for (int i = 0; i < planetLabelArray.length; i++) {
            if (planetLabelArray[i] == id) {
                planets[index++] = npcStellarParameterMatrix[i];
            }
        }
        return planets;
    }

    private boolean allStellarParametersExist(Double[] planet) {
        boolean allExist = true;
        for (Double parameter : planet) {
            if (parameter == Double.MIN_VALUE) {
                allExist = false;
            }
        }
        return allExist;
    }

    private void splitIntoTwoClustersAndGraph(Double[][] m, int[] labels) {
        List<Double[]> m1 = new ArrayList<>();
        List<Double[]> m2 = new ArrayList<>();
        for (int i = 0; i < labels.length; i++) {
            if (labels[i] == 0) {
                m1.add(m[i]);
            } else if (labels[i] == 1) {
                m2.add(m[i]);
            }
        }
        new twoDimensionalScatterPlot().twoListDataSources(m1, m2, "Mass (Jupiter)", "Radius (Jupiter)");
    }

    private void splitIntoThreeClustersAndGraph(Double[][] m, int[] labels) {
        List<Double[]> m1 = new ArrayList<>();
        List<Double[]> m2 = new ArrayList<>();
        List<Double[]> m3 = new ArrayList<>();
        for (int i = 0; i < labels.length; i++) {
            if (labels[i] == 0) {
                m1.add(m[i]);
            } else if (labels[i] == 1) {
                m2.add(m[i]);
            } else if (labels[i] == 2) {
                m3.add(m[i]);
            }
        }
        new twoDimensionalScatterPlot().threeListDataSources(m1, m2, m3);
    }

    private void splitIntoFourClustersAndGraph(Double[][] m, int[] labels) {
        List<Double[]> m1 = new ArrayList<>();
        List<Double[]> m2 = new ArrayList<>();
        List<Double[]> m3 = new ArrayList<>();
        List<Double[]> m4 = new ArrayList<>();
        for (int i = 0; i < labels.length; i++) {
            if (labels[i] == 0) {
                m1.add(m[i]);
            } else if (labels[i] == 1) {
                m2.add(m[i]);
            } else if (labels[i] == 2) {
                m3.add(m[i]);
            } else if (labels[i] == 3) {
                m4.add(m[i]);
            }
        }
        new twoDimensionalScatterPlot().fourListDataSources(m1, m2, m3, m4);
    }

    private void splitIntoFiveClustersAndGraph(Double[][] m, int[] labels) {
        List<Double[]> m1 = new ArrayList<>();
        List<Double[]> m2 = new ArrayList<>();
        List<Double[]> m3 = new ArrayList<>();
        List<Double[]> m4 = new ArrayList<>();
        List<Double[]> m5 = new ArrayList<>();
        for (int i = 0; i < labels.length; i++) {
            if (labels[i] == 0) {
                m1.add(m[i]);
            } else if (labels[i] == 1) {
                m2.add(m[i]);
            } else if (labels[i] == 2) {
                m3.add(m[i]);
            } else if (labels[i] == 3) {
                m4.add(m[i]);
            } else if (labels[i] == 4) {
                m5.add(m[i]);
            }
        }
        new twoDimensionalScatterPlot().fiveListDataSources(m1, m2, m3, m4, m5);
    }

    public Double[][] getStandardizedMassRadiusData() {
        return npcStandardizedMassRadiusMatrix;
    }

    public Double[][] getMassRadiusData() {
        return npcMassRadiusMatrix;
    }

    private int numDiscoveredByKepler(List<String[]> pl) {
        int discoveryTelescope = 62;
        int c = 0;
        for (String[] planet : pl) {
            if (planet[discoveryTelescope].contains("Kepler")) c++;
        }
        return c;
    }

    private void printPlanetNames(List<String[]> pl) {
        int discoveryTelescope = 62;
        for (String[] planet : pl) {
            System.out.println(planet[44] + " || " + planet[47] + " || " + planet[3] + " || " + planet[discoveryTelescope]);
        }
    }

    /**
     * Reads in a tab separated file into a List of String arrays
     * @param file a tab separated file
     * @return a List of String[]
     */
    private List<String[]> readTsvToListOfArrays(File file) {
        List<String[]> l = new ArrayList<>();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                l.add(scanner.nextLine().split("\t"));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return l;
    }

    /**
     * Reads in a tab separated file into a Map
     * <p>
     * The format of the file should be:
     * <p>
     * "Description of column" [tab] "column number"
     * @param file a tab separated file
     * @return a Map of (key: Integer, value: String)
     */
    private Map<Integer, String> readTsvToIntStringMap(File file) {
        Map<Integer, String> m = new HashMap<Integer, String>();
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String[] line = scanner.nextLine().split("\t");
                m.put(Integer.parseInt(line[1]), line[0]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return m;
    }

    /**
     * Standardize a 2 dimensional matrix of doubles using the formula
     * <p> (standardized feature) = ((feature) - (mean)) / (standard deviation)
     * This only works on a matrix with two columns, in other words it must be
     * a matrix that adheres to the following structure:
     * <p><code> { {2,3}, {4,5}, {6,7} } </code>
     * <p> It might be worth generalizing this method to work on 2 dimensional
     * matrices of arbitrary size
     * <p> In addition, it should be possible to standardize new values to a
     * previous set of data. Look at the second-to-last for-loop, if the mean
     * and stdev of the old data is known, new data can be fed in and standardized
     * to the same level.</p>
     * @param m 2D matrix of Doubles
     * @return the standardized matrix
     */
    private Double[][] standardizeMatrix(Double[][] m) {
        Double mean0 = 0.0;
        Double mean1 = 0.0;
        for (Double[] point : m) {
            mean0 += point[0];
            mean1 += point[1];
        }
        mean0 = mean0 / m.length;
        mean1 = mean1 / m.length;

        Double[] diffSquared0 = new Double[m.length];
        Double[] diffSquared1 = new Double[m.length];
        for (int i = 0; i < m.length; i++) {
            diffSquared0[i] = (m[i][0]-mean0) * (m[i][0]-mean0);
            diffSquared1[i] = (m[i][1]-mean1) * (m[i][1]-mean1);
        }

        Double diffSquaredMean0 = 0.0;
        Double diffSquaredMean1 = 0.0;
        for (int i = 0; i < diffSquared0.length; i++) {
            diffSquaredMean0 += diffSquared0[i];
            diffSquaredMean1 += diffSquared1[i];
        }
        diffSquaredMean0 = diffSquaredMean0 / diffSquared0.length;
        diffSquaredMean1 = diffSquaredMean1 / diffSquared1.length;
        Double stdev0 = Math.sqrt(diffSquaredMean0);
        Double stdev1 = Math.sqrt(diffSquaredMean1);

        Double[] standardized0 = new Double[m.length];
        Double[] standardized1 = new Double[m.length];
        for (int i = 0; i < m.length; i++) {
            standardized0[i] = (m[i][0] - mean0) / stdev0;
            standardized1[i] = (m[i][1] - mean1) / stdev1;
        }
        Double[][] st = new Double[standardized0.length][];
        for (int i = 0; i < st.length; i++) {
            st[i] = new Double[2];
            st[i][0] = standardized0[i];
            st[i][1] = standardized1[i];
        }
        return st;
    }

    private Double[][] generateMassRadiusMatrix(List<String[]> l) {
        Double[][] matrix = new Double[l.size()][];
        int i = 0;
        for (String[] planet : l) {
            Double[] s = {Double.parseDouble(planet[10]), Double.parseDouble(planet[12])};
            matrix[i++] = s;
        }
        return matrix;
    }

    /**
     * Reads in a list of planets as strings and pulls out the stellar parameters of interest.
     * <p> These are the indices of the parameters of interest from the list:</p>
     * <code><ul>
     *     <li>22 - st_dist: Distance [pc]</li>
     *     <li>26 - st_teff: Effective Temperature [K]</li>
     *     <li>27 - st_mass: Stellar Mass [Solar mass]</li>
     *     <li>28 - st_rad: Stellar Radius [Solar radii]</li>
     *     <li>90 - st_logg: Stellar Surface Gravity</li>
     *     <li>91 - st_lum: Stellar Luminosity</li>
     *     <li>93 - st_metfe: Stellar Metallicity [dex]</li>
     * </ul></code>
     * <p>Blank values are replaced with Double.MIN_VALUE</p>
     * @param l
     * @return
     */
    private Double[][] generateNpcStellarParameterMatrix(List<String[]> l) {
        int i = 0;
        Double[][] matrix = new Double[l.size()][];

        for (String[] planet : l) {
            matrix[i] = new Double[7];
            matrix[i][0] = (!planet[22].equals("")) ? Double.parseDouble(planet[22]) : Double.MIN_VALUE;
            matrix[i][1] = (!planet[26].equals("")) ? Double.parseDouble(planet[26]) : Double.MIN_VALUE;
            matrix[i][2] = (!planet[27].equals("")) ? Double.parseDouble(planet[27]) : Double.MIN_VALUE;
            matrix[i][3] = (!planet[28].equals("")) ? Double.parseDouble(planet[28]) : Double.MIN_VALUE;
            matrix[i][4] = (!planet[90].equals("")) ? Double.parseDouble(planet[90]) : Double.MIN_VALUE;
            matrix[i][5] = (!planet[91].equals("")) ? Double.parseDouble(planet[91]) : Double.MIN_VALUE;
            matrix[i][6] = (!planet[93].equals("")) ? Double.parseDouble(planet[93]) : Double.MIN_VALUE;
            i++;
        }
        return matrix;
    }


    public void filterAndCombineStarData() {
        List<File> tics = new ArrayList<>();
        tics.add(new File("data/tic/90S_88S.csv"));
        tics.add(new File("data/tic/88S_86S.csv"));
        tics.add(new File("data/tic/86S_84S.csv"));
        tics.add(new File("data/tic/84S_82S.csv"));
        tics.add(new File("data/tic/82S_80S.csv"));
        tics.add(new File("data/tic/80S_78S.csv"));
        tics.add(new File("data/tic/78S_76S.csv"));
        tics.add(new File("data/tic/76S_74S.csv"));
        tics.add(new File("data/tic/74S_72S.csv"));
        tics.add(new File("data/tic/72S_70S.csv"));
        for (File file : tics) {
            filterStarData(file);
            System.out.println("Finished reading, cleaning, and writing file: " + file.toString());
        }
    }

    /**
     * <p> These are the indices of the parameters of interest from the list:</p>
     * <code><ul>
     *     <li>64 - Teff: Effective Temperature (K)</li>
     *     <li>66 - logg: log of the Surface Gravity (cgs)</li>
     *     <li>68 - M/H: Metallicity (dex)</li>
     *     <li>70 - Rad: Radius (solar)</li>
     *     <li>72 - Mass: Mass (solar)</li>
     *     <li>77 - Lum: Stellar Luminosity (solar)</li>
     *     <li>79 - d: Distance (pc)</li>
     * </ul></code>
     * @param file
     */
    private void filterStarData(File file) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("data/combined_tic.csv", true))) {
            try (Scanner scanner = new Scanner(file)) {
                int numStars = 0;
                while (scanner.hasNextLine()) {
                    numStars++;
                    String[] line = scanner.nextLine().split(",");
                    Double[] star = new Double[7];
                    if (!line[64].equals("") && !line[66].equals("") && !line[68].equals("") && !line[70].equals("") &&
                            !line[72].equals("") && !line[77].equals("") && !line[79].equals("")) {
                        star[0] = Double.parseDouble(line[79]);
                        star[1] = Double.parseDouble(line[64]);
                        star[2] = Double.parseDouble(line[72]);
                        star[3] = Double.parseDouble(line[70]);
                        star[4] = Double.parseDouble(line[66]);
                        star[5] = Double.parseDouble(line[77]);
                        star[6] = Double.parseDouble(line[68]);
                        bw.write(star[0] + "," +
                                star[1] + "," +
                                star[2] + "," +
                                star[3] + "," +
                                star[4] + "," +
                                star[5] + "," +
                                star[6] + "\n");
                    }
                }
                System.out.println("Num stars in file: " + numStars);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param file
     * @return
     */
    private List<Double[]> generateTicStellarParameterMatrix(File file) {
        List<Double[]> stars = new ArrayList<>();
        int numberOfStars = 0;
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                numberOfStars++;
                String[] line = scanner.nextLine().split(",");
                Double[] star = new Double[7];
                star[0] = Double.parseDouble(line[0]);
                star[1] = Double.parseDouble(line[1]);
                star[2] = Double.parseDouble(line[2]);
                star[3] = Double.parseDouble(line[3]);
                star[4] = Double.parseDouble(line[4]);
                star[5] = Double.parseDouble(line[5]);
                star[6] = Double.parseDouble(line[6]);
                stars.add(star);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Stars in the reduced file: " + numberOfStars);
        return stars;
    }

    /**
     * Creates a new list of planets that have non-empty values for
     * mass(10), radius(12)
     * @param l the old list
     * @return the new list
     */
    private List<String[]> filterPlanetsWithoutMassAndRadius(List<String[]> l) {
        List<String[]> newList = new ArrayList<>();
        for (String[] planet : l) {
            if (!planet[10].equals("") &&                   // mass (in units of Jupiter mass)
                    !planet[12].equals("") &&               // radius (in units of Jupiter radius)
                    !planet[6].equals(""))                  // orbital period (in units of days)
            {
                newList.add(planet);
            }
        }
        return newList;
    }

    /**
     *
     * @param l a list of String arrays
     * @param m a map <\Integer, String>
     */
    private void printColumnData(List<String[]> l, Map<Integer, String> m) {
        int[] sums = numPerColumn(l);
        double total = sums[0];
        for (int i = 1; i < sums.length; i++) {
            String definition = m.get(i);
            double rate = sums[i] / total;
            if (rate > 0.0) {
                System.out.printf("%3d: %4d(%.2f)  (%s)\n", i, sums[i], rate, definition);
            } else {
//                System.out.printf("%3d: %4d(%.2f)*  (%s)\n", i, sums[i], rate, definition);
            }
        }
    }

    /**
     * Counts up the number of non empty rows in each column
     * @param l
     * @return
     */
    private int[] numPerColumn(List<String[]> l) {
        int cols = l.get(0).length;
        int[] sums = new int[cols];
        for (String[] row : l) {
            for (int param = 0; param < row.length; param++) {
                if (!row[param].equals("")) {
                    sums[param]++;
                }
            }
        }
        return sums;
    }








    private void printStandarizedMRMatrix() {
        for (Double[] p : npcStandardizedMassRadiusMatrix) {
            System.out.printf("%10.3f %10.3f\n", p[0], p[1]);
        }
    }

    /**
     * This runs KMeans on the planet data until an acceptable cluster is found.
     * <p>This is necessary because KMeans will occasionally choose initial centroids that result in poor
     * performance of the algorithm.
     * <p> Repeated testing of this data has found that two clusters of size 244 and 371 are found in the vast majority of cases,
     * with occasional runs finding a cluster that contains nearly all of the data and another that is made up of outliers.
     * <p> This step prevents that second outcome from occurring.
     * @param data
     * @return
     */
    private KMeans runKMeans(Double[][] data) {
        int validClusterSize = 244;
        KMeans km = new KMeans(data);

        boolean correctClusters = false;
        while (!correctClusters) {
            km.cluster(2, 1000);
            int[] clusterCount = km.getClusterCount();
            correctClusters = (clusterCount[0] == validClusterSize || clusterCount[1] == validClusterSize);
        }
//        km.printResults();
        return km;
    }

    private void tryMultipleDbscanParameters(Double[][] data) {
        System.out.println("DBSCAN");
        for (int clusterSize = 2; clusterSize <= 6; clusterSize++) {
            for (double minDistance = 0.1; minDistance < 1; minDistance += .1) {
                dbscanWith(data, clusterSize, minDistance);
            }
        }
    }

    private void dbscanWith(Double[][] data, int clusterSize, double minDistance) {
        Dbscan db = new Dbscan(data, clusterSize, minDistance);
        List<List<Double[]>> clusters = db.cluster();
        System.out.printf("\n\nClustering with minClusterSize = %d and eps = %.2f", clusterSize, minDistance);
        db.printClusterAnalysis(clusters);
//        new twoDimensionalScatterPlot().fiveListDataSources(clusters.get(0), clusters.get(1),
//                clusters.get(2), clusters.get(3), clusters.get(4));
    }


    public void setEarthLikeClusterId(int i) {
        earthLikeClusterId = i;
    }

    public void setJupiterLikeClusterId(int i) {
        jupiterLikeClusterId = i;
    }

}
