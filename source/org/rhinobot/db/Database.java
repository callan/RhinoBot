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

import org.rhinobot.bot.manager.User;

/**
 * Interface for database modules
 */
public interface Database
{
	/**
	 * Connects to a MySQL server
	 * 
	 * @param hostname
	 * @param username
	 * @param password
	 * @param database
	 */
	public void connect (final String hostname, final String username, final String password, final String database)
			throws DatabaseException;

	/**
	 * Adds a user blah blah blah
	 * @param User
	 * @param permission
	 * @return The users permission
	 */
	public boolean addUserPermission (final User user, final int permission) throws DatabaseException;

	/**
	 * User-class wrapper for changeUserPermission
	 * 
	 * @param user
	 * @param newPermission
	 * @return TRUE on success
	 */
	public boolean changeUserPermission (final User user, final int newPermission) throws DatabaseException;

	/**
	 * Changes a users permission
	 * @param userNick
	 * @param userIdent
	 * @param userHostmask
	 * @param newPermission
	 * @return TRUE on success
	 */
	public boolean changeUserPermission (final String userNick, final String userIdent, final String userHostmask,
			final int newPermission) throws DatabaseException;

	/**
	 * Gets user permission from the database and returns it to the required function.
	 * @param userNick
	 * @param userIdent
	 * @param userHostmask
	 * @return The users permission
	 */
	public int getUserPermission (final String userNick, final String userIdent, final String userHostmask)
				throws DatabaseException;

	/**
	 * Adds a channels permission
	 * @param channel
	 * @param network
	 * @param permission
	 * @return TRUE on success
	 */
	public boolean addChannelPermission (final String channel, final String network, final int permission)
				throws DatabaseException;
	
	/**
	 * Changes a channels permission
	 * @param channel
	 * @param network
	 * @param newPermission
	 * @return TRUE on success
	 */
	public boolean changeChannelPermission (final String channel, final String network, final int newPermission)
				throws DatabaseException;
	
	/**
	 * Gets a channels permission from the database.
	 * @param network
	 * @param channel
	 * @return The Channel Permission
	 */
	public int getChannelPermission (final String network, final String channel) throws DatabaseException;
	
	/**
	 * Adds an ignore entry to the database
	 * @param user
	 * @return TRUE on success
	 */
	public boolean addIgnoreEntry (final User user) throws DatabaseException;
	
	/**
	 * Removes an ignore entry from the database
	 * @param user
	 * @return TRUE on success
	 */
	public boolean removeIgnoreEntry (final User user) throws DatabaseException;
	
	/**
	 * Gets the ignore list from the database
	 * @return An array of users on the ignore list
	 */
	public User[] getIgnoreList () throws DatabaseException;
	
	/**
	 * Get's a long variable
	 * @param varName
	 * @param event
	 * @return A long variable (js use)
	 */
	public String getLongVar (final String varName, final String event) throws DatabaseException;

	/**
	 * Gets a JSVar from the database if it exists
	 * @param varName
	 * @return the variable value, or "undefined" if its not defined
	 */
	public String getVar (final String varName, final String event) throws DatabaseException;

	/**
	 * Sets a variable
	 * @param varName
	 * @param varValue
	 */
	public void setLongVar (final String varName, final String event, final String varValue) throws DatabaseException;

	/**
	 * Sets a variable
	 * @param varName
	 * @param varValue
	 */
	public void setVar (final String varName, final String event, final String varValue) throws DatabaseException;

	/**
	 * Deletes a variable
	 * @param varName
	 */
	public void delVar (final String varName, final String event) throws DatabaseException;

	/**
	 * Deletes a long variable
	 * @param varName
	 */
	public void delLongVar (final String varName, final String event) throws DatabaseException;

	/**
	 * Checks if the Database is connected.
	 * @return TRUE if the Database is connected
	 */
	public boolean isConnected () throws DatabaseException;

	/**
	 * Disconnects from the Database.
	 */
	public void disconnect () throws DatabaseException;
}
