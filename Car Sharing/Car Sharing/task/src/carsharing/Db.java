package carsharing;

import carsharing.dto.Car;
import carsharing.dto.Company;
import carsharing.dto.Customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Db {
    private static Connection conn = null;
    private static Statement stmt = null;
    private static PreparedStatement pstmt = null;
    private static String dbName = "db";

    public static void createDb(String[] args) {

        if (args.length > 1) {
            dbName = args[1];
        }

        try {
            Class.forName(Main.JDBC_DRIVER);

            conn = DriverManager.getConnection(Main.DB_URL.formatted(dbName));
            conn.setAutoCommit(true);

            createCompany();
            createCar();
            createCustomer();
        } catch(SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Company> getCompanyList() throws SQLException {
        var result = new ArrayList<Company>();

        stmt = conn.createStatement();
        try (ResultSet companies = stmt.executeQuery("SELECT * FROM COMPANY ORDER BY id ASC")) {
            while (companies.next()) {
                result.add(new Company(companies.getInt("id"), companies.getString("name")));
            }
        }

        return result;
    }

    public static Company getCompany(int companyId) throws SQLException {
        String sql = "SELECT * FROM COMPANY WHERE id = ?";
        pstmt = conn.prepareStatement(sql);

        // Set the value for the id
        pstmt.setInt(1, companyId);
        try (ResultSet companies = pstmt.executeQuery()) {
            if (companies.next()) {
                return new Company(companies.getInt("id"), companies.getString("name"));
            }
        }

        return null;
    }

    public static void insertCompany(String name) throws SQLException {
        String sql = "INSERT INTO COMPANY (name) VALUES (?)";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, name);

        pstmt.executeUpdate();
        pstmt.close();
    }

    public static List<Car> getCarList(int companyId) throws SQLException {
        var result = new ArrayList<Car>();

        String sql = "SELECT * FROM CAR WHERE company_id = ? ORDER BY id ASC";
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, companyId);

        try (ResultSet cars = pstmt.executeQuery()) {
            while (cars.next()) {
                result.add(new Car(cars.getInt("id"), cars.getString("name"), cars.getInt("company_id")));
            }
        }

        return result;
    }

    public static List<Car> getNotRentedCarList(int companyId) throws SQLException {
        var result = new ArrayList<Car>();

        String sql = "SELECT * FROM CAR " +
                "LEFT JOIN CUSTOMER ON CUSTOMER.rented_car_id = CAR.id " +
                "WHERE CAR.company_id = ? AND CUSTOMER.rented_car_id IS NULL " +
                "ORDER BY CAR.id ASC";
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, companyId);

        try (ResultSet cars = pstmt.executeQuery()) {
            while (cars.next()) {
                result.add(new Car(cars.getInt("id"), cars.getString("name"), cars.getInt("company_id")));
            }
        }

        return result;
    }

    public static void insertCar(String name, int companyId) throws SQLException {
        String sql = "INSERT INTO CAR (name, company_id) VALUES (?, ?)";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, name);
        pstmt.setInt(2, companyId);

        pstmt.executeUpdate();
        pstmt.close();
    }

    public static void insertCustomer(String name) throws SQLException {
        String sql = "INSERT INTO CUSTOMER (name) VALUES (?)";
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, name);

        pstmt.executeUpdate();
        pstmt.close();
    }

    public static List<Customer> getCustomerList() throws SQLException {
        var result = new ArrayList<Customer>();

        stmt = conn.createStatement();
        try (ResultSet customers = stmt.executeQuery("SELECT * FROM CUSTOMER ORDER BY id ASC")) {
            while (customers.next()) {
                result.add(
                    new Customer(
                        customers.getInt("id"),
                        customers.getString("name"),
                        customers.getInt("rented_car_id")
                    )
                );
            }
        }

        return result;
    }

    public static Customer getCustomer(int customerId) throws SQLException {
        String sql = "SELECT * FROM CUSTOMER WHERE id = ?";
        pstmt = conn.prepareStatement(sql);

        // Set the value for the id
        pstmt.setInt(1, customerId);
        try (ResultSet customers = pstmt.executeQuery()) {
            if (customers.next()) {
                int rentedCarId = customers.getInt("rented_car_id");

                return new Customer(
                    customers.getInt("id"),
                    customers.getString("name"),
                    rentedCarId == 0 ? null : rentedCarId
                );
            }
        }

        return null;
    }

    public static Car getCar(int carId) throws SQLException {
        String sql = "SELECT * FROM CAR WHERE id = ?";
        pstmt = conn.prepareStatement(sql);

        // Set the value for the id
        pstmt.setInt(1, carId);
        try (ResultSet cars = pstmt.executeQuery()) {
            if (cars.next()) {
                return new Car(
                        cars.getInt("id"),
                        cars.getString("name"),
                        cars.getInt("company_id")
                );
            }
        }

        return null;
    }

    public static void setRentedCarIdToNull(int customerId) throws SQLException {
        String sql = "UPDATE CUSTOMER SET rented_car_id = NULL WHERE id = ?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, customerId);

        pstmt.executeUpdate();
        pstmt.close();
    }

    public static void setRentedCarIdToValue(int customerId, int carId) throws SQLException {
        String sql = "UPDATE CUSTOMER SET rented_car_id = ? WHERE id = ?";
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, carId);
        pstmt.setInt(2, customerId);

        pstmt.executeUpdate();
        pstmt.close();
    }

    public static void cleanUp() {
        // STEP 4: Clean-up environment
        try {
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void createCompany() throws SQLException {
        stmt = conn.createStatement();
        String sql =  "CREATE TABLE IF NOT EXISTS COMPANY " +
                "(id INTEGER NOT NULL AUTO_INCREMENT, " +
                " name VARCHAR(255) UNIQUE NOT NULL, " +
                " PRIMARY KEY ( id ))";
        stmt.executeUpdate(sql);
        stmt.close();
    }

    private static void createCar() throws SQLException {
        stmt = conn.createStatement();
        String sql =  "CREATE TABLE IF NOT EXISTS CAR " +
                "(id INTEGER NOT NULL AUTO_INCREMENT, " +
                " name VARCHAR(255) UNIQUE NOT NULL, " +
                " company_id INT NOT NULL, " +
                " PRIMARY KEY ( id ), " +
                " FOREIGN KEY (company_id) REFERENCES COMPANY(id))";
        stmt.executeUpdate(sql);
        stmt.close();
    }

    private static void createCustomer() throws SQLException {
        stmt = conn.createStatement();
        String sql =  "CREATE TABLE IF NOT EXISTS CUSTOMER " +
                "(id INTEGER NOT NULL AUTO_INCREMENT, " +
                " name VARCHAR(255) UNIQUE NOT NULL, " +
                " rented_car_id INT DEFAULT NULL, " +
                " PRIMARY KEY ( id ), " +
                " FOREIGN KEY (rented_car_id) REFERENCES CAR(id))";
        stmt.executeUpdate(sql);
        stmt.close();
    }
}
