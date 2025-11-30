package dronefront.wave;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dronefront.GameConfig;
import dronefront.enemy.BomberDrone;
import dronefront.enemy.Enemy;
import dronefront.enemy.ScoutDrone;
import dronefront.enemy.TankDrone;
import dronefront.map.Ponto;

public class WaveManager {

    private final List<Wave> todasAsWaves = new ArrayList<>();
    private final Ponto pontoInicial;
    private int waveAtualIndex = 0;
    private final List<Enemy> inimigosAtivos = new ArrayList<>();
    
    private final Random random = new Random();

    private int inimigosSpawnadosNaWave = 0;
    private double spawnTimer = 0;

    public boolean isWaveFinished() {
        if (waveAtualIndex >= todasAsWaves.size()) {
            return true;
        }
        Wave waveAtual = todasAsWaves.get(waveAtualIndex);
        return waveAtual.estaConcluida();
    }

    public WaveManager(Ponto pontoInicial) {
        this.pontoInicial = pontoInicial;
        this.todasAsWaves.addAll(GameConfig.getWaves());
    }

    public void update(double deltaTime) {
        if (waveAtualIndex >= todasAsWaves.size()) {
            return;
        }

        Wave waveAtual = todasAsWaves.get(waveAtualIndex);

        if (waveAtual.estaConcluida() && inimigosAtivos.isEmpty()) {
            waveAtualIndex++;
            if (waveAtualIndex >= todasAsWaves.size()) {
                return;
            }
            waveAtual = todasAsWaves.get(waveAtualIndex);
        }

        if (!waveAtual.foiIniciada()) {
            iniciarProximaWave();
        }

        if (waveAtual.foiIniciada() && !waveAtual.estaConcluida()) {
            spawnTimer += deltaTime;

            if (spawnTimer >= waveAtual.getIntervaloSpawn()) {
                spawnTimer = 0;

                if (inimigosSpawnadosNaWave < waveAtual.getTotalInimigos()) {
                    String tipoInimigo = escolherInimigoAleatorio(waveAtual);
                    spawnInimigo(tipoInimigo);
                    inimigosSpawnadosNaWave++;
                }
            }
            
            if (inimigosSpawnadosNaWave >= waveAtual.getTotalInimigos()) {
                waveAtual.concluir();
            }
        }

        inimigosAtivos.removeIf(Enemy::chegouNaBase);
    }

    private String escolherInimigoAleatorio(Wave wave) {
        double roll = random.nextDouble();

        if (roll < wave.getProbScout()) {
            return "SCOUT";
        } else if (roll < wave.getProbScout() + wave.getProbBomber()) {
            return "BOMBER";
        } else {
            return "TANK";
        }
    }

    private void iniciarProximaWave() {
        if (waveAtualIndex < todasAsWaves.size()) {
            Wave novaWave = todasAsWaves.get(waveAtualIndex);
            novaWave.iniciar();
            this.inimigosSpawnadosNaWave = 0;
            this.spawnTimer = 0;
        }
    }

    private void spawnInimigo(String tipo) {
        Enemy novoInimigo = null;
        if ("SCOUT".equalsIgnoreCase(tipo)) {
            novoInimigo = new ScoutDrone(pontoInicial);
        } else if ("BOMBER".equalsIgnoreCase(tipo)) {
            novoInimigo = new BomberDrone(pontoInicial);
        } else if ("TANK".equalsIgnoreCase(tipo)) {
            novoInimigo = new TankDrone(pontoInicial);
        }

        if (novoInimigo != null) {
            inimigosAtivos.add(novoInimigo);
        }
    }
    
    public List<Enemy> getInimigosAtivos() {
        return inimigosAtivos;
    }

    public int getWaveAtual() {
        return Math.min(waveAtualIndex + 1, todasAsWaves.size());
    }

    public boolean isFinished() {
        return waveAtualIndex >= todasAsWaves.size() && inimigosAtivos.isEmpty();
    }
}