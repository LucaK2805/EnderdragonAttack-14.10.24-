package gamemode.enderdragonattack.GameWorld;

import gamemode.enderdragonattack.Color.Gradient;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

public class WorldRegenerateCommand implements CommandExecutor {

    Gradient pluginInstance = new Gradient();
    String gradientPrefix = pluginInstance.generateGradient("Dragon");
    String Prefix = "[" + gradientPrefix + ChatColor.RESET + "] ";

    private final JavaPlugin plugin;

    public WorldRegenerateCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("resetworld")) {
            boolean resetSuccessful = resetWorld("GameWorld", "ExampleWorld", "Lobby");
            if (resetSuccessful) {
                sender.sendMessage(Prefix + ChatColor.GREEN + "The GameWorld was successfully reset!");
            } else {
                sender.sendMessage(Prefix + ChatColor.RED + "The GameWorld cannot be reset!");
            }
            return true;
        }
        return false;
    }

    public boolean resetWorld(String worldNameToDelete, String sourceWorldName, String lobbyWorldName) {
        World lobbyWorld = Bukkit.getWorld(lobbyWorldName);

        if (lobbyWorld == null) {
            plugin.getLogger().severe("The lobby world '" + lobbyWorldName + "' does not exist!");
            return false;
        }

        World worldToDelete = Bukkit.getWorld(worldNameToDelete);

        if (worldToDelete != null) {
            for (Player player : worldToDelete.getPlayers()) {
                player.teleport(lobbyWorld.getSpawnLocation());
            }

            boolean unloaded = false;
            for (int i = 0; i < 10; i++) {
                if (Bukkit.unloadWorld(worldToDelete, false)) {
                    unloaded = true;
                    break;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (!unloaded) {
                return false;
            }

            File worldDir = plugin.getServer().getWorldContainer().toPath().resolve(worldNameToDelete).toFile();
            if (!deleteDirectory(worldDir)) {
                return false;
            }

            Path source = Paths.get(plugin.getServer().getWorldContainer().getAbsolutePath(), sourceWorldName);
            Path target = Paths.get(plugin.getServer().getWorldContainer().getAbsolutePath(), worldNameToDelete);

            try {
                copyDirectory(source, target);
            } catch (IOException e) {
                return false;
            }

            Bukkit.getScheduler().runTask(plugin, () -> {
                Bukkit.createWorld(new WorldCreator(worldNameToDelete));
            });

            return true;
        } else {
            return false;
        }
    }

    private boolean deleteDirectory(File directory) {
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                deleteDirectory(file);
            }
        }
        return directory.delete();
    }

    private void copyDirectory(Path source, Path target) throws IOException {
        try (Stream<Path> stream = Files.walk(source)) {
            stream.forEach(s -> {
                try {
                    Path d = target.resolve(source.relativize(s));
                    if (Files.isDirectory(s)) {
                        if (!Files.exists(d)) {
                            Files.createDirectory(d);
                        }
                    } else {
                        if (!s.getFileName().toString().equals("session.lock")) {
                            Files.copy(s, d, StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                } catch (IOException e) {
                    plugin.getLogger().severe("Error copying file: " + s + " to: " + target.resolve(source.relativize(s)));
                    throw new RuntimeException("Error copying files", e);
                }
            });
        }
    }
}