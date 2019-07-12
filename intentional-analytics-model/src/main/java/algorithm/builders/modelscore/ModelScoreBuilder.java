package algorithm.builders.modelscore;

import java.util.List;

/**
 * The model score function. Implement this interface to describe how {@link algorithm.AlgorithmOne AlgorithmOne}
 * should compute model score.
 *
 * @see algorithm.AlgorithmOne AlgorithmOne
 */
public interface ModelScoreBuilder {

    /**
     * Compute the model score
     *
     * @param modelComponentScoreList List of model component score. Model component score are coùmputed with
     *                                {@link algorithm.builders.componentscore.ComponentScoreBuilder#computeScore(Boolean[][], Double[][])
     *                                ComponentScoreBuilder.compute(&hellip)}
     * @return the model score
     */
    public double computeScore(List<Double> modelComponentScoreList);
}
