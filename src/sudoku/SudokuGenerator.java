package sudoku;

import java.util.*;

public class SudokuGenerator {
    private final Random random = new Random();

    public int[][] createSolvedBoard(int size) {
        int[][] board = new int[size][size];
        fillBasePattern(board);
        shuffleBoard(board);
        return board;
    }

    public int[][] createPuzzle(int[][] solution, int clues) {
        int n = solution.length;
        int wantedClues = Math.max(0, Math.min(clues, n * n));
        int[][] best = copy(solution);

        // Removing cells at random can create a puzzle with several valid answers.
        // Keep a removal only when the original solution remains the only solution.
        for (int attempt = 0; attempt < 5 && countClues(best) > wantedClues; attempt++) {
            int[][] candidate = copy(solution);
            List<Integer> positions = new ArrayList<>();
            for (int i = 0; i < n * n; i++) positions.add(i);
            Collections.shuffle(positions, random);
            for (int p : positions) {
                if (countClues(candidate) <= wantedClues) break;
                int r = p / n, c = p % n;
                int saved = candidate[r][c];
                candidate[r][c] = 0;
                if (countSolutions(candidate, 2) != 1) candidate[r][c] = saved;
            }
            if (countClues(candidate) < countClues(best)) best = candidate;
        }
        return best;
    }

    private int countClues(int[][] board) {
        int count = 0;
        for (int[] row : board) for (int value : row) if (value != 0) count++;
        return count;
    }

    /** Counts only up to limit; a limit of two is enough to verify uniqueness. */
    private int countSolutions(int[][] board, int limit) {
        int bestRow = -1, bestCol = -1;
        boolean[] bestCandidates = null;
        int smallest = Integer.MAX_VALUE;
        int n = board.length;

        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                if (board[r][c] != 0) continue;
                boolean[] candidates = candidatesFor(board, r, c);
                int count = 0;
                for (int value = 1; value <= n; value++) if (candidates[value]) count++;
                if (count == 0) return 0;
                if (count < smallest) {
                    smallest = count;
                    bestRow = r;
                    bestCol = c;
                    bestCandidates = candidates;
                    if (count == 1) break;
                }
            }
            if (smallest == 1) break;
        }
        if (bestRow == -1) return 1;

        int solutions = 0;
        for (int value = 1; value <= n && solutions < limit; value++) {
            if (!bestCandidates[value]) continue;
            board[bestRow][bestCol] = value;
            solutions += countSolutions(board, limit - solutions);
            board[bestRow][bestCol] = 0;
        }
        return solutions;
    }

    private boolean[] candidatesFor(int[][] board, int row, int col) {
        int n = board.length;
        boolean[] possible = new boolean[n + 1];
        Arrays.fill(possible, true);
        possible[0] = false;
        for (int i = 0; i < n; i++) {
            possible[board[row][i]] = false;
            possible[board[i][col]] = false;
        }
        int boxRows = getBoxRows(n), boxCols = getBoxCols(n);
        int startRow = row / boxRows * boxRows, startCol = col / boxCols * boxCols;
        for (int r = startRow; r < startRow + boxRows; r++)
            for (int c = startCol; c < startCol + boxCols; c++) possible[board[r][c]] = false;
        return possible;
    }

    private void fillBasePattern(int[][] board) {
        int n = board.length;
        int boxRows = getBoxRows(n);
        int boxCols = n / boxRows;
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                board[r][c] = (r * boxCols + r / boxRows + c) % n + 1;
            }
        }
    }

    private void shuffleBoard(int[][] board) {
        int n = board.length;
        int boxRows = getBoxRows(n);
        int boxCols = n / boxRows;

        // Shuffle symbols.
        int[] symbols = new int[n];
        for (int i = 0; i < n; i++) symbols[i] = i + 1;
        shuffle(symbols);
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) board[r][c] = symbols[board[r][c] - 1];
        }

        // Shuffle rows within each band and bands.
        List<int[]> bands = new ArrayList<>();
        for (int start = 0; start < n; start += boxRows) {
            int[] rows = new int[boxRows];
            for (int i = 0; i < boxRows; i++) rows[i] = start + i;
            shuffle(rows);
            bands.add(rows);
        }
        Collections.shuffle(bands, random);
        int[][] temp = copy(board);
        int rr = 0;
        for (int[] band : bands) for (int row : band) board[rr++] = temp[row].clone();

        // Shuffle columns within each stack and stacks.
        List<int[]> stacks = new ArrayList<>();
        for (int start = 0; start < n; start += boxCols) {
            int[] cols = new int[boxCols];
            for (int i = 0; i < boxCols; i++) cols[i] = start + i;
            shuffle(cols);
            stacks.add(cols);
        }
        Collections.shuffle(stacks, random);
        temp = copy(board);
        int cc = 0;
        int[] colOrder = new int[n];
        for (int[] stack : stacks) for (int col : stack) colOrder[cc++] = col;
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) board[r][c] = temp[r][colOrder[c]];
        }
    }

    private void shuffle(int[] array) {
        for (int i = array.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            int t = array[i]; array[i] = array[j]; array[j] = t;
        }
    }

    public static int getBoxRows(int size) {
        return switch (size) {
            case 6 -> 2;
            case 9 -> 3;
            case 16 -> 4;
            default -> throw new IllegalArgumentException("Unsupported Sudoku size: " + size);
        };
    }

    public static int getBoxCols(int size) {
        return size / getBoxRows(size);
    }

    public static int[][] copy(int[][] source) {
        int[][] result = new int[source.length][];
        for (int i = 0; i < source.length; i++) result[i] = source[i].clone();
        return result;
    }
}
