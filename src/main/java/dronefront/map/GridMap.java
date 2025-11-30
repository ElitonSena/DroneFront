package dronefront.map;

import java.util.ArrayList;
import java.util.List;

import dronefront.GameConfig;

public class GridMap {

    private final TileMap[][] grid;
    private final Caminho caminho;
    private final int largura;
    private final int altura;

    public GridMap(int[][] layoutPoints) {
        this.altura = GameConfig.MAP_ROWS;
        this.largura = GameConfig.MAP_COLS;
        this.grid = new TileMap[altura][largura];
        this.caminho = new Caminho();

        List<Position> posicoesDoCaminho = gerarPosicoesDoCaminho(layoutPoints);
        
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
    }

    private List<Position> gerarPosicoesDoCaminho(int[][] points) {
        List<Position> ordemDoCaminho = new ArrayList<>();
        
        if (points.length < 2) return ordemDoCaminho;

        for (int i = 0; i < points.length - 1; i++) {
            int[] p1 = points[i];
            int[] p2 = points[i + 1];
            
            boolean skipStart = (i > 0); 
            
            conectarPontos(ordemDoCaminho, p1[0], p1[1], p2[0], p2[1], skipStart);
        }
        
        return ordemDoCaminho;
    }
    
    private void conectarPontos(List<Position> lista, int startX, int startY, int endX, int endY, boolean skipStart) {
        int x = startX;
        int y = startY;
        
        if (skipStart) {
             if (x < endX) x++; else if (x > endX) x--;
             else if (y < endY) y++; else if (y > endY) y--;
        }

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
}