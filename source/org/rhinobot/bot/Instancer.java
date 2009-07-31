/**
 * RhinoBot
 * Modular IRC Bot
 * By Christopher Allan <chris@geartech.org>
 *
 * If you like this software, please e-mail me with suggestions/comments/etc..
 *
 * Copyright (c) 2009, Christopher Allan
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the product nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY Christopher Allan ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL Christopher Allan BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.rhinobot.bot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.kernel.ConfigReader;
import org.kernel.Logger;
import org.kernel.ConfigReader.UnableToParseException;
import org.kernel.Logger.LogLevel;
import org.rhinobot.module.IrcModuleController;
import org.rhinobot.rhino.Rhino;

public final class Instancer
{
	private static final Logger					logger			= new Logger("logs", "Instancer");
	private final HashMap<Integer, RhinoBot>	bots			= new HashMap<Integer, RhinoBot>();
	private static Instancer 					instancer;
	private boolean								reloadingConfig = false;
	
	public static final Instancer getInstance ()
	{
		if (instancer == null)
		{
			instancer = new Instancer();
		}
		return instancer;
	}
	
	/**
	 * @see java.lang.Object#clone()
	 */
	public final Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}
	
	
	private Instancer ()
	{
		
	}

	/**
	 * Instancer, for multiple IRC bots.
	 *  
	 * @throws BotException
	 */
	final void init () throws BotException
	{
		/*
		 * Startup the manager at the end to let some load down on resizing arrays
		 */
		ArrayList<String> channelsToJoin = new ArrayList<String>();
		String charset = "ISO-8859-1",
			   wantedNick,
			   ident,
			   wantedServer,
			   wantedSocket,
			   password;
		
		Rhino rhino = Rhino.getInstance();
		
		boolean startup     = true,
				enableRhino = true;
		
		int port;
		
		ConfigReader reader;
		
		try
		{
			reader = new ConfigReader("rhinobot.conf");
		}
		catch (UnableToParseException e)
		{
			throw new BotException("Unable to parse config: " + e.getMessage());
		}
		catch (IOException e)
		{
			throw new BotException("IOException attempting to read config: " + e.getMessage());
		}
		
		if (!reader.blockExists("rhinobot"))
		{
			logger.write(LogLevel.MAJOR, "Config block 'rhinobot' does not exist.");
			throw new BotException("Config block 'rhinobot' does not exist.");
		}
		
		if (!reader.listExists("rhinobot>networks"))
		{
			logger.write(LogLevel.MAJOR, "Config list 'networks' does not exist.");
			throw new BotException("Config list 'networks' does not exist.");
		}
		
		if (!reader.listExists("rhinobot>modules"))
		{
			logger.write(LogLevel.INFO, "Config list 'modules' does not exist, not loading any modules");
		}
		else
		{
			final ArrayList<String> modules  = reader.getList("rhinobot>modules");
			
			logger.write(LogLevel.VERBOSE, "Loading IRC Modules...");
			IrcModuleController.loadModules(modules);
			logger.write(LogLevel.VERBOSE, "Loaded IRC Modules!");
		}
		
		if (reader.settingExists("rhinobot", "charset"))
		{
			charset = reader.getSetting("rhinobot", "charset");
		}
		
		if ( (!Rhino.isEnabled()) || (rhino == null) )
		{
			enableRhino = false;
		}
		
		final ArrayList<String> networks = reader.getList("rhinobot>networks");
		
		if (networks.size() == 0)
		{
			throw new BotException("There are no networks for auto-connection");
		}
		
		for (String network : networks)
		{
			if (reader.blockExists("rhinobot>" + network))
			{
				String blockName = "rhinobot>" + network;
				
				if ((!reader.settingExists(blockName, "nickname")) || (!reader.settingExists(blockName, "ident")) ||
					(!reader.settingExists(blockName, "server")) || (!reader.settingExists(blockName, "port")) ||
					(!reader.listExists(blockName + ">channels")) || (!reader.settingExists(blockName, "socket")) ||
					(!reader.settingExists(blockName, "startup")))
				{
					throw new BotException("Network block '" + network + "' is missing one of the required fields or lists: " +
							"nickname, ident, server, port, socket, and/or channels.");
				}
				
				wantedNick     = reader.getSetting(blockName, "nickname");
				ident          = reader.getSetting(blockName, "ident");
				wantedServer   = reader.getSetting(blockName, "server");
				port		   = reader.getSettingAsInteger(blockName, "port");
				wantedSocket   = reader.getSetting(blockName, "socket");
				startup		   = reader.getSettingAsBoolean(blockName, "startup");
				channelsToJoin = reader.getList(blockName + ">channels");
				
				if (reader.settingExists(blockName, "charset"))
				{
					charset = reader.getSetting(blockName, "charset");
				}
				
				if (reader.settingExists(blockName, "password"))
				{
					password = reader.getSetting(blockName, "password");
				}
				else
				{
					password = null;
				}
				
				RhinoBot bot = new RhinoBot(charset, network, wantedNick, ident, wantedServer,
						port, wantedSocket, startup, channelsToJoin, password, enableRhino, bots.size(), this);
				
//				bot.start();
				
				bots.put(bots.size(), bot);
			}
		}
		
		final RhinoBot[] botx = bots.values().toArray(new RhinoBot[bots.size()]);
		
		for (RhinoBot bot : botx)
		{
			bot.start();
		}
	}
	
	/**
	 * Reloads the config
	 * @throws BotException
	 */
	public final void reloadConfig () //throws BotException
	{
		reloadingConfig = true;
		quitAll("Reloading Instancer Config");
		
		/*
		 * Startup the manager at the end to let some load down on resizing arrays
		 */
		ArrayList<String> channelsToJoin = new ArrayList<String>();
		String charset = "ISO-8859-1",
			   wantedNick,
			   ident,
			   wantedServer,
			   wantedSocket,
			   password;
		
		final Rhino rhino = Rhino.getInstance();
		
		boolean startup     = true,
				enableRhino = true;
		
		int port;
		
		ConfigReader reader;
		
		try
		{
			reader = new ConfigReader("rhinobot.conf");
		}
		catch (UnableToParseException e)
		{
//			throw new BotException("Unable to parse config: " + e.getMessage());
			return;
		}
		catch (IOException e)
		{
//			throw new BotException("IOException attempting to read config: " + e.getMessage());
			return;
		}
		
		if (!reader.blockExists("rhinobot"))
		{
			logger.write(LogLevel.MAJOR, "Config block 'rhinobot' does not exist.");
//			throw new BotException("Config block 'rhinobot' does not exist.");
		}
		
		if (!reader.listExists("rhinobot>networks"))
		{
			logger.write(LogLevel.MAJOR, "Config list 'networks' does not exist.");
//			throw new BotException("Config list 'networks' does not exist.");
			return;
		}
		
		if (!reader.listExists("rhinobot>modules"))
		{
			logger.write(LogLevel.INFO, "Config list 'modules' does not exist, not loading any modules");
		}
		else
		{
			final ArrayList<String> modules  = reader.getList("rhinobot>modules");
			
			logger.write(LogLevel.VERBOSE, "Loading IRC Modules...");
			IrcModuleController.loadModules(modules);
			logger.write(LogLevel.VERBOSE, "Loaded IRC Modules!");
		}
		
		if (reader.settingExists("rhinobot", "charset"))
		{
			charset = reader.getSetting("rhinobot", "charset");
		}
		
		if ( (!Rhino.isEnabled()) || (rhino == null) )
		{
			enableRhino = false;
		}
		
		final ArrayList<String> networks = reader.getList("rhinobot>networks");
		
		if (networks.size() == 0)
		{
//			throw new BotException("There are no networks for auto-connection");
			return;
		}
		
		for (String network : networks)
		{
			if (reader.blockExists("rhinobot>" + network))
			{
				String blockName = "rhinobot>" + network;
				
				if ((!reader.settingExists(blockName, "nickname")) || (!reader.settingExists(blockName, "ident")) ||
					(!reader.settingExists(blockName, "server")) || (!reader.settingExists(blockName, "port")) ||
					(!reader.listExists(blockName + ">channels")) || (!reader.settingExists(blockName, "socket")) ||
					(!reader.settingExists(blockName, "startup")))
				{
//					throw new BotException("Network block '" + network + "' is missing one of the required fields or lists: " +
//							"nickname, ident, server, port, socket, and/or channels.");
					return;
				}
				
				wantedNick     = reader.getSetting(blockName, "nickname");
				ident          = reader.getSetting(blockName, "ident");
				wantedServer   = reader.getSetting(blockName, "server");
				port		   = reader.getSettingAsInteger(blockName, "port");
				wantedSocket   = reader.getSetting(blockName, "socket");
				startup		   = reader.getSettingAsBoolean(blockName, "startup");
				channelsToJoin = reader.getList(blockName + ">channels");
				
				if (reader.settingExists(blockName, "charset"))
				{
					charset = reader.getSetting(blockName, "charset");
				}
				
				if (reader.settingExists(blockName, "password"))
				{
					password = reader.getSetting(blockName, "password");
				}
				else
				{
					password = null;
				}
				
				RhinoBot bot = new RhinoBot(charset, network, wantedNick, ident, wantedServer,
					port, wantedSocket, startup, channelsToJoin, password, enableRhino, bots.size(), this);

				bot.start();
				
				bots.put(bots.size(), bot);
			}
		}
	}
	
	/**
	 * Reloads the config.
	 * 
	 * @throws BotException
	 */
	public final void reloadRhinoBotConfig () throws BotException
	{
		for (RhinoBot bot: bots.values())
		{
			bot.reloadConfig();
		}
	}
	
	/**
	 * Retusn the amount of bots loaded in
	 * @return
	 */
	final int botCount ()
	{
		return bots.size();
	}
	
	/**
	 * @param instanceNumber
	 */
	final void removeThisBot (int instanceNumber)
	{
		bots.remove(instanceNumber);
		
		if (bots.size() == 0)
		{
			if (reloadingConfig)
			{
				reloadingConfig = false;
			}
			else
			{
				BotModule.quitModule();
			}
		}
	}

	/**
	 * @param reason
	 */
	public final void quitAll (String reason)
	{
		for (RhinoBot bot : bots.values())
		{
			bot.quit(reason);
		}
	}
}
