package dronefront.projectile;

import dronefront.enemy.Enemy;
import dronefront.map.Ponto;

public class Bullet extends Projectile {

    private static final double SPEED = 8.0;

    public Bullet(Ponto startPosition, Enemy target, int damage) {
        super(startPosition, target, SPEED, damage);
    }

    @Override
    public char getCharRepresentation() {
        return '*';
    }
}