package dronefront.tower;

import dronefront.enemy.Enemy;
import dronefront.map.Ponto;
import dronefront.projectile.Bullet;
import dronefront.projectile.Projectile;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

// torre tiro, aplica dano

public class GunTower extends Tower {

    private final int damage;

    public GunTower(Ponto position) {
        super(position, 2.5, 0.25, 100);
        this.damage = 8;
    }

    @Override
    protected Optional<Enemy> findTarget(List<Enemy> enemies) {
        // mira no inimigo mmais perto da base
        return enemies.stream()
                .filter(e -> !e.isDead() && distanceTo(e) <= this.range)
                .max(Comparator.comparingInt(Enemy::getProxCaminho));
    }

    @Override
    protected void fire(Enemy target) {
        // cria a bala e add na lista de projeteis
        Projectile bullet = new Bullet(this.position, target, this.damage);
        addProjectile(bullet);
    }
}