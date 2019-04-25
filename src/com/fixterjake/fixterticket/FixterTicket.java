package com.fixterjake.fixterticket;

import org.bukkit.plugin.java.JavaPlugin;
import com.fixterjake.fixterticket.database.Database;
import com.fixterjake.fixterticket.database.SQLite;
import com.fixterjake.fixterticket.utils.Utils;

public class FixterTicket extends JavaPlugin {
	
	private Database db;
	
    @Override
    public void onEnable() {
    	
    	this.db = new SQLite(this);
    	this.db.load();

    	this.getCommand("ticket").setExecutor(new Ticket(this));
    	Utils.log(this, "Plugin Enabled!");
    }

    @Override
    public void onDisable() {
    	Utils.log(this, "Plugin Disabled!");
    }
    
    public Database getDb() {
        return this.db;
    }
}
