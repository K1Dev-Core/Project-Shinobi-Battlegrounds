import java.awt.image.BufferedImage;
import javax.sound.sampled.*;

public class Player {
    
    private int x;
    private final int y;
    private final int speed;
    private final double scale;
    
    private boolean movingLeft = false;
    private boolean movingRight = false;
    private boolean facingRight = true;
    private boolean isAttacking = false;
    private boolean showRasengan = false;
    private boolean isHoldingAttack = false;
    
    private long lastAttackTime = 0;
    private long attackStartTime = 0;
    private int attackFrame = 0;
    private int rasenganFrame = 0;
    private long lastRasenganTime = 0;
    private int idleFrame = 0;
    private int walkFrame = 0;
    private long lastIdleTime = 0;
    private long lastWalkTime = 0;
    private int startFrame = 0;
    private long lastStartTime = 0;
    private boolean isPlayingStartAnimation = false;
    private boolean startAnimationComplete = false;
    
    private Clip rasenganSound;
    private boolean soundPlaying = false;
    
    private final AnimationManager animationManager;
    
    public Player(AnimationManager animationManager) {
        this.animationManager = animationManager;
        this.x = GameSettings.PLAYER_START_X;
        this.y = GameSettings.PLAYER_START_Y;
        this.speed = GameSettings.PLAYER_SPEED;
        this.scale = GameSettings.PLAYER_SCALE;
        
        loadSound();
    }
    
    private void loadSound() {
        try {
            java.io.File soundFile = new java.io.File(GameSettings.RASENGAN_SOUND_PATH);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
            rasenganSound = AudioSystem.getClip();
            rasenganSound.open(audioInputStream);
        } catch (Exception e) {
        }
    }
    
    public void update(long now, boolean isAttacking, boolean movingLeft, boolean movingRight) {
        this.movingLeft = movingLeft;
        this.movingRight = movingRight;
        
        if (isAttacking) {
            updateAttack(now);
        } else {
            updateMovement(now);
        }
    }
    
    private void updateAttack(long now) {
        long attackDuration = now - attackStartTime;
        if (attackDuration >= GameSettings.MAX_ATTACK_DURATION) {
            stopAttack(now);
        } else {
            if (now - lastAttackTime >= GameSettings.ANIMATION_ATTACK_DELAY) {
                attackFrame++;
                if (attackFrame >= animationManager.getAttackFrames().length) {
                    if (!isHoldingAttack) {
                        stopAttack(now);
                    } else {
                        attackFrame = animationManager.getAttackFrames().length - 1;
                    }
                }
                lastAttackTime = now;
            }
            
            if (showRasengan) {
                if (now - lastRasenganTime >= GameSettings.ANIMATION_RASENGAN_DELAY) {
                    rasenganFrame = (rasenganFrame + 1) % animationManager.getRasenganFrames().length;
                    lastRasenganTime = now;
                }
            }
        }
    }
    
    private void updateMovement(long now) {
        if (movingLeft) {
            x -= speed;
            if (x < 0) {
                x = 0;
            }
        }
        if (movingRight) {
            x += speed;
            int maxX = GameSettings.WINDOW_SIZE.width - getDrawWidth();
            if (x > maxX) {
                x = maxX;
            }
        }
        
        boolean isMoving = movingLeft || movingRight;
        if (isMoving) {
            if (now - lastWalkTime >= GameSettings.ANIMATION_WALK_DELAY) {
                walkFrame = (walkFrame + 1) % animationManager.getWalkFrames().length;
                lastWalkTime = now;
            }
        } else {
            if (now - lastIdleTime >= GameSettings.ANIMATION_IDLE_DELAY) {
                idleFrame = (idleFrame + 1) % animationManager.getIdleFrames().length;
                lastIdleTime = now;
            }
        }
    }
    
    public void startAttack(long now) {
        if (now - lastAttackTime >= GameSettings.ATTACK_COOLDOWN) {
            isHoldingAttack = true;
            if (!isAttacking) {
                isAttacking = true;
                showRasengan = true;
                attackFrame = 0;
                rasenganFrame = 0;
                attackStartTime = now;
                movingLeft = movingRight = false;
                
                playRasenganSound();
            }
        }
    }
    
    public void stopAttackInput() {
        isHoldingAttack = false;
        stopRasenganSound();
    }
    
