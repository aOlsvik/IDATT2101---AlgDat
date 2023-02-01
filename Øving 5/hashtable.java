package Øving5;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class Oving5AlgDatA {
    public static void main(String[] args) {
        List<String> names = new ArrayList<>();
        String name;
        try (Scanner scanner = new Scanner(new File("src\\navn.txt"))) {
            while (scanner.hasNext()) {
                name = scanner.nextLine();
                names.add(name);
            }
        }catch (IOException e) {
            System.out.println(e.getMessage());
        }

        Hashtable hashtable = new Hashtable(114,names);
        System.out.println();
        System.out.println(hashtable);

        String input = "";
        Scanner scanner = new Scanner(System.in);
        while(true){
            System.out.print("Search for name (\"exit\"): ");
            input = scanner.nextLine();
            if (input.equalsIgnoreCase("exit"))
                break;
            System.out.println("Result: " + hashtable.find(input));
        }
    }
}

class Hashtable {
    private LinkedList<String>[] nodes;
    private int length;
    private int collision;
    private int numberOfNodes;
    private int numberOfElements;

    public Hashtable(int length) {
        this.collision = 0;
        this.length = length;
        this.numberOfNodes = 0;
        this.numberOfElements = 0;
        this.nodes = new LinkedList[length];
    }

    public Hashtable(int length, List<String> list) {
        this.collision = 0;
        this.length = length;
        this.numberOfNodes = 0;
        this.numberOfElements = 0;
        this.nodes = new LinkedList[length];
        putAll(list);
    }

    private int hash(String s) {
        int i = 1;
        int hash = 0;

        for(char c : s.toCharArray()){
            hash += c*i;
            i++;
        }
        return hash % getLength();
    }

    public void putAll(List<String> list) {
        list.forEach(this::put);
    }

    public void put(String s) {
       int i = hash(s);
       if (nodes[i] == null) {
           nodes[i] = new LinkedList<>(List.of(s));
           numberOfNodes++;
       }
       else {
           nodes[i].addLast(s);
           collision++;
           System.out.println("Collision between: " + nodes[i].getFirst() + " and " + s);
       }
       numberOfElements++;
    }

    public String find(String s) {
        StringBuilder foundString = new StringBuilder();
        int i = hash(s);
        if (nodes[i] != null) {
            for (String string : nodes[i]) {
                if (string.equalsIgnoreCase(s))
                    foundString.append(string);
            }
        }
        return (foundString.toString().isBlank()) ? foundString.append("Name does not exist").toString() : foundString.toString();
    }

    public int getLength() {
        return length;
    }

    public int getCollisions() {
        return collision;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    @Override
    public String toString() {
        return "Øving5.Hashtable\n" +
                "Length: " + length +
                "\nNumber of collisions: " + collision +
                "\nNumber of elements: " + numberOfElements +
                "\nLoadfactor: " + (double) Math.round((double) getNumberOfNodes() / (double) nodes.length*100)/100 +
                "\nNumber of collisions per person: " +  (double) Math.round((double) getCollisions() / (double) numberOfElements*100)/100;
    }
}