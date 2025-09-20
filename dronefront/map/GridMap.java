package dronefront.map;
import java.util.ArrayList;
import dronefront.enemy.Enemy;
import java.util.List;

public class GridMap {

    private final TileMap[][] grid;
    private final Caminho caminho;
    private final int largura;
    private final int altura;
    private Position posicaoDaBase;

    public GridMap() {
        this.altura = LAYOUT_DEMO.length;
        this.largura = LAYOUT_DEMO[0].length;
        this.grid = new TileMap[altura][largura];
        this.caminho = new Caminho();

        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                boolean ehCaminho = (LAYOUT_DEMO[y][x] == 1);
                grid[y][x] = new TileMap(x, y, !ehCaminho);
            }
        }

        gerarCaminhoFixo();
    }

    //a matriz abaixo represena apenas o visual do mapa
    //to-do: representar o caminho real por meio de matriz, fica mais facil de editar
    private static final int[][] LAYOUT_DEMO = {
        {0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 1, 1, 1, 0, 0, 1, 1, 1, 0},
        {0, 0, 0, 1, 0, 0, 1, 0, 1, 0},
        {0, 0, 0, 1, 1, 1, 1, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 1, 0}
    };

    //aqui representa o caminho real
    private void gerarCaminhoFixo() {
        List<Position> ordemDoCaminho = new ArrayList<>();
        ordemDoCaminho.add(new Position(1, 0));
        ordemDoCaminho.add(new Position(1, 1));
        ordemDoCaminho.add(new Position(2, 1));
        ordemDoCaminho.add(new Position(3, 1));
        ordemDoCaminho.add(new Position(3, 2));
        ordemDoCaminho.add(new Position(3, 3));
        ordemDoCaminho.add(new Position(4, 3));
        ordemDoCaminho.add(new Position(5, 3));
        ordemDoCaminho.add(new Position(6, 3));
        ordemDoCaminho.add(new Position(6, 2));
        ordemDoCaminho.add(new Position(6, 1));
        ordemDoCaminho.add(new Position(7, 1));
        ordemDoCaminho.add(new Position(8, 1));
        ordemDoCaminho.add(new Position(8, 2));
        ordemDoCaminho.add(new Position(8, 3));
        ordemDoCaminho.add(new Position(8, 4));
        ordemDoCaminho.add(new Position(8, 5));

        for (Position pos : ordemDoCaminho) {
            TileMap peca = this.grid[pos.getY()][pos.getX()];
            this.caminho.adicionarPonto(peca.getCentro());
        }
        //base sempre fica no final do caminho
        this.posicaoDaBase = ordemDoCaminho.get(ordemDoCaminho.size() - 1);
    }

    public Caminho getCaminho() {
        return caminho;
    }

    public void desenharMapaEnemy(List<Enemy> inimigos) {
        char[][] displayGrid = new char[altura][largura];

        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                if (!grid[y][x].podeConstruir()) {
                    displayGrid[y][x] = '#'; //caminho
                } else {
                    displayGrid[y][x] = '.'; //área construível
                }
            }
        }

        displayGrid[posicaoDaBase.getY()][posicaoDaBase.getX()] = 'B'; //base

        for (Enemy inimigo : inimigos) {
            if (!inimigo.chegouNaBase()) {
                Ponto pos = inimigo.getPosition();
                int gridX = (int) Math.round(pos.getX() - 0.5);
                int gridY = (int) Math.round(pos.getY() - 0.5);

                if (gridX >= 0 && gridX < largura && gridY >= 0 && gridY < altura) {
                    char droneChar = inimigo.getCharRepresentation();
                    displayGrid[gridY][gridX] = droneChar;
                }
            }
        }

        StringBuilder builder = new StringBuilder("DroneFront:\n");
        for (int y = 0; y < altura; y++) {
            for (int x = 0; x < largura; x++) {
                builder.append(displayGrid[y][x]).append(" ");
            }
            builder.append("\n");
        }
        System.out.println(builder.toString());
    }
     public static void main(String[] args) {

        GridMap mapa = new GridMap();
        Caminho caminhoGerado = mapa.getCaminho();

        System.out.println(mapa);
        System.out.println(caminhoGerado);
    }
}