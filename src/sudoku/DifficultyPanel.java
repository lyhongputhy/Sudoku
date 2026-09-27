package sudoku;

import javax.swing.*;
import java.awt.*;

public class DifficultyPanel extends UiTheme.GradientPanel {
    public DifficultyPanel(SudokuFrame frame) {
        setLayout(new BorderLayout(22, 22));
        setBorder(BorderFactory.createEmptyBorder(38, 78, 38, 78));
        JLabel title = new JLabel("CHOOSE YOUR CHALLENGE", SwingConstants.CENTER);
        title.setFont(UiTheme.HEADING);
        title.setForeground(UiTheme.TEXT);
        add(title, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(3, 2, 18, 18));
        grid.setOpaque(false);
        for (Difficulty d : Difficulty.values()) {
            JButton button = UiTheme.button("<html><center><span style='font-size:18px'>" + d.getLabel() + "</span><br>" + d.getSize() + "×" + d.getSize() +
                    " &nbsp;•&nbsp; " + d.getHints() + " hints<br><span style='font-size:10px'>PLAY THIS MODE</span></center></html>", d == Difficulty.BEGINNER);
            button.setPreferredSize(new Dimension(240, 110));
            button.addActionListener(e -> frame.startGame(d));
            grid.add(button);
        }
        add(grid, BorderLayout.CENTER);

        JButton back = UiTheme.button("← BACK TO HOME", false);
        back.addActionListener(e -> frame.showHome());
        add(back, BorderLayout.SOUTH);
    }
}
