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
package org.rhinobot.test;

import java.util.ArrayList;
import java.util.HashMap;

public class BounceParser
{
	private String chanTypes;
	private String network;
	
	private int maxSilence = -1;
	private int maxChannels = -1;
	private int maxExceptions = -1;
	private int maxInvites = -1;
	private int maxBans = -1;
	private int maxModes = -1; // ??
	private int maxNickLength = -1;
	private int maxTopicLength = -1;
	private int maxAwayLength = -1;
	private int maxKickLength = -1;
	private int	maxChannelLength = -1;
	
	private String[] prefixes;
	
	private HashMap<String, String> chanModes = new HashMap<String, String>(4);
	
	private ArrayList<String>		commands	= new ArrayList<String>();
	
	/**
	 * @param args
	 */
	public static void main (String[] args)
	{
//		UnrealIRCD
		
//		RhinoBot CMDS=KNOCK,MAP,DCCALLOW,USERIP SAFELIST HCN MAXCHANNELS=20 CHANLIMIT=#:20 MAXLIST=b:60,e:60,I:60 NICKLEN=30 CHANNELLEN=32 TOPICLEN=307 KICKLEN=307 AWAYLEN=307 MAXTARGETS=20 WALLCHOPS :are supported by this server
//		RhinoBot WATCH=128 SILENCE=15 MODES=12 CHANTYPES=# PREFIX=(qaohv)~&@%+ CHANMODES=beI,kfL,lj,psmntirRcOAQKVGCuzNSMTG NETWORK=YRC CASEMAPPING=ascii EXTBAN=~,cqnrf ELIST=MNUCT STATUSMSG=~&@%+ EXCEPTS INVEX :are supported by this server
		
		String[] unreal = new String[] {
				"CMDS=KNOCK,MAP,DCCALLOW,USERIP SAFELIST HCN MAXCHANNELS=20 CHANLIMIT=#:20 MAXLIST=b:60,e:60,I:60 NICKLEN=30 CHANNELLEN=32 TOPICLEN=307 KICKLEN=307 AWAYLEN=307 MAXTARGETS=20 WALLCHOPS :are supported by this server",
				"WATCH=128 SILENCE=15 MODES=12 CHANTYPES=# PREFIX=(qaohv)~&@%+ CHANMODES=beI,kfL,lj,psmntirRcOAQKVGCuzNSMTG NETWORK=YRC CASEMAPPING=ascii EXTBAN=~,cqnrf ELIST=MNUCT STATUSMSG=~&@%+ EXCEPTS INVEX :are supported by this server"
		};
		
//		ircu
		
//		RhinoBot ANNOUNCE WHOX WALLCHOPS WALLVOICES USERIP CPRIVMSG CNOTICE SILENCE=15 MODES=6 MAXCHANNELS=20 MAXBANS=45 NICKLEN=30 MAXNICKLEN=30 :are supported by this server
//		RhinoBot TOPICLEN=300 AWAYLEN=200 KICKLEN=300 CHANTYPES=#& PREFIX=(ov)@+ CHANMODES=b,k,l,imnpstrDcC CASEMAPPING=rfc1459 NETWORK=GameSurge :are supported by this server
		
		String[] ircu = new String[] {
				"ANNOUNCE WHOX WALLCHOPS WALLVOICES USERIP CPRIVMSG CNOTICE SILENCE=15 MODES=6 MAXCHANNELS=20 MAXBANS=45 NICKLEN=30 MAXNICKLEN=30 :are supported by this server",
				"TOPICLEN=300 AWAYLEN=200 KICKLEN=300 CHANTYPES=#& PREFIX=(ov)@+ CHANMODES=b,k,l,imnpstrDcC CASEMAPPING=rfc1459 NETWORK=GameSurge :are supported by this server"
		};
		
//		dancer
		
//		RhinoBot IRCD=dancer CAPAB CHANTYPES=# EXCEPTS INVEX CHANMODES=bdeIq,k,lfJD,cgijLmnPQrRstz CHANLIMIT=#:20 PREFIX=(ov)@+ MAXLIST=bdeI:50 MODES=4 STATUSMSG=@ KNOCK NICKLEN=16 :are supported by this server
//		RhinoBot SAFELIST CASEMAPPING=ascii CHANNELLEN=30 TOPICLEN=450 KICKLEN=450 KEYLEN=23 USERLEN=10 HOSTLEN=63 SILENCE=50 :are supported by this server
		
		String[] dancer = new String[] {
				"IRCD=dancer CAPAB CHANTYPES=# EXCEPTS INVEX CHANMODES=bdeIq,k,lfJD,cgijLmnPQrRstz CHANLIMIT=#:20 PREFIX=(ov)@+ MAXLIST=bdeI:50 MODES=4 STATUSMSG=@ KNOCK NICKLEN=16 :are supported by this server",
				"SAFELIST CASEMAPPING=ascii CHANNELLEN=30 TOPICLEN=450 KICKLEN=450 KEYLEN=23 USERLEN=10 HOSTLEN=63 SILENCE=50 :are supported by this server"
		};
		
		BounceParser unrealParser = new BounceParser();
		BounceParser ircuParser   = new BounceParser();
		BounceParser dancerParser = new BounceParser();
		
		System.out.println("UnrealIRCD (WhyAreSee)");
		for (String bounce : unreal)
		{
			unrealParser.parseBounce(bounce);
		}
		unrealParser.debug();
		
		System.out.println("IRCU (GameSurge)");
		for (String bounce : ircu)
		{
			ircuParser.parseBounce(bounce);
		}
		ircuParser.debug();
		
		System.out.println("Dancer (freenode)");
		for (String bounce : dancer)
		{
			dancerParser.parseBounce(bounce);
		}
		dancerParser.debug();
	}
	
