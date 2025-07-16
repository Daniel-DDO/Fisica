package br.com.fisica.gui;

import br.com.fisica.basicas.Circuito;
import br.com.fisica.controller.SimuladorRLC;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

import java.net.URL;
import java.util.ResourceBundle;

@Getter
@Setter
public class ControladorTela implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        inicializarCbox();
        naoExibirLabelField();
        configurarBotoes();
        configurarGrafico();
        simulador.setGrafico(graficoCorrente);
        iniciarTimerStatus();

        apenasNumeros(resistenciaField);
        apenasNumeros(indutanciaField);
        apenasNumeros(capacitanciaField);
        apenasNumeros(ddpAmplitudeField);
        apenasNumeros(frequenciaField);
    }

    public void apenasNumeros(TextField field) {
        field.setTextFormatter(new TextFormatter<>(change -> {
            String texto = change.getControlNewText();
            if (texto.matches("\\d*([\\.]?\\d*)?")) {
                return change;
            } else {
                return null;
            }
        }));
    }


    Image circuitoCAlternada = new Image(getClass().getResourceAsStream("/br/com/fisica/images/C-Alternada.png"));
    Image circuitoCContinua = new Image(getClass().getResourceAsStream("/br/com/fisica/images/C-Continua.png"));

    @FXML
    private ImageView imageViewCircuito;
    @FXML
    private ComboBox<String> cboxSelecionarFonte;
    @FXML
    private Button confirmarFonteButton;
    @FXML
    private Label atualFonteLabel;
    @FXML
    private TextField capacitanciaField;
    @FXML
    private TextField indutanciaField;
    @FXML
    private TextField resistenciaField;
    @FXML
    private Button calcularButton;
    @FXML
    private TextField ddpAmplitudeField;
    @FXML
    private TextField frequenciaField;
    @FXML
    private Label labelDdpAmplitude;
    @FXML
    private Label labelFrequencia;
    @FXML
    private LineChart<Number, Number> graficoCorrente;
    @FXML
    private Button iniciarButton;
    @FXML
    private Button pararButton;
    @FXML
    private Button pausarButton;
    @FXML
    private Button resetarButton;
    @FXML
    private Label tempoLabel;
    @FXML
    private Label correnteLabel;
    @FXML
    private Label cargaLabel;
    @FXML
    private Label derivadaLabel;

    private Timeline statusTimer;
    private Timeline estatisticasTimer;
    private final SimuladorRLC simulador = new SimuladorRLC();

    private void configurarGrafico() {
        NumberAxis xAxis = (NumberAxis) graficoCorrente.getXAxis();
        NumberAxis yAxis = (NumberAxis) graficoCorrente.getYAxis();

        xAxis.setLabel("Tempo (ms)");
        xAxis.setAutoRanging(true);
        xAxis.setForceZeroInRange(false);

        yAxis.setLabel("Corrente (mA)");
        yAxis.setAutoRanging(true);
        yAxis.setForceZeroInRange(false);

        graficoCorrente.setCreateSymbols(false);
        graficoCorrente.setLegendVisible(false);
        graficoCorrente.setAnimated(true);
        graficoCorrente.setTitle("Simulação de Circuito RLC - Corrente vs Tempo");


        xAxis.setTickMarkVisible(false);
        xAxis.setMinorTickVisible(false);
        yAxis.setTickMarkVisible(false);
        yAxis.setMinorTickVisible(false);
    }

    private void configurarBotoes() {
        iniciarButton.setVisible(false);
        pararButton.setVisible(false);
        pausarButton.setVisible(false);
        resetarButton.setVisible(false);
        iniciarButton.setOnAction(e -> iniciarSimulacao());
        pararButton.setOnAction(e -> pararSimulacao());
        pausarButton.setOnAction(e -> pausarRetomarSimulacao());
        resetarButton.setOnAction(e -> resetarSimulacao());
    }

    private void iniciarTimerStatus() {

        statusTimer = new Timeline(new KeyFrame(Duration.millis(8.33), e -> atualizarStatus()));
        statusTimer.setCycleCount(Timeline.INDEFINITE);
        statusTimer.play();

    }

    private void atualizarStatus() {
        if (simulador.isRodando()) {
            iniciarButton.setDisable(true);
            pararButton.setDisable(false);
            pausarButton.setDisable(false);
            pausarButton.setText("Pausar");
            resetarButton.setDisable(false);
        } else {
            if (simulador.getTempoAtual() > 0) {
                pausarButton.setText("Retomar");
            } else {
                pausarButton.setText("Pausar");
                pausarButton.setDisable(true);
            }
            iniciarButton.setDisable(false);
            pararButton.setDisable(true);
            resetarButton.setDisable(simulador.getTempoAtual() == 0);
        }

        tempoLabel.setText(String.format("Tempo: %.3f ms", simulador.getTempoAtual() * 1000));
        correnteLabel.setText(String.format("Corrente: %.3f mA", simulador.getCorrenteAtualMiliamperes()));
    }


    @FXML
    public void iniciarSimulacao() {
        if (!verificandoFields()) {
            ControladorAlertas.alertaErro("Erro", "Preencha todos os campos");
        }
        Circuito circuito = cboxSelecionarFonte.getValue().equals("Corrente Contínua (CC)") ? circuitoCContinua() : circuitoCAlternada();

        if (circuito != null) {
            simulador.iniciarSimulacao(circuito);
        }
    }

    @FXML
    public void pararSimulacao() {
        simulador.pararSimulacao();
    }

    @FXML
    public void pausarRetomarSimulacao() {
        if (simulador.isRodando()) {
            simulador.pausarSimulacao();
        } else {
            simulador.retomarSimulacao();
        }
    }

    @FXML
    public void resetarSimulacao() {
        simulador.resetarSimulacao();
        tempoLabel.setText("Tempo: 0.000 ms");
        correnteLabel.setText("Corrente: 0.000 mA");
    }

    @FXML
    public Circuito circuitoCContinua() {
        if (verificandoFields()) {
            try {
                double resistencia = Double.parseDouble(resistenciaField.getText());
                double indutancia = Double.parseDouble(indutanciaField.getText()) * 0.001; // mH para H
                double capacitancia = Double.parseDouble(capacitanciaField.getText()) * 1e-6; // μF para F
                double ddp = Double.parseDouble(ddpAmplitudeField.getText());
                return new Circuito(resistencia, indutancia, capacitancia, true, ddp);
            } catch (NumberFormatException e) {
                System.err.println("Erro de Entrada: Por favor, insira valores numéricos válidos.");
                return null;
            }
        }
        return null;
    }

    @FXML
    public Circuito circuitoCAlternada() {
        if (verificandoFields()) {
            try {
                double resistencia = Double.parseDouble(resistenciaField.getText());
                double indutancia = Double.parseDouble(indutanciaField.getText()) * 0.001; // mH para H
                double capacitancia = Double.parseDouble(capacitanciaField.getText()) * 1e-6; // μF para F
                double amplitude = Double.parseDouble(ddpAmplitudeField.getText());
                double frequencia = Double.parseDouble(frequenciaField.getText());
                return new Circuito(resistencia, indutancia, capacitancia, false, amplitude, frequencia);
            } catch (NumberFormatException e) {
                System.err.println("Erro de Entrada: Por favor, insira valores numéricos válidos.");
                return null;
            }
        }
        return null;
    }

    public boolean verificandoFields() {
        return !resistenciaField.getText().isEmpty() && !indutanciaField.getText().isEmpty() &&
                !capacitanciaField.getText().isEmpty() && !ddpAmplitudeField.getText().isEmpty() &&
                (!cboxSelecionarFonte.getValue().equals("Corrente Alternada (CA)") || !frequenciaField.getText().isEmpty());
    }

    public void inicializarCbox() {
        cboxSelecionarFonte.getItems().add("Corrente Contínua (CC)");
        cboxSelecionarFonte.getItems().add("Corrente Alternada (CA)");
    }

    public void naoExibirLabelField() {
        labelDdpAmplitude.setVisible(false);
        ddpAmplitudeField.setVisible(false);
        labelFrequencia.setVisible(false);
        frequenciaField.setVisible(false);
        iniciarButton.setVisible(false);
        pararButton.setVisible(false);
        pausarButton.setVisible(false);
        resetarButton.setVisible(false);
    }

    public void selecionarCircuito() {
        if (cboxSelecionarFonte.getValue() != null) {
            boolean isCC = cboxSelecionarFonte.getValue().equals("Corrente Contínua (CC)");
            imageViewCircuito.setImage(isCC ? circuitoCContinua : circuitoCAlternada);
            atualFonteLabel.setText("Selecionado: " + cboxSelecionarFonte.getValue());
            iniciarButton.setVisible(true);
            pararButton.setVisible(true);
            pausarButton.setVisible(true);
            resetarButton.setVisible(true);
            labelDdpAmplitude.setVisible(true);
            labelDdpAmplitude.setText(isCC ? "DDP (V):" : "Amplitude (V):");
            ddpAmplitudeField.setVisible(true);
            ddpAmplitudeField.setPromptText(isCC ? "Digite a DDP" : "Digite a amplitude");
            labelFrequencia.setVisible(!isCC);
            frequenciaField.setVisible(!isCC);
        }
    }

    @FXML
    public void buttonConfirmarFonte(ActionEvent event) {
        if (cboxSelecionarFonte.getValue() == null || cboxSelecionarFonte.getValue().isEmpty()) {
            ControladorAlertas.alertaErro("Erro", "Selecione uma fonte para o circuito.");
        } else {
            selecionarCircuito();
        }
    }
}