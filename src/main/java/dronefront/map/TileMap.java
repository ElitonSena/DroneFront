package dronefront.map;

public class TileMap {

    private final Position posicao;
    private final boolean podeConstruir;

    public TileMap(int x, int y, boolean podeConstruir) {
        this.posicao = new Position(x, y);
        this.podeConstruir = podeConstruir;
    }

    public Position getPosicao() {
        return posicao;
    }

    public boolean podeConstruir() {
        return podeConstruir;
    }

    public Ponto getCentro() {
        return new Ponto(posicao.getX() + 0.5, posicao.getY() + 0.5);
    }
}