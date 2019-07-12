package algorithm.models;

import com.apporiented.algorithm.clustering.Cluster;
import com.apporiented.algorithm.clustering.ClusteringAlgorithm;
import com.apporiented.algorithm.clustering.DefaultClusteringAlgorithm;
import com.apporiented.algorithm.clustering.SingleLinkageStrategy;
import olap.CellSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Descriptive clustering is an parameter free clustering algorithm for the intentional analytics model. It's based on
 * hierarchical clustering and knee curve detection to detect the optimal number of cluster
 */
public class DescriptiveClustering extends AbstractModel {

    private ClusteringAlgorithm algo = new DefaultClusteringAlgorithm();

    @Override
    public void fit(CellSet cellSet) {

        this.modelComponentList = new ArrayList<>();

        for (Double[][] dataset : cellSet.getDatasetList()) {
            // Flatten dataset
            List<Double> dataListTemp = new ArrayList<>();
            for (Double[] row : dataset) {
                dataListTemp.addAll(Arrays.asList(row));
            }
            Double[] data = dataListTemp.toArray(new Double[0]);

            // Clean dataset from null
            List<Double> cleanedData = new ArrayList<>();
            List<String> ordinalList = new ArrayList<>();
            for (int i = 0; i < data.length; i++) {
                if (data[i] != null) {
                    cleanedData.add(data[i]);
                    ordinalList.add(String.valueOf(i));
                }
            }
            String[] names = ordinalList.toArray(new String[0]);

            // Build distance matrix
            double[][] distanceMatrix = new double[cleanedData.size()][cleanedData.size()];
            for (int i = 0; i < cleanedData.size(); i++) {
                for (int j = 0; j < cleanedData.size(); j++) {
                    double dist = Math.abs(cleanedData.get(i) - cleanedData.get(j));
                    distanceMatrix[i][j] = dist;
                }
            }

            // Perform Hierarchical clustering
            Cluster root = algo.performClustering(distanceMatrix, names, new SingleLinkageStrategy());
            List<Double> linkageDistance = new ArrayList<>();
            List<Cluster> clusterList = new ArrayList<>();
            clusterList.add(root);
            while (!clusterList.isEmpty()) {
                Cluster c = clusterList.remove(0);
                if (!c.isLeaf()) {
                    linkageDistance.add(c.getDistanceValue());
                    clusterList.addAll(c.getChildren());
                }
            }
            Collections.sort(linkageDistance);
            double threshold = findThreshold(linkageDistance);

            clusterList = algo.performFlatClustering(distanceMatrix, names, new SingleLinkageStrategy(), threshold);

            // Get models components
            List<Boolean[][]> datasetComponentList = new ArrayList<>();
            for (Cluster cluster : clusterList) {
                ordinalList = getAllLeaves(cluster);
                Boolean[][] modelComponent = new Boolean[dataset.length][dataset[0].length]; // FIXME dataset[0].length may produce NullPointer
                for (int i = 0; i < data.length; i++) {
                    if (data[i] != null) {
                        modelComponent[i / dataset[0].length][i % dataset[0].length] = ordinalList.contains(String.valueOf(i)); // FIXME dataset[0].length may produce NullPointer
                    }
                }
                datasetComponentList.add(modelComponent);
            }
            modelComponentList.add(datasetComponentList);
        }

    }

    private List<String> getAllLeaves(Cluster cluster) {
        List<String> list = new ArrayList<>();
        if (cluster.isLeaf()) {
            list.add(cluster.getName());
            return list;
        } else {
            for (Cluster child : cluster.getChildren()) {
                list.addAll(getAllLeaves(child));
            }
            return list;
        }
    }

    private double findThreshold(List<Double> d) {
        if (d.isEmpty()) return 0;
        if (d.size() < 3) return d.get(0);
        List<Double> angleList = new ArrayList<>();
        for (int i = 1; i < d.size() - 1; i++) {
            double angle = Math.PI - Math.atan(d.get(i + 1) - d.get(i)) + Math.atan(d.get(i) - d.get(i - 1));
            angleList.add(angle);
        }
        double angleMin = Collections.min(angleList);
        return d.get(angleList.indexOf(angleMin) + 1);
    }

    @Override
    public List<Integer[][]> getClusters(){
        List<Integer[][]> res = new ArrayList<>();
        if (this.modelComponentList != null){
            for (List<Boolean[][]> modelComponentList: this.modelComponentList){
                if (!modelComponentList.isEmpty()){
                    Integer[][] integers = new Integer[modelComponentList.get(0).length][modelComponentList.get(0)[0].length];
                    for (int i = 0; i < modelComponentList.size(); i++) {
                        for (int row = 0; row < modelComponentList.get(i).length; row++) {
                            for (int col = 0; col < modelComponentList.get(i)[row].length; col++) {
                                Boolean val = modelComponentList.get(i)[row][col];
                                if (val != null && val){
                                    integers[row][col] = i;
                                }
                            }
                        }
                    }
                    res.add(integers);
                }
            }
            return res;
        }
        return null; //FIXME: find something else
    }
}
