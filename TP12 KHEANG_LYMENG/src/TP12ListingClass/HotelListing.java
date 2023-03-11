package TP12ListingClass;
import java.sql.*;

import models.City;
import models.Country;
import models.Hotel;
import orms.CityORM;
import orms.CountryORM;
import orms.HotelORM;
import utils.DbManager;

public class HotelListing extends DbFunction {
    public static void main(String[] args) throws SQLException {
        create_database(DB);
        DbManager.getInstance(DB, "root", null);
        int opt;

        do {
            System.out.println("""
                \n\n
                +====================================+
                |               MENU                 |
                +====================================+
                |    1. List all hotels in a city    |
                |    2. Add new hotel                |
                |    3. View detail                  |
                |    4. Delete a hotel by id         |
                |    0. Exit hotel listing           |
                +====================================+
                    """);
            System.out.print("Choose an option: ");
            opt = validInt();
            if(!superUser && (opt==2||opt==3||opt==4)) opt=5;
            switch (opt) {
                case 0: break;
                case 1:
                    listAllHotels();
                    break;
                case 2:
                    AddNewHotel();
                    break;
                case 3:
                    viewDetailOf_AHotel();
                    break;
                case 4:
                    delteHotelByID();
                    break;
                case 5:
                    System.out.println("=> Permission denied for USER");
                    break;
                default: System.out.println("=> Option not avaiable! Please choose again\n");
            }
           
        } while (opt != 0);
    }


    private static void listAllHotels() {
        HotelORM hotelORM=new HotelORM();
        if(hotelORM.listAll().size()==0){System.out.println("\nNo hotel to display"); return;}
        CountryORM countryORM=new CountryORM();
        if(countryORM.listAll().size()==0){System.out.println("\nNo country to select!"); return;}
        CityORM CityORM=new CityORM();
        if(CityORM.listAll().size()==0){System.out.println("\nNo City to to select!"); return;}

        System.out.println("Please select a country: ");
        for (var c : countryORM.listAll()) {
            System.out.println(c.getId() + ". " + c.getCountry());
        }
        System.out.print("Enter: ");
        var foundCountries = countryORM.rawQueryList("id=" + validInt());

        if (foundCountries.size() > 0) {
            Country country = foundCountries.get(0);
            CityORM cityORM = new CityORM();
            var cities = cityORM.rawQueryList("countryid=" + country.getId());
            if(cities.size()==0){  System.out.println("\n=> No city select in this country"); return; } 
            System.out.println("Please select a city: ");
            for (City city : cities) {
                System.out.println(city.getId() + ". " + city.getCity());
            }
            System.out.print("Enter: ");
            var foundCities = cityORM.rawQueryList("id=" + validInt() + " AND countryid=" + country.getId());
            if (foundCities.size() > 0) {
                City city = foundCities.get(0);
                var hotels = hotelORM.rawQueryList("countryid=" + country.getId()
                        + " AND cityid=" + city.getId());
                if (hotels.size() == 0){System.out.println("\n=> No hotel to display in this city"); return;}
                System.out.println("\t\t+======================+");
                System.out.println("\t\t|         HOTEL        |");
                System.out.println("\t\t+======================+\n");
                for(int i=0; i<22;i++){System.out.print("=");}
                System.out.printf("\n|%-5s | %-12s|\n","ID","HOTEL");
                for(int i=0; i<22;i++){System.out.print("=");}System.out.println();
                for (Hotel hotel : hotels) {
                    System.out.printf("|%-5d | %-12s|\n",hotel.getId(),hotel.getHotel());
                }
                for(int i=0; i<22;i++){System.out.print("-");}System.out.println();      
            } else System.out.println("City not found.");
        } else System.out.println("Country not found!");
    }

    private static void AddNewHotel() {
        CountryORM countryORM=new CountryORM();
        if(countryORM.listAll().size()==0){System.out.println("\nNo country to select!"); return;}
        CityORM CityORM=new CityORM();
        if(CityORM.listAll().size()==0){System.out.println("\nNo City to to select!"); return;}
        System.out.println("Please select a country: ");
        for (var c : countryORM.listAll()) {
            System.out.println(c.getId() + ". " + c.getCountry());
        }
        System.out.print("Enter: ");
        var foundCountries = countryORM.rawQueryList("id=" + validInt());

        if (foundCountries.size() > 0) {
            Country country = foundCountries.get(0);
            CityORM cityORM = new CityORM();
            var cities = cityORM.rawQueryList("countryid=" + country.getId());
            if(cities.size()==0){  System.out.println("\n=> No city select in this country"); return; } 
            System.out.println("Please select a city: ");
            for (City city : cities) {
                System.out.println(city.getId() + ". " + city.getCity());
            }
            System.out.print("Enter: ");
            var foundCities = cityORM.rawQueryList("id=" + validInt() + " AND countryid=" + country.getId());

            if (foundCities.size() > 0) {
                City city = foundCities.get(0);
                Hotel hotel = new Hotel(0, null, country, city, (byte) 0, 0, null);
                System.out.println("-");
                System.out.print("Hotel name: ");
                hotel.setHotel(sc.nextLine());
                if(hotel.getHotel().equals("")){System.out.println("=> Hotel name can not be empty!!!");return;}
                System.out.print("Cost: ");
                hotel.setCost(validDouble());
                System.out.print("Stars: ");
                hotel.setStars(validByte_0To5());
                System.out.print("Detail info: ");
                hotel.setInfo(sc.nextLine());
                System.out.print("Comment: ");
                String comt=sc.nextLine();
                HotelORM hotelORM = new HotelORM();
                hotelORM.add(hotel);
                addCommentToHotels(comt, hotel.getId());
                System.out.println("-\n-New hootel added, ID: " + hotel.getId());
            } else
                System.out.println("City not found.");
        } else
            System.out.println("Country not found!");
    }

