package Enums;

/**
 *
 * @author Dragos-Alexandru
 */
public enum GameModes {
    SINGLEPLAYER(0), MULTIPLAYER_HOST(1),
    MULTIPLAYER_CLIENT(2);
    int value;

    private GameModes(int value) {
        this.value = value;
    }
}
