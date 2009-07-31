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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * @author Chris
 * @version 2.2.0dev
 * 
 * Changes of 2.2.0:
 * 	- Integrated UnableToParseException
 * 	- Created new private class NewConfigParser
 * 	- Created a new standard for the new config parser
 * 
 * Changes of 2.0.2:
 *   - Moved to Kernel branch. The kernel should use this for further config reading.
 *   - Plans are set in place for the kernel to use this, but they will not be implemented.
 * 
 * Changes of 2.0.1:
 *   - Added method settingExists
 *   - Redid method getSetting (just the internals)
 * 
 * Changes of 2.0.0:
 *   - Redid the versioning scheme
 *   - getConfig() now doesn't automatically call parseConfig
 *   - Improved the loop which runs through the raw config
 *   - Added documentation to the rest of the stuff
 * 
 * Changes of 2.0:
 *   - Redid it :D!
 * 
 * Changes of 1.1:
 *   - Removed support for "FileType" keyword. It will now belong in Misc.
 *   - Removed method "returnFile"
 */
public final class ConfigReader
{
	/**
	 * The ConfigReader's basic exception thingy for people fucking configs up >:|
	 * 
	 * @author Chris
	 */
	public static class ConfigException extends Exception
	{
		public ConfigException (final String detail)
		{
			super(detail);
		}
	}
	
	/**
	 * The ConfigReader exception for being unable to parse the config well or not at all
	 * 
	 * @author Chris
	 */
	public static final class UnableToParseException extends ConfigException
	{
		public UnableToParseException (final String detail)
		{
			super(detail);
		}
	}
	
	/**
	 * This is the overlay class which sets up for the new config format
	 * @author Chris
	 *
	 */
	private final class NewConfigParser
	{
		/**
		 * Lists
		 */
		private final HashMap<String, ArrayList<String>>			lists		= new HashMap<String, ArrayList<String>>();
		
		/**
		 * Settings
		 */
		private final HashMap<String, HashMap<String, String>>	settings	= new HashMap<String, HashMap<String, String>>();

		/**
		 * Default constructor. No findings will be pritned at the end
		 * 
		 * @param rawConfig
		 * @throws UnableToParseException
		 */
		private NewConfigParser (final String rawConfig) throws UnableToParseException
		{
			this(rawConfig, false);
		}
		
