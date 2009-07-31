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
package org.geartech.rhinobot.support;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.text.SimpleDateFormat;

import java.util.Date;

/**
 * 
 * @author Chris
 * @version 1.1.0
 */
public final class Logger
{
	/**
	 * The Enum for the loglevels given
	 * 
	 * @author Chris
	 * @version 1.0.2
	 */
	public enum LogLevel
	{
		/**
		 * This level should only be called when it prevents the kernel from running.
		 * 
		 * Under rare circumstances this should be called as it will be the most significant
		 * in debugging. Most of the time, if the logger isn't instantiated for a certain class
		 * than it will use logCriticalError which should also be as severe as CRITICAL.
		 */
		CRITICAL(200),
		/**
		 * SEVERE is similar to CRITICAL in that it has high importance. Normally SEVERE
		 * should be used if the error prevents any way of running the application further.
		 */
		SEVERE(100),
		/**
		 * MAJOR is for events that should be fixed / considered, but the kernel will continue
		 * to run with expected conditions.
		 */
		MAJOR(50),
		/**
		 * MINOR is for events that should be noticed, and considered. These types of errors should
		 * never stop the kernel from running
		 */
		MINOR(20),
		/**
		 * INFO is for Informational messages
		 */
		INFO(10),
		/**
		 * For those who love verbose output, this should painfully detail the major steps
		 * (or even minor) of a modules actions so that they can be logged / studied.
		 */
		VERBOSE(5);
		
		/**
		 * The severity 
		 */
		private int severity;
		
		/**
		 * 
		 * @param severity
		 */
		private LogLevel (int severity)
		{
			this.severity = severity;
		}
		
		/**
		 * The severity of the error
		 * @return severity based on integer
		 */
		public int getLevel ()
		{
			return severity;
		}
		
		/**
		 * toString(), returns the name of a LogLevel event in lower case.
		 * @return 
		 */
		public String toString ()
		{
			return name().toLowerCase();
		}
		
		/**
		 * 
		 * @param string
		 * @return
		 */
		public static LogLevel parseString (String string)
		{
			string = string.toUpperCase().trim();
			
			if (string.equals("CRITICAL"))
			{
				return CRITICAL;
			}
			else if (string.equals("SEVERE"))
			{
				return SEVERE;
			}
			else if (string.equals("MAJOR"))
			{
				return MAJOR;
			}
			else if (string.equals("MINOR"))
			{
				return MINOR;
			}
			else if (string.equals("INFO"))
			{
				return INFO;
			}
			else if (string.equals("VERBOSE"))
			{
				return VERBOSE;
			}
			
			return null;
		}
	}

	private PrintWriter		writer;
	private String			directory;
	private String			file;
	private String			purpose;
	private boolean			closed			= false;
	private static boolean	logging			= true;
	private static boolean	verbose			= true;
	private boolean 		localVerbose	= verbose;
	private static int		minLevel		= 0;

	private static final SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss");
	
	/**
	 * Logger Constructor. creates a timestamp, and
	 * determines if the directory exists.
	 * @param directory
	 * @param className
	 */
	public Logger (final String directory, final String className)
	{
		this(directory, className, verbose);
	}
	
	/**
	 * Logger constructor, with verbose parameter.
	 * 
	 * @param directory
	 * @param className
	 * @param verbose
	 */
	public Logger (final String directory, final String className, final boolean verbose)
	{
		localVerbose			= verbose;
		this.purpose			= className;
		this.directory			= directory;
		SimpleDateFormat ssdf	= new SimpleDateFormat("MM-dd-yyyy");
		this.file				= className + "." + ssdf.format(new Date()) + ".log";
		File file				= new File(this.directory + File.separator + this.file);
		File dir				= new File(this.directory);
		
		if (!dir.exists())
		{
			Logger.criticalError("Logger", "Unable to init logger for class " + className + " because "
							+ "there is no directory '" + directory + "'", null);
		}

		if (!file.exists())
		{
			try
			{
				file.createNewFile();
			}
			catch (IOException e)
			{
				Logger.criticalError("Logger", "Unable to create file '" + file + "' for class " + className, e);
				return;
			}
		}
		
		try
		{
			writer = new PrintWriter(new FileWriter(file, true), true);
		}
		catch (IOException e)
		{
			Logger.criticalError("Logger", "Unable to create IO writer for class " + className, e);
		}
		
		write(LogLevel.VERBOSE, "Date:    "+ getDate());
		write(LogLevel.VERBOSE, "Class:   "+ className);
	}
	
