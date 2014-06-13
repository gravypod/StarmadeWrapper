package com.gravypod.wrapper.server;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Scanner;

public class ServerConfig {
	
	private final File configFile;
	private final HashMap<String, ConfigEntry> values = new HashMap<String, ConfigEntry>();
	
	protected ServerConfig(final Server server) {
	
		this.configFile = new File(server.getStarmadeDirectory(), "server.cfg");
		reloadConfig();
	}
	
	public void reloadConfig() {
	
		if (configFile.exists() && configFile.canRead() && configFile.canWrite()) {
			try {
				final Scanner sc = new Scanner(configFile);
				while (sc.hasNextLine()) {
					final String[] itemValue = sc.nextLine().split(" = ");
					if (itemValue.length != 2) {
						continue;
					}
					final String key = itemValue[0];
					final int spaceIndex = itemValue[1].indexOf(" ");
					String value, comment;
					
					if (spaceIndex < 0) {
						value = itemValue[1];
						comment = null;
					} else {
						value = itemValue[1].substring(0, spaceIndex);
						comment = itemValue[1].substring(itemValue[1].indexOf(" //"));
					}
					
					final ConfigEntry entry = new ConfigEntry(key, value, comment);
					
					values.put(key, entry);
				}
				sc.close();
			} catch (final FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void saveServerConfig() {
	
		PrintWriter writer = null;
		
		try {
			writer = new PrintWriter(configFile);
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		}
		
		for (final ConfigEntry entry : values.values()) {
			writer.println(entry.toString());
		}
		
		writer.close();
	}
	
	protected String getString(final ConfigItem item) {
	
		if (item.getType() == ItemType.STRING) {
			return getValue(item);
		}
		return null;
	}
	
	protected int getInt(final ConfigItem item) {
	
		if (item.getType() == ItemType.INTEGER) {
			return Integer.parseInt(getValue(item));
		}
		return 0;
	}
	
	protected double getDouble(final ConfigItem item) {
	
		if (item.getType() == ItemType.DOUBLE) {
			return Double.parseDouble(getValue(item));
		}
		return 0D;
	}
	
	protected boolean getBoolean(final ConfigItem item) {
	
		if (item.getType() == ItemType.BOOL) {
			return Boolean.parseBoolean(getValue(item));
		}
		return false;
	}
	
	protected long getLong(final ConfigItem item) {
	
		if (item.getType() == ItemType.LONG) {
			return Long.parseLong(getValue(item));
		}
		return 0L;
	}
	
	private String getValue(ConfigItem item) {
	
		return getValue(item.name());
		
	}
	
	private String getValue(String key) {
	
		return values.get(key).getValue();
	}
	
	protected void saveString(final ConfigItem item, final String value) {
	
		if (!values.containsKey(item.name())) {
			return;
		}
		
		values.get(item.name()).setValue(value);
		saveServerConfig();
	}
	
	protected void saveInt(final ConfigItem item, final int value) {
	
		if (!values.containsKey(item.name())) {
			return;
		}
		
		values.get(item.name()).setValue(value);
		saveServerConfig();
	}
	
	protected void saveDouble(final ConfigItem item, final double value) {
	
		if (!values.containsKey(item.name())) {
			return;
		}
		
		values.get(item.name()).setValue(value);
		saveServerConfig();
	}
	
	protected void saveBoolean(final ConfigItem item, final boolean value) {
	
		if (!values.containsKey(item.name())) {
			return;
		}
		
		values.get(item.name()).setValue(value);
		saveServerConfig();
	}
	
	protected enum ConfigItem {
		PROTECT_STARTING_SECTOR(ItemType.BOOL),
			SECTOR_SIZE(ItemType.INTEGER),
			PLANET_SIZE_DEVIATION(ItemType.DOUBLE),
			PLANET_SIZE_MEAN(ItemType.DOUBLE),
			ADMINS_CIRCUMVENT_STRUCTURE_CONTROL(ItemType.BOOL),
			ENABLE_SIMULATION(ItemType.BOOL),
			CONCURRENT_SIMULATION(ItemType.INTEGER),
			ENEMY_SPAWNING(ItemType.BOOL),
			FLOATING_ITEM_LIFETIME_SECS(ItemType.INTEGER),
			SIMULATION_SPAWN_DELAY(ItemType.INTEGER),
			SIMULATION_TRADING_FILLS_SHOPS(ItemType.BOOL),
			SECTOR_INACTIVE_TIMEOUT(ItemType.INTEGER),
			SECTOR_INACTIVE_CLEANUP_TIMEOUT(ItemType.INTEGER),
			USE_STARMADE_AUTHENTICATION(ItemType.BOOL),
			REQUIRE_STARMADE_AUTHENTICATION(ItemType.BOOL),
			PROTECTED_NAMES_BY_ACCOUNT(ItemType.INTEGER),
			STARTING_CREDITS(ItemType.INTEGER),
			DEFAULT_BLUEPRINT_ENEMY_USE(ItemType.BOOL),
			LOCK_FACTION_SHIPS(ItemType.BOOL),
			DEBUG_FSM_STATE(ItemType.BOOL),
			PHYSICS_SHAPE_CASTING_TUNNELING_PREVENTION(ItemType.BOOL),
			CATALOG_SLOTS_PER_PLAYER(ItemType.INTEGER),
			UNIVERSE_DAY_IN_MS(ItemType.LONG),
			FORCE_DISK_WRITE_COMPLETION(ItemType.BOOL),
			ASTEROIDS_ENABLE_DYNAMIC_PHYSICS(ItemType.BOOL),
			ENABLE_BREAK_OFF(ItemType.BOOL),
			COLLISION_DAMAGE(ItemType.BOOL),
			SKIN_ALLOW_UPLOAD(ItemType.BOOL),
			CATALOG_NAME_COLLISION_HANDLING(ItemType.BOOL),
			SECTOR_AUTOSAVE_SEC(ItemType.INTEGER),
			PHYSICS_SLOWDOWN_THRESHOLD(ItemType.INTEGER),
			THRUST_SPEED_LIMIT(ItemType.INTEGER),
			MAX_CLIENTS(ItemType.INTEGER),
			SUPER_ADMIN_PASSWORD_USE(ItemType.BOOL),
			SUPER_ADMIN_PASSWORD(ItemType.STRING),
			SERVER_LISTEN_IP(ItemType.STRING),
			SOCKET_BUFFER_SIZE(ItemType.INTEGER),
			PHYSICS_LINEAR_DAMPING(ItemType.DOUBLE),
			PHYSICS_ROTATIONAL_DAMPING(ItemType.DOUBLE),
			AI_DESTRUCTION_LOOT_COUNT_MULTIPLIER(ItemType.DOUBLE),
			AI_DESTRUCTION_LOOT_STACK_MULTIPLIER(ItemType.DOUBLE),
			CHEST_LOOT_COUNT_MULTIPLIER(ItemType.DOUBLE),
			CHEST_LOOT_STACK_MULTIPLIER(ItemType.DOUBLE),
			USE_WHITELIST(ItemType.BOOL),
			FILTER_CONNECTION_MESSAGES(ItemType.BOOL),
			USE_UDP(ItemType.BOOL),
			AUTO_KICK_MODIFIED_BLUEPRINT_USE(ItemType.BOOL),
			AUTO_BAN_ID_MODIFIED_BLUEPRINT_USE(ItemType.BOOL),
			AUTO_BAN_IP_MODIFIED_BLUEPRINT_USE(ItemType.BOOL),
			REMOVE_MODIFIED_BLUEPRINTS(ItemType.BOOL),
			DEBUG_SEGMENT_WRITING(ItemType.BOOL),
			TCP_NODELAY(ItemType.BOOL),
			PING_FLUSH(ItemType.BOOL),
			RECIPE_BLOCK_COST(ItemType.INTEGER),
			SHOP_SPAWNING_PROBABILITY(ItemType.DOUBLE),
			RECIPE_REFUND_MULT(ItemType.DOUBLE),
			RECIPE_LEVEL_AMOUNT(ItemType.INTEGER),
			DEFAULT_SPAWN_SECTOR_X(ItemType.INTEGER),
			DEFAULT_SPAWN_SECTOR_Y(ItemType.INTEGER),
			DEFAULT_SPAWN_SECTOR_Z(ItemType.INTEGER),
			MODIFIED_BLUEPRINT_TOLERANCE(ItemType.DOUBLE),
			TURNING_DIMENSION_SCALE(ItemType.DOUBLE),
			DEFAULT_SPAWN_LOCALPOINT_X_1(ItemType.DOUBLE),
			DEFAULT_SPAWN_LOCALPOINT_Y_1(ItemType.DOUBLE),
			DEFAULT_SPAWN_LOCALPOINT_Z_1(ItemType.DOUBLE),
			DEFAULT_SPAWN_LOCALPOINT_X_2(ItemType.DOUBLE),
			DEFAULT_SPAWN_LOCALPOINT_Y_2(ItemType.DOUBLE),
			DEFAULT_SPAWN_LOCALPOINT_Z_2(ItemType.DOUBLE),
			DEFAULT_SPAWN_LOCALPOINT_X_3(ItemType.DOUBLE),
			DEFAULT_SPAWN_LOCALPOINT_Y_3(ItemType.DOUBLE),
			DEFAULT_SPAWN_LOCALPOINT_Z_3(ItemType.DOUBLE),
			DEFAULT_SPAWN_LOCALPOINT_X_4(ItemType.DOUBLE),
			DEFAULT_SPAWN_LOCALPOINT_Y_4(ItemType.DOUBLE),
			DEFAULT_SPAWN_LOCALPOINT_Z_4(ItemType.DOUBLE),
			PLAYER_DEATH_CREDIT_PUNISHMENT(ItemType.DOUBLE),
			PLAYER_DEATH_CREDIT_DROP(ItemType.BOOL),
			PLAYER_DEATH_BLOCK_PUNISHMENT(ItemType.BOOL),
			PLAYER_DEATH_PUNISHMENT_TIME(ItemType.INTEGER),
			PLAYER_DEATH_INVULNERABILITY_TIME(ItemType.INTEGER),
			PLAYER_HISTORY_BACKLOG(ItemType.INTEGER),
			PROJECTILES_ADDITIVE_VELOCITY(ItemType.BOOL),
			PROJECTILES_VELOCITY_MULTIPLIER(ItemType.DOUBLE),
			IGNORE_DOCKING_AREA(ItemType.BOOL),
			ALLOW_UPLOAD_FROM_LOCAL_BLUEPRINTS(ItemType.BOOL),
			SHOP_NPC_STARTING_CREDITS(ItemType.INTEGER),
			SHOP_NPC_RECHARGE_CREDITS(ItemType.INTEGER),
			AI_WEAPON_AIMING_ACCURACY(ItemType.INTEGER),
			BROADCAST_SHIELD_PERCENTAGE(ItemType.INTEGER),
			BROADCAST_POWER_PERCENTAGE(ItemType.INTEGER),
			BLUEPRINT_BUY_WITH_BLOCKS(ItemType.BOOL),
			SQL_NIO_FILE_SIZE(ItemType.INTEGER);
		
		private ItemType type;
		
		private ConfigItem(final ItemType type) {
		
			this.type = type;
		}
		
		private ItemType getType() {
		
			return type;
		}
		
	}
	
	private enum ItemType {
		BOOL, DOUBLE, INTEGER, LONG, STRING;
	}
	
}
