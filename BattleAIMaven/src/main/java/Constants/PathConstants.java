package Constants;

/**
 * This class will have all the path constants
 * Every resource will pe stored in 'BattleAIMaven/src/main/resources' but
 * upon build they are moved to 'BattleAIMaven/target/classes'
 * 
 * @author Dragos-Alexandru
 */
public abstract class PathConstants {
    public static final String RESOURCE_FOLDER_PATH = "target/classes/";
    public static final String AI_TEMPLATE = RESOURCE_FOLDER_PATH+"AITemplate.txt";
    public static final String USER_SOURCES_FOLDER_PATH = RESOURCE_FOLDER_PATH+"User_Sources";
    public static final String TANK_BODY_SPRITE_PATH = RESOURCE_FOLDER_PATH+"tank_body.png";
    public static final String TANK_CANNON_SPRITE_PATH = RESOURCE_FOLDER_PATH+"tank_cannon.png";
    public static final String BULLET_SPRITE_PATH = RESOURCE_FOLDER_PATH+"bullet.png";
}
