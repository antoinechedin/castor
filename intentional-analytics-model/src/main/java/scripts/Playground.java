package scripts;

import algorithm.builders.proxies.AncestorOrDescendantProxyBuilder;
import algorithm.builders.proxies.ProxyBuilder;
import olap.CellSet;
import org.olap4j.OlapConnection;
import org.olap4j.OlapException;
import org.olap4j.OlapStatement;
import org.olap4j.metadata.NamedList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;

/**
 * Playground script, for testing purpose
 */
public class Playground {

    private static final String DRIVER_CLASS = "mondrian.olap4j.MondrianOlap4jDriver";
    private static final String PROVIDER = "mondrian";
    private static final String JDBC_URL = "jdbc:sqlserver://10.195.25.10\\db_dopan:54437;databaseName=db_dopan;user=dopan;password=diblois";
    private static final String CATALOG = "ressources/cubeSchemas/DOPAN_DW3-agg.xml";
    private static final String USER = "patrick";
    private static final String PASSWORD = "oopi7taing7shahD";

    private static OlapConnection connectToServer() {
        OlapConnection olapConnection = null;
        try {
            Class.forName(Playground.DRIVER_CLASS);
            Connection connection = DriverManager.getConnection(
                    "jdbc:" + Playground.PROVIDER + ":" +
                            "Jdbc=" + Playground.JDBC_URL + ";" +
                            "Catalog=" + Playground.CATALOG + ";" +
                            "JdbcUser=" + Playground.USER + ";" +
                            "JdbcPassword=" + Playground.PASSWORD + ";"
            );
            olapConnection = connection.unwrap(OlapConnection.class);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver class not found, you should try with \"mondrian.olap4j.MondiranOlap4jDriver\" for connecting to a Mondrian server");
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return olapConnection;
    }


    public static void main(String[] args) {
        // ---------------------------------------------------
        // Feel free to remove code here if it becomes messy
        // It's only testing
        // ---------------------------------------------------
        String queryBig = "SELECT NON EMPTY {Hierarchize({[Indicateur de lieu de travail.ILOCC_Hierarchie_1].[Indicateur de lieu].Members})} ON COLUMNS, NON EMPTY {Hierarchize({[Lien avec la personne de reference.LPRM_Hierarchie_1].[Lien avec la personne de reference].Members})} ON ROWS FROM [Cube1MobProInd]";
        String querySmall = "SELECT NON EMPTY {Hierarchize({[Indicateur de lieu de travail.ILOCC_Hierarchie_1].[Categorie].Members})} ON COLUMNS, NON EMPTY {Hierarchize({[Lien avec la personne de reference.LPRM_Hierarchie_1].[Categorie].Members})} ON ROWS FROM [Cube1MobProInd]";

        String query = "select NON EMPTY {Hierarchize({[Measures].[Nombre total d'individus]})} ON COLUMNS, NON EMPTY {Hierarchize({[Temps d'emploi.TEMPSEMP_Hierarchie].[Temps d'emploi].Members})} ON ROWS from [Cube1MobProInd]";

        OlapConnection connection = connectToServer();
        if (connection != null) {
            try {

                OlapStatement statement = connection.createStatement();
                CellSet cellSetBig = new CellSet(statement.executeOlapQuery(queryBig));
                CellSet cellSetSmall = new CellSet(statement.executeOlapQuery(querySmall));

                CellSet cellSet = new CellSet(statement.executeOlapQuery(query));

                //Test Descriptive clustering
               /* DescriptiveClustering autoClustering = new DescriptiveClustering();
                List<List<Boolean[][]>> modelComponentList = autoClustering.fitAndPredict(cellSet);
                List<Integer[][]> clusterList = autoClustering.getClusters();
                for (int i = 0; i < clusterList.size(); i++) {
                    System.out.println(i);
                    System.out.println(Arrays.deepToString(clusterList.get(i)));
                }*/

                // Test Algorithm one
                /*List<AbstractModel> models = new ArrayList<>();
                models.add(new DescriptiveClustering());
                ProxyBuilder proxyBuilder = new AncestorOrDescendantProxyBuilder();
                SignificanceBuilder significanceBuilder = new ZScoreSignificanceBuilder();
                SurpriseBuilder surpriseBuilder = new DifferenceSurpriseBuilder();
                ComponentScoreBuilder componentScoreBuilder = new AverageComponentScoreBuilder();
                AlgorithmOne algo = new AlgorithmOne(models, proxyBuilder, significanceBuilder, surpriseBuilder, componentScoreBuilder, null);
                List<Boolean[][]> datasetComponentList = algo.compute(cellSet1, cellSet2);
                for (int i = 0; i < datasetComponentList.size(); i++) {
                    System.out.println(i);
                    System.out.println(Arrays.deepToString(datasetComponentList.get(i)));

                }*/

                // Print schema and cubes names
                /*System.out.println("# SCHEMA: " + connection.getSchema());
                Schema schema = connection.getOlapSchema();
                System.out.println(namedListToString(schema.getCubes()));

                Cube cube = connection.getOlapSchema().getCubes().get(0);
                System.out.println("# CUBE: " + cube.getName() + " (" + cube.getUniqueName() + ")");
                System.out.println("## DIMENSIONS:");
                System.out.println(namedListToString(cube.getDimensions()));
                System.out.println("## HIERARCHIES:");
                System.out.println(namedListToString(cube.getHierarchies()));

                for (int i = 0; i < 10; i++) {
                    Hierarchy hierarchy = cube.getHierarchies().get(i);
                    System.out.println("## " + hierarchy.getName());
                    System.out.println(namedListToString(hierarchy.getLevels()));
                }


                Hierarchy hierarchy = cube.getHierarchies().get(2);
                System.out.println(hierarchy.getName());

                Level allLevel = hierarchy.getLevels().get("Tout");
                Level regionsLevel = hierarchy.getLevels().get("Region");
                Level paysLevel = hierarchy.getLevels().get("Pays");
                Level communesLevel = hierarchy.getLevels().get("Communaute de communes");

                Member m1 = regionsLevel.getMembers().get(0);
                Member m2 = paysLevel.getMembers().get(0);
                Member m3 = communesLevel.getMembers().get(0);
                Member ma = allLevel.getMembers().get(0);
                System.out.println("# Ancestor tests");
                System.out.println(hierarchy.getDefaultMember().getName());
                System.out.println(ma.getName());
                System.out.println(m1.getName());
                System.out.println(m2.getName());
                System.out.println(m1.getChildMembers().contains(m2));*/

                ProxyBuilder proxyBuilder = new AncestorOrDescendantProxyBuilder();
                Map<List<Integer>, Set<List<Integer>>> proxies = proxyBuilder.computeProxyMatrix(cellSetSmall, cellSetBig);
                for (List<Integer> key : proxies.keySet()) {
                    System.out.print(Arrays.deepToString(key.toArray()));
                    System.out.print(" -> ");
                    for (List<Integer> val : proxies.get(key)) {
                        System.out.print(Arrays.deepToString(val.toArray()) + " ");
                    }
                    System.out.println();
                }

            } catch (OlapException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
