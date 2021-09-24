package com.windows.chat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class chat extends JavaPlugin implements Listener {
	
	HashMap<Player, Long> chat = new HashMap<Player, Long>();

	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		Bukkit.getConsoleSender().sendMessage("§e[WINDOWS] §a도배방지 플러그인 활성화");
		getConfig().set("경고목록", null);
		saveConfig();
		new BukkitRunnable() {
				Integer i = 300;
				@Override
				public void run() {
					if (i >= 1) {
						i--;
						return;
					}
					if (i == 0) {
						timer();
						cancel();
						return;
					}
					cancel();
					return;
				}
			}.runTaskTimer(this,0,20);
	}
	
	public void timer() {
		new BukkitRunnable() {
			Integer i = 300;
			@Override
			public void run() {
				if (i >= 1) {
					i--;
					return;
				}
				if (i == 0) {
					getConfig().set("경고목록", null);
					saveConfig();
					timer();
					cancel();
					return;
				}
				cancel();
				return;
			}
		}.runTaskTimer(this,0,20);
	}
	
	public void onDisable() {
		Bukkit.getConsoleSender().sendMessage("§e[WINDOWS] §c도배방지 플러그인 비활성화");
	}
	
	public void DecompileProtect() {
		ArrayList<Integer> list = new ArrayList<Integer>(Arrays.asList(0,1,2,3,4,5,6,7,8,9,10));
		list.stream().filter((Integer num) -> num % 2 == 0);
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=false)
	public void onChat(AsyncPlayerChatEvent event) {
		Player player = (Player)event.getPlayer();
		if (!event.isCancelled()) {
	    if (!chat.containsKey(player)) {
	        long time = System.currentTimeMillis() + 3000;
	        if (player.hasPermission("windows.chat")) {
	          return;
	        }
	        chat.put(player, Long.valueOf(time));
	      }
	      else if (((Long)chat.get(player)).longValue() <= System.currentTimeMillis()) {
	        if (player.hasPermission("windows.chat")) {
	          return;
	        }
	        long time = System.currentTimeMillis() + 3000;
	        chat.put(player, Long.valueOf(time));
	      } else {
	        if ((((Long)chat.get(player)).longValue() < System.currentTimeMillis()) || 
	          (player.hasPermission("windows.chat"))) {
	          return;
	        }
	        if (getConfig().getInt("경고목록." + player.getName()) >= 3) {
	        	player.sendMessage("§6§l[ 채팅 매너 ] §f§l잦은 경고에도 도배를 하셔서 §c§l30초간 채팅금지§f§l가 되었습니다.");
	        	getServer().dispatchCommand(getServer().getConsoleSender(), "mute " + player.getName() + " 30s");
	        	getConfig().set("경고목록." + player.getName(), null);
	        	saveConfig();
	        	event.setCancelled(true);
	        	return;
	        }
	        if (getConfig().contains("경고목록." + player.getName())) {
	        	int n = getConfig().getInt("경고목록." + player.getName()) + 1;
	        	getConfig().set("경고목록." + player.getName(), n);
	        	saveConfig();
	        } else {
	        	getConfig().set("경고목록." + player.getName(), 1);
	        	saveConfig();
	        }
	        player.sendMessage("§6§l[ 채팅 매너 ] §f§l쾌적한 채팅창을 위해 채팅은 §a§l3초§f§l마다 §a§l한번§f§l씩!");
	        event.setCancelled(true);
	        return;
	        }
	      }
	}
	
}
