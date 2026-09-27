package sudoku;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.*;

public class GamePanel extends UiTheme.GradientPanel {
    private final SudokuFrame frame;
    private final PlayerProfile profile;
    private final Difficulty difficulty;
    private final ProfileManager profileManager;
    private final SudokuGame game;
    private final JButton[][] cells;
    private final JLabel timerLabel = new JLabel("00:00");
    private final JLabel hintsLabel = new JLabel();
    private final JLabel mistakesLabel = new JLabel();
    private final JLabel statusLabel = new JLabel("Choose a square, then make your move.");
    private final javax.swing.Timer timer;
    private final long startTime = System.currentTimeMillis();
    private int selectedRow = -1, selectedCol = -1;
    private boolean pencilMode = false;
    private boolean completed = false;

    public GamePanel(SudokuFrame frame, PlayerProfile profile, Difficulty difficulty, ProfileManager profileManager) {
        this.frame = frame;
        this.profile = profile;
        this.difficulty = difficulty;
        this.profileManager = profileManager;
        this.game = new SudokuGame(difficulty);
        this.cells = new JButton[game.getSize()][game.getSize()];

        setLayout(new BorderLayout(16, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 32, 20, 32));

        add(buildTop(), BorderLayout.NORTH);
        add(buildBoard(), BorderLayout.CENTER);
        add(buildControls(), BorderLayout.SOUTH);

        timer = new javax.swing.Timer(1000, e -> updateTimer());
        timer.start();
        updateHints();
        refreshBoard();
    }

