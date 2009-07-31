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
package org.rhinobot.module;

import java.io.IOException;

import org.kernel.ConfigReader;
import org.kernel.Logger;
import org.kernel.ConfigReader.UnableToParseException;
import org.kernel.Logger.LogLevel;
import org.rhinobot.bot.manager.User;
import org.rhinobot.db.Database;
import org.rhinobot.db.DatabaseException;

public final class DatabaseModuleController
{
	private final class ConnectionMaintainer extends Thread
	{
		boolean running = true;
		
		public ConnectionMaintainer ()
		{
			setName("Connection Maintainer");
			setPriority(Thread.MIN_PRIORITY);
			start();
		}
		
		public void run ()
		{
			while (running)
			{
				try
				{
					if (db.isConnected())
					{
						sleep(10000);
					}
					else
					{
						logger.write(LogLevel.MAJOR, "MySQL Lost connection to the server.. Reconnecting");
						db.connect(hostname, username, password, database);
					}
				}
				catch (InterruptedException e)
				{
					
				}
				catch (DatabaseException e)
				{
					logger.write(LogLevel.MAJOR, "Unable to reconnect MySQL to the server: " + e.getMessage());
					running = false;
				}
			}
		}
	}
	
	static final Logger						logger		= new Logger("logs", "DatabaseModuleController");
	private static DatabaseModuleController dbInstance	= new DatabaseModuleController();
	
	Database db;
	
	String hostname;
	String username;
	String password;
	String database;
	
	private ConnectionMaintainer	maintainer;
	
	public static final DatabaseModuleController getInstance()
	{
		return dbInstance;
	}
	
