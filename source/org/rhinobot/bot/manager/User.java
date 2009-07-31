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
package org.rhinobot.bot.manager;

import java.util.HashMap;

import org.rhinobot.db.DatabaseException;
import org.rhinobot.module.DatabaseModuleController;

public final class User
{
	/**
	 * All the channels the user is in, as well as the users prefix
	 * in that channel.
	 */
	private final HashMap<String, String> channels = new HashMap<String, String>(3, 0.8f);

	/**
	 * Users nick, with normal casing
	 */
	private String  nick;
	
	/**
	 * Users ident
	 */
	private String  ident;
	
	/**
	 * Users hostmask
	 */
	private String  hostmask;
	
	/**
	 * Users modes
	 */
	private String  modes;
	
	/**
	 * Users permission, defaulted to 0
	 */
	private int     permission = 0;
	
	/**
	 * The idle time for this user
	 */
	private long    idleTime	= -1;
	
	/**
	 * the Signon time this user signed on..
	 */
	private long    signonTime	= -1;
	
	/**
	 * TRUE if the user is away
	 */
	private boolean away       = false;
	
	/**
	 * the reason the user is away, if the user is away. Used in conjunction with <code>away</code>
	 */
	private String  awayReason;
	
	/**
	 * This is TRUE if the user has gotten their permission
	 * from the database yet.
	 */
	private boolean hasGottenPermission = false;
	
	/**
	 * This is TRUE if the user is an irc operator
	 */
	private boolean oper                = false;
	
	/**
	 * This is true if the nickname is registered
	 */
	private boolean registeredNickname	= false;
	
	/**
	 * This should be inherently false
	 */
	private boolean secureConnection    = false;
	
	/**
	 * If the user is ignored then the bot will not attempt to parse a message
	 * from this user.
	 */
	private boolean ignored				= false;
	
	/**
	 * The server the user is on. returned by a WHOIS
	 */
	private String  server = "";
	
	/**
	 * This is set if there is an account name associated with the user
	 */
	private String  accountName;
	
	/**
	 * This is TRUE if the user is being tracked and does not exist, etc. etc.
	 */
	private boolean tracked		= false;
	
	/**
	 * Shows whether the user has been found or not.
	 */
	private boolean found		= false;
	
	/**
	 * Constructor, sends the users regular-casing nick
	 * 
	 * @param user
	 */
	public User (String user)
	{
		nick = user;
	}
	
	/**
	 * Constructor for building an ignored user.
	 * 
	 * @param user
	 * @param ignored
	 */
	public User (String user, boolean ignored)
	{
		nick         = user;
		this.ignored = ignored;
	}
	
	/**
	 * Sets the user's modes 
	 * 
	 * @param usermodes
	 */
	public final void setUsermodes (final String usermodes)
	{
		this.modes = usermodes;
	}
	
	/**
	 * Gets the user's modes
	 * @return
	 */
	public final String getUsermodes ()
	{
		return modes;
	}
	
	/**
	 * Sets whether or not this user is an operator
	 * @param operator
	 */
	public final void setOper (final boolean operator)
	{
		oper = operator;
	}
	
	/**
	 * Returns whether or not this user is an operator
	 * @return
	 */
	public final boolean isOper ()
	{
		return oper;
	}
	
	/**
	 * Checks if this user has gotten their permission
	 * from the database yet.
	 * 
	 * @return
	 */
	public final boolean hasGottenPermission ()
	{
		return hasGottenPermission;
	}
	
	/**
	 * Sets the account name if the bot is on a ircu-based server
	 * 
	 * @param accountName
	 */
	public final void setAccountName (final String accountName)
	{
		this.accountName = accountName;
	}
	
	/**
	 * Gets the account name
	 * 
	 * @return null if the accoune name was never set or doesn't exist.
	 */
	public final String getAccountName ()
	{
		return accountName;
	}
	
	/**
	 * Sets the server the user is on
	 * @param server
	 */
	public final void setServer (final String server)
	{
		if (server != null)
		{
			this.server = server;
		}
	}
	
	/**
	 * Returns the server the user was on, if any
	 * @return
	 */
	public final String getServer ()
	{
		return server;
	}
	
	/**
	 * Checks if theres enough information to get the users
	 * permission from the database.
	 * 
	 * @return if the bot can get the permission
	 */
	public final boolean canGetPermission ()
	{
		return ( (ident != null) && (hostmask != null) );
	}
	
	/**
	 * Resets the permission back to default
	 */
	public final void resetPermission ()
	{
		hasGottenPermission = false;
		permission			= 0;
	}
	
	/**
	 * Fetches the users permission from the database
	 */
	public final void fetchPermission ()
	{
		if (!canGetPermission()) { return; }
		
		hasGottenPermission = true;
		
		try
		{
			permission = DatabaseModuleController.getInstance().getUserPermission(nick, ident, hostmask);
		}
		catch (DatabaseException e)
		{
			return;
		}
	}
	
	/**
	 * Returns the users permission
	 * 
	 * @return users permission
	 */
	public final int getPermission ()
	{
		return permission;
	}
	
	/**
	 * Changes the nick this User instance is represented by
	 * 
	 * @param newNick
	 */
	public final void changeNick (final String newNick)
	{
		nick = newNick;
	}
	
	/**
	 * Returns the users nick
	 * @return
	 */
	public final String getNick ()
	{
		return nick;
	}
	
