package xyz.holocons.mc.resourceguard;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class CommandHandler implements CommandExecutor {

    private final ResourceGuard plugin;

    public CommandHandler(ResourceGuard plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof ConsoleCommandSender)) {
            sender.sendMessage(Component.text("Commands from this plugin should only be used from console!", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            return false;
        } else {
            switch (args[0].toUpperCase(Locale.ROOT)) {
                case "VALIDATE" -> {
                    plugin.getResourceRegionManager().validateRegions();
                    return true;
                }
                case "RESET" -> {
                    plugin.getResourceRegionManager().recreateRegions();
                    return true;
                }
                case "RELOAD" -> {
                    plugin.reloadConfig();
                    plugin.getLogger().info("Config reloaded from disk!");
                    return true;
                }
                default -> {
                    return false;
                }
            }
        }
    }
}
