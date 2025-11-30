package dronefront.tower;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import dronefront.GameConfig;
import dronefront.audio.SoundManager;
import dronefront.enemy.Enemy;
import dronefront.map.Ponto;
import dronefront.projectile.Bullet;
import dronefront.projectile.Projectile;

public class GunTower extends Tower {

    private int damage;

    public GunTower(Ponto position) {
        super(position, GameConfig.GUN_RANGE, GameConfig.GUN_COOLDOWN, GameConfig.GUN_COST);
        this.damage = GameConfig.GUN_DAMAGE;
    }

    @Override
    public int getUpgradeCost() {
        return GameConfig.GUN_UPGRADE_COST_PER_LEVEL * this.level;
    }

    public int getDamage() {
        return this.damage;
    }

    @Override
    protected Optional<Enemy> findTarget(List<Enemy> enemies) {
        return enemies.stream()
                .filter(e -> !e.isDead() && !e.chegouNaBase() && distanceTo(e) <= this.range)
                .max(Comparator.comparingDouble(Enemy::getProxCaminho));
    }

    @Override
    protected void fire(Enemy target, List<Projectile> worldProjectiles) {
        worldProjectiles.add(new Bullet(this.position, target, this.damage));
        SoundManager.getInstance().play("proj_gun_fire.mp3");
    }

    @Override
    public void upgrade() {
        if (isMaxLevel()) return;
        this.level++;
        this.range += GameConfig.GUN_UPGRADE_RANGE;
        this.damage += GameConfig.GUN_UPGRADE_DAMAGE;
        this.cooldown *= GameConfig.GUN_UPGRADE_COOLDOWN_FACTOR;
    }
}