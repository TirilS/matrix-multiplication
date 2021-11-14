import java.util.concurrent.CountDownLatch;

public class Iteration2 implements MatrixMultiplier{
    private double[][] a, b, c;
    private CountDownLatch[] transDone;

    @Override
    public String getName() {
        return "Iteration2";
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
        Thread[] transThreads = new Thread[cores/2];
        Thread[] multThreads = new Thread[cores];

        transDone = new CountDownLatch[b[0].length];
        for (int i = 0; i < transDone.length; i++) {
            transDone[i] = new CountDownLatch(transThreads.length);
        }

        int cColumn = 1;
        // The current column in matrix b
        int transFrom, transTo;
        // The start and endpoints of the threads numbers for transformation

        int totalTransNumb = (((c.length * c[0].length) - c.length) / 2);
        // The total amount of how many numbers should be transposed
        int minTransNumb = totalTransNumb / transThreads.length;
        // The minimum of how many numbers each thread should transpose

        // Creating and starting transposing threads:

        for (int i = 0; i < transThreads.length - 1; i++) {
            transFrom = cColumn;
            int transNumb = cColumn;
            cColumn++;

            // Adding more columns to transpose until minTransNumb is reached
            while((transNumb < minTransNumb) && (cColumn < b[0].length - 1)) {
                transNumb += cColumn;
                cColumn++;
            }

            transTo = cColumn;

            //Creating and starting a transThread
            transThreads[i] = new Thread(new ParaTrans(transFrom, transTo));
            transThreads[i].start();
        }

        // Creating and starting the last transThread
        transThreads[transThreads.length - 1] = new Thread(new ParaTrans(cColumn, b[0].length));
        transThreads[transThreads.length - 1].start();

        //Creating and starting multiplication threads:

        int multLength = c.length / multThreads.length;
        // The amount of numbers each thread gets for multiplication
        int multFrom = 0, multTo = multLength;
        // The start and endpoints of the threads numbers for multiplication

        for (int i = 0; i < multThreads.length - 1; i++) {
            //Creating and starting a multThread
            multThreads[i] = new Thread(new ParaMult(multFrom, multTo));
            multThreads[i].start();

            // Updating multFrom and multTo
            multFrom += multLength;
            multTo += multLength;
        }

        // Creating and starting the last multThread
        multThreads[multThreads.length - 1] = new Thread(new ParaMult(multFrom, a.length));
        multThreads[multThreads.length - 1].start();

        // Waiting for all transThreads to finish their tasks
        for (Thread transThread : transThreads) {
            try {
                transThread.join();
            } catch (Exception e) {
                return null;
            }
        }

        // Waiting for all multThreads to finish their tasks
        for (Thread multThread : multThreads) {
            try {
                multThread.join();
            } catch (Exception e) {
                return null;
            }
        }

        return c;
    }

    /**
     * A method that transposes the elements in matrix b,
     * making the rows columns and columns rows.
     * It starts the transposing from column fromColumn, and ends before column toColumn.
     *
     * @param fromColumn  :  An integer
     * @param toColumn    :  An integer
     */
    private void transpose(int fromColumn, int toColumn) {
        for (int i = toColumn - 1; i < transDone.length; i++) {
            transDone[i].countDown();
        }

        double temp;

        // Switches the element on row i column j with the element on row j column i
        for (int i = 0; i < toColumn - 1; i++) {
            for (int j = fromColumn; j < toColumn; j++) {
                if (i < j) {
                    temp = b[i][j];
                    b[i][j] = b[j][i];
                    b[j][i] = temp;
                }
            }
            transDone[i].countDown();
        }
    }

    /**
     * A method that multiplies elements in matrix a with elements in matrix b
     * and places the result in matrix c.
     * It starts from row fromRow in matrix a, and ends before row toRow.
     *
     * @param fromRow  :  An integer
     * @param toRow    :  An integer
     */
    private void multiply(int fromRow, int toRow) throws InterruptedException {
        double temp;
        for (int i = 0; i < c[0].length; i++) {
            transDone[i].await();
            for (int j = fromRow; j < toRow; j++) {
                temp = 0.0;
                for (int k = 0; k < a[0].length; k++) {
                    temp += b[i][k] * a[j][k];
                }
                c[j][i] = temp;
            }
        }
    }

    private class ParaTrans implements Runnable {
        int transFrom, transTo;

        ParaTrans(int transFrom, int transTo) {
            this.transFrom = transFrom;
            this.transTo = transTo;
        }

        /**
         * This method calls transpose() with transFrom and transTo as input.
         */
        public void run() {
            transpose(transFrom, transTo);
        }
    }

    private class ParaMult implements Runnable {
        int multFrom, multTo;

        ParaMult(int multFrom,
                 int multTo) {
            this.multFrom = multFrom;
            this.multTo = multTo;
        }

        /**
         * This method calls multiply() with multFrom and multTo as input.
         */
        public void run() {
            try {
                multiply(multFrom, multTo);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
