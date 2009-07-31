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

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public final class Channel
{
	/**
	 * Users that are currently in this channel
	 */
	private final ArrayList<String> users = new ArrayList<String>(10);
	
	/**
	 * This channels name, regular casing
	 */
	private String channel;
	
	/**
	 * Channel topic
	 */
	private String topic;
	
	/**
	 * The creater of the topic
	 */
	private String topicCreater;
	
	/**
	 * The date the topic was changed/created
	 */
	private String topicDate;
	
	/**
	 * Last topic on the channel
	 */
	private String previousTopic;
	
	/**
	 * Channel modes enforced
	 */
	private String modes;
	
	/**
	 * Whether or not the channel itself is ignored
	 */
	private boolean ignored = false;
	
	/**
	 * The Channel's permission
	 */
	private int permission = 0;
	
//	/**
//	 * Ban Excludes set for a specific channel
//	 */
//	private final ArrayList<String> excludes	= new ArrayList<String>();
//	
//	/**
//	 * Bans set for a specific channel
//	 */
//	private final ArrayList<String> bans		= new ArrayList<String>();

	private ModeList[] modeLists;
	
	/**
	 * TRUE if the channel has fetched the database for its permission, FALSE if not.
	 */
	private boolean	hasGottenPermission = false;
	
	/**
	 * Constructor.
	 * 
	 * @param channelName
	 */
	public Channel (String channelName, String modeChars)
	{
		channel = channelName;
		
		char[] modeListChars = modeChars.toCharArray();
		modeLists = new ModeList[modeListChars.length];
		for (int i = 0; i < modeLists.length; i++)
		{
			modeLists[i] = new ModeList(modeListChars[i]);
		}
	}
	
	/**
	 * Returns the channels name with regular casing
	 * 
	 * @return
	 */
	public final String getChannelName ()
	{
		return channel;
	}
	
	/**
	 * Sets channel modes
	 * 
	 * @param newModes
	 */
	public final void setChannelModes (final String newModes)
	{
		modes = newModes;
	}
	
	/**
	 * Returns channel modes
	 * 
	 * @return modes
	 */
	public final String getChannelModes ()
	{
		return modes;
	}
	
	/**
	 * Sets the topic creation or change date
	 * 
	 * @param timestamp
	 */
	public final void setTopicDate (final String timestamp)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("E M d, h:m:s a z Z");
		String tempDate;
		tempDate  = sdf.format(Integer.parseInt(timestamp));
		topicDate = tempDate;
	}
	
	/**
	 * Returns the topic creation or change date.
	 * 
	 * @return
	 */
	public final String getTopicDate ()
	{
		return topicDate;
	}
	
	/**
	 * Sets the topic creater
	 * 
	 * @param creater
	 */
	public final void setTopicCreater (final String creater)
	{
		if (creater != null)
		{
			topicCreater = creater;
		}
	}
	
	/**
	 * Gets the topic creater
	 * 
	 * @return
	 */
	public final String getTopicCreater ()
	{
		return topicCreater;
	}
	
	/**
	 * Sets a new topic
	 * 
	 * @param newTopic
	 */
	public final void setTopic (final String newTopic)
	{
		if (topic != null)
		{
			previousTopic = topic;
		}
		topic = newTopic;
	}
	
	/**
	 * Returns the topic
	 * 
	 * @return topic
	 */
	public final String getTopic ()
	{
		return topic;
	}
	
	/**
	 * Returns the previous topic
	 * 
	 * @return previous topic
	 */
	public final String getPreviousTopic ()
	{
		return previousTopic;
	}
	
	/**
	 * Adds to a specific list designated by its listChar mode char.
	 * @param listChar
	 * @param entry
	 */
	public final void addToList (char listChar, Mask entry)
	{
		for (ModeList list : modeLists)
		{
			if (list.getModeChar() == listChar)
				list.addToList(entry);
		}
	}
	
	/**
	 * Adds to a specific list designated by its listChar mode char.
	 * @param listChar
	 * @param entry
	 */
	public final void addToList (char listChar, String entry)
	{
		for (ModeList list : modeLists)
		{
			if (list.getModeChar() == listChar)
				list.addToList(entry);
		}
	}
	
	/**
	 * Checks if a list exists by its listChar
	 * @param listChar
	 * @return
	 */
	public final boolean listExists (char listChar)
	{
		for (ModeList list : modeLists)
		{
			if (list.getModeChar() == listChar)
				return true;
		}
		
		return false;
	}
	
	/**
	 * Gets a list of masks as strings from a specific list by its listChar
	 * @param listChar
	 * @return
	 */
	public final String[] getList (char listChar)
	{
		for (ModeList list : modeLists)
		{
			if (list.getModeChar() == listChar)
				return list.getList();
		}
		
		return null;
	}
	
	/**
	 * Attempts to remove an entry from a list
	 * @param listChar
	 * @param entry
	 */
	public final void removeFromList (char listChar, Mask entry)
	{
		for (ModeList list : modeLists)
		{
			if (list.getModeChar() == listChar)
				list.removeFromList(entry);
		}
	}
	
	/**
	 * Attempts to remove an entry from a list
	 * @param listChar
	 * @param entry
	 */
	public final void removeFromList (char listChar, String entry)
	{
		for (ModeList list : modeLists)
		{
			if (list.getModeChar() == listChar)
				list.removeFromList(entry);
		}
	}
	
	/**
	 * Clears a list.
	 * @param listChar
	 */
	public final void clearList (char listChar)
	{
		for (ModeList list : modeLists)
		{
			if (list.getModeChar() == listChar)
				list.clear();
		}
	}
	
