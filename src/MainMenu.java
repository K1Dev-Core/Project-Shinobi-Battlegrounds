import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.*;

public class MainMenu extends JPanel implements KeyListener, MouseListener {
    private final Game game;
    private boolean showing = true;
    
    public MainMenu(Game game) {
        this.game = game;
        setPreferredSize(GameSettings.WINDOW_SIZE);
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        setBackground(new Color(0, 0, 0, 208));
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 64));
        FontMetrics titleMetrics = g2d.getFontMetrics();
        String title = "Project-Shinobi-Battlegrounds";
        int titleX = centerX - titleMetrics.stringWidth(title) / 2;
        int titleY = centerY - 60;
        g2d.drawString(title, titleX, titleY);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 28));
        FontMetrics instructionMetrics = g2d.getFontMetrics();
        String instruction = "Press SPACEBAR to Start";
        int instructionX = centerX - instructionMetrics.stringWidth(instruction) / 2;
        int instructionY = centerY + 20;
        g2d.drawString(instruction, instructionX, instructionY);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 18));
        FontMetrics controlsMetrics = g2d.getFontMetrics();
        String controls = "A/D - Move Left/Right  |  SPACE - Attack";
        int controlsX = centerX - controlsMetrics.stringWidth(controls) / 2;
        int controlsY = centerY + 70;
        g2d.drawString(controls, controlsX, controlsY);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 16));
        FontMetrics subtitleMetrics = g2d.getFontMetrics();
        String subtitle = "Fight the Bomb Enemy!";
        int subtitleX = centerX - subtitleMetrics.stringWidth(subtitle) / 2;
        int subtitleY = centerY + 100;
        g2d.drawString(subtitle, subtitleX, subtitleY);
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && showing) {
            showing = false;
            game.startGame();
            setVisible(false);
            requestFocus();
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {}
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    public boolean isShowing() {
        return showing;
    }
    
    @Override
    public void show() {
        showing = true;
        setVisible(true);
        requestFocus();
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (showing) {
            showing = false;
            game.startGame();
            setVisible(false);
            requestFocus();
        }
    }
    
    @Override
    public void mousePressed(MouseEvent e) {}
    
    @Override
    public void mouseReleased(MouseEvent e) {}
    
    @Override
    public void mouseEntered(MouseEvent e) {}
    
    @Override
    public void mouseExited(MouseEvent e) {}
}
