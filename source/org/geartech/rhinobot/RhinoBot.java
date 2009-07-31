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
import java.io.UnsupportedEncodingException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;

import org.geartech.rhinobot.drivers.socket.*;
import org.geartech.rhinobot.manager.*;
import org.geartech.rhinobot.rhino.Rhino;
import org.geartech.rhinobot.support.*;
import org.geartech.rhinobot.support.Logger.LogLevel;


public final class RhinoBot
{
	/**
	 * Handles the incoming data in a timely fashion.
	 */
	private final class DataHandler extends Thread
	{
		private DataHandler ()
		{
			setName("Incoming Thread");
			setPriority(Thread.MAX_PRIORITY);
			start();
		}
		
		public final void run ()
		{
			String line = "";
			try
			{
				for (;;)
				{
					if (!connected())
					{
						break;
					}
					else
					{
						line = connectionSocket.readLine();
						logger.write(LogLevel.VERBOSE, "INCOMING: " + line);
					
						parseLine(line);
					}
				}
			}
			catch (Exception e)
			{
				if (e != null && e.getMessage() != null)
				{				
					if (e.getMessage().equalsIgnoreCase("connection reset"))
					{
						logger.write(LogLevel.MINOR, "Connection was reset... Reconnecting");
						reconnect = true;
					}
					else if (e.getMessage().equalsIgnoreCase("socket closed"))
					{
						logger.write(LogLevel.MINOR, "Socket was closed unexpectedly");
					}
					else
					{
						logger.write(LogLevel.MAJOR, "IOException occurred in handleData: " + e.getMessage(), e);
					}
				}
				else
				{
					logger.write(LogLevel.MAJOR, "Exception occurred in handleData: " + e.toString(), e);
				}
				
				disconnect();
			}
		}
	}
	
	/*
	 * Internal RhinoBot settings
	 */
	
	boolean									reconnect			= false;

	private boolean							connected			= false;

	private boolean							startup				= true;

	private static Rhino					rhino				= Rhino.getInstance();

	static final Logger						logger				= new Logger("logs", "RhinoBot");

	private int								instanceNumber		= -1;

	private final Manager					manager				= new Manager();
	
	// TODO Change
	private final String					version				= "RhinoBot public-debug";
	/*
	 * Modular Information
	 */

	private static boolean					rhinoEnabled		= true;

	private boolean							modulesEnabled		= true;

	/*
	 * Network and Server related stuff
	 */

	private String							modes				= "";

	private String							believedNetwork;
	
	private String							network				= "";

	private String							serverDaemon;

	private String							wantedServer;

	private String							actualServer;

	private String							wantedSocket;
	
	SocketDriver							connectionSocket;

	/*
	 * Network and Server related stuff (cont.)
	 */

	private int								port;

	private boolean							away				= false;

	private String							password;

	private String							charset				= Charset.defaultCharset().toString();

	private int								retryCount			= 0;
	
	private String							wantedNick;

	private String							actualNick;

	private String							ident;

	private ArrayList<String>				channelsToJoin		= new ArrayList<String>();

	private String							chanModes;

	private String							userModes;

	private int								serverCount			= 0;

	private int								operCount			= 0;

	private int								channelCount		= 0;

	private int								localUsers			= 0;

	private int								globalUsers			= 0;

	private String							serverTime;

	private String							motd;
	
	private char[]							modesWithParameters = new char[] { 'o', 'v', 'b', 'e', 'k' };
	
	private String							chanTypes;
	
	private int 							maxSilence			= -1;
	
	private int 							maxChannels			= -1;
	
	private int 							maxExceptions		= -1;
	
	private int 							maxInvites			= -1;
	
	private int 							maxBans				= -1;
	
	private int 							maxModes			= -1;
	
	private int 							maxNickLength		= -1;
	
	private int 							maxTopicLength		= -1;
	
	private int 							maxAwayLength		= -1;
	
	private int 							maxKickLength		= -1;
	
	private int 							maxChannelLength	= -1;
	
	private String[] 						prefixes;
	
	// REDO
	private HashMap<String, String>			supportedChanModes	= new HashMap<String, String>(4);
	
	private String[]						commands;
	
	/*
	 * Starting up and stuff
	 */

	public RhinoBot ()
	{
		this("ChatSpike", "RhinoBot", "RBot", "example.com", 6667, "StandardSocket", new ArrayList<String>(), null);
	}
	
	public RhinoBot (final String network, final String wantedNick, final String ident, 
				final String wantedServer, final int port, final String wantedSocket, 
				final ArrayList<String> channelsToJoin, final String password)
	{
		believedNetwork		= network;
		this.charset		= "UTF8";
		this.actualNick		= wantedNick; 
		this.wantedNick		= wantedNick;
		this.ident			= ident;
		this.actualServer	= wantedServer; 
		this.wantedServer	= wantedServer;
		this.port			= port;
		this.wantedSocket	= wantedSocket;
		this.startup		= true;
		this.channelsToJoin = channelsToJoin;
		this.password		= password;
		this.instanceNumber = 0;
		rhinoEnabled		= true;
	}
	
	/**
	 * Reloads the config.
	 * 
	 * @throws BotException
	 * @return TRUE if a restart is needed, FALSE if not.
	 */
	public final boolean reloadConfig () throws BotException
	{
		logger.write(LogLevel.VERBOSE, "Reloading Config...");
		
		boolean restart = false;
		
		// TODO reloadConfig
		
		return restart;
	}
	
	/**
	 * Checks if the bot(s) think Rhino is enabled
	 * 
	 * @return
	 */
	public static final boolean rhinoEnabled ()
	{
		return rhinoEnabled;
	}
	
	/**
	 * Tells the bots Rhino is enabled
	 *
	 */
	public static final void enableRhino ()
	{
		rhino		 = Rhino.getInstance();
		rhinoEnabled = true;
	}
	
	/**
	 * Tells the bots Rhino is disabled
	 *
	 */
	public static final void disableRhino ()
	{
		rhino		 = null;
		rhinoEnabled = false;
	}

	/**
	 * Starts the bot, if the bot is set to start and if it isn't currently connected
	 */
	public final void start ()
	{
		if (!startup)
		{
			startup = true;
			logger.write(LogLevel.INFO, "RhinoBot instance " + instanceNumber + " will not be started up");
			return;
		}
		else if (connected())
		{
			logger.write(LogLevel.INFO, "Attempt to start bot while bot was already started");
			return;
		}
		
		connect();
	}

	/**
	 * Reconnects the bot with a custom quit reason
	 * @param quitReason
	 */
	public final void reconnect (final String quitReason)
	{
		reconnect = true;
		quit(quitReason);
	}
	
	/**
	 * Reconnects the bot
	 */
	public final void reconnect ()
	{
		reconnect("Restarting...");
	}

	/**
	 * Connects the bot using SSL or not SSL, and also depending on thread settings!
	 */
	private final void connect ()
	{
		manager.purge();
	
		// TODO Fix
//		connectionSocket = ModuleController.findSocketModule(wantedSocket);

		if (connectionSocket == null)
		{
			logger.write(LogLevel.MINOR, "ModuleController was unable to find socket " + wantedSocket + ", defaulted to StandardSocket");
			connectionSocket = new StandardSocket();
		}
		
		logger.write(LogLevel.VERBOSE, "Using Socket Interface " + connectionSocket.toString());
		
		/*
		 * TODO Retry attempts 
		 */
		
		boolean shutdown = false,
				retry	 = false;
		
		try
		{
			connectionSocket.open(wantedServer, port, charset);
		}
		catch (UnknownHostException e)
		{
			logger.write(LogLevel.MAJOR, "Unknown host or address: " + wantedServer + "! Perhaps misspelled?");
			shutdown = true;
		}
		catch (UnsupportedEncodingException e)
		{
			logger.write(LogLevel.MINOR, "Encoding " + charset + " is not supported, retrying with default charset.");
			charset = Charset.defaultCharset().toString();
			retry = true;
		}
		catch (SocketTimeoutException e)
		{
			logger.write(LogLevel.MINOR, "Socket timed out attempting to connect, retrying...");
			retry = true;
		}
		catch (SocketException e)
		{
			String errMsg = e.getMessage();
			
			if (errMsg.indexOf("Connection refused") != -1)
			{
				logger.write(LogLevel.MINOR, "Connection was refused when attempting to connect to server.");
			}
			
			logger.write(LogLevel.MINOR, "Exception with the socket: " + e.getMessage());
			shutdown = true;
		}
		catch (IOException e)
		{
			logger.write(LogLevel.MINOR, "IOException: " + e.getMessage(), e);
			shutdown = true;
		}
		catch (Exception e)
		{
			logger.write(LogLevel.MINOR, "Exception: " + e.getMessage(), e);
			wantedSocket = "BotNormalSocket";
			retry = true;
		}
		
		if (retry)
		{
			if (retryCount > 4)
			{
				shutdown();
			}
			else
			{
				retryCount++;
				connect();
			}
			return;
		}
		
		if (shutdown)
		{
			shutdown();
		}
		
		if ((connectionSocket == null) || (!connectionSocket.connected()))
		{
			connectionSocket = null;
			return;
		}

		if (password != null)
		{
			raw("PASS " + password);
		}

		connected = true;

		/*
		 * We're assuming that all IRCD's go with PASS, NICK, then USER
		 */
		
		raw("NICK " + wantedNick);
		raw("USER " + ident + " " + wantedServer + " * :" + version);
		
		new DataHandler();
	}

