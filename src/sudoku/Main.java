package sudoku;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            UIManager.put("OptionPane.background", UiTheme.PANEL);
            UIManager.put("Panel.background", UiTheme.PANEL);
            UIManager.put("OptionPane.messageForeground", UiTheme.TEXT);
            UIManager.put("TextField.background", UiTheme.PANEL);
            UIManager.put("TextField.foreground", UiTheme.INK);
            new SudokuFrame().setVisible(true);
        });
    }
}
