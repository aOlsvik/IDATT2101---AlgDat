import java.util.*;

public class Oving5AlgDatB {
    public static void main(String[] args) {
        int runningTime = 10_000;
        int n = 10_000_000;
        int[] list = getList(n);

        HashtableWithIntegerKeys table = new HashtableWithIntegerKeys(n);
        System.out.println("Starting the tests: ");
        System.out.println();

        //Time measurement of hashtable with integer keys
        double time;
        int rounds = 0;
        Date start = new Date();
        Date stop;
        do {
            table.clear();
            table.putAll(list);
            stop = new Date();
            ++rounds;
        } while (stop.getTime()-start.getTime() < runningTime);

        time = (double)(stop.getTime()-start.getTime()) / rounds;
        System.out.println("Finished hashtable with integer keys (time): " + time + " ms");
        System.out.println(table);

        //Time measurement of java's hashmap
        rounds = 0;
        HashMap<Integer, Integer> javaTable = new HashMap<>();
        Date start2 = new Date();
        Date stop2;
        do {
            javaTable.clear();
            for (int j : list) {
                javaTable.put(j, j);
            }
           /* for (int i = 0; i < list.length; i++) {
                javaTable.put(i, list[i]);
            }*/
            stop2 = new Date();
            ++rounds;
        } while (stop2.getTime()-start2.getTime() < runningTime);

        double time2 = (double)(stop2.getTime()-start2.getTime()) / rounds;
        System.out.println();
        System.out.println("Finished java's hashtable (time): " + time2  + " ms");
        System.out.println("The size of the table: " + javaTable.size());
    }

    private static int[] getList(int length) {
        int[] list = new int[length];
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            list[i] = random.nextInt(length * 1_000); // so that the range of numbers is larger than the size of the list
        }
        return list;
    }
}

class HashtableWithIntegerKeys {
    int collisions;
    int[] array;

    public HashtableWithIntegerKeys(int length) {
        this.collisions = 0;
        this.array = new int[getLength(length)];
    }

    private int getLength(int n) {
        n = n * 120/100; //makes the length approximately 20% larger than n
        while(!isPrime(n)){
            n++;
        }
        return n;
    }

    private boolean isPrime(int n) {
        boolean isPrime=true;
        for(int i = 2; i < n/2; i++){
            if (n % i == 0) {
                isPrime = false;
                break;
            }
        }
        return isPrime;
    }

    public void put(int n) {
        int max = array.length;
        int pos = hash1(n);
        if (array[pos] == 0){
            array[pos] = n;
            return;
        }
        int h2 = hash2(n);
        for (int i = 0; i < max; i++) {
            pos = (pos + h2) % max;
            if (array[pos] == 0) {
                array[pos] = n;
                collisions += i + 1;
                return;
            }
        }
    }

    public void clear() {
        Arrays.fill(array, 0);
        collisions = 0;
    }

    public void putAll(int[] list) {
        Arrays.stream(list).forEach(this::put);
    }

    private int hash2(int hash) {
        return hash % (array.length - 1) + 1;
    }

    private int hash1(int n) {
        return n % array.length;
    }

    private double getLoadFactor() {
        int numberOfElements = 0;
        for (int n : array) {
            if (n != 0)
                numberOfElements++;
        }
        return (double)Math.round((double)numberOfElements / (double)array.length * 100) / 100;
    }

    @Override
    public String toString() {
        return "Collisions: " + collisions +
                "\nLoad factor: " + getLoadFactor();
    }
}