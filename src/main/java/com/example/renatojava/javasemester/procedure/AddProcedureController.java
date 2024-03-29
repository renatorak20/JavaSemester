package com.example.renatojava.javasemester.procedure;

import com.example.renatojava.javasemester.Application;
import com.example.renatojava.javasemester.database.Data;
import com.example.renatojava.javasemester.database.PatientData;
import com.example.renatojava.javasemester.database.ProcedureData;
import com.example.renatojava.javasemester.entity.Patient;
import com.example.renatojava.javasemester.entity.Procedure;
import com.example.renatojava.javasemester.exceptions.NoProceduresException;
import com.example.renatojava.javasemester.util.Notification;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AddProcedureController implements Data, PatientData, ProcedureData, Notification {

    @FXML
    private TableView<Patient> patientsTable;
    @FXML
    private TableColumn<Patient, String> nameColumn, surnameColumn, oibColumn;
    @FXML
    private TableView<Procedure> procedureTable;
    @FXML
    private TableColumn<Procedure, String> procedureColumn, priceColumn;
    @FXML
    private ListView<String> listView;

    private List<Procedure> procedureList;

    @FXML
    public void initialize(){

        fillPatientsTable(PatientData.getAllPatients());

        try{
            procedureList = ProcedureData.getAllProcedures();
        } catch (SQLException | IOException  e) {
            Application.logger.error(e.getMessage(), e);
        }catch (NoProceduresException e){
            Application.logger.error(e.getMessage(), e);
        }
        fillProceduresTable(procedureList);
    }

    public void fillPatientsTable(List<Patient> list){
        ObservableList<Patient> observableList = FXCollections.observableArrayList(list);

        nameColumn.setCellValueFactory(patient -> new SimpleStringProperty(patient.getValue().getName()));
        surnameColumn.setCellValueFactory(patient -> new SimpleStringProperty(patient.getValue().getSurname()));
        oibColumn.setCellValueFactory(patient -> new SimpleStringProperty(patient.getValue().getOib()));

        patientsTable.setItems(observableList);
    }

    public void fillProceduresTable(List<Procedure> list){
        ObservableList<Procedure> observableList = FXCollections.observableArrayList(list);

        procedureColumn.setCellValueFactory(procedure -> new SimpleStringProperty(procedure.getValue().description()));
        priceColumn.setCellValueFactory(procedure -> new SimpleStringProperty(String.valueOf(procedure.getValue().price())));

        procedureTable.setItems(observableList);
    }

    public void fillListView(){
        String proceduresFromPatient = ProcedureData.getAllProceduresFromPatientString(patientsTable.getSelectionModel().getSelectedItem());
        List<String> splittedProceduresFromPatient = List.of(proceduresFromPatient.split(","));
        ObservableList<String> observableList = FXCollections.observableArrayList(splittedProceduresFromPatient);

        if(observableList.size() > 0){
            listView.setItems(observableList);
        }else{
            observableList = FXCollections.observableArrayList("None");
            listView.setItems(observableList);
        }
    }

    public void addProcedure(){

        List<String> errorArray = new ArrayList<>();

        if(patientsTable.getSelectionModel().getSelectedItem() == null){
                errorArray.add("No patient selected!");
        }

        if(procedureTable.getSelectionModel().getSelectedItem() == null){
            errorArray.add("No procedure selected!");
        }

        if(errorArray.size() > 0){
            String errorMessage = errorArray.stream().collect(Collectors.joining("\n"));

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Errors found!");
            alert.setContentText(errorMessage);
            alert.show();
            return;
        }

        if(!Notification.confirmEdit()){
            Alert failure = new Alert(Alert.AlertType.ERROR);
            failure.setTitle("ERROR");
            failure.setHeaderText("Failure!");
            failure.setContentText("Procedure is not added to the system!");
            failure.show();
            return;
        }else{
            ProcedureData.addProcedureToPatient(patientsTable.getSelectionModel().getSelectedItem().getId(), String.valueOf(procedureTable.getSelectionModel().getSelectedItem().description()));
            Notification.addedSuccessfully("Procedure");
        }
        listView.setItems(null);
        initialize();
    }
}
