StarmadeWrapper is a wrapper utility for the starmade server. It adds command, and scripting, functionality to your server.

Configure:
	1. Refer to "How to start"
	2. Stop server (type "stop" in console and click ender)
	3. Open "config.yml"
	4. Change settings accordingly

Commands:
	1. !addwarp (faction name or "all" for all users) (x) (y) (z) (x) (y) (z): Add a warp that links to sectors together (Admin Only)
	2. !claim: Claim rewards for a user after they have voted for on http://starmade-servers.com
	3. !delwarp (x) (y) (z): Delete all warps that connect to (x) (y) (z)
	4. !setfactionowner (user) (faction name): Set the owner of a faction to allow them to permit users to use their warp-gates
	5. !help: Get help info for a user
	6. !location: Get the location as last reported to StarmadeWrapper
	7. !permit (user): Permit (user) to use any warp-gates owned by your faction
	8. !reload: Reload all of the commands, and scripts, registered to the server. (Admin Only)
	9. !stuck: Teleports the sender to spawn
	10: !tp (x) (y) (z): Sends the command sender to sector (x) (y) (z)
	11: !warp: Warps the sender to the other end of a warp sector (Only works in a sector added as a warp)

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
	
How to use scripts:
	1. Learn how to use the scripting API
		A. Learn how to use Sleep from http://sleep.dashnine.org/manual/
		B. Download and read all of the information within this script: http://gravypod.com/smw/test.sl
	2. Create a file in the scripts directory named the same thing you would like the command to be and an extension of ".sl" (e.x. If you would like to create a ("!test" command, create a file called "test.sl")
	3. Restart the server, or run the "!reload" command in game, so that the wrapper loads your newly created script
	
How to get in touch:
	1. Connect to irc.esper.net and join #gravypod
	2. Email me at gravypod@gravypod.com
	
Good to know info:
	1. Yes, I do like feature requests
	2. Yes, I can work with you if you are a hosting company to get this working with your setup
	3. No, I am not breaking the TOS of starmade
	
Requirements:
	1. Java 7
	
Web CP:
	
	The Web CP is a built in control panel for the StarmadeWrapper. 
	It currently only shows in-game chat, allows you to stop, restart, start, and kill the server, and tells you if the server is online. The panel will later allow configuration of the StarmadeWrapper.
	I am also looking into the possibility of creating a starmade config editor.
	
	Notes:
		1. The panel by default is on
		2. The default port is 453
		3. The password is randomly generated
	
Libraries used & packaged within the jar:
	1. Sleep ( http://sleep.dashnine.org/ )
	2. YamlBeans ( https://github.com/EsotericSoftware/yamlbeans/ )
	3. Json-Simple ( https://code.google.com/p/json-simple/ )
	