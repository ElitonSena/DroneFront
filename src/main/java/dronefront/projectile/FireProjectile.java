package dronefront.projectile;

import dronefront.GameConfig;
import dronefront.audio.SoundManager;
import dronefront.enemy.Enemy;
import dronefront.map.Ponto;

public class FireProjectile extends Projectile {

    private final double burnDps;
    private final double duration;

    public FireProjectile(Ponto startPosition, Enemy target, double burnDps, double duration) {
        super(startPosition, target, GameConfig.PROJ_FIRE_SPEED, 0);
        this.burnDps = burnDps;
        this.duration = duration;
    }

    @Override
    protected void onHit(Enemy target) {
        target.applyBurn(burnDps, duration);
        SoundManager.getInstance().play("impact_fire_burn.mp3");
    }
}