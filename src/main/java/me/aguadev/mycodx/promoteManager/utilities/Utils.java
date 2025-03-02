package me.aguadev.mycodx.promoteManager.utilities;

import org.bukkit.command.CommandSender;

public class Utils {

    public static void sendMessage(CommandSender sender, String ... messages) {
        for (String m : messages) {
            sender.sendMessage(CC.set(m));
        }
    }
}