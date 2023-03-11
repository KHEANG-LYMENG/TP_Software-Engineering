package TP12ListingClass;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import models.User;
import orms.RoleORM;
import orms.UserORM;
import utils.DbManager;

public class UserListing extends DbFunction {

    public static void main(String[] args) throws SQLException {
        create_database(DB);
        DbManager.getInstance(DB, "root", null);
        int opt;
        if(!superUser){
            System.out.println("\n=> Permission denied for USER");
            System.out.println("=> Because UserListing class is for admin only");
            System.out.println("=> So, only when you Login can use UserListing class\n");
            return;
        }

        do{
            System.out.println("""
                \n\n
                +===========================+
                |           MENU            |
                +===========================+
                |    1. List all users      |
                |    2. Add a new user      |
                |    3. Delete user by ID   |
                |    0. Exit user listing   |
                +===========================+
                    """);
            System.out.print("Choose option: ");
            opt=validInt();
            if(!superUser && (opt==2||opt==3)) opt=4;
            switch(opt){
                case 0: break;
                case 1:
                    listAllUsers();
                    break;
                case 2:
                    addNewUser();
                    break;
                case 3:
                    deleteUserByID();
                    break;
                default: System.out.println("=> Option not avaiable! Please choose again\n");
            }
        } while (opt != 0 );
    }

    private static boolean checkUsername(String name){
        UserORM userORM=new UserORM();
        for(User u: userORM.listAll()){
            if(u.getUsername().equals(name)) return false;
        }
        return true;
    }

    private static void addNewUser(){
        RoleORM roleORM=new RoleORM();
        if(roleORM.listAll().size()==0){System.out.println("\n=> No role to select for the user");return;}

        System.out.println("Please select a role: ");
        for(models.Role r: roleORM.listAll()){
            System.out.println(r.getId()+". "+r.getRole());
        }
        System.out.print("Enter: ");
        var foundrole=roleORM.rawQueryList("id="+validInt());
        if(foundrole.size()>0){
            User user=new User(0, null, null, null, foundrole.get(0), (short)0, null);
            System.out.print("Username: ");
            user.setUsername(sc.nextLine());  
            if(user.getUsername().equals("")){System.out.println("=> Username can not be emplty");return;}
            if(user.getUsername().length()<2){System.out.println("Username must be 2 character or more!");return;}
            if(!checkUsername(user.getUsername())){System.out.println("=> Duplicate username!!!");return;}
            System.out.print("Email: ");
            user.setEmail(sc.nextLine());
            
            System.out.print("Password: ");
            String pass=sc.nextLine();
            if(pass.equals("")){System.out.println("=> Password can not be empty");return;}
            if(pass.length()<6 || pass.length()>12){System.out.println("=> Password minimum 6 characters and maximum 12 characters");return;}
            user.setPass(pass);

            System.out.print("Avatar: ");
            user.setAvatar(sc.nextLine());

            UserORM userORM=new UserORM();
            userORM.add(user);
            System.out.println("\n=> New User added");
        }else System.out.println("Role not found");
    }

    private static void listAllUsers(){
        UserORM userORM=new UserORM();
        //if not a single user exists
        if(userORM.listAll().size()==0){System.out.println("\n=> No user to display");return;};
        for(int i=0; i<117;i++){System.out.print("=");}System.out.println();
        System.out.printf("|%-6s | %-17s | %-35s | %-17s | %-10s | %-15s|\n", "ID", "Username", "Password", "Email",  "RoleID", "Avatar");
        for(int i=0; i<117;i++){System.out.print("=");}System.out.println();
        for (User u : userORM.listAll()) {
            System.out.printf("|%-6d | %-17s | %-35s | %-17s | %-10s | %-15s|\n", u.getId(), u.getUsername(), 
                                                                           u.getPass(), 
                                                                           u.getEmail()==null||u.getEmail().equals("")?"Empty":u.getEmail(),  
                                                                           u.getRole()==null?"Deleted":String.valueOf(u.getRole().getId()), 
                                                                           u.getAvatar()==null||u.getAvatar().equals("")?"Empty": u.getAvatar());
        }
        for(int i=0; i<117;i++){System.out.print("-");}System.out.println();
    }


    private static boolean delete(int id){
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost", "root", "");
            var stmt = conn.createStatement();) {
            stmt.executeUpdate("use "+DB);
            if(stmt.executeUpdate("delete from users where id=" + id)!=0){
                return true;
            }   
        } catch (SQLException e) {
            e.printStackTrace();
        }
            return false;
    }
    private static void deleteUserByID(){
        UserORM userORM=new UserORM();
        //if not a  signle user exists
        if(userORM.listAll().size()==0){System.out.println("No user to delete");return;}

        System.out.println("Please select user");
        for(User u: userORM.listAll()){
            System.out.println(u.getId()+". "+ u.getUsername());
        }
        System.out.print("Enter: ");
        var founduser=userORM.rawQueryList("id="+validInt());
        if(founduser.size()>0 && delete(founduser.get(0).getId())){
            System.out.println("\n=> User deleted");
        }else System.out.println("=> User not found");
    }
}
