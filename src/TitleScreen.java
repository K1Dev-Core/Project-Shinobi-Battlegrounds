import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import javax.swing.*;

public class TitleScreen extends JPanel implements KeyListener, MouseListener {
    private Game game;
    private boolean showing = true;
    private long startTime;
    private float alpha = 1.0f;
    private boolean fadingOut = false;
    private AnimationManager animationManager;
    
    public TitleScreen(Game game) {
        this.game = game;
        this.animationManager = new AnimationManager();
        this.startTime = System.currentTimeMillis();
        setPreferredSize(GameSettings.WINDOW_SIZE);
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
        setBackground(Color.BLACK);
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        long currentTime = System.currentTimeMillis();
        long elapsed = currentTime - startTime;
        
        if (elapsed < 3000) {
            alpha = Math.min(1.0f, (float)elapsed / 1000.0f);
        }
        
        if (elapsed >= 8000 && !fadingOut) {
            fadingOut = true;
            startTime = currentTime;
        }
        
        animationManager.updateFogAnimation(currentTime);
        
        if (fadingOut) {
            long fadeElapsed = currentTime - startTime;
            if (fadeElapsed < 1000) {
                alpha = 1.0f - (float)fadeElapsed / 1000.0f;
            } else {
                showing = false;
                game.showMainMenu();
                return;
            }
        }
        
        Graphics2D g2d = (Graphics2D) g;
        g2d.setFont(new Font("Arial", Font.BOLD, 60));
        FontMetrics fm = g2d.getFontMetrics();
        
        String title = "Fanmade by Hex";
        int centerX = getWidth() +500;
        int centerY = getHeight() +300;
        int x = centerX - fm.stringWidth(title) / 2;
        int y = centerY;
        
        g2d.setColor(new Color(1.0f, 1.0f, 1.0f, alpha));
        g2d.drawString(title, x, y);
        
        BufferedImage fogFrame = animationManager.getCurrentFogFrame();
        if (fogFrame != null) {
            int fogX = x + fm.stringWidth(title) + 20;
            int fogY = y - fogFrame.getHeight() / 2;
            int fogWidth = fogFrame.getWidth() * 2;
            int fogHeight = fogFrame.getHeight() * 2;
            g2d.drawImage(fogFrame, fogX, fogY, fogWidth, fogHeight, null);
        }
        
        repaint();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (showing && e.getKeyCode() == KeyEvent.VK_SPACE) {
            skipToMainMenu();
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (showing) {
            skipToMainMenu();
        }
    }
    
    private void skipToMainMenu() {
        showing = false;
        game.showMainMenu();
    }
    
    @Override
    public void keyReleased(KeyEvent e) {}
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}
    
    public boolean isShowing() {
        return showing;
    }
}
