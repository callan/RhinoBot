var globalBot, globalChannel;

// include basicInformation.js
// include debugInfo.js
// include SwingApplication.js
// include XMLHttpRequest.js
// include ReloadEngine.js

function noPermission (bot, nick)
{
	bot.notice(nick, "You do not have the permission to use this command");
}

function getChannelPermission (channel, bot)
{
	var chn = bot.manager.getChannel(channel);
	
	if (chn == null) return 0;
	
	channel.fetchPermission(network);
	cpermission = channel.getPermission();
}

function onMessage (message, channel, nick, ident, hostmask, permission, bot)
{
	globalChannel	= channel;
	globalBot		= bot;
	
	var fragments = message.toLowerCase().split(" ");
	var length    = fragments.length;
	var prefix    = (message.length > 1) ? fragments[0].substr(1) : "";
	
	if (prefix == "")
		return;
	
	if (prefix == "version")
	{
		bot.notice(nick, "Version " + bot.getVersion());
	}
	else if (prefix == "swing")
	{
		if (permission == 100)
		{
			initSwing(nick, bot);
		}
		else
		{
			bot.notice(nick, "Only the Owner can init a swing application");
		}
	}
	else if (prefix == "cpermission")
	{
		// Refetch the permissions
		var channel = bot.manager.getChannel(channel);
		
		if (channel == null) return;	
		
		channel.fetchPermission(network);
		
		permission = channel.getPermission();
		
		bot.notice(nick, "Channel permission: " + permission);
	}
	else if (prefix == "permission")
	{
		// Refetch the permissions :D!
		var user = bot.manager.getUser(nick);
		
		if (user == null)
		{
			bot.notice(nick, "RhinoBot was unable to find you!");
		}
		
		if (user.canGetPermission())
		{
			user.fetchPermission();
		}
		
		permission = user.getPermission();
		
		bot.notice(nick, "Permission: " + permission);
		if (permission == 100)
		{
			bot.notice(nick, "Owner level granted");
		}
	}
	else if (prefix == "reload")
	{
		if (permission >= 90)
		{
			reloadEngine(fragments, nick, bot);
		}
		else
		{
			noPermission(bot, nick);
		}
	}
	else if ((prefix == "reconnect") && (permission >= 90))
	{
		bot.reconnect();
	}
	else if ((prefix == "quit") && (permission >= 90))
	{
		bot.quit("Shutdown initiated by " + nick);
	}
	else if ((prefix == "join") && (length > 1))
	{
		if (permission >= 70)
		{
			if (length > 2)
				bot.join(fragments[1], fragments[2]);
			else
				bot.join(fragments[1]);
		}
		else
		{
			noPermission(bot, nick);
		}
	}
	else if ((prefix == "part") && (length > 1))
	{
		if (permission >= 70)
		{
			bot.part(fragments[1], "Part command issued by " + nick);
		}
		else
		{
			noPermission(bot, nick);
		}
	}
	else if ((prefix == "switch") && (length > 2))
	{
		if (permission >= 70)
		{
			bot.part(fragments[1], "Switching to " + fragments[2]);
			bot.join(fragments[2]);
		}
		else
		{
			noPermission(bot, nick);
		}
	}
	else if ((prefix == "manager") && (length > 1))
	{
		if (permission >= 90)
		{
			if ((fragments[1] == "getuser") && (length > 3))
			{
				var user = bot.getManager().getUser(fragments[2]);
				
				if (user == null)
				{
					bot.notice(nick, "User not found");
					return;
				}
				
				if (fragments[3] == "channels")
				{
					var channels = user.getChannels();
					
					if (channels.length > 0)
					{
						bot.notice(nick, "Channels:");
						for (var i = 0; i < channels.length; i++)
						{
							bot.notice(nick, channels[i]);
						}
					}
					else
					{
						bot.notice(nick, "User is in no channels");
					}
				}
			}
		}
		else
		{
			noPermission(bot, nick);
		}
	}
	else if (prefix == "umode")
	{
		if (permission >= 70)
		{
			bot.mode(botNick, fragments[1]);
		}
		else
		{
			noPermission(bot, nick);
		}
	}
	else if (prefix == "mode")
	{
		if (permission >= 50)
		{
			cpermission = getChannelPermission(channel, bot);
			
			if (cpermission >= 30)
			{
				bot.mode(channel, fragments[1]);
			}
		}
		else
		{
			noPermission(bot, nick);
		}
	}
	else if (prefix == "say")
	{
		if (permission >= 10)
		{
			bot.privmsg(channel, message.substring(message.indexOf(prefix) + prefix.length + 1));
		}
		else
		{
			noPermission(bot, nick);
		}
	}
	else if (prefix == "whoami")
	{
		bot.notice(nick, "You are: " + nick);
	}
	else if ((prefix == "test-rhino") && (permission >= 80))
	{
		bot.privmsg(channel, "Testing RhinoModuleController");
		
		var worked = true;
		
		try
		{
			var test = new TestRhinoModule2();
		}
		catch (e)
		{
			worked = false;
			bot.privmsg(channel, "Error: " + e);
		}
		
		if (worked)
			bot.privmsg(channel, "Seems to have worked: " + test);
		else
			bot.privmsg(channel, "Failed miserably");
	}
	else if ((prefix == "test-rhino-unload") && (permission >= 80))
	{
		bot.privmsg(channel, "Testing RhinoModuleController");
		
		var rhino = new Rhino();
		
		try
		{
			rhino.unloadModule("TestRhinoModule2");
		}
		catch (e)
		{
			bot.privmsg(channel, "Error" + e);
		}
		finally
		{
			bot.privmsg(channel, "Seems to have worked");
		}
	}
	else if ((prefix == "test-rhino-load") && (permission >= 80))
	{
		bot.privmsg(channel, "Testing RhinoModuleController");
		
		var rhino = new Rhino();
		
		try
		{
			rhino.loadModule("TestRhinoModule2");
		}
		catch (e)
		{
			bot.privmsg(channel, "Error loading module: " + e);
		}
		finally
		{
			bot.privmsg(channel, "Seems to have worked");
		}
	}
	else if (prefix == "debug-info")
	{
		if (permission >= 90)
		{
			debugInfo(bot);
		}
		else
		{
			noPermission(bot, nick);
		}
	}
	else if (prefix == "9ball")
	{
		var db = new Database();
		
		
		var responses = [
			"I don't think you want to know.",
			"Yes",
			"No",
			"What? I was just staring at serbz' faggotry",
			"wat",
			"It'll never happen",
			"If you think so"
		];
		
		var rand = Math.round(Math.random() * 100) % responses.length;
		
		bot.privmsg(channel, nick + ": " + responses[rand]);
		
		/*
		if (db.getVar('8ball-'+nick, 'message') != null)
		{
			var level = parseInt(db.getVar('8ball-'+nick, 'message'));
			
			if (++level == 5)
			{
				db.delVar('8ball-'+nick, 'message');
			}
			else
			{
				db.setVar('8ball-'+nick, 'message', ++level);
			}
			
			bot.privmsg(channel, nick + ": " + responses[level]);
		}
		else
		{
			db.setVar('8ball-'+nick, 'message', '0');
			
			bot.privmsg(channel, nick + ": " + responses[0]);
		}
		*/
	}
	else if (prefix == "tumblr")
	{
		reqobj = new XMLHttpRequest();
		
		reqobj.open('GET', "http://" + fragments[1] + ".tumblr.com/api/read", true);
		reqobj.onreadystatechange = function ()
		{
			if (reqobj.readyState == REQUEST_COMPLETE)
			{
//				var db = new Database();
//				db.delLongVar('TUMBLR', 'message');
//				db.setLongVar('TUMBLR', 'message', new STring(reqobj.responseText));
				
				bot.notice(nick, "YATTAH!");
				
				var txt = new String(reqobj.responseText);
				var xml = new XML(txt);
				
				bot.notice(nick, 'DEBUG: ' + xml.toString());
			}
		};
		-1
		reqobj.onerror = function (instance)
		{
			java.lang.System.out.println(instance);
		};
		
		reqobj.send(' ');
	}
	else if (prefix == "req")
	{
		if (permission >= 10)
		{
			if (length > 1)
			{
				try
				{
					initXmlHttpRequest(fragments[1]);
				}
				catch (e)
				{
					bot.privmsg(channel, "Problem: " + e);
				}
			}
			else
			{
				
			}
		}
		else
		{
			noPermission(nick, bot);
		}
	}
	else if (prefix == "view-req")
	{
		if (permission >= 10)
		{
			var db = new Database();
			var txt = db.getLongVar('XMLHttpRequest', 'message').split("\r\n");
			
			for (var i = 0; ((i < txt.length) && (i < 5)); i++)
			{
				bot.privmsg(channel, txt[i]);
			}
		}
		else
		{
			noPermission(nick, bot);
		}
	}
	else if (prefix == "test-nfile")
	{
	
		function onNFileError ()
		{
			bot.privmsg(channel, "NFile error!");
		}
		
		function onNFileRead ()
		{
			bot.privmsg(channel, "Huh? Reading? ");
		}
	
		if (permission < 80)
		{
			noPermission(nick, bot);
			return;
		}
		
		var nFile = new NFile("test.txt");
		
		nFile.onerror = onNFileError;
		nFile.onread  = onNFileRead;
		
		try { nFile.open('a'); }
		catch (e) { bot.privmsg(channel, "Error with 'nFile.open('w');': " + e); }
		try { nFile.write("Test!\r\n"); }
		catch (e) { bot.privmsg(channel, "Error with 'nFile.write(\"Test!\");': " + e); }
		try { nFile.close(); }
		catch (e) { bot.privmsg(channel, "Error with 'nFile.close();': " + e); }
		
		nFile = null;
	}
	else if (prefix == "rhino")
	{
		if (permission >= 90)
		{
			var rhino = new Rhino();
			
			if (length == 1)
			{
				bot.notice(nick, "Requires more parameters");
			}
			else
			{
				if ((fragments[1] == "load-module") && (length > 2))
				{
					var fragments2 = message.split(" ");
					
					try
					{
						if (rhino.loadModule(fragments2[2]))
						{
							bot.notice(nick, "Module " + fragments2[2] + " Sucessfully loaded");
							
							var module = eval("new " + fragments2[2] + "();");
						}
						else
						{
							bot.notice(nick, "Rhino was unable to load module " + fragments2[2] + ".");
						}
					}
					catch (e)
					{
						bot.notice(nick, "Error while loading module " + fragments2[2] + ": " + e);
					}
				}
				else if ((fragments[1] == "unload-module") && (length > 2))
				{
					var fragments2 = message.split(" ");
					
					try
					{
						rhino.unloadModule(fragments[2]);
					}
					catch (e)
					{
						bot.notice(nick, "Error attempting to unload module: " + e);
					}
					
					bot.notice("Attempted to unload Module");
				}
			}
		}
		else
		{
			noPermission(bot, nick);
		}
	}
}