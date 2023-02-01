import java.util.Date;
import java.util.Random;

public class Oving1AlgDat {
    public static void main(String[] args) {
        int[] courseChange = addTestData(100_000);

        Date startingTime = new Date();
        int[] dateIndexes = getBuyAndSellDayForTheBestProfit(courseChange);
        Date endingTime = new Date();

        System.out.println("Startingtime: " + startingTime.getTime());
        System.out.println("Buyingdate: " + (dateIndexes[0] + 1) + "\nSellingdate: " + (dateIndexes[1] + 1) + "\nwith the course change of " + getSumCourseChange(courseChange, dateIndexes));
        System.out.println("Endingtime: " + endingTime.getTime());
        System.out.println("Difference in time: " + (endingTime.getTime() - startingTime.getTime()) + "ms");
    }

    private static int[] getBuyAndSellDayForTheBestProfit(int[] courseChange) {
        int biggestProfit = 0;
        int sellIndex = 0;
        int buyIndex = 0;
        int profit;

        for (int i = 0; i < courseChange.length; i++) {
            profit = 0;
            for (int j = i + 1; j < courseChange.length; j++) {
                profit += courseChange[j];
                if (profit > biggestProfit){
                    biggestProfit = profit;
                    sellIndex = j;
                    buyIndex = i;
                }
            }
        }

        return new int[]{buyIndex, sellIndex};
    }

    private static int[] addTestData(int lengthOfTable) {
        Random random = new Random();
        int[] table = new int[lengthOfTable];
        for (int i = 0; i < lengthOfTable; i++)
            table[i] = random.nextInt(-10, 10);

        return table;
    }

    private static int getSumCourseChange(int[] courseChange, int[] dateIndexes) {
        int sumCourseChange = 0;
        for (int i = dateIndexes[0] + 1; i <= dateIndexes[1]; i++)
            sumCourseChange += courseChange[i];

        return sumCourseChange;
    }
}