	/**
	 * @see java.lang.Object#clone()
	 */
	public final Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}
	
	private DatabaseModuleController ()
	{
	}
	
	public void init ()
	{
		if (isConnected()) return; 
		
		ConfigReader reader = null;
		
		try
		{
			reader = new ConfigReader("rhinobot.conf");
		}
		catch (UnableToParseException e)
		{
			logger.write(LogLevel.MAJOR, "Unable to parse rhinobot.conf: " + e.getMessage());
			return;
		}
		catch (IOException e)
		{
			logger.write(LogLevel.MAJOR, "IOException attempting to read rhinobot.conf: " + e.getMessage(), e);
			return;
		}
		
		if ((reader.blockExists("rhinobot>database")) &&
			(reader.settingExists("rhinobot>database", "class")) &&
			(reader.settingExists("rhinobot>database", "username")) &&
			(reader.settingExists("rhinobot>database", "password")) &&
			(reader.settingExists("rhinobot>database", "database")) &&
			(reader.settingExists("rhinobot>database", "hostname")))
		{
			String className = reader.getSetting("rhinobot>database", "class");
			
			username = reader.getSetting("rhinobot>database", "username");
			password = reader.getSetting("rhinobot>database", "password");
			database = reader.getSetting("rhinobot>database", "database");
			hostname = reader.getSetting("rhinobot>database", "hostname");
			
			
			try
			{
				Class cls = ModuleController.findClass(className);
				
				if (ModuleController.hasInterface(cls, "org.rhinobot.db.Database"))
				{
					db = (Database) cls.newInstance();
					db.connect(hostname, username, password, database);
				}
				else
				{
					logger.write(LogLevel.MAJOR, "Class loaded does not have interface "
							+ "org.rhinobot.db.Database");
					return;
				}
			}
			catch (Exception e)
			{
				logger.write(LogLevel.MAJOR, "Unable to load in class " + className);
				return;
			}
		}
		else
		{
			logger.write(LogLevel.MAJOR, "Config is missing one of the required parameters: " +
					"class, username, password, database, hostname");
			return;
		}
		
		maintainer = new ConnectionMaintainer();
	}
	
	/**
	 * 
	 * @return
	 */
	public final boolean isConnected ()
	{
		if (db == null) { return false; }
		
		boolean isConnected = false;

		try
		{
			isConnected = db.isConnected();
		}
		catch (DatabaseException e)
		{
			
		}
		
		return isConnected;
	}

	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#connect(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public final void connect (final String hostname, final String username, final String password, final String database) throws DatabaseException
	{
		if (isConnected()) 
		{
			return;
		}
		
		db.connect(hostname, username, password, database);
	}

	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#addUserPermission(org.rhinobot.bot.manager.User, int)
	 */
	public final boolean addUserPermission (final User user, final int permission) throws DatabaseException
	{
		if (!isConnected()) 
		{
			throw new DatabaseException("Not connected");
		}
		
		return db.addUserPermission(user, permission);
	}

	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#changeUserPermission(org.rhinobot.bot.manager.User, int)
	 */
	public final boolean changeUserPermission (final User user, final int newPermission) throws DatabaseException
	{
		if (!isConnected()) 
		{
			throw new DatabaseException("Not connected");
		}
		
		return db.changeUserPermission(user, newPermission);
	}

	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#changeUserPermission(java.lang.String, java.lang.String, java.lang.String, int)
	 */
	public final boolean changeUserPermission (final String userNick, final String userIdent, final String userHostmask, final int newPermission) throws DatabaseException
	{
		if (!isConnected()) 
		{
			throw new DatabaseException("Not connected");
		}
		
		return db.changeUserPermission(userNick, userIdent, userHostmask, newPermission);
	}

	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#getUserPermission(java.lang.String, java.lang.String, java.lang.String)
	 */
	public final int getUserPermission (final String userNick, final String userIdent, final String userHostmask) throws DatabaseException
	{
		if (!isConnected()) 
		{
			throw new DatabaseException("Not connected");
		}
		
		return db.getUserPermission(userNick, userIdent, userHostmask);
	}

	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#addChannelPermission(java.lang.String, java.lang.String, int)
	 */
	public final boolean addChannelPermission (final String channel, final String network, final int permission) throws DatabaseException
	{
		if (!isConnected()) 
		{
			throw new DatabaseException("Not connected");
		}
		
		return db.addChannelPermission(channel, network, permission);
	}

	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#changeChannelPermission(java.lang.String, java.lang.String, int)
	 */
	public final boolean changeChannelPermission (final String channel, final String network, final int newPermission) throws DatabaseException
	{
		if (!isConnected()) 
		{
			throw new DatabaseException("Not connected");
		}
		
		return db.changeChannelPermission(channel, network, newPermission);
	}

	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#getChannelPermission(java.lang.String, java.lang.String)
	 */
	public final int getChannelPermission (final String network, final String channel) throws DatabaseException
	{
		if (!isConnected()) 
		{
			throw new DatabaseException("Not connected");
		}
		
		return db.getChannelPermission(network, channel);
	}

	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#addIgnoreEntry(org.rhinobot.bot.manager.User)
	 */
	public final boolean addIgnoreEntry (final User user) throws DatabaseException
	{
		if (!isConnected()) 
		{
			throw new DatabaseException("Not connected");
		}
		
		return db.addIgnoreEntry(user);
	}

	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#removeIgnoreEntry(org.rhinobot.bot.manager.User)
	 */
	public final boolean removeIgnoreEntry (final User user) throws DatabaseException
	{
		if (!isConnected()) 
		{
			throw new DatabaseException("Not connected");
		}
		
		return db.removeIgnoreEntry(user);
	}

	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#getIgnoreList()
	 */
	public final User[] getIgnoreList () throws DatabaseException
	{
		if (!isConnected()) 
		{
			throw new DatabaseException("Not connected");
		}
		
		return db.getIgnoreList();
	}

	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#getLongVar(java.lang.String, java.lang.String)
	 */
	public final String getLongVar (final String varName, final String event) throws DatabaseException
	{
		if (!isConnected()) 
		{
			throw new DatabaseException("Not connected");
		}
		
		return db.getLongVar(varName, event);
	}

	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#getVar(java.lang.String, java.lang.String)
	 */
	public final String getVar (final String varName, final String event) throws DatabaseException
	{
		if (!isConnected()) 
		{
			throw new DatabaseException("Not connected");
		}
		
		return db.getVar(varName, event);
	}

	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#setLongVar(java.lang.String, java.lang.String, java.lang.String)
	 */
	public final void setLongVar (final String varName, final String event, final String varValue) throws DatabaseException
	{
		if (!isConnected()) 
		{
			throw new DatabaseException("Not connected");
		}
		
		db.setLongVar(varName, event, varValue);
	}

	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#setVar(java.lang.String, java.lang.String, java.lang.String)
	 */
	public final void setVar (final String varName, final String event, final String varValue) throws DatabaseException
	{
		if (!isConnected()) 
		{
			throw new DatabaseException("Not connected");
		}
		
		db.setVar(varName, event, varValue);
	}

	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#delVar(java.lang.String, java.lang.String)
	 */
	public final void delVar (final String varName, final String event) throws DatabaseException
	{
		if (!isConnected()) 
		{
			throw new DatabaseException("Not connected");
		}
		
		db.delVar(varName, event);
	}

	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#delLongVar(java.lang.String, java.lang.String)
	 */
	public final void delLongVar (final String varName, final String event) throws DatabaseException
	{
		if (!isConnected()) 
		{
			throw new DatabaseException("Not connected");
		}
		
		db.delLongVar(varName, event);
	}

	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#disconnect()
	 */
	public final void disconnect () throws DatabaseException
	{
		if (!isConnected()) 
		{
			throw new DatabaseException("Not connected");
		}
		
		db.disconnect();
		maintainer.running = false;
	}
}
