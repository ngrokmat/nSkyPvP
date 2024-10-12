package io.ngrok.plugin.utils;

import lombok.experimental.UtilityClass;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class Utils {

    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }



    public static List<String> colorize(List<String> list) {
        List<String> colorizedList = new ArrayList<>();
        for (String line : list) {
            line = colorize(line);
            colorizedList.add(line);
        }
        return colorizedList;
    }
}
