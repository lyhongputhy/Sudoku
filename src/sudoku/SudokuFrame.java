package sudoku;

import javax.swing.*;
import java.awt.*;

public class SudokuFrame extends JFrame {
    private final CardLayout cards = new CardLayout();
    private final JPanel root = new JPanel(cards);
    private final ProfileManager profileManager = new ProfileManager();
    private PlayerProfile profile;
    private HomePanel homePanel;
    private DifficultyPanel difficultyPanel;
    private GamePanel gamePanel;

    public SudokuFrame() {
        setTitle("SUDOKU • Milk Tea Edition");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(980, 720));
        setSize(1180, 820);
        setLocationRelativeTo(null);

        homePanel = new HomePanel(this);
        difficultyPanel = new DifficultyPanel(this);
        root.add(homePanel, "home");
        root.add(difficultyPanel, "difficulty");
        setContentPane(root);
    }

    public void startWithPlayer(String name) {
        String clean = name == null ? "" : name.trim();
        if (clean.isEmpty()) clean = "Player";
        profile = profileManager.loadOrCreate(clean);
        homePanel.refresh(profile);
    }

    public PlayerProfile getProfile() {
        if (profile == null) startWithPlayer("Player");
        return profile;
    }

    public void showHome() {
        homePanel.refresh(profile);
        cards.show(root, "home");
    }

    public void showDifficulty() {
        cards.show(root, "difficulty");
    }

    public boolean canContinueGame() {
        return gamePanel != null && gamePanel.canResume();
    }

    public void continueGame() {
        if (!canContinueGame()) return;
        gamePanel.resumeGame();
        cards.show(root, "game");
    }

    public void startGame(Difficulty difficulty) {
        if (gamePanel != null) {
            gamePanel.stopGame();
            root.remove(gamePanel);
        }
        gamePanel = new GamePanel(this, getProfile(), difficulty, profileManager);
        root.add(gamePanel, "game");
        cards.show(root, "game");
        revalidate();
        repaint();
    }

    public void editProfile() {
        String current = getProfile().getName();
        String name = JOptionPane.showInputDialog(this, "Enter your player name:", current);
        if (name != null && !name.trim().isEmpty()) {
            profile = profileManager.loadOrCreate(name.trim());
            homePanel.refresh(profile);
        }
    }
}
