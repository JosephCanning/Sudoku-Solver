/*
 *  Name: Joseph Canning
 *  Class: CSCI-340
 *  Assignment: Project 5 - Sudoku
 *  Professor: Stuart Hansen
 *  Date Due: 5/4/2018
 *
 *  Program Purpose:    This program uses a recursive backtracking, depth-first search like approach to solve sudoku
 *                      puzzles. Puzzle text files are read in from the command line and their data is stored in a 2D
 *                      array called grid. newGrid is created as a copy of the original grid, and all changes are
 *                      made to newGrid rather grid. A third 2D array, fixed, is a boolean array that is true wherever
 *                      a number exists in grid. A variety of methods help numPossible determine if a value is possible
 *                      in the puzzle. When no values are possible, the program backtracks and tries new values for
 *                      previously visited array positions. Once the end of the puzzle is reached and a possible value
 *                      is found, a file is outputted with the solved puzzle in text form.
 */

package sudoku;

import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class Sudoku {

    private int[][] grid; // stores original input i.e. unsolved puzzle
    private int[][] newGrid; // modifications to the puzzle are made to newGrid; starts identical to grid
    private boolean[][] fixed; // true wherever a number exists in grid
    private String fileName; // the name of the input file

    /*
     *  Constructor for Sudoku. Accepts a String, fileName, that is the name of the input file for the puzzle. This
     *  constructor initializes all necessary objects and populates grid, newGrid, and fixed. Lastly, solve is run
     *  at position (0,0) which solves the sudoku puzzle and generates a file with the results.
     */
    public Sudoku(String fileName) throws Exception {

        Scanner in = new Scanner(new File(fileName));
        grid = new int[9][9];
        newGrid = new int[9][9];
        fixed = new boolean[9][9];
        int next;
        int col = 0;
        int row = 0;
        this.fileName = fileName;

        while (in.hasNext()) {

            next = in.nextInt();
            grid[row][col] = next;
            col++;

            if (col == 9) {

                col = 0;
                row++;

            }

        }

        System.arraycopy(grid, 0, newGrid, 0, 9);

        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {

                if (grid[i][j] != 0) {
                    fixed[i][j] = true;
                } else {
                    fixed[i][j] = false;
                }

            }
        }

        solve(0,0);

    }

    // Returns the array at position row within newGrid; called in numPossible
    private int[] getRow(int row) {
        return newGrid[row];
    }

    // Returns an array containing all values with column number col in newGrid; called in numPossible
    private int[] getCol(int col) {

        int[] colArr = new int[9];

        for (int i = 0; i < 9; i++) {
            colArr[i] = newGrid[i][col];
        }

        return colArr;

    }

    // Sudoku puzzles have nine blocks of nine numbers; this method returns an array with all numbers in the block in
    // which (row,col) resides; called in numPossible
    private int[] getBlock(int row, int col) {

        int[] blockArr = new int[9];
        int blockRow = row / 3;
        int blockCol = col / 3;
        int index = 0;

        for (int i = (3 * blockRow); i < (3 * blockRow) + 3; i++) {
            for (int j = (3 * blockCol); j < (3 * blockCol) + 3; j++) {
                blockArr[index++] = newGrid[i][j];
            }
        }

        return blockArr;

    }

    /*
     *  Returns true only if int num is a legal choice at position (row,col) according to the rules of sudoku. In
     *  sudoku, each column, row, and block must each contain nine unique digits; these are the rules that are being
     *  checked by this method. Three arrays are generated to represent the different groups that must contain unique
     *  digits. Each array is iterated through, and if a value is found that equals num, the method returns false.
     *  Otherwise, no conflicts occurred, so true is returned.
     */
    private boolean numPossible(int num, int row, int col) {

        int[] rowArr = getRow(row);
        int[] colArr = getCol(col);
        int[] blockArr = getBlock(row, col);

        for (int i : rowArr) {

            if (i == num) {
                return false;
            }

        }

        for (int i : colArr) {

            if (i == num) {
                return false;
            }

        }

        for (int i : blockArr) {

            if (i == num) {
                return false;
            }

        }

        return true;

    }

    // Outputs the solution to the puzzle as a file named [fileName].solved.
    private void outputSolution() throws Exception {

        File solved = new File(fileName.substring(0, fileName.length() - 4) + ".solved");
        solved.createNewFile();
        PrintWriter out = new PrintWriter(solved);

        for (int[] i : newGrid) {
            for (int j : i) {
                out.print(j + " ");
            }
            out.print("\n");
        }

        out.close();

    }

    /*
     *  This method is the heart of the program as it solves the puzzle. First, the method checks if row is equal to
     *  nine; if it is, that means the method must have found a legal value for the last position in the puzzle, so
     *  the solution is outputted and true is returned. The two parameters, row and col, represent the position in
     *  newGrid for which a legal value is being found. If that position was provided initially, a recursive call is
     *  made that processes the next position; false is returned. Lastly, for any other position, all legal digits are
     *  run through and once a legal digit is found, the position has its set to that digit. If the recursion for the
     *  next position yields true, true is returned for the current position. If no legal value can be found, the
     *  current position's value is set back to zero, and false is returned.
     */
    public boolean solve(int row, int col) {

        if (row == 9) {

            try {
                outputSolution();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return true;

        }

        if (fixed[row][col]) {

            if (col != 8) {
                solve(row, col + 1);
            } else {
                solve(row + 1, 0);
            }

            return false;

        }

        for (int i = 1; i < 10; i++) {

            if (numPossible(i, row, col)) {

                newGrid[row][col] = i;

                if (col != 8) {

                    if (solve(row, col + 1)) {
                        return true;
                    }

                } else {

                    if (solve(row + 1, 0)) {
                        return true;
                    }

                }

            }

        }

        newGrid[row][col] = 0;
        return false;

    }

    // Main method runs the program
    public static void main(String[] args) throws Exception {
        new Sudoku(args[0]);
    }

}
