StarmadeWrapper is a wrapper utility for the starmade server. It adds command, and scripting, functionality to your server.

Updating from 0.x.x:
	1. Remove the apiKey, donorsTpOthers, adminPanelPort, adminPanelPass, and usePanel settings from the config

Configure:
	1. Refer to "How to start"
	2. Stop server (type "stop" in console and click ender)
	3. Open "config.yml"
	4. Change settings accordingly

Commands:
	1. !help: Help for using the commands
	2. !location: Server pms user the location that is expected of the player
	2. !reload: Reload all loaded plugins

How to find more builds:
	1. Go to http://gravypod.com/smw/
	
How to start:
	1. Unzip this into a directory
	2. Run the script file for your OS
		A. On windows run "windows-start.bat"
		B. On linux run "linux-start.sh"
	3. Wait for the server to start
	4. Stop the server, edit the configs to your liking.
	5. Redo step number 2 to start the server again.
	
How to write plugins:
	1. Download the latest plugins and extract contents of SMW-Plugins
	2. Add StarmadeWrapper-API.jar to the libs of your plugin
	3. Use this as a guide https://github.com/gravypod/StarmadeWrapper-Warps
	
How to get in touch:
	1. Connect to irc.esper.net and join #gravypod
	2. Email me at gravypod@gravypod.com
	
Good to know info:
	1. Yes, I do like feature requests
	2. Yes, I can work with you if you are a hosting company to get this working with your setup
	3. No, I am not breaking the TOS of starmade
	
Requirements:
	1. Java 7
	
Libraries used & packaged within the jar:
	1. YamlBeans ( https://github.com/EsotericSoftware/yamlbeans/ )
	2. Json-Simple ( https://code.google.com/p/json-simple/ )
	