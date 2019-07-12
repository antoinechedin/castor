package algorithm;

import algorithm.builders.componentscore.ComponentScoreBuilder;
import algorithm.builders.modelscore.ModelScoreBuilder;
import algorithm.builders.proxies.ProxyBuilder;
import algorithm.builders.significance.SignificanceBuilder;
import algorithm.builders.surprise.SurpriseBuilder;
import algorithm.models.AbstractModel;
import olap.CellSet;

import java.util.*;

/**
 * Algorithm 1 describe in the paper.
 */
public class AlgorithmOne {

    private List<AbstractModel> modelList;
    private ProxyBuilder proxyBuilder;
    private SignificanceBuilder significanceBuilder;
    private SurpriseBuilder surpriseBuilder;
    private ComponentScoreBuilder componentScoreBuilder;
    private ModelScoreBuilder modelScoreBuilder;

    private Double[][] significanceScores;
    private Double[][] surpriseScore;

    public AlgorithmOne(List<AbstractModel> modelList, ProxyBuilder proxyBuilder, SignificanceBuilder significanceBuilder, SurpriseBuilder surpriseBuilder, ComponentScoreBuilder componentScoreBuilder, ModelScoreBuilder modelScoreBuilder) {
        this.modelList = modelList;
        this.proxyBuilder = proxyBuilder;
        this.significanceBuilder = significanceBuilder;
        this.surpriseBuilder = surpriseBuilder;
        this.componentScoreBuilder = componentScoreBuilder;
        this.modelScoreBuilder = modelScoreBuilder;
    }

    /**
     * Run the algorithm and return the highlighted cells in a boolean matrix. There's one boolean matrix for each measure
     * in newCellSet
     *
     * @param oldCellSet the previous cellSet
     * @param newCellSet the current cellSet
     * @return a list of boolean matrix, one for each measure in newCellSet
     */
    public List<Boolean[][]> compute(CellSet oldCellSet, CellSet newCellSet) {
        Double[][] oldSignificance = significanceBuilder.computeScore(oldCellSet);
        Double[][] newSignificance = significanceBuilder.computeScore(newCellSet);
        significanceScores = newSignificance;
        Map<List<Integer>, Set<List<Integer>>> proxies = proxyBuilder.computeProxyMatrix(newCellSet, oldCellSet);

        // Build surprise
        Double[][] surprise = new Double[newCellSet.getNbOfRows()][newCellSet.getNbOfColumns()];
        for (int m = 0; m < surprise.length; m++) {
            for (int n = 0; n < surprise[m].length; n++) {
                Double significance = newSignificance[m][n];
                if (significance != null) {
                    List<Double> proxySignificance = new ArrayList<>();
                    List<Integer> coordinates = Arrays.asList(n, m);
                    Set<List<Integer>> proxiesSet = proxies.get(coordinates);
                    if (proxiesSet != null) {
                        for (List<Integer> proxyIndexes : proxiesSet) {
                            proxySignificance.add(oldSignificance[proxyIndexes.get(1)][proxyIndexes.get(0)]);
                        }
                        surprise[m][n] = surpriseBuilder.computeScore(significance, proxySignificance);
                    } else {
                        surprise[m][n] = significance;
                    }
                }
            }
        }
        surpriseScore = surprise;

        // Split surprise
        List<Double[][]> surpriseList = new ArrayList<>();
        int offset = 0;
        if (newCellSet.getMeasureAxisOrdinal() == 0) { // Column
            for (Double[][] dataset : newCellSet.getDatasetList()) {
                Double[][] surpriseSubSet = new Double[dataset.length][dataset[0].length]; // FIXME: dataset[0].length may produce NullPointer
                for (int row = 0; row < dataset.length; row++) {
                    System.arraycopy(surprise[row], offset, surpriseSubSet[row], 0, dataset[0].length); // FIXME: dataset[0].length may produce NullPointer
                }
                surpriseList.add(surpriseSubSet);
                offset += dataset[0].length; // FIXME: dataset[0].length may produce NullPointer
            }
        } else if (newCellSet.getMeasureAxisOrdinal() == 1) { // Row
            for (Double[][] dataset : newCellSet.getDatasetList()) {
                Double[][] surpriseSubSet = new Double[dataset.length][dataset[0].length]; // FIXME: dataset[0].length may produce NullPointer
                for (int row = 0; row < dataset.length; row++) {
                    System.arraycopy(surprise[offset + row], 0, surpriseSubSet[row], 0, dataset[0].length); // FIXME: dataset[0].length may produce NullPointer
                }
                surpriseList.add(surpriseSubSet);
                offset += dataset.length;
            }
        } else { // No measure found in axes
            surpriseList.add(surprise);
        }

        // Compute component models score
        // List<Double> modelScoreList = new ArrayList<>();
        List<Boolean[][]> datasetComponentScoreList = new ArrayList<>();
        for (AbstractModel model : modelList) {
            List<List<Boolean[][]>> datasetComponentList = model.fitAndPredict(newCellSet);
            for (int i = 0; i < datasetComponentList.size(); i++) {
                List<Boolean[][]> modelComponentList = datasetComponentList.get(i);
                Double[][] surpriseSubSet = surpriseList.get(i);
                TreeMap<Double, Boolean[][]> modelComponentScoreMap = new TreeMap<>();

                for (Boolean[][] modelComponent : modelComponentList) {
                    modelComponentScoreMap.put(componentScoreBuilder.computeScore(modelComponent, surpriseSubSet), modelComponent);
                }
                // This is line is commented since I only have one model. Thus, I don't need to compare model between them
                // modelScoreList.add(modelScoreBuilder.computeScore(modelComponentScoreList));
                datasetComponentScoreList.add(modelComponentScoreMap.lastEntry().getValue());
            }
        }
        return datasetComponentScoreList;
    }


    public Double[][] getSignificanceScores() {
        return significanceScores;
    }

    public Double[][] getSurpriseScore() {
        return surpriseScore;
    }

    public List<AbstractModel> getModelList() {
        return modelList;
    }
}

