package algorithm.builders.proxies;

import olap.CellSet;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * The proxies function. Implement this interface to describe how {@link algorithm.AlgorithmOne AlgorithmOne}
 * should compute the proxy map.
 *
 * @see algorithm.AlgorithmOne AlgorithmOne
 */
public interface ProxyBuilder {

    /**
     * <p>Compute the proxy map. This allow, given a coordinate in cellSetNew, to find corresponding proxies in
     * cellSetOld</p>
     * <p><b>WARNING:</b> proxy map coordinates are [columnIndex, rowIndex] !</p>
     *
     * @param cellSetNew cellSet of the current request
     * @param cellSetOld cellSet of the previous request
     * @return the proxy map. Keys are coordinates like [columnIndex, rowIndex] from cellSetNew. And values are Set of
     * coordinates like [columnIndex, rowIndex] from cellSetOld
     */
    public Map<List<Integer>, Set<List<Integer>>> computeProxyMatrix(CellSet cellSetNew, CellSet cellSetOld);
}
