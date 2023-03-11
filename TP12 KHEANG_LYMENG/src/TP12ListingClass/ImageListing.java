package TP12ListingClass;
import java.sql.*;

import models.City;
import models.Hotel;
import models.Image;
import orms.CityORM;
import orms.CountryORM;
import orms.HotelORM;
import orms.ImageORM;
import utils.DbManager;

public class ImageListing extends DbFunction{
    
    public static void main(String[] args) throws SQLException {
        create_database(DB);
        DbManager.getInstance(DB, "root", null);
        int opt;
        do{
            System.out.println("""
                +================================+
                |              MENU              |
                +================================+
                |    1. List all images          |
                |    2. Add a new image          |
                |    3. Delelet an image by ID   |
                |    0. Exit image listing       |
                +================================+
                    """);
            System.out.print("Choose option: ");
            opt=validInt();
            if(!superUser && (opt==2||opt==3)) opt=4;

            switch (opt){
                case 0: break;
                case 1:
                    listAllImage();
                    break;
                case 2:
                    addNewImage();
                    break;
                case 3:
                    deleteImageByID();
                    break;
                case 4:
                    System.out.println("=> Permission denied for USER\n");
                    break;
                default: System.out.println("=> Option not avaiable! Please choose again\n");
            }
        }while(opt!=0);
    }


    private static void listAllImage(){
        ImageORM imageORM=new ImageORM();
        if(imageORM.listAll().size()==0){System.out.println("\n=> No image to display");return;}
        CountryORM countryORM=new CountryORM();
        if(countryORM.listAll().size()==0){System.out.println("\n=> No country to select!"); return;}

        CityORM cityORM=new CityORM();
        if(cityORM.listAll().size()==0){System.out.println("\n=> No city to select!"); return;}

        HotelORM hotelORM=new HotelORM();
        if(hotelORM.listAll().size()==0){System.out.println("\n=> No hotel to select");return;}

        System.out.println("Please a select country: ");

        for(var country : countryORM.listAll()){
            System.out.println(country.getId()+". " +country.getCountry());
        }

        System.out.print("\nEnter: ");
        var foundCountries=countryORM.rawQueryList("id="+validInt());
        if(foundCountries.size()>0){            
            var images=imageORM.rawQueryList("hotelid IN (SELECT id FROM hotels WHERE countryid ="+foundCountries.get(0).getId()+")");
            if(images.size()==0){System.out.println("-\nNo image to display in this country");return;}
        
            var cities=cityORM.rawQueryList("countryID="+foundCountries.get(0).getId());
            if(cities.size()==0) {System.out.println("-\nNo city to select in this country!"); return;}

            var hotels=hotelORM.rawQueryList("countryid="+foundCountries.get(0).getId());
            if(hotels.size()==0){System.out.println("-\nNo hotel to select in this country");return;}
            
            System.out.println("Please select a city: ");
            for (City c : cities) {
                System.out.println(c.getId() + ". " + c.getCity());
            }
            System.out.print("Enter: ");
            var foundCities = cityORM.rawQueryList("id=" + validInt() + " AND countryid=" + foundCountries.get(0).getId());
            if(foundCities.size()>0){
                images=imageORM.rawQueryList("hotelid in (SELECT id FROM hotels WHERE cityid="+foundCities.get(0).getId()+")");
                if(images.size()==0){System.out.println("\n=> No image to display in this city");return;}

                hotels=hotelORM.rawQueryList("cityid="+foundCities.get(0).getId());
                if(hotels.size()==0){System.out.println("\n=> No hotel to select in this city");return;}
                
                System.out.println("Please select a hotel");
                for(Hotel h: hotels){
                    System.out.println(h.getId()+". "+h.getHotel());
                }

                System.out.print("Enter: ");
               var foundHotels =hotelORM.rawQueryList("id="+validInt()+" AND cityid="+foundCities.get(0).getId()+
                                " AND countryid="+foundCountries.get(0).getId());
                if(foundHotels.size()>0){
                    images=imageORM.rawQueryList("hotelID=" + foundHotels.get(0).getId());
                    if(images.size()==0){System.out.println("\n=> No image to display in this hotel");return;}
                    System.out.println("\t\t+======================+");
                    System.out.println("\t\t|         IMAGE        |");
                    System.out.println("\t\t+======================+\n");
                    for(int i=0; i<46; i++){System.out.print("=");}System.out.println();
                    System.out.printf("|%-6s | %-12s | %-20s|\n", "ID", "HotelID", "ImagePath");
                    for(int i=0; i<46; i++){System.out.print("=");}System.out.println();
                    for(Image i: images){
                        System.out.printf("|%-6d | %-12d | %-20s|\n", i.getId(), i.getHotel().getId(), i.getImagePath());
                    }
                    for(int i=0; i<46; i++){System.out.print("-");}System.out.println();
                }
            }else System.out.println("City not fond");
        }else System.out.println("Counry not found!");
        System.out.println();
    }

