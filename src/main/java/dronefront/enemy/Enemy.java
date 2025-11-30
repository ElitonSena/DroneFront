package dronefront.enemy;

import java.util.List;
import dronefront.map.Caminho;
import dronefront.map.Ponto;
import dronefront.GameConfig;
import dronefront.audio.SoundManager;

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
    protected double rotation = 0;

    protected double deathTimer = 0;

    public Enemy(int hp, double speed, int moeda, int dano, Ponto startPosition) {
        this.hp = hp;
        this.speed = speed;
        this.moeda = moeda;
        this.dano = dano;
        this.position = startPosition;
        this.proxCaminho = 1;
    }

    public boolean update(double deltaTime, Caminho caminho) {
        if (this.hp <= 0) {
            deathTimer += deltaTime;
            return deathTimer >= GameConfig.ENEMY_TIME_TO_DISAPPEAR; 
        }

        if (chegouNaBase) return true;
    
        handleStatusEffects(deltaTime);
        return moveAlongPath(deltaTime, caminho);
    }

    private void handleStatusEffects(double deltaTime) {
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
    }

    private boolean moveAlongPath(double deltaTime, Caminho caminho) {
        List<Ponto> pontosDoCaminho = caminho.getPontosDoCaminho();
        
        if (proxCaminho >= pontosDoCaminho.size()) {
            this.chegouNaBase = true;
            return true;
        }

        Ponto alvo = pontosDoCaminho.get(proxCaminho);
        double distanciaParaAlvo = calculateDistance(this.position, alvo);
        double movimentoPossivel = (speed * speedModifier) * deltaTime;

        if (distanciaParaAlvo <= movimentoPossivel) {
            this.position = alvo;
            this.proxCaminho++;
            
            if (proxCaminho >= pontosDoCaminho.size()) {
                this.chegouNaBase = true;
                return true;
            }
        } else {
            calculateNextPosition(alvo, distanciaParaAlvo, movimentoPossivel);
        }

        return false;
    }

    private void calculateNextPosition(Ponto alvo, double distanciaTotal, double movimentoPasso) {
        double dirX = (alvo.getX() - position.getX()) / distanciaTotal;
        double dirY = (alvo.getY() - position.getY()) / distanciaTotal;
        
        double angleRad = Math.atan2(dirY, dirX);
        this.rotation = Math.toDegrees(angleRad) - 90;

        double novoX = position.getX() + dirX * movimentoPasso;
        double novoY = position.getY() + dirY * movimentoPasso;
        this.position = new Ponto(novoX, novoY);
    }

    private double calculateDistance(Ponto p1, Ponto p2) {
        return Math.sqrt(Math.pow(p2.getX() - p1.getX(), 2) + Math.pow(p2.getY() - p1.getY(), 2));
    }

    public boolean chegouNaBase() {
        return chegouNaBase;
    }
    
    public Ponto getPosition() {
        return position;
    }
    
    public int getDano() {
        return this.dano;
    }

    public int getHp() {
        return this.hp;
    }

    public int getProxCaminho() {
        return this.proxCaminho;
    }

    public double getRotation() { 
        return rotation;
    }

    public boolean isWreckage() {
        return hp <= 0;
    }
    
    public void takeDamage(int amount) {
        int actualDamage = (int)(amount * this.vulnerabilityFactor);
        
        this.hp -= actualDamage;
        if (this.hp < 0) {
            this.hp = 0;
            SoundManager.getInstance().play("enemy_explode.mp3");
        }
    }

    public void applySlow(double modifier, double duration) {
        this.speedModifier = modifier;
        this.slowTimer = duration;
        this.vulnerabilityFactor = GameConfig.SLOW_VULNERABILITY_FACTOR;
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

    public double getSpeedAtual() {
        return this.speed * this.speedModifier;
    }
}