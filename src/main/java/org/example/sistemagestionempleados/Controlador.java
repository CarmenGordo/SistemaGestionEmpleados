package org.example.sistemagestionempleados;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;

import javax.xml.bind.JAXBException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Controlador {

    @FXML
    private TextField textNombre, textApellidos, textDepartamento, textSueldo;

    @FXML
    private TableView<Empleado> tablaEmpleados;

    @FXML
    private TableColumn<Empleado, Integer> columnId;
    @FXML
    private TableColumn<Empleado, String> columnNombre;
    @FXML
    private TableColumn<Empleado, String> columnApellidos;
    @FXML
    private TableColumn<Empleado, String> columnDepartamento;
    @FXML
    private TableColumn<Empleado, Double> columnSueldo;

    private ObservableList<Empleado> empleados = FXCollections.observableArrayList();
    private int idEmpleado = 1;

    @FXML
    private void btnInsetar() {
        if (validarDatos()) {
            String nombre = textNombre.getText();
            String apellidos = textApellidos.getText();
            String departamento = textDepartamento.getText();
            double sueldo = Double.parseDouble(textSueldo.getText());

            Empleado nuevoEmpleado = new Empleado(idEmpleado++, nombre, apellidos, departamento, sueldo);
            empleados.add(nuevoEmpleado);
            tablaEmpleados.refresh();
            guardar();
            limpiar();
        } else {
            mostrarErrorEspecifico();
        }
    }

    private void mostrarErrorEspecifico() {
        if (textNombre.getText().isEmpty() || textNombre.getText().length() > 30) {
            mostrarError("El nombre es inválido. Debe tener entre 1 y 30 caracteres.");
        } else if (textApellidos.getText().isEmpty() || textApellidos.getText().length() > 60) {
            mostrarError("Los apellidos son inválidos. Deben tener entre 1 y 60 caracteres.");
        } else if (textDepartamento.getText().isEmpty() || textDepartamento.getText().length() > 30) {
            mostrarError("El departamento es inválido. Debe tener entre 1 y 30 caracteres.");
        } else if (!textSueldo.getText().matches("\\d+(\\.\\d{1,2})?")) {
            mostrarError("El sueldo es inválido. Debe ser un número con hasta 2 decimales.");
        } else if (Double.parseDouble(textSueldo.getText()) < 0 || Double.parseDouble(textSueldo.getText()) > 99999.99) {
            mostrarError("El sueldo debe estar entre 0 y 99,999.99.");
        }
    }

    private boolean validarDatos() {
        if (textNombre.getText().isEmpty() || textNombre.getText().length() > 30
                || textApellidos.getText().isEmpty() || textApellidos.getText().length() > 60
                || textDepartamento.getText().isEmpty() || textDepartamento.getText().length() > 30
                || !textSueldo.getText().matches("\\d+(\\.\\d{1,2})?")) {
            mostrarErrorEspecifico();
            return false;
        }
        double sueldo = Double.parseDouble(textSueldo.getText());
        if (sueldo < 0 || sueldo > 99999.99) {
            mostrarErrorEspecifico();
            return false;
        }
        return true;
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }


    private void guardarEmpleados() {
        try {
            Persistencia.guardarEmpleadosBinario(empleados, "empleados.dat");
        } catch (IOException e) {
            mostrarError("Error al guardar empleados.");
        }
    }

    @FXML
    private void btnBorrar() {
        Empleado empleadoSeleccionado = tablaEmpleados.getSelectionModel().getSelectedItem();
        if (empleadoSeleccionado != null) {
            empleados.remove(empleadoSeleccionado);
            actualizarTabla();
            guardarEmpleados();
            tablaEmpleados.refresh();
        } else {
            mostrarError("No se ha seleccionado ningún empleado.");
        }
    }

    @FXML
    private void btnEditar() {
        Empleado empleadoSeleccionado = tablaEmpleados.getSelectionModel().getSelectedItem();
        if (empleadoSeleccionado != null) {
            empleadoSeleccionado.setNombre(textNombre.getText());
            empleadoSeleccionado.setApellidos(textApellidos.getText());
            empleadoSeleccionado.setDepartamento(textDepartamento.getText());
            empleadoSeleccionado.setSueldo(Double.parseDouble(textSueldo.getText()));
            actualizarTabla();
            guardarEmpleados();
        } else {
            mostrarError("No se ha seleccionado ningún empleado.");
        }
    }

    private void limpiar() {
        textNombre.clear();
        textApellidos.clear();
        textDepartamento.clear();
        textSueldo.clear();
    }

    private void guardar() {
        Gson gson = new Gson();
        try (FileWriter writer = new FileWriter("empleados.json")) {
            gson.toJson(empleados, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadEmpleados() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader("empleados.json")) {
            Type empleadoListType = new TypeToken<List<Empleado>>() {}.getType();
            List<Empleado> listaEmpleados = gson.fromJson(reader, empleadoListType);
            if (listaEmpleados != null) {
                empleados.addAll(listaEmpleados);
                idEmpleado = empleados.size() + 1;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void btnExportarXML() {
        try {
            Persistencia.exportarXML(empleados, "empleados.xml");
        } catch (JAXBException e) {
            mostrarError("Error al exportar a XML.");
        }
    }

    @FXML
    private void btnImportarXML() {
        try {
            List<Empleado> empleadosImportados = Persistencia.importarXML("empleados.xml");
            if (empleadosImportados != null) {
                empleados.setAll(empleadosImportados);
                actualizarTabla();
            } else {
                mostrarError("No se encontraron empleados en el archivo XML.");
            }
        } catch (JAXBException e) {
            mostrarError("Error al importar desde XML: " + e.getMessage());
        }
    }

    @FXML
    private void btnExportarJson() {
        try {
            Persistencia.exportarJSON(empleados, "empleados.json");
        } catch (IOException e) {
            mostrarError("Error al exportar a JSON.");
        }
    }

    @FXML
    private void btnImportarJson() {
        try {
            List<Empleado> empleadosImportados = Persistencia.importarJSON("empleados.json");
            if (empleadosImportados != null) {
                empleados.setAll(empleadosImportados); // Actualiza el ObservableList con la lista importada
                actualizarTabla();
            } else {
                mostrarError("No se encontraron empleados en el archivo JSON.");
            }
        } catch (IOException e) {
            mostrarError("Error al importar desde JSON: " + e.getMessage());
        }
    }

    @FXML
    private void initialize() {
        if (columnId != null && columnNombre != null && columnApellidos != null && columnDepartamento != null && columnSueldo != null) {
            columnId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
            columnNombre.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNombre()));
            columnApellidos.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getApellidos()));
            columnDepartamento.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDepartamento()));
            columnSueldo.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getSueldo()).asObject());

            tablaEmpleados.setItems(empleados);
        }

        try {
            List<Empleado> empleadosCargados = Persistencia.cargarEmpleadosBinario("empleados.dat");
            if (empleadosCargados != null) {
                empleados.addAll(empleadosCargados);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No hay empleados.");
        }
    }

    private void actualizarTabla() {
        if (tablaEmpleados != null) {
            tablaEmpleados.getItems().clear();
            tablaEmpleados.setItems(empleados);
        } else {
            System.out.println(textNombre.getText()+ "  "+ textApellidos.getText()+ "   "+
                    textDepartamento.getText()+ "   "+textSueldo.getText());
            System.out.println("La tabla de empleados no se ha inicializado correctamente.");
        }
    }

}
