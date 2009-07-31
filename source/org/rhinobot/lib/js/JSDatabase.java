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
package org.rhinobot.lib.js;

import org.mozilla.javascript.ScriptableObject;

import org.rhinobot.db.DatabaseException;
import org.rhinobot.module.DatabaseModuleController;

/**
 * Integrates the current database into JavaScript
 */
public final class JSDatabase extends ScriptableObject
{
	/**
	 * Instance of MySQL
	 */
	private DatabaseModuleController controller = DatabaseModuleController.getInstance();

	/**
	 * Empty Constructor is used in implementation
	 * for Rhino
	 */
	public JSDatabase () {}
	
	/**
	 * Used with Rhino's implementation
	 * Returns the class name, "MySQL"
	 */
	public String getClassName () { return "Database"; }
	
	/**
	 * Gets a LONG JS Variable from the MySQL database
	 * @param name
	 * @param event
	 * @return
	 */
	public String jsFunction_getLongVar (String name, String event) throws DatabaseException
	{
		return controller.getLongVar(name, event);
	}
	
	/**
	 * Gets a JS Variable from the MySQL database
	 * @param name
	 * @return
	 */
	public String jsFunction_getVar (String name, String event) throws DatabaseException
	{
		return controller.getVar(name, event);
	}
	
	/**
	 * Sets a long JS variable
	 * @param name
	 * @param event
	 * @param value
	 */
	public void jsFunction_setLongVar (String name, String event, String value) throws DatabaseException
	{
		controller.setLongVar(name, event, value);
	}
	
	/**
	 * Sends a JS variable in the MySQL database
	 * @param name
	 * @param value
	 */
	public void jsFunction_setVar (String name, String event, String value) throws DatabaseException
	{
		controller.setVar(name, event, value);
	}
	
	/**
	 * Deletes a LONG variable
	 * @param name
	 * @param event
	 */
	public void jsFunction_delLongVar (String name, String event) throws DatabaseException
	{
		controller.delLongVar(name, event);
	}
	
	/**
	 * Deletes a JS Variable
	 * @param name
	 * @param event
	 */
	public void jsFunction_delVar (String name, String event) throws DatabaseException
	{
		controller.delVar(name, event);
	}
}
