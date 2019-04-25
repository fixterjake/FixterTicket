package com.fixterjake.fixterticket;

import java.util.Arrays;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.fixterjake.fixterticket.database.Database;
import com.fixterjake.fixterticket.utils.Utils;

public class Ticket implements CommandExecutor {

	private FixterTicket plugin;
	private Database db;

	public Ticket(FixterTicket plugin) {
		this.plugin = plugin;
		this.db = this.plugin.getDb();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {

			Player player = (Player) sender;

			if (sender.hasPermission("fixterticket.ticket".toLowerCase())) {
				if (args.length > 0) {
					switch (args[0].toLowerCase()) {
						case "create":
							if (args.length > 1) {
								String[] messageArgs = Arrays.copyOfRange(args, 1, args.length);
								String message = String.join(" ", messageArgs);
								db.addTicket(player, message);
							} else {
								Utils.sendChatMessage(player, "Correct usage: " +
															  "/ticket create <message>" + ChatColor.RESET);
							}
							break;
							
						case "list":
							String message = db.getOwnTickets(player);
							Utils.sendDbMessage(player, message);
							break;
							
						case "close":
							if (args.length > 1) {
								try {
									int id = Integer.parseInt(args[1]);
									db.closeTicket(player, id);
								}
								catch (Exception ex) {
									Utils.sendChatMessage(player, "Correct usage: " +
											   				      "/ticket close <id>" + ChatColor.RESET);
								}
								
							}
							else {
								Utils.sendChatMessage(player, "Correct usage: "+ 
														      "/ticket close <id>" + ChatColor.RESET);
							}
							break;
							
						case "mod":
							Utils.sendModeratorMessage(player);
							break;
							
						case "comment":
							try {
								int id = Integer.parseInt(args[1]);
								String[] commentArgs = Arrays.copyOfRange(args, 2, args.length);
								String comment = String.join(" ", commentArgs);
								if (sender.hasPermission("fixterticket.mod")) {
									db.addComment(player, id, comment);
								}
								else {
									db.addCommentOwn(player, id, comment);
								}
							}
							catch (Exception ex) {
								Utils.sendChatMessage(player, "Correct usage: " +
										  "/ticket comment <id> <message>" + ChatColor.RESET);
							}
							break;
							
						case "pending":
							if (sender.hasPermission("fixterticket.mod")) {
								String pending = db.getAllTickets(player);
								Utils.sendDbMessage(player, pending);
							}
							else {
								Utils.sendChatMessage(player, "You don't have permission to do that!");
							}
							break;
							
						case "status":
							try {
								int id = Integer.parseInt(args[1]);
								db.getTicketStatusOwn(player, id);
							}
							catch (Exception ex) {
								Utils.sendChatMessage(player, "Correct usage: " +
										  "/ticket status <id>");
							}
							break;
							
						default:
							Utils.sendDescriptionMessage(player);
							break;
					}
				} else {
					Utils.sendDescriptionMessage(player);
				}
				return true;
			} else {
				Utils.sendChatMessage(player, "You don't have permission to do that!");
				return true;
			}
		}
		return false;
	}
}
