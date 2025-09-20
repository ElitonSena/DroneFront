package dronefront.map;

import java.util.ArrayList;
import java.util.List;

public class Caminho {

    private final List<Ponto> pontosDoCaminho;

    public Caminho() {
        this.pontosDoCaminho = new ArrayList<>();
    }

    public void adicionarPonto(Ponto novoPonto) {
        this.pontosDoCaminho.add(novoPonto);
    }

    public List<Ponto> getPontosDoCaminho() {
        return new ArrayList<>(pontosDoCaminho);
    }

    @Override
    public String toString() {
        if (pontosDoCaminho.isEmpty()) {
            return "caminho vazio.";
        }

        StringBuilder builder = new StringBuilder("coordenadas do caminho:\n");
        for (int i = 0; i < pontosDoCaminho.size(); i++) {
            builder.append(String.format("  %d: %s\n", i + 1, pontosDoCaminho.get(i)));
        }
        return builder.toString();
    }
}