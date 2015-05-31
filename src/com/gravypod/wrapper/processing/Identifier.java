package com.gravypod.wrapper.processing;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public enum Identifier {

	PLAYER_KILL_PLAYER("Announcing kill: PlayerCharacter", "playerKillPlayer"),
	SHIP_KILL_PLAYER("] Announcing kill: Ship", "shipKillPlayer"),
	SHIP_CHANGE("[CONTROLLER][ADD-UNIT] (Server(0)): PlS[", "shipChange"),
	LOGIN("[SERVER][LOGIN] login received. returning login info for RegisteredClient: ", "login"),
	LOGOUT("[SERVER][TAG] LOGOUT SPAWNING POINT OF PlS[", "logout"),
	MOVEMENT("Doing Sector Change for PlayerCharacter", "move"),
	FULLY_STARTED("[SERVER][UNIVERSE] LOADING SECTOR... (2, 2, 2)", "fullyStarted"),
	WHISPER("[SERVER][CHAT][WISPER] ", "whisper"),
	SHOP_BUY("[SERVER] Executing Shopping Buy: ", "shopBuy");

	private final String pattern; // TODO: Make RegEx (with grouping) instead of String contains()
	private final Method callback;

	private Identifier(String pattern, String callback) {
		this.pattern = pattern;
		this.callback = getCallback(callback);
	}

	public String getPattern() {
		return pattern;
	}

	public void invoke(MessageProcessor processor, String line) throws InvocationTargetException, IllegalAccessException {
		callback.invoke(processor, line);
	}

	public static Identifier findMatch(String line) {
		for (Identifier identifier : Identifier.values()) {
			if (line.contains(identifier.pattern))
				return identifier;
		}
		return null;
	}

	private Method getCallback(String name) {
		try {
			return MessageProcessor.class.getMethod(name, String.class);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
		return null;
	}

}
