public class Parallel implements MatrixMultiplier{
    private double[][] a, b, c;

    @Override
    public String getName() {
        return "Parallel";
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

        int multLength = c.length / cores;
        // The amount of numbers each thread gets for multiplication
        int multFrom = 0, multTo = multLength;
        // The start and endpoints of the threads numbers for multiplication

        for(int i = 0; i < cores - 1; i++) {
            //Creating and starting a thread
            threads[i] = new Thread(new multiplierThread(multFrom, multTo));
            threads[i].start();

            // Updating multFrom and multTo
            multFrom += multLength;
            multTo += multLength;
        }

        // Creating and starting the last thread
        threads[cores - 1] = new Thread(new multiplierThread(multFrom, c.length));
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
                    temp += a[i][k] * b[k][j];
                }
                c[i][j] = temp;
            }
        }
    }

    private class multiplierThread implements Runnable {
        int multFrom, multTo;

        multiplierThread(int multFrom, int multTo) {
            this.multFrom = multFrom;
            this.multTo = multTo;
        }

        /**
         * This method calls multMatrix() with multFrom and multTo as input.
         */
        public void run() {
            multMatrix(multFrom, multTo);
        }
    }
}
