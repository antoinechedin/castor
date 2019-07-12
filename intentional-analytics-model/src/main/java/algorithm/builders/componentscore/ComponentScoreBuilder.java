package algorithm.builders.componentscore;

/**
 * The model component score function. Implement this interface to describe how {@link algorithm.AlgorithmOne AlgorithmOne}
 * should compute model component score.
 *
 * @see algorithm.AlgorithmOne AlgorithmOne
 */
public interface ComponentScoreBuilder {

    /**
     * <p>Compute the model component score</p>
     * <p>A model component is a boolean matrix of the same size as the data set (see {@link olap.CellSet CellSet}).
     * Highlighted cells by the model component are indicated with true value in the matrix</p>
     *
     * @param modelComponent The model component to score.
     * @param surpriseScore  The surprise matrix
     * @return component model score
     */
    public double computeScore(Boolean[][] modelComponent, Double[][] surpriseScore);
}
