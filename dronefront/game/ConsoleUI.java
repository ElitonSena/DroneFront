package dronefront.game;

import dronefront.enemy.Enemy;
import dronefront.map.GridMap;
import java.util.List;

public class ConsoleUI {

    public static void draw(GridMap map, List<Enemy> enemies, int vidaBase, int moedas, int wave) {
        clearConsole();
        map.desenharMapaEnemy(enemies);
        System.out.printf("Vida da Base: %d | Moedas: %d | Onda: %d\n", vidaBase, moedas, wave);
    }

    public static void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }
}