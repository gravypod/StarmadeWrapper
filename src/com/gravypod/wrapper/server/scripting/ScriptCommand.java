package com.gravypod.wrapper.server.scripting;

import java.util.Arrays;
import java.util.Stack;

import sleep.runtime.Scalar;
import sleep.runtime.ScriptInstance;
import sleep.runtime.SleepUtils;

import com.gravypod.wrapper.server.Command;

public class ScriptCommand extends Command {
	
	private final ScriptInstance script;
	
	public ScriptCommand(ScriptInstance script) {
	
		this.script = script;
	}
	
	@Override
	public void run(final String user, final String ... args) {
	
		final Stack<Scalar> s = new Stack<Scalar>();
		s.addElement(SleepUtils.getArrayWrapper(Arrays.asList(args)));
		s.addElement(SleepUtils.getScalar(user));
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
	
	@Override
	public void onLogin(final String username) {
	
		final Stack<Scalar> args = new Stack<Scalar>();
		args.addElement(SleepUtils.getScalar(username));
		script.callFunction("&onLogin", args);
	}
	
	@Override
	public void onLogout(final String username) {
	
		final Stack<Scalar> args = new Stack<Scalar>();
		args.addElement(SleepUtils.getScalar(username));
		script.callFunction("&onLogout", args);
	}
	
}
