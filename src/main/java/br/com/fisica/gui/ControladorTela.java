package br.com.fisica.gui;

import br.com.fisica.basicas.Circuito;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
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

        apenasNumeros(resistenciaField);
        apenasNumeros(indutanciaField);
        apenasNumeros(capacitanciaField);
        apenasNumeros(ddpAmplitudeField);
        apenasNumeros(frequenciaField);

    }

    public void apenasNumeros(TextField field) {
        field.setTextFormatter(new TextFormatter<>(change -> {String texto = change.getControlNewText();
            if (texto.matches("\\d*([\\.]?\\d*)?")) {
                return change;
            } else {
                return null;
            }
        }));
    }

    private static ControladorTela instancia;

    public static ControladorTela getInstancia() {
        if (instancia == null) {
            instancia = new ControladorTela();
        }
        return instancia;
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

    public void inicializarCbox() {
        cboxSelecionarFonte.getItems().add("Corrente Contínua (CC)");
        cboxSelecionarFonte.getItems().add("Corrente Alternada (CA)");
    }

    public void naoExibirLabelField() {
        labelDdpAmplitude.setVisible(false);
        ddpAmplitudeField.setVisible(false);
        labelFrequencia.setVisible(false);
        frequenciaField.setVisible(false);
        calcularButton.setVisible(false);
    }

    public void selecionarCircuito() {
        if (cboxSelecionarFonte.getValue() != null) {
            if (cboxSelecionarFonte.getValue().equals("Corrente Contínua (CC)")) {
                imageViewCircuito.setImage(circuitoCContinua);
                atualFonteLabel.setText("Selecionado: Corrente Contínua (CC)");

                calcularButton.setVisible(true);
                labelDdpAmplitude.setVisible(true);
                labelDdpAmplitude.setText("DDP:");
                ddpAmplitudeField.setVisible(true);
                ddpAmplitudeField.setPromptText("Digite a DDP: ");
                labelFrequencia.setVisible(false);
                frequenciaField.setVisible(false);

            } else if (cboxSelecionarFonte.getValue().equals("Corrente Alternada (CA)")) {
                imageViewCircuito.setImage(circuitoCAlternada);
                atualFonteLabel.setText("Selecionado: Corrente Alternada (CA)");

                calcularButton.setVisible(true);
                labelDdpAmplitude.setVisible(true);
                labelDdpAmplitude.setText("Amplitude:");
                ddpAmplitudeField.setVisible(true);
                ddpAmplitudeField.setPromptText("Digite a amplitude: ");
                labelFrequencia.setVisible(true);
                frequenciaField.setVisible(true);
            }
        }
    }

    @FXML
    public boolean verificandoFields() {
        if (resistenciaField.getText() == null || resistenciaField.getText().isEmpty()) {
            ControladorAlertas.alertaErro("Erro", "Digite a resistência.");
            return false;
        } else if (indutanciaField.getText() == null || indutanciaField.getText().isEmpty()) {
            ControladorAlertas.alertaErro("Erro", "Digite a indutância.");
            return false;
        } else if (capacitanciaField.getText() == null || capacitanciaField.getText().isEmpty()) {
            ControladorAlertas.alertaErro("Erro", "Digite a capacitância.");
            return false;
        }
        return true;
    }

    @FXML
    public void buttonConfirmarFonte(ActionEvent event) {
        if (cboxSelecionarFonte.getValue() == null || cboxSelecionarFonte.getValue().isEmpty()) {
            ControladorAlertas.alertaErro("Erro", "Selecione uma fonte.");
        } else {
            selecionarCircuito();
        }
    }
    @FXML
    public void buttonCalcular(ActionEvent event) {
        if (cboxSelecionarFonte.getValue() == null) {
            ControladorAlertas.alertaErro("Erro", "Selecione qual será a fonte do circuito.");
        } else {
            if (cboxSelecionarFonte.getValue().equals("Corrente Contínua (CC)")) {
                circuitoCContinua();
            } else {
                circuitoCAlternada();
            }
        }
    }

    @FXML
    public void circuitoCContinua() {
        if (verificandoFields()) {

            double resistencia = Double.parseDouble(resistenciaField.getText());
            double indutancia = Double.parseDouble(indutanciaField.getText());
            double capacitancia = Double.parseDouble(capacitanciaField.getText());
            double ddp = Double.parseDouble(ddpAmplitudeField.getText());

            Circuito circuito = new Circuito(resistencia, indutancia, capacitancia, true, ddp);
        }
    }

    @FXML
    public void circuitoCAlternada() {
        if (verificandoFields()) {

            double resistencia = Double.parseDouble(resistenciaField.getText());
            double indutancia = Double.parseDouble(indutanciaField.getText());
            double capacitancia = Double.parseDouble(capacitanciaField.getText());
            double amplitude = Double.parseDouble(ddpAmplitudeField.getText());
            double frequencia = Double.parseDouble(frequenciaField.getText());

            Circuito circuito = new Circuito(resistencia, indutancia, capacitancia, false, amplitude, frequencia);
        }
    }

}
