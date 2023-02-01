import java.util.Scanner;

public class opg4d2 {
    public static void main(String[] args) {

        Tree binarySearchTree = new Tree();


        /* Example nodes: */
        String[] newWords = {"head", "leg", "foot", "elbow", "chin", "toe", "arm", "tooth"};
        for (String word : newWords) binarySearchTree.insertWord(new Word(word));


        Scanner in = new Scanner(System.in);

        while (true) {
            System.out.println("Add a new word: ('exit' to exit the program)");
            String newWord = in.nextLine();

            if (newWord.equalsIgnoreCase("exit")) {
                System.out.println("Finishing...");
                break;
            }

            binarySearchTree.insertWord(new Word(newWord));
            binarySearchTree.printTree();
        }
    }
}

class TreeNode {
    Word element;
    TreeNode left;
    TreeNode right;
    TreeNode parent;

    /**
     *
     * @param word - the word to be made into a node
     * @param parent - the parent node of this word
     * @param left - the left child
     * @param right - the right child
     */
    public TreeNode(Word word, TreeNode parent, TreeNode left, TreeNode right) {
        this.element = word;
        this.parent = parent;
        this.left = left;
        this.right = right;
    }
}

class Tree {
    TreeNode root;

    public Tree() {
        root = null;
    }

    /**
     * The method to insert a word into the complete tree by comparing it with all the words, and passing it left/right of the given word until it is alone on a spot
     * @param e the word to be added
     */
    void insertWord(Word e) {

        String key = ((Element)e).findKey();
        TreeNode node = root;

        if (root == null) {
            root = new TreeNode(e, null, null, null);
            return;
        }

        String sml = null;
        TreeNode f = null;

        while (node != null) {
            f = node;
            sml = ((Element)(node.element)).findKey();
            if (key.compareToIgnoreCase(sml) < 0) {
                node = node.left;
            } else {
                node = node.right;
            }
        }

        if (key.compareToIgnoreCase(sml) < 0) {
            f.left = new TreeNode(e, f,null,null);
        } else {
            f.right = new TreeNode(e, f,null,null);
        }
    }

    /**
     * Method to print the tree
     * Will print the first
     */
    public void printTree() {
        int numberOfWords = 0;
        StringBuilder print = new StringBuilder();

        Queue queue = new Queue(10);
        queue.addToQueue(root);
        while (!queue.isEmpty()) {
            TreeNode thisNode = (TreeNode) (queue.nextInQueue());

            StringBuilder node = new StringBuilder();

            if (thisNode != null) {

                String ord = thisNode.element.toString();

                // Calculates the margin of the node:
                if (numberOfWords == 0) printNode(node, ord, 64);
                else if (numberOfWords >= 1 && numberOfWords <= 2) printNode(node, ord, 32);
                else if (numberOfWords >= 3 && numberOfWords <= 6) printNode(node, ord, 16);
                else if (numberOfWords >= 7 && numberOfWords <= 15) printNode(node, ord, 8);

                // Adding the nodes children to the queue:
                queue.addToQueue(thisNode.left);
                queue.addToQueue(thisNode.right);
            } else {

                // Filling possible empty spaces:
                if (numberOfWords > 0 && numberOfWords < 3) node.append(" ".repeat(32));
                else if (numberOfWords > 2 && numberOfWords < 7) node.append(" ".repeat(16));
                else if (numberOfWords >= 7 && numberOfWords < 15) node.append(" ".repeat(8));

                // Adding null to the queue to fill blanks:
                if (numberOfWords < 16) {
                    queue.addToQueue(null);
                    queue.addToQueue(null);
                }
            }

            // Adding current node to the print:
            print.append(node);

            numberOfWords++;

            // Changes line:
            if (numberOfWords == 1 || numberOfWords == 3 || numberOfWords == 7 || numberOfWords == 15) print.append("\n");

        }
        // Finally prints the entire tree:
        System.out.println(print);
    }



    /**
     * Function used to calculate the number of spaces for each word
     * @param node - the node to be printed (placement)
     * @param word - the word to be printed (content)
     * @param spaces - number of spaces, depending on the row
     */
    private static void printNode(StringBuilder node, String word, int spaces) {
        int margin = (spaces - word.length())/2;
        node.append(" ".repeat(Math.max(0, margin)));
        node.append(word);
        node.append(" ".repeat(Math.max(0, margin)));
    }
}

interface Element {
    String findKey();
}

class Word implements Element {
    String key;

    public Word(String key) {
        this.key = key;
    }

    public String findKey() {
        return key;
    }

    @Override
    public String toString() {
        return key;
    }
}

class Queue {
    private Object[] tab;
    private int start = 0;
    private int end = 0;
    private int count = 0;

    public Queue(int str) {
        tab = new Object[str];
    }

    public boolean isEmpty() {
        return count == 0;
    }

    public boolean isFull() {
        return count == tab.length;
    }

    public void addToQueue(Object e) {
        if (isFull()) return;
        tab[end] = e;
        end = (end +1)%tab.length;
        ++count;
    }

    public Object nextInQueue() {
        if (!isEmpty()) {
            Object e = tab[start];
            start = (start+1)%tab.length;
            --count;
            return e;
        }
        else return null;
    }
}