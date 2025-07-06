package br.com.fisica.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ControladorCircuito {

    private static ControladorCircuito instancia;

    public static ControladorCircuito getInstancia() {
        if (instancia == null) {
            instancia = new ControladorCircuito();
        }
        return instancia;
    }



}
