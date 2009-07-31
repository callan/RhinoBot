function reloadEngine (params, nick, bot)
{
	bot.notice(nick, "Reload Engine (v1.00.00.45)");

	if (params[1] == "scripts")
	{
		var rhino = new Rhino();
		rhino.reloadScripts();
		bot.notice(nick, "Reloaded scripts!");
	}
	else if (params[1] == "script")
	{
		if ( (params.length < 3) || (params[2] == null) )
		{
			bot.notice(nick, "Requires more parameters");
			return;
		}
		
		bot.notice(nick, "Coming Soon");
	}
	else if (params[1] == "config")
	{
		try
		{
			var restart = bot.reloadConfig();
		}
		catch (e)
		{
			bot.notice(nick, "Unable to reload config: " + e);
			return;
		}
		
		if (restart)
		{
			bot.notice(nick, "RhinoBot will need to restart to utilize all changes");
		}
		else
		{
			bot.notice(nick, "All changes successfully applied");
		}
		
		bot.notice(nick, "Reloaded Config");
	}
	else if (params[1] == "rhino-config")
	{
		var rhino = new Rhino();
		
		rhino.reloadConfig();
		
		bot.notice(nick, "Reloaded Config");
	}
	else if (params[1] == "instancer-config")
	{
		var instance = new Instancer();
		
		bot.notice(nick, "Reloading config... (This will restart the bot)");
		
		try
		{
			instance.reloadConfig();
		}
		catch (e)
		{
			bot.notice(nick, "Failed to reload config because: " + e);
		}
	}
	else
	{
		if (params[1] != undefined)
		{
			bot.notice(nick, "Unknown Parameter " + params[1]);
		}
		else
		{
			bot.notice(nick, "Not enough parameters supplied");
		}
	}
}