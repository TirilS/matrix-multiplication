import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

import static java.util.Arrays.sort;

public class MeasureSpeedup {
    private static final Classic classic = new Classic();
    private static final Parallel parallel = new Parallel();
    private static final CacheFriendly cacheFriendly = new CacheFriendly();
    private static final Iteration1 iteration1 = new Iteration1();
    private static final Iteration2 iteration2 = new Iteration2();

    private static MatrixMultiplier firstMultiplier;
    private static MatrixMultiplier secondMultiplier;

    private static boolean latex = false;

    public static void main(String[] args) {
        UserInput();
        
        PrintWriter writer;
        String filename = String.format("results/%dcores-%s-%s.%s", Runtime.getRuntime().availableProcessors(),
                firstMultiplier.getName(), secondMultiplier.getName(), (latex ? "tex" : "txt"));
        try {
            writer = new PrintWriter(filename, "UTF-8");

            if (latex) {
                for (String s : toLaTex()) {
                    writer.println(s);
                }
            } else {
                for (String s : toText()) {
                    writer.println(s);
                }
            }

            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        System.out.printf("%n%nSpeedup successfully measured%n");
    }

    /**
     * This method takes an int (size), and creates two double[size][size]-
     * matrices. It then calls the solve()-method of firstMultiplier and
     * secondMultiplier with the matrices, and records and returns the runtimes.
     * @param size  : an int
     * @return  : a double[]
     */
    private static double[] averageTime(int size) {
        Random random = new Random();

        double[] firstMultTime = new double[7];
        double[] secondMultTime = new double[7];

        for(int i = 0; i < 7; i++) {
            double[][] a = new double[size][size];
            double[][] a1 = new double[size][size];

            double[][] b = new double[size][size];
            double[][] b1 = new double[size][size];

            //fill the matrices
            for(int j = 0; j < size; j++) {
                for(int m = 0; m < size; m++) {
                    a[j][m] = random.nextDouble();
                    a1[j][m] = a[j][m];

                    b[j][m] = random.nextDouble();
                    b1[j][m] = b[j][m];
                }
            }

            long tt = System.nanoTime();
            double[][] c = firstMultiplier.solve(a, b);
            firstMultTime[i] = (System.nanoTime() - tt) / 1000000.0;

            tt = System.nanoTime();
            double[][] c1 = secondMultiplier.solve(a1, b1);
            secondMultTime[i] = (System.nanoTime() - tt) / 1000000.0;

            if(!checkSolve(c, c1)) {
                System.out.println("Error: matrices not identical.");
                System.exit(0);
            }
        }

        sort(firstMultTime);
        sort(secondMultTime);

        return new double[]{firstMultTime[3], secondMultTime[3]};
    }

    /**
     * This method takes two double[][]-matrices, and iterates through each
     * element in one, comparing it to the equivalent element in the other.
     * It returns true iff the two matrices are identical, and false otherwise.
     * @param a : a double[][]
     * @param b : a double[][]
     * @return  : a boolean
     */
    private static boolean checkSolve(double[][] a, double[][] b) {
        for(int j = 0; j < a.length; j++) {
            for(int m = 0; m < a[0].length; m++) {
                if(a[j][m] != b[j][m]) {return false;}
            }
        }
        return true;
    }

    /**
     * This method writes the resulting runtimes and speedup of two
     * MatrixMultiplier classes to a double[] in a LaTeX format.
     * @return  : an ArrayList<String>
     */
    private static ArrayList<String> toLaTex () {
        double[] time;

        ArrayList<String> results = new ArrayList<>();
        results.add("\\begin{tabular}{ | c | c | c | c | }");
        results.add("\\hline");
        results.add(String.format("Size & %s & %s & Speedup \\\\",
                firstMultiplier.getName(), secondMultiplier.getName()));
        results.add("\\hline \\hline");

        int[] sizes = {100, 200, 500, 1000};
        for (int size : sizes) {
            time = averageTime(size);
            results.add(String.format("%s & %.4f & %.4f & %.1f \\\\", size,
                    time[0], time[1], (time[0] / time[1])));
            results.add("\\hline");
        }

        results.add("\\end{tabular}");

        return results;
    }

    /**
     * This method writes the resulting runtimes and speedup of two
     * MatrixMultiplier classes to a double[] in a plain text format.
     * @return  : an ArrayList<String>
     */
    private static ArrayList<String> toText () {
        double[] time;

        ArrayList<String> results = new ArrayList<>();

        results.add(String.format("Size\t\t%-13s\t\t%-13s\t\tSpeedup",
                firstMultiplier.getName(), secondMultiplier.getName()));

        int[] sizes = {100, 200, 500, 1000};
        for (int size : sizes) {
            time = averageTime(size);
            results.add(String.format("%-10s\t%-10.4f\t\t\t%-10.4f\t\t\t%.1f", size,
                    time[0], time[1], (time[0] / time[1])));
        }

        return results;
    }

    /**
     * This method determines which two programs to run, and what file format
     * to write the results to, by asking the user.
     */
    private static void UserInput () {
        Scanner in = new Scanner(System.in);

        System.out.println("Please select the comparison program:");
        System.out.printf("   [0] %s%n", classic.getName());
        System.out.printf("   [1] %s%n", parallel.getName());
        System.out.printf("   [2] %s%n", cacheFriendly.getName());
        System.out.printf("   [3] %s%n", iteration1.getName());
        System.out.printf("   [4] %s%n", iteration2.getName());

        int input1 = -1;
        try {
            input1 = Integer.parseInt(in.nextLine());
        } catch (NumberFormatException ignored) {}

        while (input1 < 0 || input1 > 4) {
            System.out.println("Please type a valid number");
            try {
                input1 = Integer.parseInt(in.nextLine());
            } catch (NumberFormatException ignored) {}
        }

        switch (input1) {
            case 0 : firstMultiplier = classic; break;
            case 1 : firstMultiplier = parallel; break;
            case 2 : firstMultiplier = cacheFriendly; break;
            case 3 : firstMultiplier = iteration1; break;
            case 4 : firstMultiplier = iteration2; break;
        }

        System.out.printf("'%s' chosen%nPlease select the main program:%n", firstMultiplier.getName());
        if (!(firstMultiplier instanceof Classic)) System.out.printf("   [0] %s%n", classic.getName()) ;
        if (!(firstMultiplier instanceof Parallel)) System.out.printf("   [1] %s%n", parallel.getName()) ;
        if (!(firstMultiplier instanceof CacheFriendly)) System.out.printf("   [2] %s%n", cacheFriendly.getName()) ;
        if (!(firstMultiplier instanceof Iteration1)) System.out.printf("   [3] %s%n", iteration1.getName()) ;
        if (!(firstMultiplier instanceof Iteration2)) System.out.printf("   [4] %s%n", iteration2.getName()) ;

        int input2 = -1;
        try {
            input2 = Integer.parseInt(in.nextLine());
        } catch (NumberFormatException ignored) {}

        while (input2 < 0 || input2 > 4 || input2 == input1) {
            System.out.println("Please type a valid number");
            try {
                input2 = Integer.parseInt(in.nextLine());
            } catch (NumberFormatException ignored) {}
        }

        switch (input2) {
            case 0 : secondMultiplier = classic; break;
            case 1 : secondMultiplier = parallel; break;
            case 2 : secondMultiplier = cacheFriendly; break;
            case 3 : secondMultiplier = iteration1; break;
            case 4 : secondMultiplier = iteration2; break;
        }

        System.out.printf("'%s' chosen%n", secondMultiplier.getName());

        System.out.println("Please select in what format you would like the results to be outputted:");
        System.out.println("   [0] .tex");
        System.out.println("   [1] .txt");

        int input3 = -1;
        try {
            input3 = Integer.parseInt(in.nextLine());
        } catch (NumberFormatException ignored) {}

        while (input3 < 0 || input3 > 1) {
            System.out.println("Please type a valid number");
            try {
                input3 = Integer.parseInt(in.nextLine());
            } catch (NumberFormatException ignored) {}
        }

        if (input3 == 0) {
            latex = true;
        }

        System.out.printf("'%s' chosen%nMeasuring speedup ...", latex ? ".tex" : ".txt");
    }
}
