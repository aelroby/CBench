package ShapeAnalysis;

import Graph.Edge;
import Graph.Graph;
import java.util.ArrayList;
import java.util.Iterator;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.ElementWalker;

public class QueryShapeType {

    public static boolean isSingleEdge(String queryString){ //if no. of edges = 1, it is a single edge
        if (queryToGraph(queryString).edges.size() == 1) //Graph is a list of edges
        {
            return true;
        }
        return false;
    }

    public static boolean isChain(String queryString) {
        if (isSingleEdge(queryString)) //  single edge e.g. 0----0
        {
            return true;
        }

        //if chain with mutiple edges, return true e.g.   0---0----0  0----0---0---0---0...---0 
        //degree for every vertex is 2 except 2 vertices: 1st and last have degree 1
        Graph graph = queryToGraph(queryString);
        ArrayList<Integer> verticesDegrees = graph.getVerticesDegrees();

        int degree_1 = 0;
        int degree_2 = 0;
        int degree_not_1_or_2 = 0;

        for (Integer verticesDegree : verticesDegrees) {
            if (verticesDegree == 1) {
                degree_1++;
            } else if (verticesDegree == 2) {
                degree_2++;
            } else {
                degree_not_1_or_2++;
            }
        }

        if (degree_1 == 2 && degree_2 > 0 && degree_not_1_or_2 == 0) {
            return true;
        }
        return false;
    }

    public static boolean isChainSet(String queryString) {
        if (isChain(queryString)) {
            return true;
        }

        //if multiple chain with one or mutiple edges, return true e.g.   [0---0----0 0---0] [ 0----0---0---0---0...---0] 
        //(degree for every vertex is 2) or (degree 1 not greater than 1)
        Graph graph = queryToGraph(queryString);
        ArrayList<Integer> verticesDegrees = graph.getVerticesDegrees();

        int degree_1 = 0;
        int degree_2 = 0;
        int degree_not_1_or_2 = 0;

        for (Integer verticesDegree : verticesDegrees) {
            if (verticesDegree == 1) {
                degree_1++;
            } else if (verticesDegree == 2) {
                degree_2++;
            } else {
                degree_not_1_or_2++;
            }
        }

        if (degree_1 >= 2 && degree_2 > 0 && degree_not_1_or_2 == 0) {
            return true;
        }

        return false;
    }

    public static boolean isTree(String queryString) {

        if (isChain(queryString)) {
            return true;
        }

        if (isCycle(queryString)) {
            return false;
        }

        Graph graph = queryToGraph(queryString);
        //if for every pair of nodes i and j, there exists only one path from i to j
        for (int i = 0; i < graph.vertices.size(); i++) {
            for (int j = 0; j < graph.vertices.size(); j++) {
                if (graph.getAllPaths(graph.vertices.get(i), graph.vertices.get(j)).size() > 1
                  ||graph.getAllPaths(graph.vertices.get(i), graph.vertices.get(j)).size() == 0) {
                    return false;
                }
            }
        }

        return true;
    }

    public static boolean isStar(String queryString) {
        if (isTree(queryString)) {
            //if there exists exactly one node with more than 2 neighbors
            //(u,u1), (u,u2), (u,u3), ..
            Graph graph = queryToGraph(queryString);
            ArrayList<Integer> verticesDegrees = graph.getVerticesDegrees();

            int degree_1 = 0;
            int degree_2 = 0;
            int degree_greater_2 = 0;

            for (Integer verticesDegree : verticesDegrees) {
                if (verticesDegree > 2) {
                    degree_greater_2++;
                }
            }

            if (degree_greater_2 == 1) {
                //get node with dgree > 2
                String u = "";
                for (int i = 0; i < graph.vertices.size(); i++) {
                    if (verticesDegrees.get(i) > 2) {
                        u = graph.vertices.get(i);
                    }
                }
                for (int i = 0; i < graph.edges.size(); i++) {
                    if (graph.edges.get(i).source.equals(u) && graph.edges.get(i).destination.equals(u)) {
                        return false;
                    }

                }
                //get Vertices Connected To u
                ArrayList<String> vs = new ArrayList<>();
                for (int i = 0; i < graph.edges.size(); i++) {
                    if (graph.edges.get(i).source.equals(u)) {
                        if (vs.contains(graph.edges.get(i).destination)) {
                            return false;
                        }
                        vs.add(graph.edges.get(i).destination);
                    }

                    if (graph.edges.get(i).destination.equals(u)) {
                        if (vs.contains(graph.edges.get(i).source)) {
                            return false;
                        }
                        vs.add(graph.edges.get(i).source);
                    }

                }
                return true;
            }
        }
        return false;
    }

