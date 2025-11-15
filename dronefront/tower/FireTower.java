package dronefront.tower;

import dronefront.enemy.Enemy;
import dronefront.map.Ponto;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

// torre fogo, aplica DPS

public class FireTower extends Tower {

    private double damagePerSecond = 5;
    private double burnDuration = 3.0;

    public FireTower(Ponto position) {
        super(position, 2.0, 1.5, 125); 
    }

    @Override
    protected Optional<Enemy> findTarget(List<Enemy> enemies) {
        // mira no inimigo mais proximo da torre
        return enemies.stream()
                .filter(e -> distanceTo(e) <= this.range && !e.chegouNaBase())
                .min(Comparator.comparingDouble(this::distanceTo));
    }

    @Override
    protected void fire(Enemy target) {
        // colocar projetil de fogo aqui
        target.applyBurn(this.damagePerSecond, this.burnDuration);
    }

    @Override
    public int getUpgradeCost() {
        return (int)(this.cost * 0.8 * this.level);
    }

    @Override
    public void upgrade() {
        if (isMaxLevel()) return;
        
        this.level++;
        this.damagePerSecond += 3;
        this.burnDuration += 0.5;
        this.range += 0.25;
    }
}