	/**
	 * Quits with the version of the bot
	 */
	public final void quit ()
	{
		quit(version);
	}

	/**
	 * Quits the server with a (custom) quit message
	 * 
	 * @param message
	 */
	public final void quit (final String message)
	{
		raw("QUIT :" + message);
		disconnect();
	}

	/**
	 * Makes sure the bot is known to be connected, and is actually connected
	 * @return
	 */
	public final boolean connected ()
	{
		return ((connected) && (connectionSocket.connected()));
	}
	
	/**
	 * Internal command to exit the bot
	 *
	 */
	private final void shutdown ()
	{
		if (reconnect)
		{
			reconnect = false;
			connect();
		}
	}
	
	/**
	 * Disconnects the bot. If <code>reconnect</code> is set to true, this method will automatically restart
	 * the bot.
	 * 
	 * TODO Find out if this needs to be synchronized
	 */
	public final void disconnect ()
	{
		logger.write(LogLevel.VERBOSE, "Disconnect method called!");
		
		connected = false;

		try
		{
			if (connectionSocket != null)
			{
				connectionSocket.close();
			}
		}
		catch (Exception e)
		{
			logger.write(LogLevel.MAJOR, "Unable to close connection, or error closing connection: " + e.getMessage(), e);
		}
		
		shutdown();
	}

	/*
	 * Sending Commands
	 */

	/**
	 * Raw output line
	 * 
	 * @param line
	 */
	private final void raw (final String line)
	{
		if (connectionSocket != null)
		{
			logger.write(LogLevel.VERBOSE, "OUTGOING: " + line);
			connectionSocket.writeLine(line);
		}
		else
		{
			logger.write(LogLevel.MAJOR, "ERROR: Unable to send data because connectionSocket is null.");
			shutdown();
		}
	}

	/**
	 * Performs a WHOIS
	 * 
	 * @param audience
	 */
	public final void whois (final String audience)
	{
		raw("WHOIS " + audience);
	}
	
	/**
	 * @param newNick
	 */
	public final void nick (final String newNick)
	{
		raw("NICK " + newNick);
	}

	/**
	 * Oper
	 * 
	 * @param user
	 * @param pass
	 */
	public final void oper (final String user, final String pass)
	{
		raw("OPER " + user + " " + pass);
	}

	/**
	 * CTCP
	 * 
	 * @param audience
	 * @param command
	 * @param message
	 */
	public final void ctcp (final String audience, final String command, final String message)
	{
		raw("PRIVMSG " + audience + " :\u0001" + command.toUpperCase() + " " + message + "\u0001");
	}

	/**
	 * CTCP Reply
	 * 
	 * @param audience
	 * @param command
	 * @param message
	 */
	public final void ctcpReply (final String audience, final String command, final String message)
	{
		raw("NOTICE " + audience + " :\u0001" + command.toUpperCase() + " " + message + "\u0001");
	}
	
	/**
	 * Notice
	 * 
	 * @param audience
	 * @param message
	 */
	public final void notice (final String audience, final String message)
	{
		raw("NOTICE " + audience + " :" + message);
	}

	/**
	 * PRIVMSG event
	 * 
	 * @param audience
	 * @param message
	 */
	public final void privmsg (final String audience, final String message)
	{
		raw("PRIVMSG " + audience + " :" + message);
	}
	
	/**
	 * An Action event, or an emote
	 * @param channel
	 * @param action
	 */
	public final void action (final String channel, final String action)
	{
		ctcp(channel, "ACTION", action);
	}

	/**
	 * @param channel
	 * @param modes
	 */
	public final void mode (final String channel, final String modes)
	{
		raw("MODE " + channel + " :" + modes);
	}

	/**
	 * @param channel
	 * @param topic
	 */
	public final void topic (final String channel, final String topic)
	{
		raw("TOPIC " + channel + " :" + topic);
	}

	/**
	 * Provides a Wallops message.
	 * 
	 * @param message
	 */
	public final void wallops (final String message)
	{
		raw("WALLOPS :" + message);
	}

	/**
	 * Joins a channel
	 * 
	 * @param channel
	 */
	public final void join (final String channel)
	{
		raw("JOIN " + channel);
	}

	/**
	 * Joins a channel with a key/password
	 * 
	 * @param channel
	 * @param password
	 */
	public final void join (final String channel, final String password)
	{
		raw("JOIN " + channel + " " + password);
	}

	/**
	 * Parts a channel
	 * 
	 * @param channel
	 */
	public final void part (final String channel)
	{
		raw("PART " + channel);
	}

	/**
	 * Parts a channel with a reason
	 * 
	 * @param channel
	 * @param reason
	 */
	public final void part (final String channel, final String reason)
	{
		raw("PART " + channel + " " + reason);
	}

	/**
	 * Cycles a channel (Rejoins)
	 * 
	 * @param channel
	 */
	public final void cycle (final String channel)
	{
		raw("PART " + channel + " :Cycling...");
		raw("JOIN " + channel);
	}

	/**
	 * Resets all the permissions
	 */
	public final void resetPermissions ()
	{
		manager.resetAllPermissions();
	}
	
	/*
	 * Setting up Scripts & Modules
	 */

	/**
	 * Checks to see if the bot is in a channel
	 * 
	 * @param channel
	 * @return bool
	 */
	public final boolean inChannel (String channel)
	{
		return (manager.inChannel(channel));
	}

	/**
	 * Gets the nickname of the bot
	 * 
	 * @return String
	 */
	public final String getNick ()
	{
		return actualNick;
	}

	/**
	 * Returns the instance number
	 * 
	 * @return
	 */
	public final int getInstanceNumber ()
	{
		return instanceNumber;
	}
	
	/**
	 * Returns the network
	 * @return
	 */
	public final String getNetwork ()
	{
		return network;
	}
	
	/**
	 * Gets the version of the bot
	 * 
	 * @return String
	 */
	public final String getVersion ()
	{
		return version;
	}

	/**
	 * Returns the MOTD if there ever was one, or NULL if there wasn't
	 * 
	 * @return motd
	 */
	public final String getMOTD ()
	{
		return motd;
	}

	/**
	 * @return statistics
	 */
	public final int[] getStatistics ()
	{
		return new int[] { serverCount, operCount, channelCount, localUsers, globalUsers };
	}

	/**
	 * Grabs the server time even if it wasn't already grabbed, in which case it will return NULL
	 * 
	 * @return serverTime
	 */
	public final String getServerTime ()
	{
		return serverTime;
	}

	/**
	 * Returns the modes the IRCD claims to support
	 * 
	 * @return Modes supposedly supported by the IRCD
	 */
	public final String[] getSupportedModes ()
	{
		return new String[] { chanModes, userModes };
	}

	/**
	 * Returns the Manager
	 * 
	 * @return
	 */
	public final Manager getManager ()
	{
		return manager;
	}

	/**
	 * Returns the server daemon if caught
	 * 
	 * @return
	 */
	public final String getServerDaemon ()
	{
		return serverDaemon;
	}

	/**
	 * Makes some basic information into javascript code
	 * 
	 * @return
	 */
	private final Object[] getBasicInfo ()
	{
		return new Object[] {
			actualNick,
			ident,
			instanceNumber,
			actualServer,
			network,
			charset,
			chanModes,
			userModes,
			new String(modesWithParameters),
			chanTypes,
			maxSilence,
			maxChannels,
			maxExceptions,
			maxInvites,
			maxBans,
			maxModes,
			maxNickLength,
			maxTopicLength,
			maxAwayLength,
			maxKickLength,
			maxChannelLength,
			commands
		};
	}
	