    private void stopAttack(long now) {
        attackFrame = 0;
        isAttacking = false;
        showRasengan = false;
        isHoldingAttack = false;
        movingLeft = false;
        movingRight = false;
        lastAttackTime = now;
        stopRasenganSound();
    }
    
    private void playRasenganSound() {
        if (rasenganSound != null && !soundPlaying) {
            try {
                rasenganSound.setFramePosition(0);
                rasenganSound.start();
                soundPlaying = true;
            } catch (Exception e) {
            }
        }
    }
    
    private void stopRasenganSound() {
        if (rasenganSound != null && soundPlaying) {
            try {
                rasenganSound.stop();
                soundPlaying = false;
            } catch (Exception e) {
            }
        }
    }
    
    public BufferedImage getCurrentFrame() {
        if (isAttacking) {
            BufferedImage[] frames = animationManager.getAttackFrames();
            if (frames != null && frames.length > 0) {
                return frames[attackFrame % frames.length];
            }
        } else {
            boolean isMoving = movingLeft || movingRight;
            if (isMoving) {
                BufferedImage[] frames = animationManager.getWalkFrames();
                if (frames != null && frames.length > 0) {
                    return frames[walkFrame % frames.length];
                }
            } else {
                BufferedImage[] frames = animationManager.getIdleFrames();
                if (frames != null && frames.length > 0) {
                    return frames[idleFrame % frames.length];
                }
            }
        }
        return null;
    }
    
    public BufferedImage getRasenganFrame() {
        BufferedImage[] frames = animationManager.getRasenganFrames();
        if (frames != null && frames.length > 0) {
            return frames[rasenganFrame % frames.length];
        }
        return null;
    }
    
    public int getDrawWidth() {
        return (int) (getCurrentFrame().getWidth() * scale);
    }
    
    public int getDrawHeight() {
        return (int) (getCurrentFrame().getHeight() * scale);
    }
    
    public int getCenterX() {
        return x + getDrawWidth() / 2;
    }
    
    public int getCenterY() {
        return y + getDrawHeight() / 2;
    }
    
    public int getRasenganX() {
        if (facingRight) {
            return x + getDrawWidth() - GameSettings.RASENGAN_OFFSET_X;
        } else {
            return x - GameSettings.RASENGAN_SIZE + GameSettings.RASENGAN_OFFSET_X;
        }
    }
    
    public int getRasenganY() {
        return y - GameSettings.RASENGAN_OFFSET_Y;
    }
    
    public boolean canAttack(long now) {
        return now - lastAttackTime >= GameSettings.ATTACK_COOLDOWN;
    }
    
    public long getRemainingCooldown(long now) {
        return GameSettings.ATTACK_COOLDOWN - (now - lastAttackTime);
    }
    
    public long getRemainingAttackTime(long now) {
        if (isAttacking) {
            long attackDuration = now - attackStartTime;
            return GameSettings.MAX_ATTACK_DURATION - attackDuration;
        }
        return 0;
    }
    
    public void setFacingRight(boolean facingRight) {
        this.facingRight = facingRight;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    public boolean isAttacking() { return isAttacking; }
    public boolean isShowRasengan() { return showRasengan; }
    public boolean isFacingRight() { return facingRight; }
    public double getScale() { return scale; }
    
    public void startGameAnimation() {
        isPlayingStartAnimation = true;
        startAnimationComplete = false;
        startFrame = 0;
        lastStartTime = 0;
    }
    
    public void updateStartAnimation(long now) {
        if (isPlayingStartAnimation && !startAnimationComplete) {
            if (now - lastStartTime >= 100) {
                startFrame++;
                if (animationManager.getStartFrames() != null && startFrame >= animationManager.getStartFrames().length) {
                    startAnimationComplete = true;
                    isPlayingStartAnimation = false;
                }
                lastStartTime = now;
            }
        }
    }
    
    public boolean isPlayingStartAnimation() {
        return isPlayingStartAnimation;
    }
    
    public boolean isStartAnimationComplete() {
        return startAnimationComplete;
    }
    
    public BufferedImage getCurrentStartFrame() {
        if (isPlayingStartAnimation && animationManager.getStartFrames() != null) {
            int safeFrame = startFrame % animationManager.getStartFrames().length;
            return animationManager.getStartFrames()[safeFrame];
        }
        return null;
    }
}