		/**
		 * Alternative constructor.
		 * 
		 * @param rawConfig
		 * @param printFindings
		 * @throws UnableToParseException
		 */
		private NewConfigParser (final String rawConfig, final boolean printFindings) throws UnableToParseException
		{
			/*
			 * Block level
			 */
			int level = 0;
			
			/*
			 * Delimiters, these are exceptionally useful for keeping
			 * placeholders when going through the config
			 */
			int[] delim  = new int[] { 0 },
				  delim2 = new int[] { 0 };
			
			/*
			 * Block and List names
			 */
			String[] blockName = new String[] { "" },
					 listName  = new String[] { "" };

			/*
			 * Start Block, List and end Block List locations
			 */
			int[] startBlock = new int[] { -1 },
				  endBlock   = new int[] { -1 },
				  startList  = new int[] { -1 },
				  endList    = new int[] { -1 };
			
			/*
			 * Comment locations, used only when removing comments.
			 */
			int	  startComment = -1,
				  endComment   = -1,
				  lines        = 0;
			
			/*
			 * Used to check if we're in a block / list
			 */
			boolean[] inBlock = new boolean[] { false },
					  inList  = new boolean[] { false };
			
			/*
			 * inComment is used specifically in the comment remover, while
			 * resizeUp and resizeDown are for incrementing and decrementing
			 * the level, respectively.
			 */
			boolean	inComment  = false,
					resizeUp   = false,
					resizeDown = false;
			
			/*
			 * Remove Windows styles, and OS X style newlines and replace with linux
			 */
			String config = rawConfig.replace("\r\n", "\n").
									  replace("\r", "\n").
									  replace("\t", "");
			
			/*
			 * Make the config a char array
			 */
			char[] conf   = config.toCharArray();
			
			/*
			 * This removes the comments before we even have to deal with them! 
			 */
			for (int i = 0; i < conf.length; i++)
			{
				/*
				 * Create a block of two characters, overlapping the previous character, e.g.
				 * char[] conf = new char[] { 'a', 'b', 'c', 'd' };
				 * createBlock(conf, 0, 2, conf.length); << ab
				 * createBlock(conf, 1, 3, conf.length); << bc
				 */
				String commBlock = createBlock(conf, i, 2, conf.length);

				/*
				 * Start of comment
				 */
				if (commBlock.equals("/*"))
				{
					inComment		= true;
					startComment	= i;
				}
				/*
				 * End of comment
				 */
				else if (commBlock.equals("*/"))
				{
					if (!inComment)
					{
						throw new UnableToParseException("End comment */ found, but no start comment was found");
					}
					
					inComment	= false;
					endComment	= (i + 1);
					
					for (int a = startComment; a <= endComment; a++)
					{
						if (conf[a] == '\n')
						{
							lines++;
						}
						
						conf[a] = '\0';
					}
				}
			}
			
			/*
			 * We're done with comments!
			 */
			startComment = endComment = -1;
			
			/*
			 * String it, since the null characters can be easily stripped.
			 */
			conf = new String(conf).replace("\0", "").
									replace("~new", "").
									replace(" ", "").
									toCharArray();
			
			/*
			 * temp command and values
			 */
			
			String[] tmpCommand	 = new String[1],
					 tmpValue	 = new String[1];
		
			/*
			 * Main iteration through the config.
			 */
			for (int i = 0; i < conf.length; i++)
			{
				if (resizeUp)
				{
					level++;
				}
					
				if (resizeDown)
				{
					level--; 
				}
				resizeUp	= false;
				resizeDown	= false;
				
				if (level == delim.length)
				{
					delim		= resize(delim, level + 1);
					delim2		= resize(delim2, level + 1);
					
					startBlock	= resize(startBlock, level + 1);
					endBlock	= resize(endBlock, level + 1);
					startList	= resize(startList, level + 1);
					endList		= resize(endList, level + 1);
					
					inBlock		= resize(inBlock, level + 1);
					inList		= resize(inList, level + 1);
					
					blockName	= resize(blockName, level + 1);
					listName	= resize(listName, level + 1);
					
					tmpCommand	= resize(tmpCommand, level + 1);
					tmpValue	= resize(tmpValue, level + 1);
				}

				char chr = conf[i];
				
				if (chr == '\n')
				{
					lines++;
				}
				else if (chr == '>')
				{
					throw new UnableToParseException("Invalid character > found on line " + ++lines);
				}
				else if (chr == '@')
				{
					inBlock[level]		= true;
					startBlock[level]	= i;
					delim[level]		= i;
				}
				else if (chr == '*')
				{
					inList[level]		= true;
					startList[level]	= i;
					delim2[level]		= i;
				}
				else if (chr == '{')
				{
					if (inList[level])
					{
						String temp = strip(new String(conf, startList[level] + 1, i - 1 - startList[level]));
						temp = createHeirarchy(blockName, temp, level);

						if ((temp != null) && (lists.containsKey(temp)))
						{
							throw new UnableToParseException("Cannot have two lists named the same on line " + lines);
						}
						
						listName[level] = temp;
						
						lists.put(listName[level], new ArrayList<String>());
						
						delim2[level]	= i;
						resizeUp		= true;
					}
					else if (inBlock[level])
					{
						String temp = strip(new String(conf, startBlock[level] + 1, i - 1 - startBlock[level]));
						
						temp = createHeirarchy(blockName, temp, level);
						if ((temp != null) && (settings.containsKey(temp)))
						{
							throw new UnableToParseException("Cannot have two blocks named the same on line " + ++lines);
						}
						
						blockName[level] = temp;
						
						settings.put(blockName[level], new HashMap<String, String>());
						delim[level] = i;
						resizeUp = true;
					}
				}
				else if (chr == '}')
				{
					if (level - 1 > -1)
					{
						level--;
					}
					
					if (inList[level])
					{
						inList[level]		= false;
						startList[level]	= endList[level] = -1;
						listName[level]		= "";
						delim2[level]		= i;
					}
					else if (inBlock[level])
					{
						inBlock[level]		= false;
						startBlock[level]	= endBlock[level] = -1;
						blockName[level]	= "";
						delim[level]		= i;
					}
					
					/*
					 * This code points the delimiter to the end of the } after a block has been complete
					 */
					if (level - 1 > -1)
					{
						level--;
						if (inList[level])
						{
							delim2[level] = i;
						}
						else if (inBlock[level])
						{
							delim[level] = i;
						}
						level++;
					}
				}
				else if (chr == ':')
				{
					level--;
					if (inBlock[level])
					{
						tmpCommand[level] = strip(new String(conf, delim[level] + 1, (i - 1) - delim[level]));
						delim[level]      = i;
					}
					level++;
				}
				else if (chr == ';')
				{
					if ( (new String(conf, delim[level], i - delim[level]).equals("}")) ||
						 (new String(conf, delim2[level], i - delim2[level]).equals("}")) )
					{
						if (level - 1 > -1)
						{
							level--;
						}
						
						if (inBlock[level])
						{
							delim[level] = i;
						}
						else if (inList[level])
						{
							delim2[level] = i;
						}
						
						if (level + 1 > 0)
						{
							level++;
						}
					}
					else
					{
						level--;
						if (inBlock[level])
						{
							if (tmpCommand[level] == null)
							{
								throw new UnableToParseException("No command given to value in level " + level + " on line " + lines);
							}
							
							tmpValue[level] = strip(new String(conf, delim[level] + 1, i - 1 - delim[level]));
							
							HashMap<String, String> temp = settings.get(blockName[level]);
							
							if (temp == null)
							{
								throw new UnableToParseException("Encountered null block name. Invalid block found on line " +
										lines);
							}
						
							if (!temp.containsKey(tmpCommand[level]))
							{
								temp.put(tmpCommand[level], tmpValue[level]);
							}
							else
							{
								throw new UnableToParseException("Value " + tmpCommand[level] + " already found on line " + lines);
							}
							
							delim[level]      = i;
							tmpCommand[level] = tmpValue[level] = "";
						}
						else if (inList[level])
						{
							String[] rawList = strip(new String(conf, delim2[level] + 1, i - 1 - delim2[level])).split(",");
							
							for (String listEntry : rawList)
							{
								lists.get(listName[level]).add(listEntry);
							}
						}
						level++;
					}
				}
			}
			
			if (printFindings)
			{
				printFindings();
			}
		}
		
