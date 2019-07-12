package algorithm.builders.significance;

import olap.CellSet;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

/**
 * Compute significance score with the Z-score of each cell
 */
public class ZScoreSignificanceBuilder implements SignificanceBuilder {

    @Override
    public Double[][] computeScore(CellSet cellSet) {
        Double[][] significanceScores = new Double[cellSet.getNbOfRows()][cellSet.getNbOfColumns()];

        int measureAxisOrdinal = cellSet.getMeasureAxisOrdinal();
        int offset = 0;
        for (Double[][] dataset : cellSet.getDatasetList()) {
            SummaryStatistics stats = new SummaryStatistics();
            for (Double[] row : dataset) {
                for (Double val : row) {
                    if (val != null) stats.addValue(val);
                }
            }

            if (measureAxisOrdinal == 0) { // Column
                for (int row = 0; row < dataset.length; row++) {
                    for (int col = 0; col < dataset[row].length; col++) {
                        if (dataset[row][col] != null) {
                            double val = Math.abs((dataset[row][col] - stats.getMean()) / stats.getStandardDeviation());
                            if (Double.isNaN(val)) val = 0.;
                            significanceScores[row][offset + col] = val;

                        }
                    }
                }
                offset += dataset[0].length;
            } else if (measureAxisOrdinal == 1) { // Row
                for (int row = 0; row < dataset.length; row++) {
                    for (int col = 0; col < dataset[row].length; col++) {
                        if (dataset[row][col] != null) {
                            double val = Math.abs((dataset[row][col] - stats.getMean()) / stats.getStandardDeviation());
                            if (Double.isNaN(val)) val = 0.;
                            significanceScores[offset + row][col] = val;
                        }
                    }
                }
                offset += dataset.length;
            } else { // No measure in axis
                for (int row = 0; row < dataset.length; row++) {
                    for (int col = 0; col < dataset[row].length; col++) {
                        if (dataset[row][col] != null) {
                            double val = Math.abs((dataset[row][col] - stats.getMean()) / stats.getStandardDeviation());
                            if (Double.isNaN(val)) val = 0.;
                            significanceScores[row][col] = val;

                        }
                    }
                }
            }

        }
        return significanceScores;
    }
}
