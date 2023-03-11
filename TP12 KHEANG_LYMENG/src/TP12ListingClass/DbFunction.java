package TP12ListingClass;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class DbFunction {
    public static boolean superUser=false;
    public static String DB="I4A";//databse name
    public static Scanner sc = new Scanner(System.in);



    public static void create_database(String DB){
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://Localhost", "root", "");
        var stmt = conn.createStatement();) {
        stmt.executeUpdate("Create database if not exists "+DB);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static int validInt() {
        while (!sc.hasNextInt()) {
            System.out.print("Value only!\n>");
            sc.nextLine();
        }
        int n = sc.nextInt();
        sc.nextLine();
        return n;
    }

    public static double validDouble() {
        while (!sc.hasNextDouble()) {
            sc.nextLine();
            System.out.println("Value only!\n>");

        }
        double d = sc.nextDouble();
        sc.nextLine();
        return d;
    }

    public static byte validByte_0To5() {
        byte b=0;
        while (true) {
            if (!sc.hasNextInt()) {
                sc.nextLine();
                System.out.println("Value only");
                continue;
            }
            b = sc.nextByte();
            sc.nextLine();
            if (b < 0 || b > 5)
                System.out.print("Vlaue from 0 to 5 only!\n>");
            else break;
        }
        return b;
    }

    




}
