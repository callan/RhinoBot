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
package org.geartech.rhinobot.rhino;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import org.geartech.rhinobot.support.BotEvent;
import org.geartech.rhinobot.support.Logger;
import org.geartech.rhinobot.support.Logger.LogLevel;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.ErrorReporter;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.ScriptableObject;

/**
 * This is the controller for the rhino threads. Anything sent to the rhino threads should be sent through
 * this.
 */
public final class Rhino
{
	private final class RhinoErrorReporter implements ErrorReporter
	{
		/**
		 * @param message
		 * @param sourceName
		 * @param line
		 * @param lineSource
		 * @param lineOffset
		 */
		public final void warning (String message, String sourceName, int line, String lineSource, int lineOffset)
		{
			logger.write(LogLevel.INFO, "JavaScript Warning Caught: " + message + " in " + sourceName + " on line " + line
					+ ", line offset: " + lineOffset);
			logger.write(LogLevel.INFO, "Line Source: " + lineSource);
		}

		/**
		 * @param message
		 * @param sourceName
		 * @param line
		 * @param lineSource
		 * @param lineOffset
		 */
		public final void error (String message, String sourceName, int line, String lineSource, int lineOffset)
		{
			logger.write(LogLevel.MINOR, "JavaScript Error Caught: " + message + " in " + sourceName + " on line " + line
					+ ", line offset: " + lineOffset);
			logger.write(LogLevel.MINOR, "Line Source: " + lineSource);
		}

		/**
		 * @param message
		 * @param sourceName
		 * @param line
		 * @param lineSource
		 * @param lineOffset
		 */
		public final EvaluatorException runtimeError (String message, String sourceName, int line,
				String lineSource, int lineOffset)
		{
			logger.write(LogLevel.MAJOR, "JavaScript Runtime Error Caught: " + message + " in " + sourceName + " on line "
					+ line + ", line offset: " + lineOffset);
			logger.write(LogLevel.MAJOR, "Line Source: " + lineSource);
			return (EvaluatorException) new Exception();
		}
	}

	/**
	 * All the scripts the controller uses.
	 */
	private final Hashtable<BotEvent, RhinoScript>	scripts					= new Hashtable<BotEvent, RhinoScript>();

	/**
	 * Scripts Directory
	 */
	private String								scriptsDir					= "scripts";

	/**
	 * This is the ErrorReporter handled with every thread.
	 */
	private ErrorReporter						errorReporter;

	/**
	 * A scope used with every RhinoThread thread.
	 */
	private ScriptableObject					scope;
	
	/**
	 * Logger for everything
	 */
	Logger										logger						= new Logger("logs", "Rhino");

	/**
	 * Saves compiled scripts
	 */
	private boolean								saveCompiledScripts			= true;
	
	/**
	 * @see Rhino#getInstance();
	 */
	private static Rhino						rhino;
	
	/**
	 * Whether or not Rhino is enabled
	 */
	private static boolean						rhinoEnabled = false;

	/**
	 * Prevents class from being instantiated (well, in a way)
	 * 
	 * @return Rhino instance
	 */
	public static final Rhino getInstance ()
	{
		if ((rhino == null) && (rhinoEnabled == true))
		{
			rhino = new Rhino();
		}
		return rhino;
	}
	
	/**
	 * Checks if Rhino is enabled
	 * @return
	 */
	public static final boolean isEnabled ()
	{
		return rhinoEnabled;
	}
	
	/**
	 * Enables rhino
	 */
	protected static final void enableRhino ()
	{
		rhino			= new Rhino();
		rhinoEnabled	= true;
	}
	
	/**
	 * Disables rhino
	 */
	protected static final void disableRhino ()
	{
		rhino			= null;
		rhinoEnabled	= false;
	}

