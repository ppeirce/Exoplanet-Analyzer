package edu.nyu.cs.pa.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class KMeans {
    private Double[][] dataMatrix;
    private Double[][] centroids;
    private int[] labelArray;
    private int[] clusterCount;
    private int numRows;
    private int numCols;
    private int numClusters;

    public KMeans(Double[][] d) {
        this.dataMatrix = d;
        this.numRows = d.length;
        this.numCols = d[0].length;
    }

    /**
     * Perform k-means clustering with the specified number of clusters
     * using Euclidean distance. Sets the private member variables which
     * track cluster labels. Results of clustering can be accessed by
     * calling one of the public print() methods
     * @param numberOfClusters the "k" in k-means: how many clusters are to
     * be created.
     * @param numberOfIterations Number of iterations. How many times clustering
     * will be performed unless the clusters converge earlier in the process.
     */
    public void cluster(int numberOfClusters, int numberOfIterations) {
        numClusters = numberOfClusters;

        centroids = setCentroids();

        Double[][] newCentroids = centroids;
        int round = 0;
        do {
            centroids = newCentroids;
            labelArray = new int[numRows];
            for (int i = 0; i < numRows; i++) {
                labelArray[i] = closest(dataMatrix[i]);
            }
            newCentroids = updateCentroids();
            round++;
        } while (!((numberOfIterations > 0 && round >= numberOfIterations) || converge(centroids, newCentroids)));

        clusterCount = new int[numClusters];
        for (int i = 0; i < numClusters; i++) {
            for (int j = 0; j < numRows; j++) {
                if (labelArray[j] == i) {
                    clusterCount[i]++;
                }
            }
        }
    }

    /**
     * Recalculate centroids by averaging the members of the cluster.
     * @return the new centroids
     */
    private Double[][] updateCentroids() {
        Double[][] newCentroids = new Double[numClusters][];
        int[] counts = new int[numClusters];

        // gotta initialize the nested Double[] in newCentroids to 0.0
        for (int i = 0; i < numClusters; i++) {
            counts[i] = 0;
            newCentroids[i] = new Double[numCols];
            for (int j = 0; j < numCols; j++) {
                newCentroids[i][j] = 0.0;
            }
        }

        // sum up the values
        for (int i = 0; i < numRows; i++) {
            int clusterId = labelArray[i];
            for (int j = 0; j < numCols; j++) {
                newCentroids[clusterId][j] += dataMatrix[i][j];
            }
            counts[clusterId]++;
        }

        // divide by counts to get averages
        for (int i = 0; i < numClusters; i++) {
            for (int j = 0; j < numCols; j++) {
                newCentroids[i][j] /= counts[i];
            }
        }

        return newCentroids;
    }

    /**
     * Determines the centroid closest to the passed vector
     * @param v the vector being checked
     * @return the label of the centroid closest to v
     */
    private int closest(Double[] v) {
        Double minDistance = dist(v, centroids[0]);
        int label = 0;
        for (int i = 0; i < numClusters; i++) {
            Double t = dist(v, centroids[i]);
            if (minDistance > t) {
                minDistance = t;
                label = i;
            }
        }
        return label;
    }

    /**
     * Checks the equivalence of the two centroids by comparing the
     * distance of their respective parameters. If the distance between
     * each parameter is zero, the method returns true.
     * @param c1 the first centroid
     * @param c2 the second centroid
     * @return a boolean denoting whether the two centroids are equivalent
     */
    private boolean converge(Double[][] c1, Double[][] c2) {
        Double maxDist = Double.MIN_VALUE;
        for (int i = 0; i < numClusters; i++) {
            Double distance = dist(c1[i], c2[i]);
            if (maxDist < distance) {
                maxDist = distance;
            }
        }
        return (maxDist < 0.001);
    }

    /**
     * Calculate the Euclidean distance between the two vectors.
     * @param v1 the first vector
     * @param v2 the second vector
     * @return the distance as a Double
     */
    private Double dist(Double[] v1, Double[] v2) {
        Double sum = 0.0;

        for (int i = 0; i < numCols; i++) {
            Double difference = v1[i] - v2[i];
            sum += difference * difference;
        }
        return Math.sqrt(sum);
    }

    /**
     * Sets the centroids to unique members of the original data set
     * @return a matrix of Doubles representing the centroids
     */
    private Double[][] setCentroids() {
        Double[][] centroids = new Double[numClusters][];
        List<Integer> duplicates = new ArrayList<Integer>();
        for (int i = 0; i < numClusters; i++) {
            int c;
            do {
                c = (int) (Math.random() * numRows);
            } while (duplicates.contains(c));
            duplicates.add(c);
            centroids[i] = dataMatrix[c];
        }
        return centroids;
    }

    public int[] getClusterCount() {
        return clusterCount;
    }

    public int[] getLabelArray() {
        return labelArray;
    }

    public void printCentroids() {
        for (Double[] centroid : centroids) {
            String c = "";
            for (Double d : centroid) {
                c += d + " ";
            }
            System.out.println(c);
        }
    }

    public void printResults() {
        System.out.println("\nResults of KMeans clustering.");
        System.out.println(numClusters + " clusters.");
        System.out.println("Planets per cluster:");
        System.out.println(Arrays.toString(clusterCount));
        System.out.println();
    }

}
