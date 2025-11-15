package dronefront.tower;

import dronefront.enemy.Enemy;
import dronefront.map.Ponto;
import dronefront.projectile.Projectile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class Tower {

    protected final Ponto position;
    protected double range;
    protected double cooldown;
    protected final int cost;
    protected int level = 1;
    protected int maxLevel = 3;

    private double cooldownTimer = 0;

    private final List<Projectile> newProjectiles = new ArrayList<>();

    public Tower(Ponto position, double range, double cooldown, int cost) {
        this.position = position;
        this.range = range;
        this.cooldown = cooldown;
        this.cost = cost;
    }

    public Ponto getPosition() {
        return this.position;
    }

    public int getCost() {
        return this.cost;
    }

    public int getLevel() {
        return this.level;
    }

    public void update(double deltaTime, List<Enemy> enemies) {
        if (cooldownTimer > 0) {
            cooldownTimer -= deltaTime;
        }

        if (cooldownTimer <= 0) {
            findTarget(enemies).ifPresent(target -> {
                fire(target);
                this.cooldownTimer = this.cooldown;
            });
        }
    }

    protected void addProjectile(Projectile projectile) {
        this.newProjectiles.add(projectile);
    }

    public List<Projectile> collectNewProjectiles() {
        List<Projectile> projectiles = new ArrayList<>(newProjectiles);
        newProjectiles.clear(); 
        return projectiles;
    }

    protected double distanceTo(Enemy enemy) {
        return Math.sqrt(Math.pow(position.getX() - enemy.getPosition().getX(), 2) +
                         Math.pow(position.getY() - enemy.getPosition().getY(), 2));
    }

    public boolean isMaxLevel() {
        return this.level >= this.maxLevel;
    }

    public abstract int getUpgradeCost();

    public abstract void upgrade();

    protected abstract Optional<Enemy> findTarget(List<Enemy> enemies);

    protected abstract void fire(Enemy target);
}