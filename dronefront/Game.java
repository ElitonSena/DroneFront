package dronefront;

import dronefront.enemy.Enemy;
import dronefront.game.ConsoleUI;
import dronefront.game.WaveManager;
import dronefront.map.Caminho;
import dronefront.map.GridMap;
import dronefront.map.Ponto;
import dronefront.map.Position;
import dronefront.map.TileMap;
import dronefront.projectile.Projectile;
import dronefront.tower.FireTower;
import dronefront.tower.GunTower;
import dronefront.tower.PEMTower;
import dronefront.tower.Tower;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class Game {

    private enum GameState { BUILD_PHASE, WAVE_IN_PROGRESS }
    private boolean gameWon = false;

    private final GridMap mapa;
    private final Caminho caminho;
    private final WaveManager waveManager;
    private final List<Tower> towers = new ArrayList<>();
    private final List<Projectile> projectiles = new ArrayList<>();
    private int vidaBase = 20;
    private int moedas = 250;
    private GameState currentState = GameState.BUILD_PHASE;
    private String buildMessage = "Bem-vindo! Construa suas defesas.";
    private Position cursor = new Position(0, 0);

    public Game() {
        mapa = new GridMap();
        caminho = mapa.getCaminho();
        Ponto pontoInicial = caminho.getPontosDoCaminho().get(0);
        waveManager = new WaveManager(pontoInicial);
    }

    public void run() throws InterruptedException {
    Scanner scanner = new Scanner(System.in);

    while (vidaBase > 0 && !gameWon) {
        drawCurrentState();

        if (currentState == GameState.WAVE_IN_PROGRESS) {
            handleWaveInProgress();
        } else {
            handleBuildPhase(scanner);
        }
    }
    scanner.close();
    System.out.println(vidaBase <= 0 ? "\nVoce foi derrotado! :(" : "\nVoce venceu! :)");
}

    private void drawCurrentState() {
        int cursorX = (currentState == GameState.BUILD_PHASE) ? cursor.getX() : -1;
        int cursorY = (currentState == GameState.BUILD_PHASE) ? cursor.getY() : -1;
        ConsoleUI.draw(mapa, waveManager.getInimigosAtivos(), projectiles, towers, vidaBase, moedas, waveManager.getWaveAtual(), cursorX, cursorY);
}

    private void handleWaveInProgress() throws InterruptedException {
        final double TICK_S = 0.1;
        waveManager.update(TICK_S);
        List<Enemy> inimigosAtivos = waveManager.getInimigosAtivos();

        towers.forEach(tower -> {
            tower.update(TICK_S, inimigosAtivos);
            projectiles.addAll(tower.collectNewProjectiles());
        });

        projectiles.forEach(p -> p.update(TICK_S));
        projectiles.removeIf(Projectile::hasHitTarget);

        Iterator<Enemy> enemyIterator = inimigosAtivos.iterator();
        while (enemyIterator.hasNext()) {
            Enemy inimigo = enemyIterator.next();
            if (inimigo.update(TICK_S, caminho)) {
                vidaBase -= inimigo.getDano();
            }
            if (inimigo.isDead()) {
                moedas += inimigo.getMoeda();
                enemyIterator.remove();
            }
        }

            if (waveManager.isWaveFinished() && waveManager.getInimigosAtivos().isEmpty()) {
            if (waveManager.isFinished()) {
                gameWon = true;
            } else {
                currentState = GameState.BUILD_PHASE;
            }
        }
        Thread.sleep((long)(TICK_S * 1000));
    }
    private void handleBuildPhase(Scanner scanner) {
        ConsoleUI.displayBuildMessage(buildMessage);
        buildMessage = "";
        String input = scanner.next().toLowerCase();
        if (input.isEmpty()) return;

        char command = input.charAt(0);
        switch (command) {
            case 'w': if (cursor.getY() > 0) cursor = new Position(cursor.getX(), cursor.getY() - 1); break;
            case 's': if (cursor.getY() < mapa.getAltura() - 1) cursor = new Position(cursor.getX(), cursor.getY() + 1); break;
            case 'a': if (cursor.getX() > 0) cursor = new Position(cursor.getX() - 1, cursor.getY()); break;
            case 'd': if (cursor.getX() < mapa.getLargura() - 1) cursor = new Position(cursor.getX() + 1, cursor.getY()); break;
            case '1': tryBuildTower('1'); break;
            case '2': tryBuildTower('2'); break;
            case '3': tryBuildTower('3'); break; 
            case 'u': tryUpgradeTower(); break;
            case 'v': trySellTower(); break;
            case 'n': currentState = GameState.WAVE_IN_PROGRESS; break;
        }
    }

    private void tryBuildTower(char towerType) {
        int x = cursor.getX();
        int y = cursor.getY();
        TileMap tile = mapa.getTileAt(x, y);

        if (tile == null || !tile.podeConstruir()) {
            buildMessage = "Nao pode construir ai (caminho ou fora do mapa).";
            return;
        }
        if (towers.stream().anyMatch(t -> (int)(t.getPosition().getX() - 0.5) == x && (int)(t.getPosition().getY() - 0.5) == y)) {
            buildMessage = "Ja existe uma torre neste local.";
            return;
        }

        Tower newTower;

        if (towerType == '1') {
            newTower = new GunTower(new Ponto(x + 0.5, y + 0.5));
        } else if (towerType == '2') {
            newTower = new PEMTower(new Ponto(x + 0.5, y + 0.5));
        } else if (towerType == '3') {
            newTower = new FireTower(new Ponto(x + 0.5, y + 0.5));
        } else {
            return;
        }

        if (moedas < newTower.getCost()) {
            buildMessage = "Moedas insuficientes! Custo: " + newTower.getCost();
        } else {
            moedas -= newTower.getCost();
            towers.add(newTower);
            
            String towerName = "Torre";
            if (towerType == '1') towerName = "GunTower";
            else if (towerType == '2') towerName = "PEMTower";
            else if (towerType == '3') towerName = "FireTower";
            
            buildMessage = towerName + " construida em (" + x + "," + y + ").";
        }
    }

    private void trySellTower() {
        int x = cursor.getX();
        int y = cursor.getY();
        Optional<Tower> towerToSell = towers.stream()
            .filter(t -> (int)(t.getPosition().getX() - 0.5) == x && (int)(t.getPosition().getY() - 0.5) == y)
            .findFirst();

        if (towerToSell.isPresent()) {
            Tower foundTower = towerToSell.get();
            int refund = foundTower.getCost() / 2;
            moedas += refund;
            towers.remove(foundTower);
            buildMessage = "Torre vendida em (" + x + "," + y + ") por +" + refund + " moedas.";
        } else {
            buildMessage = "Nenhuma torre para vender em (" + x + "," + y + ").";
        }
    }

    private void tryUpgradeTower() {
        int x = cursor.getX();
        int y = cursor.getY();
        Optional<Tower> towerToUpgrade = towers.stream()
            .filter(t -> (int)(t.getPosition().getX() - 0.5) == x && (int)(t.getPosition().getY() - 0.5) == y)
            .findFirst();

        if (!towerToUpgrade.isPresent()) {
            buildMessage = "Nenhuma torre para aprimorar aqui.";
            return;
        }

        Tower tower = towerToUpgrade.get();
        if (tower.isMaxLevel()) {
            buildMessage = "Torre ja esta no nivel maximo (Nivel " + tower.getLevel() + ").";
            return;
        }

        int cost = tower.getUpgradeCost();
        if (moedas < cost) {
            buildMessage = "Moedas insuficientes! Custo do Aprimoramento: " + cost;
        } else {
            moedas -= cost;
            tower.upgrade();
            buildMessage = "Torre aprimorada para o Nivel " + tower.getLevel() + " por " + cost + " moedas.";
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new Game().run();
    }
}