	/**
	 * Sets the users ident
	 * 
	 * @param ident
	 */
	public final void setIdent (final String ident)
	{
		this.ident = ident;
	}

	/**
	 * Returns the users ident
	 * 
	 * @return
	 */
	public final String getIdent ()
	{
		return ident;
	}
	
	/**
	 * Sets the users hostmask
	 * 
	 * @param hostmask
	 */
	public final void setHostmask (final String hostmask)
	{
		this.hostmask = hostmask;
	}
	
	/**
	 * Returns the users hostmask
	 * 
	 * @return
	 */
	public final String getHostmask ()
	{
		return hostmask;
	}
	
	/**
	 * Adds a channel that this user is in
	 * 
	 * @param channel
	 */
	public final void addChannel (final String channel)
	{
		channels.put(channel.toLowerCase(), null);
	}
	
	/**
	 * Adds a channel this user is in with their prefix.
	 * 
	 * @param channel
	 * @param status
	 */
	public final void addChannel (final String channel, final String status)
	{
		channels.put(channel.toLowerCase(), status);
	}
	
	/**
	 * Gets all the channels this user is in, in an array form
	 * 
	 * @return
	 */
	public final String[] getChannels ()
	{
		return channels.keySet().toArray(new String[channels.keySet().size()]);
	}
	
	/**
	 * This checks if the user is in any channels. If not, then the
	 * user should be promptly deleted
	 * 
	 * @return
	 */
	public final boolean inAnyChannels ()
	{
		return (!channels.isEmpty());
	}
	
	/**
	 * Checks if the user is in a specific channel
	 * 
	 * @param channel
	 * @return
	 */
	public final boolean inChannel (final String channel)
	{
		return (channels.containsKey(channel.toLowerCase()));
	}
	
	/**
	 * Removes this user from a channel
	 * 
	 * @param channel
	 */
	public final void removeChannel (final String channel)
	{
		if (inChannel(channel))
		{
			channels.remove(channel.toLowerCase());
		}
	}
	
	/**
	 * Sets the value on whether or not the nickname is registered
	 * @param registered
	 */
	public final void setRegistered (final boolean registered)
	{
		registeredNickname = registered;
	}
	
	/**
	 * Returns whether or not the nickname is registered.
	 * @return
	 */
	public final boolean isRegistered ()
	{
		return registeredNickname;
	}
	
	/**
	 * Sets the idle time for this bat
	 * 
	 * @param time
	 */
	public final void setIdleTime (final long time)
	{
		idleTime = time;
	}
	
	/**
	 * Gets the idle time for this client
	 * @return
	 */
	public final long getIdleTime ()
	{
		return idleTime;
	}
	
	/**
	 * Sets the signon time for this user. Usually in the
	 * form of a linux timestamp
	 * 
	 * @param time
	 */
	public final void setSignonTime (final long time)
	{
		signonTime = time;
	}
	
	/**
	 * Gets the signon time for this user
	 * 
	 * @return
	 */
	public final long getSignonTime ()
	{
		return signonTime;
	}
	
	/**
	 * Adds the users prefix to a channel
	 * 
	 * @param channel
	 * @param status
	 */
	public final void setStatus (final String channel, final String status)
	{
		removeChannel(channel);
		addChannel(channel, status);
	}
	
	/**
	 * Gets the user prefix to a specific channel
	 * 
	 * @param channel
	 * @return the user prefix
	 */
	public final String getStatus (final String channel)
	{
		return channels.get(channel.toLowerCase());
	}
	
	/**
	 * Sets the ignored status of this user from true to false
	 * @param ignored
	 */
	public final void setIgnoredStatus (final boolean ignored)
	{
		this.ignored = ignored;
	}
	
	/**
	 * 
	 * @return TRUE if ignored, FALSE if not.
	 */
	public final boolean isIgnored ()
	{
		return ignored;
	}
	
	/**
	 * 
	 * @param secure
	 */
	public final void setSecureConnection (final boolean secure)
	{
		secureConnection = secure;
	}
	
	/**
	 * 
	 * @return true if the user is connected on an SSL connection.
	 */
	public final boolean isConnectedSecurely ()
	{
		return secureConnection;
	}
	
	/**
	 * Set's that the user has been found, or hasn't
	 * @param found
	 */
	public final void setFound (final boolean hasBeenFound)
	{
		if ((tracked) && (!found))
		{
			found = hasBeenFound;
		}
	}
	
	/**
	 * Returns whether or not the user has been found
	 * @return
	 */
	public final boolean isFound ()
	{
		return found;
	}
	
	/**
	 * Enables tracking
	 */
	public final void enableTracking ()
	{
		if (!tracked)
		{
			tracked = true;
		}
	}
	
	/**
	 * Returns TRUE if the user is being tracked
	 * @return
	 */
	public final boolean beingTracked ()
	{
		return tracked;
	}
	
	/**
	 * Sets the away status
	 * 
	 * @param away
	 */
	public final void setAway (final boolean away)
	{
		this.away = away;
	}
	
	/**
	 * Returns if the user is away
	 * 
	 * @return
	 */
	public final boolean isAway ()
	{
		return away;
	}
	
	/**
	 * Sets away reason
	 * 
	 * @param reason
	 */
	public final void setAwayReason (final String reason)
	{
		awayReason = reason;
	}
	
	/**
	 * Gets the away reason
	 * 
	 * @return
	 */
	public final String getAwayReason ()
	{
		return awayReason;
	}
}
