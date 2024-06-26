package carsharing;

import carsharing.dto.Car;
import carsharing.dto.Company;
import carsharing.dto.Customer;
import carsharing.exception.ReplInterruptedException;
import carsharing.exception.UnsupportedOperationException;

import java.sql.SQLException;
import java.util.Scanner;

public class Menu {
    private final Scanner scanner;

    public Menu(Scanner scanner) {
        this.scanner = scanner;
    }

    public void startRepl() {
        boolean finished = false;

        while (!finished) {
            try {
                main();
            } catch (UnsupportedOperationException | ReplInterruptedException e) {
                finished = true;
            }
        }
    }

    private void manager() throws UnsupportedOperationException, ReplInterruptedException {
        System.out.println("""
                1. Company list
                2. Create a company
                0. Back"""
        );
        int number = scanner.nextInt();
        scanner.nextLine();

        switch (number) {
            case 1 -> showCompanyList();
            case 2 -> createCompany();
            case 0 -> main();
            default -> throw new UnsupportedOperationException("Invalid operation!");
        }
    }

    private void main() throws UnsupportedOperationException, ReplInterruptedException {
        System.out.println("""
                1. Log in as a manager
                2. Log in as a customer
                3. Create a customer
                0. Exit"""
        );

        int number = scanner.nextInt();
        scanner.nextLine();

        switch (number) {
            case 1 ->  manager();
            case 2 ->  customer();
            case 3 ->  createCustomer();
            case 0 -> throw new ReplInterruptedException();
            default -> throw new UnsupportedOperationException("Invalid operation!");
        };
    }

