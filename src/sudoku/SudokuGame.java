package sudoku;

import java.util.*;

public class SudokuGame {
    private final Difficulty difficulty;
    private final int size;
    private final int[][] solution;
    private final int[][] puzzle;
    private final int[][] board;
    private final boolean[][][] pencil;
    private final Deque<Move> history = new ArrayDeque<>();
    private int hintsLeft;

    public SudokuGame(Difficulty difficulty) {
        this.difficulty = difficulty;
        this.size = difficulty.getSize();
        SudokuGenerator generator = new SudokuGenerator();
        solution = generator.createSolvedBoard(size);
        puzzle = generator.createPuzzle(solution, difficulty.getClues());
        board = SudokuGenerator.copy(puzzle);
        pencil = new boolean[size][size][size + 1];
        hintsLeft = difficulty.getHints();
    }

    public Difficulty getDifficulty() { return difficulty; }
    public int getSize() { return size; }
    public int[][] getPuzzle() { return SudokuGenerator.copy(puzzle); }
    public int getValue(int r, int c) { return board[r][c]; }
    public boolean isGiven(int r, int c) { return puzzle[r][c] != 0; }
    public boolean isPencil(int r, int c, int value) { return pencil[r][c][value]; }
    public int getHintsLeft() { return hintsLeft; }

    public boolean setValue(int r, int c, int value) {
        if (isGiven(r, c) || value < 0 || value > size) return false;
        int old = board[r][c];
        if (old == value) return true;
        board[r][c] = value;
        history.push(new Move(r, c, old, value, false, false));
        if (value != 0) clearPencils(r, c);
        return true;
    }

    public void erase(int r, int c) { setValue(r, c, 0); }

    public void togglePencil(int r, int c, int value) {
        if (isGiven(r, c) || value < 1 || value > size || board[r][c] != 0) return;
        boolean old = pencil[r][c][value];
        pencil[r][c][value] = !old;
        history.push(new Move(r, c, 0, 0, old, !old));
    }

    private void clearPencils(int r, int c) {
        Arrays.fill(pencil[r][c], false);
    }

    public boolean undo() {
        if (history.isEmpty()) return false;
        Move m = history.pop();
        if (m.oldValue() != m.newValue()) {
            board[m.row()][m.col()] = m.oldValue();
            return true;
        }
        pencil[m.row()][m.col()][m.newValue()] = m.oldPencil();
        return true;
    }

    public boolean isCorrect(int r, int c) {
        int value = board[r][c];
        return value != 0 && value == solution[r][c];
    }

    /** True when a filled square duplicates a value in its row, column, or box. */
    public boolean hasConflict(int r, int c) {
        int value = board[r][c];
        if (value == 0) return false;
        for (int i = 0; i < size; i++) {
            if (i != c && board[r][i] == value) return true;
            if (i != r && board[i][c] == value) return true;
        }
        int boxRows = SudokuGenerator.getBoxRows(size);
        int boxCols = SudokuGenerator.getBoxCols(size);
        int firstRow = r / boxRows * boxRows;
        int firstCol = c / boxCols * boxCols;
        for (int rr = firstRow; rr < firstRow + boxRows; rr++)
            for (int cc = firstCol; cc < firstCol + boxCols; cc++)
                if ((rr != r || cc != c) && board[rr][cc] == value) return true;
        return false;
    }

    public boolean isComplete() {
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (board[r][c] != solution[r][c]) return false;
            }
        }
        return true;
    }

    public boolean useHint() {
        if (hintsLeft <= 0) return false;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                if (!isGiven(r, c) && board[r][c] != solution[r][c]) {
                    setValue(r, c, solution[r][c]);
                    hintsLeft--;
                    return true;
                }
            }
        }
        return false;
    }

    public int[][] getCandidates(int r, int c) {
        boolean[] used = new boolean[size + 1];
        for (int i = 0; i < size; i++) {
            if (board[r][i] > 0) used[board[r][i]] = true;
            if (board[i][c] > 0) used[board[i][c]] = true;
        }
        int boxRows = SudokuGenerator.getBoxRows(size);
        int boxCols = SudokuGenerator.getBoxCols(size);
        int br = (r / boxRows) * boxRows;
        int bc = (c / boxCols) * boxCols;
        for (int rr = br; rr < br + boxRows; rr++)
            for (int cc = bc; cc < bc + boxCols; cc++)
                if (board[rr][cc] > 0) used[board[rr][cc]] = true;
        int[][] candidates = new int[1][size];
        for (int i = 1; i <= size; i++) candidates[0][i - 1] = used[i] ? 0 : i;
        return candidates;
    }
}
