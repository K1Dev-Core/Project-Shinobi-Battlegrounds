import java.awt.*;
import javax.swing.*;

public class Game extends JPanel implements Runnable {

    private final Player player;
    private final Bomb bomb;
    private final AnimationManager animationManager;
    private final GameRenderer renderer;
    private final MainMenu mainMenu;
    private final TitleScreen titleScreen;
    private final SoundManager soundManager;
    
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private boolean gameStarted = false;
    private long gameStartTime = 0;
    private static final long MENU_PROTECTION_TIME = 3000; 
    private boolean showingTitle = true;
    private boolean hasPlayedStartAnimation = false;
    private boolean hasPlayedRespawnSound = false;

    public Game() {
        animationManager = new AnimationManager();
        player = new Player(animationManager);
        bomb = new Bomb(animationManager);
        renderer = new GameRenderer();
        mainMenu = new MainMenu(this);
        titleScreen = new TitleScreen(this);
        soundManager = new SoundManager();
        
        setPreferredSize(GameSettings.WINDOW_SIZE);
        setBackground(Color.BLACK);
        setFocusable(true);
        
        addKeyListener(titleScreen);
        addMouseListener(titleScreen);

        addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent e) {
                if (!gameStarted) {
            
                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE) {
                        long currentTime = System.currentTimeMillis();
                        if (currentTime - gameStartTime < MENU_PROTECTION_TIME) {
                            return; 
                        }
                    }
                    return;
                }
                
                if (!player.isAttacking()) {
                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_A) {
                        movingLeft = true;
                        player.setFacingRight(false);
                    }
                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_D) {
                        movingRight = true;
                        player.setFacingRight(true);
                    }
                }

                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE) {
                    player.startAttack(System.currentTimeMillis());
                }
            }

            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                if (!gameStarted) return;
                
                if (!player.isAttacking()) {
                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_A) {
                        movingLeft = false;
                    }
                    if (e.getKeyCode() == java.awt.event.KeyEvent.VK_D) {
                        movingRight = false;
                    }
                }
                
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_SPACE) {
                    player.stopAttackInput();
                }
                
                if (e.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    if (gameStarted) {
                        gameStarted = false;
                        hasPlayedStartAnimation = false;
                        hasPlayedRespawnSound = false;
                        showMainMenu();
                    }
                }
            }
        });

        new Thread(this).start();
    }

    @Override
    public void run() {
        while (true) {
            long now = System.currentTimeMillis();

            if (gameStarted) {
                boolean wasAttacking = player.isAttacking();
                player.update(now, player.isAttacking(), movingLeft, movingRight);
                
                if (wasAttacking && !player.isAttacking()) {
                    movingLeft = false;
                    movingRight = false;
                }
                
                player.updateStartAnimation(now);
                bomb.update(now, player);
                
                long elapsedSinceStart = now - gameStartTime;
                if (elapsedSinceStart >= 1000 && !hasPlayedStartAnimation && !hasPlayedRespawnSound) {
                    soundManager.stopBackgroundMusic();
                    player.startGameAnimation();
                    bomb.playRespawnSound();
                    hasPlayedStartAnimation = true;
                    hasPlayedRespawnSound = true;
                }
            }

            repaint();

            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if (showingTitle) {
            titleScreen.paintComponent(g);
            return;
        }
        
        if (!gameStarted) {
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
            String instruction = "Click Anywhere to Start";
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
            
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));
            FontMetrics escMetrics = g2d.getFontMetrics();
            String escText = "ESC - Back to Menu";
            int escX = centerX - escMetrics.stringWidth(escText) / 2;
            int escY = centerY + 130;
            g2d.drawString(escText, escX, escY);
            return;
        }
        
        Graphics2D g2d = (Graphics2D) g;
        renderer.setGraphics(g2d);

        renderer.drawBackground(getWidth(), getHeight());
        
        if (player.isPlayingStartAnimation()) {
            renderer.drawPlayerStartAnimation(player);
        } else {
            renderer.drawPlayer(player);
            renderer.drawRasengan(player);
        }
        
        renderer.drawBomb(bomb);
        
        renderer.drawPlayerHealthBar(player, getWidth());
        renderer.drawBombHealthBar(bomb, getWidth());
        renderer.drawVS(getWidth());
        
        long currentTime = System.currentTimeMillis();
        
        if (!player.canAttack(currentTime)) {
            renderer.drawCooldownBar(player.getRemainingCooldown(currentTime), GameSettings.ATTACK_COOLDOWN);
        } else {
            long remainingAttackTime = player.getRemainingAttackTime(currentTime);
            if (remainingAttackTime > 0) {
                renderer.drawAttackBar(remainingAttackTime, GameSettings.MAX_ATTACK_DURATION);
            }
        }
    }

    public void showMainMenu() {
        showingTitle = false;
        soundManager.startBackgroundMusic();
        removeKeyListener(titleScreen);
        removeMouseListener(titleScreen);
        addKeyListener(mainMenu);
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!gameStarted) {
                    startGame();
                }
            }
        });
        requestFocus();
    }
    
    public void startGame() {
        gameStarted = true;
        gameStartTime = System.currentTimeMillis();
        bomb.startNewGame(gameStartTime);
        requestFocus();
    }
    
    public static void main(String[] args) {
        JFrame f = new JFrame("Project-Shinobi-Battlegrounds");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Game game = new Game();
        f.add(game);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setResizable(false);
        f.setVisible(true);
        game.requestFocus();
    }
}
