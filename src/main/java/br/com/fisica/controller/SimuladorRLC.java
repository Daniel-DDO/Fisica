package br.com.fisica.controller;

import br.com.fisica.basicas.Circuito;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Esta classe simula um circuito RLC em série, tanto para fontes de
 * corrente contínua (CC) quanto alternada (CA).
 * A simulação utiliza o metodo numérico de Runge-Kutta de 4ª ordem (RK4)
 * para resolver o sistema de equações diferenciais que descreve o circuito.
 */
public class SimuladorRLC {

    private AnimationTimer animationTimer;
    private final AtomicBoolean isRunning = new AtomicBoolean(false);
    @Getter
    private double tempoAtual = 0.0;

    // VETOR DE ESTADO: [q, i]
    // estadoAtual[0] = q(t): Carga no capacitor em Coulombs.
    // estadoAtual[1] = i(t): Corrente no circuito em Amperes.
    private double[] estadoAtual = {0.0, 0.0};

    @Getter
    private double derivadaCorrenteAtual = 0.0;

    private XYChart.Series<Number, Number> series;
    private LineChart<Number, Number> grafico;

    private double h = 1e-6; // Passo de tempo para o solver numérico (h).

    // OTIMIZAÇÃO: Reduzindo drasticamente o intervalo de atualização para maior fluidez
    private final double intervaloAtualizacaoGrafico = 2e-6;
    private double proximaAtualizacaoGrafico = 0.0;
    private final int maxPontosGrafico = 8000;

    // OTIMIZAÇÃO: Contador para reduzir chamadas Platform.runLater
    private int contadorAtualizacoes = 0;
    private final int batchSize = 3; // Agrupa 3 atualizações em uma única chamada Platform.runLater

    /**
     * Define o gráfico (LineChart) que será atualizado pela simulação.
     * @param grafico O componente LineChart da interface gráfica.
     */
    public void setGrafico(LineChart<Number, Number> grafico) {
        this.grafico = grafico;
        this.series = new XYChart.Series<>();
        Platform.runLater(() -> {
            this.grafico.getData().clear();
            this.grafico.getData().add(series);
        });
        this.grafico.setCreateSymbols(false);

        this.grafico.setAnimated(false);
    }

    /**
     * Inicia a simulação com os parâmetros do circuito fornecido.
     * @param circuito O objeto Circuito contendo R, L, C e os parâmetros da fonte.
     */
    public void iniciarSimulacao(Circuito circuito) {
        if (isRunning.getAndSet(true)) {
            return;
        }
        resetarSimulacao();
        isRunning.set(true);

        animationTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                for (int i = 0; i < 5; i++) {
                    passoDeSimulacao(circuito);
                }
            }
        };
        animationTimer.start();
    }

    /**
     * Executa um único passo de cálculo da simulação usando o metodo RK4.
     * @param c O objeto Circuito.
     */
    private void passoDeSimulacao(Circuito c) {
        if (c.getIndutor() <= 0 || c.getCapacitor() <= 0) {
            pararSimulacao();
            System.err.println("Erro: Indutância e Capacitância devem ser maiores que zero.");
            return;
        }

        estadoAtual = rk4(c, estadoAtual, tempoAtual, h);
        tempoAtual += h;

        if (tempoAtual >= proximaAtualizacaoGrafico) {
            double tempoMs = tempoAtual * 1000.0;
            double correnteMa = estadoAtual[1] * 1000.0;

            contadorAtualizacoes++;

            if (contadorAtualizacoes >= batchSize) {
                Platform.runLater(() -> {
                    series.getData().add(new XYChart.Data<>(tempoMs, correnteMa));

                    if (series.getData().size() > maxPontosGrafico) {
                        // Remove múltiplos pontos antigos de uma vez para melhor performance
                        int pontosParaRemover = series.getData().size() - maxPontosGrafico + 100;
                        for (int i = 0; i < pontosParaRemover && !series.getData().isEmpty(); i++) {
                            series.getData().remove(0);
                        }
                    }
                });
                contadorAtualizacoes = 0;
            }

            proximaAtualizacaoGrafico += intervaloAtualizacaoGrafico;
        }
    }

    private double[] rk4(Circuito c, double[] estado, double t, double h) {
        double[] k1 = derivadas(c, estado, t);

        double[] estado_k2 = new double[2];
        estado_k2[0] = estado[0] + 0.5 * h * k1[0];
        estado_k2[1] = estado[1] + 0.5 * h * k1[1];
        double[] k2 = derivadas(c, estado_k2, t + 0.5 * h);

        double[] estado_k3 = new double[2];
        estado_k3[0] = estado[0] + 0.5 * h * k2[0];
        estado_k3[1] = estado[1] + 0.5 * h * k2[1];
        double[] k3 = derivadas(c, estado_k3, t + 0.5 * h);

        double[] estado_k4 = new double[2];
        estado_k4[0] = estado[0] + h * k3[0];
        estado_k4[1] = estado[1] + h * k3[1];
        double[] k4 = derivadas(c, estado_k4, t + h);

        double[] novoEstado = new double[2];
        novoEstado[0] = estado[0] + (h / 6.0) * (k1[0] + 2 * k2[0] + 2 * k3[0] + k4[0]);
        novoEstado[1] = estado[1] + (h / 6.0) * (k1[1] + 2 * k2[1] + 2 * k3[1] + k4[1]);

        return novoEstado;
    }

    private double[] derivadas(Circuito c, double[] estado, double t) {
        double q = estado[0];
        double i = estado[1];
        double dq_dt = i;
        double v_s = tensaoFonte(c, t);

        double di_dt = (v_s - c.getResistor() * i - q / c.getCapacitor()) / c.getIndutor();

        this.derivadaCorrenteAtual = di_dt;
        return new double[]{dq_dt, di_dt};
    }

    private double tensaoFonte(Circuito c, double t) {
        if (c.isCorrenteContinua()) {
            return c.getDdp();
        } else {
            return c.getAmplitudeFonte() * Math.sin(2 * Math.PI * c.getFrequenciaFonte() * t);
        }
    }

    public void pararSimulacao() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        isRunning.set(false);
    }

    public void pausarSimulacao() {
        if (isRunning.get()) {
            animationTimer.stop();
            isRunning.set(false);
        }
    }

    public void retomarSimulacao() {
        if (!isRunning.get() && tempoAtual > 0) {
            animationTimer.start();
            isRunning.set(true);
        }
    }

    //Reseta a simulação para o estado inicial (t=0, q=0, i=0) e limpa o gráfico.
    public void resetarSimulacao() {
        pararSimulacao();
        tempoAtual = 0.0;
        estadoAtual[0] = 0.0;
        estadoAtual[1] = 0.0;
        derivadaCorrenteAtual = 0.0;
        proximaAtualizacaoGrafico = 0.0;
        contadorAtualizacoes = 0; // Reset do contador de batch

        if (grafico != null && series != null) {
            Platform.runLater(() -> series.getData().clear());
        }
    }

    public boolean isRodando() {
        return isRunning.get();
    }

    public double getCorrenteAtual() {
        return estadoAtual[1];
    }

    public double getCorrenteAtualMiliamperes() {
        return getCorrenteAtual() * 1000.0;
    }
}