    private static void addNewImage(){
        CountryORM countryORM=new CountryORM();
        if(countryORM.listAll().size()==0){System.out.println("\n=> No country to select!"); return;}
        
        CityORM cityORM=new CityORM();
        if(cityORM.listAll().size()==0){System.out.println("\n=> No city to select!"); return;}

        HotelORM hotelORM=new HotelORM();
        if(hotelORM.listAll().size()==0){System.out.println("\n=> No hotel to select");return;}

        System.out.println("Please select a country: ");

        for(var country : countryORM.listAll()){
            System.out.println(country.getId()+". " +country.getCountry());
        }

        System.out.print("\nEnter: ");
        var foundCountries=countryORM.rawQueryList("id="+validInt());
        if(foundCountries.size()>0){    
            var cities=cityORM.rawQueryList("countryID="+foundCountries.get(0).getId());
            if(cities.size()==0) {System.out.println("\n=> No city to select in this country!"); return;}
            
            var hotels=hotelORM.rawQueryList("countryid="+foundCountries.get(0).getId());
            if(hotels.size()==0){System.out.println("\n=> No hotel to select in this country");return;}

            System.out.println("Please select a city: ");
            for (City c : cities) {
                System.out.println(c.getId() + ". " + c.getCity());
            }
            System.out.print("Enter: ");
            var foundCities = cityORM.rawQueryList("id=" + validInt() + " AND countryid=" + foundCountries.get(0).getId());
            if(foundCities.size()>0){
                hotels=hotelORM.rawQueryList("cityid="+foundCities.get(0).getId());
                if(hotels.size()==0){System.out.println("\n=> No hotel to select in this city");return;}
                
                System.out.println("Please select a hotel");
                for(Hotel h: hotels){
                    System.out.println(h.getId()+". "+h.getHotel());
                }

                System.out.print("Enter: ");
               var foundHotels =hotelORM.rawQueryList("id="+validInt()+" AND cityid="+foundCities.get(0).getId()+
                                " AND countryid="+foundCountries.get(0).getId());
                if(foundHotels.size()>0){
                    ImageORM imageORM=new ImageORM();
                    Image image=new Image(0, foundHotels.get(0), null);
                    System.out.print("Enter Image path: ");
                    image.setImagePath(sc.nextLine());
                    if(image.getImagePath().equals("")){System.out.println("=> Image path can not be empty");return;}
                    imageORM.add(image);
                    System.out.println("\n=> New added image, ID: "+image.getId());
                }else System.out.println("Hotel not found");
            }else System.out.println("City not fond");
        }else System.out.println("Counry not found!");
     }

    private static boolean delete(int id) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost", "root", "");
                var stmt = conn.createStatement();) {
            stmt.executeUpdate("use "+DB);
            if(stmt.executeUpdate("delete from images where id=" + id)!=0){
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static void deleteImageByID(){
        ImageORM ImageORM=new ImageORM();
        if(ImageORM.listAll().size()==0){System.out.println("\n=> No Image to delete!"); return;}

        for(var i : ImageORM.listAll()){
            System.out.println( i.getId()+". "+ i.getImagePath());
        }
        System.out.print("Enter: ");
        var foundCountries=ImageORM.rawQueryList("id="+validInt());
        if(foundCountries.size()>0 && delete(foundCountries.get(0).getId())){
            System.out.println("\nImage \""+ foundCountries.get(0).getImagePath() +"\" deleted");
        }else System.out.println("Image not found");
    }
}
