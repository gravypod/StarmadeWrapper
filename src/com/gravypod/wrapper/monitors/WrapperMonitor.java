package com.gravypod.wrapper.monitors;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import com.gravypod.starmadewrapper.plugins.events.EventHandler;
import com.gravypod.starmadewrapper.plugins.events.Listener;
import com.gravypod.starmadewrapper.plugins.events.players.ChatEvent;
import com.gravypod.starmadewrapper.plugins.events.players.LoginEvent;
import com.gravypod.starmadewrapper.plugins.events.players.LogoutEvent;
import com.gravypod.wrapper.FixedSizeArrayList;
import com.gravypod.wrapper.SSWPlugin;
import com.gravypod.wrapper.server.Server;

import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.NanoHTTPD.Response.Status;

public class WrapperMonitor extends NanoHTTPD {
	
	private final String PANEL_PAGE = getPage("panel");
	
	private final String LOGIN_PAGE = getPage("login");
	
	private final static String AUTH_KEY_NAME = "key";
	
	private final Server server;
	
	private final FixedSizeArrayList<String> chatMessages = new FixedSizeArrayList<String>(30);
	
	private final AtomicReference<String> currentChat = new AtomicReference<String>();
	
	private final String masterPassword;
	
	private final Map<String, Long> authKeys = new HashMap<String, Long>();
	
	public WrapperMonitor(int port, Server server, String masterPassword) {
	
		super(port);
		try {
			server.getPluginManager().getEventManager().registerEvents(SSWPlugin.PLUGIN, new Listener() {
				@EventHandler
				public void login(LoginEvent event) {

					updateLog("[LOGOUT] " + event.getUsername() + " has logged out");
				}
				@EventHandler
				public void logout(LogoutEvent event) {

					updateLog("[LOGIN] " + event.getUsername() + " has logged in");
				}

				@EventHandler
				public void logout(ChatEvent event) {

					updateLog("[CHAT] " + event.getUsername() + ": " + event.getMessage());
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.server = server;
		this.masterPassword = masterPassword;
	}
	
	@Override
	public Response serve(IHTTPSession session) {
	
		CookieHandler cookies = session.getCookies();
		
		String storedKey = cookies.read(AUTH_KEY_NAME);
		
		Map<String, String> params = session.getParms();
		if (!isValidAuth(storedKey)) {
			if (params.containsKey("login") && login(session)) {
				Response res = new Response(Status.REDIRECT, MIME_HTML, "<html><body>Redirected: <a href=\"/\">Panel</a></body></html>");
				res.addHeader("Location", "/");
				return res;
			} else {
				return showLogin(session);
			}
		}
		
		if (session.getUri().endsWith("command") && params.containsKey("action")) {
			return command(params.get("action"), params.get("command"));
		}
		
		return showPanel();
	}
	
	private Response command(String action, String command) {
	
		switch (action) {
			case "command":
				server.exec(command);
				break;
			case "restart":
				server.getRunning().set(true);
				server.exec("/shutdown 60");
				break;
			case "stop":
				server.getRunning().set(false);
				server.exec("/shutdown 0");
				break;
			case "start":
				if (!server.getRunning().get()) {
					server.getRunning().set(true);
				} else {
					return new Response(Status.OK, "text/plain", "Server was already online.");
				}
				return new Response(Status.OK, "text/plain", "Server was started.");
			case "status":
				return new Response(Status.OK, "text/plain", "Server is " + (server.getRunning().get() ? "online" : "offline"));
			case "log":
				return new Response(Status.OK, "text/plain", currentChat.get());
			default:
				return new Response(Status.OK, "text/plain", "Uknown command");
		}
		
		return new Response(Status.OK, "text/plain", "Uknown command");
	}
	
	private Response showPanel() {
	
		return new Response(Status.OK, "text/html", PANEL_PAGE);
	}
	
	private Response showLogin(IHTTPSession session) {
	
		return new Response(Status.OK, "text/html", LOGIN_PAGE);
		
	}
	
	private boolean login(IHTTPSession session) {
	
		String enteredPassword = session.getParms().get("login");
		
		if (!enteredPassword.equals(masterPassword)) {
			return false;
		}
		
		String id = UUID.randomUUID().toString();
		
		authKeys.put(id, System.currentTimeMillis());
		
		session.getCookies().set(AUTH_KEY_NAME, id, 2);
		
		return true;
		
	}
	
	private boolean isValidAuth(String id) {
	
		return id != null && authKeys.containsKey(id);
	}
	
	private String getPage(String name) {
	
		InputStream in = getClass().getResourceAsStream("/html/" + name + ".html");
		StringBuilder sb = new StringBuilder();
		Scanner sc = new Scanner(in);
		
		while (sc.hasNextLine()) {
			sb.append(sc.nextLine() + "\n");
		}
		
		sc.close();
		
		return sb.toString();
	}
	
	public void updateLog(String message) {
	
		chatMessages.add(message);
		StringBuilder builder = new StringBuilder();
		for (String s : chatMessages) {
			builder.append(s + "\r\n");
		}
		
		currentChat.set(builder.toString());
	}
}
