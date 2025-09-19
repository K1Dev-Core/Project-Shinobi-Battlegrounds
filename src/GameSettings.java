import java.awt.Color;
import java.awt.Dimension;

public class GameSettings {
    
    public static final Dimension WINDOW_SIZE = new Dimension(1280, 720);
    
    public static final int PLAYER_SPEED = 4;
    public static final double PLAYER_SCALE = 2.3;
    public static final int PLAYER_START_X = 200;
    public static final int PLAYER_START_Y = 470;
    
    public static final int ATTACK_COOLDOWN = 5000;
    public static final int MAX_ATTACK_DURATION = 2000;
    
    public static final int BOMB_HEALTH = 300;
    public static final int BOMB_SPAWN_DELAY = 5000;
    public static final int BOMB_START_X = 800;
    public static final int BOMB_START_Y = 470;
    public static final int ATTACK_RANGE = 400;
    public static final int DAMAGE_PER_FRAME = 2;
    
    public static final int ANIMATION_IDLE_DELAY = 120;
    public static final int ANIMATION_WALK_DELAY = 80;
    public static final int ANIMATION_ATTACK_DELAY = 100;
    public static final int ANIMATION_RASENGAN_DELAY = 80;
    public static final int ANIMATION_HIT_DELAY = 80;
    public static final int ANIMATION_BOMB_IDLE_DELAY = 120;
    public static final int ANIMATION_BOMB_HIT_DELAY = 150;
    
    public static final int BOMB_BLINK_DURATION = 1000;
    public static final int BOMB_BOUNCE_Y = 20;
    public static final int BOMB_BOUNCE_X = 30;
    public static final int BOMB_KNOCKBACK_DISTANCE = 100;
    
    public static final int RASENGAN_SIZE = 250;
    public static final int RASENGAN_OFFSET_X = 80;
    public static final int RASENGAN_OFFSET_Y = 40;
    
    public static final Color TRANSPARENT_COLOR = new Color(255, 0, 255);
    public static final int COLOR_TOLERANCE = 15;
    public static final int RASENGAN_COLOR_TOLERANCE = 80;
    
    public static final String BACKGROUND_PATH = "assets/map/map-naruto.png";
    public static final String IDLE_SPRITE_PATH = "assets/players/naruto_idle.png";
    public static final String WALK_SPRITE_PATH = "assets/players/naruto_walk.png";
    public static final String ATTACK_SPRITE_PATH = "assets/players/naruto_attack.png";
    public static final String RASENGAN_SPRITE_PATH = "assets/players/rasengan.png";
    public static final String HIT_SPRITE_PATH = "assets/players/naruto_hit.png";
    public static final String RASENGAN_SOUND_PATH = "assets/sfx/rasengan.wav";
    
    public static final int IDLE_FRAMES = 6;
    public static final int WALK_FRAMES = 6;
    public static final int ATTACK_FRAMES = 4;
    public static final int RASENGAN_FRAMES = 4;
    public static final int HIT_FRAMES = 7;
    
    public static final int IDLE_FRAME_WIDTH = 87;
    public static final int IDLE_FRAME_HEIGHT = 88;
    public static final int WALK_FRAME_WIDTH = 89;
    public static final int WALK_FRAME_HEIGHT = 90;
    public static final int ATTACK_FRAME_WIDTH = 129;
    public static final int ATTACK_FRAME_HEIGHT = 98;
    public static final int RASENGAN_FRAME_WIDTH = 32;
    public static final int RASENGAN_FRAME_HEIGHT = 32;
    public static final int HIT_FRAME_WIDTH = 82;
    public static final int HIT_FRAME_HEIGHT = 82;
}
