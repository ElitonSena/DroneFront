package dronefront.projectile;

import dronefront.enemy.Enemy;
import dronefront.map.Ponto;

public abstract class Projectile {

    protected Ponto position;
    protected Enemy target;
    protected double speed;
    protected int damage;
    private boolean hasHitTarget = false;

    private static final double DistanciaAlvo = 0.2;

    public Projectile(Ponto startPosition, Enemy target, double speed, int damage) {
        this.position = startPosition;
        this.target = target;
        this.speed = speed;
        this.damage = damage;
    }

    public boolean update(double deltaTime) {
        if (hasHitTarget) {
            return true;
        }

        if (target.isDead() || target.chegouNaBase()) {
            hasHitTarget = true;
            return true;
        }

        Ponto targetPosition = target.getPosition();
        double distance = Math.sqrt(Math.pow(targetPosition.getX() - position.getX(), 2) +
                                    Math.pow(targetPosition.getY() - position.getY(), 2));

        if (distance < DistanciaAlvo) {
            hasHitTarget = true;
            target.takeDamage(this.damage);
            return true;
        }

        double dirX = (targetPosition.getX() - position.getX()) / distance;
        double dirY = (targetPosition.getY() - position.getY()) / distance;
        double newX = position.getX() + dirX * speed * deltaTime;
        double newY = position.getY() + dirY * speed * deltaTime;
        this.position = new Ponto(newX, newY);

        return false;
    }

    public Ponto getPosition() {
        return position;
    }

    public boolean hasHitTarget() {
        return hasHitTarget;
    }
    
    public abstract char getCharRepresentation();
}