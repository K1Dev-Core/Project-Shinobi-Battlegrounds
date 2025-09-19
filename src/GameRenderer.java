import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class GameRenderer {
    
    private BufferedImage background;
    private Graphics2D g2d;
    
    public GameRenderer() {
        loadBackground();
    }
    
    private void loadBackground() {
        try {
            background = ImageIO.read(new File(GameSettings.BACKGROUND_PATH));
        } catch (Exception e) {
        }
    }
    
    public void setGraphics(Graphics2D g2d) {
        this.g2d = g2d;
    }
    
    public void drawBackground(int width, int height) {
        if (background != null) {
            g2d.drawImage(background, 0, 0, width, height, null);
        }
    }
    
    public void drawPlayer(Player player) {
        BufferedImage frame = player.getCurrentFrame();
        if (frame != null) {
            int drawW = player.getDrawWidth();
            int drawH = player.getDrawHeight();
            
            if (player.isFacingRight()) {
                g2d.drawImage(frame, player.getX(), player.getY(), drawW, drawH, null);
            } else {
                g2d.drawImage(frame,
                        player.getX() + drawW,
                        player.getY(),
                        -drawW,
                        drawH,
                        null);
            }
        }
    }
    
    public void drawRasengan(Player player) {
        if (player.isShowRasengan()) {
            BufferedImage rasenganEffect = player.getRasenganFrame();
            if (rasenganEffect != null) {
                g2d.drawImage(rasenganEffect, 
                        player.getRasenganX(), 
                        player.getRasenganY(), 
                        GameSettings.RASENGAN_SIZE, 
                        GameSettings.RASENGAN_SIZE, null);
            }
        }
    }
    
    public void drawBomb(Bomb bomb) {
        if (!bomb.isDestroyed() && bomb.shouldDraw()) {
            BufferedImage bombFrame = bomb.getCurrentFrame();
            if (bombFrame != null) {
                int bombDrawW = bomb.getDrawWidth();
                int bombDrawH = bomb.getDrawHeight();
                int drawX = bomb.getDrawX();
                int drawY = bomb.getDrawY();
                
                if (bomb.isFacingRight()) {
                    g2d.drawImage(bombFrame, drawX, drawY, bombDrawW, bombDrawH, null);
                } else {
                    g2d.drawImage(bombFrame,
                            drawX + bombDrawW,
                            drawY,
                            -bombDrawW,
                            bombDrawH,
                            null);
                }
                
            }
        }
    }
    
    
    public void drawPlayerHealthBar(Player player, int screenWidth) {
        int barWidth = 400;
        int barHeight = 30;
        int barX = 30;
        int barY = 10;
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(barX, barY, barWidth, barHeight);
        
        g2d.setColor(Color.RED);
        g2d.fillRect(barX, barY, barWidth, barHeight);
        
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(barX, barY, barWidth, barHeight);
    }
    
    public void drawBombHealthBar(Bomb bomb, int screenWidth) {
        int barWidth = 400;
        int barHeight = 30;
        int barX = screenWidth - barWidth - 30;
        int barY = 10;
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(barX, barY, barWidth, barHeight);
        
        if (bomb.isDead()) {
            long remainingTime = GameSettings.BOMB_SPAWN_DELAY - (System.currentTimeMillis() - bomb.getLastSpawnTime());
            if (remainingTime > 0) {
                int respawnWidth = (int)((double)(GameSettings.BOMB_SPAWN_DELAY - remainingTime) / GameSettings.BOMB_SPAWN_DELAY * barWidth);
                g2d.setColor(Color.ORANGE);
                g2d.fillRect(barX, barY, respawnWidth, barHeight);
            }
        } else {
          
            int smoothHealthWidth = bomb.getSmoothHealthWidth();
            g2d.setColor(Color.RED);
            g2d.fillRect(barX, barY, smoothHealthWidth, barHeight);
        }
        
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(barX, barY, barWidth, barHeight);
    }
    
    public void drawCooldownBar(long remainingTime, long maxCooldown) {
        int barWidth = 400;
        int barHeight = 20;
        int barX = 30;
        int barY = 50;
        
        g2d.setColor(Color.WHITE);
        g2d.fillRect(barX, barY, barWidth, barHeight);
        
        int currentWidth = (int)((double)(maxCooldown - remainingTime) / maxCooldown * barWidth);
        g2d.setColor(Color.ORANGE);
        g2d.fillRect(barX, barY, currentWidth, barHeight);
        
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(barX, barY, barWidth, barHeight);
    }
    
    public void drawAttackBar(long remainingTime, long maxAttackTime) {
        if (remainingTime > 0) {
            int barWidth = 400;
            int barHeight = 20;
            int barX = 30;
            int barY = 50;
            
            g2d.setColor(Color.WHITE);
            g2d.fillRect(barX, barY, barWidth, barHeight);
            
            int currentWidth = (int)((double)(maxAttackTime - remainingTime) / maxAttackTime * barWidth);
            g2d.setColor(Color.YELLOW);
            g2d.fillRect(barX, barY, currentWidth, barHeight);
            
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(barX, barY, barWidth, barHeight);
        }
    }
    
    public void drawVS(int screenWidth) {
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        String vsText = "VS";
        int textX = (screenWidth - fm.stringWidth(vsText)) / 2;
        int textY = 25;
        g2d.drawString(vsText, textX, textY);
    }
    
    public void drawPlayerStartAnimation(Player player) {
        if (player.isPlayingStartAnimation()) {
            BufferedImage startFrame = player.getCurrentStartFrame();
            if (startFrame != null) {
                int drawWidth = (int)(startFrame.getWidth() * player.getScale());
                int drawHeight = (int)(startFrame.getHeight() * player.getScale());
                int drawX = player.getX();
                int drawY = player.getY();
                
                g2d.drawImage(startFrame, drawX, drawY, drawWidth, drawHeight, null);
            }
        }
    }
}