		/**
		 * @param string
		 * @return
		 */
		private String strip (String string)
		{
			string = string.trim();
			string = string.replace("\n", "");
			
			return string;
		}

		/**
		 * Resizes an integer
		 * 
		 * @param delim
		 * @param level
		 */
		private int[] resize (int[] array, int newSize)
		{
			int[] tmpArray = array;
			int[] newArray = new int[newSize];
			
			System.arraycopy(tmpArray, 0, newArray, 0, array.length);
			
			array = newArray;
			
			return array;
		}
		
		/**
		 * Resizes a boolean
		 * 
		 * @param array
		 * @param newSize
		 * @return
		 */
		private boolean[] resize (boolean[] array, int newSize)
		{
			boolean[] newArray = new boolean[newSize];
			
			System.arraycopy(array, 0, newArray, 0, array.length);
			
			array = newArray;
			
			return array;
		}

		/**
		 * Resizes a string
		 * 
		 * @param array
		 * @param newSize
		 * @return
		 */
		private String[] resize (String[] array, int newSize)
		{
			String[] newArray = new String[newSize];
			
			System.arraycopy(array, 0, newArray, 0, array.length);
			
			array = newArray;
			
			return array;
		}
		
		/**
		 * Creates a heirarchy
		 * 
		 * @param blocks
		 * @param name
		 * @param level
		 * @return
		 */
		private String createHeirarchy (String[] blocks, String name, int level)
		{
			String temp = "";
			
			String block;
			
			for (int i = 0; i < level && (block = blocks[i]) != null; i++)
			{
				if ( (block != null) && (!block.equals("")) ) 
					temp += getLast(block) + ">";
			}

			temp += name;
			
			return temp;
		}
		
		/**
		 * Gets the last element in a heirarchy
		 * 
		 * @param heirarchy
		 * @return
		 */
		private String getLast (String heirarchy)
		{
			String[] split = heirarchy.split(">");
			return ( split[split.length-1] );
		}
		
		/**
		 * Gets everything before the last elemtn
		 * 
		 * @param heirarchy
		 * @return
		 */
		private String getBefore (String heirarchy)
		{
			if (heirarchy.lastIndexOf(">") != -1)
				return heirarchy.substring(0, heirarchy.lastIndexOf(">"));
			else
				return heirarchy;
		}
		
