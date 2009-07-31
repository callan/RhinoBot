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
package org.kernel;

import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import java.util.ArrayList;
import java.util.HashMap;

import org.kernel.ConfigReader.UnableToParseException;
import org.kernel.Logger.LogLevel;

/**
 * This is a module kernel, which is supposed to allow a rather large amount of expansion
 * on any project this kernel exists in. Conjoining two projects together will be entirely
 * possible thanks to this kernel. This is basically a psuedo-kernel, in which it allows
 * panics, quits, comprehensive logging, and finally module support
 * 
 * @author Chris
 * @version 1.1.0
 */
public final class Kernel
{
	/**
	 * Directory where the files should be.
	 */
	private String										directory				= null;

	/**
	 * Config file
	 */
	private String										config					= null;

	/**
	 * Checks if init() has already been called.
	 */
	private boolean                                     initCalled              = false;
	
	/**
	 * Whether or not to load JAR files
	 */
	private boolean										loadJarModules			= true;

	/**
	 * A list of module names, just in case one of the other modules requires it as a
	 * dependancy
	 */
	private ArrayList<String>							moduleNames;
	
	/**
	 * This is the list of names that appear out of jar files.
	 */
	private ArrayList<String>							moduleJarNames;

	/**
	 * A list of modules already loaded into the Kernel
	 */
	private ArrayList<Module>							modules;
	
	/**
	 * ClassLoader to load modules
	 */
	private URLClassLoader								urlClassLoader			= null;

	/**
	 * This will return TRUE if you're allowed to load modules after startup
	 */
	private boolean										loadModulesAfterStartup	= false;

	/**
	 * This will return TRUE if you can unload modules
	 */
	private boolean										unloadableModules		= true;
	
	/**
	 * Logger
	 */
	private Logger										logger;
	
	/**
	 * ConfigReader
	 */
	private ConfigReader								reader;

	/**
	 * Instance of this kernel
	 */
	private static Kernel								kernelInstance;

	/**
	 * Prevents this class from being cloned
	 */
	protected final Object clone () throws CloneNotSupportedException
	{
		throw new CloneNotSupportedException();
	}

	/**
	 * Returns the one and only instance of the kernel
	 * 
	 * @return
	 */
	public static final Kernel getInstance ()
	{
		return kernelInstance;
	}

