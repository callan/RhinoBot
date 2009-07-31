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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.kernel.Kernel;
import org.kernel.Logger;
import org.kernel.Logger.LogLevel;
import org.rhinobot.bot.BotSocket;

public final class ModuleController
{
	/**
	 * URL Class Loader
	 */
	private static URLClassLoader loader;
	
	/**
	 * Logger!
	 */
	private static final Logger	  logger = new Logger("logs", "ModuleController");
	
	/**
	 * This loads up the static loader :)
	 */
	static
	{
		try
		{
			loader = new URLClassLoader(
						new URL[]{
							new File("modules").toURL(),
							new File("." + File.separator).toURL()
						}
					);
		}
		catch (MalformedURLException e)
		{
			Kernel.getInstance().panic("ModuleController", "Unable to statically load loader: " + e.getMessage(), e);
		}
	}
	
	private ModuleController ()
	{
		
	}
	
	/**
	 * @see java.lang.Object#clone()
	 */
	public Object clone() throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}
	
	/**
	 * Finds a class under two well defined paths
	 * 
	 * @param className
	 * @throws Exception
	 * @return Class
	 */
	static final Class findClass (String className) throws Exception
	{
		File[] dirs = new File[] { new File("modules"), new File("." + File.separator) };
		
		if (className.lastIndexOf('.') != -1)
		{
			// Attempt to find it first!
			try
			{
				return loader.loadClass(className);
			}
			catch (Exception e)
			{
			}
			// Guess it didn't work
			
			className  = className.substring(className.lastIndexOf('.') + 1);
		}
		
		File found = null;
		
		for (File directory : dirs)
		{
			File[] files = directory.listFiles();
			
			for (File file : files)
			{
				if ( (!file.isDirectory()) && (file.getName().equals(className + ".class")) )
				{
					found = file;
					break;
				}
			}
		}
		
		if (found != null)
		{
			BufferedReader reader = new BufferedReader(new FileReader(found));
			
			while (reader.ready())
			{
				String line = reader.readLine();
				
				if (line.startsWith("package"))
				{
					return loader.loadClass( line.substring(line.indexOf(" ")) + "." + className );
				}
			}
			
			return loader.loadClass(className);
		}
		
		return null;
	}
	
	/**
	 * Checks if cls has an interface by the name of interfaceName
	 * @param cls
	 * @param interfaceName
	 * @return
	 */
	static final boolean hasInterface (Class cls, String interfaceName)
	{
		Class[] interfaces = cls.getInterfaces();
		
		for (Class Interface : interfaces)
		{
			if (Interface.toString().equals("interface " + interfaceName))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Finds a socket module that interfaces BotSocket
	 * @param moduleName
	 * @return
	 */
	public static final BotSocket findSocketModule (String moduleName)
	{
		Class classModule = null;
		try
		{
			classModule = findClass(moduleName);
		}
		catch (Exception e)
		{
			logger.write(LogLevel.MINOR, "Exception while trying to find socket module '" + moduleName + "': " + e.getMessage());
		}
		
		if (classModule == null)
		{
			return null;
		}
		
		if (hasInterface(classModule, "org.rhinobot.bot.BotSocket"))
		{
			try
			{
				BotSocket socket = (BotSocket) classModule.newInstance();
				return socket;
			}
			catch (InstantiationException e)
			{
				return null;
			}
			catch (IllegalAccessException e)
			{
				return null;
			}
		}
		
		return null;
	}
}
