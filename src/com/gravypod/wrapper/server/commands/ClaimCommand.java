package com.gravypod.wrapper.server.commands;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import sleep.error.YourCodeSucksException;
import sleep.runtime.Scalar;
import sleep.runtime.ScriptInstance;
import sleep.runtime.ScriptLoader;
import sleep.runtime.SleepUtils;

import com.gravypod.wrapper.server.Command;
import com.gravypod.wrapper.server.User;
import com.gravypod.wrapper.server.scripting.StarmadeScriptBridge;

public class ClaimCommand extends Command {
	
	private ExecutorService group;
	
	private String starmadeServersAPI = "";
	
	private ScriptLoader loader;
	
	private ScriptInstance script;
	
	@Override
	public void run(final String user, final String ... args) {
	
		group.execute(new Runnable() {
			
			@Override
			public void run() {
			
				try {
					
					final URL url = new URL(starmadeServersAPI);
					final Scanner sc = new Scanner(url.openStream());
					final StringBuilder builder = new StringBuilder();
					
					while (sc.hasNext()) {
						builder.append(sc.next());
					}
					sc.close();
					
					final String s = builder.toString();
					final Object data = JSONValue.parse(s);
					
					if (data == null || s == null) {
						pm(user, "There was an error checking your voting stats. Please notify an administrator and try again later.");
					} else if (getVoteFor(user, data)) {
						
						exec("/chat Thank you, " + user + ", for voting!");
						
						final User u = getServer().getUser(user);
						
						if (script == null) {
							int ammount = generateAmmount(u.lastVotes);
							if (args.length == 1 ? args[0].equalsIgnoreCase("money") : true) {
								ammount += 10000;
								exec("/give_credits " + user + " " + ammount);
								pm(user, "You have been sent " + ammount + " credits");
							} else {
								final List<String> blocks = getServer().getFileInfo().getBlocks();
								
								final String block = blocks.get((int) (blocks.size() * Math.random()) - 1);
								
								exec("/giveid " + user + " " + block.trim() + " " + ammount);
								
								pm(user, "You have been sent " + ammount + " of the block " + block);
							}
						} else {
							Stack<Scalar> aguments = new Stack<Scalar>();
							aguments.addElement(SleepUtils.getArrayWrapper(Arrays.asList(args)));
							aguments.addElement(SleepUtils.getScalar(u.lastVotes));
							aguments.addElement(SleepUtils.getScalar(user));
							script.callFunction("&claim", aguments);
						}
					} else {
						pm(user, "You must vote again to get another reward");
					}
					
				} catch (final Exception e) {
					e.printStackTrace();
					pm(user, "There was an error checking your voting stats. Please try again later.");
				}
				
			}
		});
		
	}
	
	public boolean getVoteFor(final String user, final Object data) {
	
		final JSONObject fullMap = (JSONObject) data;
		
		final JSONArray voterList = (JSONArray) fullMap.get("voters");
		
		for (int x = 0; x < voterList.size(); x++) {
			
			final JSONObject player = (JSONObject) voterList.get(x);
			
			final String name = (String) player.get("nickname");
			
			if (!user.equalsIgnoreCase(name)) {
				continue;
			}
			
			final User u = getServer().getUser(name);
			
			final int votes = Integer.parseInt((String) player.get("votes"));
			
			if (!(u.lastVotes == votes)) {
				u.lastVotes = votes;
				return true;
			}
			
			return false;
			
		}
		
		return false;
		
	}
	
	public int generateAmmount(final int votes) {
	
		final int min = 1;
		int max = 1000000;
		
		if (votes < 10) {
			max /= 2;
			
		}
		
		return (int) (min + Math.random() * max);
		
	}
	
	@Override
	public void init() {
	
		group = Executors.newCachedThreadPool();
		starmadeServersAPI = "http://starmade-servers.com/api/?object=servers&element=voters&key=" + getServer().getConfig().apiKey + "&month=current&format=json";
		loader = new ScriptLoader();
		loader.addGlobalBridge(new StarmadeScriptBridge(getServer()));
		
		File claimScript = new File(getServer().getDataFolder(), "claim.sl");
		
		if (claimScript.exists()) {
			try {
				script = loader.loadScript(claimScript);
				if (script != null) {
					script.runScript();
				}
			} catch (YourCodeSucksException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public String getHelp() {
	
		return "!claim (optional money or blocks): Claim a reward for voting on the starmade-serverlist. By default you will get money.";
	}
	
}
