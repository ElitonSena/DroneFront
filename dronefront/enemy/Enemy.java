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
    protected boolean chegouNaBase = false;
    protected double speedModifier = 1.0;
    protected double slowTimer = 0;
    protected double vulnerabilityFactor = 1.0;
    protected double burnDamagePerSecond = 0;
    protected double burnTimer = 0;
    protected double accumulatedBurnDamage = 0;

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
        
        if (burnTimer > 0) {
            burnTimer -= deltaTime;
            accumulatedBurnDamage += burnDamagePerSecond * deltaTime;
            
            int damageToApply = (int) accumulatedBurnDamage;
            if (damageToApply > 0) {
                takeDamage(damageToApply);
                accumulatedBurnDamage -= damageToApply;
            }

            if (burnTimer <= 0) {
                burnDamagePerSecond = 0;
                accumulatedBurnDamage = 0;
            }
        }

        if (slowTimer > 0) {
            slowTimer -= deltaTime;
            if (slowTimer <= 0) {
                this.speedModifier = 1.0; 
                this.vulnerabilityFactor = 1.0;
            }
        }

        if (slowTimer > 0) {
            slowTimer -= deltaTime;
            if (slowTimer <= 0) {
                this.speedModifier = 1.0;
            }
        }

        List<Ponto> pontosDoCaminho = caminho.getPontosDoCaminho();
        if (proxCaminho >= pontosDoCaminho.size()) {
            this.chegouNaBase = true;
            return true;
        }

        Ponto alvo = pontosDoCaminho.get(proxCaminho);
        double distanciaParaAlvo = Math.sqrt(Math.pow(alvo.getX() - position.getX(), 2) + Math.pow(alvo.getY() - position.getY(), 2));
        double movimento = (speed * speedModifier) * deltaTime;

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

    public int getHp() {
        return this.hp;
    }

    public int getProxCaminho() {
        return this.proxCaminho;
    }
    
    public void takeDamage(int amount) {
        int actualDamage = (int)(amount * this.vulnerabilityFactor);
        
        this.hp -= actualDamage;
        if (this.hp < 0) {
            this.hp = 0;
        }
    }

    public void applySlow(double modifier, double duration) {
        this.speedModifier = modifier;
        this.slowTimer = duration;
        this.vulnerabilityFactor = 1.5;
    }

    public void applyBurn(double dps, double duration) {
        if (dps >= this.burnDamagePerSecond) {
             this.burnDamagePerSecond = dps;
             this.burnTimer = duration;
        }
    }
    
    public boolean isDead() {
        return this.hp <= 0;
    }

    public int getMoeda() {
        return this.moeda;
    }
}