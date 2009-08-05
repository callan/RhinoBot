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

import java.io.IOException;
import java.util.HashMap;

import org.geartech.rhinobot.modules.Module;
import org.geartech.rhinobot.support.DynamicLoadSupport;

/**
 * IrcModuleController is the module controller for IrcModule's in RhinoBot. This acts as a subclass
 * of ModuleController since it's primary focus is those modules which interface IrcModule.
 */
public final class ModuleFactory
{
	private static DynamicLoadSupport _loader; 

	static
	{
		try
		{
			_loader = new DynamicLoadSupport(new String[] { "modules", "." });
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * The hashmap of modules, the index is the module names, the value is the actual module
	 */
	private static HashMap<String, Module> _modules = new HashMap<String, Module>();
	
	private ModuleFactory () {}
	
	/**
	 * @see java.lang.Object#clone()
	 */
	public final Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}
	
	/**
	 * TODO Clean
	 * 
	 * @param className
	 * @return
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	public static Module loadModule (String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException
	{
		if (_modules.get(className) != null)
			return _modules.get(className);
		
		Class clazz = _loader.findClass(className);
		Module mod  = (Module) clazz.newInstance();
		
		if (clazz == null || mod == null)
			return null;
		
		return mod;
	}
}