	/**
	 * Returns the limitations the server gave us. If the limitation is unknown
	 * then the value will be -1<br>
	 * <br>
	 * Order: <br>
	 * <ul>
	 *   <li>maxSilence</li>
	 *   <li>maxChannels</li>
	 *   <li>maxExceptions</li>
	 *   <li>maxInvites</li>
	 *   <li>maxBans</li>
	 *   <li>maxModes</li>
	 *   <li>maxNickLength</li>
	 *   <li>maxTopicLength</li>
	 *   <li>maxAwayLength</li>
	 *   <li>maxKickLength</li>
	 *   <li>maxChannelLength</li>
	 * </ul>
	 * 
	 * @return int array of all the data.
	 */
	public final int[] getLimitInfo ()
	{
		return new int[] {
			maxSilence,
			maxChannels,
			maxExceptions,
			maxInvites,
			maxBans,
			maxModes,
			maxNickLength,
			maxTopicLength,
			maxAwayLength,
			maxKickLength,
			maxChannelLength
		};
	}
	
	/*
	 * Parsing
	 */
	
	/**
	 * Parses a modes line
	 * @param rawModes
	 * @return ArrayList of modes.
	 */
	private final ArrayList<Mode> parseModes (final String rawModes)
	{
		int buffer	= 0,
			pos		= 0,
			ppos	= 1,
			length	= rawModes.length();
		
		char	 chr	= rawModes.charAt(buffer);
		boolean	 plus		= false;
		String[] params		= null;
		
		if (rawModes.indexOf(' ') != -1)
		{
			params = rawModes.split(" ");
		}
		
		ArrayList<Mode> modes  = new ArrayList<Mode>();
		
		plus = (chr != '-');
		
		for (;;)
		{
			if ((chr == '-') || (chr == '+'))
			{
				for (int i = pos + 1; i < buffer; i++)
				{
					char x = rawModes.charAt(i);
					
					if ((StringUtils.inCharArray(modesWithParameters, x)) && (params != null))
					{
						modes.add(new Mode(x, params[ppos++], plus));
					}
					else
					{
						modes.add(new Mode(x, "", plus));
					}
				}
				
				pos  = buffer;
				plus = (chr == '+');
			}
			
			if ((chr == ' ') || ((buffer + 1) >= length))
			{
				for (int i = pos + 1; i < buffer; i++)
				{
					char temp = rawModes.charAt(i);
					
					if ((StringUtils.inCharArray(modesWithParameters, temp)) && (params != null))
					{
						modes.add(new Mode(temp, params[ppos++], plus));
					}
					else
					{
						modes.add(new Mode(temp, "", plus));
					}
				}
				break;
			}
			
			chr = rawModes.charAt( ++buffer );
		}
		
		return modes;
	}
	
	/**
	 * Parses a RPL_ISUPPORT (Defined as RPL_BOUNCE in '93) (event 005) for various servers (Unreal, ircu, dancer)
	 * 
	 * @param String
	 */
	private final void parseISupport (final String bounce)
	{
		int			index	= 1;
		String[]	split	= bounce.substring(bounce.indexOf(' ') + 1, bounce.lastIndexOf(':')).split(" ");
		
		ArrayList<String> tmp = new ArrayList<String>();
		
		for (; index < split.length; index++)
		{
			// Must have a NAME=VALUE type thing
			if (split[index].indexOf('=') != -1)
			{
				String[] nameval = split[index].split("=");
				if (nameval[0].equalsIgnoreCase("awaylen"))
				{
					maxAwayLength = Integer.parseInt(nameval[1]);
				}
				if (nameval[0].equalsIgnoreCase("topiclen"))
				{
					maxTopicLength = Integer.parseInt(nameval[1]);
				}
				else if (nameval[0].equalsIgnoreCase("network"))
				{
					if (believedNetwork.equalsIgnoreCase(nameval[1]))
					{
						network = nameval[1];
					}
				}
				else if (nameval[0].equalsIgnoreCase("chantypes"))
				{
					chanTypes = nameval[1];
				}
				else if (nameval[0].equalsIgnoreCase("channellen"))
				{
					maxChannelLength = Integer.parseInt(nameval[1]);
				}
				else if (nameval[0].equalsIgnoreCase("maxchannels"))
				{
					maxChannels = Integer.parseInt(nameval[1]);
				}
				else if (nameval[0].equalsIgnoreCase("maxbans"))
				{
					maxBans = Integer.parseInt(nameval[1]);
				}
				else if ( (nameval[0].equalsIgnoreCase("nicklen")) || (nameval[0].equalsIgnoreCase("maxnicklen")) )
				{
					maxNickLength = Integer.parseInt(nameval[1]);
				}
				else if (nameval[0].equalsIgnoreCase("modes"))
				{
					maxModes = Integer.parseInt(nameval[1]);
				}
				else if (nameval[0].equalsIgnoreCase("chanmodes"))
				{
					String[] modes = nameval[1].split(",");
					
//					if (modes.length == 4)
//					{
						supportedChanModes.put("list", modes[0]);
						supportedChanModes.put("param", modes[1]);
						supportedChanModes.put("setparam", modes[2]);
						supportedChanModes.put("normal", modes[3]);
//					}
//					else
//					{
//						supportedChanModes.put("normal", nameval[1]);
//					}
				}
				else if (nameval[0].equalsIgnoreCase("prefix"))
				{
					// If the string was (ovhaq) @+%&~
					// prefixes[0] = ovhaq
					// prefixes[1] = @+%&~
					prefixes = nameval[1].substring(1).split("\\)");
				}
				else if (nameval[0].equalsIgnoreCase("maxlist"))
				{
					int p = nameval[1].indexOf(',');
					
					// Must be only one of them! We're going to try to figure out which one.
					if (p == -1)
					{
						if (nameval[1].indexOf("I:") != -1)
						{
							maxInvites		= Integer.parseInt(nameval[1].substring(nameval[1].indexOf("I:") + 2));
						}
						else if (nameval[1].indexOf("b:") != -1)
						{
							maxBans			= Integer.parseInt(nameval[1].substring(nameval[1].indexOf("b:") + 2, p));
						}
						else if (nameval[1].indexOf("e:") != -1)
						{
							maxExceptions	= Integer.parseInt(nameval[1].substring(nameval[1].indexOf("e:") + 2, p));
						}
					}
					else
					{
						maxBans			= Integer.parseInt(nameval[1].substring(nameval[1].indexOf("b:") + 2, p));
						p				= nameval[1].indexOf(',', p + 1);
						maxExceptions	= Integer.parseInt(nameval[1].substring(nameval[1].indexOf("e:") + 2, p));
						maxInvites		= Integer.parseInt(nameval[1].substring(nameval[1].indexOf("I:") + 2));
					}
				}
				else if (nameval[0].equalsIgnoreCase("silence"))
				{
					maxSilence = Integer.parseInt(nameval[1]);
				}
				else if (nameval[0].equalsIgnoreCase("cmds"))
				{
					String[] cmds = nameval[1].split(",");
					for (String cmd : cmds)
					{
						tmp.add(cmd);
					}
				}
			}
			else
			{
				tmp.add(split[index]);
			}
		}
		
		commands = tmp.toArray(new String[tmp.size()]);
		
		tmp.clear();
		
		if ((prefixes == null) && (!supportedChanModes.containsKey("list")) && (!supportedChanModes.containsKey("param"))
				&& (!supportedChanModes.containsKey("setparam")))
		{
			modesWithParameters = new char[] { 'o', 'v', 'b', 'e', 'k' };
		}
		else
		{
			String modes = "";
			
			if (prefixes != null)
			{
				modes += prefixes[0];
			}
			
			if (supportedChanModes.containsKey("list"))
			{
				modes += supportedChanModes.get("list");
				manager.setListModeChars(supportedChanModes.get("list"));
			}
			
			if (supportedChanModes.containsKey("param"))
			{
				modes += supportedChanModes.get("param");
			}
			
			if (supportedChanModes.containsKey("setparam"))
			{
				modes += supportedChanModes.get("setparam");
			}
			
			if (modes.equals(""))
			{
				modesWithParameters = new char[] { 'o', 'v', 'b', 'e', 'k' };
			}
			else
			{
				modesWithParameters = new char[modes.length()];
				
				modes.getChars(0, modes.length(), modesWithParameters, 0);
			}
		}
	}
	
