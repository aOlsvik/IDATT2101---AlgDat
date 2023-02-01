import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.StringTokenizer;

public class ALT {
    public static void main(String[] args) {
        Graph g = new Graph();
        try{
            BufferedReader br = new BufferedReader(new FileReader("noder.txt"));
            BufferedReader brbr = new BufferedReader(new FileReader("kanter.txt"));
            BufferedReader brbrbr = new BufferedReader(new FileReader("interessepkt.txt"));

            g.readNodes(br);
            g.readEdges(brbr);
            g.readInterestPoints(brbrbr);

            Node start = g.node[232073]; //   Kårvåg - 3292784  Tampere - 232073
            Node destination = g.node[2518780];   //    Gjemnes - 7352330  Ålesund - 2518780
            Node trondheimLufthavn = g.node[7172108];
            Node trondheimTorg = g.node[4546048];
            Node hemsedal = g.node[3509663];

            int ladestasjon = 4;
            int spisested = 8;
            int drikkested = 16;
            int numberOfPoints = 8;

            String[] landmarks = {"Nordkapp", "Kristiansand", "Krakow", "Bremen", "Joensuu"};

            String filename = "preprocessedNordicMap.txt";
            FileWriter dijkstra = new FileWriter("dijkstraNodes.txt");
            FileWriter altAlgorithm = new FileWriter("altNodes.txt");

            long endTime;
            long startTime;
            if(!new File(filename).exists()){
                startTime = System.currentTimeMillis();
                g.preprocessMap(landmarks, filename);
                endTime= System.currentTimeMillis();
                System.out.println("Time spent preprocessing map: "+(endTime-startTime));
            }

            startTime = System.currentTimeMillis();
            g.readPreProcessedMap(filename);
            endTime = System.currentTimeMillis();
            System.out.println("Time spent reading map: "+(endTime-startTime) + " ms\n");

            startTime = System.currentTimeMillis();
            g.dijkstra(start,destination);
            endTime = System.currentTimeMillis();
            // writes the coordinates in the path ready for routing
            writeSearchResults(destination,dijkstra);
            System.out.println("Time spent on dijkstra: "+(endTime-startTime) + " ms");
            System.out.println("Time used from start->end: "+formatSeconds(((Prev)g.node[destination.value].d).dist/100) + "\n");

            int dijkstraVisited = 0;
            for(int i = 0; i<g.N; i++){
                if(g.visited[i]) dijkstraVisited++;
            }

            startTime = System.currentTimeMillis();
            g.altAlgorithm(start,destination);
            endTime = System.currentTimeMillis();
            writeSearchResults(destination,altAlgorithm);

            System.out.println("Time spent on alt algorithm: "+(endTime-startTime) + "ms");
            System.out.println("Time used from start->end: "+formatSeconds(((Prev)g.node[destination.value].d).dist/100) + "\n");

            int altVisited = 0;
            for(int i = 0; i<g.N; i++){
                if(g.visited[i]) altVisited++;
            }
            System.out.println("Alt visited nodes vs dijkstra visited nodes: " + altVisited + '/' + dijkstraVisited);

            Node[] ladestasjoner = g.dijkstra(trondheimLufthavn,ladestasjon,numberOfPoints);
            Node[] drikkesteder = g.dijkstra(trondheimTorg,drikkested,numberOfPoints);
            Node[] spisesteder = g.dijkstra(hemsedal,spisested,numberOfPoints);
            System.out.println("Charging stations the closest to Trondheim Lufthavn, Værnes");
            Arrays.stream(ladestasjoner).forEach(s-> System.out.println(s.value + ": " +s.name + " with type: " + s.classification));
            Arrays.stream(ladestasjoner).forEach(System.out::println);
            System.out.println("\nDrinking places the closest to Trondheim Torg");
            Arrays.stream(drikkesteder).forEach(s-> System.out.println(s.name + " with type: " + s.classification));
            Arrays.stream(drikkesteder).forEach(System.out::println);
            System.out.println("\nEating places the closest to Hemsedal");
            Arrays.stream(spisesteder).forEach(s-> System.out.println(s.name + " with type: " + s.classification));
            Arrays.stream(spisesteder).forEach(System.out::println);


        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private static void writeSearchResults(Node destination, FileWriter fw) throws IOException {
        int index = 0;
        Node n = destination;
        Prev p;
        while(n!=null){
            p=(Prev)n.d;
            n=p.prev;
            index++;
        }
        int adjust = index/500;
        n = destination;
        for(int i = 0; i<index; i++){
            if(i%(adjust+1)==0) fw.write(n+"\n");
            p=(Prev)n.d;
            n=p.prev;
        }
        fw.close();

    }

    static String formatSeconds(int timeInSeconds)
    {
        int secondsLeft = timeInSeconds % 3600 % 60;
        int minutes = (int) Math.floor(timeInSeconds % 3600 / 60);
        int hours = (int) Math.floor(timeInSeconds / 3600);

        String HH = ((hours       < 10) ? "0" : "") + hours;
        String MM = ((minutes     < 10) ? "0" : "") + minutes;
        String SS = ((secondsLeft < 10) ? "0" : "") + secondsLeft;

        return HH + ":" + MM + ":" + SS;
    }

    static class Node{
        Edge edge1;
        Object d;
        int value;
        String latitude;
        String longitude;
        int classification;
        String name;

        Node(int i){
            value=i;
        }

        Node(int i, String latitude, String longitude){
            value=i;
            this.latitude=latitude;
            this.longitude=longitude;
        }

        @Override
        public String toString() {
            return latitude + "," + longitude;
        }
    }
    static class Edge {
        Edge next;
        Node to;
        public Edge(Node n, Edge nst){
            to =n;
            next =nst;
        }
    }
    static class WEdge extends Edge {
        int weight;
        public WEdge(Node n, WEdge nxt, int wght){
            super(n,nxt);
            weight=wght;
        }
    }

    static class Prev {
        int dist;
        int estimate;
        Node prev;
        static int inf = 100000000;

        public Prev(){
            dist=inf;
            estimate=0;
        }

        public int getDistance() {
            return this.dist+this.estimate;
        }
    }

    static class Graph {
        int N, K, P;
        Node[] node;
        Node[] transposed;
        boolean[] visited;
        boolean[] found;
        PriorityQueue<Node> pq;
        HashMap<String, Node> interestPoints;
        int[] landmarks;
        int[][] fromLandmark;
        int[][] toLandmark;

        public Graph(){}

        public Graph(BufferedReader br)throws IOException{
            visited = new boolean[N];
            found = new boolean[N];
            interestPoints=new HashMap<>();
            newGraph(br);
        }

        public void newGraph(BufferedReader br)throws IOException {
            StringTokenizer st = new StringTokenizer(br.readLine());
            N=Integer.parseInt(st.nextToken());
            node=new Node[N];
            for(int i = 0; i<N; i++) node[i] = new Node(i);
            K=Integer.parseInt(st.nextToken());
            for(int i = 0; i<K; i++){
                st = new StringTokenizer(br.readLine());
                int from = Integer.parseInt(st.nextToken());
                int to = Integer.parseInt(st.nextToken());
                int weight = Integer.parseInt(st.nextToken());
                WEdge w = new WEdge(node[to],(WEdge) node[from].edge1,weight);
                node[from].edge1 = w;
            }
        }


        void readNodes(BufferedReader br) throws IOException {
            System.out.println("Reading nodes");
            StringTokenizer st = new StringTokenizer(br.readLine());
            N = Integer.parseInt(st.nextToken());
            node = new Node[N];
            transposed = new Node[N];
            for (int i = 0; i < N; i++) {
                st = new StringTokenizer(br.readLine());
                int value = Integer.parseInt(st.nextToken());
                String latitude = st.nextToken();
                String longitude = st.nextToken();
                node[i] = new Node(value,latitude,longitude);
                transposed[i] = new Node(value,latitude,longitude);
            }
        }

        void readEdges(BufferedReader br) throws IOException {
            System.out.println("Reading edges");
            StringTokenizer st = new StringTokenizer(br.readLine());
            K = Integer.parseInt(st.nextToken());
            for (int i = 0; i < K; i++) {
                st = new StringTokenizer(br.readLine());
                int from = Integer.parseInt(st.nextToken());
                int to = Integer.parseInt(st.nextToken());
                int weight = Integer.parseInt(st.nextToken());
                int length = Integer.parseInt(st.nextToken());
                WEdge w = new WEdge(node[to], (WEdge) node[from].edge1, weight);
                WEdge w2 = new WEdge(transposed[from], (WEdge) transposed[to].edge1, weight);
                node[from].edge1 = w;
                transposed[to].edge1 = w2;
            }
        }

        void readInterestPoints(BufferedReader br) throws IOException {
            System.out.println("Reading interest points");
            interestPoints=new HashMap<>();
            StringTokenizer st = new StringTokenizer(br.readLine());
            P = Integer.parseInt(st.nextToken());
            for (int i = 0; i < P; i++) {
                st = new StringTokenizer(br.readLine());
                int nodeValue = Integer.parseInt(st.nextToken());
                int category = Integer.parseInt(st.nextToken());
                StringBuilder name = new StringBuilder(st.nextToken());
                while (st.hasMoreTokens())
                    name.append(" ").append(st.nextToken());
                name = new StringBuilder(name.toString().replaceAll(String.valueOf('"'), ""));
                node[nodeValue].classification=category;
                node[nodeValue].name = name.toString();
                interestPoints.put(name.toString(),node[nodeValue]);
            }
        }


        public Node[] dijkstra(Node s, int type, int numberOfPoints) {
            Node[] interestPoints = new Node[numberOfPoints];
            int counter = 0;
            visited = new boolean[N];
            found = new boolean[N];
            initPrev(s);
            pq = makePrio(s);
            found[s.value] = true;
            while(counter<numberOfPoints){
                Node n = pq.poll();
                if ((n.classification & type) == type){
                    interestPoints[counter++]=n;
                }
                for(WEdge w = (WEdge)n.edge1; w!= null; w=(WEdge) w.next){
                    shorten(n,w);
                }
            }
            return interestPoints;
        }

        public void dijkstra(Node s) {
            visited = new boolean[N];
            found = new boolean[N];
            initPrev(s);
            pq = makePrio(s);
            found[s.value] = true;
            while(!pq.isEmpty()){
                Node n = pq.poll();
                visited[n.value]=true;
                for(WEdge w = (WEdge)n.edge1; w!= null; w=(WEdge) w.next){
                    shorten(n,w);
                }
            }
        }

        public void dijkstra(Node start,Node end){
            visited = new boolean[N];
            found = new boolean[N];
            initPrev(start);
            pq = makePrio(start);
            found[start.value] = true;
            while(!visited[end.value]){
                Node n = pq.poll();
                visited[n.value]=true;
                for(WEdge w = (WEdge)n.edge1; w!= null; w=(WEdge) w.next){
                    shorten(n,w);
                }
            }
        }

        public void dijkstraTransposed(Node s) {
            visited = new boolean[N];
            found = new boolean[N];
            initPrevTransposed(s);
            pq = makePrio(s);
            while(!pq.isEmpty()){
                Node n = pq.poll();
                visited[n.value]=true;
                for(WEdge w = (WEdge)n.edge1; w!= null; w=(WEdge) w.next){
                    shorten(n,w);
                }
            }
        }
        private void initPrevTransposed(Node s){
            for(int i=N; i-->0;){
                transposed[i].d=new Prev();
            }
            ((Prev)s.d).dist=0;
        }


        private void initPrev(Node s){
            for(int i=N; i-->0;){
                node[i].d=new Prev();
            }
            ((Prev)s.d).dist=0;
        }
        private void shorten(Node n, WEdge w){
            if(visited[w.to.value]) return;
            Prev nd = (Prev)n.d;
            Prev md=(Prev)w.to.d;
            if(!found[w.to.value]){
                pq.add(w.to);
                found[w.to.value] = true;
            }
            if(md.dist>nd.dist+w.weight){
                md.dist = nd.dist + w.weight;
                md.prev = n;
                pq.remove(w.to);
                pq.add(w.to);
            }
        }

        private PriorityQueue<Node> makePrio(Node s){
            PriorityQueue<Node> pq = new PriorityQueue<>(N, (a, b) -> ((Prev)a.d).getDistance() - ((Prev)b.d).getDistance());
            pq.add(s);
            return pq;
        }

        public void altAlgorithm(Node start, Node end){
            visited = new boolean[N];
            found = new boolean[N];
            initPrev(start);
            pq = makePrio(start);
            while(!visited[end.value]){
                Node n = pq.poll();
                visited[n.value]=true;
                for(WEdge w = (WEdge)n.edge1; w!= null; w=(WEdge) w.next){
                    altShorten(n,end, w);
                }
            }
        }
        private void altShorten(Node n, Node e, WEdge w){
            if(visited[w.to.value]) return;
            Prev nd = (Prev)n.d;
            Prev md=(Prev)w.to.d;
            if(!found[w.to.value]){
                calculateEstimate(w.to,e);
                pq.add(w.to);
                found[w.to.value] = true;
            }
            if(md.dist>nd.dist+w.weight){
                md.dist = nd.dist + w.weight;
                md.prev = n;
                pq.remove(w.to);
                pq.add(w.to);
            }
        }

        private void calculateEstimate(Node n, Node endNode) {
            int largestEstimate = 0;
            int previous = -1;
            for (int i = 0; i < landmarks.length; i++) {
                int estimateFromLandmark = fromLandmark[i][endNode.value] - fromLandmark[i][n.value];
                int estimateToLandmark = toLandmark[i][n.value] - toLandmark[i][endNode.value];
                largestEstimate = Math.max(estimateToLandmark, estimateFromLandmark);
                if (previous > largestEstimate) largestEstimate = previous;
                previous = largestEstimate;
            }
            if (largestEstimate > 0) ((Prev)n.d).estimate = largestEstimate;
        }

        public void preprocessMap(String[] landmarks, String filename) throws IOException{
            System.out.println("Preprocessing...");
            int[][] dijkstraLengths = new int[landmarks.length][N];
            for (int i = 0; i < landmarks.length; i++) {
                dijkstra(findInterestPoints(landmarks[i]));
                for (int j = 0; j < N; j++) {
                    dijkstraLengths[i][j] = ((Prev)node[j].d).dist;
                }
            }
            System.out.println("Dijkstra done");
            int[][] dijkstraLengthsTransposed = new int[landmarks.length][N];
            for (int i = 0; i < landmarks.length; i++) {
                dijkstraTransposed(findTransposedInterestPoint(landmarks[i]));
                for (int j = 0; j < N; j++) {
                    dijkstraLengthsTransposed[i][j] = ((Prev)transposed[j].d).dist;
                }
            }
            System.out.println("Dijkstra transposed done");
            FileWriter fw = new FileWriter(filename);
            for (int i = 0; i < landmarks.length; i++) {
                fw.write(String.valueOf(findInterestPoints(landmarks[i]).value));
                if(i+1<landmarks.length) fw.write(" ");
            }
            fw.write("\n");
            System.out.println("Landmarks was written");
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < landmarks.length; j++) {
                    fw.write(String.valueOf(dijkstraLengths[j][i]));
                    if(j+1<landmarks.length) fw.write(" ");
                }
                fw.write("\n");
            }
            System.out.println("Dijkstra was written");
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < landmarks.length; j++) {
                    fw.write(String.valueOf(dijkstraLengthsTransposed[j][i]));
                    if(j+1<landmarks.length) fw.write(" ");
                }
                if(i+1<N) fw.write("\n");
            }
            System.out.println("Dijkstra transposed was written");
            fw.close();
        }

        public void readPreProcessedMap(String filename) throws IOException {
            BufferedReader br = new BufferedReader(new FileReader(filename));
            StringTokenizer str = new StringTokenizer(br.readLine());
            final int size = str.countTokens();
            this.landmarks=new int[size];
            this.fromLandmark= new int[size][N];
            this.toLandmark = new int[size][N];
            for (int i = 0; i < size; i++) {
                landmarks[i] = Integer.parseInt(str.nextToken());
            }
            System.out.println("Landmarks was read");
            for (int i = 0; i < N; i++) {
                str = new StringTokenizer(br.readLine());
                for (int j = 0; j < size; j++) {
                    fromLandmark[j][i] = Integer.parseInt(str.nextToken());
                }
            }
            System.out.println("Distances from landmarks were read");
            for (int i = 0; i < N; i++) {
                str = new StringTokenizer(br.readLine());
                for (int j = 0; j < size; j++) {
                    toLandmark[j][i] = Integer.parseInt(str.nextToken());
                }
            }

            System.out.println("Distances to landmarks were read");
            System.out.println("Pre processed map was read");
        }


        public Node findInterestPoints(String s){
            return interestPoints.get(s);
        }

        public Node findTransposedInterestPoint(String s){
            return transposed[interestPoints.get(s).value];
        }
    }

}