	/**
	 * Gets the date
	 * @return the date
	 */
	private static final String getDate ()
	{
		return (sdf.format(new Date()));
	}
	
	/**
	 * Sets the minimum level of severity to be logged.
	 * 
	 * @param severity
	 */
	public static final void setMinLevel (final LogLevel severity)
	{
		int severityLevel = severity.getLevel();
		
		if (severityLevel > 20) { severityLevel = 20; }
		minLevel = severityLevel;
	}
	
	/**
	 * Sets the logging state.
	 * @param newLogging
	 */
	public static final void setLogging (final boolean newLogging)
	{
		logging = newLogging;
	}
	
	/**
	 * Sets the verbose of this class.
	 * @param verbose
	 */
	public static final void setVerbose (final boolean newVerbose)
	{
		verbose = newVerbose;
	}
	
	/**
	 * Writes a b
	 * @param level
	 * @param message
	 * @param throwable
	 */
	public final void write (final LogLevel level, final String message, final Throwable throwable)
	{
		if ((closed) || (level.getLevel() < minLevel))
		{
			return;
		}
		
		if ( (logging) && (writer != null) )
		{
			if (throwable != null)
			{
				throwable.printStackTrace(writer);
			}
			
			writer.println(getDate() + " " + level.toString().toUpperCase() + ": " + message);
		}
		
		if ((verbose) || (localVerbose))
		{
			if (throwable != null)
			{
				throwable.printStackTrace();
			}
			
			String line = purpose + ": " + getDate() + " " + level.toString().toUpperCase() + ":  " + message;
			
			if (level.getLevel() >= 50)
			{
				System.err.println(line);
			}
			else
			{
				System.out.println(line);
			}
		}
	}
	
	/**
	 * Writes to the logger, and flushes data.
	 * @param message
	 */
	public final void write (final LogLevel level, final String message)
	{
		write(level, message, null);
	}
	
	/**
	 * Closes the logger for use, after this it will ignore all data
	 * incoming.
	 */
	public final void close ()
	{
		if (writer != null)
		{
			writer.close();
			writer = null;
		}
			
		closed = true;
	}
	
	/**
	 * The enhanced way to report a critical error. This is writing a critical error with no exception.
	 * To write one with an exception, use criticalError(className, errorMessage, exception)
	 * 
	 * @param className
	 * @param errorMessage
	 */
	public static final void criticalError (final String className, final String errorMessage)
	{
		criticalError(className, errorMessage, null);
	}
	
	/**
	 * The enhanced way to report a critical error. This is writing a critical error with an exception.
	 * To write one with no exception, use criticalError(className, errorMessage)
	 * 
	 * @param className
	 * @param errorMessage
	 * @param throwable
	 */
	public static final void criticalError (final String className, final String errorMessage, final Throwable throwable)
	{
		String	fileName	= "critical.log";
		File	file		= new File(fileName);
		
		if (!file.exists())
		{
			try
			{
				file.createNewFile();
			}
			catch (IOException e)
			{
				System.err.println("-------------------------------------------------------");
				System.err.println("Unable to loggin critical error to default filename, trying another one.");
				e.printStackTrace();
				System.err.println("-------------------------------------------------------");
				return;
			}
		}
		
		try
		{
			PrintWriter writer = new PrintWriter(new FileWriter(file, true));
			
			String writeMessage = "A Critical Error was enountered: \r\n"
						+ "Class: " + className + "\r\n"
						+ "Date: " + getDate() + "\r\n"
						+ "Message: " + ((errorMessage != null) ? errorMessage : "none given") + "\r\n"
						+ "Stack Trace: \r\n";
			
			System.err.println("-------------------------------------------------------");
			System.err.println(writeMessage);
			writer.println(writeMessage);
			
			if (throwable != null)
			{
				throwable.printStackTrace();
				throwable.printStackTrace(writer);				
			}
			else
			{
				writer.println("No stack trace was provided for this error.");
				System.err.println("No stack trace was provided for this error.");
			}
			
			writer.println("---------------------------------------------\r\n");
			writer.flush();
			writer.close();
		}
		catch (IOException e)
		{
			System.err.println("-------------------------------------------------------");
			System.err.println("Unable to write to critical loggin file: " + e.getMessage());
			e.printStackTrace();
			System.err.println("-------------------------------------------------------");
		}
	}
}