	/**
	 * This is my internal parser for IRC events, command, and everything in between
	 * Although it will have more considerable work on it, I consider it stable enough
	 * to use "in the field." I have battered this quite a bit and determined that it's
	 * stable enough for anything.
	 * 
	 * @param line
	 */
	final void parseLine (final String line)
	{
		if ((line == null) || (line.equals("")))
		{
			logger.write(LogLevel.MINOR, "Null or blank line caught");
			return;
		}

		if (line.indexOf(':') != 0)
		{
			if (line.startsWith("PING"))
			{
				if ((line.indexOf(' ') != -1) || (line.length() > 5))
				{
					raw("PONG " + line.substring(5));
				}
				else
				{
					// Send data anyway, just to make sure :>
					raw("PONG");
					logger.write(LogLevel.MINOR, "Invalid PING sent from server!");
				}
				
				return;
			}
			else if (line.startsWith("ERROR"))
			{
				logger.write(LogLevel.INFO, "Error sent from server: " + line.substring(7));
				
				reconnect();
				return;
			}
		}

		String nick		= "";
		String ident	= "";
		String hostmask	= "";

		/*
		 * Faster way, kind of C-based way to build up the nick!ident@hostmask Also, this allows strings that
		 * would otherwise break the bot be parsed specifically so the bot doesn't crash
		 */

		int buffer     = 0;
		char character = line.charAt(buffer);
		int colon      = (line.indexOf(':') == 0) ? 1 : 0;
		int length     = 0;
		int pos        = 0;

		if (line.indexOf(' ') != -1)
		{
			length = line.substring(0, line.indexOf(' ')).length();
		}
		else
		{
			logger.write(LogLevel.MINOR, "Invalid line caught: " + line);
			return;
		}

		/*
		 * My impenetrable parser.
		 */
		for (;;)
		{
			if ((character == '!') && (length > buffer))
			{
				if (nick.equals(""))
				{
					nick = line.substring(colon, buffer);
					pos = (buffer + 1);
				}
				else
				{
					if (ident.equals(""))
					{
						ident = line.substring(pos, buffer);
						pos = (buffer + 1);
					}
				}
			}

			if ((character == '@') && (length > buffer))
			{
				if (nick.equals(""))
				{
					nick = line.substring(colon, buffer);
					pos = (buffer + 1);
					logger.write(LogLevel.MINOR, "Character @ found but no nickname set, setting nickname");
				}
				else
				{
					ident = line.substring(pos, buffer);
					pos = (buffer + 1);
				}
			}

			if (character == ' ')
			{
				// If the server sends nothing but a nick
				// Apparently this is allowed :(
				if ((nick.equals("")) && (ident.equals("")))
				{
					nick = line.substring(colon, buffer);
//					logger.write(LogLevel.VERBOSE, "Error parsing masks: Character ' ' found but no ident or nickname exists");
				}
				else
				{
					if (pos < buffer)
					{
						hostmask = line.substring(pos, buffer);
						pos = (buffer + 1);
					}
					else
					{
						nick = line.substring(colon);
						logger.write(LogLevel.VERBOSE, "Error parsing masks: Character ' ' found but "
								+ "the length of nick and ident and 3 is less than the current buffer");
					}
				}
				break;
			}
			/*
			 * Prevents the loop from becoming truly infinate Though, this is nearly impossible to actually
			 * happen.
			 */
			else if (buffer > length)
			{
				logger.write(LogLevel.MINOR, "Error parsing masks: Buffer is larger than the string length");
				logger.write(LogLevel.MINOR, line);
				break;
			}

			buffer++;
			character = line.charAt(buffer);
		}

		if ((nick.equals("")) && (ident.equals("")) && (hostmask.equals("")))
		{
			logger.write(LogLevel.MINOR, "Error parsing masks: Nickname, ident, and hostmask weren't grabbed!");
			logger.write(LogLevel.MINOR, "Line: " + line);
			return;
		}

		if (nick.length() < 1)
		{
			logger.write(LogLevel.MINOR, "Unable to grab nickname from server message");
			logger.write(LogLevel.MINOR, "Line: " + line);
			return;
		}

		String command = "";
		String string  = "";

		int lastCommandSpace = line.indexOf(' ', line.indexOf(' ') + 1);
		
		if ((line.indexOf(' ', 1) != lastCommandSpace) && (lastCommandSpace != -1)
				&& (line.length() > lastCommandSpace))
		{
			command = line.substring(line.indexOf(' ') + 1, line.indexOf(' ', line.indexOf(' ') + 1)).toLowerCase();
		}
		else
		{
			command = line.substring(line.indexOf(' ') + 1).toLowerCase().trim();
			logger.write(LogLevel.MINOR, "Invalid Event grabbed: " + command);
			return;
		}

		// if command length is longer than the line (like maybe it is the size of the line) quit
		// Fixed a bug here that would screw everything up.
		if ((line.toLowerCase().indexOf(command) + command.length() + 1) >= line.length())
		{
			logger.write(LogLevel.MINOR, "Invalid line caught: " + line);
			return;
		}
		else
		{
			// This grabs the string
			string = line.substring(line.toLowerCase().indexOf(command) + command.length() + 1);
		}

		/*
		 * Let's now go through all the supported rfc events.
		 */

		/*
		 * First, the connection based events
		 */

		if (command.equals("001")) // RPL_WELCOME
		{
			logger.write(LogLevel.VERBOSE, "RPL_WELCOME caught");
			// onConnect stuff goes here.

			String[] result = string.split(" ");

			if (result.length > 3)
			{
				logger.write(LogLevel.VERBOSE, "The network claims to be " + result[4]);
				logger.write(LogLevel.VERBOSE, "We defined the network as " + believedNetwork);
				if ((believedNetwork != null) && (believedNetwork.equalsIgnoreCase(result[4])))
				{
					network = believedNetwork;
				}
			}

			onConnect();

			for (String channel : channelsToJoin)
			{
				join(channel);
			}
		}
		else if ((command.equals("002")) || // RPL_YOURHOST
				(command.equals("003"))) // RPL_CREATED
		{
			// No support needed as of yet
		}
		else if (command.equals("004")) // RPL_MYINFO
		{
			// 0 1 2 3 4
			// RhinoBot essence.whyaresee.net Unreal3.2.3 iowghraAsORTVSxNCWqBzvdHtGp
			// lvhopsmntikrRcaqOALQbSeIKVfMCuzNTGj
			String[] array = string.split(" ");

			if (array.length > 4)
			{
				actualServer = array[1];
				serverDaemon = array[2];
				userModes    = array[3];
				chanModes    = array[4];
			}
			else
			{
				logger.write(LogLevel.MAJOR, "Invalid RPL_MYINFO caught");
			}

			logger.write(LogLevel.VERBOSE, "RPL_MYINFO caught");
		}
		else if (command.equals("005")) // RPL_ISUPPORT / RPL_BOUNCE
		{
			logger.write(LogLevel.VERBOSE, "RPL_ISUPPORT caught");
			
			parseISupport(string.substring(string.indexOf(' ') + 1));
		}
		else if (command.equals("301")) // RPL_AWAY
		{
			String awayNick = string.substring(0, string.indexOf(" :"));
			User awayUser = manager.getUser(awayNick);

			if (awayUser != null)
			{
				awayUser.setAway(true);
				awayUser.setAwayReason(string.substring(awayNick.length() + 2));
			}

			logger.write(LogLevel.VERBOSE, "RPL_AWAY caught");
		}
		else if (command.equals("302")) // RPL_USERHOST
		{
			/*
			 * 302 RPL_USERHOST ":*1<reply> *( " " <reply> )" - Reply format used by USERHOST to list replies
			 * to the query list. The reply string is composed as follows:
			 * 
			 * 		reply = nickname [ "*" ] "=" ( "+" / * "-" ) hostname
			 * 
			 * The '*' indicates whether the client has registered as an Operator.
			 * The '-' or '+' characters represent whether the client has set an 
			 * AWAY message or not respectively. + means not away - means away 
			 * :Mage*=-Mage@example.com
			 */

			String[] array = string.split("=", 1);

			if (array.length < 1)
			{
				logger.write(LogLevel.MAJOR, "Invalid RPL_USERHOST caught: " + line);
				return;
			}

			String replyName = array[0];

			if (array[0].indexOf('*') != -1)
			{
				replyName = array[0].replace("\\*", "");
			}

			User replyUser = manager.getUser(replyName);

			if (replyUser != null)
			{
				if (array[0].indexOf('*') != -1)
				{
					replyUser.setOper(true);
				}

				if (array[1].startsWith("-"))
				{
					replyUser.setAway(true);
				}
				else if (array[1].startsWith("+"))
				{
					replyUser.setAway(false);
				}
			}

			logger.write(LogLevel.VERBOSE, "RPL_USERHOST caught");
		}
		else if (command.equals("303")) // RPL_ISON
		{
			// UserTracker tracker = manager.getUserTracker();

			String[] array = string.split(" ");

			for (String foundUser : array)
			{
				if (!manager.userExists(foundUser))
				{
					manager.addUser(foundUser);
				}
				
				manager.foundUser(foundUser);
			}

			logger.write(LogLevel.VERBOSE, "RPL_ISON caught");
		}
		else if (command.equals("305")) // RPL_UNAWAY
		{
			away = false;

			logger.write(LogLevel.VERBOSE, "RhinoBot is no longer marked as away");
		}
		else if (command.equals("306"))
		{
			away = true;

			logger.write(LogLevel.VERBOSE, "RhinoBot is now marked as away");
		}
		/*
		 * WHOIS Command replies 
		 * 
		 * UnrealIRCD
		 * 
		 * >> :server.example.com 311 Mage Spec john marshmellow * :Unknown
		 * >> :server.example.com 379 Mage Spec :is using modes +iowghraAsxNWzt +kcfFjveGnNqSso
		 * >> :server.example.com 378 Mage Spec :is connecting from *@localhost 127.0.0.1
		 * >> :server.example.com 307 Mage Spec :is a registered nick
		 * >> :server.example.com 319 Mage Spec :~#ex1 ~#ex2 &#ex3 @#ex4 #ex5
		 * >> :server.example.com 312 Mage Spec server.example.com :example.com
		 * >> :server.example.com 301 Mage Spec :Vacation!
		 * >> :server.example.com 313 Mage Spec :is a Network Administrator
		 * >> :server.example.com 671 Mage Spec :is using a Secure Connection
		 * >> :server.example.com 320 Mage Spec :is special
		 * >> :server.example.com 317 Mage Spec 24 1126404559 :seconds idle, signon time 
		 * >> :server.example.com 318 Mage Spec :End of /WHOIS list.
		 * 
		 * ircu
		 * 
		 * >> :example.server.com 311 Mage Mage yrc the.sign.on.the.door.said.berightback.org * :Mage
		 * >> :example.server.com 319 Mage Mage :@#ex3 @#ex2 +#ex1
		 * >> :example.server.com 312 Mage Mage server.example.com :example.com
		 * >> :example.server.com 330 Mage Mage MageOfChrisz :is logged in as
		 * >> :example.server.com 317 Mage Mage 1519 1126462274 :seconds idle, signon time
		 * >> :example.server.com 318 Mage Mage :End of /WHOIS list.
		 */
		else if (command.equals("307")) // Registered Nickname
		{
			String reggedNick = string.substring(0, string.indexOf(" :"));
			User reggedUser = manager.getUser(reggedNick);

			if (reggedUser != null)
			{
				reggedUser.setRegistered(true);
			}
		}
		else if (command.equals("311")) // RPL_WHOISUSER
		{
			// 0    1    2   3
			// Mage Mage yrc hostmask.com * :Mage
			String[] array = string.split(" ", 4);

			if (array.length != 5)
			{
				logger.write(LogLevel.MAJOR, "Unable to properly split string for RPL_WHOISUSER");
				return;
			}

			// Shouldn't have to worry about this :o
		}
		else if (command.equals("312"))
		{
			// Mage Spec server.example.com :The essence of life

			buffer = 0;
			pos    = 0;

			String nickName = null;
			String server   = null;

			pos = string.indexOf(' ');

			if ((pos != -1) && (string.indexOf(' ', pos + 1) != -1))
			{
				nickName = string.substring(pos + 1, string.indexOf(' ', pos + 1));
				pos = string.indexOf(' ', pos + 1);
			}

			if ((nickName != null) && (pos != -1) && (string.indexOf(" :") != -1))
			{
				server = string.substring(pos + 1, string.indexOf(" :"));
			}

			if ((nickName != null) && (server != null))
			{
				User user = manager.getUser(nickName);

				if (user != null)
				{
					user.setServer(server);
				}
			}
		}
		else if (command.equals("313"))
		{
			// Mage Spec :is a Network Administrator

			String nickName = null;

			pos = string.indexOf(' ');

			if ((pos != -1) && (string.indexOf(' ', pos + 1) != -1))
			{
				pos++;
				nickName = string.substring(pos, string.indexOf(' ', pos));

				if (nickName != null)
				{
					User user = manager.getUser(nickName);

					if (user != null)
					{
						user.setOper(true);
					}
				}
			}
		}
		else if (command.equals("317"))
		{
			// Unreal
			// Mage Spec 24 1126404559 :seconds idle, signon time
			// ircu
			// Mage Mage 1519 1126462274 :seconds idle, signon time

			String nickName = null;
			User user;
			long time = -1;

			pos = string.indexOf(' ');

			if ((pos != -1) && (string.indexOf(' ', pos + 1) != -1))
			{
				pos++;
				nickName = string.substring(pos, string.indexOf(' ', pos));
				pos = string.indexOf(' ', pos);
			}

			if (nickName != null)
			{
				user = manager.getUser(nickName);

				if (user != null)
				{
					if ((pos != -1) && (string.indexOf(' ', pos + 1) != -1))
					{
						if (user != null)
						{
							pos++;

							try
							{
								time = Long.parseLong(string.substring(pos, string.indexOf(' ', pos)));
								user.setIdleTime(time);
							}
							catch (NumberFormatException e)
							{
								user.setIdleTime(0);
							}
							finally
							{
								pos = string.indexOf(' ', pos);
							}
						}
					}

					if (string.indexOf(' ', pos + 1) != -1)
					{
						pos++;

						try
						{
							time = Long.parseLong(string.substring(pos, string.indexOf(' ', pos)));
						}
						catch (NumberFormatException e)
						{
							user.setSignonTime(time);
						}
					}
				}
			}

		}
		else if (command.equals("318"))
		{
			// END OF WHOIS
		}
		else if (command.equals("319"))
		{
			// Unreal
			// Mage Spec :~#fruitbats ~#oper &#devel @#pc #mm
			// ircu
			// Mage Mage :@#brokend @#tekzonegaming +#php

			// This is ignored since the bot would probably add in those channels
			// Even though it isn't on them
		}
		else if (command.equals("320"))
		{
			// Mage Spec :is special
			// Ignored Event
		}
		else if (command.equals("330"))
		{
			// Mage Mage MageOfChrisz :is logged in as
			String nickName = null;
			pos = string.indexOf(' ');

			if ((pos != -1) && (string.indexOf(' ', pos + 1) != -1))
			{
				pos++;
				nickName = string.substring(pos, string.indexOf(pos));
				pos = string.indexOf(' ', pos);
			}

			if (nickName != null)
			{
				User user = manager.getUser(nickName);

				if ((user != null) && (string.indexOf(' ', pos + 1) != -1))
				{
					pos++;
					user.setAccountName(string.substring(pos, string.indexOf(' ', pos)));
				}
			}

		}
		else if (command.equals("378"))
		{
			// Mage Spec :is connecting from *@localhost 127.0.0.1

		}
		else if (command.equals("379"))
		{
			// Mage Spec :is using modes +iowghraAsxNWzt +kcfFjveGnNqSso
			// And now for the lazy way :<
			String[] array = string.split(" ");

			User user = manager.getUser(array[1]);

			if (user != null)
			{
				user.setUsermodes(array[5] + array[6]);
			}
		}
		else if (command.equals("671"))
		{
			String secureNick = string.substring(string.indexOf(' ') + 1, string.indexOf(" :"));
			User secureUser = manager.getUser(secureNick);

			if (secureUser != null)
			{
				secureUser.setSecureConnection(true);
			}
		}
		/*
		 * WHOWAS command replies
		 */
		else if (command.equals("314"))
		{
			// "<nick> <user> <host> * :<real name>"

		}
		else if (command.equals("369"))
		{
			// "<nick> :End of WHOWAS"
		}
		/*
		 * LIST replies
		 */
		else if (command.equals("321"))
		{
			logger.write(LogLevel.MINOR, "Obsolete command replie RPL_LISTSTART received");

		}
		else if (command.equals("322"))
		{
			// "<channel> <# visible> :<topic>"
		}
		else if (command.equals("323"))
		{
			// ":End of List"
		}
		
		/*
		 * TOPIC information
		 */
		
		/*
		 * 325 RPL_UNIQOPIS "<channel> <nickname>"
		 * 324 RPL_CHANNELMODEIS "<channel> <mode> <mode params>"
		 * 331 RPL_NOTOPIC "<channel> :No topic is set"
		 * 332 RPL_TOPIC "<channel> :<topic>" - When sending a TOPIC message to determine the channel 
		 * 										topic, one of two replies is sent. If the topic is set,
		 * 										RPL_TOPIC is sent back else RPL_NOTOPIC.
		 */

		else if (command.equals("325")) // RPL_UNIQOPIS
		{
			// "<channel> <nickname>"

		}
		else if (command.equals("324")) // RPL_CHANNELMODES
		{
			// Mage #mm +snrG
			// "<channel> <mode> <mode params>"

			String[] array = string.split(" ");

			if (manager.inChannel(array[1]))
			{
				manager.getChannel(array[1]).setChannelModes(array[2] + " " + array[3]);
			}

		}
		else if (command.equals("332")) // RPL_TOPIC
		{
			// Mage #mm :topci tcopiticapsiretpicicocpi | if (true)

			String[] array = string.split(" ", 3);

			Channel channel = manager.getChannel(array[1]);

			if (channel != null)
			{
				channel.setTopic(string.substring(string.indexOf(':') + 1));
			}
		}
		else if (command.equals("341")) // RPL_INVITING
		{
		}
		else if (command.equals("342")) // RPL_SUMMONING
		{
			// Whoever actually uses summoning nowadays is insane!
		}
		else if ((command.equals("346")) || // RPL_INVITELIST
				(command.equals("347"))) // RPL_ENDOFINVITELIST
		{
			// This also isn't supported by most IRCDs today
		}
		else if (command.equals("348")) // RPL_EXCEPTLIST
		{

		}
		else if (command.equals("349")) // RPL_ENDOFEXCEPTLIST
		{
			// not a big deal
		}
		else if (command.equals("351")) // RPL_VERSION
		{
			// Not supported afaik
		}
		else if (command.equals("352")) // RPL_WHOREPLY
		{
			String[] array = string.split(" ");

			if (array.length < 6)
			{
				logger.write(LogLevel.MINOR, "Invalid RPL_WHOREPLY caught!");
				logger.write(LogLevel.MINOR, line);
				return;
			}

			User user = manager.getUser(array[5]);

			if (user != null)
			{
				manager.addUserToChannel(user.getNick(), array[1]);
				user.setIdent(array[2]);
				user.setHostmask(array[3]);
				// user.setServer(array[4]);
				// user.setRealName(string.substring(line.indexOf(":") + 1));
				user.fetchPermission();
			}
		}
		else if (command.equals("315")) // RPL_ENDOFWHO
		{
			// We've reached the end of /WHO :D!
		}
		else if (command.equals("353")) // RPL_NAMEREPLY
		{
			String[] array = string.substring(string.indexOf(':')).split(" ");
			
			String realName;

			for (String name : array)
			{
				if ((name.startsWith("+")) || (name.startsWith("%")) || (name.startsWith("@"))
						|| (name.startsWith("&")) || (name.startsWith("~")))
				{
					realName = name.substring(1);
				}
				else
				{
					realName = name;
				}

				User user = null;
				if (!manager.userExists(realName))
				{
					user = manager.addUser(realName);
				}
				else
				{
					user = manager.getUser(realName);
				}

				if (string.indexOf("@ ") != -1)
				{
					user.setStatus(string.substring(string.indexOf("@ ") + 2, string.indexOf(" :")), name
							.substring(0, 1));
				}
			}

		}
		else if (command.equals("372")) // RPL_MOTD
		{
			if (string.indexOf(':') != -1)
			{
				motd += string.substring(string.indexOf(':') + 1);
			}
		}
		else if (command.equals("376")) // RPL_ENDOFMOTD
		{
			// Who really cares!
		}
		else if (command.equals("422")) // RPL_NOMOTD
		{
			motd = "MOTD File is missing or non-existant";
		}
		else if (command.equals("391")) // RPL_TIME
		{
			serverTime = string.substring(string.indexOf(':'));
		}
		else if (command.equals("433")) // ERR_NICKNAMEINUSE
		{
			logger.write(LogLevel.INFO, "Nick Name " + wantedNick + " already in use, switching to "
					+ wantedNick + "2");

			nick(wantedNick.concat("2"));

			actualNick = wantedNick + "2";
		}
		else if (command.equals("519")) // ERR_CANTJOINCHANNEL
		{

		}

		/*
		 * Plaintext events/commands These are commands which have no numerical counterpart (thank god) and
		 * are therefore separated.
		 */

		else if (command.equals("privmsg")) // PRIVMSG
		{
			int colonIndex = string.indexOf(':');

			if ((colonIndex == -1) || (colonIndex < string.indexOf(' ')) || (colonIndex == string.length()))
			{
				logger.write(LogLevel.MINOR, "Something was wrong when attempting to validate PRIVMSG");
				return;
			}

			String one = string.substring(colonIndex + 1, colonIndex + 2);

			/*
			 * Checks if it's actually a CTCP specific message
			 */
			if (one.equals("\u0001"))
			{
				if ((string.indexOf(':') + 2 > string.length())
						|| (string.indexOf(' ', string.indexOf(':')) > string.length()))
				{
					logger.write(LogLevel.MINOR, "Invalid CTCP message caught");
					logger.write(LogLevel.MINOR, "Line was: " + line);
					return;
				}

				String command2 = "";

				if (string.indexOf(' ', string.indexOf(':')) != -1)
				{
					command2 = string.substring(string.indexOf(':') + 2,
							string.indexOf(' ', string.indexOf(':'))).toLowerCase();
				}
				else
				{
					command2 = string.substring(string.indexOf(':') + 2, string.length() - 1).toLowerCase();
				}

				if (command2.equals("action"))
				{
					onAction(string.substring(string.indexOf(':') + 3 + command2.length(),
							string.length() - 1), string.substring(0, string.indexOf(' ')), nick, ident,
							hostmask);
				}
				else if (command2.equals("version"))
				{
					onVersion(nick, ident, hostmask);
				}
				else if (command2.equals("send"))
				{
				}
				else if (command2.equals("chat"))
				{
				}
				else
				{
					// BUGGY
					
					String	extra		= "",
							audience	= "";
					
					if ( (string.indexOf(':') != -1) && (string.indexOf(':') + 3 + command2.length() < string.length()) )
					{
						extra = string.substring(string.indexOf(':') + 3 + command2.length(), string.length() - 1);
					}
					
					if (string.indexOf(' ') != -1)
					{
						audience = string.substring(0, string.indexOf(' '));
					}
					
					onCTCP(command2.toUpperCase(), extra, audience, nick, ident, hostmask);
				}
			}
			else
			{
				onMessage(string.substring(string.indexOf(':') + 1),
						string.substring(0, string.indexOf(' ')), nick, ident, hostmask);
			}
		}
		else if (command.equals("notice")) // NOTICE
		{
			int colonIndex = string.indexOf(':');

			if ((colonIndex == -1) || (colonIndex < string.indexOf(' ')) || (colonIndex == string.length()))
			{
				logger.write(LogLevel.MINOR, "Something was wrong when attempting to validate NOTICE");
				return;
			}

			onNotice(string.substring(string.indexOf(':') + 1), string.substring(0, string.indexOf(' ')),
					nick, ident, hostmask);
		}
		else if (command.equals("wallops")) // WALLOP
		{
			// :Test
			onWallops(string.substring(2), nick, ident, hostmask);
		}
		else if (command.equals("quit")) // QUIT
		{
			String reason = ((string.indexOf(':') != -1) && (string.length() != string.indexOf(':'))) ? string.substring(string.indexOf(':') + 1) : "";
			
			if ( (reason.equalsIgnoreCase("*.net *.split")) || (reason.indexOf(actualServer) != -1) )
			{
				// TODO finish netsplit code
			}
			
			if (!nick.equalsIgnoreCase(actualNick))
			{
				onQuit(reason, nick, ident, hostmask);
				
				manager.removeUser(nick);
				
				if (nick.equalsIgnoreCase(wantedNick))
				{
					nick(wantedNick);
				}
			}
			else
			{
				onDisconnect();
			}
		}
		else if (command.equals("join")) // JOIN
		{
			String channel = (string.startsWith(":")) ? string.substring(1) : string;

			if (channel.indexOf(' ') != -1)
			{
				channel = channel.substring(0, channel.indexOf(' '));
			}

			if (nick.equalsIgnoreCase(actualNick))
			{
				// REDO
				manager.addChannel(channel, network);
				raw("WHO " + channel);
			}
			else
			{
				if (manager.userExists(nick))
				{
					manager.addUserToChannel(nick, channel);
					manager.updateUser(nick, ident, hostmask);
				}
				else
				{
					manager.addUser(nick, channel);
				}
			}
			
			onJoin(channel, nick, ident, hostmask);
		}
		else if (command.equals("part")) // PART
		{
			String channel = (string.indexOf(' ') != -1) ?
								string.substring(0, string.indexOf(' ')) : string;
			
			String reason  = (string.indexOf(' ') != -1) ?
								string.substring(string.indexOf(' ') + 1) : "";
			
			onPart(channel, reason, nick, ident, hostmask);
			
			if (nick.equalsIgnoreCase(actualNick))
			{
				manager.removeChannel(channel);
			}
			else
			{
				manager.removeUserFromChannel(nick, channel);
				manager.updateUser(nick, ident, hostmask);
			}
		}
		else if (command.equals("mode")) // MODE
		{
			// #devel +i
			// #devel +oq Mage Mage

			String			audience	= string.substring(0, string.indexOf(' '));
			ArrayList<Mode>	modes		= parseModes(string.substring(string.indexOf(' ') + 1));

			if (audience.equalsIgnoreCase(actualNick))
			{
				// Must be the bot
				
				for (Mode mode : modes)
				{
					if (mode.isPlusMode())
					{
						this.modes += mode.getMode();
					}
					else
					{
						this.modes = this.modes.replace(String.valueOf(mode.getMode()), "");
					}
				}
			}

			onMode(audience, modes, nick, ident, hostmask);
		}
		else if (command.equals("kick")) // KICK
		{
			// #devel Special :I need to test this, sorry
			// Full line: :Mage!Mage@marshmellow KICK #devel Special :Test

			String channel	= string.substring(0, string.indexOf(' '));
			String kicked	= string.substring(string.indexOf(' ') + 1, string.indexOf(" :"));
			String reason	= string.substring(string.indexOf(':') + 1);

			onKick(channel, kicked, reason, nick, ident, hostmask);
			
			manager.removeUserFromChannel(kicked, channel);
			manager.updateUser(nick, ident, hostmask);
		}
		else if (command.equals("nick")) // NICK
		{
			// :NewNick
			String nick2 = (string.startsWith(":")) ? string.substring(1) : string;

			onNick(nick2, nick, ident, hostmask);
			
			if (nick.equalsIgnoreCase(actualNick))
			{
				// The new nick is the bot
				actualNick = nick2;
			}
			else
			{
				if (manager.userExists(nick))
				{
					manager.changeUser(nick, nick2);
					manager.updateUser(nick2, ident, hostmask);
				}
				else
				{
					manager.addUser(nick2, ident, hostmask);
				}
			}
		}
		else if (command.equals("invite")) // INVITE
		{
			// RhinoBot :#devel
			String channel = string.substring(string.indexOf(' ') + 1);

			channel = (channel.startsWith(":")) ? channel.substring(1) : channel;

			onInvite(channel, nick, ident, hostmask);
		}
	}

