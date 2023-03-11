import java.sql.SQLException;

import TP12ListingClass.*;
import models.User;
import orms.RoleORM;
import orms.UserORM;
import utils.DbManager;

public class MainMenu {
    public static void main(String[] args) throws SQLException {
        DbFunction.create_database(DbFunction.DB);
        System.out.println();
        boolean userAcc=false;
        /* NOTE:
        By default execute the program is just a user account
        So ypu can only view listing

        If want to to LOGIN as admin for the first time must use username and password Provide below
        
                userName= root
                password=""
                root is like admin is the only one who can do everything while user role will be restricted to insert, delete and update

        */
        int opt=0;
        do{

            System.out.println("""
            ===========> Main Menu <===========
            1. Hotel listing
            2. Country listing
            3. City listing 
            4. Image listing
            5. Users listing 
            6. Role listing""");
            System.out.print((userAcc?"7. Logout":"7. Login")+ "\n0. Exit app\n");
            System.out.println("===================================\n");

            System.out.print("Choose option: ");
            opt=DbFunction.validInt();
            switch (opt){
                case 0:break;
                case 1:
                    HotelListing.main(args);
                    break;
                case 2:
                    CountryListing.main(args);
                    break;
                case 3:
                    CityListing.main(args);
                    break;
                case 4:
                    ImageListing.main(args);
                    break;
                case 5:
                    UserListing.main(args);
                    break;
                case 6:
                    RoleListing.main(args);
                    break;
                case 7:
                    if(userAcc){
                        DbFunction.superUser=false; 
                        userAcc=false;
                        System.out.println("<========== Logged out ==========>");
                        System.out.println("\n  Press enter to continue...");
                        DbFunction.sc.nextLine();
                        break;
                    }

                    System.out.print("Username: ");
                    String userName=DbFunction.sc.nextLine();
                    System.out.print("Passsword: ");
                    String pswd=DbFunction.sc.nextLine();
                    if(varifyUser(userName, pswd)){
                        System.out.println("<========== Logged in as "+userName+" ==========>");
                        userAcc=true;
                    }else System.out.println("=> User or password incorrect!!!");
                            System.out.println("\n  Press enter to continue...");
                            DbFunction.sc.nextLine();
                    break;
                default: System.out.println("=> Option not avaiable! Please choose again\n");
            }

        }while(opt!=0);

        DbFunction.sc.close();
    }

    private static boolean varifyUser(String userName, String pswd) throws SQLException{
        DbManager.getInstance(DbFunction.DB, "root", null);
        UserORM userORM=new UserORM();
        models.User u=new User(0, userName, null, null, null, null, null);
        u.setPass(pswd);
        var foundUser =userORM.rawQueryList("username='"+u.getUsername()+
                                        "' AND pass='"+u.getPass()+"'");

                                        
        if(pswd.equals("")&& userName.equals("root")){
            DbFunction.superUser=true;
            return true;//root
        }
        
        if(foundUser.size()>0){
            RoleORM roleORM =new RoleORM();
            var checkRole=roleORM.rawQueryList("id="+foundUser.get(0).getRole().getId() +" AND role='Admin'");
            if(checkRole.size()>0) DbFunction.superUser=true;
            return true;
        }
        return false;
    }
}
