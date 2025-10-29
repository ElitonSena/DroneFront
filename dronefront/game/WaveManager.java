package dronefront.game;

import dronefront.enemy.BomberDrone;
import dronefront.enemy.Enemy;
import dronefront.enemy.ScoutDrone;
import dronefront.map.Ponto;
import java.util.ArrayList;
import java.util.List;

public class WaveManager {

    private final List<Wave> todasAsWaves = new ArrayList<>();
    private final Ponto pontoInicial;
    private int waveAtualIndex = 0;
    private final List<Enemy> inimigosAtivos = new ArrayList<>();

    private static class SpawnState {
        Wave.SpawnEvent evento;
        int spawnados = 0;
        double timer = 0;

        SpawnState(Wave.SpawnEvent evento) {
            this.evento = evento;
        }
    }

    public boolean isWaveFinished() {
        if (waveAtualIndex >= todasAsWaves.size()) {
            return true;
        }
        Wave waveAtual = todasAsWaves.get(waveAtualIndex);
        return waveAtual.estaConcluida();
    }

    private final List<SpawnState> estadosSpawnAtuais = new ArrayList<>();

    public WaveManager(Ponto pontoInicial) {
        this.pontoInicial = pontoInicial;
        definirWaves();
    }

    private void definirWaves() {
        // w1
        Wave wave1 = new Wave();
        wave1.adicionarEvento(new Wave.SpawnEvent("SCOUT", 3, 2.0));
        this.todasAsWaves.add(wave1);
        // w2
        Wave wave2 = new Wave();
        wave2.adicionarEvento(new Wave.SpawnEvent("SCOUT", 5, 1.5));
        wave2.adicionarEvento(new Wave.SpawnEvent("BOMBER", 1, 5.0));
        this.todasAsWaves.add(wave2);
        // w3
        Wave wave3 = new Wave();
        wave3.adicionarEvento(new Wave.SpawnEvent("BOMBER", 3, 3.0));
        wave3.adicionarEvento(new Wave.SpawnEvent("SCOUT", 8, 1.0));
        this.todasAsWaves.add(wave3);
        //to do: implementar uma função pra descrever a qtd de drones com base no índice da onda
    }

    public void update(double deltaTime) {
        if (waveAtualIndex >= todasAsWaves.size()) {
            return;
        }

        Wave waveAtual = todasAsWaves.get(waveAtualIndex);

        if (waveAtual.estaConcluida() && inimigosAtivos.isEmpty()) {
            waveAtualIndex++;
            if (waveAtualIndex < todasAsWaves.size()) {
                iniciarProximaWave();
            }
            return;
        }

        if (!waveAtual.foiIniciada()) {
            iniciarProximaWave();
        }

        boolean todosEventosConcluidos = true;
        for (SpawnState state : estadosSpawnAtuais) {
            if (state.spawnados < state.evento.quantidade) {
                todosEventosConcluidos = false;
                state.timer += deltaTime;
                if (state.timer >= state.evento.intervalo) {
                    state.timer = 0;
                    spawnInimigo(state.evento.tipoInimigo);
                    state.spawnados++;
                }
            }
        }
        
        if (todosEventosConcluidos) {
            waveAtual.concluir();
        }

        inimigosAtivos.removeIf(Enemy::chegouNaBase);
    }
    
    private void iniciarProximaWave() {
        if (waveAtualIndex < todasAsWaves.size()) {
            Wave novaWave = todasAsWaves.get(waveAtualIndex);
            novaWave.iniciar();
            estadosSpawnAtuais.clear();
            for (Wave.SpawnEvent evento : novaWave.getEventos()) {
                estadosSpawnAtuais.add(new SpawnState(evento));
            }
        }
    }

    private void spawnInimigo(String tipo) {
        Enemy novoInimigo = null;
        if ("SCOUT".equalsIgnoreCase(tipo)) {
            novoInimigo = new ScoutDrone(pontoInicial);
        } else if ("BOMBER".equalsIgnoreCase(tipo)) {
            novoInimigo = new BomberDrone(pontoInicial);
        }

        if (novoInimigo != null) {
            inimigosAtivos.add(novoInimigo);
        }
    }
    
    public List<Enemy> getInimigosAtivos() {
        return inimigosAtivos;
    }

    public int getWaveAtual() {
        return waveAtualIndex + 1;
    }

    public boolean isFinished() {
        return waveAtualIndex >= todasAsWaves.size() && inimigosAtivos.isEmpty();
    }
}