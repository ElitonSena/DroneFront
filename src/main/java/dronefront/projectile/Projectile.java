package dronefront.projectile;

import dronefront.enemy.Enemy;
import dronefront.map.Ponto;
import dronefront.GameConfig;

public abstract class Projectile {

    protected Ponto position;
    protected Enemy target;
    protected double speed;
    protected int damage; 
    protected double rotation = 0;
    private boolean hasHitTarget = false;

    public Projectile(Ponto startPosition, Enemy target, double speed, int damage) {
        this.position = startPosition;
        this.target = target;
        this.speed = speed;
        this.damage = damage;
    }

    public boolean update(double deltaTime) {
        if (hasHitTarget) return true;

        if (target.isDead() || target.chegouNaBase()) {
            hasHitTarget = true;
            return true;
        }

        Ponto targetPosition = target.getPosition();
        double distance = Math.sqrt(Math.pow(targetPosition.getX() - position.getX(), 2) +
                                    Math.pow(targetPosition.getY() - position.getY(), 2));

        if (distance < GameConfig.PROJECTILE_HIT_DISTANCE) {
            hasHitTarget = true;
            onHit(target);
            return true;
        }

        double dirX = (targetPosition.getX() - position.getX()) / distance;
        double dirY = (targetPosition.getY() - position.getY()) / distance;

        this.rotation = Math.toDegrees(Math.atan2(dirY, dirX));

        double newX = position.getX() + dirX * speed * deltaTime;
        double newY = position.getY() + dirY * speed * deltaTime;
        this.position = new Ponto(newX, newY);

        return false;
    }

    protected abstract void onHit(Enemy target);

    public Ponto getPosition() {
        return position;
    }

    public boolean hasHitTarget() {
        return hasHitTarget;
    }

    public double getRotation() {
        return rotation;
    }
}