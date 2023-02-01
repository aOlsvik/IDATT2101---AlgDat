import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Oving6v2 {
    public static void main(String[] args) {
        for(int i = 0; i<4 ; i++){
            System.out.println(getFileName(i));
            getStronglyConnected(getFileName(i));
        }
    }

    private static String getFileName(int i){
        String[] filenames = {"ø6g1", "ø6g2", "ø6g5", "ø6g6"};
        return filenames[i];
    }

    private static void getStronglyConnected(String filename){
        Graph g = null;
        try {
            g = new Graph(filename);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Get Strongly Connected Components: ");
        assert g != null;
        var doubleList = g.getSCC();
        StringBuilder sb = new StringBuilder();
        for (ArrayList<Node> list : doubleList) {
            for (Node n : list) {
                sb.append(n.value).append(" ");
            }
            sb.append("\n");
        }
        System.out.println(sb);
    }

    static class Graph{
        int n,k;
        Node[] nodes;

        public Graph(String filename) throws IOException{
            BufferedReader br = new BufferedReader(new FileReader(filename));
            newGraph(br);
        }

        public Graph(int n, int k){
            this.n=n;
            this.k=k;
            this.nodes=new Node[n];
        }

        private void newGraph(BufferedReader br) throws IOException {
            StringTokenizer st = new StringTokenizer(br.readLine());
            this.n = Integer.parseInt(st.nextToken());
            nodes = new Node[n];
            for (int i = 0; i < n; ++i) nodes[i] = new Node(i);
            this.k = Integer.parseInt(st.nextToken());
            for (int i=0; i < k; ++i) {
                st = new StringTokenizer(br.readLine());
                int from = Integer.parseInt(st.nextToken());
                int to = Integer.parseInt(st.nextToken());
                nodes[from].addEdge(nodes[to]);
            }
        }

        private Graph transposeGraph(){
            Graph g = new Graph(this.n,this.k);
            ArrayList<Edge> transposeNodes;
            for (int i = 0; i < n; i++) g.nodes[i]=new Node(i);
            for (int i = 0; i < n; i++) {
                transposeNodes = deepCopyEdges(nodes[i].edges, g.nodes);
                for (Edge e : transposeNodes) {
                    g.nodes[e.to.value].addEdge(e.from);
                }
            }

            return g;
        }


        private ArrayList<Edge> deepCopyEdges(ArrayList<Edge> listToBeCopied, Node[] nodeList) {
            ArrayList<Edge> newList = new ArrayList<>();
            for (Edge e : listToBeCopied) {
                newList.add(new Edge(nodeList[e.from.value], nodeList[e.to.value]));
            }
            return newList;
        }

        private ArrayList<Node> dfs(Node startNode){
            boolean[] visited = new boolean[n];
            ArrayList<Node> nodesVisited = new ArrayList<>();
            dfs_step(startNode,visited);

            for(int i=0; i<n; i++){
                if(visited[i]) nodesVisited.add(nodes[i]);
            }

            return nodesVisited;
        }

        private void dfs_step(Node atNode, boolean[] visited){
            if(visited[atNode.value]) return;
            visited[atNode.value] = true;
            for(Node n : atNode.getConnectedNodes()){
                dfs_step(n,visited);
            }
        }

        ArrayList<ArrayList<Node>> getAllDfs(){
            ArrayList<ArrayList<Node>> dfsTrees = new ArrayList<>();
            for(int i = 0; i<n; i++){
                dfsTrees.add(dfs(nodes[i]));
            }
            return dfsTrees;
        }



        public ArrayList<ArrayList<Node>> getSCC () {
            Graph transposed = transposeGraph();

            ArrayList<ArrayList<Node>> dfsTree = transposed.getAllDfs();
            ArrayList<ArrayList<Node>> uniqueDfsTree = getUniqueDFSTrees(transposed);

            ArrayList<ArrayList<Node>> components = new ArrayList<>();
            for (int i = 0; i < uniqueDfsTree.size(); i++) {
                ArrayList<Node> component = new ArrayList<>();
                for (int j = 0; j < dfsTree.size(); j++) {
                    if (uniqueDfsTree.get(i).equals(dfsTree.get(j))) {
                        if (!component.contains(transposed.nodes[j]))
                            component.add(transposed.nodes[j]);
                    }
                }
                components.add(component);
            }

            return components;
        }

        private ArrayList<ArrayList<Node>> getUniqueDFSTrees(Graph transposed) {
            ArrayList<ArrayList<Node>> uniqueDfsTree = new ArrayList<>();
            for (Node n : transposed.nodes) {
                if (!uniqueDfsTree.contains(transposed.dfs(n)))
                    uniqueDfsTree.add(transposed.dfs(n));
            }
            return uniqueDfsTree;
        }


        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Node n : nodes) {
                sb.append(n).append("\n");
            }
            return "Graph\n" +
                    "N: " + n +
                    "\nK: " + k +
                    "\n\n" + sb;
        }
    }

    static class Node{
        int value;
        ArrayList<Edge> edges;

        public Node(int value){
            this.value=value;
            this.edges=new ArrayList<>();
        }

        public void addEdge(Node to){
            this.edges.add(new Edge(this,to));
        }

        private ArrayList<Node> getConnectedNodes(){
            ArrayList<Node> connectedNodes = new ArrayList<>();
            for(Edge e : edges){
                connectedNodes.add(e.to);
            }
            return connectedNodes;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder("Node ").append(value);
            if(!edges.isEmpty()){
                sb.append("--> ");
                edges.forEach(s->sb.append(s).append(", "));
                sb.delete(sb.length()-2,sb.length()-1);
            }
            return sb.toString();
        }
    }
    static class Edge{
        private Node from,to;

        public Edge(Node from, Node to){
            this.from=from;
            this.to=to;
        }

        @Override
        public String toString() {
            return " " + from.value + " " + to.value;
        }
    }
}