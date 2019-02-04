package edu.nyu.cs.pa.algorithms;

import edu.nyu.cs.pa.plotting.twoDimensionalScatterPlot;

import java.util.*;

/**
 * Implementation of the DBSCAN algorithm.
 * Focus: Removing noise.
 *
 * @author ppeirce
 */
public class Dbscan {
    private double eps = 1.0;
    private int minClusterSize = 2;
    private List<Double[]> dataMatrix = null;
    private Set<Double[]> visited = new HashSet<Double[]>();

    /**
     * Create DBSCAN instance.
     * @param inputMatrix The matrix to be clustered
     * @param clusterSize The number of elements required for a cluster to exist
     * @param eps The maximum distance between neighboring points
     */
    // TODO: create setters that check validity of inputs
    public Dbscan(Double[][] inputMatrix, int clusterSize, double eps) {
        setDataMatrix(inputMatrix);
        this.minClusterSize = clusterSize;
        this.eps = eps;
    }

    private void setDataMatrix(Double[][] inputMatrix) {
        List<Double[]> dataMatrix = new ArrayList<>();
        Collections.addAll(dataMatrix, inputMatrix);
        this.dataMatrix = dataMatrix;
    }

    private List<Double[]> getNeighbors(Double[] sourcePoint) {
        List<Double[]> neighbors = new ArrayList<>();
        for (Double[] candidatePoint : dataMatrix) {
            if (distance(sourcePoint, candidatePoint) <= eps) {
                neighbors.add(candidatePoint);
            }
        }
        return neighbors;
    }

    private double distance(Double[] v1, Double[] v2) {
        double sum = 0.0;
        for (int i = 0; i < v1.length; i++) {
            double difference = v1[i] - v2[i];
            sum += difference * difference;
        }
        return Math.sqrt(sum);
    }

    private List<Double[]> merge(List<Double[]> n1, List<Double[]> n2) {
        for (Double[] point: n2) {
            if (!n1.contains(point)) n1.add(point);
        }
        return n1;
    }

    public List<List<Double[]>> cluster() {
        List<List<Double[]>> listOfNeighborGroups = new ArrayList<List<Double[]>>();
        visited.clear();
        List<Double[]> neighbors;
        int[] labelArray = new int[dataMatrix.size()];

        for (Double[] point : dataMatrix) {
            if (!visited.contains(point)) {
                visited.add(point);
                neighbors = getNeighbors(point);
                if (neighbors.size() >= minClusterSize) {
                    for (int j = 0; j < neighbors.size(); j++) {
                        Double[] neighbor = neighbors.get(j);
                        if (!visited.contains(neighbor)) {
                            visited.add(neighbor);
                            List<Double[]> localNeighbors = getNeighbors(neighbor);
                            if (localNeighbors.size() >= minClusterSize) {
                                neighbors = merge(neighbors, localNeighbors);
                            }
                        }
                    }
                    listOfNeighborGroups.add(neighbors);
                }
            }
        }
        return listOfNeighborGroups;
    }

    public void printClusterAnalysis(List<List<Double[]>> clusters) {
//        System.out.println(clusters.size());
        new twoDimensionalScatterPlot().twoListDataSources(clusters.get(0), clusters.get(1), "Mass (Jupiter)", "Radius (Jupiter)");

        int clusterNum = 1;
        for (List<Double[]> cluster : clusters) {
            System.out.printf("\nCluster %d with size %d", clusterNum++, cluster.size());
//            System.out.println("\nCluster with size " + cluster.size());
//            StringBuilder sb = new StringBuilder("");
//            for (Double[] point : cluster) {
//                sb.append(Arrays.toString(point));
//            }
//            System.out.println(sb.toString());
        }
    }

}