package sudoku;

public enum Difficulty {
    BEGINNER("Beginner", 6, 12, 3),
    EASY("Easy", 9, 38, 4),
    MEDIUM("Medium", 9, 32, 3),
    HARD("Hard", 9, 27, 2),
    EXPERT("Expert", 9, 23, 2),
    EXTREME("Extreme", 16, 150, 1);

    private final String label;
    private final int size;
    private final int clues;
    private final int hints;

    Difficulty(String label, int size, int clues, int hints) {
        this.label = label;
        this.size = size;
        this.clues = clues;
        this.hints = hints;
    }

    public String getLabel() { return label; }
    public int getSize() { return size; }
    public int getClues() { return clues; }
    public int getHints() { return hints; }

    @Override
    public String toString() {
        return label;
    }
}
