import java.awt.image.BufferedImage;
import java.io.File;
import javax.sound.sampled.*;

public class Bomb {
    
    private int x, y;
    private int health;
    private final int maxHealth;
    private boolean destroyed = false;
    private boolean isHit = false;
    private boolean isBeingAttacked = false;
    private boolean hitAnimationStarted = false;
    private final boolean facingRight = false;
    private boolean blinking = false;
    private boolean isDead = false;
    private boolean deathAnimationComplete = false;
    private boolean knockedBack = false;
    private boolean lastPlayerFacingRight = true;
    private long lastDamageTime = 0;
    private float smoothHealth;
    private Clip deathSound;
    private Clip respawnSound;
    
    private int idleFrame = 0;
    private int hitFrame = 0;
    private int offsetY = 0;
    private int offsetX = 0;
    
    private long lastIdleFrameTime = 0;
    private long lastHitFrameTime = 0;
    private long lastSpawn = 0;
    private long blinkStartTime = 0;
    
    private final AnimationManager animationManager;
    
    public Bomb(AnimationManager animationManager) {
        this.animationManager = animationManager;
        this.maxHealth = GameSettings.BOMB_HEALTH;
        this.health = maxHealth;
        this.smoothHealth = maxHealth;
        this.x = GameSettings.BOMB_START_X;
        this.y = GameSettings.BOMB_START_Y;
        loadSounds();
    }
    
