import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class AnimationManager {
    
    private BufferedImage[] idleFrames;
    private BufferedImage[] walkFrames;
    private BufferedImage[] attackFrames;
    private BufferedImage[] rasenganFrames;
    private BufferedImage[] hitFrames;
    private BufferedImage[] jumpFrames;
    private BufferedImage[] startFrames;
    private BufferedImage[] fogFrames;
    
    private int idleFrame = 0;
    private int walkFrame = 0;
    private int attackFrame = 0;
    private int rasenganFrame = 0;
    private int hitFrame = 0;
    private int jumpFrame = 0;
    private int startFrame = 0;
    private int fogFrame = 0;
    
    private long lastIdleFrameTime = 0;
    private long lastWalkFrameTime = 0;
    private long lastAttackFrameTime = 0;
    private long lastRasenganFrameTime = 0;
    private long lastHitFrameTime = 0;
    private long lastJumpFrameTime = 0;
    private long lastStartFrameTime = 0;
    private long lastFogFrameTime = 0;
    
    public AnimationManager() {
        loadAnimations();
    }
    
    private void loadAnimations() {
        try {
            BufferedImage idleSheet = ImageIO.read(new File(GameSettings.IDLE_SPRITE_PATH));
            idleSheet = makeTransparent(idleSheet, GameSettings.TRANSPARENT_COLOR, GameSettings.COLOR_TOLERANCE);
            idleFrames = loadFrames(idleSheet, GameSettings.IDLE_FRAMES, GameSettings.IDLE_FRAME_WIDTH, GameSettings.IDLE_FRAME_HEIGHT, 1);

            BufferedImage walkSheet = ImageIO.read(new File(GameSettings.WALK_SPRITE_PATH));
            walkSheet = makeTransparent(walkSheet, GameSettings.TRANSPARENT_COLOR, GameSettings.COLOR_TOLERANCE);
            walkFrames = loadFrames(walkSheet, GameSettings.WALK_FRAMES, GameSettings.WALK_FRAME_WIDTH, GameSettings.WALK_FRAME_HEIGHT, 1);

            BufferedImage attackSheet = ImageIO.read(new File(GameSettings.ATTACK_SPRITE_PATH));
            attackSheet = makeTransparent(attackSheet, GameSettings.TRANSPARENT_COLOR, GameSettings.COLOR_TOLERANCE);
            attackFrames = loadFrames(attackSheet, GameSettings.ATTACK_FRAMES, GameSettings.ATTACK_FRAME_WIDTH, GameSettings.ATTACK_FRAME_HEIGHT, 1);

            BufferedImage rasenganSheet = ImageIO.read(new File(GameSettings.RASENGAN_SPRITE_PATH));
            rasenganSheet = makeTransparent(rasenganSheet, new Color(128, 128, 128), GameSettings.RASENGAN_COLOR_TOLERANCE);
            rasenganSheet = makeTransparent(rasenganSheet, new Color(140, 140, 140), GameSettings.RASENGAN_COLOR_TOLERANCE);
            rasenganSheet = makeTransparent(rasenganSheet, new Color(120, 120, 120), GameSettings.RASENGAN_COLOR_TOLERANCE);
            rasenganFrames = loadFrames(rasenganSheet, GameSettings.RASENGAN_FRAMES, GameSettings.RASENGAN_FRAME_WIDTH, GameSettings.RASENGAN_FRAME_HEIGHT, 0);

            BufferedImage hitSheet = ImageIO.read(new File(GameSettings.HIT_SPRITE_PATH));
            hitSheet = makeTransparent(hitSheet, GameSettings.TRANSPARENT_COLOR, GameSettings.COLOR_TOLERANCE);
            hitFrames = loadFrames(hitSheet, GameSettings.HIT_FRAMES, GameSettings.HIT_FRAME_WIDTH, GameSettings.HIT_FRAME_HEIGHT, 0);

            BufferedImage jumpSheet = ImageIO.read(new File(GameSettings.JUMP_SPRITE_PATH));
            jumpSheet = makeTransparent(jumpSheet, GameSettings.TRANSPARENT_COLOR, GameSettings.COLOR_TOLERANCE);
            jumpFrames = loadFrames(jumpSheet, GameSettings.JUMP_FRAMES, GameSettings.JUMP_FRAME_WIDTH, GameSettings.JUMP_FRAME_HEIGHT, 1);

            BufferedImage startSheet = ImageIO.read(new File("assets/players/naruto_start.png"));
            startSheet = makeTransparent(startSheet, GameSettings.TRANSPARENT_COLOR, GameSettings.COLOR_TOLERANCE);
            int totalWidth = startSheet.getWidth();
            int frameWidth = (totalWidth - 2) / 3;
            int frameHeight = startSheet.getHeight();
            startFrames = loadFrames(startSheet, 3, frameWidth, frameHeight, 1);
            
            BufferedImage fogSheet = ImageIO.read(new File("assets/players/naruto_fog.png"));
            fogSheet = makeTransparent(fogSheet, GameSettings.TRANSPARENT_COLOR, GameSettings.COLOR_TOLERANCE);
            int fogTotalWidth = fogSheet.getWidth();
            int fogFrameWidth = (fogTotalWidth - 1) / 2;
            int fogFrameHeight = fogSheet.getHeight();
            fogFrames = loadFrames(fogSheet, 2, fogFrameWidth, fogFrameHeight, 1);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private BufferedImage[] loadFrames(BufferedImage sheet, int cols, int frameW, int frameH, int spacing) {
        BufferedImage[] frames = new BufferedImage[cols];
        for (int x = 0; x < cols; x++) {
            int sx = x * (frameW + spacing);
            frames[x] = sheet.getSubimage(sx, 0, frameW, frameH);
        }
        return frames;
    }
    
    private BufferedImage makeTransparent(BufferedImage img, Color color, int tolerance) {
        BufferedImage out = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_ARGB);
        int rMatch = color.getRed(), gMatch = color.getGreen(), bMatch = color.getBlue();

        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                int pixel = img.getRGB(x, y);
                int r = (pixel >> 16) & 0xFF, g = (pixel >> 8) & 0xFF, b = pixel & 0xFF;

                if (Math.abs(r - rMatch) <= tolerance &&
                        Math.abs(g - gMatch) <= tolerance &&
                        Math.abs(b - bMatch) <= tolerance) {
                    out.setRGB(x, y, 0x00000000);
                } else {
                    out.setRGB(x, y, pixel);
                }
            }
        }
        return out;
    }
    
    public void updateIdleAnimation(long now) {
        if (now - lastIdleFrameTime >= GameSettings.ANIMATION_IDLE_DELAY) {
            if (idleFrames != null && idleFrames.length > 0) {
                idleFrame = (idleFrame + 1) % idleFrames.length;
            }
            lastIdleFrameTime = now;
        }
    }
    
    public void updateWalkAnimation(long now) {
        if (now - lastWalkFrameTime >= GameSettings.ANIMATION_WALK_DELAY) {
            if (walkFrames != null && walkFrames.length > 0) {
                walkFrame = (walkFrame + 1) % walkFrames.length;
            }
            lastWalkFrameTime = now;
        }
    }
    
    public void updateAttackAnimation(long now) {
        if (now - lastAttackFrameTime >= GameSettings.ANIMATION_ATTACK_DELAY) {
            attackFrame++;
            lastAttackFrameTime = now;
        }
    }
    
    public void updateRasenganAnimation(long now) {
        if (now - lastRasenganFrameTime >= GameSettings.ANIMATION_RASENGAN_DELAY) {
            if (rasenganFrames != null && rasenganFrames.length > 0) {
                rasenganFrame = (rasenganFrame + 1) % rasenganFrames.length;
            }
            lastRasenganFrameTime = now;
        }
    }
    
    public void updateHitAnimation(long now) {
        if (now - lastHitFrameTime >= GameSettings.ANIMATION_HIT_DELAY) {
            hitFrame++;
            lastHitFrameTime = now;
        }
    }
    
    public void updateJumpAnimation(long now) {
        if (now - lastJumpFrameTime >= GameSettings.ANIMATION_JUMP_DELAY) {
            if (jumpFrames != null && jumpFrames.length > 0) {
                jumpFrame = (jumpFrame + 1) % jumpFrames.length;
            }
            lastJumpFrameTime = now;
        }
    }
    
    public void resetAttackAnimation() {
        attackFrame = 0;
    }
    
    public void resetRasenganAnimation() {
        rasenganFrame = 0;
    }
    
    public void resetHitAnimation() {
        hitFrame = 0;
    }
    
    public void resetJumpAnimation() {
        jumpFrame = 0;
    }
    
    public BufferedImage getCurrentIdleFrame() {
        if (idleFrames == null || idleFrames.length == 0) return null;
        return idleFrames[idleFrame % idleFrames.length];
    }
    
    public BufferedImage getCurrentWalkFrame() {
        if (walkFrames == null || walkFrames.length == 0) return null;
        return walkFrames[walkFrame % walkFrames.length];
    }
    
    public BufferedImage getCurrentAttackFrame() {
        if (attackFrames == null || attackFrames.length == 0) return null;
        return attackFrames[attackFrame % attackFrames.length];
    }
    
    public BufferedImage getCurrentRasenganFrame() {
        if (rasenganFrames == null || rasenganFrames.length == 0) return null;
        return rasenganFrames[rasenganFrame % rasenganFrames.length];
    }
    
    public BufferedImage getCurrentHitFrame() {
        if (hitFrames == null || hitFrames.length == 0) return null;
        return hitFrames[hitFrame % hitFrames.length];
    }
    
    public BufferedImage getCurrentJumpFrame() {
        if (jumpFrames == null || jumpFrames.length == 0) return null;
        return jumpFrames[jumpFrame % jumpFrames.length];
    }
    
    public BufferedImage[] getIdleFrames() {
        return idleFrames;
    }
    
    public BufferedImage[] getWalkFrames() {
        return walkFrames;
    }
    
    public BufferedImage[] getAttackFrames() {
        return attackFrames;
    }
    
    public BufferedImage[] getRasenganFrames() {
        return rasenganFrames;
    }
    
    public BufferedImage[] getHitFrames() {
        return hitFrames;
    }
    
    public BufferedImage[] getJumpFrames() {
        return jumpFrames;
    }
    
    public int getIdleFrame() {
        return idleFrame;
    }
    
    public int getWalkFrame() {
        return walkFrame;
    }
    
    public int getAttackFrame() {
        return attackFrame;
    }
    
    public int getRasenganFrame() {
        return rasenganFrame;
    }
    
    public int getHitFrame() {
        return hitFrame;
    }
    
    public int getJumpFrame() {
        return jumpFrame;
    }
    
    public boolean isAttackAnimationComplete() {
        return attackFrame >= attackFrames.length;
    }
    
    public boolean isHitAnimationComplete() {
        return hitFrame >= hitFrames.length;
    }
    
    public void setAttackFrame(int frame) {
        this.attackFrame = frame;
    }
    
    public void updateStartAnimation(long now) {
        if (now - lastStartFrameTime >= 100) {
            if (startFrames != null && startFrames.length > 0) {
                startFrame = (startFrame + 1) % startFrames.length;
            }
            lastStartFrameTime = now;
        }
    }
    
    public BufferedImage getCurrentStartFrame() {
        if (startFrames == null || startFrames.length == 0) return null;
        return startFrames[startFrame % startFrames.length];
    }
    
    public BufferedImage[] getStartFrames() {
        return startFrames;
    }
    
    public void updateFogAnimation(long now) {
        if (now - lastFogFrameTime >= 150) {
            if (fogFrames != null && fogFrames.length > 0) {
                fogFrame = (fogFrame + 1) % fogFrames.length;
            }
            lastFogFrameTime = now;
        }
    }
    
    public BufferedImage getCurrentFogFrame() {
        if (fogFrames == null || fogFrames.length == 0) return null;
        return fogFrames[fogFrame % fogFrames.length];
    }
    
    public void resetStartAnimation() {
        startFrame = 0;
        lastStartFrameTime = 0;
    }
}
