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
package org.rhinobot.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.kernel.Logger;
import org.kernel.Logger.LogLevel;
import org.rhinobot.bot.manager.User;
import org.rhinobot.lib.StringUtils;

/**
 * MySQL JDBC wrapper
 */
public final class MySQL implements Database
{	
	/**
	 * Connection to the MySQL database
	 */
	private Connection		connection;
	
	private final Logger	logger		= new Logger("logs", "MySQL");
	
	private boolean			connected	= false;
	
	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#connect(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
	 */
	public final void connect (final String hostname, final String username, final String password, final String database)
					throws CannotConnectException
	{
		if (connected) return;
		
		try
		{
			String url = "jdbc:mysql://" + hostname + "/" + database + "?user=" + username + "&password=" + password;
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			connection = DriverManager.getConnection(url);
		}
		catch (Exception e)
		{
			logger.write(LogLevel.MAJOR, "Unable to start up MySQL connection: " + e.getMessage());
			throw new CannotConnectException(e.getMessage());
		}
		
		connected = true;
	}

	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#addUserPermission(org.rhinobot.bot.manager.User, int)
	 */
	public final boolean addUserPermission (final User user, final int permission)
	{
		logger.write(LogLevel.INFO, "Adding User " + user.getNick() + " to database with permission " + 
				Integer.toString(permission));
		
		if (!isConnected())
		{
			logger.write(LogLevel.MINOR, "Unable to add a user to permission: MySQL is not connected!");
			return false;
		}
		
		if (!user.canGetPermission())
		{
			return false;
		}
		
		String sql = "INSERT INTO users (user_id, user_nick, user_ident, user_hostmask, user_permission) "
				+" VALUES ('', '" + user.getNick() + "', '" + user.getIdent() + "',"
				+"'" + cutString(user.getHostmask(), 75) + "', '" + Integer.toString(permission) + "')";
		
		boolean success = false;
		
		try
		{
			Statement sm = connection.createStatement();
			sm.execute(sql);
		}
		catch (SQLException e)
		{
			logger.write(LogLevel.MINOR, "SQL Error attempting to add user permission: " + e.getMessage(), e);
		}
		finally
		{
			success = true;
		}
		
		return success;
	}
	
	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#changeUserPermission(org.rhinobot.bot.manager.User, int)
	 */
	public final boolean changeUserPermission (final User user, final int newPermission)
	{
		return changeUserPermission(user.getNick(), user.getIdent(), user.getHostmask(), newPermission);
	}
	
	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#changeUserPermission(java.lang.String, java.lang.String, java.lang.String, int)
	 */
	public final boolean changeUserPermission (final String userNick, final String userIdent, String userHostmask, final int newPermission)
	{
		logger.write(LogLevel.INFO, "Changing User " + userNick + "!" + userIdent + "@" 
				+ userHostmask + " to " + Integer.toString(newPermission));
		
		if (!isConnected())
		{
			logger.write(LogLevel.MINOR, "Unable to change user permission: MySQL is not connected!");
			return false;
		}
		
		userHostmask = cutString(userHostmask, 75);
		String sql = "UPDATE users SET user_permission = " + newPermission + " WHERE user_nick = '" + StringUtils.addSlashes(userNick) + "'"
				+ " AND user_ident = '" + StringUtils.addSlashes(userIdent) + "'"
				+ " AND user_hostmask = '" + StringUtils.addSlashes(userHostmask) + "'";
		
		boolean success = false;
		
		try
		{
			Statement sm = connection.createStatement();
			sm.execute(sql);
		}
		catch (SQLException e)
		{
			logger.write(LogLevel.MINOR, "SQL Error attempting to change user permission: " + e.getMessage(), e);
		}
		finally
		{
			success = true;
		}
		
		return success;
	}
	
	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#getUserPermission(java.lang.String, java.lang.String, java.lang.String)
	 */
	public final int getUserPermission (final String userNick, final String userIdent, String userHostmask)
	{
		logger.write(LogLevel.VERBOSE, 
				"Getting user permission: getUserPermission("+userNick+", "+userIdent+", "+userHostmask+");");
		
		if (!isConnected())
		{
			logger.write(LogLevel.MINOR, "Unable to get permission: MySQL is not connected!");
			return 0;
		}
		
		userHostmask = cutString(userHostmask, 75);
		String sql = "SELECT user_permission FROM users WHERE user_nick = '" + StringUtils.addSlashes(userNick) + "' "
					+"AND user_ident = '" + StringUtils.addSlashes(userIdent) + "' "
					+"AND user_hostmask = '" + StringUtils.addSlashes(userHostmask) + "' "
					+"ORDER BY user_permission DESC "
					+"LIMIT 1";
		
		int result = 0;
		
		try
		{
			Statement sm = connection.createStatement();
			ResultSet rs = sm.executeQuery(sql);
			while (rs.next())
			{
				result = rs.getInt("user_permission");
			}
		}
		catch (SQLException e)
		{
			logger.write(LogLevel.MINOR, "SQL Error attempting to get user permission: "+e.getMessage());
		}
		
		logger.write(LogLevel.VERBOSE, "Got result: "+result);
		
		return result;
	}
	
	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#addChannelPermission(java.lang.String, java.lang.String, int)
	 */
	public final boolean addChannelPermission (String network, String channel, int permission)
	{
		logger.write(LogLevel.INFO, "Adding Channel " + channel + " to database with permission " + Integer.toString(permission));
		
		if (!isConnected())
		{
			logger.write(LogLevel.MINOR, "Unable to add channel permission: MySQL is not connected!");
			return false;
		}
		
		String sql = "INSERT INTO channels (channel_id, channel_name, network, channel_permission) "
					+"VALUES('', '" + channel + "', '" + network + "', '" + Integer.toString(permission) + "')";
		
		boolean success = false;
		
		try
		{
			Statement sm = connection.createStatement();
			sm.execute(sql);
		}
		catch (SQLException e)
		{
			logger.write(LogLevel.MINOR, "SQL Error attempting to add channel permission: " + e.getMessage(), e);
		}
		finally
		{
			success = true;
		}
		
		return success;
	}
	
	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#changeChannelPermission(java.lang.String, java.lang.String, int)
	 */
	public final boolean changeChannelPermission (String network, String channel, int newPermission)
	{
		logger.write(LogLevel.INFO, "Changing channel permission of channel " + channel + " on network " + network + " to become " +
				Integer.toString(newPermission));
		
		if (!isConnected())
		{
			logger.write(LogLevel.MINOR, "Unable to change channel permission: MySQL is not connected!");
			return false;
		}
		
		String sql = "UPDATE channels SET channel_permission = " + Integer.toString(newPermission) + " WHERE "
					+"network = '" + network + "' AND channel = '" + channel + "'";
		
		boolean success = false;
		
		try
		{
			Statement sm = connection.createStatement();
			sm.execute(sql);
		}
		catch (SQLException e)
		{
			logger.write(LogLevel.MINOR, "SQL Error attempting to change channel permission: " + e.getMessage(), e);
		}
		finally
		{
			success = true;
		}
		
		return success;
	}
	
