package TP12ListingClass;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import models.Country;
import orms.CountryORM;
import utils.DbManager;

public class CountryListing extends DbFunction{

    public static void main(String[] args) throws SQLException {
        create_database(DB);
        DbManager.getInstance(DB, "root", null);

        int opt;
        do{
            System.out.println("""
                \n\n
                +===========================+
                |           MENU            |
                +===========================+
                | 1. List all countries     |
                | 2. Add a new country      |
                | 3. Delelte country by ID  |
                | 0. Exit country listing   |
                +===========================+
                """);
            System.out.print("Choose option: ");
            opt=validInt();
            if(!superUser && (opt==2||opt==3)) opt=4;
            switch (opt){
                case 0: break;
                case 1:
                    listAllCountry();
                    break;
                case 2:
                    addNewCountry();
                    break;
                case 3:
                    deleteCountryByID();
                    break;
                case 4:
                    System.out.println("=> Permission denied for USER");
                    break;
                default: System.out.println("=> Option not avaiable! Please choose again\n");
            }
        }while(opt!=0);
    }


    private static void listAllCountry(){
        CountryORM countryORM=new CountryORM();
        //if no country in databse 
        if(countryORM.listAll().size()==0){System.out.println("\nNo country to display!"); return;}
        System.out.println("\t\t+======================+");
        System.out.println("\t\t|       COUNTRY        |");
        System.out.println("\t\t+======================+");
        for(int i=0; i<26; i++){System.out.print("=");}System.out.println();
        System.out.printf("|%-6s | %-15s|\n", "ID", "Country");
        for(int i=0; i<26; i++){System.out.print("=");}System.out.println();
        for(var country : countryORM.listAll()){
            System.out.printf("|%-6d | %-15s|\n",country.getId(),country.getCountry());
        }
        for(int i=0; i<26; i++){System.out.print("-");}System.out.println();
    }


    private static void addNewCountry(){
        CountryORM countryORM=new CountryORM();
        System.out.print("Enter country name: ");
        Country country=new Country(0, sc.nextLine());
        if(country.getCountry().equals("")){System.out.println("=> Country name can not be empty!!!");return;}
        countryORM.add(country);
        System.out.println("\n=> New country added, ID: "+country.getId());
    }

    private static boolean delete(int id) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost", "root", "");
                var stmt = conn.createStatement();) {
            stmt.executeUpdate("use "+DB);
            if (stmt.executeUpdate("DELETE FROM countries WHERE id=" + id) != 0){
                stmt.executeUpdate("DELETE FROM cities WHERE countryid ="+id);
                stmt.executeUpdate("DELETE FROM hotels WHERE countryid ="+id);
                stmt.executeUpdate("DELETE FROM images WHERE hotelid=id IN (SELECT id FROM hotels WHERE countryid ="+id+")");
                 return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void deleteCountryByID(){
        CountryORM countryORM=new CountryORM();
        if(countryORM.listAll().size()==0){System.out.println("\n=> No country to delete!"); return;}
        System.out.println("Please select country to delete by ID ");
        for(var country : countryORM.listAll()){
            System.out.println( country.getId()+". "+ country.getCountry());
        }
        System.out.print("Enter country ID to delete: ");
        var foundCountries=countryORM.rawQueryList("id="+validInt());
        if(foundCountries.size()>0 && delete(foundCountries.get(0).getId())){
            System.out.println("\nCountry \""+ foundCountries.get(0).getCountry() +"\" deleted");
        }else System.out.println("Country not found");
    }
}
