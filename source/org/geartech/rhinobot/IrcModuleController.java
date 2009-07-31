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
package org.geartech.rhinobot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.geartech.rhinobot.legacy.IrcModule;
import org.geartech.rhinobot.manager.Mode;
import org.geartech.rhinobot.support.Logger;
import org.geartech.rhinobot.support.Logger.LogLevel;

/**
 * IrcModuleController is the module controller for IrcModule's in RhinoBot. This acts as a subclass
 * of ModuleController since it's primary focus is those modules which interface IrcModule.
 */
public final class IrcModuleController
{	
	/**
	 * The hashmap of modules, the index is the module names, the value is the actual module
	 */
	private static final HashMap<String, IrcModule> modules = new HashMap<String, IrcModule>();
	private static final Logger						logger	= new Logger("logs", "IrcModuleController");
	
	private IrcModuleController ()
	{
		
	}
	
	/**
	 * @see java.lang.Object#clone()
	 */
	public final Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}
	
	/**
	 * Reloads all BotIrcModules
	 */
	public static final synchronized void reloadAllModules ()
	{
		logger.write(LogLevel.INFO, "Reloading all IRC Modules");
		
		for (Iterator<String> it = modules.keySet().iterator(); it.hasNext(); )
		{
			String moduleName = it.next();
			
			reloadModule(moduleName);
		}
	}
	
	/**
	 * Attempts to reload a IrcModule
	 * @param moduleName
	 */
	public static final synchronized void reloadModule (String moduleName)
	{
		logger.write(LogLevel.INFO, "Reloading Module " + moduleName);
		
		if (modules.containsKey(moduleName))
		{
			modules.remove(moduleName);
			loadModule(moduleName);
		}
	}
	
	/**
	 * Loads the modules in the ArrayList if not already loaded.
	 * @param modules
	 */
	public static final synchronized void loadModules (ArrayList<String> modules)
	{
		logger.write(LogLevel.INFO, "Loading " + modules.size() + " modules...");
		
		for (String module : modules)
		{
			loadModule(module);
		}
	}
	
	/**
	 * Finds a IrcModule
	 * @param moduleName
	 * @return
	 */
	public static final synchronized void loadModule (String moduleName)
	{
		if (modules.containsKey(moduleName))
			return;
		
		logger.write(LogLevel.VERBOSE, "Loading Module " + moduleName);
		
		Class<IrcModule> classModule = null;
		
		try
		{
//			classModule = ModuleController.findClass(moduleName);
		}
		catch (Exception e)
		{
		}
		
		if (classModule == null)
		{
			logger.write(LogLevel.MINOR, "Unable to load module " + moduleName + ": module not found, or cannot be loaded");
			return;
		}
		
//		if (ModuleController.hasInterface(classModule, "org.rhinobot.bot.irc.BotIrcModule"))
		if (classModule != null)
		{
			IrcModule module = null;
			
			try
			{
				module = (IrcModule) classModule.newInstance();
				modules.put(moduleName, module);
			}
			catch (InstantiationException e)
			{
				logger.write(LogLevel.MINOR, "Unable to instantiate module " + moduleName + ": " + e.getMessage(), e);
			}
			catch (IllegalAccessException e)
			{
				logger.write(LogLevel.MINOR, "Unable to access a part of module " + moduleName + ": " + e.getMessage(), e);
			}
			
			logger.write(LogLevel.MINOR, "Module " + moduleName + " loaded!");
		}
	}
	
	/**
	 * @see org.geartech.rhinobot.legacy.IrcModule#onConnect()
	 */
	public static final synchronized void onConnect (RhinoBot bot)
	{
		for (IrcModule module : modules.values())
		{
			module.onConnect(bot);
		}
	}

	/**
	 * @see org.geartech.rhinobot.legacy.IrcModule#onDisconnect(RhinoBot)
	 */
	public static final synchronized void onDisconnect (RhinoBot bot)
	{
		for (IrcModule module : modules.values())
		{
			module.onDisconnect(bot);
		}
	}

	/**
	 * @see org.geartech.rhinobot.legacy.IrcModule#onQuit(String, String, String, String, RhinoBot)
	 */
	public static final synchronized void onQuit (String reason, String nick, String ident, String hostmask, RhinoBot bot)
	{
		for (IrcModule module : modules.values())
		{
			module.onQuit(reason, nick, ident, hostmask, bot);
		}
	}

	/**
	 * @see org.geartech.rhinobot.legacy.IrcModule#onJoin(String, String, String, String, RhinoBot)
	 */
	public static final synchronized void onJoin (String channel, String nick, String ident, String hostmask, RhinoBot bot)
	{
		for (IrcModule module : modules.values())
		{
			module.onJoin(channel, nick, ident, hostmask, bot);
		}
	}

	/**
	 * @see org.geartech.rhinobot.legacy.IrcModule#onPart(String, String, String, String, String, RhinoBot)
	 */
	public static final synchronized void onPart (String channel, String reason, String nick, String ident, String hostmask, RhinoBot bot)
	{
		for (IrcModule module : modules.values())
		{
			module.onPart(channel, reason, nick, ident, hostmask, bot);
		}
	}

	/**
	 * @see org.geartech.rhinobot.legacy.IrcModule#onKick(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public static final synchronized void onKick (String channel, String kicked, String reason, String nick, String ident, String hostmask, RhinoBot bot)
	{
		for (IrcModule module : modules.values())
		{
			module.onKick(channel, kicked, reason, nick, ident, hostmask, bot);
		}
	}

	/**
	 * @see org.geartech.rhinobot.legacy.IrcModule#onMode(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public static final synchronized void onMode (String channel, ArrayList<Mode> modes, String nick, String ident, String hostmask, RhinoBot bot)
	{
		for (IrcModule module : modules.values())
		{
			module.onMode(channel, modes, nick, ident, hostmask, bot);
		}
	}

	/**
	 * @see org.geartech.rhinobot.legacy.IrcModule#onInvite(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public static final synchronized void onInvite (String channel, String nick, String ident, String hostmask, RhinoBot bot)
	{
		for (IrcModule module : modules.values())
		{
			module.onInvite(channel, nick, ident, hostmask, bot);
		}
	}

	/**
	 * @see org.geartech.rhinobot.legacy.IrcModule#onNick(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public static final synchronized void onNick (String newNick, String nick, String ident, String hostmask, RhinoBot bot)
	{
		for (IrcModule module : modules.values())
		{
			module.onNick(newNick, nick, ident, hostmask, bot);
		}
	}

	/**
	 * @see org.geartech.rhinobot.legacy.IrcModule#onMessage(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public static final synchronized void onMessage (String message, String channel, String nick, String ident, String hostmask, RhinoBot bot)
	{
		for (IrcModule module : modules.values())
		{
			module.onMessage(message, channel, nick, ident, hostmask, bot);
		}
	}

	/**
	 * @see org.geartech.rhinobot.legacy.IrcModule#onAction(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public static final synchronized void onAction (String action, String channel, String nick, String ident, String hostmask, RhinoBot bot)
	{
		for (IrcModule module : modules.values())
		{
			module.onAction(action, channel, nick, ident, hostmask, bot);
		}
	}

	/**
	 * @see org.geartech.rhinobot.legacy.IrcModule#onNotice(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public static final synchronized void onNotice (String message, String channel, String nick, String ident, String hostmask, RhinoBot bot)
	{
		for (IrcModule module : modules.values())
		{
			module.onNotice(message, channel, nick, ident, hostmask, bot);
		}
	}

	/**
	 * @see org.geartech.rhinobot.legacy.IrcModule#onWallops(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public static final synchronized void onWallops (String message, String nick, String ident, String hostmask, RhinoBot bot)
	{
		for (IrcModule module : modules.values())
		{
			module.onWallops(message, nick, ident, hostmask, bot);
		}
	}

	/**
	 * @see org.geartech.rhinobot.legacy.IrcModule#onCTCP(String, String, String, String, String, RhinoBot)
	 */
	public static final synchronized void onCTCP (String command, String extra, String audience, String nick, String ident, String hostmask, RhinoBot bot)
	{
		for (IrcModule module : modules.values())
		{
			module.onCTCP(command, extra, audience, nick, ident, hostmask, bot);
		}
	}
	
	/**
	 * @see org.geartech.rhinobot.legacy.IrcModule#onVersion(java.lang.String, java.lang.String, java.lang.String)
	 */
	public static final synchronized void onVersion (String nick, String ident, String hostmask, RhinoBot bot)
	{
		for (IrcModule module : modules.values())
		{
			module.onVersion(nick, ident, hostmask, bot);
		}
	}
}