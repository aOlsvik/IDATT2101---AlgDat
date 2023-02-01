import java.util.Date;

public class Exponent {

    public static void main(String[] args) {
        Exponent exponent = new Exponent();

        double x = 2;
        int n=5000;
        int timeUsage = 1000;

        double r;
        Date start = new Date();
        int runder = 0;
        double tid;
        Date slutt;
        do {
            r = exponent.calculateExponent1(x,n);
            slutt = new Date();
            runder++;
        } while (slutt.getTime()-start.getTime() < timeUsage);
        tid = (double)
                (slutt.getTime()-start.getTime()) / runder;
        double calculationOne = exponent.calculateExponent1(x,n);
        System.out.println("Method 1 calculation: " + calculationOne);
        System.out.println("Millisekund pr. runde:" + tid+"\n");


        start=new Date();
        runder=0;
        do {
            r = exponent.calculateExponent2(x,n);
            slutt = new Date();
            runder++;
        } while (slutt.getTime()-start.getTime() < timeUsage);
        tid = (double)
                (slutt.getTime()-start.getTime()) / runder;
        double calculationTwo = exponent.calculateExponent2(x,n);
        System.out.println("Method 2 calculation: " + calculationTwo);
        System.out.println("Millisekund pr. runde:" + tid +"\n");

        start=new Date();
        runder=0;
        do {
            r = Math.pow(x,n);
            slutt = new Date();
            runder++;
        } while (slutt.getTime()-start.getTime() < timeUsage);
        tid = (double)
                (slutt.getTime()-start.getTime()) / runder;
        double calculationMathPowOne = Math.pow(x,n);
        System.out.println("Math.Pow calculation: "+ calculationMathPowOne);
        System.out.println("Millisekund pr. runde:" + tid+"\n");


    }
    public double calculateExponent1(double x, int n){
        if(n==0) return 1;
        else return x*calculateExponent1(x,n-1);
    }
    public double calculateExponent2(double x, int n){
        if(n==0) return 1;
        else if(n%2!=0) return x*calculateExponent2(x*x, (n-1)/2);
        else return calculateExponent2(x*x,n/2);
    }
}
