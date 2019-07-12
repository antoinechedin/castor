package algorithm.builders.significance;

import olap.CellSet;

/**
 * The significance score function. Implement this interface to describe how {@link algorithm.AlgorithmOne AlgorithmOne}
 * should compute significance score.
 *
 * @see algorithm.AlgorithmOne AlgorithmOne
 */
public interface SignificanceBuilder {

    /**
     * Compute significance matrix for a cellSet
     *
     * @param cellSet the given cellSet
     * @return the significance matrix
     */
    public Double[][] computeScore(CellSet cellSet);
}
