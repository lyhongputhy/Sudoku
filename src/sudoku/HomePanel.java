package sudoku;

import javax.swing.*;
import java.awt.*;

public class HomePanel extends UiTheme.GradientPanel {
    private final SudokuFrame frame;
    private final JLabel welcome = new JLabel();
    private final JButton continueGame = UiTheme.button("CONTINUE GAME", false);

    public HomePanel(SudokuFrame frame) {
        this.frame = frame;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(42, 70, 42, 70));

        JLabel eyebrow = UiTheme.label("MINDFUL PLAY • SHARPEN YOUR FOCUS", new Font("SansSerif", Font.BOLD, 13), UiTheme.CYAN);
        eyebrow.setHorizontalAlignment(SwingConstants.CENTER);
        JLabel title = new JLabel("SUDOKU", SwingConstants.CENTER);
        title.setFont(UiTheme.TITLE);
        title.setForeground(UiTheme.TEXT);
        welcome.setHorizontalAlignment(SwingConstants.CENTER);
        welcome.setFont(UiTheme.BODY);
        welcome.setForeground(UiTheme.MUTED);

        JPanel card = UiTheme.card();
        card.setLayout(new GridLayout(5, 1, 0, 10));
        JButton newGame = UiTheme.button("NEW GAME", true);
        JButton profile = UiTheme.button("PLAYER PROFILE", false);
        JButton howTo = UiTheme.button("HOW TO PLAY", false);
        JButton exit = UiTheme.button("EXIT", false);
        newGame.addActionListener(e -> frame.showDifficulty());
        continueGame.addActionListener(e -> frame.continueGame());
        profile.addActionListener(e -> frame.editProfile());
        howTo.addActionListener(e -> JOptionPane.showMessageDialog(frame,
                "Fill every row, column, and box with each number exactly once.\n" +
                "Use Pencil for notes. Erase removes your selected answer.\n" +
                "Hints are limited. The timer starts when a game begins.",
                "How to Play", JOptionPane.INFORMATION_MESSAGE));
        exit.addActionListener(e -> System.exit(0));
        card.add(newGame); card.add(continueGame); card.add(profile); card.add(howTo); card.add(exit);

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        eyebrow.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(eyebrow);
        content.add(Box.createVerticalStrut(10));
        content.add(title);
        content.add(Box.createVerticalStrut(12));
        content.add(welcome);
        content.add(Box.createVerticalStrut(34));
        content.add(card);
        add(content);
    }

    public void refresh(PlayerProfile profile) {
        if (profile == null) welcome.setText("Take a breath. Find the pattern. Make your move.");
        else welcome.setText("Welcome back, " + profile.getName() + ". Your next puzzle is waiting.");
        continueGame.setEnabled(frame.canContinueGame());
        continueGame.setToolTipText(frame.canContinueGame() ? "Return to your current puzzle" : "Start a new game to enable this");
    }
}
