package br.com.fisica.basicas;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Circuito {

    private double resistor;
    private double indutor;
    private double capacitor;
    private double corrente;
    private boolean correnteContinua; //quando false -> correnteAlternada
    private double ddp;
    private double amplitudeFonte;
    private double frequenciaFonte;

    public Circuito() {}

    public Circuito(double resistor, double indutor, double capacitor, double corrente, boolean correnteContinua, double ddp, double amplitudeFonte, double frequenciaFonte) {
        this.resistor = resistor;
        this.indutor = indutor;
        this.capacitor = capacitor;
        this.corrente = corrente;
        this.correnteContinua = correnteContinua;
        this.ddp = ddp;
        this.amplitudeFonte = amplitudeFonte;
        this.frequenciaFonte = frequenciaFonte;
    }
}
