package carsharing;

import java.util.Scanner;

public class Main {
    // JDBC driver name and database URL
    static final String JDBC_DRIVER = "org.h2.Driver";
    static final String DB_URL = "jdbc:h2:./src/carsharing/db/%s";

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Db.createDb(args);

        var menu = new Menu(sc);
        menu.startRepl();

        Db.cleanUp();
    }
}