	/**
	 * Constructor
	 */
	private Kernel (final String[] args)
	{
		String arg;
		
		if (initCalled)
		{
			return;
		}
		
		if (modules == null)
		{
			modules = new ArrayList<Module>();
		}
		
		try
		{
			setModuleDirectory("./");
			setKernelConfig("kernel.conf");
		}
		catch (IOException e)
		{
			System.err.println(e);
		}
		
		Logger.setMinLevel(LogLevel.INFO);

		/*
		 * Set up the ConfigReader
		 */
		
		try
		{
			reader = new ConfigReader(config);
		}
		catch (IOException e)
		{
			panic("Kernel", "Unable to load ConfigReader: " + e.getMessage(), e);
			return;
		}
		catch (UnableToParseException e)
		{
			panic("Kernel", "Parse error in Kernel config: " + e.getMessage(), e);
			return;
		}
		
		if (reader.blockExists("kernel>logger"))
		{
			if (reader.settingExists("kernel>logger", "verbose"))
			{
				Logger.setVerbose(reader.getSetting("kernel>logger", "verbose").equalsIgnoreCase("true"));
			}
			
			if (reader.settingExists("kernel>logger", "loglevel"))
			{
				Logger.setMinLevel(LogLevel.parseString(reader.getSetting("kernel>logger", "loglevel")));
			}
		}
		
		if (reader.blockExists("kernel>module-settings"))
		{
			if (reader.settingExists("kernel>module-settings", "directory"))
			{
				try
				{
					setModuleDirectory(reader.getSetting("kernel>module-settings", "directory"));
				}
				catch (IOException e)
				{
					Logger.criticalError("Kernel", "setModuleDirectory could not set directory found in config", e);
				}
			}
		}
		
		if (args.length > 0)
		{
			for (int i = 0; i < args.length; i++)
			{
				arg = args[i].toLowerCase();
				
				if (arg.equals("-loglevel"))
				{
					if ((i + 1) == args.length)
					{
						System.err.println("Not enough arguments for loglevel.");
						return;
					}
					
					if (LogLevel.parseString(args[(i + 1)]) != null)
					{
						System.out.println("Using Log Level " + LogLevel.parseString(args[(i + 1)]));
						Logger.setMinLevel(LogLevel.parseString(args[(i + 1)]));
					}
					else
					{
						System.err.println("Invalid Log level -- switching to INFO");
						Logger.setMinLevel(LogLevel.INFO);
					}
					i++;
				}
				else if (arg.equals("-verbose"))
				{
					Logger.setVerbose(true);
				}
				else if (arg.equals("-no-verbose"))
				{
					Logger.setVerbose(false);
				}
				else if (arg.equals("-no-logging"))
				{
					Logger.setLogging(false);
				}
				else if (arg.equals("-logging"))
				{
					Logger.setLogging(true);
				}
				else if ((arg.equals("-kernel-conf")) || (arg.equals("-kc")))
				{
					if ((i + 1) == args.length)
					{
						System.err.println("Missing parameter for -kernel-conf");
						return;
					}
	
					try
					{
						setKernelConfig(args[(i + 1)]);
					}
					catch (IOException e)
					{
						System.err.println("Error setting kernel config: "+e.getMessage());
						return;
					}
					i++;
				}
				else if ((arg.equals("-help")) || (arg.equals("-h")) || (arg.equals("/?")))
				{
					System.out.print("Parameters:\r\n"
						+"\t-module-dir\t\tSets the module directory\r\n"
						+"\t-module-conf\t\tSets the module config\r\n"
						+"\t\t\t\tfor that instance\r\n"
						+"\r\n"
						+"\t-loglevel\t\tSets the log level for the logger. This is a universal setting for every app\r\n"
						+"\t\t\t\tthat does logging.\r\n"
						+"\t-verbose\t\tSets the verbose on (default). This is a universal setting like -loglevel\r\n"
						+"\t-no-verbose\t\tSets the verbose off. This is a universal setting, like -verbose\r\n"
						+"\r\n"
						+"\t-logging\t\tSets logging to files on (default). This is a universal setting, like -verbose\r\n"
						+"\t-no-logging\t\tSets loggin to files off. This is a universal setting, like -verbose\r\n"
						+"\r\n"
						+"\t-help\r\n"
						+"\t/?\r\n"
						+"\t-h\t\t\tThis help message"
						+"\r\n\r\n"
						+"\tCopyright (c) Chris Allan 2005. All rights reserved.\r\n"
					);
					return;
				}
				else
				{
					System.err.println("Invalid Parameter '" + arg + "'. Use the -help parameter for more info.");
					return;
				}
			}
		}
		else
		{
			/*
			 * Set up the ConfigReader
			 */
			try
			{
				reader = new ConfigReader(config);
			}
			catch (IOException e)
			{
				panic("Kernel", "Unable to load ConfigReader: " + e.getMessage(), e);
				return;
			}
			catch (UnableToParseException e)
			{
				panic("Kernel", "Parse error in Kernel config: " + e.getMessage(), e);
				return;
			}
		}
		
		/*
		 * Check for the required blocks.
		 */
		if (!reader.blockExists("kernel"))
		{
			panic("Kernel", "Kernel config requires block kernel");
			return;
		}
		
		if ( (!reader.blockExists("kernel>module-settings")) || (!reader.listExists("kernel>modules")) )
		{
			panic("Kernel", "Kernel config requires the following blocks: " +
					"module-settings, and the list modules");
			return;
		}
		
		final ArrayList<String> modules = reader.getList("kernel>modules"),
						jarModules = reader.getList("kernel>jar-modules");
		
		if (modules != null)
		{
			moduleNames = modules;
		}
		
		if (jarModules != null)
		{
			moduleJarNames = jarModules;
		}
		
		reader = null;
		
		try
		{
			/*
			 * This is separated from the constructor so I can pick up Panic's and
			 * Exceptions better.
			 */
			init();
		}
		catch (Panic e)
		{
			if (e.isModule())
			{
				panic(e.getModule().getModuleName(), e.getMessage(), e.getException());
			}
			else
			{
				panic(e.getLibraryName(), e.getMessage(), e.getException());
			}
		}
		catch (Exception e)
		{
			Logger.criticalError("Kernel", "Uncaught Exception: " + e.getMessage(), e);
		}
	}

