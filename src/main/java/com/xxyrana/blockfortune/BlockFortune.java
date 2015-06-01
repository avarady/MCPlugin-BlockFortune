package com.xxyrana.blockfortune;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * 
 * @author xxyrana
 *
 */
public class BlockFortune extends JavaPlugin implements Listener {
	private static final Logger log = Logger.getLogger("Minecraft");
	public static Economy econ = null;
	public static Permission perms = null;
	public static Chat chat = null;

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		if (!setupEconomy() ) {
			log.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		setupPermissions();
		setupChat();
	}

	@Override
	public void onDisable() {
		log.info(String.format("[%s] Disabled Version %s", getDescription().getName(), getDescription().getVersion()));
	}

	//----------------//
	// Event Listener //
	//----------------//
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		Block block = event.getBlock();
		Material type = block.getType();
		String reward;
		int num = getNum(event.getPlayer());
		ItemStack is;

		if(type.equals(Material.IRON_ORE)){
			reward = "265";
			is = getItemStack(reward, num);
		} else if(type.equals(Material.GOLD_ORE)){
			reward = "266";
			is = getItemStack(reward, num);
		} else if(type.equals(Material.COAL_ORE)){
			reward = "263";
			is = getItemStack(reward, num);
		} else if(type.equals(Material.LAPIS_ORE)){
			reward = "351:4";
			is = getItemStack(reward, num);
		} else if(type.equals(Material.DIAMOND_ORE)){
			reward = "264";
			is = getItemStack(reward, num);
		} else if(type.equals(Material.REDSTONE_ORE) || type.equals(Material.GLOWING_REDSTONE_ORE)){
			reward = "331";
			is = getItemStack(reward, num);
		} else if(type.equals(Material.EMERALD_ORE)){
			reward = "388";
			is = getItemStack(reward, num);
		} else if(type.equals(Material.QUARTZ_ORE)){
			reward = "406";
			is = getItemStack(reward, num);
		} else {
			Collection<ItemStack> drops = block.getDrops();
			Iterator<ItemStack> iterator = drops.iterator();
			while(iterator.hasNext()){
				is = iterator.next();
				is.setAmount(num);
				event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), is);				
			}
			event.setCancelled(true);
			event.getBlock().setType(Material.AIR);
			return;
		}
		event.getBlock().getWorld().dropItemNaturally(event.getBlock().getLocation(), is);
		event.setCancelled(true);
		event.getBlock().setType(Material.AIR);
	}

	private int getNum(Player player){
		if(perms.has(player, "fortune.one")){
			return 2;
		} else if(perms.has(player, "fortune.two")){
			return 3;
		} else if(perms.has(player, "fortune.three")){
			return 4;
		} else if(perms.has(player, "fortune.four")){
			return 5;
		} else if(perms.has(player, "fortune.five")){
			return 6;
		} else if(perms.has(player, "fortune.six")){
			return 7;
		} else if(perms.has(player, "fortune.seven")){
			return 8;
		}
		return 1;
	}

	//-----
	// Vault Setup
	//-----
	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}
	private boolean setupChat() {
		RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
		chat = rsp.getProvider();
		return chat != null;
	}
	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		perms = rsp.getProvider();
		return perms != null;
	}

	//-------------//
	// Item Lookup //
	//-------------//
	private ItemStack getItemStack(String str, int num){
		Material m;
		//Check for id formatting
		if(str.contains(":")){
			String[] ids = str.split(":");
			m = Material.matchMaterial(ids[0]);
			ItemStack i = new ItemStack(m, num);
			i.setDurability(Byte.parseByte(ids[1]));
			return i;
		}
		//Check built in material function
		m = Material.matchMaterial(str);
		ItemStack i = new ItemStack(m, num);
		return i;
	}

}
