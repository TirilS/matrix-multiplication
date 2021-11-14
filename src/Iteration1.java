import java.util.concurrent.CyclicBarrier;

public class Iteration1 implements MatrixMultiplier{
    private double[][] a, b, c;
    private CyclicBarrier transDone;

    @Override
    public String getName() {
        return "Iteration1";
    }

    /**
     * A method that multiplies a matrix (arr1) with another matrix (arr2) and
     * returns the resulting matrix.
     *
     * @param arr1  :  a double[][]
     * @param arr2  :  a double[][]
     * @return      :  a double[][]
     */
    @Override
    public double[][] solve(double[][] arr1, double[][] arr2) {
        a = arr1;
        b = arr2;

        c = new double[a.length][b[0].length];

        int cores = Runtime.getRuntime().availableProcessors();
        Thread[] threads = new Thread[cores];

        transDone = new CyclicBarrier(cores);

        int multLength = c.length / cores;
        // The amount of numbers each thread gets for multiplication
        int multFrom = 0, multTo = multLength;
        // The start and endpoints of the threads numbers for multiplication

        int cRow = 0;
        // The current row in matrix b
        int transFrom, transTo;
        // The start and endpoints of the threads numbers for transformation

        int totalTransNumb = (((c.length * c[0].length) - c.length) / 2);
        // The total amount of how many numbers should be transposed
        int minTransNumb = totalTransNumb / cores;
        // The minimum of how many numbers each thread should transpose

        // Creating and starting the threads
        for(int i = 0; i < cores - 1; i++) {
            transFrom = cRow;
            int transNumb = c[cRow].length - (cRow + 1);
            cRow++;

            // Adding more columns to transpose until minTransNumb is reached
            while((transNumb < minTransNumb) && (cRow < c.length - 1)) {
                transNumb += c[cRow].length - (cRow + 1);
                cRow++;
            }

            transTo = cRow;

            //Creating and starting a thread
            threads[i] = new Thread(new para(transFrom, transTo, multFrom,
                    multTo));
            threads[i].start();

            // Updating multFrom and multTo
            multFrom += multLength;
            multTo += multLength;
        }

        // Creating and starting the last thread
        threads[cores - 1] = new Thread(new para(cRow, b.length - 1,
                multFrom, c.length));
        threads[cores - 1].start();

        // Waiting for all threads to finish their tasks
        for(int i = 0; i < cores; i++) {
            try {
                threads[i].join();
            } catch (Exception e) {return null;}
        }

        return c;
    }

    /**
     * A method that transposes the elements in matrix b,
     * making the rows columns and columns rows.
     * It starts the transposing from row fromRow, and ends before row toRow.
     *
     * @param fromRow  :  An integer
     * @param toRow    :  An integer
     */
    private void transpose(int fromRow, int toRow) {
        double temp;

        // Switches the element on row i column j with the element on row j column i
        for(int i = fromRow; i < toRow; i++) {
            for(int j = i + 1; j < b[0].length; j++) {
                temp = b[i][j];
                b[i][j] = b[j][i];
                b[j][i] = temp;
            }
        }
    }

    /**
     * A method that multiplies elements in matrix a with elements in matrix b.
     * It starts from row fromRow in matrix c, and ends before row toRow.
     *
     * @param fromRow  :  An integer
     * @param toRow    :  An integer
     */
    private void multMatrix(int fromRow, int toRow) {
        double temp;
        for(int i = fromRow; i < toRow; i++) {
            //for each row in C
            for(int j = 0; j < c[0].length; j++) {
                //for each element in row i
                temp = 0.0;
                for(int k = 0; k < a[0].length; k++) {
                    // Adding each multiplication
                    temp += a[i][k] * b[j][k];
                }
                c[i][j] = temp;
            }
        }
    }

    private class para implements Runnable {
        int transFrom, transTo;
        int multFrom, multTo;

        para(int transFrom, int transTo, int multFrom,
             int multTo) {
            this.transFrom = transFrom;
            this.transTo = transTo;
            this.multFrom = multFrom;
            this.multTo = multTo;
        }

        /**
         * This method calls transpose() with transFrom and transTo as input.
         * It then awaits the cyclicBarrier transDone before calling
         * multMatrix() with multFrom and multTo as input.
         */
        public void run() {
            transpose(transFrom, transTo);

            try {
                transDone.await();
            } catch(Exception e) {return;}

            multMatrix(multFrom, multTo);
        }
    }
}