	/*
	 * INTERPRETING
	 * Interpreting All these really do is pass on commands to the Rhino controller as well as the module
	 * controller
	 */

	/**
	 * 
	 */
	private final void onConnect ()
	{
		if (modulesEnabled)
		{
			IrcModuleController.onConnect(this);
		}
	
		if (rhinoEnabled)
		{
			rhino.runScript(BotEvent.onConnect, new Object[] { this }, getBasicInfo());
		}
	}
	
	/**
	 * onDisconnect event
	 */
	private final void onDisconnect ()
	{
		if (modulesEnabled)
		{
			IrcModuleController.onDisconnect(this);
		}
	
		if (rhinoEnabled)
		{
			rhino.runScript(BotEvent.onDisconnect, new Object[] { this }, getBasicInfo());
		}
	}

	/**
	 * @param reason
	 * @param nick
	 * @param ident2
	 * @param hostmask
	 */
	private final void onQuit (String reason, String nick, String ident, String hostmask)
	{
		User user = manager.getUser(nick);
		int permission = 0;

		if (user != null)
		{
			if (!user.canGetPermission())
			{
				user.setIdent(ident);
				user.setHostmask(hostmask);
			}

			if ((user.canGetPermission()) && (!user.hasGottenPermission()))
			{
				user.fetchPermission();
			}

			permission = user.getPermission();
		}
		
		final Object[] parameters = new Object[] { reason, nick, ident, hostmask, permission, this };
		
		if (modulesEnabled)
		{
			IrcModuleController.onQuit(reason, nick, ident, hostmask, this);
		}

		if (rhinoEnabled)
		{
			rhino.runScript(BotEvent.onQuit, parameters, getBasicInfo());
		}
	}
	
