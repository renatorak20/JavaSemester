package com.example.renatojava.javasemester.entity;

import com.example.renatojava.javasemester.patientControllers.RegisterPatientScreenController;
import com.example.renatojava.javasemester.exceptions.ObjectExistsException;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public sealed interface CheckObjects permits RegisterPatientScreenController {

    static void checkIfPatientExists(String oib) throws ObjectExistsException{
        List<Patient> patientsList = new ArrayList<>();
        try {
            Connection conn = DriverManager.getConnection("jdbc:h2:tcp://localhost/~/production", "student", "student");

            Statement sqlStatement = conn.createStatement();
            ResultSet proceduresResultSet = sqlStatement.executeQuery(
                    "SELECT * FROM PATIENTS WHERE OIB='" + oib + "'"
            );

            while(proceduresResultSet.next()){
                Patient newPatient = Data.getPatient(proceduresResultSet);
                patientsList.add(newPatient);
            }

            conn.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if(patientsList.size() > 0){
            throw new ObjectExistsException("Patient already exists in system!");
        }
    }

    static void checkIfDoctorExists(Doctor doctor) throws ObjectExistsException{
        List<Doctor> doctorsList = new ArrayList<>();
        try {
            Connection conn = Data.connectingToDatabase();

            Statement sqlStatement = conn.createStatement();
            ResultSet proceduresResultSet = sqlStatement.executeQuery(
                    "SELECT * FROM DOCTORS WHERE NAME='" + doctor.getName() + "' AND SURNAME='" + doctor.getSurname() + "'"
            );

            while(proceduresResultSet.next()){
                Doctor newDoctor = Data.getDoctor(proceduresResultSet);
                doctorsList.add(newDoctor);
            }

            conn.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if(doctorsList.size() > 0){
            throw new ObjectExistsException("Doctor already exists in system!");
        }
    }

    static void checkIfRoomExists(String roomName) throws ObjectExistsException{
        Room foundRoom = null;
        try(Connection conn = Data.connectingToDatabase()){
            Statement sqlStatement = conn.createStatement();
            ResultSet roomResults = sqlStatement.executeQuery(
                    "SELECT * FROM HOSPITAL WHERE ROOM='" + roomName + "'"
            );
            while(roomResults.next()){
                foundRoom = Data.getRoom(roomResults);
            }

            if(foundRoom != null && foundRoom.getDoctorID() != -1){
                throw new ObjectExistsException("Another doctor is in this room!");
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
