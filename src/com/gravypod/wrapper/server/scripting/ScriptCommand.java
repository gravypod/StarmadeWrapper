package com.gravypod.wrapper.server.scripting;

import java.util.Arrays;
import java.util.Stack;

import sleep.runtime.Scalar;
import sleep.runtime.ScriptInstance;
import sleep.runtime.SleepUtils;

import com.gravypod.starmadewrapper.User;
import com.gravypod.starmadewrapper.plugins.commands.Command;
import com.gravypod.starmadewrapper.plugins.events.EventHandler;
import com.gravypod.starmadewrapper.plugins.events.EventPriority;
import com.gravypod.starmadewrapper.plugins.events.Listener;
import com.gravypod.starmadewrapper.plugins.events.players.LoginEvent;
import com.gravypod.starmadewrapper.plugins.events.players.LogoutEvent;

public class ScriptCommand extends Command implements Listener {
	
	private final ScriptInstance script;
	
	public ScriptCommand(final ScriptInstance script) {
	
		this.script = script;
	}
	
	@Override
	public void run(String username, User user, String... args) {

		final Stack<Scalar> s = new Stack<Scalar>();
		s.addElement(SleepUtils.getArrayWrapper(Arrays.asList(args)));
		s.addElement(SleepUtils.getScalar(username));
		script.callFunction("&run", s);
	}
	
	@Override
	public String getHelp() {
	
		final Scalar scalar = script.callFunction("&getHelp", new Stack<Scalar>());
		if (scalar == null) {
			return super.getHelp();
		}
		final String value = scalar.stringValue();
		if (value == null || value.trim().isEmpty()) {
			return super.getHelp();
		}
		return value;
	}
	
	@Override
	public void init() {
	
		script.callFunction("&init", new Stack<Scalar>());
	}
	
	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onLogin(LoginEvent event) {
	
		final Stack<Scalar> args = new Stack<Scalar>();
		args.addElement(SleepUtils.getScalar(event.getUsername()));
		script.callFunction("&onLogin", args);
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onLogout(LogoutEvent event) {
	
		final Stack<Scalar> args = new Stack<Scalar>();
		args.addElement(SleepUtils.getScalar(event.getUsername()));
		script.callFunction("&onLogout", args);
	}
	
}
