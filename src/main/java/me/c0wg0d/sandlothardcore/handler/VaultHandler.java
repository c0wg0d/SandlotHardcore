package me.c0wg0d.sandlothardcore.handler;

import me.c0wg0d.sandlothardcore.SandlotHardcore;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public enum VaultHandler {;
    private static Permission perms;
    private static Economy econ;

    static {
        perms = null;
        econ = null;
    }

    public static void addPermission(final Player player, final String perk) {
        perms.playerAdd(player, perk);
    }

    public static void addPermission(final OfflinePlayer player, final String perk) {
        perms.playerAdd(null, player, perk);
    }

    public static void removePermission(final OfflinePlayer player, final String perk) {
        perms.playerRemove(null, player, perk);
    }

    public static boolean hasPermission(OfflinePlayer player, String perk) {
        return perms.playerHas(null, player, perk);
    }

    public static boolean setupPermissions() {
        final RegisteredServiceProvider<Permission> rsp = (RegisteredServiceProvider<Permission>) SandlotHardcore.getInstance().getServer().getServicesManager().getRegistration((Class) Permission.class);
        if (rsp.getProvider() != null) {
            perms = rsp.getProvider();
        }
        return perms != null;
    }

    public static boolean setupEconomy() {
        if (SandlotHardcore.getInstance().getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        final RegisteredServiceProvider<Economy> rsp = (RegisteredServiceProvider<Economy>) SandlotHardcore.getInstance().getServer().getServicesManager().getRegistration((Class) Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static boolean hasEcon() {
        return econ != null;
    }

    public static void depositPlayer(Player player, double v) {
        econ.depositPlayer(player, v);
    }

    public static Economy getEcon() {
        return econ;
    }

}