	/**
	 * @param channel
	 * @param nick
	 * @param ident
	 * @param hostmask
	 */
	private final void onJoin (String channel, String nick, String ident, String hostmask)
	{
		User user = manager.getUser(nick);
		int permission = 0;

		if (user == null)
		{
			user = manager.addUser(nick, ident, hostmask);
			
			if (manager.inChannel(channel))
			{
				manager.addUserToChannel(nick, channel);
			}
		}
		
		if (!user.canGetPermission())
		{
			user.setIdent(ident);
			user.setHostmask(hostmask);
		}

		if ((user.canGetPermission()) && (!user.hasGottenPermission()))
		{
			user.fetchPermission();
		}

		permission = user.getPermission();
		
		final Object[] parameters = new Object[] { channel, nick, ident, hostmask, permission, this };
		
		if (modulesEnabled)
		{
			IrcModuleController.onJoin(channel, nick, ident, hostmask, this);
		}

		if (rhinoEnabled)
		{
			rhino.runScript(BotEvent.onJoin, parameters, getBasicInfo());
		}
	}

	/**
	 * @param string
	 * @param nick
	 * @param ident
	 * @param hostmask
	 */
	private final void onPart (String channel, String reason, String nick, String ident, String hostmask)
	{
		User user = manager.getUser(nick);
		int permission = 0;
	
		if (user != null)
		{
			if (!user.canGetPermission())
			{
				user.setIdent(ident);
				user.setHostmask(hostmask);
			}
	
			if ((user.canGetPermission()) && (!user.hasGottenPermission()))
			{
				user.fetchPermission();
			}
	
			permission = user.getPermission();
		}
		
		final Object[] parameters = new Object[] { channel, reason, nick, ident, hostmask, permission, this };
		
		if (modulesEnabled)
		{
			IrcModuleController.onPart(channel, reason, nick, ident, hostmask, this);
		}
	
		if (rhinoEnabled)
		{
			rhino.runScript(BotEvent.onPart, parameters, getBasicInfo());
		}
	}

