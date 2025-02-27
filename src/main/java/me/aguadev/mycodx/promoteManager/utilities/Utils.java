package me.aguadev.mycodx.promoteManager.utilities;

import me.aguadev.mycodx.promoteManager.Logger;
import org.bukkit.command.CommandSender;

public class Utils {

    public static void sendMessage(CommandSender sender, String ... messages) {
        for (String m : messages) {
            if (m.contains("%prefix%")) m = m.replace("%prefix%", Logger.getInstance().getSettings().getString("prefix"));
            sender.sendMessage(CC.set(m));
        }
    }
}