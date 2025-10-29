package dronefront.game;

import dronefront.enemy.Enemy;
import dronefront.map.GridMap;
import dronefront.projectile.Projectile;
import dronefront.tower.Tower;
import java.util.List;

public class ConsoleUI {

    public static void draw(GridMap map, List<Enemy> enemies, List<Projectile> projectiles, List<Tower> towers, int vidaBase, int moedas, int wave, int cursorX, int cursorY) {
        clearConsole();
        map.desenharMapa(enemies, projectiles, towers, cursorX, cursorY);
        System.out.printf("Base: %d | Moedas: %d | Onda: %d\n", vidaBase, moedas, wave);
    }

    public static void displayBuildMessage(String message) {
        System.out.println("=========================================================");
        if (!message.isEmpty()) {
            System.out.println(">> " + message);
        }
        System.out.println("CONSTRUCAO: WASD para mover o X");
        System.out.println(" [1] Construir GunTower (100) | [2] Construir PEMTower (150)");
        System.out.println(" [V] Vender Torre (50% do valor)  | [N] Iniciar proxima onda");
        System.out.print("Comando: ");
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
        }
    }
}