//	/**
//	 * Sets a ban
//	 * 
//	 * @param banMask
//	 */
//	public final void addBan (String banMask)
//	{
//		banMask = banMask.toLowerCase();
//		
//		bans.add(banMask);
//	}
//	
//	/**
//	 * Returns all the bans
//	 * 
//	 * @return
//	 */
//	public final String[] getBans ()
//	{
//		return bans.toArray(new String[bans.size()]);
//	}
//	
//	/**
//	 * Attempts to remove a ban
//	 * 
//	 * @param banMask
//	 */
//	public final void removeBan (String banMask)
//	{
//		banMask = banMask.toLowerCase();
//		
//		bans.remove(banMask);
//	}
//	
//	/**
//	 * Attempts to remove a ban via its number
//	 * 
//	 * @param banNumber
//	 */
//	public final void removeBan (final int banNumber)
//	{
//		bans.remove(banNumber);
//	}
//	
//	/**
//	 * Clears all the bans
//	 *
//	 */
//	public final void clearBans ()
//	{
//		bans.clear();
//	}
//	
//	/**
//	 * Checks if a ban exists
//	 * 
//	 * @param banMask
//	 * @return
//	 */
//	public final boolean banExists (String banMask)
//	{
//		banMask = banMask.toLowerCase();
//		
//		return bans.contains(banMask);
//	}
//	
//	/**
//	 * Sets an exclude mask
//	 * 
//	 * @param excludeMask
//	 */
//	public void addExclude (String excludeMask)
//	{
//		excludeMask = excludeMask.toLowerCase();
//		
//		excludes.add(excludeMask);
//	}
//	
//	/**
//	 * Returns all the excludes
//	 * 
//	 * @return
//	 */
//	public final String[] getExcludes ()
//	{
//		return excludes.toArray(new String[excludes.size()]);
//	}
//	
//	/**
//	 * Removes an Exclude mask
//	 * 
//	 * @param excludeMask
//	 */
//	public final void removeExclude (String excludeMask)
//	{
//		excludeMask = excludeMask.toLowerCase();
//		
//		excludes.remove(excludeMask);
//	}
//	
//	/**
//	 * Clears the exclude list
//	 */
//	public final void clearExcludes ()
//	{
//		excludes.clear();
//	}
//	
//	/**
//	 * Checks if an exclude mask is present
//	 * 
//	 * @param excludeMask
//	 * @return
//	 */
//	public final boolean excludeExists (String excludeMask)
//	{
//		excludeMask = excludeMask.toLowerCase();
//		
//		return excludes.contains(excludeMask);
//	}
	
	/**
	 * Sets whether or not the channel is ignored
	 * 
	 * @param ignored
	 */
	public final void setIgnored (final boolean ignored)
	{
		this.ignored = ignored;
	}
	
	/**
	 * Returns whether or not the channel is ignored
	 * 
	 * @return
	 */
	public final boolean isIgnored ()
	{
		return ignored;
	}
	
	/**
	 * Resets a channels permission
	 *
	 */
	public final void resetPermission ()
	{
		permission = 0;
		hasGottenPermission = false;
	}
	
	/**
	 * Checks if a channel has gotten its permission
	 * @return
	 */
	public final boolean hasGottenPermission ()
	{
		return hasGottenPermission;
	}
	
	/**
	 * Returns the channels permission
	 * @return
	 */
	public final int getPermission ()
	{
		return permission;
	}
	
	/**
	 * Fetches a channels permission
	 * @param network
	 */
	public final void fetchPermission (final String network)
	{
		hasGottenPermission = true;
		
	}
	
	/**
	 * Adds a user to this channel
	 * 
	 * @param user
	 */
	public final void addUser (final String user)
	{
		users.add(user.toLowerCase());
	}
	
	/**
	 * Returns users in an array
	 * 
	 * @return
	 */
	public final String[] getUsers ()
	{
		return users.toArray(new String[0]);
	}
	
	/**
	 * Checks if a user exists
	 * @param user
	 * @return
	 */
	public final boolean userExists (final String user)
	{
		return (users.contains(user.toLowerCase()));
	}
	
	/**
	 * Changes a users nick
	 * 
	 * @param user
	 * @param newUser
	 */
	public final void changeUser (final String user, final String newUser)
	{
		if (userExists(user) && (!userExists(newUser)))
		{
			users.remove(user);
			users.add(newUser);
		}
	}
	
	/**
	 * Removes a user from this channel instance
	 * 
	 * @param user
	 */
	public final void removeUser (final String user)
	{
		users.remove(user);
	}
}
