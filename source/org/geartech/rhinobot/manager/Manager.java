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
package org.geartech.rhinobot.manager;

import java.util.HashMap;
import java.util.Set;

import org.geartech.rhinobot.support.Logger;
import org.geartech.rhinobot.support.Logger.LogLevel;

/**
 * The Manager maintains a information system over the users and channels on
 * an IRC server. The Manager uses a few other classes to have it's job completely
 * done, but this is mostly fitted around usability rather than performance. This 
 * is an integrated part of the IRCBot and would be rather difficult to remove or
 * replace.
 */
public final class Manager
{
	/**
	 * Channels HashMap
	 * Keys are the channel name in lower case,
	 * while values are the Channel representation of each channel
	 */
	private final HashMap<String, Channel> channels	= new HashMap<String, Channel>();
	
	/**
	 * Users HashMap
	 * Keys are the user name in lowercase,
	 * while values are the User representation of each user
	 */
	private final HashMap<String, User>		users	= new HashMap<String, User>();
	
	/**
	 * Logger
	 */
	private static final Logger				logger	= new Logger("logs", "Manager");
	
	/**
	 * List Mode Chars
	 */
	private String 							listModeChars;
	
	/**
	 * Sets the listModeChars member
	 * @param listModeChars
	 */
	public final void setListModeChars (final String listModeChars)
	{
		if (this.listModeChars == null)
			this.listModeChars = listModeChars;
	}
	
	/**
	 * Adds a channel to the bot.
	 * 
	 * @param channel
	 */
	public final void addChannel (final String channel, final String network)
	{
		logger.write(LogLevel.VERBOSE, "Adding channel "+channel);
		Channel chnl = new Channel(channel, listModeChars);
		
		if (network != null)
		{
			chnl.fetchPermission(network);
		}
		
		channels.put(channel.toLowerCase(), chnl);
	}
	
	/**
	 * Gets a channel.
	 * 
	 * @param channel
	 * @return
	 */
	public final Channel getChannel (final String channel)
	{
		logger.write(LogLevel.VERBOSE, "Getting channel "+channel);
		return channels.get(channel.toLowerCase());
	}

	/**
	 * Returns all the channels with their Channel instance
	 * 
	 * @return
	 */
	public final Channel[] getChannels ()
	{
		logger.write(LogLevel.VERBOSE, "Getting all channels");
		return channels.values().toArray(new Channel[channels.size()]);
	}
	
	/**
	 * Removes a channel 
	 * 
	 * @param channel
	 */
	public final void removeChannel (final String channel)
	{
		logger.write(LogLevel.VERBOSE, "Removing channel "+channel);
		if (inChannel(channel))
		{
			Channel channelObj = getChannel(channel);
			User    userObj;
			
			for (String user : channelObj.getUsers())
			{
				userObj = getUser(user);
				
				// BUGGY
//				if (userObj == null) return;
				
				logger.write(LogLevel.VERBOSE, "Removing channel " + channel + " from user " + userObj.getNick());
				
				userObj.removeChannel(channel);
				
				if (!userObj.inAnyChannels())
				{
					removeUser(userObj.getNick());
				}
			}
			
			channels.remove(channel.toLowerCase());
		}
	}
	
	/**
	 * Checks if a user exists
	 * 
	 * @param user
	 * @return
	 */
	public final boolean userExists (String user)
	{
		logger.write(LogLevel.VERBOSE, "Checking if user "+user+" exists");
		user = user.toLowerCase();
		
		return (users.containsKey(user));
	}
	
	/**
	 * Lets the manager know a user that has been tracked has been found
	 * @param user
	 */
	public final void foundUser (final String userFound)
	{
		User user = users.get(userFound);
		
		if ((user != null) && (user.beingTracked()))
		{
			user.setFound(true);
		}
	}
	
	/**
	 * Checks if the bot is in a channel
	 * 
	 * @param channel
	 * @return
	 */
	public final boolean inChannel (String channel)
	{
		logger.write(LogLevel.VERBOSE, "Checking if bot is in channel "+channel);
		channel = channel.toLowerCase();
		
		return (channels.containsKey(channel));
	}
	
	/**
	 * Adds a user to the manager, as well as to a channel
	 * 
	 * @param user
	 * @param channel
	 */
	public final User addUser (final String user, final String channel)
	{
		logger.write(LogLevel.VERBOSE, "Adding user "+user+" who is in the channel "+channel);
		User newUser = new User(user);
		
		newUser.addChannel(channel);
		
		users.put(user.toLowerCase(), newUser);
		
		if (channels.containsKey(channel))
		{
			// Regular casing
			channels.get(channel).addUser(user);
		}
		else
		{
			Channel newChannel = new Channel(channel, listModeChars);
			newChannel.addUser(user);
			channels.put(channel.toLowerCase(), newChannel);
		}
		
		return newUser;
	}
	
	/**
	 * Resets the permission of ALL users.
	 */
	public final void resetAllPermissions ()
	{
		Set<String> keys = users.keySet();
		
		for (String key : keys)
		{
			users.get(key).resetPermission();
		}
	}
	
