package dronefront;

import dronefront.enemy.Enemy;
import dronefront.game.ConsoleUI;
import dronefront.game.WaveManager;
import dronefront.map.Caminho;
import dronefront.map.GridMap;
import dronefront.map.Ponto;
import java.util.ArrayList;
import java.util.List;

public class Game {

    public static void main(String[] args) throws InterruptedException {
        GridMap mapa = new GridMap();
        Caminho caminho = mapa.getCaminho();
        Ponto pontoInicial = caminho.getPontosDoCaminho().get(0);

        // parâmetros iniciais
        int vidaBase = 20;
        int moedas = 100;

        WaveManager waveManager = new WaveManager(pontoInicial);

        // loop
        final int TICK_MS = 100;
        final double TICK_S = TICK_MS / 1000.0;
        
        while (vidaBase > 0 && !waveManager.isFinished()) {
            waveManager.update(TICK_S);

            List<Enemy> inimigosAtivos = waveManager.getInimigosAtivos();
            
            for (Enemy inimigo : inimigosAtivos) {
                boolean chegou = inimigo.update(TICK_S, caminho);
                if (chegou) {
                    vidaBase -= inimigo.getDano();
                }
            }

            ConsoleUI.draw(mapa, inimigosAtivos, vidaBase, moedas, waveManager.getWaveAtual());

            Thread.sleep(TICK_MS);
        }

        ConsoleUI.draw(mapa, new ArrayList<>(), vidaBase, moedas, waveManager.getWaveAtual());

        if (vidaBase <= 0) {
            System.out.println("\nvc morreu :(");
        } else {
            System.out.println("\nvc ganhou :)");
        }
    }
}