		/**
		 * Creates a block
		 * 
		 * @param array
		 * @param start
		 * @param offset
		 * @param max
		 * @return
		 */
		private String createBlock (char[] array, int start, int offset, int max)
		{
			if (start - offset < 0)
			{
				start = 0;
			}
			
			if ( (start + offset) >= max)
			{
				offset = ( max - (start + offset) );
			}
			
			if (offset < 0)
			{
				return "";
			}
			
			return new String(array, start, offset);
		}
		
		/**
		 * Prints the findings
		 *
		 */
		private void printFindings()
		{
			System.out.println("Settings grabbed: ");
			
			Set<String> keys = settings.keySet();
			
			for (String key : keys)
			{
				System.out.println("\r\nSettings for " + getLast(key));
				System.out.println("Heirarchy: " + getBefore(key) + "\r\n");
				
				HashMap<String, String> setting = settings.get(key);
				Set<String> settingNames  = setting.keySet();
				
				for (String settingName : settingNames)
				{
					System.out.println("\t"+settingName + ": " + setting.get(settingName));
				}
			}
			
			System.out.println("\r\nLists Grabbed: \r\n");
			
			keys = lists.keySet();
			
			for (String key : keys)
			{
				System.out.println("List for " + getLast(key));
				System.out.println("Heirarchy: " + getBefore(key) + "\r\n");
				
				ArrayList<String> list = lists.get(key);
				
				for (String entry : list)
				{
					System.out.println("\t"+entry);
				}
			}
		}
	}
	
	/**
	 * The ConfigFile, cannot be blank.
	 */
	private String configFile;
	
	/**
	 * The raw config
	 */
	private String rawConfig;
	
	/**
	 * The settings found and loaded
	 */
	private final HashMap<String, String>	settings	= new HashMap<String, String>();
	
	/**
	 * The new parser :D!
	 */
	private NewConfigParser					newParser	= null;
	
	/**
	 * Constructor with the directory and config file specified
	 * 
	 * @param directory
	 * @param config
	 * @throws IOException if it's unable to get the config file
	 * @throws UnableToParseException if the config file cannot be parsed
	 */
	public ConfigReader (final String directory, final String config) throws IOException, UnableToParseException
	{
		configFile = directory+File.separator+config;
		getConfig();
		parseConfig();
	}
	
	/**
	 * Constructor without a directory specified.
	 * 
	 * @param config
	 * @throws IOException if it's unable to get the config file
	 * @throws UnableToParseException if the config file cannot be parsed
	 */
	public ConfigReader (final String config) throws IOException, UnableToParseException
	{
		configFile = config;
		getConfig();
		parseConfig();
	}
	
	/**
	 * Sets the config file differently
	 * @param configFile
	 * @return Old configFile value
	 */
	public final String setConfigFile (final String configFile)
	{
		String oldConfigFile = this.configFile;
		this.configFile      = configFile;
		return oldConfigFile;
	}
	
	/**
	 * Attempts to get the config file provided it exists and is readable.
	 * If it is not or something happens during its loading, the method throws
	 * Exception.
	 * 
	 * @throws IOException if this script was unable to get the config file.
	 */
	public final void getConfig () throws IOException
	{
		final File config = new File(configFile);
		
		if (config.canRead())
		{
			rawConfig = "";
			
			final BufferedReader in = new BufferedReader(new FileReader(config));
			
			while (in.ready())
			{
				rawConfig += in.readLine()+"\r\n";
			}
			in.close();
			
			if (rawConfig.length() < 1)
			{
				throw new IOException("Config file "+configFile+" contains no data.");
			}
			
			return;
		}
		
		throw new IOException("Config file "+configFile+" either does not exist or cannot be read.");
	}
	
