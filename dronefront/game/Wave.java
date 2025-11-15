package dronefront.game;

public class Wave {

    private final int totalInimigos;
    private final double intervaloSpawn;
    private final double probScout;
    private final double probBomber;
    private final double probTank;

    private boolean iniciada = false;
    private boolean concluida = false;

    public Wave(int totalInimigos, double intervaloSpawn, double probScout, double probBomber, double probTank) {
        this.totalInimigos = totalInimigos;
        this.intervaloSpawn = intervaloSpawn;

        double probTotal = probScout + probBomber + probTank;
        if (probTotal <= 0) {
            this.probScout = 1.0;
            this.probBomber = 0.0;
            this.probTank = 0.0;
        } else {
            this.probScout = probScout / probTotal;
            this.probBomber = probBomber / probTotal;
            this.probTank = probTank / probTotal;
        }
    }

    public int getTotalInimigos() {
        return totalInimigos;
    }

    public double getIntervaloSpawn() {
        return intervaloSpawn;
    }

    public double getProbScout() {
        return probScout;
    }

    public double getProbBomber() {
        return probBomber;
    }

    public double getProbTank() {
        return probTank;
    }

    public boolean foiIniciada() { return iniciada; }
    public void iniciar() { this.iniciada = true; }
    public boolean estaConcluida() { return concluida; }
    public void concluir() { this.concluida = true; }
}