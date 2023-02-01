import java.util.Scanner;

public class Oving4AlgDat {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        DoubleLinkedList listOne = new DoubleLinkedList();
        DoubleLinkedList listTwo = new DoubleLinkedList();

        System.out.println("Sum two numbers");
        System.out.print("Insert the first number: ");
        String numberOne = scanner.nextLine().trim();
        System.out.println();
        System.out.print("Insert the second number: ");
        String numberTwo = scanner.nextLine().trim();

        makeNumberToDoubleLinkedList(listOne, numberOne);
        makeNumberToDoubleLinkedList(listTwo, numberTwo);

        System.out.println();
        printDoubleLinkedList(listOne);
        System.out.println();
        System.out.println(" + ");
        printDoubleLinkedList(listTwo);
        System.out.println();
        System.out.println(" = ");
        printDoubleLinkedList(listOne.sumList(listTwo));


        System.out.println("\n");
        System.out.println("Subtract two numbers");
        System.out.print("Insert the first number: ");
        numberOne = scanner.nextLine().trim();
        System.out.println(numberOne);
        System.out.println();
        System.out.print("Insert the second number: ");
        numberTwo = scanner.nextLine().trim();

        listOne = new DoubleLinkedList();
        listTwo = new DoubleLinkedList();
        makeNumberToDoubleLinkedList(listOne, numberOne);
        makeNumberToDoubleLinkedList(listTwo, numberTwo);

        System.out.println();
        printDoubleLinkedList(listOne);
        System.out.println();
        System.out.println(" - ");
        printDoubleLinkedList(listTwo);
        System.out.println();
        System.out.println(" = ");
        printDoubleLinkedList(listOne.subtractList(listTwo));
    }

    private static void makeNumberToDoubleLinkedList(DoubleLinkedList list, String number) {
        char[] numberList = number.toCharArray();
        for (int i = 0; i < number.length(); i++)
            list.insertAtTheBack(Integer.parseInt(String.valueOf(numberList[i])));
    }

    private static void printDoubleLinkedList(DoubleLinkedList list) {
        Node n = list.getHead();
        for (int i = 0; i < list.getNumberOfElements(); i++) {
            System.out.print(n.findElement());
            n = n.findNext();
        }
    }
}

/**
 * The double linked list class
 * This holds the information about what the first and last node is in the list
 */
class DoubleLinkedList {
    private Node head;
    private Node tail;
    private int numberOfElements;

    public DoubleLinkedList(Node head, Node tail) {
        this.head = head;
        this.tail = tail;

        if (head == null && tail == null)
            this.numberOfElements = 0;
        else if (head == null || tail == null)
            this.numberOfElements = 1;
        else
            this.numberOfElements = 2;
    }

    public DoubleLinkedList() {
        this.head = null;
        this.tail = null;
        this.numberOfElements = 0;
    }

    public void insertAtTheFront(int value) {
        head = new Node(value, head, null);

        if (tail == null)
            tail = head;
        else
            head.findNext().setPrevious(head);
        numberOfElements++;
    }

    public void insertAtTheBack(int value) {
        Node newElement = new Node(value, null, tail);

        if (tail != null)
            tail.setNext(newElement);
        else
            head = newElement;
        tail = newElement;

        numberOfElements++;
    }

    public Node remove(Node n) {
        if (n.findPrevious() != null)
            n.findPrevious().setNext(n.findNext());
        else
            head = n.findNext();

        if (n.findNext() != null)
            n.findNext().setPrevious(n.findPrevious());
        else
            tail = n.findPrevious();

        n.setNext(null);
        n.setPrevious(null);
        numberOfElements--;
        return n;
    }

    public int getNumberOfElements() {
        return numberOfElements;
    }

    public Node getHead() {
        return head;
    }

    public Node getTail() {
        return tail;
    }

    /**
     * The method to sum together two lists
     * @param otherList the other list being summed
     * @return the resulting list
     */
    public DoubleLinkedList sumList(DoubleLinkedList otherList) {
        DoubleLinkedList sumList = new DoubleLinkedList();
        Node otherStartingNode = otherList.getTail();
        Node startingNode = getTail();

        addNumbers(sumList, startingNode, otherStartingNode, false);
        return sumList;
    }

    /**
     * The method that adds the two numbers
     * @param sumList the resulting list
     * @param node the node that the addition is on
     * @param otherNode the other node that the addition is on
     * @param lastSumGreaterThanNine if the last sum was greater than nine, one is added to the sum
     * @return recursive method call until the list is reviewed or on of the lists are reviewed. If the last example occurs the method subtractNumber is called instead.
     */
    private int addNumbers(DoubleLinkedList sumList, Node node, Node otherNode, boolean lastSumGreaterThanNine) {
        int sum = (lastSumGreaterThanNine) ? (node.findElement() + otherNode.findElement() + 1) : (node.findElement() + otherNode.findElement());
        boolean sumGreaterThanNine = sum >= 10;

        if (sumGreaterThanNine)
            sumList.insertAtTheFront(sum - 10);
        else
            sumList.insertAtTheFront(sum);

        if (node.findPrevious() == null || otherNode.findPrevious() == null) {
            if (node.findPrevious() != null)
                return addNumber(sumList, node.findPrevious(), sumGreaterThanNine);
            else if (otherNode.findPrevious() != null)
                return addNumber(sumList, otherNode.findPrevious(), sumGreaterThanNine);
            else {
                if (sumGreaterThanNine)
                    sumList.insertAtTheFront(1);
                return 0;
            }
        }
        else
            return addNumbers(sumList, node.findPrevious(), otherNode.findPrevious(), sumGreaterThanNine);
    }

