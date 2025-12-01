package dronefront;

import java.util.ArrayList;
import java.util.List;

import dronefront.wave.Wave;

public class GameConfig {

    // CONFIGURAÇÕES DE MAPA E TILE
    public static final int TILE_SIZE = 45;
    public static final int MAP_COLS = 28; 
    public static final int MAP_ROWS = 14;
    
    public static final int WINDOW_WIDTH = MAP_COLS * TILE_SIZE;
    public static final int WINDOW_HEIGHT = MAP_ROWS * TILE_SIZE;
    public static final int UI_HEIGHT = 100;

    // ECONOMIA E VIDA INICIAL
    public static final int INITIAL_BASE_LIVES = 50;
    public static final int INITIAL_MONEY = 550;
    public static final int SELL_REFUND_PERCENTAGE = 60;

    // TORRES
    // Gun
    public static final int GUN_COST = 120;
    public static final int GUN_DAMAGE = 35;
    public static final double GUN_RANGE = 3.5;
    public static final double GUN_COOLDOWN = 0.7;
    // Upgrade
    public static final int GUN_UPGRADE_COST_PER_LEVEL = 90;
    public static final int GUN_UPGRADE_DAMAGE = 20;
    public static final double GUN_UPGRADE_RANGE = 0.5;
    public static final double GUN_UPGRADE_COOLDOWN_FACTOR = 0.85;

    // Fire
    public static final int FIRE_COST = 160;
    public static final double FIRE_RANGE = 3.0;
    public static final double FIRE_COOLDOWN = 0.8;
    public static final double FIRE_DPS = 25.0;
    public static final double FIRE_BURN_DURATION = 3.5;
    // Upgrade
    public static final int FIRE_UPGRADE_COST_PER_LEVEL = 130;
    public static final double FIRE_UPGRADE_DPS = 15.0;
    public static final double FIRE_UPGRADE_BURN_DURATION = 1.0;
    public static final double FIRE_UPGRADE_RANGE = 0.3;

    // PEM
    public static final int PEM_COST = 200;
    public static final double PEM_RANGE = 4.0;
    public static final double PEM_COOLDOWN = 2.5;
    public static final double PEM_SLOW_FACTOR = 0.6;
    public static final double PEM_DURATION = 2.0;
    // Upgrade
    public static final int PEM_UPGRADE_COST = 180;
    public static final double PEM_UPGRADE_RANGE = 0.5;
    public static final double PEM_UPGRADE_DURATION = 1.0;
    public static final double PEM_UPGRADE_COOLDOWN_FACTOR = 0.8;

    // INIMIGOS
    public static final double SLOW_VULNERABILITY_FACTOR = 1.5;
    public static final double ENEMY_TIME_TO_DISAPPEAR = 1.0;
    public static final double PROJECTILE_HIT_DISTANCE = 0.6; 

    public static final int SCOUT_HP = 70;
    public static final double SCOUT_SPEED = 3.8;
    public static final int SCOUT_REWARD = 15;
    public static final int SCOUT_DAMAGE_TO_BASE = 1;
    public static final double SCOUT_MIN_SPEED_MODIFIER = 0.6;

    public static final int BOMBER_HP = 280;
    public static final double BOMBER_SPEED = 2.0;
    public static final int BOMBER_REWARD = 35;
    public static final int BOMBER_DAMAGE_TO_BASE = 5;
    public static final double BOMBER_BURN_MULTIPLIER = 2.0;
    
    public static final int TANK_HP = 1200;
    public static final double TANK_SPEED = 0.9;
    public static final int TANK_REWARD = 120;
    public static final int TANK_DAMAGE_TO_BASE = 20;

    // PROJETEIS
    public static final double PROJ_BULLET_SPEED = 10.0;
    public static final double PROJ_PEM_SPEED = 7.0;
    public static final double PROJ_FIRE_SPEED = 8.0;
    public static final double PROJ_HIT_DISTANCE = 0.6;

    // WAVES
    
    public static List<Wave> getWaves() {
        List<Wave> waves = new ArrayList<>();
        // qtdInimigos, intervaloSpawn, probScout, probBomber, probTank

        waves.add(new Wave(10, 2.0, 1.0, 0.0, 0.0));
        waves.add(new Wave(18, 1.2, 1.0, 0.0, 0.0));
        waves.add(new Wave(12, 1.8, 0.6, 0.4, 0.0));
        waves.add(new Wave(30, 0.8, 0.9, 0.1, 0.0));
        waves.add(new Wave(15, 1.5, 0.2, 0.8, 0.0));
        waves.add(new Wave(20, 1.2, 0.6, 0.3, 0.1));
        waves.add(new Wave(40, 0.7, 0.5, 0.4, 0.1));
        waves.add(new Wave(10, 2.5, 0.0, 0.4, 0.6));
        waves.add(new Wave(60, 0.5, 0.8, 0.2, 0.0));
        waves.add(new Wave(25, 1.5, 0.0, 0.7, 0.3));
        waves.add(new Wave(80, 0.3, 0.9, 0.1, 0.0));
        waves.add(new Wave(50, 1.0, 0.0, 0.5, 0.5));

        return waves;
    }
}