    private JPanel buildTop() {
        JPanel top = UiTheme.card();
        top.setLayout(new BorderLayout());
        JLabel title = new JLabel("PUZZLE  /  " + difficulty.getLabel().toUpperCase() + "  " + difficulty.getSize() + "×" + difficulty.getSize());
        title.setFont(UiTheme.HEADING);
        title.setForeground(UiTheme.TEXT);
        timerLabel.setFont(new Font("Monospaced", Font.BOLD, 22));
        timerLabel.setForeground(UiTheme.GOLD);
        hintsLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        hintsLabel.setForeground(UiTheme.CYAN);
        mistakesLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        mistakesLabel.setForeground(UiTheme.CORAL);
        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 18, 0));
        right.setOpaque(false);
        right.add(mistakesLabel); right.add(hintsLabel); right.add(timerLabel);
        top.add(title, BorderLayout.WEST); top.add(right, BorderLayout.EAST);
        return top;
    }

    private JPanel buildBoard() {
        int n = game.getSize();
        int boxRows = SudokuGenerator.getBoxRows(n);
        int boxCols = SudokuGenerator.getBoxCols(n);
        JPanel boardPanel = new JPanel(new GridLayout(n, n));
        boardPanel.setBackground(UiTheme.INK);
        boardPanel.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(UiTheme.CYAN, 4),
                new LineBorder(new Color(255, 255, 255, 70), 1)));

        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                final int rr = r, cc = c;
                JButton button = new JButton();
                button.setMargin(new Insets(0, 0, 0, 0));
                button.setFont(new Font("SansSerif", Font.BOLD, n == 16 ? 13 : 22));
                button.setFocusPainted(false);
                button.setContentAreaFilled(true);
                button.setOpaque(true);
                button.addActionListener(e -> selectCell(rr, cc));
                button.setBorder(BorderFactory.createMatteBorder(
                        r % boxRows == 0 ? 2 : 1,
                        c % boxCols == 0 ? 2 : 1,
                        (r + 1) % boxRows == 0 ? 2 : 1,
                        (c + 1) % boxCols == 0 ? 2 : 1,
                        new Color(153, 104, 68)));
                cells[r][c] = button;
                boardPanel.add(button);
            }
        }

        // The board owns the available play space. GridBagLayout previously kept
        // it near its tiny minimum size on some look-and-feels.
        JPanel stage = new JPanel(null) {
            @Override
            public void doLayout() {
                int padding = 8;
                int side = Math.max(0, Math.min(getWidth() - padding * 2, getHeight() - padding * 2));
                int x = (getWidth() - side) / 2;
                int y = (getHeight() - side) / 2;
                boardPanel.setBounds(x, y, side, side);
            }
        };
        stage.setOpaque(false);
        stage.setPreferredSize(new Dimension(620, 620));
        stage.add(boardPanel);
        boardPanel.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                SwingUtilities.invokeLater(GamePanel.this::refreshBoard);
            }
        });
        return stage;
    }

    private JPanel buildControls() {
        JPanel bottom = UiTheme.card();
        bottom.setLayout(new BorderLayout(8, 8));
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        buttons.setOpaque(false);
        JButton undo = UiTheme.button("UNDO", false);
        JButton erase = UiTheme.button("ERASE", false);
        JButton pencil = UiTheme.button("PENCIL  OFF", false);
        JButton hint = UiTheme.button("HINT", true);
        JButton check = UiTheme.button("CHECK", false);
        JButton newGame = UiTheme.button("NEW PUZZLE", false);
        JButton home = UiTheme.button("HOME", false);
        undo.addActionListener(e -> { if (game.undo()) { refreshBoard(); statusLabel.setText("Last move undone."); } });
        erase.addActionListener(e -> eraseSelected());
        pencil.addActionListener(e -> { pencilMode = !pencilMode; pencil.setText("PENCIL  " + (pencilMode ? "ON" : "OFF")); pencil.setBackground(pencilMode ? UiTheme.GOLD : UiTheme.PANEL_LIGHT); pencil.setForeground(pencilMode ? UiTheme.INK : UiTheme.TEXT); });
        hint.addActionListener(e -> useHint());
        check.addActionListener(e -> checkSelected());
        newGame.addActionListener(e -> frame.startGame(difficulty));
        home.addActionListener(e -> { stopGame(); frame.showHome(); });
        buttons.add(undo); buttons.add(erase); buttons.add(pencil); buttons.add(hint); buttons.add(check); buttons.add(newGame); buttons.add(home);

        JPanel keypad = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        keypad.setOpaque(false);
        for (int i = 1; i <= game.getSize(); i++) {
            final int value = i;
            JButton key = UiTheme.button(displayValue(i), false);
            // Number keys need compact insets; the normal action-button padding
            // clips 16×16 labels into an ellipsis.
            key.setBorder(BorderFactory.createEmptyBorder(8, 3, 8, 3));
            key.setFont(new Font("SansSerif", Font.BOLD, game.getSize() == 16 ? 14 : 16));
            key.setPreferredSize(new Dimension(game.getSize() == 16 ? 46 : 50, 38));
            key.addActionListener(e -> enterNumber(value));
            keypad.add(key);
        }

        bottom.add(buttons, BorderLayout.NORTH);
        bottom.add(keypad, BorderLayout.CENTER);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        statusLabel.setForeground(UiTheme.MUTED);
        bottom.add(statusLabel, BorderLayout.SOUTH);
        return bottom;
    }

    private void selectCell(int r, int c) {
        selectedRow = r; selectedCol = c;
        refreshBoard();
        statusLabel.setText(game.isGiven(r, c) ? "This is a fixed clue." : "Selected cell: " + (r + 1) + ", " + (c + 1));
    }

    private void eraseSelected() {
        if (!hasSelection() || game.isGiven(selectedRow, selectedCol)) return;
        game.erase(selectedRow, selectedCol);
        refreshBoard();
        statusLabel.setText("Cell erased.");
    }

    private void useHint() {
        if (game.getHintsLeft() <= 0) {
            statusLabel.setText("No hints left.");
            return;
        }
        if (game.useHint()) {
            refreshBoard(); updateHints();
            if (game.isComplete()) finishGame();
            else statusLabel.setText("Hint used. Keep going!");
        }
    }

    private void checkSelected() {
        if (!hasSelection() || game.isGiven(selectedRow, selectedCol)) return;
        int value = game.getValue(selectedRow, selectedCol);
        if (value == 0) statusLabel.setText("This cell is empty.");
        else if (game.isCorrect(selectedRow, selectedCol)) statusLabel.setText("Correct!");
        else statusLabel.setText("Not correct yet. Try another number.");
    }

    private void enterNumber(int value) {
        if (!hasSelection() || game.isGiven(selectedRow, selectedCol)) return;
        if (pencilMode) {
            game.togglePencil(selectedRow, selectedCol, value);
        } else {
            game.setValue(selectedRow, selectedCol, value);
        }
        refreshBoard();
        if (!pencilMode && value != 0 && !game.isCorrect(selectedRow, selectedCol)) {
            statusLabel.setText("Mistake — that number is not correct. Try again.");
        } else if (!pencilMode) {
            statusLabel.setText("Nice move — that number is correct.");
        }
        if (game.isComplete()) finishGame();
    }

    private boolean hasSelection() { return selectedRow >= 0 && selectedCol >= 0; }

    private void refreshBoard() {
        int n = game.getSize();
        for (int r = 0; r < n; r++) {
            for (int c = 0; c < n; c++) {
                JButton b = cells[r][c];
                int value = game.getValue(r, c);
                boolean selected = r == selectedRow && c == selectedCol;
                boolean peer = r == selectedRow || c == selectedCol;
                boolean mistake = !game.isGiven(r, c) && value != 0 && !game.isCorrect(r, c);
                if (value != 0) {
                    b.setText(displayValue(value));
                    b.setFont(new Font("SansSerif", Font.BOLD, cellFontSize(b, n, false)));
                } else {
                    b.setText(pencilText(r, c));
                    b.setFont(new Font("SansSerif", Font.PLAIN, cellFontSize(b, n, true)));
                }
                boolean conflict = game.hasConflict(r, c);
                boolean badEntry = mistake || conflict;
                b.setForeground(badEntry ? new Color(255, 231, 216) : selected ? UiTheme.INK : game.isGiven(r, c) ? new Color(255, 219, 147) : value == 0 ? new Color(224, 193, 159) : new Color(255, 239, 207));
                b.setBackground(badEntry ? new Color(161, 73, 62) : selected ? UiTheme.GOLD : peer ? new Color(128, 83, 53) : game.isGiven(r, c) ? new Color(86, 55, 37) : new Color(105, 68, 46));
                b.setOpaque(true);
            }
        }
        int mistakes = countMistakes();
        mistakesLabel.setText("✕  " + mistakes + " " + (mistakes == 1 ? "MISTAKE" : "MISTAKES"));
    }

    private int countMistakes() {
        int mistakes = 0;
        for (int r = 0; r < game.getSize(); r++) {
            for (int c = 0; c < game.getSize(); c++) {
                if (!game.isGiven(r, c) && game.getValue(r, c) != 0 && !game.isCorrect(r, c)) mistakes++;
            }
        }
        return mistakes;
    }

    private String pencilText(int r, int c) {
        StringBuilder s = new StringBuilder("<html><center><span style='color:#E0C19F'>");
        int n = game.getSize();
        for (int i = 1; i <= n; i++) if (game.isPencil(r, c, i)) s.append(displayValue(i)).append(' ');
        return s.append("</span></center></html>").toString();
    }

    private int cellFontSize(JButton cell, int size, boolean pencil) {
        int edge = Math.min(cell.getWidth(), cell.getHeight());
        if (edge <= 0) return pencil ? (size == 16 ? 10 : 12) : (size == 16 ? 13 : 22);
        if (pencil) return Math.max(9, Math.min(size == 16 ? 15 : 18, edge / (size == 16 ? 5 : 4)));
        return Math.max(size == 16 ? 13 : 20, Math.min(size == 16 ? 24 : 46, edge / 2));
    }

    private String displayValue(int value) {
        return value <= 9 ? String.valueOf(value) : String.valueOf((char)('A' + value - 10));
    }

    private void updateHints() { hintsLabel.setText("✦  " + game.getHintsLeft() + " HINTS"); }

    private void updateTimer() {
        long seconds = (System.currentTimeMillis() - startTime) / 1000;
        timerLabel.setText(String.format("%02d:%02d", seconds / 60, seconds % 60));
    }

    /** Stops the Swing timer before the panel is replaced by a new game. */
    public void stopGame() {
        timer.stop();
    }

    public boolean canResume() {
        return !completed;
    }

    public void resumeGame() {
        if (!completed) timer.start();
    }

    private void finishGame() {
        completed = true;
        timer.stop();
        long seconds = (System.currentTimeMillis() - startTime) / 1000;
        boolean newRecord = profile.updateBestTime(difficulty.getLabel(), seconds);
        profileManager.save(profile);
        String record = newRecord ? "\nNew personal best!" : "";
        JOptionPane.showMessageDialog(frame,
                String.format("Congratulations, %s!\nYou solved %s in %02d:%02d.%s",
                        profile.getName(), difficulty.getLabel(), seconds / 60, seconds % 60, record),
                "Sudoku Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    // Keyboard support: 1-9 and A-G for 16×16.
    @Override
    public void addNotify() {
        super.addNotify();
        setFocusable(true);
        requestFocusInWindow();
        getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("BACK_SPACE"), "erase");
        getActionMap().put("erase", new AbstractAction() { public void actionPerformed(java.awt.event.ActionEvent e) { eraseSelected(); } });
        for (int i = 1; i <= 16; i++) {
            final int value = i;
            String key = value <= 9 ? String.valueOf(value) : String.valueOf((char)('A' + value - 10));
            getInputMap(WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), "num" + value);
            getActionMap().put("num" + value, new AbstractAction() { public void actionPerformed(java.awt.event.ActionEvent e) { if (value <= game.getSize()) enterNumber(value); } });
        }
    }
}
