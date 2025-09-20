package dronefront.enemy;

import dronefront.map.Caminho;
import dronefront.map.Ponto;
import java.util.List;

public abstract class Enemy {

    protected int hp;
    protected double speed;
    protected int moeda;
    protected int dano;
    protected Ponto position;
    protected int proxCaminho;
    private boolean chegouNaBase = false;

    public Enemy(int hp, double speed, int moeda, int dano, Ponto startPosition) {
        this.hp = hp;
        this.speed = speed;
        this.moeda = moeda;
        this.dano = dano;
        this.position = startPosition;
        this.proxCaminho = 1;
    }
    public boolean chegouNaBase() {
        return chegouNaBase;
    }
    
    public Ponto getPosition() {
        return position;
    }
    public abstract char getCharRepresentation();

    public int getDano() {
        return this.dano;
    }

    public boolean update(double deltaTime, Caminho caminho) {
        if (chegouNaBase) return true;

        List<Ponto> pontosDoCaminho = caminho.getPontosDoCaminho();
        if (proxCaminho >= pontosDoCaminho.size()) {
            this.chegouNaBase = true;
            return true;
        }

        Ponto alvo = pontosDoCaminho.get(proxCaminho);
        double distanciaParaAlvo = Math.sqrt(Math.pow(alvo.getX() - position.getX(), 2) + Math.pow(alvo.getY() - position.getY(), 2));
        double movimento = speed * deltaTime;

        if (distanciaParaAlvo <= movimento) {
            this.position = alvo;
            this.proxCaminho++;
            if (proxCaminho >= pontosDoCaminho.size()) {
                this.chegouNaBase = true;
                return true;
            }
        } else {
            double dirX = (alvo.getX() - position.getX()) / distanciaParaAlvo;
            double dirY = (alvo.getY() - position.getY()) / distanciaParaAlvo;
            double novoX = position.getX() + dirX * movimento;
            double novoY = position.getY() + dirY * movimento;
            this.position = new Ponto(novoX, novoY);
        }

        return false;
    }

    @Override
    public String toString() {
        String status = chegouNaBase ? "chegou na base" : String.format("pos: (%.2f, %.2f)", position.getX(), position.getY());
        return String.format("%s | HP: %d | %s",
                this.getClass().getSimpleName(), hp, status);
    }
}