	/**
	 * This method parses attempts to parse a config file provided that the file is loaded
	 * and has delimiters. This will return ConfigNotLoadedException if the config is not
	 * loaded (Or has no data) and UnableToParseException if there are no delimiters, or
	 * if there is no settings found after parsing the config.
	 * 
	 * @return void
	 * @throws UnableToParseException
	 */
	public final void parseConfig () throws UnableToParseException
	{
		// Just in case the settings table was used last time :O
		if (settings.size() > 0)
		{
			settings.clear();
		}
		
		final String[] config = rawConfig.split("\r\n");
		String[] values;
		
		// If there's no delimiters
		if (config.length < 1)
		{
			throw new UnableToParseException("There are no delimiters, Try putting a newline char ('\\r\\n') " +
								"at the end of each config line");
		}
		
		// New Config Parser value
		if (config[0].startsWith("~new"))
		{
			if (config[0].indexOf("debug") != -1)
			{
				newParser = new NewConfigParser(rawConfig, true);
			}
			else
			{
				newParser = new NewConfigParser(rawConfig);
			}
			
			return;
		}
		
		for (String line : config)
		{
			if ((line.indexOf("//") != 0) && (line.indexOf("=") != -1))
			{
				values = line.split("=", 2);
				
				if (values.length == 2)
				{
					settings.put(values[0].trim().toLowerCase(), values[1].trim());					
				}
			}
		}
	}
	
	/**
	 * Searchings the Settings array for a specific setting by the name of <code>settingName</code>.
	 * If this setting is found the method will return its value, or null if the setting cannot be found.
	 * <br>
	 * Example:<br>
	 * <em>In RhinoBot.conf</em><br>
	 * <blockquote><code>newconfig = RhinoBot;</code></blockquote><br>
	 * <em>In the example code</em><br>
	 * <blockquote><code>String botNameVar = cfgReader.getSetting("botName");</code></blockquote><br>
	 * <br>
	 * @param settingName the setting name to look for
	 * @return settingValue the value of which the settingName was associated with, or NULL
	 * 			if it doesn't exist
	 * @deprecated see getSetting(String block, String name);
	 */
	public final String getSetting (final String name)
	{
		return settings.get(name.toLowerCase());
	}
	
	/**
	 * Checks if a setting actually exists before grabbing it.
	 * 
	 * @param name
	 * @return
	 * @deprecated see settingExists(String block, String name)
	 */
	public final boolean settingExists (final String name)
	{
		return settings.containsKey(name.toLowerCase());
	}
	
	/**
	 * Grab a setting from the new config parser
	 * @param block
	 * @param name
	 * @return
	 */
	public final String getSetting (final String block, final String name)
	{
		if (newParser.settings.get(block) != null)
		{
			return newParser.settings.get(block).get(name);
		}
		return null;
	}
	
	/**
	 * Gets a setting as a byte array
	 * @param block
	 * @param name
	 * @return
	 */
	public final int getSettingAsInteger (final String block, final String name)
	{
		return Integer.parseInt(getSetting(block, name));
	}
	
	/**
	 * Grab a setting as a boolean
	 * 
	 * @param block
	 * @param name
	 * @return
	 */
	public final boolean getSettingAsBoolean (final String block, final String name)
	{
		String tmp = newParser.settings.get(block).get(name).toLowerCase();
		
		if ( (tmp.equals("yes")) || (tmp.equals("true")) || (tmp.equals("1")) )
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * Grabs a list found in the list block if it exists, or null
	 * if it doesn't.
	 * 
	 * @param name
	 * @return
	 */
	public final ArrayList<String> getList (final String name)
	{
		if (newParser.lists.containsKey(name))
		{
			return newParser.lists.get(name);
		}
		return null;
	}
	
	/**
	 * Checks to see if a list exists
	 * 
	 * @param name
	 * @return
	 */
	public final boolean listExists (final String name)
	{
		return newParser.lists.containsKey(name);
	}
	
	/**
	 * Checks to see if a setting exists. If block is null this which
	 * check all the blocks.
	 * 
	 * @param block
	 * @param setting
	 * @return
	 */
	public final boolean settingExists (final String block, final String setting)
	{
		if (block == null)
		{
			Set<String> keys = newParser.settings.keySet();
			
			for (String key : keys)
			{
				if (newParser.settings.get(key).containsKey(setting))
				{
					return true;
				}
			}
		}
		else
		{
			if (newParser.settings.containsKey(block))
			{
				return newParser.settings.get(block).containsKey(setting);
			}
		}
		return false;
	}
	
	/**
	 * Checks to see if a block exists
	 * 
	 * @param block
	 * @return
	 */
	public final boolean blockExists (final String block)
	{
		return newParser.settings.containsKey(block);
	}
}
