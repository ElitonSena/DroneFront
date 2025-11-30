package dronefront.enemy;
import dronefront.GameConfig;
import dronefront.map.Ponto;

public class BomberDrone extends Enemy {

    public BomberDrone(Ponto startPosition) {
        super(GameConfig.BOMBER_HP, 
              GameConfig.BOMBER_SPEED, 
              GameConfig.BOMBER_REWARD, 
              GameConfig.BOMBER_DAMAGE_TO_BASE, 
              startPosition);
    }

    @Override
    public void applyBurn(double dps, double duration) {
        if (dps >= this.burnDamagePerSecond) {
             this.burnDamagePerSecond = dps * GameConfig.BOMBER_BURN_MULTIPLIER;
             this.burnTimer = duration * GameConfig.BOMBER_BURN_MULTIPLIER;
        }
    }
}