    private void loadSounds() {
        try {
            AudioInputStream deathAudioStream = AudioSystem.getAudioInputStream(new File("assets/sfx/0233_0000.wav"));
            deathSound = AudioSystem.getClip();
            deathSound.open(deathAudioStream);
            
            AudioInputStream respawnAudioStream = AudioSystem.getAudioInputStream(new File("assets/sfx/022A_0000.wav"));
            respawnSound = AudioSystem.getClip();
            respawnSound.open(respawnAudioStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void update(long now, Player player) {
        if (isDead) {
            if (deathAnimationComplete && now - lastSpawn >= GameSettings.BOMB_SPAWN_DELAY) {
                respawn(now);
            }
            updateAnimation(now);
            return;
        }
        
        if (destroyed) {
            if (now - lastSpawn >= GameSettings.BOMB_SPAWN_DELAY) {
                respawn(now);
            }
            return;
        }
        
        checkAttackCollision(player);
        updateSmoothHealth(now);
        updateAnimation(now);
        updateBlinking(now);
    }
    
    private void updateSmoothHealth(long now) {
       
        if (smoothHealth > health) {
            float healthDiff = smoothHealth - health;
            float smoothSpeed = 2.0f; 
            smoothHealth = Math.max(health, smoothHealth - smoothSpeed);
        } else if (smoothHealth < health) {
            smoothHealth = health; 
        }
    }
    
    private void respawn(long now) {
        health = maxHealth;
        smoothHealth = maxHealth;
        destroyed = false;
        isDead = false;
        deathAnimationComplete = false;
        x = GameSettings.BOMB_START_X + (int)(Math.random() * 200);
        y = GameSettings.PLAYER_START_Y;
        isBeingAttacked = false;
        hitAnimationStarted = false;
        isHit = false;
        offsetY = 0;
        offsetX = 0;
        blinking = true;
        blinkStartTime = now;
        knockedBack = false;
        lastPlayerFacingRight = true;
        lastSpawn = now;
    }
    
    private void checkAttackCollision(Player player) {
        if (player.isAttacking() && player.isShowRasengan()) {
            int playerCenterX = player.getCenterX();
            int playerCenterY = player.getCenterY();
            int bombCenterX = x + 100;
            int bombCenterY = y + 100;
            
            int distance = (int)Math.sqrt(Math.pow(playerCenterX - bombCenterX, 2) + Math.pow(playerCenterY - bombCenterY, 2));
            
            if (distance < GameSettings.ATTACK_RANGE) {
                health -= GameSettings.DAMAGE_PER_FRAME;
                isBeingAttacked = true;
                lastPlayerFacingRight = player.isFacingRight();
                lastDamageTime = System.currentTimeMillis();
                
                if (!isHit) {
                    isHit = true;
                    animationManager.resetHitAnimation();
                    hitAnimationStarted = false;
                }
                
                if (health <= 0) {
                    isDead = true;
                    health = 0;
                    isHit = true;
                    hitAnimationStarted = true;
                    hitFrame = 0;
                    isBeingAttacked = false;
                    playDeathSound();
                }
            } else {
                isBeingAttacked = false;
            }
        } else {
            if (isHit && !hitAnimationStarted) {
                hitAnimationStarted = true;
                animationManager.resetHitAnimation();
                isBeingAttacked = false;
            }
        }
    }
    
    private void updateAnimation(long now) {
        if (isDead && animationManager.getHitFrames() != null) {
            if (now - lastHitFrameTime >= GameSettings.ANIMATION_BOMB_HIT_DELAY) {
                hitFrame++;
                if (hitFrame >= animationManager.getHitFrames().length) {
                    if (!deathAnimationComplete) {
                        deathAnimationComplete = true;
                        destroyed = true;
                        lastSpawn = now;
                    }
                }
                lastHitFrameTime = now;
            }
        } else if (isHit && animationManager.getHitFrames() != null) {
            if (now - lastHitFrameTime >= GameSettings.ANIMATION_BOMB_HIT_DELAY) {
                if (isBeingAttacked) {
                    hitFrame = (hitFrame + 1) % 2;
                } else {
                    if (!hitAnimationStarted) {
                        hitFrame = 0;
                        hitAnimationStarted = true;
                    }
                    hitFrame++;
                    if (hitFrame >= animationManager.getHitFrames().length) {
                        hitFrame = 0;
                        isHit = false;
                        hitAnimationStarted = false;
                        blinking = true;
                        blinkStartTime = now;
                        offsetY = 0;
                        offsetX = 0;
                        playHitCompleteSound();
                        
                        if (!knockedBack) {
                            knockedBack = true;
                            if (lastPlayerFacingRight) {
                                x += GameSettings.BOMB_KNOCKBACK_DISTANCE;
                                if (x > GameSettings.WINDOW_SIZE.width - getDrawWidth()) {
                                    x = GameSettings.WINDOW_SIZE.width - getDrawWidth();
                                }
                            } else {
                                x -= GameSettings.BOMB_KNOCKBACK_DISTANCE;
                                if (x < 0) {
                                    x = 0;
                                }
                            }
                        }
                    }
                }
                lastHitFrameTime = now;
            }
        } else {
            if (now - lastIdleFrameTime >= GameSettings.ANIMATION_BOMB_IDLE_DELAY) {
                if (animationManager.getIdleFrames() != null && animationManager.getIdleFrames().length > 0) {
                    idleFrame = (idleFrame + 1) % animationManager.getIdleFrames().length;
                }
                lastIdleFrameTime = now;
            }
        }
    }
    
    private void updateBlinking(long now) {
        if (blinking) {
            if (now - blinkStartTime >= GameSettings.BOMB_BLINK_DURATION) {
                blinking = false;
                offsetY = 0;
                offsetX = 0;
                knockedBack = false;
            }
        }
    }
    
    public BufferedImage getCurrentFrame() {
        if (isDead && !deathAnimationComplete && animationManager.getHitFrames() != null) {
            int safeHitFrame = hitFrame % animationManager.getHitFrames().length;
            return animationManager.getHitFrames()[safeHitFrame];
        } else if (isHit && animationManager.getHitFrames() != null) {
            int safeHitFrame = hitFrame % animationManager.getHitFrames().length;
            return animationManager.getHitFrames()[safeHitFrame];
        } else if (!isDead && !destroyed) {
            int safeIdleFrame = idleFrame % animationManager.getIdleFrames().length;
            return animationManager.getIdleFrames()[safeIdleFrame];
        }
        return null;
    }
    
    public int getDrawWidth() {
        if (getCurrentFrame() != null) {
            return (int) (getCurrentFrame().getWidth() * GameSettings.PLAYER_SCALE);
        }
        return 0;
    }
    
    public int getDrawHeight() {
        if (getCurrentFrame() != null) {
            return (int) (getCurrentFrame().getHeight() * GameSettings.PLAYER_SCALE);
        }
        return 0;
    }
    
    public int getDrawX() {
        return x + offsetX;
    }
    
    public int getDrawY() {
        return y + offsetY;
    }
    
    public boolean shouldDraw() {
        if (isDead && deathAnimationComplete) {
            return false;
        }
        if (blinking) {
            return (System.currentTimeMillis() / 100) % 2 == 0;
        }
        return true;
    }
    
    public boolean isDead() {
        return isDead;
    }
    
    public int getHealthBarWidth() {
        if (getCurrentFrame() != null) {
            return getDrawWidth();
        }
        return 0;
    }
    
    public int getHealthBarX() {
        return getDrawX();
    }
    
    public int getHealthBarY() {
        return getDrawY() - 15;
    }
    
    public int getCurrentHealthWidth() {
        return (int)((double)health / maxHealth * getHealthBarWidth());
    }
    
    public int getSmoothHealthWidth() {
 
        int smoothMaxHealth = 400;
        float safeSmoothHealth = Math.max(0, smoothHealth);
        return (int)(safeSmoothHealth / smoothMaxHealth * 400);
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public int getHealth() { return health; }
    public int getMaxHealth() { return maxHealth; }
    public boolean isDestroyed() { return destroyed; }
    public boolean isFacingRight() { return facingRight; }
    public long getLastSpawnTime() { return lastSpawn; }
    
    public void startNewGame(long now) {
        respawn(now);
        blinking = true;
        blinkStartTime = now;
    }
    
    public void playRespawnSound() {
        if (respawnSound != null) {
            respawnSound.setFramePosition(0);
            respawnSound.start();
        }
    }
    
    private void playDeathSound() {
        if (deathSound != null) {
            deathSound.setFramePosition(0);
            deathSound.start();
        }
    }
    
    private void playHitCompleteSound() {
        if (deathSound != null) {
            deathSound.setFramePosition(0);
            deathSound.start();
        }
    }
    
}