	/**
	 * Initializes the kernel
	 */
	private final void init () throws Panic
	{
		if (initCalled)
		{
			return;
		}
		
		String logDir = "logs";
		
		if ((reader != null) && (reader.settingExists("kernel>logger", "directory")))
		{
			logDir = reader.getSetting("kernel>logger", "directory");
		}
		
		logger = new Logger(logDir, "Kernel");
		
		logger.write(LogLevel.VERBOSE, "Kernel has been instantiated");
		
		kernelInstance = this;
		
		logger.write(LogLevel.VERBOSE, "init() called");
		
		loadModules();
		
		if (loadJarModules)
		{
			loadJarModules();
		}
		
		initCalled = true;
	}

	/**
	 * Causes the kernel to panic and shut down completely.
	 * This should only be used in special cases like a primary
	 * module (A module you decide to be most important) is
	 * lacking something required and nothing else would (or
	 * should) run without it running.
	 * 
	 * @param className
	 * @param reason
	 */
	public final void panic (final String className, final String reason)
	{
		panic(className, reason, null);
	}

	/**
	 * Causes the kernel to panic, and shuts it down completely.
	 * 
	 * @param className
	 * @param reason
	 * @param stackTrace
	 */
	public final void panic (final String className, final String reason, final Throwable stackTrace)
	{
		if (logger != null)
		{
			logger.write(LogLevel.CRITICAL, reason);
		}
		
		if (stackTrace != null)
		{
			if (logger != null)
			{
				logger.write(LogLevel.CRITICAL, reason, stackTrace);
			}
			
			Logger.criticalError(className, reason, stackTrace);
		}
		else
		{
			if (logger != null)
			{
				logger.write(LogLevel.CRITICAL, reason);
			}
			
			Logger.criticalError(className, reason);
		}
		
		if (modules != null)
		{
			for (Module module : modules)
			{
				module.panic();
			}
		}
		
		try
		{
			Thread.sleep(2000);
		}
		catch (InterruptedException e)
		{
			System.exit(1);
		}
		
		System.exit(1);
	}

	/**
	 * Shuts the kernel down and all corresponding modules. Any module can make reference
	 * to this.
	 */
	public final void shutdown ()
	{
		logger.write(LogLevel.INFO, "Shutting Down...");
		
		for (Module module : modules)
		{
			module.quit();
		}
		
		System.exit(0);
	}

	/**
	 * Sets the module directory
	 * 
	 * @param newDirectory
	 * @throws IOException
	 */
	private final void setModuleDirectory (final String newDirectory) throws IOException
	{
		File file = new File(newDirectory);
		
		if (!file.isDirectory())
		{
			throw new IOException("Directory "+newDirectory+" is not a directory"); 
		}

		file      = null;
		directory = newDirectory;
	}

	/**
	 * Sets the config file
	 * 
	 * @param newFile
	 * @throws IOException
	 */
	private final void setKernelConfig (final String newFile) throws IOException
	{
		File file = new File(newFile);

		if (!file.canRead())
		{
			throw new IOException("File provided cannot be read");
		}
		else if ((!file.isFile()) && (file.exists()))
		{
			throw new IOException("File provided is not an actual file");
		}
		
		file   = null;
		config = newFile;
	}
	
