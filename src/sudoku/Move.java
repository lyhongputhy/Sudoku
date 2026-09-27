package sudoku;

public record Move(int row, int col, int oldValue, int newValue, boolean oldPencil, boolean newPencil) {}
