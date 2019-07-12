package algorithm.models;

import olap.CellSet;

import java.util.List;

/**
 * Model for cell selection
 */
public abstract class AbstractModel {

    /**
     * A list of the models components. A component is a boolean membership matrix, one for each label the models has predict
     *
     * @see #fit(CellSet)
     */
    protected List<List<Boolean[][]>> modelComponentList;

    /**
     * <p>Fit the models to the given <code>cellSet</code></p>
     * <p>When implementing your own models, be sure to store the models prediction into the <code>modelComponentList</code>
     * field. Each component of the list act as a boolean membership matrix. For example, if your models predict 5 target
     * (like a K-Mean with k = 5) you should store 5 membership matrix, one for each label</p>
     *
     * @param cellSet cellSet to fit your models on
     * @see #modelComponentList
     */
    public abstract void fit(CellSet cellSet);

    /**
     * Fit the models to the given <code>cellSet</code> and return a list of the different models component built
     *
     * @param cellSet cellSet to fit your models on
     * @return list of models component
     */
    public List<List<Boolean[][]>> fitAndPredict(CellSet cellSet) {
        fit(cellSet);
        return this.modelComponentList;
    }

    public List<List<Boolean[][]>> getModelComponentList() {
        return modelComponentList;
    }

    public abstract List<Integer[][]> getClusters();
}
