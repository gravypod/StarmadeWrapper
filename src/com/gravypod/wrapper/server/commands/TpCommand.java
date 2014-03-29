package com.gravypod.wrapper.server.commands;

import com.gravypod.wrapper.LocationUtils;
import com.gravypod.wrapper.server.DonorCommand;
import com.gravypod.wrapper.server.User;

public class TpCommand extends DonorCommand {
	
	@Override
	public void runDonor(final String user, final String ... args) {
	
		switch (args.length) {
			case 4: {
				if (!getServer().getConfig().donorsTpOthers && !getServer().getFileInfo().getAdmins().contains(user)) {
					pm(user, "You are not allowed to tp others");
					return;
				}
				
				tp(args[0], args[1], args[2], args[3]);
				
				pm(user, "Attempting to tp " + args[0] + " to sector " + args[1] + ", " + args[2] + ", " + args[3]);
				
				break;
			}
			case 3: {
				tp(user, args[0].trim(), args[1].trim(), args[2].trim());
				pm(user, "You have been teleported to sector " + args[0] + ", " + args[1] + ", " + args[2]);
				break;
			}
			case 1: {
				if (args[0].trim().isEmpty()) {
					pm(user, "Incorrect number of arguments");
					pm(user, getHelp());
					return;
				}
				final User player = getServer().getUser(args[0]);
				tp(user, player.x, player.y, player.z);
				pm(user, "You have teleported to sector " + LocationUtils.locationToString(player.x, player.y, player.z));
				break;
			}
			default: {
				pm(user, "Incorrect number of arguments");
				pm(user, getHelp());
				break;
			}
		}
		
	}
	
	@Override
	public String getHelp() {
	
		return "!tp (user) (x y z): Teleport to a user, or to a sector, or teleport a user to a sector (Donors and Admins only)";
	}
	
}
