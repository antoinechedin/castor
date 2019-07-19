package scripts;

import algorithm.builders.componentscore.AverageComponentScoreBuilder;
import algorithm.builders.componentscore.ComponentScoreBuilder;
import algorithm.builders.proxies.AncestorOrDescendantProxyBuilder;
import algorithm.builders.proxies.ProxyBuilder;
import algorithm.builders.significance.SignificanceBuilder;
import algorithm.builders.significance.ZScoreSignificanceBuilder;
import algorithm.builders.surprise.DifferenceSurpriseBuilder;
import algorithm.builders.surprise.SurpriseBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import algorithm.AlgorithmOne;
import algorithm.models.AbstractModel;
import algorithm.models.DescriptiveClustering;
import olap.CellSet;
import org.olap4j.Axis;
import org.olap4j.OlapConnection;
import org.olap4j.OlapStatement;
import castor.response.CastorJsonResponse;
import castor.response.CastorTable;
import castor.session.QueryRequest;
import castor.session.Session;
import utils.MatrixUtil;

import java.io.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Use this script to build a <i>CASTOR</i> json input file (Local demo button). Edit {@link BuildDemoJsonScript#JSON_LOG JSON_LOG}
 * and {@link BuildDemoJsonScript#OUTPUT_FILE OUTPUT_FILE} variables to choose input and output of the script.
 */
public class BuildDemoJsonScript {

    private static final String DRIVER_CLASS = "mondrian.olap4j.MondrianOlap4jDriver";
    private static final String PROVIDER = "mondrian";
    private static final String JDBC_URL = "jdbc:sqlserver://10.195.25.10\\db_dopan:54437;databaseName=db_dopan;user=dopan;password=diblois";
    private static final String CATALOG = "ressources/cubeSchemas/DOPAN_DW3-agg.xml";
    private static final String USER = "patrick";
    private static final String PASSWORD = "oopi7taing7shahD";

    /**
     * Session log file to execute
     */
    private static final String JSON_LOG = "ressources/logs/json/dibstudent03_2016-09-25_16-20.json";

    /**
     * Output path
     */
    private static final String OUTPUT_FILE = "ressources/demo/session_demo.json";

    public static void main(String[] args) {
        OlapConnection olapConnection = null;
        try {
            Class.forName(DRIVER_CLASS);
            Connection connection = DriverManager.getConnection(
                    "jdbc:" + PROVIDER + ":" +
                            "Jdbc=" + JDBC_URL + ";" +
                            "Catalog=" + CATALOG + ";" +
                            "JdbcUser=" + USER + ";" +
                            "JdbcPassword=" + PASSWORD + ";"
            );
            olapConnection = connection.unwrap(OlapConnection.class);
            OlapStatement statement = olapConnection.createStatement();

            CastorJsonResponse response = null;
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
            Gson gson = new GsonBuilder().setDateFormat(dateFormat.toPattern()).setPrettyPrinting().serializeNulls().create();
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(JSON_LOG));) {
                Session session = gson.fromJson(bufferedReader, Session.class);
                List<CastorTable> castorTableList = new ArrayList<>();

                List<AbstractModel> models = new ArrayList<>();
                models.add(new DescriptiveClustering());
                ProxyBuilder proxyBuilder = new AncestorOrDescendantProxyBuilder();
                SignificanceBuilder significanceBuilder = new ZScoreSignificanceBuilder();
                SurpriseBuilder surpriseBuilder = new DifferenceSurpriseBuilder();
                ComponentScoreBuilder componentScoreBuilder = new AverageComponentScoreBuilder();
                AlgorithmOne algo = new AlgorithmOne(models, proxyBuilder, significanceBuilder, surpriseBuilder, componentScoreBuilder, null);

                CellSet oldCellset = null;
                for (QueryRequest request : session.getQueries()) {
                    CellSet cellSet = new CellSet(statement.executeOlapQuery(request.getQuery()));
                    CastorTable castorTable = new CastorTable(request.getQuery(), cellSet.getData(), cellSet.getHeaderTree(Axis.ROWS.axisOrdinal()), cellSet.getHeaderTree(Axis.COLUMNS.axisOrdinal()), null, null, null);
                    if (oldCellset != null) {
                        List<List<Integer>> highlightedCellPositions = new ArrayList<>();
                        List<Boolean[][]> modelComponentList = algo.compute(oldCellset, cellSet);

                        // Build highlighted cell positions
                        int offset = 0;
                        for (Boolean[][] modelComponent : modelComponentList) {
                            if (cellSet.getMeasureAxisOrdinal() == 0) { // Column
                                for (int row = 0; row < modelComponent.length; row++) {
                                    for (int col = 0; col < modelComponent[row].length; col++) {
                                        if (modelComponent[row][col] != null && modelComponent[row][col]) {
                                            highlightedCellPositions.add(Arrays.asList(row, offset + col));
                                        }
                                    }
                                }
                                offset += modelComponent[0].length; // FIXME: modelComponent[0].length may produce NullPointer
                            } else if (cellSet.getMeasureAxisOrdinal() == 1) { // Row
                                for (int row = 0; row < modelComponent.length; row++) {
                                    for (int col = 0; col < modelComponent[row].length; col++) {
                                        if (modelComponent[row][col] != null && modelComponent[row][col]) {
                                            highlightedCellPositions.add(Arrays.asList(offset + row, col));
                                        }
                                    }
                                }
                                offset += modelComponent.length;
                            } else { // No measure found in axes
                                for (int row = 0; row < modelComponent.length; row++) {
                                    for (int col = 0; col < modelComponent[row].length; col++) {
                                        if (modelComponent[row][col] != null && modelComponent[row][col]) {
                                            highlightedCellPositions.add(Arrays.asList(row, col));
                                        }
                                    }
                                }
                            }
                        }
                        castorTable.setHighlightedCellPositions(highlightedCellPositions);

                        // Build clusters
                        List<Integer[][]> clusterList = algo.getModelList().get(0).getClusters();
                        int[] shape = new int[]{cellSet.getNbOfRows(), cellSet.getNbOfColumns()};
                        Integer[][] clusters;
                        if (cellSet.getMeasureAxisOrdinal() == 0) { // Column
                            clusters = MatrixUtil.hstack(clusterList, shape);
                        } else if (cellSet.getMeasureAxisOrdinal() == 1) { // Row
                            clusters = MatrixUtil.vstack(clusterList, shape);
                        } else { // No measure found in axes
                            clusters = clusterList.get(0);
                        }
                        castorTable.setClusters(clusters);

                        castorTable.setSignificanceScores(algo.getSignificanceScores());
                        castorTable.setSurpriseScores(algo.getSurpriseScore());
                    }
                    castorTableList.add(castorTable);
                    oldCellset = cellSet;
                }
                response = new CastorJsonResponse(castorTableList);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(OUTPUT_FILE))) {
                gson.toJson(response, bufferedWriter);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }
}
