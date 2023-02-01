package Øving7;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Oving7AlgDat {
    public static void main(String[] args) {
        String[] filepath = {"src/Øving7/vg1.txt", "src/Øving7/vg2.txt", "src/Øving7/vg3.txt", "src/Øving7/vg5.txt", "src/Øving7/vg4.txt"/*, "src/Øving7/vgSkandinavia.txt"*/};
        Scanner scanner = new Scanner(System.in);

        int counter = 0;
        boolean run = true;
        while (run) {
            System.out.println();
            System.out.println("Run prims on the files\nType \"r\" to rune the next file\n\"e\" to exit the program");
            String input = scanner.nextLine().trim();
            switch (input) {
                case "r":
                    if (counter >= filepath.length) {
                        run = false;
                        break;
                    }
                    System.out.print("starting node: ");
                    runPrimsOnFile(filepath[counter++], scanner.nextInt());
                    break;
                case "e":
                    System.out.println("exits the program");
                    run = false;
                    break;
            }
        }
    }

    private static void runPrimsOnFile(String filepath, int startNode) {
        System.out.println("Making graph");
        WGraph graph = null;
        try {
            graph = new WGraph(new BufferedReader(new FileReader(filepath)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Starting test for: " + filepath);
        long time = System.currentTimeMillis();
        var result = graph.prims(startNode);
        long time2 = System.currentTimeMillis();
        System.out.println("\nFinished test, printing result\n");
        System.out.println("From-To Weight");
        result.forEach(System.out::println);
        System.out.println(filepath);
        System.out.println("Total weight: " + result.stream().map(WEdge::getWeight).reduce(0, Integer::sum));
        System.out.println("result: " + result.size() + " / " + graph.getN());
        System.out.println(time2 - time + " ms");
    }
}

class WGraph {
    private int N;
    private int K;
    private Node[] nodes;

    public WGraph(BufferedReader br) throws IOException {
        try {
            newWGraph(br);
        } catch (IOException e) {
            throw new IOException("Something went wrong when reading from the file");
        }
    }

    private void newWGraph(BufferedReader br) throws IOException {
        StringTokenizer st = new StringTokenizer(br.readLine());
        N = Integer.parseInt(st.nextToken());
        nodes = new Node[N];

        for (int i = 0; i < N; i++)
            nodes[i] = new Node(i);
        K = Integer.parseInt(st.nextToken());
        for (int i = 0; i < K; i++) {
            st = new StringTokenizer(br.readLine());
            int from = Integer.parseInt(st.nextToken());
            int to = Integer.parseInt(st.nextToken());
            int weight = Integer.parseInt(st.nextToken());
            nodes[from].addEdge(new WEdge(nodes[from], nodes[to], weight));
            nodes[to].addEdge(new WEdge(nodes[to], nodes[from], weight));
        }
    }

    public int getN() {
        return N;
    }

    public ArrayList<WEdge> prims(int i) {
        ArrayList<WEdge> edges = new ArrayList<>();
        MinHeap heap = new MinHeap();
        Node at = nodes[i];

        while (!at.isVisited()) {
            at.visit();
            for (Node n :  at.getConnectedNodes()) {
                if (n.isVisited()) continue;
                boolean updated = setPrev(at, n);
                heap.insert(n);
                if (updated)
                    heap.update(n);
            }
            if (heap.getLength() == 0)
                break;

            Node min = heap.getMin();
            if (min == null)
                break;

            min.getPrev().setPrev(min);
            WEdge shortestEdge = min.getPrev().getShortestEdge();
            if (!edges.contains(shortestEdge)) edges.add(shortestEdge);
            at = min;
        }
        return edges;
    }

    private boolean setPrev(Node at, Node to) {
        boolean updated = true;
        if (to.getPrev() == null)
            to.setPrev(at);
        Node lastPrev = to.getPrev();
        WEdge shortestEdge = to.getShortestEdge();

        to.setPrev(at);
        if (to.getShortestEdge().getWeight() > shortestEdge.getWeight()) {
            to.setPrev(lastPrev);
            updated = false;
        }
        return updated;
    }
}

class Node {
    private int value;
    private ArrayList<WEdge> edges;
    private boolean visited;
    private Node prev;

    public Node(int value) {
        this.value = value;
        this.edges = new ArrayList<>();
        this.visited = false;
        this.prev = null;
    }

    public Node getPrev() {
        return prev;
    }

    public void setPrev(Node prev) {
        this.prev = prev;
    }

    public void addEdge(WEdge e) {
        edges.add(e);
    }

    public int getValue() {
        return value;
    }

    public WEdge getShortestEdge() {
        WEdge shortestEdge = null;
        for (WEdge e : edges) {
            if (e.getTo().equals(prev)) {
                if (shortestEdge == null)
                    shortestEdge = e;
                else if (e.getWeight() < shortestEdge.getWeight())
                    shortestEdge = e;
            }
        }
        return shortestEdge;
    }

    public ArrayList<WEdge> getEdges() {
        return edges;
    }

    public boolean isVisited() {
        return visited;
    }

    public void visit() {
        this.visited = true;
    }

    public List<Node> getConnectedNodes() {
        ArrayList<Node> connectedNodes = new ArrayList<>();
        for (WEdge e : edges) {
            connectedNodes.add(e.getTo());
        }
        return connectedNodes;
    }

    @Override
    public String toString() {
        return "Node: " + value +
                "\nEdges:\n" + edges;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node node = (Node) o;
        return value == node.value;
    }
}

class WEdge {
    private Node from;
    private Node to;
    private int weight;

    public WEdge(Node from, Node to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    public Node getFrom() {
        return from;
    }

    public Node getTo() {
        return to;
    }

    public int getWeight() {
        return weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WEdge wEdge = (WEdge) o;
        return weight == wEdge.weight && ((from.getValue() == wEdge.from.getValue() && to.getValue() == wEdge.to.getValue()) || (from.getValue() == wEdge.to.getValue() && to.getValue() == wEdge.from.getValue()));
    }

    @Override
    public String toString() {
        return  from.getValue() +
                " -> " +
                to.getValue() +
                ", w: " + weight;
    }
}

class MinHeap {
    private int length;
    private Node[] nodes;

    public MinHeap() {
        this.length = 0;
        this.nodes = new Node[8];
    }

    private boolean contains (Node x) {
        boolean contains = false;
        for (Node n : nodes) {
            if (n == null) return contains;
            if (n.equals(x))
                return true;
        }
        return contains;
    }

    public void insert(Node x) {
        if (!contains(x) && !x.isVisited()) {
            int i = length++;
            if (i == nodes.length)
                expand();
            nodes[i] = x;

            int f;
            while (i > 0 && nodes[i].getShortestEdge().getWeight() < nodes[f = over(i)].getShortestEdge().getWeight()) {
                swap(nodes, i, f);
                i = f;
            }
        }
    }

    private void expand() {
        Node[] newList = new Node[length<<1];
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] == null) break;
            newList[i] = nodes[i];
        }
        nodes = newList;
    }

    public Node getMin() {
        Node min = nodes[0];
        nodes[0] = nodes[(length--) - 1];
        nodes[length] = null;
        fixHeap(0);
        return min;
    }

    public void fixHeap(int i) {
        int m = left(i);
        if (m < length) {
            int h = m + 1;
            if (h < length && nodes[h].getShortestEdge().getWeight() < nodes[m].getShortestEdge().getWeight())
                m = h;
            if (nodes[m].getShortestEdge().getWeight() < nodes[i].getShortestEdge().getWeight()){
                swap(nodes, i, m);
                fixHeap(m);
            }
        }
    }

    private void swap(Node[] nodes, int i, int m) {
        Node k = nodes[m];
        nodes[m] = nodes[i];
        nodes[i] = k;
    }

    private int over(int i) {
        return (i - 1) >> 1;
    }

    private int left(int i) {
        return (i << 1) + 1;
    }

    private int right(int i) {
        return (i + 1) << 1;
    }

    public int getLength() {
        return length;
    }

    public void createHeap(){
        int i = length /2;
        while(i-- > 0) fixHeap(i);
    }

    public void update(Node n) {
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] == null) break;
            if (nodes[i].equals(n)) {
                prioUp(i);
            }
        }
    }

    public void prioUp(int i){
        int f;
        while(i>0 && nodes[i].getShortestEdge().getWeight() < nodes[f=over(i)].getShortestEdge().getWeight()){
            swap(nodes, i, f);
            i=f;
        }
    }
}