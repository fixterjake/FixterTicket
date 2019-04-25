package com.fixterjake.fixterticket.database;

import java.util.logging.Level;
import com.fixterjake.fixterticket.FixterTicket;

public class Error {
	
    public static void execute(FixterTicket plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Couldn't execute MySQL statement: ", ex);
    }
    public static void close(FixterTicket plugin, Exception ex){
        plugin.getLogger().log(Level.SEVERE, "Failed to close MySQL connection: ", ex);
    }
}
