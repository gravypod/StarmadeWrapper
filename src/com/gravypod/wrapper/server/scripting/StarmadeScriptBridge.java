package com.gravypod.wrapper.server.scripting;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.Stack;

import sleep.bridges.BridgeUtilities;
import sleep.interfaces.Function;
import sleep.interfaces.Loadable;
import sleep.runtime.Scalar;
import sleep.runtime.ScriptInstance;
import sleep.runtime.SleepUtils;

import com.gravypod.wrapper.server.Server;
import com.gravypod.wrapper.server.User;

public class StarmadeScriptBridge implements Loadable {
	
	private final Server s;
	
	public StarmadeScriptBridge(final Server s) {
	
		this.s = s;
	}
	
	@Override
	public void scriptUnloaded(final ScriptInstance script) {
	
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void scriptLoaded(final ScriptInstance script) {
	
		@SuppressWarnings("rawtypes")
		final Hashtable env = script.getScriptEnvironment().getEnvironment();
		
		env.put("&execute", new Function() {
			
			private static final long serialVersionUID = -8438090197928228871L;
			
			@SuppressWarnings("rawtypes")
			@Override
			public Scalar evaluate(final String name, final ScriptInstance script, final Stack args) {
			
				final String command = BridgeUtilities.getString(args, "");
				s.exec(command);
				return SleepUtils.getEmptyScalar();
			}
		});
		
		env.put("&pm", new Function() {
			
			private static final long serialVersionUID = -8438090197928228871L;
			
			@SuppressWarnings("rawtypes")
			@Override
			public Scalar evaluate(final String name, final ScriptInstance script, final Stack args) {
			
				final String username = BridgeUtilities.getString(args, "");
				final String message = BridgeUtilities.getString(args, "");
				s.pm(username, message);
				return SleepUtils.getEmptyScalar();
			}
		});
		env.put("&tp", new Function() {
			
			private static final long serialVersionUID = -4306465396927794506L;
			
			@SuppressWarnings("rawtypes")
			@Override
			public Scalar evaluate(final String name, final ScriptInstance script, final Stack args) {
			
				final String username = BridgeUtilities.getString(args, "");
				final int x = BridgeUtilities.getInt(args, 0);
				final int y = BridgeUtilities.getInt(args, 0);
				final int z = BridgeUtilities.getInt(args, 0);
				s.tp(username, x, y, z);
				return SleepUtils.getEmptyScalar();
			}
		});
		
		env.put("&give", new Function() {
			
			private static final long serialVersionUID = -1470700634961182714L;
			
			@SuppressWarnings("rawtypes")
			@Override
			public Scalar evaluate(final String name, final ScriptInstance script, final Stack args) {
			
				final String username = BridgeUtilities.getString(args, "");
				final int id = BridgeUtilities.getInt(args, 0);
				final int ammount = BridgeUtilities.getInt(args, 0);
				s.give(username, id, ammount);
				return SleepUtils.getEmptyScalar();
			}
		});
		
		env.put("&getLocation", new Function() {
			
			private static final long serialVersionUID = 4763526048121452303L;
			
			@SuppressWarnings("rawtypes")
			// Rawtypes are needed to implement this interface and maintain my
			// sanity
			@Override
			public Scalar evaluate(final String name, final ScriptInstance script, final Stack args) {
			
				final String username = BridgeUtilities.getString(args, "");
				
				final User u = s.getUser(username);
				
				final Map<String, Integer> m = new HashMap<String, Integer>();
				
				if (u != null) {
					m.put("x", u.x);
					m.put("y", u.y);
					m.put("z", u.z);
				}
				
				return SleepUtils.getHashWrapper(m);
				
			}
		});
	}
	
}
