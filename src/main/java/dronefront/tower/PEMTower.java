package dronefront.tower;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import dronefront.GameConfig;
import dronefront.audio.SoundManager;
import dronefront.enemy.Enemy;
import dronefront.map.Ponto;
import dronefront.projectile.Projectile;

public class PEMTower extends Tower {

    private double slowDuration;

    public PEMTower(Ponto position) {
        super(position, GameConfig.PEM_RANGE, GameConfig.PEM_COOLDOWN, GameConfig.PEM_COST);
        this.slowDuration = GameConfig.PEM_DURATION;
    }

    @Override
    protected Optional<Enemy> findTarget(List<Enemy> enemies) {
        return enemies.stream()
                .filter(e -> distanceTo(e) <= this.range && !e.chegouNaBase())
                .max(Comparator.comparingDouble(Enemy::getSpeedAtual)); 
    }

    @Override
    protected void fire(Enemy target, List<Projectile> worldProjectiles) {
        worldProjectiles.add(new dronefront.projectile.PEMProjectile(this.position, target, GameConfig.PEM_SLOW_FACTOR, this.slowDuration));
        SoundManager.getInstance().play("proj_pem_fire.mp3");
    }

    @Override
    public int getUpgradeCost() {
        return GameConfig.PEM_UPGRADE_COST;
    }

    public double getSlowFactor() {
        return GameConfig.PEM_SLOW_FACTOR;
    }
    public double getSlowDuration() {
        return GameConfig.PEM_DURATION;
    }

    @Override
    public void upgrade() {
        if (isMaxLevel()) return;
        this.level++;
        this.range += GameConfig.PEM_UPGRADE_RANGE;
        this.slowDuration += GameConfig.PEM_UPGRADE_DURATION;
        this.cooldown *= GameConfig.PEM_UPGRADE_COOLDOWN_FACTOR;
    }
}