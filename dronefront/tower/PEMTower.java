package dronefront.tower;

import dronefront.enemy.Enemy;
import dronefront.map.Ponto;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

// torre de PEM, aplica slow

public class PEMTower extends Tower {

    private static final double SLOW_FACTOR = 0.5;
    private double slowDuration = 3.0;

    public PEMTower(Ponto position) {
        super(position, 3.0, 2.0, 150);
    }

    @Override
    protected Optional<Enemy> findTarget(List<Enemy> enemies) {
        // mira no inimigo com mais vida no range da torre
        return enemies.stream()
                .filter(e -> distanceTo(e) <= this.range && !e.chegouNaBase())
                .max(Comparator.comparingInt(Enemy::getHp));
    }

    @Override
    protected void fire(Enemy target) {
        // colocar projetil PEM aq
        System.out.println("PEMTower aplicando slow em " + target.getClass().getSimpleName());
        target.applySlow(SLOW_FACTOR, this.slowDuration);
    }

    @Override
    public int getUpgradeCost() {
        return (int)(this.cost * 0.75 * this.level);
    }

    @Override
    public void upgrade() {
        if (isMaxLevel()) return;

        this.level++;
        this.range += 0.5;
        this.slowDuration += 1.0;
        this.cooldown *= 0.85;
    }
}