	/**
	 * Adds a user to the Manager
	 * 
	 * @param user
	 * @return the user created
	 */
	public final User addUser (final String user)
	{
		logger.write(LogLevel.VERBOSE, "Adding user "+user);
		
		User newUser = new User(user);
		
		users.put(user.toLowerCase(), newUser);
		
		return newUser;
	}
	
	/**
	 * Adds a new user with their ident and hostmask
	 * 
	 * @param user
	 * @param ident
	 * @param hostmask
	 */
	public final User addUser (final String user, final String ident, final String hostmask)
	{
		logger.write(LogLevel.VERBOSE, "Adding user "+user+", with ident "+ident+" and hostmask "+hostmask);

		User newUser = new User(user);
		
		newUser.setIdent(ident);
		newUser.setHostmask(hostmask);
		newUser.fetchPermission();
		
		users.put(user.toLowerCase(), newUser);
		
		return newUser;
	}
	
	/**
	 * Adds a user to a channel
	 * 
	 * @param user
	 * @param channel
	 */
	public final void addUserToChannel (final String user, final String channel)
	{
		logger.write(LogLevel.VERBOSE, "Adding user " + user + " to channel " + channel);
		
		if ((userExists(user)) && (inChannel(channel)))
		{
			User    userObj    = getUser(user);
			Channel channelObj = getChannel(channel);
			
			channelObj.addUser(userObj.getNick());
			userObj.addChannel(channel);
		}
	}
	
	/**
	 * Changes a users nick
	 * 
	 * @param user
	 * @param newUser
	 */
	public final void changeUser (final String user, final String newUser)
	{
		logger.write(LogLevel.VERBOSE, "Changing user "+user+" to "+newUser);
		if (userExists(user))
		{
			if (userExists(newUser))
			{
				removeUser(newUser);
			}
			
			User userObj = getUser(user);
			
			String[] channels = userObj.getChannels();
			
			for (String channel : channels)
			{
				getChannel(channel).changeUser(user, newUser.toLowerCase());
			}
			
			userObj.changeNick(newUser);
			
			users.put(newUser.toLowerCase(), userObj);
			users.remove(user);
		}
	}
	
	/**
	 * Returns all users.
	 * 
	 * @return
	 */
	public final String[] getUsers ()
	{
		logger.write(LogLevel.VERBOSE, "Getting ALL users");
		return users.keySet().toArray(new String[users.size()]);
	}
	
	/**
	 * Gets a user
	 * 
	 * @param user
	 * @return
	 */
	public final User getUser (String user)
	{
		logger.write(LogLevel.VERBOSE, "Getting user "+user);

		return users.get(user.toLowerCase());
	}
	
	/**
	 * Updates a user with new information for some specific events
	 * 
	 * @param nick
	 * @param ident
	 * @param hostmask
	 */
	public final void updateUser (String nick, String ident, String hostmask)
	{
		logger.write(LogLevel.VERBOSE, "Performing user update...");
		
		User user = getUser(nick);
		
		if ( (ident != null) && (!ident.equals("")) )
		{
			user.setIdent(ident);
		}
		
		if ( (hostmask != null) && (!hostmask.equals("")) )
		{
			user.setHostmask(hostmask);
		}
	}
	
	/**
	 * Deletes a user
	 * 
	 * @param user
	 */
	public final void removeUser (String user)
	{
		logger.write(LogLevel.VERBOSE, "Removing user "+user);
		if (userExists(user))
		{
			User userObj = getUser(user);
			
			String[] channels = userObj.getChannels();
			
			for (String channel : channels)
			{
				getChannel(channel).removeUser(user);
			}
			users.remove(user.toLowerCase());
		}
	}
	
	/**
	 * Removes a user from a channel
	 * 
	 * @param user
	 * @param channel
	 */
	public final void removeUserFromChannel (String user, String channel)
	{
		logger.write(LogLevel.VERBOSE, "Removing user "+user+" from channel "+channel);
		if ((userExists(user)) && (inChannel(channel)))
		{
			getChannel(channel).removeUser(user);
			getUser(user).removeChannel(channel);
		}
	}
	
	/**
	 * Ignores a user
	 * @param user
	 */
	public final void ignoreUser (String user)
	{
		User userx = getUser(user);
		
		if ((userx != null) && (!userx.isIgnored()))
		{
			userx.setIgnoredStatus(true);
		}
	}
	
	/**
	 * Un-ignores a user
	 * @param user
	 */
	public final void unIgnoreUser (String user)
	{
		User userx = getUser(user);
		
		if ((userx != null) && (userx.isIgnored()))
		{
			userx.setIgnoredStatus(false);
		}
	}
	
	/**
	 * Tells whether or not the user is ignored.
	 * @param user
	 * @return
	 */
	public final boolean isIgnored (String user)
	{
		User userx = getUser(user);
		
		if (userx == null) { return false; }
		
		return userx.isIgnored();
	}
	
	/**
	 * Purges all users, channels, and the tracker
	 */
	public final void purge ()
	{
		users.clear();
		channels.clear();
	}
}
