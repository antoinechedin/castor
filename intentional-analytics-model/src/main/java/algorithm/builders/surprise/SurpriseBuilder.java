package algorithm.builders.surprise;

import java.util.List;

/**
 * The surprise score function. Implement this interface to describe how {@link algorithm.AlgorithmOne AlgorithmOne}
 * should surprise score.
 *
 * @see algorithm.AlgorithmOne AlgorithmOne
 */
public interface SurpriseBuilder {

    /**
     * Compute surprise for one cell
     * @param significanceScoreNew the significance score of the cell
     * @param significanceScoreProxyList a list of significance score of all the proxies of the cell
     * @return surprise score
     */
    public Double computeScore(double significanceScoreNew, List<Double> significanceScoreProxyList);
}