	/**
	 * Loads a Module
	 * 
	 * @param moduleName
	 */
	public final boolean loadModule (final String moduleName, final ClassLoader loader)
	{
		if ((initCalled) && (!loadModulesAfterStartup)) { return false; }
		
		Module module = grabModule(moduleName, (URLClassLoader) loader);
		
		if ((module != null) && (checkDependencies(module)))
		{
			module.init();
			return true;
		}
		
		return false;
	}
	
	/**
	 * Loads modules
	 */
	private final void loadModules ()
	{
		logger.write(LogLevel.VERBOSE, "loadModules() called");
		
		try
		{
			urlClassLoader = new URLClassLoader(new URL[] { new File(directory).toURL() });
		}
		catch (MalformedURLException e)
		{
			panic("Kernel", "Error attempting to open directory '" + directory + "': " + e.getMessage(), e);
		}
		
		Module module = null;
		
		for (String moduleName : moduleNames)
		{
			module = grabModule(moduleName, urlClassLoader);
			logger.write(LogLevel.VERBOSE, "Grabbed module " + moduleName + " from out of thin air");
			
			if ((module != null) && (checkDependencies(module)))
			{
				logger.write(LogLevel.VERBOSE, "Initializing Module '" + module.getModuleName() + "'");
				modules.add(module);

				module.init();
				logger.write(LogLevel.VERBOSE, "Initialized Module '" + module.getModuleName() + "'");
			}
		}
	}
	
	/**
	 * Loads Jar Modules
	 */
	private final void loadJarModules ()
	{
		if (moduleJarNames == null)
		{
			return;
		}
		
		/*
		 * Get the files
		 */
		
		HashMap<String, URLClassLoader> tempClassLoaders = new HashMap<String, URLClassLoader>();
		
		URLClassLoader loader = null;
		Module         module = null;
		
		for (String moduleJarName : moduleJarNames)
		{
			String[] temp = moduleJarName.split("\\?");
			
			try
			{
				if (tempClassLoaders.containsKey(temp[0]))
				{
					loader = tempClassLoaders.get(temp[0]);
				}
				else
				{
					loader = new URLClassLoader(new URL[] { new File(temp[0]).toURL() });
					tempClassLoaders.put(temp[0], loader);
				}
			}
			catch (MalformedURLException e)
			{
				logger.write(LogLevel.SEVERE, "Error attempting to load jar modules for file " + temp[0] + ": MalformedURLException");
				return;
			}
			
			module = grabModule(temp[1], loader);
			
			if ((module != null) && (checkDependencies(module)))
			{
				logger.write(LogLevel.VERBOSE, "Initializing Module '" + module.getModuleName() + "'");
				modules.add(module);

				module.init();
				logger.write(LogLevel.VERBOSE, "Initialized Module '" + module.getModuleName() + "'");
			}
		}
		
		tempClassLoaders.clear();
		tempClassLoaders = null;
		loader = null;
	}
	
	/**
	 * Checks a module for dependencies
	 * @param module
	 * @return
	 */
	private final boolean checkDependencies (final Module module)
	{
		String[] dependencies = module.getDependencies();
		
		if ((dependencies == null) || (dependencies.length == 0))
		{
			return true;
		}
		
		boolean found = true;
		
		for (String dependency : dependencies)
		{
			logger.write(LogLevel.INFO, module.getModuleName() + ": Checking Dependency " + dependency);
			
			if (moduleNames.contains(dependency) || ((moduleJarNames != null) && (moduleJarNames.contains(dependency))))
			{
				logger.write(LogLevel.VERBOSE, module.getModuleName() + ": Dependency Found!");
			}
			else
			{
				logger.write(LogLevel.MAJOR, "Module " + module.getModuleName() + " is missing dependency " + dependency);
				found = false;
			}
		}
		
		if (found)
		{
			logger.write(LogLevel.INFO, "Found all dependencies for module " + module.getModuleName());
			return true;
		}
		else
		{
			logger.write(LogLevel.VERBOSE, "Module " + module.getModuleName() + " has missing dependencies.");
			return false;
		}
	}

