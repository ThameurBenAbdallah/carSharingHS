package carsharing;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Get the database name from command-line arguments
        final String databaseName = args[1];
        CompanyDao companyDao = new CompanyDaoImp(databaseName);

            while (true) {
                System.out.println("1. Log in as a manager");
                System.out.println("2. Log in as a customer");
                System.out.println("3. Create a customer");
                System.out.println("0. Exit");

                int choice = scanner.nextInt();

                switch (choice) {
                    case 1 ->
                        // Manager login
                            managerLogin(scanner, companyDao);
                    case 2 ->
                        // Customer login
                            customerLogin(scanner, companyDao);
                    case 3 ->
                        // Create a customer
                            createCustomer(scanner, companyDao);
                    case 0 -> {
                        // Exit the program
                        System.out.println("Goodbye!");
                        companyDao.closeConnection();

                        return;
                    }
                    default -> System.out.println("Invalid choice! Please try again.");
                }
            }

    }

    private static void managerLogin(Scanner scanner, CompanyDao companyDao) {
        innerLoop:
        while (true) {
            System.out.println("1. Company list");
            System.out.println("2. Create a company");
            System.out.println("0. Back");
            int secondChoice = scanner.nextInt();
            switch (secondChoice) {
                case 1 -> {
                    List<Company> companies = companyDao.findAll();
                    if (companies.isEmpty()) {
                        System.out.println("The company list is empty!");
                    } else {
                        System.out.println("Choose the company:");
                        for (int i = 0; i < companies.size(); i++) {
                            System.out.println((i + 1) + ". " + companies.get(i).getName());
                        }
                        System.out.println("0. Back");

                        int companyChoice = scanner.nextInt();

                        if (companyChoice == 0) {
                            // Go back to the manager menu
                        } else if (companyChoice >= 1 && companyChoice <= companies.size()) {
                            // Valid company choice
                            Company selectedCompany = companies.get(companyChoice - 1);
                            while (true) {
                                // Print the company menu
                                System.out.println("\n'" + selectedCompany.getName() + "' company:");
                                System.out.println("1. Car list");
                                System.out.println("2. Create a car");
                                System.out.println("0. Back");
                                //System.out.print("Enter your choice: ");
                                int companyMenuChoice = scanner.nextInt();

                                switch (companyMenuChoice) {
                                    case 1 -> {
                                        // List cars for the selected company
                                        List<Car> cars = companyDao.findAllCarsByCompany(selectedCompany);
                                        System.out.println("\n'" + selectedCompany.getName() + "' cars:");

                                        if (cars.isEmpty()) {
                                            System.out.println("The car list is empty!");
                                        } else {
                                            for (int j = 0; j < cars.size(); j++) {
                                                System.out.println((j + 1) + ". " + cars.get(j).getName());
                                            }
                                        }
                                    }
                                    case 2 -> {
                                        // Create a car for the selected company
                                        System.out.print("Enter the car name: ");
                                        //scanner.nextLine();
                                        scanner.nextLine(); // Consume the newline character
                                        String carName = scanner.nextLine();

                                        // Create a new Car object
                                        Car newCar = new Car(0, carName, selectedCompany.getId());

                                        // Add the car to the database
                                        companyDao.addCar(newCar);
                                        System.out.println("The car was added!");
                                    }
                                    case 0 -> {
                                        // Go back to the company menu
                                        continue innerLoop;
                                    }
                                    default -> System.out.println("Invalid choice! Please try again.");
                                }
                            }
                        } else {
                            System.out.println("Invalid choice! Please try again.");
                        }
                    }
                }

                case 2 -> {
                    System.out.println("Enter the company name: ");
                    scanner.nextLine(); // Consume the newline character
                    String companyName = scanner.nextLine();
                    // Create a new Company object
                    Company newCompany = new Company(0, companyName);
                    // Add the company to the database
                    companyDao.add(newCompany);
                    System.out.println("The company was created!");
                }
                case 0 -> {
                    return;
                }
            }
        }
    }

    private static void customerLogin(Scanner scanner, CompanyDao companyDao) {
        List<Customer> customers = companyDao.findAllCustomers();
        if (customers.isEmpty()) {
            System.out.println("The customer list is empty!");
        } else {
            System.out.println("Customer list:");
            for (int i = 0; i < customers.size(); i++) {
                System.out.println((i + 1) + ". " + customers.get(i).getName());
            }
            System.out.println("0. Back");

            int customerChoice = scanner.nextInt();

            if (customerChoice == 0) {
                // Go back to the main menu
                return;
            } else if (customerChoice >= 1 && customerChoice <= customers.size()) {
                // Valid customer choice
                Customer selectedCustomer = customers.get(customerChoice - 1);
                customerMenu(scanner, companyDao, selectedCustomer);
            } else {
                System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private static void customerMenu(Scanner scanner, CompanyDao companyDao, Customer customer) {
        while (true) {
            System.out.println("1. Rent a car");
            System.out.println("2. Return a rented car");
            System.out.println("3. My rented car");
            System.out.println("0. Back");

            int customerMenuChoice = scanner.nextInt();

            switch (customerMenuChoice) {
                case 1 -> {
                    rentCar(scanner, companyDao, customer);
                    customer = companyDao.findCustomerById(customer.getId());
                }
                case 2 -> {
                    returnRentedCar(companyDao, customer);
                    customer.setRentedCar(0);
                }
                case 3 -> showRentedCar(companyDao, customer);
                case 0 -> {
                    // Go back to the customer login menu
                    return;
                }
                default -> System.out.println("Invalid choice! Please try again.");
            }
        }
    }

    private static void rentCar(Scanner scanner, CompanyDao companyDao, Customer customer) {
        if (customer.getRentedCar() != 0) {
            System.out.println("You've already rented a car!");
            return;
        }
        List<Company> companies = companyDao.findAll();
        System.out.println("Choose a company:");

        if (companies.isEmpty()) {
            System.out.println("The company list is empty!");
        } else {
            for (int j = 0; j < companies.size(); j++) {
                System.out.println((j + 1) + ". " + companies.get(j).getName());
            }
            System.out.println("0. Back");
            int customerCompanyChoice = scanner.nextInt();
            if (customerCompanyChoice == 0) {
                // Go back to the customer menu

            } else if (customerCompanyChoice >= 1 && customerCompanyChoice <= companies.size()) {
                Company selectedCompany = companies.get(customerCompanyChoice - 1);
                boolean back = true;
                while (back) {
                    System.out.println("Choose a car:");
                    List<Car> cars = companyDao.findNonRentedCars(selectedCompany.getId());
                    for (int j = 0; j < cars.size(); j++) {
                        System.out.println((j + 1) + ". " + cars.get(j).getName());
                    }
                    System.out.println("0. Back");
                    int carChoice = scanner.nextInt();
                    if (carChoice == 0) {
                        // Go back to the customer menu
                        back = false;
                    } else if (carChoice >= 1 && carChoice <= cars.size()) {
                        Car car = cars.get(carChoice - 1);
                        companyDao.addCarToCustomer(customer, car);
                        System.out.println("You rented '" + car.getName() + "'");
                        return;
                    }
                }
            }
        }
    }

    private static void returnRentedCar(CompanyDao companyDao, Customer customer) {
        if(customer.getRentedCar() == 0) System.out.println("You didn't rent a car!");
        else {
            companyDao.returnCar(customer);
            System.out.println("You've returned a rented car!");
        }
    }

    private static void showRentedCar(CompanyDao companyDao, Customer customer) {
        if (customer.getRentedCar() == 0) {
            System.out.println("You didn't rent a car!");
        } else {
            Car rentedCar = companyDao.findCarByID(customer.getRentedCar());
            System.out.println("Your rented car:");
            System.out.println(rentedCar.getName());
            System.out.println("Company:");
            System.out.println(companyDao.findCompanyByCar(rentedCar).getName());
        }
    }

    private static void createCustomer(Scanner scanner, CompanyDao companyDao) {
        System.out.print("Enter the customer name: ");
        scanner.nextLine(); // Consume the newline character
        String customerName = scanner.nextLine();

        Customer newCustomer = new Customer(0, customerName, 0);
        companyDao.addCustomer(newCustomer);

        System.out.println("The customer was added!");
    }
}


interface CompanyDao {
    List<Company> findAll();

    Company findById(int id);

    void add(Company company);

    void addCar(Car car);

    List<Car> findAllCarsByCompany(Company company);

    void update(Company company);

    void deleteById(int id);

    void deleteCarById(int id);
    void addCustomer(Customer customer);

    List<Customer> findAllCustomers();
    void addCarToCustomer(Customer customer, Car car);
    void returnCar(Customer customer);
    Car findCarByID(int id);

    Company findCompanyByCar(Car rentedCar);

    Customer findCustomerById(int id);

    List<Car> findNonRentedCars(int companyId);

    void closeConnection();
}

class Company {
    private int id;
    private String name;

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    Company(int id, String name) {
        this.name = name;
        this.id = id;
    }
}
class Customer{
    private int id;
    private String name;
    private int rentedCar;

    public Customer(int id, String name, int rentedCar) {
        this.id = id;
        this.name = name;
        this.rentedCar = rentedCar;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getRentedCar() {
        return rentedCar;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRentedCar(int rentedCar) {
        this.rentedCar = rentedCar;
    }
}

class Car {
    private int id;
    private String name;
    private int companyId;

    public int getCompanyId() {
        return companyId;
    }

    public void setCompanyId(int companyId) {
        this.companyId = companyId;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public Car(int id, String name, int companyId) {
        this.id = id;
        this.name = name;
        this.companyId = companyId;
    }

    @Override
    public String toString() {
        return "Car: [Id " + id
                + ", Name : " + name + " ]";
    }
}

class DbClient {
    private final Connection dataSource;


    public DbClient(Connection dataSource) {
        this.dataSource = dataSource;
    }
    public Connection getConnection(){
        return  this.dataSource;
    }

    public void run(String str) {
        try (
                Statement statement = dataSource.createStatement()
        ) {
            statement.executeUpdate(str); // Statement execution
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Company select(String query) {
        List<Company> companies = selectForList(query);
        if (companies.size() == 1) {
            return companies.get(0);
        } else if (companies.size() == 0) {
            return null;
        } else {
            throw new IllegalStateException("Query returned more than one object");
        }
    }
    public List<Company> selectForList(String query) {
        List<Company> companies = new ArrayList<>();

        try (
                Statement statement = dataSource.createStatement();
                ResultSet resultSetItem = statement.executeQuery(query)
        ) {
            while (resultSetItem.next()) {
                // Retrieve column values
                int id = resultSetItem.getInt("id");
                String name = resultSetItem.getString("name");
                Company company = new Company(id, name);
                companies.add(company);
            }

            return companies;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return companies;
    }

    public Car selectCar(String query) {
        List<Car> cars = selectForListCars(query);
        if (cars.size() == 1) {
            return cars.get(0);
        } else if (cars.size() == 0) {
            return null;
        } else {
            throw new IllegalStateException("Query returned more than one object");
        }
    }
    public Customer selectCustomer(String query) {
        List<Customer> customers = selectForListCustomers(query);
        if (customers.size() == 1) {
            return customers.get(0);
        } else if (customers.size() == 0) {
            return null;
        } else {
            throw new IllegalStateException("Query returned more than one object");
        }
    }

    public List<Car> selectForListCars(String query) {
        List<Car> cars = new ArrayList<>();

        try (
                Statement statement = dataSource.createStatement();
                ResultSet resultSetItem = statement.executeQuery(query)
        ) {
            while (resultSetItem.next()) {
                // Retrieve column values
                int id = resultSetItem.getInt("id");
                String name = resultSetItem.getString("name");
                int companyId = resultSetItem.getInt("company_id");
                Car car = new Car(id, name, companyId);
                cars.add(car);
            }

            return cars;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cars;
    }
    public List<Customer> selectForListCustomers(String query) {
        List<Customer> customers = new ArrayList<>();

        try (
                Statement statement = dataSource.createStatement();
                ResultSet resultSetItem = statement.executeQuery(query)
        ) {
            while (resultSetItem.next()) {
                // Retrieve column values
                int id = resultSetItem.getInt("id");
                String name = resultSetItem.getString("name");
                int carId = resultSetItem.getInt("RENTED_CAR_ID");
                if (resultSetItem.wasNull()) {
                    carId = 0; // Set carId to 0 if it was NULL in the database
                }
                Customer customer = new Customer(id, name, carId);
                customers.add(customer);
            }

            return customers;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return customers;
    }
}

class CompanyDaoImp implements CompanyDao {
    final String JDBC_DRIVER = "org.h2.Driver";
    static final String CONNECTION_URL = "jdbc:h2:./src/carsharing/db/";
    final String CREATE_DB = "CREATE TABLE IF NOT EXISTS COMPANY(" +
            "id int PRIMARY KEY AUTO_INCREMENT," +
            "name VARCHAR(100) NOT NULL UNIQUE);" +
            "ALTER TABLE company ALTER COLUMN id RESTART WITH 1;";
    final String CREATE_CAR = "CREATE TABLE IF NOT EXISTS CAR(" +
            "id INT PRIMARY KEY AUTO_INCREMENT," +
            "name VARCHAR(100) NOT NULL UNIQUE," +
            "company_id INT NOT NULL," +
            "CONSTRAINT fk_company FOREIGN KEY (company_id)" +
            "REFERENCES COMPANY(id) ON DELETE CASCADE" +
            ");";
      final String CREATE_CUSTOMER = "CREATE TABLE IF NOT EXISTS CUSTOMER(" +
            "ID INT PRIMARY KEY AUTO_INCREMENT," +
            "NAME VARCHAR(100) NOT NULL UNIQUE," +
            "RENTED_CAR_ID INT," +
            "CONSTRAINT fk_car FOREIGN KEY (RENTED_CAR_ID)" +
            "REFERENCES CAR(id) ON DELETE SET NULL" +
            ");";


    static final String SELECT_ALL = "SELECT * FROM COMPANY";
    static final String SELECT = "SELECT * FROM COMPANY WHERE id = %d";
    static final String INSERT_DATA = "INSERT INTO COMPANY(name) VALUES ('%s')";
    static final String UPDATE_DATA = "UPDATE COMPANY SET name " +
            "= '%s' WHERE id = %d";
    static final String DELETE_DATA = "DELETE FROM COMPANY WHERE id = %d";
    static final String SELECT_CARS = "SELECT * FROM CAR WHERE company_id = %d";
    static final String SELECT_CAR = "SELECT * FROM CAR WHERE id = %d";
    static final String DELETE_CAR = "DELETE FROM CAR WHERE id = %d";
    static final String INSERT_CAR = "INSERT INTO CAR(name, company_id) VALUES ('%s', %d)";
    static final String INSERT_CUSTOMER = "INSERT INTO CUSTOMER(NAME, ID) VALUES ('%s', NULL)";
    private static final String SELECT_CUSTOMERS = "SELECT * FROM CUSTOMER";
    static final String SELECT_CUSTOMER = "SELECT * FROM CUSTOMER WHERE id = %d";
    private static final String ADD_CAR_TO_CUSTOMER = "UPDATE customer " +
            "SET RENTED_CAR_ID = %d " +
            "WHERE ID = %d;";
    private static final String RETURN_CAR = "UPDATE customer " +
            "SET RENTED_CAR_ID = NULL " +
            "WHERE ID = %d;";

    private DbClient dbClient;
    private Connection conn;
    public CompanyDaoImp(String databaseName) {
        final String DB_URL = CONNECTION_URL + databaseName;
        try {
            // Register JDBC driver
            Class.forName(JDBC_DRIVER);

            // Open a connection
            conn = DriverManager.getConnection(DB_URL);
            conn.setAutoCommit(true);
            this.dbClient = new DbClient(conn);
            // Create the tables if it doesn't exist
            createCompanyTable();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void createCompanyTable() {
        dbClient.run(CREATE_DB);
        dbClient.run(CREATE_CAR);
        dbClient.run(CREATE_CUSTOMER);
    }
    @Override
    public List<Company> findAll() {
        return dbClient.selectForList(SELECT_ALL);
    }
    @Override
    public Company findById(int id) {
        String query = String.format(SELECT, id);
        return dbClient.select(query);
    }
    @Override
    public void add(Company company) {
        String query = String.format(INSERT_DATA, company.getName());
        dbClient.run(query);
    }
    @Override
    public void addCar(Car car) {
        String query = String.format(INSERT_CAR, car.getName(), car.getCompanyId());
        dbClient.run(query);
    }
    @Override
    public List<Car> findAllCarsByCompany(Company company) {
        String query = String.format(SELECT_CARS, company.getId());
        return dbClient.selectForListCars(query);
    }
    @Override
    public void update(Company company) {
        String query = String.format(UPDATE_DATA, company.getName(), company.getId());
        dbClient.run(query);
    }
    @Override
    public void deleteById(int id) {
        String query = String.format(DELETE_DATA, id);
        dbClient.run(query);
    }
    @Override
    public void deleteCarById(int id) {
        String query = String.format(DELETE_CAR, id);
        dbClient.run(query);
    }
    @Override
    public void addCustomer(Customer customer) {
        String query = String.format(INSERT_CUSTOMER, customer.getName());
        dbClient.run(query);
    }
    @Override
    public List<Customer> findAllCustomers() {
        String query = String.format(SELECT_CUSTOMERS);
        return dbClient.selectForListCustomers(query);
    }
    @Override
    public void addCarToCustomer(Customer customer, Car car) {
        String query = String.format(ADD_CAR_TO_CUSTOMER, car.getId(), customer.getId());
        dbClient.run(query);
    }
    @Override
    public void returnCar(Customer customer) {
        String query = String.format(RETURN_CAR, customer.getId());
        dbClient.run(query);
    }
    @Override
    public Car findCarByID(int id) {
        String query = String.format(SELECT_CAR, id);
        return  dbClient.selectCar(query);
    }
    @Override
    public Company findCompanyByCar(Car rentedCar) {
        String query = String.format(SELECT, rentedCar.getCompanyId());
        return  dbClient.select(query);
    }

    @Override
    public Customer findCustomerById(int id) {
        String query = String.format(SELECT_CUSTOMER, id);
        return  dbClient.selectCustomer(query);
    }
    @Override
    public List<Car> findNonRentedCars(int companyID){
        String query = String.format( "SELECT c.* FROM CAR c " +
                "LEFT JOIN CUSTOMER cu ON c.id = cu.RENTED_CAR_ID " +
                "WHERE cu.RENTED_CAR_ID IS NULL OR cu.RENTED_CAR_ID = 0 " +
                "AND c.company_id = %d", companyID);
        return  dbClient.selectForListCars(query);
    }
    @Override
    public void closeConnection() {
        try {
            if (dbClient != null && dbClient.getConnection() != null) {
                dbClient = null;
                this.conn.close();

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}