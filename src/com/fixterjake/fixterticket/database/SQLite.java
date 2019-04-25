package com.fixterjake.fixterticket.database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import com.fixterjake.fixterticket.FixterTicket;
import com.fixterjake.fixterticket.utils.Utils;

public class SQLite extends Database {
	
    String dbname;
    
    
    public SQLite(FixterTicket instance){
        super(instance);
        dbname = plugin.getConfig().getString("SQLite.Filename", "FixterTicket");
    }

    public String CreateMainTicketTable = 
    		"CREATE TABLE IF NOT EXISTS FixterTicket (" +
    	    "`uuid` 		text NOT NULL," +
            "`id` 	 		integer NOT NULL," + 
    	    "`player`		text NOT NULL," +
            "`open` 		boolean NOT NULL," +
            "`message`		text NOT NULL," +
            "PRIMARY KEY	(`id`)" + 
            ");";
    
    public String CreateCommentsTable =
    		"CREATE TABLE IF NOT EXISTS FixterComments (" +
    		"`id`			integer NOT NULL," +
    		"`comment`		text," +
    		"FOREIGN KEY (`id`) REFERENCES FixterTicket(`id`)" +
    		");";

    public Connection getSQLConnection() {
        File dataFolder = new File(plugin.getDataFolder(), dbname+".db");
        if (!dataFolder.exists()){
            try {
                dataFolder.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "File write error: "+dbname+".db");
            }
        }
        try {
            if(connection!=null&&!connection.isClosed()){
                return connection;
            }
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder);
            return connection;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE,"SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            plugin.getLogger().log(Level.SEVERE, "You need the SQLite JBDC library. Google it. Put it in /lib folder.");
        }
        return null;
    }

    public void load() {
        connection = getSQLConnection();
        try {
            Utils.log(plugin, "Loading Database...");
            Statement s = connection.createStatement();
            s.executeUpdate(CreateMainTicketTable);
            s.executeUpdate(CreateCommentsTable);
            s.close();
            Utils.log(plugin, "Database Loaded!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }
}
