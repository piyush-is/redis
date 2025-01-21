package com.externaldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Random;

public class PostgresDataDump {
	
    public static void main(String[] args) throws Exception {

        String jdbcUrl = "jdbc:postgresql://<host>:5432/test_db";
        String username = "postgres";
        String password = "";


            // Register the PostgreSQL driver

            Class.forName("org.postgresql.Driver");

            // Connect to the database

            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            
//            Statement s =connection.createStatement();
//            s.execute("SELECT * from app_users");
//            ResultSet rs = s.getResultSet();
//            System.out.println(rs.next());
              	
            // Perform desired database operations
            PreparedStatement ps = connection.prepareStatement("INSERT INTO app_users (first_name, last_name, role) VALUES (?, ?, ?)");
            Random rand = new Random(System.currentTimeMillis());
            for(int ctr = 1; ctr <=1000; ctr++) {
            	  ps.setString(1,"fname-"+ctr+"-"+rand.nextInt());            
            	  ps.setString(2,"lname-"+ctr+"-"+rand.nextInt());   
            	  ps.setString(3,"role-"+ctr+"-"+rand.nextInt());
            	  ps.addBatch();
            	}
            	int [] num = ps.executeBatch();
            	//connection.commit();
            	System.out.println(num.length);
            	
            // Close the connection
            	ps.clearBatch();
            	ps.close();
            	connection.close();
        }
}