    public static boolean isForest(String queryString) {
        if (isTree(queryString)) {
            return true;
        }

        //if separated graphs are tree, ruren true
        Graph graph = queryToGraph(queryString);
        //if for every pair of nodes i and j, there exists only one path from i to j or no paths
        for (int i = 0; i < graph.vertices.size(); i++) {
            for (int j = 0; j < graph.vertices.size(); j++) {
                if (graph.getAllPaths(graph.vertices.get(i), graph.vertices.get(j)).size() > 1) {
                    return false;
                }
            }
        }
        return false;
    }

    public static boolean isCycle(String queryString) {
        //if the same rules of chain with 1st and last nodes connected
        //All nodes have degree 2
        Graph graph = queryToGraph(queryString);
        ArrayList<Integer> verticesDegrees = graph.getVerticesDegrees();

        int degree_2 = 0;
        int degree_not_1_or_2 = 0;

        for (Integer verticesDegree : verticesDegrees) {
            if (verticesDegree == 2) {
                degree_2++;
            } else {
                degree_not_1_or_2++;
            }
        }

        if (degree_2 > 0 && degree_not_1_or_2 == 0) {
            return true;
        }
        return false;
    }

    public static boolean isFlower(String queryString) {
        //flower is a graph consisting of a node x with three types of attachements:
        //chains, trees, petals(has nodes (s,t) with 2 or more oathes between them)
        return true;
    }

    public static boolean isFlowerSet(String queryString) {
        //if separate graphs are flowers
        return true;
    }

    private static Graph queryToGraph(String queryString) {
        //Define Graph as a set of edges
        final Graph graph = new Graph();
        try {
            Query query = QueryFactory.create(queryString);
            // This will walk through all parts of the query
            ElementWalker.walk(query.getQueryPattern(),
                    // For each element...
                    new ElementVisitorBase() {
                        // ...when it's a block of triples...
                        public void visit(ElementPathBlock el) {
                            // ...go through all the triples...
                            Iterator<TriplePath> triples = el.patternElts();
                            while (triples.hasNext()) {
                                TriplePath triple = triples.next();

                                graph.edges.add(new Edge(triple.getSubject().toString(), triple.getObject().toString()));
                                if (!graph.vertices.contains(triple.getSubject().toString())) {
                                    graph.vertices.add(triple.getSubject().toString());
                                }
                                if (!graph.vertices.contains(triple.getObject().toString())) {
                                    graph.vertices.add(triple.getObject().toString());
                                }
                            }
                        }
                    }
            );

        } catch (Exception e) {
            System.out.println("This Query has a problem");
        }
        return graph;
    }

    public static void main(String[] args) {
        String s = "SELECT  * WHERE { ?person  <http://xmlns.com/foaf/0.1/name>  ?name . ?name ?b ?c }";

        //Test Query to Graph
        Graph graph = queryToGraph(s);
        System.out.println("Graph Edges");
        System.out.println("============");
        for (Edge e : graph.edges) {
            System.out.println("(" + e.source + "," + e.destination + ")");
        }
        System.out.println("Graph Vertices");
        System.out.println("===============");
        ArrayList<Integer> verticesDegrees = graph.getVerticesDegrees();
        for (int i = 0; i < graph.vertices.size(); i++) {
            System.out.print("(" + graph.vertices.get(i) + ", " + verticesDegrees.get(i) + ")    ");
        }
        System.out.println();
        System.out.println("Graph Type");
        System.out.println("============");
        //Test Single Edge
        if (isSingleEdge(s)) {
            System.out.println("Single Edge");
        } else {
            System.out.println("Not Single Edge");
        }
        if (isChain(s)) {
            System.out.println("Chain");
        } else {
            System.out.println("Not Chain");
        }
        if (isChainSet(s)) {
            System.out.println("Chain Set");
        } else {
            System.out.println("Not Chain Set");
        }
        if (isStar(s)) {
            System.out.println("Star");
        } else {
            System.out.println("Not Star");
        }
        if (isTree(s)) {
            System.out.println("Tree");
        } else {
            System.out.println("Not Tree");
        }
        if (isForest(s)) {
            System.out.println("Forest");
        } else {
            System.out.println("Not Forest");
        }
        if (isCycle(s)) {
            System.out.println("Cycle");
        } else {
            System.out.println("Not Cycle");
        }
        if (isFlower(s)) {
            System.out.println("Flower");
        } else {
            System.out.println("Not Flower");
        }
        if (isFlowerSet(s)) {
            System.out.println("Flower Set");
        } else {
            System.out.println("Not Flower Set");
        }

    }
}