	/**
	 * Grabs a module
	 * @param moduleName
	 * @param loader
	 * @return
	 */
	private final Module grabModule (final String moduleName, final URLClassLoader loader)
	{
		logger.write(LogLevel.VERBOSE, "Attempting to gram module " + moduleName + "...");
		
		Class   module       = null;
		Class[] interfaces   = null;
		Module  loadedModule = null;
		
		try
		{
			module        = loader.loadClass(moduleName);
			interfaces    = module.getInterfaces();
			loadedModule  = null;
			boolean found = false;
			
			for (Class tempInterface : interfaces)
			{
				if (tempInterface.toString().equals("interface org.kernel.Module"))
				{
					found = true;
					break;
				}
			}
			
			if (!found)
			{
				logger.write(LogLevel.MAJOR, "Module '" + moduleName + "' "
						+ "does not implement org.kernel.Module");
				return null;
			}
		}
		catch (ClassNotFoundException e)
		{
			logger.write(LogLevel.MAJOR, "Unable to find module '" + moduleName + "'", e);
			return null;
		}
		
		logger.write(LogLevel.VERBOSE, "Module seems to have correctly implemented org.kernel.Module");
		logger.write(LogLevel.VERBOSE, "Initializing...");
		
		try
		{
			loadedModule = (Module) module.newInstance();
		}
		catch (InstantiationException e)
		{
			logger.write(LogLevel.MAJOR, "Unable to load module: Instantiation Exception", e);
			return null;
		}
		catch (IllegalAccessException e)
		{
			logger.write(LogLevel.MAJOR, "Unable to load module: Illegal Access Exception", e);
			return null;
		}

		if (loadedModule == null)
		{
			logger.write(LogLevel.MAJOR, "Unexpected problem ocurred: Loaded module '" + moduleName + "' is null");
			return null;
		}
		
		return loadedModule;
	}
	
	/**
	 * Unloads a module
	 * 
	 * @param moduleNumber
	 */
	public final void unloadModule (final Module module)
	{
		if (!unloadableModules)
		{
			logger.write(LogLevel.MINOR, "Method unloadModule called when boolean unloadableModules is set to false");
			return;
		}
		
		module.quit();
		modules.remove(module);
		
		if (modules.size() == 0)
		{
			logger.write(LogLevel.INFO, "No modules currently exist or are loaded. Exiting.");
			System.exit(0);
		}
	}
	
	/**
	 * This is the interface a module uses to send information to another module.
	 * Unfortunately this interface is rather slow but will hopefully be updated soon
	 *
	 * @param from
	 * @param to
	 * @param <E> data
	 * @return if the data was sent or not
	 */
	public final <E> boolean sendData (final Module from, final String to, final E data)
	{
		if (from == null)
		{
			throw new IllegalArgumentException("From parameter cannot be null");
		}
		else if (to == null)
		{
			throw new IllegalArgumentException("to parameter cannot be null");
		}
		else if (data == null)
		{
			throw new IllegalArgumentException("data cannot be null");
		}
		
		logger.write(LogLevel.VERBOSE, "Attempting to send data from module " + from.getModuleName() + " to module " + to + "...");
		
		if (data instanceof Module)
		{
			logger.write(LogLevel.SEVERE, "Module " + from.getModuleName() + " attempted to send itself through sendData. " 
					+ "This action is not permitted.");
			return false;
		}
		
		for (Module module : modules)
		{
			if (module.getModuleName().equalsIgnoreCase(to))
			{
				module.<E>dataTransferEvent(this, from.getModuleName(), data);
				return true;
			}
		}
		
		return false;
	}

	/**
	 * Turns the kernel on.
	 * 
	 * @param args
	 */
	public static void main (final String[] args)
	{
		if (Kernel.getInstance() == null)
		{
			new Kernel(args);
		}
		else
		{
			Logger.criticalError("Kernel", "Attempted double-instancing of the kernel by using main()");
		}
	}
}
