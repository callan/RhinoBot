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

import java.util.HashMap;

/**
 * 
 */
class Bootstrap
{
	private HashMap<String, String> _arguments = new HashMap<String, String>();
	
	/**
	 * @param args
	 */
	public static void main (String[] args)
	{
		new Bootstrap(args);
	}
	
	Bootstrap (String[] args)
	{
		parseArguments(args);

		String configName = "config.json";
		
		if (getArgument("--config") != null)
			configName = getArgument("--config");
		
		try
		{
			CoreFactory.useConfig(configName);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			System.exit(-1);
		}
	}
	
	private String getArgument (String arg)
	{
		return _arguments.get(arg);
	}
	
	private void parseArguments (String[] args)
	{
		for (int i = 0; i != args.length; i++)
		{
			if (!isValid(args[i]))
				continue;
			
			if (hasNext(args[i]) && ((i + 1) < args.length))
				_arguments.put(args[i], args[i + 1]);
			else
				_arguments.put(args[i], "true");
		}
	}
	
	private boolean isValid (String argument)
	{
		return argument.equals("--debug")  ||
			   argument.equals("--help")   ||
			   argument.equals("-v")	   ||
			   argument.equals("--config");
	}
	
	private boolean hasNext (String argument)
	{
		return argument.equals("--config");
	}
}
