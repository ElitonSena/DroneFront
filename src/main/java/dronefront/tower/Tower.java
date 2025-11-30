package dronefront.tower;

import java.util.List;
import java.util.Optional;

import dronefront.enemy.Enemy;
import dronefront.map.Ponto;
import dronefront.projectile.Projectile;

public abstract class Tower {

    protected final Ponto position;
    protected double range;
    protected double cooldown;
    protected final int cost;
    protected int level = 1;
    protected int maxLevel = 3;
    protected double rotation = 0;
    private double cooldownTimer = 0;

    public Tower(Ponto position, double range, double cooldown, int cost) {
        this.position = position;
        this.range = range;
        this.cooldown = cooldown;
        this.cost = cost;
    }

    public Ponto getPosition() { return this.position; }
    public int getCost() { return this.cost; }
    public int getLevel() { return this.level; }
    public double getRotation() { return this.rotation; }
    public double getRange() { return this.range; }
    public double getCooldown() { return this.cooldown; }

    public void update(double deltaTime, List<Enemy> enemies, List<Projectile> worldProjectiles) {
        if (cooldownTimer > 0) {
            cooldownTimer -= deltaTime;
        }

        Optional<Enemy> alvoOp = findTarget(enemies);
        
        if (alvoOp.isPresent()) {
            Enemy target = alvoOp.get();
            
            double dx = target.getPosition().getX() - this.position.getX();
            double dy = target.getPosition().getY() - this.position.getY();
            
            this.rotation = Math.toDegrees(Math.atan2(dy, dx));
            
            if (cooldownTimer <= 0) {
                fire(target, worldProjectiles);
                this.cooldownTimer = this.cooldown;
            }
        }
    }

    protected double distanceTo(Enemy enemy) {
        return Math.sqrt(Math.pow(position.getX() - enemy.getPosition().getX(), 2) +
                         Math.pow(position.getY() - enemy.getPosition().getY(), 2));
    }

    public boolean isMaxLevel() { return this.level >= this.maxLevel; }
    public abstract int getUpgradeCost();
    public abstract void upgrade();
    protected abstract Optional<Enemy> findTarget(List<Enemy> enemies);

    protected abstract void fire(Enemy target, List<Projectile> worldProjectiles);
}