	public void debug ()
	{
		System.out.print("Misc: \r\n" +
				"\tChannel Types: " + chanTypes + "\r\n" +
				"\tNetwork: " + network + "\r\n" +
				"\tSILENCE max: " + maxSilence + "\r\n" +
				"\tMax amount of channels: " + maxChannels + "\r\n" +
				"\tMax amount of exceptions: " + maxExceptions + "\r\n" +
				"\tMax amount of +I invites: " + maxInvites + "\r\n" +
				"\tMax amount of bans: " + maxBans + "\r\n" +
				"\tMax amount of parameter-based modes on one MODE line: " + maxModes + "\r\n" +
				"\tMax NICK length: " + maxNickLength + "\r\n" +
				"\tMax TOPIC length: " + maxTopicLength + "\r\n" +
				"\tMax AWAY length: " + maxAwayLength + "\r\n" +
				"\tMax KICK reason length: " + maxKickLength + "\r\n" +
				"\tMax channel name length: " + maxChannelLength + "\r\n"
		);
		
		System.out.println();
		System.out.print("Channel user prefixes allowed: \r\n" +
				"\tMODE style: " + prefixes[0] + "\r\n" +
				"\tLiteral: " + prefixes[1] + "\r\n"
		);
		
		System.out.println();
		System.out.print("Channel Modes: \r\n" +
				"\tControl-style modes: " + chanModes.get("control") + "\r\n" +
				"\tProtection-style modes: " + chanModes.get("protection") + "\r\n" +
				"\tLimitation-style modes: " + chanModes.get("limits") + "\r\n" +
				"\tNormal modes: " + chanModes.get("normal") + "\r\n"
		);
		
		System.out.println();
		System.out.println("Commands supported:");
		
		System.out.print("\t");
		
		for (String command : commands)
		{
			System.out.print(command + " ");
		}
		
		System.out.println();
		System.out.println();
		System.out.println();
	}
	
	private void parseMultiValues (String[] nameval)
	{
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
			network = nameval[1];
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
			if (modes.length == 4)
			{
				// We're ok to resume normal parsing!
				chanModes.put("control", modes[0]);
				chanModes.put("protection", modes[1]);
				chanModes.put("limits", modes[2]);
				chanModes.put("normal", modes[3]);
			}
			else
			{
				chanModes.put("normal", nameval[1]);
			}
		}
		else if (nameval[0].equalsIgnoreCase("prefix"))
		{
			prefixes = nameval[1].substring(1).split("\\)");
		}
		else if (nameval[0].equalsIgnoreCase("maxlist"))
		{
			int p = nameval[1].indexOf(",");
			
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
				p				= nameval[1].indexOf(",", p + 1);
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
				commands.add(cmd);
			}
		}
	}
	
	public void parseBounce (String bounce)
	{
		int			index	= 1;
		String[]	split	= bounce.substring(bounce.indexOf(' ') + 1, bounce.lastIndexOf(':')).split(" ");
		
		for (; index < split.length; index++)
		{
			// Must have a NAME=VALUE type thing
			if (split[index].indexOf('=') != -1)
			{
				parseMultiValues(split[index].split("="));
			}
			else
			{
				commands.add(split[index]);
			}
		}
	}
}
