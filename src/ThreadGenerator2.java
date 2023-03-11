import java.util.Scanner;

public class ThreadGenerator2 implements Runnable {
    public int indx, start, end;
    public int primeCount, sumPrime;

    public ThreadGenerator2(int indx, int start, int end) {
        this.indx = indx;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        primeCount = 0;
        sumPrime = 0;
        for (int i = start; i < end; i++) {
            if (isPrime(i)) {
                System.out.print("t" + indx + "-" + i + "  ");
                primeCount++;
                sumPrime+=i;
            }
        }
    }

    public boolean isPrime(int num) {
        for (int i = 2; i < num / 2; i++) {
            if (num % i == 0) {
                return false;
            }
        }
        return num == 1 ? false : true;
    }

    public int getPrimeCount() {
        return primeCount;
    }

    public int getSumPrime(){
        return sumPrime;
    }

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.print("Input start: ");
        int start = sc.nextInt();
        System.out.print("Input end: ");
        int end = sc.nextInt();

        int numThread = (end - start) / 100 + ((end - start) % 100 == 0 ? 0 : 1);

        Thread tr[] = new Thread[numThread];
        ThreadGenerator2 obj[] = new ThreadGenerator2[numThread];
        System.out.println("Running " + numThread + " thread"+(numThread==1?"":"(s)"));

        for (int t = 0; t < numThread; t++) {
            obj[t] = new ThreadGenerator2(t, start, (start + 100) < end ? (start + 100) : end);
            tr[t] = new Thread(obj[t]);
            tr[t].start();
            start += 100;
        }

        int numPrime = 0;
        int sum = 0;
        for (int i = 0; i < numThread; i++) {
            try {
                tr[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            numPrime += obj[i].getPrimeCount();
            sum += obj[i].getSumPrime();
        }
        System.out.println("\n-\nNumber of primes: " + numPrime);
        System.out.println("Sum of primes: " + sum);
        sc.close();
    }
}