    private void createCompany() throws ReplInterruptedException, UnsupportedOperationException {
        System.out.println("Enter the company name:");
        var newCompanyName = scanner.nextLine();
        try {
            Db.insertCompany(newCompanyName);
            System.out.println("The company was created!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        manager();
    }

    private void showCompanyList() throws ReplInterruptedException, UnsupportedOperationException {
        System.out.println("Company list:");

        try {
             var list = Db.getCompanyList();
             if (list.isEmpty()) {
                 System.out.println("The company list is empty!");
                 manager();
                 return;
             }


             list.forEach(company -> System.out.printf("%d. %s\n", company.id(), company.name()));
             System.out.println("0. Back");

             chooseCompany();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        manager();
    }

    private void chooseCompany() throws UnsupportedOperationException, ReplInterruptedException {
        int number = scanner.nextInt();
        scanner.nextLine();

        if (number == 0) {
            manager();
            return;
        }

        try {
            var company = Db.getCompany(number); 
            if (company != null) {
                showCompany(company);
            } else {
                manager();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        manager();
    }

    private void showCompany(Company company) throws ReplInterruptedException, UnsupportedOperationException {
        System.out.printf("'%s' company:\n", company.name());

        System.out.println("""
                1. Car list
                2. Create a car
                0. Back"""
        );
        int carNumber = scanner.nextInt();
        scanner.nextLine();

        switch (carNumber) {
            case 1 -> showCarList(company);
            case 2 -> createCar(company);
            case 0 -> manager();
            default -> throw new UnsupportedOperationException("Invalid operation!");
        }
    }

    private void showCarList(Company company) throws ReplInterruptedException, UnsupportedOperationException {
        try {
            var list = Db.getCarList(company.id());
            if (list.isEmpty()) {
                System.out.println("The car list is empty!");
                showCompany(company);
                return;
            }

            System.out.println("Car list:");

//            list.forEach(car -> System.out.printf("%d. %s\n", car.id(), car.name()));
            for (int i = 0; i < list.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, list.get(i).name());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        showCompany(company);
    }

    private void createCar(Company company) throws ReplInterruptedException, UnsupportedOperationException {
        System.out.println("Enter the car name:");
        var newCarName = scanner.nextLine();

        try {
            Db.insertCar(newCarName, company.id());
            System.out.println("The car was added!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        showCompany(company);
    }

    private void customer() throws ReplInterruptedException, UnsupportedOperationException {
        try {
            var list = Db.getCustomerList();
            if (list.isEmpty()) {
                System.out.println("The customer list is empty!");
                main();
            }

            System.out.println("Choose a customer:");
            list.forEach(customer -> System.out.printf("%d. %s\n", customer.id(), customer.name()));
            System.out.println("0. Back");

            chooseCustomer();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void chooseCustomer() throws ReplInterruptedException, UnsupportedOperationException {
        int number = scanner.nextInt();
        scanner.nextLine();

        if (number == 0) {
            main();
            return;
        }

        try {
            var customer = Db.getCustomer(number);
            if (customer != null) {
                showCustomer(customer);
            } else {
                main();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        main();
    }

    private void showCustomer(Customer customer) throws ReplInterruptedException, UnsupportedOperationException {
        System.out.println("""
                1. Rent a car
                2. Return a rented car
                3. My rented car
                0. Back"""
        );

        int number = scanner.nextInt();
        scanner.nextLine();

        switch (number) {
            case 1 ->  rentCar(customer);
            case 2 ->  returnCar(customer);
            case 3 ->  showRentedCar(customer);
            case 0 ->  main();
            default -> throw new UnsupportedOperationException("Invalid operation!");
        };
    }

    private void rentCar(Customer customer) throws ReplInterruptedException, UnsupportedOperationException {
        if (customer.rentedCarId() != null) {
            System.out.println("You've already rented a car!");
            showCustomer(customer);
            return;
        }

        showCompanyListForRent(customer);

        var company = chooseCompanyForRent(customer);
        if (company != null) {
            chooseCarForRent(company, customer);
        }
    }

    private void chooseCarForRent(Company company, Customer customer) throws ReplInterruptedException, UnsupportedOperationException {
        try {
            var list = Db.getNotRentedCarList(company.id());
            if (list.isEmpty()) {
                System.out.println("The car list is empty!");
                return;
            }

            System.out.println("Choose a car:");

            for (int i = 0; i < list.size(); i++) {
                System.out.printf("%d. %s\n", i + 1, list.get(i).name());
            }
            System.out.println("0. Back");

            int number = scanner.nextInt();
            scanner.nextLine();

            if (number == 0) {
                showCustomer(customer);
                return;
            }

            Db.setRentedCarIdToValue(customer.id(), list.get(number - 1).id());
            System.out.printf("You rented '%s'\n", list.get(number - 1).name());

            showCustomer(Db.getCustomer(customer.id()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        showCustomer(customer);
    }

    private Company chooseCompanyForRent(Customer customer) throws ReplInterruptedException, UnsupportedOperationException {
        int number = scanner.nextInt();
        scanner.nextLine();

        if (number == 0) {
            showCustomer(customer);
            return null;
        }

        try {
            var company = Db.getCompany(number);
            if (company != null) {
                return company;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    private void showCompanyListForRent(Customer customer) throws ReplInterruptedException, UnsupportedOperationException {
        System.out.println("Company list:");

        try {
            var list = Db.getCompanyList();
            if (list.isEmpty()) {
                System.out.println("The company list is empty!");
                showCustomer(customer);
                return;
            }


            list.forEach(company -> System.out.printf("%d. %s\n", company.id(), company.name()));
            System.out.println("0. Back");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void returnCar(Customer customer) throws ReplInterruptedException, UnsupportedOperationException {
        if (customer.rentedCarId() == null) {
            System.out.println("You didn't rent a car!");
            showCustomer(customer);
            return;
        }

        try {
            Db.setRentedCarIdToNull(customer.id());
            System.out.println("You've returned a rented car!");

            showCustomer(Db.getCustomer(customer.id()));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        showCustomer(customer);
    }

    private void showRentedCar(Customer customer) throws ReplInterruptedException, UnsupportedOperationException {
        if (customer.rentedCarId() == null) {
            System.out.println("You didn't rent a car!");
            showCustomer(customer);
            return;
        }
        try {
            var car = Db.getCar(customer.rentedCarId());
            if (car != null) {
                var company = Db.getCompany(car.companyId());
                if (company != null) {
                    System.out.println("Your rented car:");
                    System.out.println(car.name());
                    System.out.println("Company:");
                    System.out.println(company.name());
                }
            }

            showCustomer(customer);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        showCustomer(customer);
    }

    private void createCustomer() throws ReplInterruptedException, UnsupportedOperationException {
        System.out.println("Enter the customer name:");
        var newCustomerName = scanner.nextLine();

        try {
            Db.insertCustomer(newCustomerName);
            System.out.println("The customer was added!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        main();
    }
}
