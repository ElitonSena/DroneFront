package dronefront.enemy;
import dronefront.GameConfig;
import dronefront.map.Ponto;

public class ScoutDrone extends Enemy {

    public ScoutDrone(Ponto startPosition) {
        super(GameConfig.SCOUT_HP, 
              GameConfig.SCOUT_SPEED, 
              GameConfig.SCOUT_REWARD, 
              GameConfig.SCOUT_DAMAGE_TO_BASE, 
              startPosition);
    }
    
    @Override
    public void applySlow(double modifier, double duration) {
        this.speedModifier = Math.max(modifier, GameConfig.SCOUT_MIN_SPEED_MODIFIER); 
        this.slowTimer = duration;
    }
}