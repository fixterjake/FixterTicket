package com.fixterjake.fixterticket.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import com.fixterjake.fixterticket.FixterTicket;
import com.fixterjake.fixterticket.utils.Utils;

public abstract class Database {
	FixterTicket plugin;
	Connection connection;
	public String table = "FixterTicket";
	public String commentTable = "FixterComments";

	public Database(FixterTicket instance) {
		plugin = instance;
	}

	public abstract Connection getSQLConnection();

	public abstract void load();

	/**
	 * Initialize connection
	 */
	public void initialize() {
		connection = getSQLConnection();
		try {
			String select = "SELECT * FROM " + table + " WHERE id = ?";
			PreparedStatement ps = connection.prepareStatement(select);
			ps.setInt(1, 1);
			ResultSet rs = ps.executeQuery();
			close(ps, rs);

		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
		}
	}

	/**
	 * Function to get a player's open tickets.
	 * @param uuid Given player's UUID.
	 * @return String based on DB query.
	 */
	public String getOwnTickets(Player player) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE uuid = '" + player.getUniqueId().toString().toLowerCase() + "' AND open = 1;");
			rs = ps.executeQuery();

			String tickets = ChatColor.GRAY + "==============[" + ChatColor.AQUA + ChatColor.BOLD
					+ "Ticket" + ChatColor.RESET + ChatColor.GRAY
					+ "]==============\n" + ChatColor.GRAY + "= Please use /ticket status <id> for more \n"
					+ "= details on your ticket(s).\n=";

			String ticketsDefault = tickets;
			int rows = 0;

