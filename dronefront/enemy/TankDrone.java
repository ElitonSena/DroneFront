package dronefront.enemy;
import dronefront.map.Ponto;

public class TankDrone extends Enemy {
    private static final int HP = 500;
    private static final double SPEED = 1.0;
    private static final int MOEDA = 50;
    private static final int DANO = 8;

    public TankDrone(Ponto startPosition) {
        super(HP, SPEED, MOEDA, DANO, startPosition);
    }

    @Override
    public char getCharRepresentation() {
        return 'T';
    }

    @Override
    public void applySlow(double modifier, double duration) {
    this.speedModifier = (1.0 + modifier) / 2.0; 
    this.slowTimer = duration * 0.5;
    }
}