    private static void viewDetailOf_AHotel() {
        HotelORM hotelORM = new HotelORM();
        var hotels = hotelORM.listAll();
        if(hotels.size()==0){  System.out.println("\n=> No hotels to display"); return; } 

        System.out.println("Please select a  hotel: ");
        for (Hotel hotel : hotels) {
            System.out.println(hotel.getId() + ". " + hotel.getHotel());
        }
        System.out.print("Enter: ");
        var foundHotels = hotelORM.rawQueryList("id=" + validInt());
        if (foundHotels.size() > 0) {
            Hotel h = foundHotels.get(0);
            for(int i=0; i<138; i++){System.out.print("=");}System.out.println();
            System.out.printf("|%-6s | %-20s | %-10s | %-10s | %-6s | %-13s | %-30s | %-20s|\n", "ID", "Hotel", "CountryID", "CityID",
                    "Stars", "Cost", "Info", "Comment");
            for(int i=0; i<138; i++){System.out.print("=");}System.out.println();
            System.out.printf("|%-6d | %-20s | %-10s | %-10s | %-6d | %-13.2f | %-30s | %-20s|\n", 
                    h.getId(), 
                    h.getHotel(),
                    h.getCountry()==null?"Deleted":String.valueOf(h.getCountry().getId()),
                    h.getCity()==null?"Deleted":String.valueOf(h.getCity().getId()), 
                    h.getStars(), h.getCost(), 
                    h.getInfo()==null||h.getInfo().equalsIgnoreCase("")?"Empty": h.getInfo(),
                    getComment(h.getId())==null||getComment(h.getId()).equals("")?"Empty":getComment(h.getId()));
                    for(int i=0; i<138; i++){System.out.print("-");}System.out.println();
        }
        else System.out.println("Hotel not found");
    }
    private static boolean delete(int id) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost", "root", "");
                var stmt = conn.createStatement();) {
            stmt.executeUpdate("use "+DB);
            if (stmt.executeUpdate("DELETE from hotels where id=" + id)!=0){
                stmt.executeUpdate("DELETE FROM images WHERE hotelid="+id);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void delteHotelByID() {
        HotelORM hotelORM = new HotelORM();
        var hotels = hotelORM.listAll();
        if(hotels.size()==0){  System.out.println("\n=> No hotel to delete"); return; } 
        System.out.println("Please select hotel: ");
        for (Hotel hotel : hotels) {
            System.out.println(hotel.getId() + ". " + hotel.getHotel());
        }
        System.out.print("Enter: ");
        var foundHotels = hotelORM.rawQueryList("id=" + validInt());
        if (foundHotels.size() > 0 && delete(foundHotels.get(0).getId())) {

            System.out.println("\nHotel " + foundHotels.get(0).getHotel() + " deleted");
        } else
            System.out.println("Hotel not found");
    }
    private static void addCommentToHotels(String comt, int id){
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost", "root", "");
        var stmt = conn.createStatement();) {
        stmt.executeUpdate("use "+DB);
        ResultSet rs= stmt.executeQuery("SELECT * FROM Information_Schema.Columns WHERE Table_Schema='"+DB+"' AND Table_Name='hotels' AND Column_Name='comment'");
        if(!rs.next()){
            stmt.executeUpdate("ALTER TABLE hotels ADD COLUMN comment varchar(250)");
        }
        stmt.executeUpdate("UPDATE hotels SET comment='"+comt+"' WHERE id="+id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private static String getComment(int id){
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost", "root", "");
        var stmt = conn.createStatement();) {
        stmt.executeUpdate("use "+DB);
        ResultSet rs=stmt.executeQuery("select comment from hotels where id ="+id);
        if(rs.next()){
            return rs.getString("comment");
        }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }
}