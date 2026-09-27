package sudoku;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Shared visual language for the game's screens and controls. */
public final class UiTheme {
    // Milk-tea palette: fresh cream, honey caramel, and roasted cocoa.
    public static final Color INK = new Color(62, 40, 28);
    public static final Color PANEL = new Color(255, 247, 234);
    public static final Color PANEL_LIGHT = new Color(235, 207, 169);
    public static final Color TEXT = new Color(62, 40, 28);
    public static final Color MUTED = new Color(133, 100, 76);
    public static final Color CYAN = new Color(186, 113, 68);
    public static final Color GOLD = new Color(222, 166, 91);
    public static final Color CORAL = new Color(177, 81, 69);
    public static final Font TITLE = new Font("SansSerif", Font.BOLD, 44);
    public static final Font HEADING = new Font("SansSerif", Font.BOLD, 25);
    public static final Font BODY = new Font("SansSerif", Font.PLAIN, 16);

    private UiTheme() { }

    public static Border cardBorder() {
        return BorderFactory.createEmptyBorder(20, 24, 20, 24);
    }

    public static JPanel card() {
        JPanel card = new RoundedPanel(PANEL, 22);
        card.setBorder(cardBorder());
        return card;
    }

    public static JButton button(String text, boolean primary) {
        JButton button = new RoundedButton(text, primary ? CYAN : PANEL_LIGHT);
        button.setFont(new Font("SansSerif", Font.BOLD, 15));
        button.setForeground(primary ? INK : TEXT);
        button.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    public static JLabel label(String text, Font font, Color color) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(color);
        return label;
    }

    public static class GradientPanel extends JPanel {
        public GradientPanel() { setOpaque(false); }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setPaint(new GradientPaint(0, 0, new Color(255, 245, 227), getWidth(), getHeight(), new Color(224, 176, 126)));
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(new Color(255, 255, 255, 135));
            g.fillOval(-getWidth() / 5, -getHeight() / 4, getWidth() * 3 / 4, getHeight() * 3 / 4);
            g.setColor(new Color(137, 82, 49, 38));
            g.fillOval(getWidth() / 2, getHeight() / 2, getWidth() * 2 / 3, getHeight() * 2 / 3);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static class RoundedPanel extends JPanel {
        private final Color fill;
        private final int arc;

        RoundedPanel(Color fill, int arc) {
            this.fill = fill;
            this.arc = arc;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(fill);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g.setColor(new Color(255, 255, 255, 32));
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, arc, arc);
            g.dispose();
            super.paintComponent(graphics);
        }
    }

    private static class RoundedButton extends JButton {
        private boolean hovering;

        RoundedButton(String text, Color background) {
            super(text);
            setBackground(background);
            setContentAreaFilled(false);
            setOpaque(false);
            addMouseListener(new MouseAdapter() {
                @Override public void mouseEntered(MouseEvent e) { hovering = true; repaint(); }
                @Override public void mouseExited(MouseEvent e) { hovering = false; repaint(); }
            });
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            Graphics2D g = (Graphics2D) graphics.create();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Color fill = getBackground();
            if (getModel().isPressed()) fill = fill.darker();
            else if (hovering) fill = fill.brighter();
            g.setColor(fill);
            g.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            g.setColor(new Color(255, 255, 255, hovering ? 90 : 48));
            g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 18, 18);
            g.dispose();
            super.paintComponent(graphics);
        }
    }
}
