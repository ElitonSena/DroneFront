package dronefront.enemy;
import dronefront.GameConfig;
import dronefront.map.Ponto;

public class TankDrone extends Enemy {

    public TankDrone(Ponto startPosition) {
        super(GameConfig.TANK_HP, 
              GameConfig.TANK_SPEED, 
              GameConfig.TANK_REWARD, 
              GameConfig.TANK_DAMAGE_TO_BASE, 
              startPosition);
    }

    @Override
    public void applySlow(double modifier, double duration) {
        this.speedModifier = modifier; 
        this.slowTimer = duration;
    }
}