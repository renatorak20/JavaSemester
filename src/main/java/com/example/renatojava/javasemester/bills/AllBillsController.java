package com.example.renatojava.javasemester.bills;

import com.example.renatojava.javasemester.database.BillData;
import com.example.renatojava.javasemester.entity.Bill;
import com.example.renatojava.javasemester.util.DateFormatter;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AllBillsController {

    @FXML
    private TableView<Bill> billTable;

    @FXML
    private TableColumn<Bill,String> nameColumn, surnameColumn, debtColumn, dateColumn, oibColumn;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize(){
        fillBillsTable(BillData.getAllBills());
    }

    public void fillBillsTable(List<Bill> list){
        ObservableList<Bill> observableList = FXCollections.observableArrayList(list);

        nameColumn.setCellValueFactory(bill -> new SimpleStringProperty(bill.getValue().getPatient().getName()));
        surnameColumn.setCellValueFactory(bill -> new SimpleStringProperty(bill.getValue().getPatient().getSurname()));
        debtColumn.setCellValueFactory(bill -> new SimpleStringProperty(String.valueOf(bill.getValue().getPatient().getDebt())));
        dateColumn.setCellValueFactory(bill -> new SimpleStringProperty(DateFormatter.getDateTimeFormatted(bill.getValue().getTime().toString())));
        oibColumn.setCellValueFactory(bill -> new SimpleStringProperty(bill.getValue().getPatient().getOib()));

        billTable.setItems(observableList);
    }

    public void search(){
        String searchText = searchField.getText();

        List<Bill> filteredBills;

        filteredBills = BillData.getAllBills().stream().filter(bill -> bill.getPatient().getFullName().contains(searchText) ||
                                                            String.valueOf(bill.getPatient().getDebt()).contains(searchText) ||
                                                            DateFormatter.getDateTimeFormatted(bill.getTime().toString()).contains(searchText) ||
                                                            bill.getPatient().getOib().contains(searchText)).collect(Collectors.toList());
        fillBillsTable(filteredBills);
    }


    public void showMoreInfo(){
        Optional<Bill> selectedBill = Optional.ofNullable(billTable.getSelectionModel().getSelectedItem());

        if(selectedBill.isPresent()){
            Alert moreInfo = new Alert(Alert.AlertType.INFORMATION);
            moreInfo.setTitle("Bill");
            moreInfo.setHeaderText("Information about this bill:");
            StringBuilder info = new StringBuilder();
            info.append("Full name: " + selectedBill.get().getPatient().getFullName() + "\n");
            info.append("OIB: " + selectedBill.get().getPatient().getOib() + "\n");
            info.append("Birth date: " + DateFormatter.getDateFormatted(selectedBill.get().getPatient().getDate().toString()) + "\n");

            info.append("Procedures: \n");
            List<String> currentProceduresSplitted = List.of(selectedBill.get().getPatient().getProcedures().split(","));
            for(String s : currentProceduresSplitted){
                info.append(s + "\n");
            }

            info.append("Total: " + selectedBill.get().getPatient().getDebt() + "\n");
            info.append("Date issued: " + DateFormatter.getDateTimeFormatted(selectedBill.get().getTime().toString()));
            moreInfo.setContentText(info.toString());
            moreInfo.show();
        }else{
            Alert notSelected = new Alert(Alert.AlertType.ERROR);
            notSelected.setTitle("INFORMATION");
            notSelected.setHeaderText("Please select a bill!");
            notSelected.show();
        }
    }

}
