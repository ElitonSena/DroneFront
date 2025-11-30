package dronefront.projectile;

import dronefront.GameConfig;
import dronefront.audio.SoundManager;
import dronefront.enemy.Enemy;
import dronefront.map.Ponto;

public class PEMProjectile extends Projectile {

    private final double slowFactor;
    private final double duration;

    public PEMProjectile(Ponto startPosition, Enemy target, double slowFactor, double duration) {
        super(startPosition, target, GameConfig.PROJ_PEM_SPEED, 0);
        this.slowFactor = slowFactor;
        this.duration = duration;
    }

    @Override
    protected void onHit(Enemy target) {
        target.applySlow(slowFactor, duration);
        SoundManager.getInstance().play("impact-pem-slow.mp3");
    }
}