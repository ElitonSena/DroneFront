package dronefront.projectile;

import dronefront.GameConfig;
import dronefront.enemy.Enemy;
import dronefront.map.Ponto;

public class Bullet extends Projectile {

    public Bullet(Ponto startPosition, Enemy target, int damage) {
        super(startPosition, target, GameConfig.PROJ_BULLET_SPEED, damage);
    }

    @Override
    protected void onHit(Enemy target) {
        target.takeDamage(this.damage);
    }
}