	/**
	 * @param channel
	 * @param kicked
	 * @param reason
	 * @param nick
	 * @param ident
	 * @param hostmask
	 */
	private final void onKick (String channel, String kicked, String reason, String nick, String ident,
			String hostmask)
	{
		User user = manager.getUser(nick);
		int permission = 0;
	
		if (user != null)
		{
			if (!user.canGetPermission())
			{
				user.setIdent(ident);
				user.setHostmask(hostmask);
			}
	
			if ((user.canGetPermission()) && (!user.hasGottenPermission()))
			{
				user.fetchPermission();
			}
	
			permission = user.getPermission();
		}
		
		final Object[] parameters = new Object[] { channel, kicked, reason, nick, ident, hostmask, permission, this };
		
		if (modulesEnabled)
		{
			IrcModuleController.onKick(channel, kicked, reason, nick, ident, hostmask, this);
		}
	
		if (rhinoEnabled)
		{
			rhino.runScript(BotEvent.onKick, parameters, getBasicInfo());
		}
	}

	/**
	 * @param channel
	 * @param plusModes
	 * @param minusmodes
	 * @param modes
	 * @param params
	 * @param raw
	 * @param nick
	 * @param ident
	 * @param hostmasks
	 */
	private final void onMode (String channel, ArrayList<Mode> modes, String nick, String ident, String hostmask)
	{
		User user = manager.getUser(nick);
		int permission = 0;
	
		if (user != null)
		{
			if (!user.canGetPermission())
			{
				user.setIdent(ident);
				user.setHostmask(hostmask);
			}
	
			if ((user.canGetPermission()) && (!user.hasGottenPermission()))
			{
				user.fetchPermission();
			}
	
			permission = user.getPermission();
		}

		final Object[] parameters = new Object[] { channel, modes, nick, ident,
				hostmask, permission, this };
		
		if (modulesEnabled)
		{
			IrcModuleController.onMode(channel, modes, nick, ident, hostmask, this);
		}
	
		if (rhinoEnabled)
		{
			rhino.runScript(BotEvent.onMode, parameters, getBasicInfo());
		}
	}