	/**
	 * @param network
	 * @param channel
	 * @return
	 */
	public final int getChannelPermission (String network, String channel)
	{
		logger.write(LogLevel.VERBOSE, "Getting channel permission for channel " + channel + " on network " + network);
		
		if (!isConnected())
		{
			logger.write(LogLevel.MINOR, "Unable to get permission: MySQL is not connected!");
			return 0;
		}
		
		channel = cutString(channel, 45);
		
		if (network != null)
		{
			network = cutString(network, 45);
		}
		
		String sql = "SELECT channel_permission FROM channels WHERE channel_name = '" + StringUtils.addSlashes(channel) + "' "
					+"AND network = '" + StringUtils.addSlashes(network) + "' "
					+"ORDER BY channel_permission DESC "
					+"LIMIT 1";
		
		int result = 0;
		
		try
		{
			Statement sm = connection.createStatement();
			ResultSet rs = sm.executeQuery(sql);
			while (rs.next())
			{
				result = rs.getInt("channel_permission");
			}
		}
		catch (SQLException e)
		{
			logger.write(LogLevel.MINOR, "SQL Error getting user permission: " + e.getMessage(), e);
		}
		
		logger.write(LogLevel.VERBOSE, "Got result: "+result);
		
		return result;
	}
	
	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#addIgnoreEntry(org.rhinobot.bot.manager.User)
	 */
	public final boolean addIgnoreEntry (User user)
	{
		logger.write(LogLevel.INFO, "Adding user " + user.getNick() + " to Ignore List");
		
		if (!isConnected())
		{
			logger.write(LogLevel.MINOR, "Unable to add user to ignore list: MySQL is not connected!");
			return false;
		}
		
		String sql = "INSERT INTO users (user_id, user_nick, user_ident, user_hostmask) "
			+" VALUES ('', '" + user.getNick() + "', '" + user.getIdent() + "',"
			+"'" + cutString(user.getHostmask(), 75) + "')";
		
		boolean success = false;
		
		try
		{
			Statement sm = connection.createStatement();
			sm.execute(sql);
		}
		catch (SQLException e)
		{
			logger.write(LogLevel.MINOR, "SQL Error attempting to add ignore entry: " + e.getMessage(), e);
		}
		finally
		{
			success = true;
		}
		
		return success;
	}
	
	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#removeIgnoreEntry(org.rhinobot.bot.manager.User)
	 */
	public final boolean removeIgnoreEntry (User user)
	{
		logger.write(LogLevel.INFO, "Removing user " + user.getNick() + " from Ignore List");
		
		if (!isConnected())
		{
			logger.write(LogLevel.MINOR, "Unable to remove user from ignore list: MySQL is not connected!");
			return false;
		}
		
		String sql = "DELETE FROM users WHERE "
			+" user_nick = '" + user.getNick() + "' AND user_ident = '" + user.getIdent() + "' AND "
			+" user_hostmask = '" + cutString(user.getHostmask(), 75) + "'";
		
		boolean success = false;
		
		try
		{
			Statement sm = connection.createStatement();
			sm.execute(sql);
		}
		catch (SQLException e)
		{
			logger.write(LogLevel.MINOR, "SQL Error attempting to remove user from ignore list: " + e.getMessage(), e);
		}
		finally
		{
			success = true;
		}
		
		return success;
	}
	
	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#getIgnoreList()
	 */
	public final User[] getIgnoreList ()
	{
		logger.write(LogLevel.INFO, "Grabbing a list of all ignored users");
		
		if (!isConnected())
		{
			logger.write(LogLevel.MINOR, "Unable to remove user from ignore list: MySQL is not connected!");
			return null;
		}
		
		String sql = "SELECT * FROM ignores";
		
		User[] result = null;
		
		try
		{
			Statement sm = connection.createStatement();
			ResultSet rs = sm.executeQuery(sql);
			
			result = new User[rs.getFetchSize()];
			int i = 0;
			User user;
			
			while (rs.next())
			{
				user = new User(rs.getString("user_nick"));
				user.setIdent(rs.getString("user_ident"));
				user.setHostmask(rs.getString("user_hostmask"));
				
				result[i++] = user;
			}
		}
		catch (SQLException e)
		{
			logger.write(LogLevel.MINOR, "SQL Error attempting to get ignore lise: " + e.getMessage(), e);
		}
		
		return result;
	}
	
	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#getLongVar(java.lang.String, java.lang.String)
	 */
	public final String getLongVar (String varName, String event)
	{
		varName    = StringUtils.addSlashes(cutString(varName, 75));
		event      = StringUtils.addSlashes(cutString(event, 20));
		String sql = "SELECT var_value FROM long_variables "
			 		+"WHERE var_name = '" + varName + "' "
			 		+"AND var_event = '" + event + "' LIMIT 1";
		
		String result = null;
		
		try
		{
			Statement sm = connection.createStatement();
			ResultSet rs = sm.executeQuery(sql);
			while (rs.next())
			{
				result = StringUtils.stripSlashes(rs.getString("var_value"));
			}
		}
		catch (SQLException e)
		{
			logger.write(LogLevel.MINOR, "SQL Error getting user permission: " + e.getMessage(), e);
		}
		
		return result;
	}
	
	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#getVar(java.lang.String, java.lang.String)
	 */
	public final String getVar (String varName, String event)
	{
		varName    = cutString(varName, 50);
		String sql = "SELECT var_value FROM short_variables "
				 	+"WHERE var_name = '" + varName + "' "
				 	+"AND var_event = '" + event + "' LIMIT 1";
		
		String result = null;
		
		try
		{
			Statement sm = connection.createStatement();
			ResultSet rs = sm.executeQuery(sql);
			while (rs.next())
			{
				result = StringUtils.stripSlashes(rs.getString("var_value"));
			}
		}
		catch (SQLException e)
		{
			logger.write(LogLevel.MINOR, "SQL Error getting a variable: " + e.getMessage(), e);
		}
		
		return result;
	}
	
	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#setLongVar(java.lang.String, java.lang.String, java.lang.String)
	 */
	public final void setLongVar (String varName, final String event, String varValue)
	{
		varName  = StringUtils.addSlashes(cutString(varName, 75));
		varValue = StringUtils.addSlashes(cutString(varValue, 65535));
		
		String sql;
		
		if (getLongVar(varName, event) != null)
		{
			sql = "UPDATE long_variables SET var_value = '" + varValue + "' "
				+ "WHERE var_name = '" + varName + "' "
				+ "AND var_event = '" + event + "' LIMIT 1";
		}
		else
		{
			sql = "INSERT INTO long_variables (var_name, var_event, var_value) "
				+ "VALUES ('" + varName + "', '" + event + "', '" + varValue + "')";
		}
		
		try
		{
			Statement sm = connection.createStatement();
			sm.execute(sql);
		}
		catch (SQLException e)
		{
			logger.write(LogLevel.MINOR, "SQL Error setting a variable: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#setVar(java.lang.String, java.lang.String, java.lang.String)
	 */
	public final void setVar (String varName, String event, String varValue)
	{
		varName  = StringUtils.addSlashes(cutString(varName, 50));
		varValue = StringUtils.addSlashes(cutString(varValue, 250));
		
		String sql;
		
		if (getVar(varName, event) != null)
		{
			sql = "UPDATE short_variables SET var_value = '" + varValue + "' "
				+ "WHERE var_name = '" + varName + "' "
				+ "AND var_event = '" + event + "' LIMIT 1";
		}
		else
		{
			sql = "INSERT INTO short_variables (var_name, var_event, var_value) "
				+ "VALUES ('" + varName + "', '" + event + "', '" + varValue + "')";
		}
		
		try
		{
			Statement sm = connection.createStatement();
			sm.execute(sql);
		}
		catch (SQLException e)
		{
			logger.write(LogLevel.MINOR, "SQL Error trying to set variable '" + varName + "': " + e.getMessage(), e);
		}
	}
	
	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#delVar(java.lang.String, java.lang.String)
	 */
	public final void delVar (String varName, String event)
	{
		varName = StringUtils.addSlashes(cutString(varName, 25));
		
		String sql = "DELETE FROM short_variables WHERE var_name = '" + varName + "' AND var_event = '" + event + "' LIMIT 1";
		
		try
		{
			Statement sm = connection.createStatement();
			sm.execute(sql);
		}
		catch (SQLException e)
		{
			logger.write(LogLevel.MINOR, "SQL Error trying to delete variable '" + varName + "': " + e.getMessage(), e);
		}
	}
	
	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#delLongVar(java.lang.String, java.lang.String)
	 */
	public final void delLongVar (String varName, String event)
	{
		varName = StringUtils.addSlashes(cutString(varName, 75));
		
		String sql = "DELETE FROM long_variables WHERE var_name = '" + varName + "' AND var_event = '" + event + "' LIMIT 1";
		
		try
		{
			Statement sm = connection.createStatement();
			sm.execute(sql);
		}
		catch (SQLException e)
		{
			logger.write(LogLevel.INFO, "SQL Error trying to delete variable '" + varName + "': " + e.getMessage(), e);
		}
	}
	
	/**
	 * Cuts a string to a specific length
	 * @param string
	 * @param limit
	 * @return
	 */
	private final String cutString (final String string, final int limit)
	{
		return ((string.length() > limit) ? string.substring(0, limit) : string);
	}
	
	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#isConnected()
	 */
	public final boolean isConnected ()
	{
		return ((connection != null) && (connected));
	}
	
	/**
	 * Overidden
	 * @see org.rhinobot.db.Database#disconnect()
	 */
	public final void disconnect ()
	{
		if (!connected) return;
		
		try
		{
			if ((connection != null) && (!connection.isClosed()))
			{
				connection.close();
			}
		}
		catch (SQLException e)
		{
			logger.write(LogLevel.MAJOR, "Unable to close connection: " + e.getMessage(), e);
		}
		
		connected = false;
	}
}