	/**
	 * Override of clone() from java.lang.Object
	 */
	public final Object clone () throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}

	/**
	 * Initializes Rhino. This not only makes every other method know the bot is initialized, but this
	 * initializes the array <code>rhinoInstances</code> for use with calling scripts. Also, this defines a
	 * standard scope for every rhino thread. This scope is sealed to prevent unknown (and dangerous) damage
	 * to the scope that may prevent the bot from working (unless restarted).
	 * 
	 * @see Rhino#getInstance();
	 */
	private Rhino ()
	{
		Context cx    = getContext();
		scope         = cx.initStandardObjects(null, true);
		errorReporter = new RhinoErrorReporter();
		
		cx.setErrorReporter(errorReporter);
		/*
		ConfigReader reader = null;
		
		try
		{
			reader = new ConfigReader("rhinobot.conf");
		}
		catch (UnableToParseException e1)
		{
			logger.write(LogLevel.MINOR, "Unable to parse rhinobot.conf: " + e1.getMessage());
		}
		catch (IOException e1)
		{
			logger.write(LogLevel.MINOR, "Unable to load rhinobot.conf: " + e1.getMessage());
		}
		
		try
		{
			ScriptableObject.defineClass(scope, JSNewFile.class);
			ScriptableObject.defineClass(scope, JSConfigReader.class);
			ScriptableObject.defineClass(scope, JSCron.class);
			ScriptableObject.defineClass(scope, JSCronScript.class);
			ScriptableObject.defineClass(scope, JSInstancer.class);
			ScriptableObject.defineClass(scope, JSRhino.class);
			ScriptableObject.defineClass(scope, JSDatabase.class);
			ScriptableObject.defineClass(scope, XMLHttpRequest.class);
		}
		catch (Exception e)
		{
			logger.write(LogLevel.SEVERE, "Unable to load one or more of the main js library files: " + e.getMessage(), e);
			disableRhino();
			return;
		}
		*/
		Object reader = null;
		
		// Load custom libraries TODO TODO TODO
		if ((reader != null) && true
//			(reader.listExists("rhinobot>rhino-modules")))
				)
		{
//			RhinoModuleController.loadModules(reader.getList("rhinobot>rhino-modules"));
			
			for (Class module : RhinoModuleController.getModules())
			{
				try
				{
					ScriptableObject.defineClass(scope, module);
				}
				catch (IllegalAccessException e)
				{
					logger.write(LogLevel.MAJOR, "Unable to load module " + module.toString() + ": " + e.getMessage(), e);
				}
				catch (InstantiationException e)
				{
					logger.write(LogLevel.MAJOR, "Unable to load module " + module.toString() + ": " + e.getMessage(), e);
				}
				catch (InvocationTargetException e)
				{
					logger.write(LogLevel.MAJOR, "Unable to load module " + module.toString() + ": " + e.getMessage(), e);
				}
				
				module = null;
			}
		}
	}
	
	/**
	 * Attempts to load a module into Rhino.
	 * 
	 * @param moduleName
	 * @throws ModuleLoadException if something went wrong.
	 */
	public synchronized final void loadModule (final String moduleName) throws Exception
	{
		Class module = RhinoModuleController.loadModule(moduleName);
		
		if (module == null)
		{
			throw new Exception("Module cannot be found or does not exist.");
		}
		
		try
		{
			ScriptableObject.defineClass(scope, module);
		}
		catch (Exception e)
		{
			throw new Exception("Unable to load module into Rhino: " + e.getMessage());
		}
	}
	
	/**
	 * Attempts to delete moduleName from the scope. This will not allow the unloading
	 * of any main library classes.
	 * 
	 * @param moduleName
	 */
	public synchronized final void unloadModule (final String moduleName)
	{
		if (RhinoModuleController.getModule(moduleName) != null)
		{
			RhinoModuleController.unloadModule(moduleName);
			scope.delete(moduleName);
		}
	}
	
	/**
	 * Gets the context
	 * @return
	 */
	private final Context getContext ()
	{
		Context cx = Context.getCurrentContext();
		
		if (cx == null)
		{
			cx = Context.enter();
		}
		
		return cx;
	}
	
	/**
	 * Runs a script.
	 * @param event
	 * @param parameters
	 */
	public final synchronized void runScript (final BotEvent event, Object[] parameters, final Object[] basicInfo)
	{
		if (event == BotEvent.cron)
		{
			return;
		}
		
		if (parameters == null)
		{
			parameters = new Object[0];
		}
		
		if (event.getParamCount() != parameters.length)
		{
			logger.write(LogLevel.INFO, "Event " + event.toString() + " failed parameter count check!");
			return;
		}
		
		getScript(event);
		
		if (scripts.get(event).getScript() == null)
		{
			return;
		}
		
		RhinoScript rScript = scripts.get(event);
		Script		script  = rScript.getScript();
		
		Context context = getContext();
		
		context.setErrorReporter(errorReporter);
		
		script.exec(context, scope);
		
		try
		{
			if ((scope.has("basicInformation", scope)) && (basicInfo != null))
			{
				Function basic   = (Function) scope.get("basicInformation", scope);
				basic.call(context, scope, scope, basicInfo);
				logger.write(LogLevel.VERBOSE, "Called function basicInformation");
			}
			else
			{
				logger.write(LogLevel.MINOR, "basicInformation function not found");
			}
			
			if (scope.has(event.toString(), scope))
			{
				Function method  = (Function) scope.get(event.toString(), scope);
				method.call(context, scope, scope, parameters);
				logger.write(LogLevel.VERBOSE, "Called function " + event.toString());
			}
			else
			{
				logger.write(LogLevel.MINOR, "Function " + event.toString() + " not found in script " + rScript.getEventName());
			}
		}
		catch (ClassCastException e)
		{
			logger.write(LogLevel.MINOR, "Unable to convert object(s) within " + rScript.getEventName() + " to Function class: " + e.getMessage(), e);
			getScript(event);
		}
		catch (EcmaError e)
		{
			logger.write(LogLevel.MINOR, "JS Error running script on" + rScript.getEventName() + ": " + e.getMessage());
			getScript(event);
		}
		catch (Exception e)
		{
			logger.write(LogLevel.MINOR, "Exception running script on" + rScript.getEventName() + ": " + e.getMessage(), e);
			getScript(event);
		}
	}
	
	/**
	 * Runs a cron script
	 * @param script
	 */
	final synchronized void runCronScript (final CronScript script)
	{
		Context cx = getContext();
		
		cx.setErrorReporter(errorReporter);
		
		script.getScript().exec(cx, scope);
	}
	
	/**
	 * Creates a test case for execution
	 * @param code
	 * @param name
	 * @param parameters
	 */
	@SuppressWarnings("unused")
	private final void executeTest (final String code, final String name, final Object[] parameters)
	{
		Context context = getContext();
		
		ScriptableObject scope = context.initStandardObjects();
		
		
		Script script = context.compileString(code, name, 0, null);
		
		script.exec(context, scope);
		Object something = scope.get("test", scope);
	    Function f = (Function) something;
	    Object result = f.call(context, scope, scope, parameters);
	    String report = "test('my args') = " + Context.toString(result);
	    System.out.println(report);
	}

	/**
	 * Reloads the scripts that are currently loaded.
	 */
	public final synchronized void reloadScripts ()
	{
		logger.write(LogLevel.INFO, "Reloading Scripts...");
		
		Enumeration<BotEvent>	keys  = scripts.keys();
		BotEvent				event = null;
		
		while (keys.hasMoreElements())
		{
			event = keys.nextElement();
			
			logger.write(LogLevel.VERBOSE, "Found request for event " + event.toString());
			
			scripts.remove(event);
			
			getScript(event);
		}
		
		logger.write(LogLevel.VERBOSE, "Scripts reloaded");
	}
	
	/**
	 * Compiles a script
	 * @param event
	 * @param source
	 * @return
	 */
	static final Script compileScript (final BotEvent event, final String source)
	{
		return compileScript(event.toString(), source);
	}
	
	/**
	 * Compiles a script
	 * @param source
	 * @return
	 */
	static final Script compileScript (final String source)
	{
		return compileScript("", source);
	}
	
	/**
	 * Compiles a script
	 * @param name
	 * @param source
	 * @return
	 */
	static final synchronized Script compileScript (final String name, final String source)
	{
		if (source == null) { return null; }
		
		Rhino rhino = Rhino.getInstance();
		Context cx  = rhino.getContext();
		
		Script script = cx.compileString(source, name, 0, null);
		
		return script;
	}
	
	/**
	 * Load Compiled Script
	 * @param event
	 * @return TRUE if the compiled script was loaded, or FALSE if it wasn't
	 */
	private final boolean loadCompiledScript (final BotEvent event)
	{
		if (scripts.containsKey(event))
			return true;
		
		File file = new File( scriptsDir + File.separator + "compiled" + File.separator + event.toString() + ".cjs");
		
		if (!file.exists())
			return false;
		
		ObjectInputStream in = null;
		
		try
		{
			in = new ObjectInputStream(new FileInputStream(file));
		}
		catch (IOException e)
		{
			return false;
		}
		
		try
		{
			RhinoScript script = (RhinoScript) in.readObject();
			scripts.put(event, script);
		}
		catch (IOException e)
		{
			return false;
		}
		catch (ClassNotFoundException e)
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Saves a compiled script
	 * @param event
	 */
	private final void saveCompiledScript (final BotEvent event)
	{
		if (!scripts.containsKey(event))
		{
			return;
		}

		File file = new File( scriptsDir + File.separator + "compiled" + File.separator + event.toString() + ".cjs");
		
		if (!file.exists())
		{
			try
			{
				if (!file.createNewFile())
				{
					logger.write(LogLevel.INFO, "Unable to create file " + file.getPath());
					return;
				}
			}
			catch (IOException e)
			{
				logger.write(LogLevel.MINOR, "Error attempting to create " 
						+ file.getPath() + ": " + e.getMessage(), e);
				return;
			}
		}
		
		ObjectOutputStream out = null;
		
		try
		{
			out = new ObjectOutputStream(new FileOutputStream(file));
		}
		catch (IOException e)
		{
			logger.write(LogLevel.MINOR, "Error attempting to open up file socket to file " 
					+ file.getPath() + ": " + e.getMessage(), e);
			return;
		}
		
		try
		{
			out.writeObject(scripts.get(event));
		}
		catch (IOException e)
		{
			logger.write(LogLevel.MINOR, "Error attempting to write script to file " 
					+ file.getPath() + ": " + e.getMessage(), e);
			return;
		}
		
		try
		{
			out.close();
		}
		catch (IOException e)
		{
			logger.write(LogLevel.MINOR, "Error attempting to close file socket for file " 
					+ file.getPath() + ": " + e.getMessage(), e);
		}
	}

	/**
	 * Get's a request for a specific event and loads it into it's associated variable
	 * 
	 * @param event
	 */
	private final void getScript (final BotEvent event)
	{
		if (scripts.containsKey(event))
		{
			return;
		}
		else if (saveCompiledScripts)
		{
			if (loadCompiledScript(event))
			{
				return;
			}
		}

		String	fileName	= event.toString() + ".js",
				data		= "",
				file		= scriptsDir + File.separator + fileName;
		
		File	script		= new File(file);
		
		if (script.canRead())
		{
			try
			{
				BufferedReader in = new BufferedReader(new FileReader(file));
				
				String temp;
				
				while (in.ready())
				{
					temp = in.readLine();
					
					if (temp.toLowerCase().startsWith("// include "))
					{
						temp = getInclude(temp.substring(11));
					}

					if (temp != null)
					{
						data += temp + "\r\n";
					}
				}
				
				in.close();
			}
			catch (IOException e)
			{
				logger.write(LogLevel.MINOR, "Unable to load include '" + file + "': " + e.getMessage());
			}
			
			logger.write(LogLevel.INFO, "Script "+file+" loaded");
		}
		else if ((script.exists()) && (!script.canRead()))
		{
			logger.write(LogLevel.MINOR, "Script " + file + " cannot be read.");
			scripts.put(event, new RhinoScript(null, event.toString()));
			return;
		}
		else
		{
			logger.write(LogLevel.MINOR, "Script " + file + " does not exist.");
			scripts.put(event, new RhinoScript(null, event.toString()));
			return;
		}
		
		scripts.put(event, new RhinoScript(data, event.toString()));
		
		if (saveCompiledScripts)
			saveCompiledScript(event);
	}
	
	/**
	 * getInclude used at the base, by getScript
	 * @param file
	 * @return
	 */
	private final String getInclude (final String file)
	{
		return getInclude(file, 0);
	}
	
	/**
	 * Gets an included file from the main event file loaded by getScript().
	 * 
	 * @param file
	 * @return
	 */
	private final String getInclude (String file, int level)
	{
		if (level > 3)
		{
			return "";
		}
		
		file = scriptsDir + File.separator + "includes" + File.separator + file;
		
		File   include	= new File(file);
		String data		= "";
		
		if (include.canRead())
		{
			try
			{
				BufferedReader in = new BufferedReader(new FileReader(file));
				
				String temp;
				
				while (in.ready())
				{
					temp = in.readLine();
					if (temp.startsWith("// include "))
					{
						temp = getInclude(temp.substring(11), ++level);
					}
					
					if (temp != null)
					{
						data += temp + "\r\n";
					}
				}
				in.close();
			}
			catch (IOException e)
			{
				logger.write(LogLevel.MINOR, "Error getting include file "+file+": "+e);
			}
		}
		return data;
	}
}
