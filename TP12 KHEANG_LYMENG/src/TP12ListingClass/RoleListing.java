package TP12ListingClass;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import models.Role;
import orms.RoleORM;
import utils.DbManager;

public class RoleListing extends DbFunction{
    
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
                |    1. List all roles      |
                |    2. Add a new role      |
                |    3. Update role by ID   |
                |    0. Exit role listing   |
                +===========================+
                    """);
            System.out.print("Choose option: ");
            opt=validInt();
            if(!superUser && (opt==2||opt==3)) opt=4;

            switch (opt){
                case 0:break;
                case 1:
                    listAllRoles();
                    break;
                case 2:
                    addNewRole();
                    break;
                case 3:
                    UpdateRoleByID();
                    break;
                case 4:
                    System.out.println("=> Permission denied for USER");
                    break;
                default: System.out.println("=> Option not avaiable! Please choose again\n");
            }
        }while(opt!=0);
    }

    private static void listAllRoles(){
        RoleORM roleORM=new RoleORM();
        //if no role in database
        
        var roles=roleORM.listAll();
        System.out.println("\t\t+======================+");
        System.out.println("\t\t|         ROLE         |");
        System.out.println("\t\t+======================+\n");
        for(int i=0; i<20; i++){System.out.print("=");}System.out.println();
        System.out.printf("|%-5s | %-10s|\n", "ID", "Role");
        for(int i=0; i<20; i++){System.out.print("=");}System.out.println();
        for(models.Role r: roles){
            System.out.printf("|%-5d | %-10s|\n", r.getId(), r.getRole());
        }
        for(int i=0; i<20; i++){System.out.print("-");}System.out.println();
    }

    private static void addNewRole(){
        RoleORM roleORM=new RoleORM();
        models.Role role=new Role(0, null);
        System.out.print("Input role: ");
        role.setRole(sc.nextLine());
        boolean dupeRole=false;
        if(role.getRole().equals("")){System.out.println("=> Role can not be empty!");return;}
        for(models.Role r: roleORM.listAll()){
            if(r.getRole().toLowerCase().equalsIgnoreCase(role.getRole().toLowerCase())){
                System.out.println("\n=> Duplicate Role!!!");
                dupeRole=true;
                break;
            }
        }
        if(!dupeRole){
            roleORM.add(role);
            System.out.println("-\n-New added role, ID: "+role.getId());
        }
    }

    //implement update role
    private static void update(models.Role r){
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost", "root", "");
        var stmt = conn.createStatement();) {
            stmt.executeUpdate("use "+DB);
            stmt.executeUpdate("UPDATE roles set role ='" + r.getRole()+"' WHERE id="+r.getId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private static void UpdateRoleByID(){
        RoleORM roleORM=new RoleORM();
        //if no role in database 
        if(roleORM.listAll().size()==0){System.out.println("\n=> No role to update");return;}
        System.out.println("Please select a role");
        for(models.Role r: roleORM.listAll()){
            System.out.println(r.getId()+". "+r.getRole());
        }
        System.out.print("Enter: ");
        var foundRoles=roleORM.rawQueryList("id="+validInt());

        if(foundRoles.size()>0){
            System.out.print("Enter new Role to update: ");
            models.Role role=new Role(foundRoles.get(0).getId(), sc.nextLine());
            if(role.getRole().equals("")){System.out.println("=> Role can not be empty!");return;}

            boolean dupeRole=false;
            for(models.Role r: roleORM.listAll()){
                if(r.getRole().toLowerCase().equalsIgnoreCase(role.getRole().toLowerCase())){
                    System.out.println("\n=> Duplicate Role!!!\nCan not update");
                    dupeRole=true;
                    break;
                }
            }
            if(!dupeRole){
            update(role);
                System.out.println("\n=> Role updated");
            }            
        }else System.err.println("Role not found");
    }
}

