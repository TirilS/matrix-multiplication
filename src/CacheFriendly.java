public class CacheFriendly implements MatrixMultiplier{
    private double[][] a, b, c;

    @Override
    public String getName() {
        return "CacheFriendly";
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
        int cRows = a.length;
        int cColumns = b[0].length;
        c = new double[cRows][cColumns];

        transpose();

        double temp;
        for(int i = 0; i < cRows; i++) {
            //for each row in C
            for(int j = 0; j < cColumns; j++) {
                //for each column in C
                temp = 0.0;
                for(int k = 0; k < a[0].length; k++) {
                    temp += a[i][k] * b[j][k];
                }
                c[i][j] = temp;
            }
        }

        return c;
    }

    /**
     * A method that transposes the elements in matrix b,
     * making the rows columns and columns rows.
     */
    private void transpose() {
        double temp;

        for(int i = 0; i < b.length - 1; i++) {
            for(int j = i + 1; j < b[0].length; j++) {
                temp = b[i][j];
                b[i][j] = b[j][i];
                b[j][i] = temp;
            }
        }
    }
}
