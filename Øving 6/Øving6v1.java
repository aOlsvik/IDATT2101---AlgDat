package Øving6;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class OvingAlgDat6 {
    public static void main(String[] args) {
        String[] filenames = {"src/Øving6/ø6g1.txt", "src/Øving6/ø6g2.txt", "src/Øving6/ø6g5.txt", "src/Øving6/ø6g6.txt"};
        for (String filename : filenames) {
            System.out.println("Get Strongly Connected Components for file (" + filename + "): ");
            printSCCFromFile(filename);
        }
    }

    private static void printSCCFromFile(String filename) {
        Graph graph = null;
        try {
            graph = new Graph(new BufferedReader(new FileReader(filename)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert graph != null;
        var doubleList = graph.getSCC();
        System.out.println(printDoubleArrayList(doubleList));
    }

    private static StringBuilder printDoubleArrayList(ArrayList<ArrayList<Node>> doubleList) {
        StringBuilder sb = new StringBuilder();
        for (ArrayList<Node> list : doubleList) {
            for (Node n : list) {
                sb.append(n.getValue()).append(" ");
            }
            sb.append("\n");
        }
        return sb;
    }
}

class Graph {
    private int N;
    private int E;
    private Node[] nodes;

    public Graph(BufferedReader br) throws IOException{
        try {
            newGraph(br);
        } catch (IOException e) {
            throw new IOException("Something went wrong when reading from file");
        }
    }

    public Graph(int numberOfNodes, int numberOfEdges) {
        this.N = numberOfNodes;
        this.E = numberOfEdges;
        this.nodes = new Node[numberOfNodes];
    }

    private Node[] getNodes() {
        return nodes;
    }

    private void newGraph (BufferedReader br) throws IOException {
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        nodes = new Node[N];

        for (int i = 0; i < N; ++i)
            nodes[i] = new Node(i);

        E = Integer.parseInt(st.nextToken());

        for (int i=0; i < E; ++i) {
            st = new StringTokenizer(br.readLine());
            int from = Integer.parseInt(st.nextToken());
            int to = Integer.parseInt(st.nextToken());
            nodes[from].addEdge(nodes[to]);
        }


    }

    private Graph transpose() {
        Graph transposedGraph = new Graph(this.N, this.E);
        ArrayList<Edge> transposeNodes;
        for (int i = 0; i < N; i++) transposedGraph.getNodes()[i] = new Node(i);
        for (int i = 0; i < N; i++) {
            transposeNodes = deepCopyEdges(nodes[i].getEdges(), transposedGraph.getNodes());
            for (Edge e : transposeNodes) {
                transposedGraph.getNodes()[e.getTo().getValue()].addEdge(e.getFrom());
            }
        }
        return transposedGraph;
    }

    private ArrayList<Edge> deepCopyEdges(ArrayList<Edge> listToBeCopied, Node[] nodeList) {
        ArrayList<Edge> newList = new ArrayList<>();
        for (Edge e : listToBeCopied) {
            newList.add(new Edge(nodeList[e.getFrom().getValue()], nodeList[e.getTo().getValue()]));
        }
        return newList;
    }

    public ArrayList<ArrayList<Node>> getSCC () {
        Graph transposed = transpose();

        ArrayList<ArrayList<Node>> dfsTree = getDFSTrees(transposed);
        ArrayList<ArrayList<Node>> uniqueDfsTree = getUniqueDFSTrees(transposed);

        ArrayList<ArrayList<Node>> components = new ArrayList<>();
        for (int i = 0; i < uniqueDfsTree.size(); i++) {
            ArrayList<Node> component = new ArrayList<>();
            for (int j = 0; j < dfsTree.size(); j++) {
                if (uniqueDfsTree.get(i).equals(dfsTree.get(j))) {
                    if (!component.contains(transposed.getNodes()[j]))
                        component.add(transposed.getNodes()[j]);
                }
            }
            components.add(component);
        }

        return components;
    }

    private ArrayList<ArrayList<Node>> getUniqueDFSTrees(Graph transposed) {
        ArrayList<ArrayList<Node>> uniqueDfsTree = new ArrayList<>();
        for (Node n : transposed.getNodes()) {
            if (!uniqueDfsTree.contains(transposed.dfs(n)))
                uniqueDfsTree.add(transposed.dfs(n));
        }
        return uniqueDfsTree;
    }

    private ArrayList<ArrayList<Node>> getDFSTrees(Graph transposed) {
        ArrayList<ArrayList<Node>> dfsTree = new ArrayList<>();
        for (Node n : transposed.getNodes()) {
            dfsTree.add(transposed.dfs(n));
        }
        return dfsTree;
    }

    private ArrayList<Node> dfs(Node startingNode) {
        boolean[] visited = new boolean[N];
        ArrayList<Node> nodesVisited = new ArrayList<>();
        dfsStep(startingNode, visited);

        for (int i = 0; i < visited.length; i++) {
            if (visited[i]) nodesVisited.add(nodes[i]);
        }
        return nodesVisited;
    }

    private void dfsStep(Node at, boolean[] visited) {
        if (visited[at.getValue()]) return;
        visited[at.getValue()] = true;

        for (Node next : at.getConnectedNodes())
            dfsStep(next, visited);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Node n : nodes) {
            sb.append(n).append("\n").append("\n");
        }
        return "Graph\n" +
                "Number of Nodes: " + N +
                "\nNumber of Edges: " + E +
                "\n\n" + sb;
    }
}

class Node {
    private int value;
    private ArrayList<Edge> edges;

    public Node(int value) {
        this.value = value;
        this.edges = new ArrayList<>();
    }

    public void addEdge(Node to) {
        edges.add(new Edge(this, to));
    }

    public int getValue() {
        return value;
    }

    public List<Node> getConnectedNodes() {
        ArrayList<Node> connectedNodes = new ArrayList<>();
        for (Edge e : edges) {
            connectedNodes.add(e.getTo());
        }
        return connectedNodes;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    @Override
    public String toString() {
        return "Node: " + value +
                "\nEdges:\n" + edges;
    }
}

class Edge {
    private Node to;
    private Node from;

    public Edge(Node from, Node to) {
        this.from = from;
        this.to = to;
    }

    public Node getTo() {
        return to;
    }

    public Node getFrom() {
        return from;
    }

    @Override
    public String toString() {
        return  from.getValue() +
                " -> " +
                to.getValue();
    }
}