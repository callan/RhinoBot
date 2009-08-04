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
package org.geartech.rhinobot.scripting.javascript;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Set;


/**
 * ModuleController for JavaScript libraries built in Java. For use with Rhino.
 */
public final class RhinoFactory
{
	/**
	 * The synchronized variant of HashMap, as this does implement Map.
	 */
	private static final Hashtable<String, Class<RhinoModule>> modules = new Hashtable<String, Class<RhinoModule>>();
	
	private RhinoFactory ()
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
	 * Reloads all modules currently loaded
	 */
	public static final synchronized void reloadAllModules ()
	{
		for (String moduleName : modules.keySet())
		{
			reloadModule(moduleName);
		}
	}
	
	/**
	 * Reloads a specific module
	 * 
	 * @param moduleName
	 */
	public static final synchronized void reloadModule (final String moduleName)
	{
		if (modules.containsKey(moduleName))
		{
			modules.remove(moduleName);
			loadModule(moduleName);
		}
	}
	
	/**
	 * Unloads a module (or at least attempts to)
	 */
	public static final synchronized boolean unloadModule (final String moduleName)
	{
		return (modules.remove(moduleName) != null);
	}
	
	/**
	 * Attempts to load a module
	 * 
	 * @param moduleName
	 */
	public static final synchronized Class<RhinoModule> loadModule (final String moduleName)
	{
		Class<RhinoModule> classModule = null;
		
		try
		{
// TODO loadModule			
//			classModule = ModuleController.findClass(moduleName);
		}
		catch (Exception e)
		{
			return null;
		}
		
		modules.put(moduleName, classModule);
		return classModule;
	}
	
	/**
	 * Attempts to load many modules
	 * 
	 * @param moduleNames
	 */
	public static final synchronized void loadModules (final ArrayList<String> moduleNames)
	{
		for (String moduleName : moduleNames)
		{
			loadModule(moduleName);
		}
	}
	
	public static final String[] getModuleNames ()
	{
		Set<String> set = modules.keySet();
		
		return set.toArray(new String[set.size()]);
	}
	
	public static final Class<RhinoModule> getModule (final String moduleName)
	{
		return modules.get(moduleName);
	}
	
	public static final Collection<Class<RhinoModule>> getModules ()
	{
		return modules.values();
	}
}
