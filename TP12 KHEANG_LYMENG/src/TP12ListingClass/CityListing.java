package TP12ListingClass;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import models.City;
import orms.CityORM;
import orms.CountryORM;
import utils.DbManager;

public class CityListing extends DbFunction{

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
                | 1. List all city          |
                | 2. Add a new city         |
                | 3. Delelte city by ID     |
                | 0. Exit city listing      |
                +===========================+
                """);
            System.out.print("Choose option: ");
            opt=validInt();
            if(!superUser && (opt==2||opt==3)) opt=4;
            switch (opt){
                case 0: break;
                case 1:
                    listAllCity();
                    break;
                case 2:
                    addNewCity();
                    break;
                case 3:
                    deleteCityByID();
                    break;
                case 4:
                    System.out.println("=> Permission denied for USER");
                    break;
                default: System.out.println("=> Option not avaiable! Please choose again\n");
            }
        }while(opt!=0);
    }


    private static void listAllCity(){        
        CityORM CityORM=new CityORM();
        //if no City
        if(CityORM.listAll().size()==0){System.out.println("-\nNo city to display!"); return;}

        CountryORM countryORM=new CountryORM();
        //if no country
        if(countryORM.listAll().size()==0){System.out.println("\nNo country to select!"); return;}
        System.out.println("Please select a country by ID: ");

        for(var country : countryORM.listAll()){
            System.out.println(country.getId()+". " +country.getCountry());
        }
        System.out.print("\nEnter: ");
        var foundCountries=countryORM.rawQueryList("id="+validInt());
        if(foundCountries.size()>0){            
            var cities=CityORM.rawQueryList("countryID="+foundCountries.get(0).getId());
            if(cities.size()==0) {System.out.println("\nNo City to display in this country!"); return;}
            System.out.println("\t\t+======================+");
            System.out.println("\t\t|         CITY         |");
            System.out.println("\t\t+======================+");
            for(int i=0; i<60; i++){System.out.print("=");}System.out.println();
            System.out.printf("|%-5s | %-20s | %-9s | %-15s|\n", "ID", "City", "CountryID", "UCity");
            for(int i=0; i<60; i++){System.out.print("=");}System.out.println();
            for(var c : cities){
                System.out.printf("|%-5d | %-20s | %-9d | %-15s|\n", c.getId(), c.getCity(), c.getCountry().getId(), c.getUcity());
            }
            for(int i=0; i<60; i++){System.out.print("-");}System.out.println();
        }else System.out.println("Country not found!");
    }


    private static void addNewCity(){
        CountryORM countryORM=new CountryORM();
        //if no country
        if(countryORM.listAll().size()==0){System.out.println("-\nNo country to select!"); return;}
        System.out.println("Pleae select a country by ID: ");

        for(var country : countryORM.listAll()){
            System.out.println(country.getId()+". " +country.getCountry());
        }

        System.out.print("\nEnter: ");
        var foundCountries=countryORM.rawQueryList("id="+validInt());
        if(foundCountries.size()>0){
            CityORM CityORM=new CityORM();
            System.out.print("Enter City name: ");
            City city=new City(0, sc.nextLine(), foundCountries.get(0), null);
            if(city.getCity().equals("")){System.out.println(">>>City name can not be empty!!!");return;}
            CityORM.add(city);
            System.out.println("-\n-New City added, ID: "+city.getId());
        }else System.out.println("Country not found!");
    }

    
    // implement method to delete city
    private static boolean delete(int id) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost", "root", "");
                var stmt = conn.createStatement();) {
            stmt.executeUpdate("use "+DB);
            if(stmt.executeUpdate("delete from cities where id=" + id)!=0){
                stmt.executeUpdate("DELETE FROM hotels WHERE cityid="+id);
                stmt.executeUpdate("DELETE FROM images WHERE hotelid=id IN (SELECT id FROM hotels WHERE cityid="+id+")");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void deleteCityByID(){
        CityORM CityORM=new CityORM();
        if(CityORM.listAll().size()==0){System.out.println("\n>No city to delete!"); return;}
        System.out.println("Please select City to delete by ID ");
        for(var City : CityORM.listAll()){
            System.out.println(City.getId()+". "+ City.getCity());
        }

        System.out.print("Enter City ID to delete: ");
        var foundCities=CityORM.rawQueryList("id="+validInt());
        if(foundCities.size()>0 && delete(foundCities.get(0).getId())){
            System.out.println("City \""+ foundCities.get(0).getCity() +"\" deleted");
        }else System.out.println("City not found!");
    }

}
