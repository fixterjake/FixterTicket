package com.fixterjake.fixterticket.utils;

import org.bukkit.entity.Player;
import org.bukkit.ChatColor;
import com.fixterjake.fixterticket.FixterTicket;

public class Utils {
	
	public static void sendChatMessage(Player player, String message) {
		player.sendMessage(ChatColor.GRAY + "[" + ChatColor.AQUA + ChatColor.BOLD + "Ticket" + ChatColor.RESET +  ChatColor.GRAY + "]: " + ChatColor.GRAY + message);
	}
	
	public static void sendDescriptionMessage(Player player) {
		player.sendMessage(
				ChatColor.GRAY + "============[" + ChatColor.AQUA + ChatColor.BOLD + "Ticket Commands" + ChatColor.RESET + 
				ChatColor.GRAY + "]============\n" +
				ChatColor.GRAY + "= /ticket create <message> -" + ChatColor.AQUA + " Creates a new ticket.\n" +
				ChatColor.GRAY + "= /ticket list -" + ChatColor.AQUA + " Lists any of your open tickets.\n" +
				ChatColor.GRAY + "= /ticket status <id> -" + ChatColor.AQUA + " Shows details of the specified ticket.\n" +
				ChatColor.GRAY + "= /ticket comment <id> <message> -" + ChatColor.AQUA +" Add a comment to your ticket.\n" +
				ChatColor.GRAY + "= /ticket close <id> -" + ChatColor.AQUA + " Closes specific ticket.\n" +
				ChatColor.GRAY + "==========================================");
	}
	
	public static void sendModeratorMessage(Player player) {
		player.sendMessage(
				ChatColor.GRAY + "============[" + ChatColor.AQUA + ChatColor.BOLD + "Ticket Moderator Commands" + ChatColor.RESET +
				ChatColor.GRAY + "]============\n" +
				ChatColor.GRAY + "= /ticket pending -" + ChatColor.AQUA + " Shows all pending tickets.\n" +
				ChatColor.GRAY + "= /ticket status <id> -" + ChatColor.AQUA + " Shows details of the specified ticket.\n" +
				ChatColor.GRAY + "= /ticket comment <id> <message> -" + ChatColor.AQUA +" Add a comment to any open ticket.\n" +
				ChatColor.GRAY + "= /ticket close <id> -" + ChatColor.AQUA + " Closes specific ticket.\n" +
				ChatColor.GRAY + "====================================================");
	}
	
	public static void sendDbMessage(Player player, String message) {
		player.sendMessage(message);
	}
	
	public static void log(FixterTicket plugin, String message) {
    	plugin.getLogger().info(message);
    }
}
