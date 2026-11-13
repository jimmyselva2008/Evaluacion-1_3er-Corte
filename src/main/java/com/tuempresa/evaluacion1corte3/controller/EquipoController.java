
package com.tuempresa.evaluacion1corte3.controller;

import com.tuempresa.evaluacion1corte3.dao.EquipoDAO;
import com.tuempresa.evaluacion1corte3.dao.TipoEquipoDAO;
import com.tuempresa.evaluacion1corte3.model.Equipo;
import com.tuempresa.evaluacion1corte3.model.TipoEquipo;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

public class EquipoController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtMarca;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtBuscar;

    @FXML private ComboBox<TipoEquipo> cbTipo;
    @FXML private ComboBox<TipoEquipo> cbFiltroTipo;
    @FXML private ComboBox<String> cbEstado;

    @FXML private TableView<Equipo> tablaEquipos;

    @FXML private TableColumn<Equipo, String> colCodigo;
    @FXML private TableColumn<Equipo, String> colNombre;
    @FXML private TableColumn<Equipo, String> colMarca;
    @FXML private TableColumn<Equipo, String> colEstado;
    @FXML private TableColumn<Equipo, Integer> colCantidad;
    @FXML private TableColumn<Equipo, String> colTipo;

    private final EquipoDAO equipoDAO = new EquipoDAO();
    private final TipoEquipoDAO tipoDAO = new TipoEquipoDAO();

    private Equipo seleccionado;

    @FXML
    public void initialize() {

        cbEstado.setItems(FXCollections.observableArrayList(
                "Disponible",
                "En mantenimiento",
                "Fuera de servicio"
        ));

        colCodigo.setCellValueFactory(
                new PropertyValueFactory<>("codigo")
        );

        colNombre.setCellValueFactory(
                new PropertyValueFactory<>("nombre")
        );

        colMarca.setCellValueFactory(
                new PropertyValueFactory<>("marca")
        );

        colEstado.setCellValueFactory(
                new PropertyValueFactory<>("estado")
        );

        colCantidad.setCellValueFactory(
                new PropertyValueFactory<>("cantidad")
        );

        colTipo.setCellValueFactory(datos ->
                new javafx.beans.property.SimpleStringProperty(
                        datos.getValue().getTipoEquipo().getNombre()
                )
        );

        tablaEquipos.getSelectionModel()
                .selectedItemProperty()
                .addListener((obs, anterior, actual) -> {

                    if (actual != null) {
                        seleccionado = actual;

                        txtCodigo.setText(actual.getCodigo());
                        txtNombre.setText(actual.getNombre());
                        txtMarca.setText(actual.getMarca());
                        txtCantidad.setText(
                                String.valueOf(actual.getCantidad())
                        );

                        cbEstado.setValue(actual.getEstado());

                        cbTipo.getItems().stream()
                                .filter(t -> t.getIdTipo().equals(
                                        actual.getTipoEquipo().getIdTipo()
                                ))
                                .findFirst()
                                .ifPresent(cbTipo::setValue);
                    }
                });

        try {
            cargarTipos();
            cargarEquipos();
        } catch (Exception e) {
            alerta(
                    Alert.AlertType.ERROR,
                    "Error de conexión",
                    "No se pudieron cargar los datos: " +
                            e.getMessage()
            );
        }
    }

    private void cargarTipos() {
        var tipos = FXCollections.observableArrayList(
                tipoDAO.listar()
        );

        cbTipo.setItems(tipos);
        cbFiltroTipo.setItems(
                FXCollections.observableArrayList(tipos)
        );
    }

    private void cargarEquipos() {
        tablaEquipos.setItems(
                FXCollections.observableArrayList(
                        equipoDAO.listar()
                )
        );
    }

    private Equipo obtenerFormulario() {

        String codigo = txtCodigo.getText().trim();
        String nombre = txtNombre.getText().trim();
        String marca = txtMarca.getText().trim();
        String cantidadTexto = txtCantidad.getText().trim();

        if (codigo.isEmpty() || nombre.isEmpty()) {
            throw new IllegalArgumentException(
                    "El código y el nombre son obligatorios."
            );
        }

        if (cbTipo.getValue() == null) {
            throw new IllegalArgumentException(
                    "Seleccione un tipo de equipo."
            );
        }

        if (cbEstado.getValue() == null ||
                !cbEstado.getItems().contains(cbEstado.getValue())) {
            throw new IllegalArgumentException(
                    "Seleccione un estado válido."
            );
        }

        int cantidad;

        try {
            cantidad = Integer.parseInt(cantidadTexto);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser un número entero."
            );
        }

        if (cantidad < 0) {
            throw new IllegalArgumentException(
                    "La cantidad no puede ser negativa."
            );
        }

        Equipo equipo = new Equipo();

        equipo.setCodigo(codigo);
        equipo.setNombre(nombre);
        equipo.setMarca(marca);
        equipo.setEstado(cbEstado.getValue());
        equipo.setCantidad(cantidad);
        equipo.setTipoEquipo(cbTipo.getValue());

        return equipo;
    }

    @FXML
    private void guardar() {

        try {
            Equipo equipo = obtenerFormulario();

            if (equipoDAO.existeCodigo(
                    equipo.getCodigo(), null)) {
                throw new IllegalArgumentException(
                        "Ya existe un equipo con ese código."
                );
            }

            equipoDAO.guardar(equipo);

            alerta(
                    Alert.AlertType.INFORMATION,
                    "Éxito",
                    "Equipo guardado correctamente."
            );

            limpiar();
            cargarEquipos();

        } catch (Exception e) {
            mostrarError(e);
        }
    }

    @FXML
    private void actualizar() {

        if (seleccionado == null) {
            alerta(
                    Alert.AlertType.WARNING,
                    "Aviso",
                    "Seleccione un equipo para actualizar."
            );
            return;
        }

        try {
            Equipo equipo = obtenerFormulario();
            equipo.setIdEquipo(seleccionado.getIdEquipo());

            if (equipoDAO.existeCodigo(
                    equipo.getCodigo(),
                    equipo.getIdEquipo())) {
                throw new IllegalArgumentException(
                        "Ya existe otro equipo con ese código."
                );
            }

            equipoDAO.actualizar(equipo);

            alerta(
                    Alert.AlertType.INFORMATION,
                    "Éxito",
                    "Equipo actualizado correctamente."
            );

            limpiar();
            cargarEquipos();

        } catch (Exception e) {
            mostrarError(e);
        }
    }

    @FXML
    private void eliminar() {

        if (seleccionado == null) {
            alerta(
                    Alert.AlertType.WARNING,
                    "Aviso",
                    "Seleccione un equipo para eliminar."
            );
            return;
        }

        Alert confirmacion = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(
                "¿Desea eliminar este equipo?"
        );
        confirmacion.setContentText(
                seleccionado.getNombre()
        );

        Optional<ButtonType> respuesta =
                confirmacion.showAndWait();

        if (respuesta.isPresent() &&
                respuesta.get() == ButtonType.OK) {

            try {
                equipoDAO.eliminar(
                        seleccionado.getIdEquipo()
                );

                alerta(
                        Alert.AlertType.INFORMATION,
                        "Éxito",
                        "Equipo eliminado correctamente."
                );

                limpiar();
                cargarEquipos();

            } catch (Exception e) {
                mostrarError(e);
            }
        }
    }

    @FXML
    private void buscar() {

        try {
            String nombre = txtBuscar.getText().trim();

            tablaEquipos.setItems(
                    FXCollections.observableArrayList(
                            equipoDAO.buscar(
                                    nombre,
                                    cbFiltroTipo.getValue()
                            )
                    )
            );

        } catch (Exception e) {
            mostrarError(e);
        }
    }

    @FXML
    private void limpiarFiltros() {
        txtBuscar.clear();
        cbFiltroTipo.setValue(null);

        try {
            cargarEquipos();
        } catch (Exception e) {
            mostrarError(e);
        }
    }

    @FXML
    private void limpiar() {

        seleccionado = null;

        txtCodigo.clear();
        txtNombre.clear();
        txtMarca.clear();
        txtCantidad.clear();

        cbTipo.setValue(null);
        cbEstado.setValue(null);

        tablaEquipos.getSelectionModel()
                .clearSelection();
    }

    private void mostrarError(Exception e) {

        String mensaje;

        if (e instanceof IllegalArgumentException) {
            mensaje = e.getMessage();
        } else {
            mensaje = "No se pudo completar la operación. " +
                    "Verifique la conexión y las restricciones " +
                    "de la base de datos. Detalle: " +
                    e.getMessage();
        }

        alerta(
                Alert.AlertType.ERROR,
                "Error",
                mensaje
        );
    }

    private void alerta(
            Alert.AlertType tipo,
            String titulo,
            String mensaje) {

        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
