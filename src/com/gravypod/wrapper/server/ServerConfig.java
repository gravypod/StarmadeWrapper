package com.gravypod.wrapper.server;

import java.io.*;
import java.util.HashMap;

public class ServerConfig {

    private Server server;
    private File file;
    private HashMap<ConfigItem, String> values = new HashMap<ConfigItem, String>();

    protected ServerConfig(Server server) {
        this.server = server;
        this.file = new File(server.getStarmadeDirectory() + "/server.cfg");
        if (file.exists() && file.canRead() && file.canWrite()) {
            try {
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line = null;

                while((line = bufferedReader.readLine()) != null) {
                    String[] itemValue = line.split(" = ");
                    String item = itemValue[0];
                    int spaceIndex = itemValue[1].indexOf(" ");
                    String value;
                    if (spaceIndex < 0) {
                        value = itemValue[1];
                    } else {
                        value = itemValue[1].substring(0, itemValue[1].indexOf(" //"));
                    }
                    values.put(ConfigItem.valueOf(item), value);
                }

                bufferedReader.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveServerConfig() {
        try {
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (ConfigItem item : values.keySet()) {
                String value = values.get(item);
                bufferedWriter.write(item.name() + " = " + value);
                bufferedWriter.newLine();
            }

            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected String getString(ConfigItem item) {
        if (item.getType().equals("string")) {
            return values.get(item);
        }
        return null;
    }

    protected int getInt(ConfigItem item) {
        if (item.getType().equals("int")) {
            return Integer.parseInt(values.get(item));
        }
        return 0;
    }

    protected double getDouble(ConfigItem item) {
        if (item.getType().equals("double")) {
            return Double.parseDouble(values.get(item));
        }
        return 0D;
    }

    protected boolean getBoolean(ConfigItem item) {
        if (item.getType().equals("boolean")) {
            return Boolean.parseBoolean(values.get(item));
        }
        return false;
    }

    protected void saveString(ConfigItem item, String value) {
        values.remove(item);
        values.put(item, value);
        saveServerConfig();
    }

    protected void saveInt(ConfigItem item, int value) {
        values.remove(item);
        values.put(item, String.valueOf(value));
        saveServerConfig();
    }

    protected void saveDouble(ConfigItem item, double value) {
        values.remove(item);
        values.put(item, String.valueOf(value));
        saveServerConfig();
    }

    protected void saveBoolean(ConfigItem item, boolean value) {
        values.remove(item);
        values.put(item, String.valueOf(value));
        saveServerConfig();
    }

    protected enum ConfigItem {
        // some of these may be longs, have to check
        PROTECT_STARTING_SECTOR("boolean"),
        ENABLE_SIMULATION("boolean"),
        CONCURRENT_SIMULATION("int"),
        ENEMY_SPAWNING("boolean"),
        FLOATING_ITEM_LIFETIME_SECS("int"),
        SIMULATION_SPAWN_DELAY("int"),
        SIMULATION_TRADING_FILLS_SHOPS("boolean"),
        SECTOR_INACTIVE_TIMEOUT("int"),
        SECTOR_INACTIVE_CLEANUP_TIMEOUT("int"),
        USE_STARMADE_AUTHENTICATION("boolean"),
        REQUIRE_STARMADE_AUTHENTICATION("boolean"),
        PROTECTED_NAMES_BY_ACCOUNT("int"),
        STARTING_CREDITS("int"),
        DEFAULT_BLUEPRINT_ENEMY_USE("boolean"),
        LOCK_FACTION_SHIPS("boolean"),
        DEBUG_FSM_STATE("boolean"),
        PHYSICS_SHAPE_CASTING_TUNNELING_PREVENTION("boolean"),
        CATALOG_SLOTS_PER_PLAYER("int"),
        UNIVERSE_DAY_IN_MS("int"),
        FORCE_DISK_WRITE_COMPLETION("boolean"),
        ASTEROIDS_ENABLE_DYNAMIC_PHYSICS("boolean"),
        ENABLE_BREAK_OFF("boolean"),
        COLLISION_DAMAGE("boolean"),
        SKIN_ALLOW_UPLOAD("boolean"),
        CATALOG_NAME_COLLISION_HANDLING("boolean"),
        SECTOR_AUTOSAVE_SEC("int"),
        PHYSICS_SLOWDOWN_THRESHOLD("int"),
        THRUST_SPEED_LIMIT("int"),
        MAX_CLIENTS("int"),
        SUPER_ADMIN_PASSWORD_USE("boolean"),
        SUPER_ADMIN_PASSWORD("string"),
        SERVER_LISTEN_IP("string"),
        SOCKET_BUFFER_SIZE("int"),
        PHYSICS_LINEAR_DAMPING("double"),
        PHYSICS_ROTATIONAL_DAMPING("double"),
        AI_DESTRUCTION_LOOT_COUNT_MULTIPLIER("double"),
        AI_DESTRUCTION_LOOT_STACK_MULTIPLIER("double"),
        CHEST_LOOT_COUNT_MULTIPLIER("double"),
        CHEST_LOOT_STACK_MULTIPLIER("double"),
        USE_WHITELIST("boolean"),
        FILTER_CONNECTION_MESSAGES("boolean"),
        USE_UDP("boolean"),
        AUTO_KICK_MODIFIED_BLUEPRINT_USE("boolean"),
        AUTO_BAN_ID_MODIFIED_BLUEPRINT_USE("boolean"),
        AUTO_BAN_IP_MODIFIED_BLUEPRINT_USE("boolean"),
        REMOVE_MODIFIED_BLUEPRINTS("boolean"),
        DEBUG_SEGMENT_WRITING("boolean"),
        TCP_NODELAY("boolean"),
        PING_FLUSH("boolean"),
        RECIPE_BLOCK_COST("int"),
        SHOP_SPAWNING_PROBABILITY("double"),
        RECIPE_REFUND_MULT("double"),
        RECIPE_LEVEL_AMOUNT("int"),
        DEFAULT_SPAWN_SECTOR_X("int"),
        DEFAULT_SPAWN_SECTOR_Y("int"),
        DEFAULT_SPAWN_SECTOR_Z("int"),
        MODIFIED_BLUEPRINT_TOLERANCE("double"),
        TURNING_DIMENSION_SCALE("double"),
        DEFAULT_SPAWN_LOCALPOINT_X_1("double"),
        DEFAULT_SPAWN_LOCALPOINT_Y_1("double"),
        DEFAULT_SPAWN_LOCALPOINT_Z_1("double"),
        DEFAULT_SPAWN_LOCALPOINT_X_2("double"),
        DEFAULT_SPAWN_LOCALPOINT_Y_2("double"),
        DEFAULT_SPAWN_LOCALPOINT_Z_2("double"),
        DEFAULT_SPAWN_LOCALPOINT_X_3("double"),
        DEFAULT_SPAWN_LOCALPOINT_Y_3("double"),
        DEFAULT_SPAWN_LOCALPOINT_Z_3("double"),
        DEFAULT_SPAWN_LOCALPOINT_X_4("double"),
        DEFAULT_SPAWN_LOCALPOINT_Y_4("double"),
        DEFAULT_SPAWN_LOCALPOINT_Z_4("double"),
        PLAYER_DEATH_CREDIT_PUNISHMENT("double"),
        PLAYER_DEATH_CREDIT_DROP("boolean"),
        PLAYER_DEATH_BLOCK_PUNISHMENT("boolean"),
        PLAYER_DEATH_PUNISHMENT_TIME("int"),
        PLAYER_DEATH_INVULNERABILITY_TIME("int"),
        PLAYER_HISTORY_BACKLOG("int"),
        PROJECTILES_ADDITIVE_VELOCITY("boolean"),
        PROJECTILES_VELOCITY_MULTIPLIER("double"),
        IGNORE_DOCKING_AREA("boolean"),
        ALLOW_UPLOAD_FROM_LOCAL_BLUEPRINTS("boolean"),
        SHOP_NPC_STARTING_CREDITS("int"),
        SHOP_NPC_RECHARGE_CREDITS("int"),
        AI_WEAPON_AIMING_ACCURACY("int"),
        BROADCAST_SHIELD_PERCENTAGE("int"),
        BROADCAST_POWER_PERCENTAGE("int"),
        BLUEPRINT_BUY_WITH_BLOCKS("boolean"),
        SQL_NIO_FILE_SIZE("int");

        private String type; // there must be a better way to do this

        private ConfigItem(String type) {
            this.type = type;
        }

        private String getType() {
            return type;
        }

    }

}