    /**
     * The method for adding a number to the resulting list. This is when one of the lists are larger than the other
     * @param sumList the resulting list
     * @param node the node that the addition is on
     * @param lastSumGreaterThanNine if the last sum was greater than nine
     * @return recursive method call until the list is reviewed
     */
    private int addNumber(DoubleLinkedList sumList, Node node, boolean lastSumGreaterThanNine) {
        int sum = node.findElement();
        if (lastSumGreaterThanNine) sum++;
        boolean sumGreaterThanNine = sum >= 10;

        if (sumGreaterThanNine)
            sumList.insertAtTheFront(sum - 10);
        else
            sumList.insertAtTheFront(sum);

        if (node.findPrevious() == null){
            if (sumGreaterThanNine)
                sumList.insertAtTheFront(1);
            return 0;
        }
        else
            return addNumber(sumList, node.findPrevious(), sumGreaterThanNine);
    }

    /**
     * The method for subtracting a list with another list
     * @param otherList the other list being subtracted
     * @return the resulting list
     */
    public DoubleLinkedList subtractList(DoubleLinkedList otherList) {
        DoubleLinkedList sumList = new DoubleLinkedList();
        DoubleLinkedList theLargestList = findTheBiggestList(this, otherList);

        assert theLargestList != null;
        Node otherStartingNode = (theLargestList.equals(otherList)) ? getTail() : otherList.getTail();
        Node startingNode = (theLargestList.equals(this)) ? getTail() : otherList.getTail();

        subtractNumbers(sumList, startingNode, otherStartingNode, false);
        if (theLargestList.equals(otherList))
            sumList.getHead().setElement(sumList.getHead().findElement() * (-1));
        return sumList;
    }

    /**
     * The method that subtracts the two numbers
     * @param sumList the result list
     * @param node the node that the subtraction is on
     * @param otherNode the other node that the subtraction is on
     * @param lastSumLowerThanZero this is true if the last sum was lower than zero and the next node is not null
     * @return recursive method call until the list is reviewed or on of the lists are reviewed. If the last example occurs the method subtractNumber is called instead.
     */
    private int subtractNumbers(DoubleLinkedList sumList, Node node, Node otherNode, boolean lastSumLowerThanZero) {
        int sum = (lastSumLowerThanZero) ? ((node.findElement() - otherNode.findElement()) - 1) : (node.findElement() - otherNode.findElement());
        boolean sumLowerThanZero = sum < 0 && node.findPrevious() != null;

        if (sumLowerThanZero)
            sumList.insertAtTheFront(sum + 10);
        else
            sumList.insertAtTheFront(sum);

        if (node.findPrevious() == null || otherNode.findPrevious() == null) {
            if (node.findPrevious() != null)
                return subtractNumber(sumList, node.findPrevious(), sumLowerThanZero);
            else if (otherNode.findPrevious() != null)
                return subtractNumber(sumList, otherNode.findPrevious(), sumLowerThanZero);
            else {
                removeZerosAtTheFront(sumList);
                return 0;
            }
        }
        else
            return subtractNumbers(sumList, node.findPrevious(), otherNode.findPrevious(), sumLowerThanZero);
    }

    /**
     * This is for when the other list is shorter than the one being subtracted
     * @param sumList the result list after the subtraction
     * @param node the node that the subtraction is on
     * @param lastSumLowerThanZero this is true if the last sum was lower than zero and the next number is not null
     * @return recursive method call until the list is reviewed
     */
    private int subtractNumber(DoubleLinkedList sumList, Node node, boolean lastSumLowerThanZero) {
        int sum = node.findElement();
        if (lastSumLowerThanZero) sum--;
        boolean sumLowerThanZero = sum < 0 && node.findPrevious() != null;

        if (sumLowerThanZero)
            sumList.insertAtTheFront(sum + 10);
        else
            sumList.insertAtTheFront(sum);

        if (node.findPrevious() == null) {
            removeZerosAtTheFront(sumList);
            return 0;
        }
        else
            return subtractNumber(sumList, node.findPrevious(), sumLowerThanZero);
    }

    /**
     * The method for finding out wich nlist is the biggest
     * @param list the list being checked
     * @param otherList the other list being checked
     * @return the biggest list
     */
    private DoubleLinkedList findTheBiggestList(DoubleLinkedList list, DoubleLinkedList otherList) {
        if (list.getNumberOfElements() > otherList.getNumberOfElements())
            return list;
        else if (list.getNumberOfElements() < otherList.getNumberOfElements())
            return otherList;
        else{
            Node node = list.getHead();
            Node otherNode = otherList.getHead();
            while (node != null && otherNode != null){
                if (node.findElement() > otherNode.findElement())
                    return list;
                else if (node.findElement() < otherNode.findElement())
                    return otherList;

                node = node.findNext();
                otherNode = otherNode.findNext();

            }
        }
        return null;
    }

    /**
     * This removes the zeros in front of the list after subtraction
     * @param list the sum list with zeros at front
     */
    private void removeZerosAtTheFront(DoubleLinkedList list) {
        Node number = list.getHead();
        Node nextNumber = number;
        int numberOfRuns = list.getNumberOfElements();

        for (int i = 0; i < numberOfRuns; i++) {
            if (number != null && number.findElement() == 0) {
                nextNumber = number.findNext();
                list.remove(number);
            }
            number = nextNumber;
        }
    }
}

class Node {
    private int element;
    private Node previous;
    private Node next;

    public Node(int element, Node next, Node previous) {
        this.element = element;
        this.next = next;
        this.previous = previous;
    }

    public int findElement() {
        return element;
    }

    public Node findNext() {
        return next;
    }

    public Node findPrevious() {
        return previous;
    }

    public void setNext(Node next) {
        this.next = next;
    }

    public void setPrevious(Node previous) {
        this.previous = previous;
    }

    public void setElement(int element) {
        this.element = element;
    }
}

