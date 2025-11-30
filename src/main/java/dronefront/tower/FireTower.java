package dronefront.tower;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import dronefront.GameConfig;
import dronefront.audio.SoundManager;
import dronefront.enemy.Enemy;
import dronefront.map.Ponto;
import dronefront.projectile.Projectile;

public class FireTower extends Tower {

    private double damagePerSecond = GameConfig.FIRE_DPS;
    private double burnDuration = GameConfig.FIRE_BURN_DURATION;

    public FireTower(Ponto position) {
        super(position, GameConfig.FIRE_RANGE, GameConfig.FIRE_COOLDOWN, GameConfig.FIRE_COST); 
    }

    @Override
    protected Optional<Enemy> findTarget(List<Enemy> enemies) {
        return enemies.stream()
                .filter(e -> distanceTo(e) <= this.range && !e.chegouNaBase())
                .max(Comparator.comparingInt(Enemy::getHp));
    }

    @Override
    protected void fire(Enemy target, List<Projectile> worldProjectiles) {
        worldProjectiles.add(new dronefront.projectile.FireProjectile(this.position, target, this.damagePerSecond, this.burnDuration));
        SoundManager.getInstance().play("proj_fire_fire.mp3");
    }

    @Override
    public int getUpgradeCost() {
        return GameConfig.FIRE_UPGRADE_COST_PER_LEVEL * this.level;
    }

    public double getDamagePerSecond() {
        return this.damagePerSecond;
    }
    public double getBurnDuration() {
        return this.burnDuration;
    }

    @Override
    public void upgrade() {
        if (isMaxLevel()) return;
        this.level++;
        this.damagePerSecond += GameConfig.FIRE_UPGRADE_DPS;
        this.burnDuration += GameConfig.FIRE_UPGRADE_BURN_DURATION;
        this.range += GameConfig.FIRE_UPGRADE_RANGE;
    }
}