	/**
	 * @param channel
	 * @param nick
	 * @param ident
	 * @param hostmask
	 */
	private final void onInvite (String channel, String nick, String ident, String hostmask)
	{
		User user = manager.getUser(nick);
		int permission = 0;

		if (user == null)
		{
			user = manager.addUser(nick, ident, hostmask);
			
			if (manager.inChannel(channel))
			{
				manager.addUserToChannel(nick, channel);
			}
		}
		
		if (!user.canGetPermission())
		{
			user.setIdent(ident);
			user.setHostmask(hostmask);
		}

		if ((user.canGetPermission()) && (!user.hasGottenPermission()))
		{
			user.fetchPermission();
		}

		permission = user.getPermission();
		
		final Object[] parameters = new Object[] { channel, nick, ident, hostmask, permission, this };
		
		if (modulesEnabled)
		{
			IrcModuleController.onInvite(channel, nick, ident, hostmask, this);
		}

		if (rhinoEnabled)
		{
			rhino.runScript(BotEvent.onInvite, parameters, getBasicInfo());
		}
	}

	/**
	 * @param newNick
	 * @param nick
	 * @param ident
	 * @param hostmask
	 */
	private final void onNick (String newNick, String nick, String ident, String hostmask)
	{
		User user = manager.getUser(nick);
		int permission = 0;
	
		if (user != null)
		{
			if (!user.canGetPermission())
			{
				user.setIdent(ident);
				user.setHostmask(hostmask);
			}
	
			if ((user.canGetPermission()) && (!user.hasGottenPermission()))
			{
				user.fetchPermission();
			}
	
			permission = user.getPermission();
		}
		
		final Object[] parameters = new Object[] { newNick, nick, ident, hostmask, permission, this };		
		
		if (modulesEnabled)
		{
			IrcModuleController.onNick(newNick, nick, ident, hostmask, this);
		}
	
		if (rhinoEnabled)
		{
			rhino.runScript(BotEvent.onNick, parameters, getBasicInfo());
		}
	}
	
	/**
	 * @param message
	 * @param channel
	 * @param nick
	 * @param ident
	 * @param hostmask
	 */
	private final void onMessage (String message, String channel, String nick, String ident, String hostmask)
	{
		if (away) { return; }
		
		User user = manager.getUser(nick);
		int permission = 0;

		if (user == null)
		{
			user = manager.addUser(nick, ident, hostmask);
			
			if (manager.inChannel(channel))
			{
				manager.addUserToChannel(nick, channel);
			}
		}
		
		if (!user.canGetPermission())
		{
			user.setIdent(ident);
			user.setHostmask(hostmask);
		}

		if ((user.canGetPermission()) && (!user.hasGottenPermission()))
		{
			user.fetchPermission();
		}

		permission = user.getPermission();
		
		final Object[] parameters = new Object[] { message, channel, nick, ident, hostmask, permission, this };
	
		if (modulesEnabled)
		{
			IrcModuleController.onMessage(message, channel, nick, ident, hostmask, this);
		}
	
		if (rhinoEnabled)
		{
			rhino.runScript(BotEvent.onMessage, parameters, getBasicInfo());
		}
	}
	
	/**
	 * @param action
	 * @param channel
	 * @param nick
	 * @param ident
	 * @param hostmask
	 */
	private final void onAction (String action, String channel, String nick, String ident, String hostmask)
	{
		if (away) { return; }
		
		User user = manager.getUser(nick);
		int permission = 0;

		if (user == null)
		{
			user = manager.addUser(nick, ident, hostmask);
			
			if (manager.inChannel(channel))
			{
				manager.addUserToChannel(nick, channel);
			}
		}
		
		if (!user.canGetPermission())
		{
			user.setIdent(ident);
			user.setHostmask(hostmask);
		}

		if ((user.canGetPermission()) && (!user.hasGottenPermission()))
		{
			user.fetchPermission();
		}

		permission = user.getPermission();
		
		final Object[] parameters = new Object[] { action, channel, nick, ident, hostmask, permission, this };
	
		if (modulesEnabled)
		{
			IrcModuleController.onAction(action, channel, nick, ident, hostmask, this);
		}
	
		if (rhinoEnabled)
		{
			rhino.runScript(BotEvent.onAction, parameters, getBasicInfo());
		}
	}

	/**
	 * @param message
	 * @param channel
	 * @param nick
	 * @param ident
	 * @param hostmask
	 */
	private final void onNotice (String message, String channel, String nick, String ident, String hostmask)
	{
		User user = manager.getUser(nick);
		int permission = 0;

		if (user == null)
		{
			user = manager.addUser(nick, ident, hostmask);
			
			if (manager.inChannel(channel))
			{
				manager.addUserToChannel(nick, channel);
			}
		}
		
		if (!user.canGetPermission())
		{
			user.setIdent(ident);
			user.setHostmask(hostmask);
		}

		if ((user.canGetPermission()) && (!user.hasGottenPermission()))
		{
			user.fetchPermission();
		}

		permission = user.getPermission();
		
		final Object[] parameters = new Object[] {  message, channel, nick, ident, hostmask, permission, this };
		
		if (modulesEnabled)
		{
			IrcModuleController.onNotice(message, channel, nick, ident, hostmask, this);
		}
	
		if (rhinoEnabled)
		{
			rhino.runScript(BotEvent.onNotice, parameters, getBasicInfo());
		}
	}

	/**
	 * 
	 * @param message
	 * @param nick
	 * @param ident
	 * @param hostmask
	 */
	private final void onWallops (String message, String nick, String ident, String hostmask)
	{
		User user = manager.getUser(nick);
		int permission = 0;
	
		if (user != null)
		{
			if (!user.canGetPermission())
			{
				user.setIdent(ident);
				user.setHostmask(hostmask);
			}
	
			if ((user.canGetPermission()) && (!user.hasGottenPermission()))
			{
				user.fetchPermission();
			}
	
			permission = user.getPermission();
		}
		
		final Object[] parameters = new Object[] { message, nick, ident, hostmask, permission, this };
		
		if (modulesEnabled)
		{
			IrcModuleController.onWallops(message, nick, ident, hostmask, this);
		}
	
		if (rhinoEnabled)
		{
			rhino.runScript(BotEvent.onWallops, parameters, getBasicInfo());
		}
	}

	/**
	 * @param command
	 * @param extra
	 * @param audience
	 * @param nick
	 * @param ident
	 * @param hostmask
	 */
	private final void onCTCP (String command, String extra, String audience, String nick, String ident, String hostmask)
	{
		User user = manager.getUser(nick);
		int permission = 0;

		if (user == null)
		{
			user = manager.addUser(nick, ident, hostmask);
		}
		
		if (!user.canGetPermission())
		{
			user.setIdent(ident);
			user.setHostmask(hostmask);
		}

		if ((user.canGetPermission()) && (!user.hasGottenPermission()))
		{
			user.fetchPermission();
		}

		permission = user.getPermission();
		
		final Object[] parameters = new Object[] { command, extra, audience, nick, ident, hostmask, permission, this };
		
		if (modulesEnabled)
		{
			IrcModuleController.onCTCP(command, extra, audience, nick, ident, hostmask, this);
		}
		
		if (rhinoEnabled)
		{
			rhino.runScript(BotEvent.onCTCP, parameters, getBasicInfo());
		}
	}
	
	/**
	 * @param nick
	 * @param ident2
	 * @param hostmask
	 */
	private final void onVersion (String nick, String ident, String hostmask)
	{
		final Object[] parameters = new Object[] { nick, ident, hostmask, this };
		
		if (modulesEnabled)
		{
			IrcModuleController.onVersion(nick, ident, hostmask, this);
		}

		if (rhinoEnabled)
		{
			rhino.runScript(BotEvent.onVersion, parameters, getBasicInfo());
		}
	}
}
