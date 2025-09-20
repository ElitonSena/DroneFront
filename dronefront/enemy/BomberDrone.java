package dronefront.enemy;
import dronefront.map.Ponto;

public class BomberDrone extends Enemy {
    private static final int HP = 200;
    private static final double SPEED = 1.5; // metade do scout
    private static final int MOEDA = 25;
    private static final int DANO = 5;

    public BomberDrone(Ponto startPosition) {
        super(HP, SPEED, MOEDA, DANO, startPosition);
    }

    @Override
    public char getCharRepresentation() {
        return 'B';
    }
}