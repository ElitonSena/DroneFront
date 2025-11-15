package dronefront.map;

import dronefront.enemy.Enemy;
import dronefront.projectile.Projectile;
import dronefront.tower.Tower;
import java.util.ArrayList;
import java.util.List;

public class GridMap {

    private final TileMap[][] grid;
    private final Caminho caminho;
    private final int largura;
    private final int altura;
    private Position posicaoDaBase;

    private static final int LARGURA_MAPA = 20;
    private static final int ALTURA_MAPA = 10;

    public GridMap() {
        this.altura = ALTURA_MAPA;
        this.largura = LARGURA_MAPA;
        this.grid = new TileMap[altura][largura];
        this.caminho = new Caminho();

        List<Position> posicoesDoCaminho = gerarPosicoesDoCaminho();
        
        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                final int currentX = x;
                final int currentY = y;
                boolean ehCaminho = posicoesDoCaminho.stream()
                                     .anyMatch(p -> p.getX() == currentX && p.getY() == currentY);
                grid[y][x] = new TileMap(x, y, !ehCaminho);
            }
        }
        
        for (Position pos : posicoesDoCaminho) {
            this.caminho.adicionarPonto(grid[pos.getY()][pos.getX()].getCentro());
        }

        this.posicaoDaBase = posicoesDoCaminho.get(posicoesDoCaminho.size() - 1);
    }

    private List<Position> gerarPosicoesDoCaminho() {
        List<Position> ordemDoCaminho = new ArrayList<>();
        conectarPontos(ordemDoCaminho, 1, 1, 1, 8);
        conectarPontos(ordemDoCaminho, 1, 8, 7, 8);
        conectarPontos(ordemDoCaminho, 7, 8, 7, 1);
        conectarPontos(ordemDoCaminho, 7, 1, 3, 1);
        conectarPontos(ordemDoCaminho, 3, 1, 3, 6);
        conectarPontos(ordemDoCaminho, 3, 6, 11, 6);
        conectarPontos(ordemDoCaminho, 11, 6, 11, 2);
        conectarPontos(ordemDoCaminho, 11, 2, 18, 2);
        conectarPontos(ordemDoCaminho, 18, 2, 18, 8);
        conectarPontos(ordemDoCaminho, 18, 8, 13, 8);
        conectarPontos(ordemDoCaminho, 13, 8, 13, 4);
        return ordemDoCaminho;
    }
    
    private void conectarPontos(List<Position> lista, int startX, int startY, int endX, int endY) {
        int x = startX;
        int y = startY;
        while (x != endX || y != endY) {
            lista.add(new Position(x, y));
            if (x < endX) x++; else if (x > endX) x--;
            else if (y < endY) y++; else if (y > endY) y--;
        }
        lista.add(new Position(endX, endY));
    }

    public Caminho getCaminho() { return caminho; }
    public int getLargura() { return largura; }
    public int getAltura() { return altura; }
    public TileMap getTileAt(int x, int y) {
        if (x >= 0 && x < largura && y >= 0 && y < altura) {
            return grid[y][x];
        }
        return null;
    }

    public void desenharMapa(List<Enemy> inimigos, List<Projectile> projectiles, List<Tower> towers, int cursorX, int cursorY) {
        char[][] displayGrid = new char[altura][largura];

        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                displayGrid[y][x] = grid[y][x].podeConstruir() ? ' ' : '.';
            }
        }
        displayGrid[posicaoDaBase.getY()][posicaoDaBase.getX()] = 'B';

        for (Tower tower : towers) {
            Ponto pos = tower.getPosition();
            int gridY = (int)(pos.getY() - 0.5);
            int gridX = (int)(pos.getX() - 0.5);
            
            if (gridX >= 0 && gridX < largura && gridY >= 0 && gridY < altura) {
                displayGrid[gridY][gridX] = (char) ('0' + Math.min(tower.getLevel(), 9));
            }
        }
        for (Enemy inimigo : inimigos) {
            if (!inimigo.chegouNaBase()) {
                Ponto pos = inimigo.getPosition();
                displayGrid[(int) pos.getY()][(int) pos.getX()] = inimigo.getCharRepresentation();
            }
        }
        for (Projectile p : projectiles) {
            Ponto pos = p.getPosition();
            int gridX = (int) pos.getX();
            int gridY = (int) pos.getY();
            if (gridX >= 0 && gridX < largura && gridY >= 0 && gridY < altura) {
                if (displayGrid[gridY][gridX] == '.' || displayGrid[gridY][gridX] == '#') {
                    displayGrid[gridY][gridX] = p.getCharRepresentation();
                }
            }
        }
        if (cursorX >= 0 && cursorY >= 0) {
            displayGrid[cursorY][cursorX] = 'X';
        }

        StringBuilder builder = new StringBuilder("DroneFront:\n");
        for (char[] row : displayGrid) {
            for (char cell : row) {
                builder.append(cell).append(" ");
            }
            builder.append("\n");
        }
        System.out.println(builder.toString());
    }
}