public class Classic implements MatrixMultiplier{
    private double[][] a, b, c;

    @Override
    public String getName() {
        return "Classic";
    }

    /**
     * A method that multiplies a matrix (arr1) with another matrix (arr2) and
     * returns the resulting matrix using the classic algorithm.
     *
     * @param arr1  :  a double[][]
     * @param arr2  :  a double[][]
     * @return      :  a double[][]
     */
    @Override
    public double[][] solve(double[][] arr1, double[][] arr2) {
        a = arr1;
        b = arr2;
        int cRows = a.length;
        int cColumns = b[0].length;
        c = new double[cRows][cColumns];

        double temp;
        for(int i = 0; i < cRows; i++) {
            //for each row in C
            for(int j = 0; j < cColumns; j++) {
                //for each column in C
                temp = 0.0;
                for(int k = 0; k < a[0].length; k++) {
                    temp += a[i][k] * b[k][j];
                }
                c[i][j] = temp;
            }
        }

        return c;
    }
}
