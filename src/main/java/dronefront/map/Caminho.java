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
}