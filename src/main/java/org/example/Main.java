package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "postgres";
    private static final String firstNameForAdding = "Beaverley";
    private static final String lastNameForAdding = "Hofsteader";

    public static void main(String[] args) {

        System.out.println("Getting the group: \n");
        getGroup().forEach(System.out::println);
        System.out.println("=====================");
        System.out.println("Adding a student without a hometown: \n");
        addStudent(firstNameForAdding, lastNameForAdding);
        getGroup().forEach(System.out::println);
        setCity(7, "Polotsk");
        setCity(1, "Braslav");
        System.out.println("=====================");
        System.out.println("Setting a hometown with ID: \n");
        getGroup().forEach(System.out::println);
        System.out.println("=====================");
        System.out.println("Deleting a city: \n");
        deleteCity(2);
        getGroup().forEach(System.out::println);
        System.out.println("=====================");
        System.out.println("Deleting a person: \n");
        deleteStudent(2);
        getGroup().forEach(System.out::println);

    }

    private static void deleteStudent(int ID) {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

            PreparedStatement deleteStudent = connection.prepareStatement("DELETE FROM Class WHERE id = ?");
            deleteStudent.setInt(1, ID);

            PreparedStatement deleteCity = connection.prepareStatement("DELETE FROM cities WHERE fk_id_class = ?");
            deleteCity.setInt(1, ID);

            PreparedStatement checkID = connection.prepareStatement("SELECT id FROM Class ");
            ResultSet set = checkID.executeQuery();

            deleteCity.executeUpdate();
            deleteStudent.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Student hasn't been deleted");
            throw new RuntimeException(e);
        }

    }

    private static void deleteCity(int ID) {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE cities SET city = NULL WHERE fk_id_class = ?");
            preparedStatement.setInt(1, ID);
            PreparedStatement forNull = connection.prepareStatement("SELECT city FROM Cities WHERE fk_id_class = ?");
            forNull.setInt(1, ID);
            ResultSet resultSet = forNull.executeQuery();
            while (resultSet.next()) {
                if (resultSet.getString("city") == null) {
                    System.out.println("Can't find the city");
                    throw new SQLException();
                }
            }
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("The city hasn't been deleted");
        }

    }

    private static void setCity(int studentID, String city) {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE cities SET city = ? WHERE fk_id_class = ?");
            preparedStatement.setString(1, city);
            preparedStatement.setInt(2, studentID);
            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            System.out.println("The city hasn't been set");
            throw new RuntimeException(e);
        }

    }

    private static void addStudent(String firstName, String lastName) {

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String idStr = "SELECT COUNT(*) FROM Class";
            PreparedStatement idCounter = connection.prepareStatement(idStr);
            ResultSet set = idCounter.executeQuery();
            set.next();
            int id = set.getInt(1);

            PreparedStatement addStudent = connection.prepareStatement("INSERT INTO Class VALUES (?, ?, ?)");
            addStudent.setInt(1, id + 1);
            addStudent.setString(2, firstName);
            addStudent.setString(3, lastName);
            PreparedStatement addCity = connection.prepareStatement("INSERT INTO Cities VALUES (?, NULL, ?)");
            addCity.setInt(1, id + 1);
            addCity.setInt(2, id + 1);
            addStudent.executeUpdate();
            addCity.executeUpdate();

        } catch (SQLException e) {
            System.out.println("New student hasn't been added");
            throw new RuntimeException(e);
        }

    }

    private static List<Person> getGroup() {
        List<Person> group = new ArrayList<>();
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String getInfo = "SELECT * FROM Class, Cities WHERE Cities.fk_id_class = Class.id ORDER BY Class.id ";
            PreparedStatement statement = connection.prepareStatement(getInfo);
            ResultSet set = statement.executeQuery();
            while (set.next()) {

                Person person = new Person();
                person.setId(set.getInt("id"));
                person.setLastName(set.getString("lastName"));
                person.setFirstName(set.getString("firstName"));
                person.setCity(set.getString("city"));
                group.add(person);
            }

        } catch (SQLException e) {
            System.out.println("Can't get connection");
            throw new RuntimeException(e);
        }
        return group;
    }
}