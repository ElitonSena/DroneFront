package dronefront.game;

import java.util.ArrayList;
import java.util.List;

public class Wave {

    public static class SpawnEvent {
        public final String tipoInimigo;
        public final int quantidade;
        public final double intervalo;

        public SpawnEvent(String tipoInimigo, int quantidade, double intervalo) {
            this.tipoInimigo = tipoInimigo;
            this.quantidade = quantidade;
            this.intervalo = intervalo;
        }
    }

    private final List<SpawnEvent> eventos;
    private boolean iniciada = false;
    private boolean concluida = false;

    public Wave() {
        this.eventos = new ArrayList<>();
    }

    public void adicionarEvento(SpawnEvent evento) {
        this.eventos.add(evento);
    }

    public List<SpawnEvent> getEventos() {
        return eventos;
    }
    
    //estados da onda
    public boolean foiIniciada() { return iniciada; }
    public void iniciar() { this.iniciada = true; }
    public boolean estaConcluida() { return concluida; }
    public void concluir() { this.concluida = true; }
}