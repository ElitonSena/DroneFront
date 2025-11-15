package dronefront.enemy;
import dronefront.map.Ponto;

public class ScoutDrone extends Enemy {
    private static final int HP = 50;
    private static final double SPEED = 3.0;
    private static final int MOEDA = 10;
    private static final int DANO = 1;


    public ScoutDrone(Ponto startPosition) {
        super(HP, SPEED, MOEDA, DANO, startPosition);
    }

    @Override
    public char getCharRepresentation() {
        return 'S';
    }
    
    @Override
    public void applySlow(double modifier, double duration) {
        this.speedModifier = modifier;
        this.slowTimer = duration;
    }
}