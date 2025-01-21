package com.externaldb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class PostgresFetchData {
	
    public static void main(String[] args) throws Exception {

        String jdbcUrl = "jdbc:postgresql://<host>/test_db";
        String username = "postgres";
        String password = "";


            // Register the PostgreSQL driver

            Class.forName("org.postgresql.Driver");

            // Connect to the database

            Connection connection = DriverManager.getConnection(jdbcUrl, username, password);
            
            Statement s =connection.createStatement();
            long start = System.currentTimeMillis();
            s.execute("SELECT * from public.app_users");
            ResultSet rs = s.getResultSet();
            int ctr= 0;
            while(rs.next()) {
            	rs.getInt(1);
            	ctr++;
            	//System.out.print(rs.getInt(1) +"  ");
            	//System.out.print(rs.getString(2) +"  ");
            	//System.out.print(rs.getString(3) +"  ");
            	// System.out.print(rs.getString(4) +"  ");
            }
            long end = System.currentTimeMillis();
            System.out.println("Records =" + ctr);
            System.out.println("Time Taken =" + (end-start));
            	
            // Close the connection
            	s.close();
            	connection.close();
        }
}