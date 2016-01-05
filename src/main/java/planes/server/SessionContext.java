package planes.server;

/**
 * Created by anton on 03.01.2016.
 */
public class SessionContext {
    private SessionPhase phase;
    private PlayerSide playerSide;

    public SessionPhase getPhase() {
        return phase;
    }

    public void setPhase(SessionPhase phase) {
        this.phase = phase;
    }

    public PlayerSide getPlayerSide() {
        return playerSide;
    }

    public void setPlayerSide(PlayerSide playerSide) {
        this.playerSide = playerSide;
    }

    public enum SessionPhase {SETUP_WORLD, GAME}
    public enum PlayerSide{LEFT, RIGHT}
}
