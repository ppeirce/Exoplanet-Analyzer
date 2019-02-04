package edu.nyu.cs.pa.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KNN {

    public KNN() {

    }

    /**
     *
     * @param newObservations
     * @param trainingSet
     * @param clusterLabels must be the same size (and with matching indices) as the trainingSet
     * @return
     */
    public int[] classify(int k, List<Double[]> newObservations, List<Double[]> trainingSet, List<Integer> clusterLabels) {
        if (trainingSet.size() != clusterLabels.size()) {
            throw new IllegalArgumentException("Training set and Cluster labels must be the same size");
        }
        int[] classificationLabels = new int[newObservations.size()];

        for (int obs = 0; obs < newObservations.size(); obs++) {
            List<Double> distances = new ArrayList<>();
            Double[] csPoint = newObservations.get(obs);
            for (Double[] tsPoint : trainingSet) {
                distances.add(euclideanDistanceIgnoreFirstTwo(csPoint, tsPoint));
            }

            // <cluster, count>
            Map<Integer, Integer> closestNeighborsByCluster = new HashMap<>();
            for (int i = 1; i <= k; i++){
                int clusterOfIthNearestNeighbor = clusterLabels.get(getIthSmallestValue(i, distances));
                if (closestNeighborsByCluster.containsKey(clusterOfIthNearestNeighbor)) {
                    int oldValue = closestNeighborsByCluster.get(clusterOfIthNearestNeighbor);
                    closestNeighborsByCluster.put(clusterOfIthNearestNeighbor, oldValue + 1);
                } else {
                    closestNeighborsByCluster.put(clusterOfIthNearestNeighbor, 1);
                }
            }
            classificationLabels[obs] = getIndexOfLargestValue(closestNeighborsByCluster);
        }

        return classificationLabels;
    }

    public int getIndexOfLargestValue(Map<Integer, Integer> map) {
        int largest = 0;
        int largestIndex = 0;
        for (Map.Entry<Integer, Integer> e : map.entrySet()) {
            if (e.getValue() > largest) {
                largest = e.getValue();
                largestIndex = e.getKey();
            }
        }
        return largestIndex;
    }

    /**
     * From a List of Doubles, find the INDEX of the i-th smallest number in the list
     * @param i
     * @param l
     * @return
     */
    public int getIthSmallestValue(int i, List<Double> l) {
        double smallest;
        int smallestIndex = 0;
        List<Double> alreadyFound = new ArrayList<>();

        for (int j = 0; j < i; j++) {
            smallest = Double.MAX_VALUE;
            for (int k = 0; k < l.size(); k++) {
                Double d = l.get(k);
                if (d < smallest && !alreadyFound.contains(d)) {
                    smallest = d;
                    smallestIndex = k;
                }
            }
            alreadyFound.add(smallest);
        }
        return smallestIndex;
    }

    /**
     * Calculate the distance between two vectors (Double arrays) of the same size
     * @param a
     * @param b
     * @return the Euclidean distance between these vectors as a double primitive
     */
    private double euclideanDistance(Double[] a, Double[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Arrays must be of the same length to calculate distance");
        }
        double sum = 0.0;

        for (int i = 0; i < a.length; i++) {
            double difference = a[i] - b[i];
            sum += difference * difference;
        }
        return Math.sqrt(sum);
    }

    private double euclideanDistanceIgnoreFirstTwo(Double[] a, Double[] b) {
        double sum = 0.0;
        for (int i = 2; i < a.length; i++) {
            double difference = a[i] - b[i];
            sum += difference * difference;
        }
        return Math.sqrt(sum);
    }
}
