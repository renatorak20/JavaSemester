package com.example.renatojava.javasemester.checkups;

import com.example.renatojava.javasemester.Application;
import com.example.renatojava.javasemester.database.CheckupData;
import com.example.renatojava.javasemester.database.PatientData;
import com.example.renatojava.javasemester.database.ProcedureData;
import com.example.renatojava.javasemester.entity.*;
import com.example.renatojava.javasemester.exceptions.NoProceduresException;
import com.example.renatojava.javasemester.util.CheckObjects;
import com.example.renatojava.javasemester.util.Notification;
import com.example.renatojava.javasemester.util.Validator;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.StringConverter;
import tornadofx.control.DateTimePicker;

import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AddCheckupController implements PatientData,ProcedureData, Notification, CheckupData {

    public static final DateTimeFormatter DATE_TIME_FORMAT_FULL = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    @FXML
    private TableView<Patient> patientsTable;
    @FXML
    private TableColumn<Patient, String> nameColumn, surnameColumn, oibColumn;

    @FXML
    private TableView<Procedure> procedureTable;
    @FXML
    private TableColumn<Procedure, String> procedureColumn, priceColumn;
    @FXML
    private DateTimePicker datePicker;
    @FXML
    private ChoiceBox<PatientRoom> roomChoiceBox;

    @FXML
    public void initialize(){
        fillPatientsTable(PatientData.getAllPatients());

        ObservableList<PatientRoom> observableList = FXCollections.observableArrayList(List.of(new ConsultingRoom("Consulting room"), new Sickroom("Sickroom"), new Casualty("Casualty")));
        roomChoiceBox.setItems(observableList);
        roomChoiceBox.getSelectionModel().selectFirst();
        roomChoiceBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(PatientRoom patientRoom) {
                return patientRoom.getRoomType();
            }

            @Override
            public PatientRoom fromString(String s) {
                return null;
            }
        });
        
        try{
            fillProceduresTable(ProcedureData.getAllProcedures());
        } catch (SQLException | IOException e) {
            Application.logger.error("There has been an error while getting all procedures!", e);
        }catch (NoProceduresException e){
            Application.logger.error(e.getMessage(), e);
        }
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

    public void addNewCheckup(){
        if (CheckObjects.checkIfHospitalHasDoctors() && Validator.isValidTime(String.valueOf(datePicker.getDateTimeValue()), DATE_TIME_FORMAT_FULL) && Notification.confirmEdit()) {
            if(!CheckObjects.isBeforeToday(datePicker.getDateTimeValue())){
                if(CheckObjects.checkCheckupTime(datePicker.getDateTimeValue(), null)){
                    CheckupData.addNewActiveCheckup(procedureTable.getSelectionModel().getSelectedItem().id(),
                            Integer.valueOf(patientsTable.getSelectionModel().getSelectedItem().getId()),
                            datePicker.getDateTimeValue(),
                            roomChoiceBox.getValue(),
                            patientsTable.getSelectionModel().getSelectedItem().getFullName(),
                            ProcedureData.getProcedureFromId(procedureTable.getSelectionModel().getSelectedItem().id()).description()
                            );
                }


            }
        }

    }

}
