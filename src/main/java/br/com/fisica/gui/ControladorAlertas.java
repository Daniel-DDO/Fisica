package br.com.fisica.gui;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import lombok.Getter;
import lombok.Setter;

import java.util.Optional;

@Getter
@Setter
public class ControladorAlertas {
    //Código para telas de transição, para otimizar a criação de controladores

    private static ControladorAlertas instancia;

    public static ControladorAlertas getInstancia() {
        if (instancia == null) {
            instancia = new ControladorAlertas();
        }
        return instancia;
    }

    private String titulo = "Física Aplicada à Computação";

    public static void alertaErro(String titulo, String texto) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(titulo);
        alert.setContentText(texto);
        alert.show();
    }

    public static void alertaInformacao(String titulo, String texto) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(titulo);
        alert.setContentText(texto);
        alert.show();
    }

    public static boolean alertaConfirmacao(String titulo, String texto) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(titulo);
        alert.setContentText(texto);

        Optional<ButtonType> resultado = alert.showAndWait();
        return resultado.isPresent() && resultado.get() == ButtonType.OK;
    }

}