			while (rs.next()) {
				if (rs.getString(1).equalsIgnoreCase(player.getUniqueId().toString().toLowerCase())) {
					tickets += "\n= Ticket ID: " + rs.getInt(2) + ChatColor.GRAY + "\n"
							+ ChatColor.GRAY + "==================================";
					rows++;
				}
			}
			Utils.log(plugin, "Database query returned " + rows + " ticket(s).");
			if (tickets == ticketsDefault) {
				tickets += "\n= You do not have any open tickets!\n" + ChatColor.GRAY
						+ "==================================";
				;
			}
			return tickets;
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
			}
		}
		return null;
	}
	
	public String getAllTickets(Player player) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE open = 1;");
			rs = ps.executeQuery();

			String tickets = ChatColor.GRAY + "==============[" + ChatColor.AQUA + ChatColor.BOLD
					+ "Ticket" + ChatColor.RESET + ChatColor.GRAY
					+ "]==============\n" + ChatColor.GRAY + "= Please use /ticket status <id> for more \n"
					+ "= details on the ticket(s).\n=";

			String ticketsDefault = tickets;
			int rows = 0;

			while (rs.next()) {
				tickets += "\n= Ticket ID: " + rs.getInt(2) + " - " + rs.getString(3) + "\n"
						+ ChatColor.GRAY + "==================================";
				rows++;
			}
			Utils.log(plugin, "Database query returned " + rows + " ticket(s).");
			if (tickets == ticketsDefault) {
				tickets += "\n= No open tickets!\n" + ChatColor.GRAY
						+ "==================================";
				;
			}
			return tickets;
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		} finally {
			try {
				if (ps != null) {
					ps.close();
				}
				if (conn != null) {
					conn.close();
				}
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
			}
		}
		return null;
	}

	/**
	 * Adds a ticket to the database.
	 * @param player Given player.
	 * @param message Ticket message.
	 */
	public void addTicket(Player player, String message) {
		Connection conn = null;
		PreparedStatement ps = null;
		
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement("INSERT INTO " + table + " (uuid,id,player,open,message) VALUES(?,?,?,?,?)");
			ps.setString(1, player.getUniqueId().toString().toLowerCase());
			ps.setString(3, player.getName());
			ps.setBoolean(4, true);
			ps.setString(5, message);
			ps.execute();
			Utils.sendChatMessage(player, "Ticket Created.");
			Utils.log(plugin, "Ticket created by " + player.getName().toLowerCase() + ".");
			return;
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
			Utils.sendChatMessage(player, "Error creating ticket, please contact an administrator.");
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
				Utils.sendChatMessage(player, "Error creating ticket, please contact an administrator.");
			}
		}
	}
	
	/**
	 * Function to close ticket.
	 * @param id ID of ticket.
	 */
	@SuppressWarnings("resource")
	public void closeTicket(Player player, int id) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try {
			conn = getSQLConnection();
			
			ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE uuid = '" + player.getUniqueId().toString().toLowerCase() + "' AND open = 1;");
			rs = ps.executeQuery();
			boolean found = false;
			
			while (rs.next()) {
				if (rs.getInt(2) == id) {
					found = true;
				}
			}
			
			if (found) {
				ps = conn.prepareStatement("UPDATE " + table + " SET open = 0 WHERE id = " + id + ";");
				ps.execute();
				Utils.sendChatMessage(player, "Ticket " + id + " closed.");
				Utils.log(plugin, "Ticket " + id + " closed by " + player.getName() + ".");
				ps.close();
			}
			else {
				Utils.sendChatMessage(player, "No ticket with ID " + id + " found.");
			}
			
			return;
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
			}
		}
	}
	
	/**
	 * Add comment to own ticket.
	 * @param player Given player.
	 * @param id Given ID.
	 * @param comment Comment to add.
	 */
	public void addCommentOwn(Player player, int id, String comment) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			
			conn = getSQLConnection();
			
			ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE uuid = '" + player.getUniqueId() + "' AND open = 1;");
			rs = ps.executeQuery();
			
			boolean found = false;
			
			while(rs.next()) {
				if (rs.getInt(2) == id) {
					found = true;
				}
			}
			ps.close();
			if (!found) {
				Utils.sendChatMessage(player, "No ticket with ID " + id + " found.");
			}
			else {
				ps = conn.prepareStatement("INSERT INTO " + commentTable + " (id, comment) VALUES(?,?)");
				ps.setInt(1, id);
				ps.setString(2, comment);
				ps.execute();
				Utils.sendChatMessage(player, "Comment added to ticket " + id + ".");
				Utils.log(plugin, "Comment added to ticket " + id + " by " + player.getName() + ".");
			}
			return;
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
			Utils.sendChatMessage(player, "Error adding comment, please contact an administrator.");
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
				Utils.sendChatMessage(player, "Error adding comment, please contact an administrator.");
			}
		}
	}
	
	/**
	 * Add comment to any ticket.
	 * @param player Given player.
	 * @param id Given ID.
	 * @param comment Comment to add.
	 */
	public void addComment(Player player, int id, String comment) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			
			conn = getSQLConnection();
			
			ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE open = 1;");
			rs = ps.executeQuery();
			
			boolean found = false;
			
			while(rs.next()) {
				if (rs.getInt(2) == id) {
					found = true;
				}
			}
			ps.close();
			if (!found) {
				Utils.sendChatMessage(player, "No ticket with ID " + id + " found.");
			}
			else {
				ps = conn.prepareStatement("INSERT INTO " + commentTable + " (id, comment) VALUES(?,?)");
				ps.setInt(1, id);
				ps.setString(2, comment);
				ps.execute();
				Utils.sendChatMessage(player, "Comment added to ticket " + id + ".");
				Utils.log(plugin, "Comment added to ticket " + id + " by " + player.getName() + ".");
			}
			return;
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
			Utils.sendChatMessage(player, "Error adding comment, please contact an administrator.");
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
				Utils.sendChatMessage(player, "Error adding comment, please contact an administrator.");
			}
		}
	}
	
	@SuppressWarnings("resource")
	public void getTicketStatusOwn(Player player, int id) {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		
		try {
			conn = getSQLConnection();
			ps = conn.prepareStatement("SELECT * FROM " + table + " WHERE uuid = '" + player.getUniqueId().toString().toLowerCase() + "' AND open = 1;");
			rs = ps.executeQuery();
			boolean found = false;
			
			String message = ChatColor.GRAY + "==============[" + ChatColor.AQUA + ChatColor.BOLD
						  + "Ticket" + ChatColor.RESET + ChatColor.GRAY
						  + "]==============\n" + ChatColor.GRAY;
			
			while (rs.next()) {
				if (rs.getInt(2) == id) {
					found = true;
					message += "= ID: " + rs.getInt(2) + "\n";
					message += "= Created by: " + rs.getString(3) + "\n";
					message += "= Message: " + rs.getString(5) + "\n";
					message += "= Comments: " + "\n";
				}
			}
			
			if (found) {
				ps = conn.prepareStatement("SELECT * FROM " + commentTable + " WHERE id = " + id + ";");
				rs = ps.executeQuery();
				
				found = false;
				
				while (rs.next()) {
					found = true;
					message += "= " + rs.getString(2) + "\n";
				}
				
				if (!found) {
					message += "= No comments found.\n";
				}
				
				message += "==================================";
				Utils.sendDbMessage(player, message);
			}
			else {
				Utils.sendChatMessage(player, "No ticket with ID " + id + " found.");
			}
			
		} catch (SQLException ex) {
			plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionExecute(), ex);
			Utils.sendChatMessage(player, "Error getting status, please contact an administrator.");
		} finally {
			try {
				if (ps != null)
					ps.close();
				if (conn != null)
					conn.close();
			} catch (SQLException ex) {
				plugin.getLogger().log(Level.SEVERE, Errors.sqlConnectionClose(), ex);
				Utils.sendChatMessage(player, "Error getting status, please contact an administrator.");
			}
		}
	}

	/**
	 * Close connections.
	 * @param ps Prepared Statement.
	 * @param rs Result Set.
	 */
	public void close(PreparedStatement ps, ResultSet rs) {
		try {
			if (ps != null)
				ps.close();
			if (rs != null)
				rs.close();
		} catch (SQLException ex) {
			Error.close(plugin, ex);
		}
	}
}
