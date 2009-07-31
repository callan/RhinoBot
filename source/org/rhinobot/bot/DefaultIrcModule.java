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
package org.rhinobot.bot;

import java.util.ArrayList;

import org.kernel.Kernel;
import org.rhinobot.bot.manager.Mode;
import org.rhinobot.bot.manager.User;

/**
 * @author Chris
 */
@SuppressWarnings("unused")
public final class DefaultIrcModule implements IrcModule
{

	/**
	 * Overidden
	 * 
	 * @see org.rhinobot.bot.IrcModule#onConnect(org.rhinobot.bot.RhinoBot)
	 */
	public final void onConnect (final RhinoBot bot)
	{
	}

	/**
	 * Overidden
	 * 
	 * @see org.rhinobot.bot.IrcModule#onDisconnect(org.rhinobot.bot.RhinoBot)
	 */
	public final void onDisconnect (final RhinoBot bot)
	{
	}

	/**
	 * Overidden
	 * 
	 * @see org.rhinobot.bot.IrcModule#onQuit(java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, org.rhinobot.bot.RhinoBot)
	 */
	public final void onQuit (final String reason, final String nick, final String ident,
			final String hostmask, final RhinoBot bot)
	{
	}

	/**
	 * Overidden
	 * 
	 * @see org.rhinobot.bot.IrcModule#onJoin(java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, org.rhinobot.bot.RhinoBot)
	 */
	public final void onJoin (final String channel, final String nick, final String ident,
			final String hostmask, final RhinoBot bot)
	{
	}

	/**
	 * Overidden
	 * 
	 * @see org.rhinobot.bot.IrcModule#onPart(java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String, org.rhinobot.bot.RhinoBot)
	 */
	public final void onPart (final String channel, final String reason, final String nick,
			final String ident, final String hostmask, final RhinoBot bot)
	{
	}

	/**
	 * Overidden
	 * 
	 * @see org.rhinobot.bot.IrcModule#onKick(java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String, org.rhinobot.bot.RhinoBot)
	 */
	public final void onKick (final String channel, final String kicked, final String reason,
			final String nick, final String ident, final String hostmask, final RhinoBot bot)
	{
	}

	/**
	 * Overidden
	 * 
	 * @see org.rhinobot.bot.IrcModule#onMode(java.lang.String, ArrayList, java.lang.String, java.lang.String,
	 *      java.lang.String, org.rhinobot.bot.RhinoBot)
	 */
	public final void onMode (final String channel, final ArrayList<Mode> modes, final String nick,
			final String ident, final String hostmask, final RhinoBot bot)
	{
	}

	/**
	 * Overidden
	 * 
	 * @see org.rhinobot.bot.IrcModule#onInvite(java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, org.rhinobot.bot.RhinoBot)
	 */
	public final void onInvite (final String channel, final String nick, final String ident,
			final String hostmask, final RhinoBot bot)
	{
	}

	/**
	 * Overidden
	 * 
	 * @see org.rhinobot.bot.IrcModule#onNick(java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, org.rhinobot.bot.RhinoBot)
	 */
	public final void onNick (final String newNick, final String nick, final String ident,
			final String hostmask, final RhinoBot bot)
	{
	}

	/**
	 * Overidden
	 * 
	 * @see org.rhinobot.bot.IrcModule#onMessage(java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String, org.rhinobot.bot.RhinoBot)
	 */
	public final void onMessage (final String message, final String channel, final String nick,
			final String ident, final String hostmask, final RhinoBot bot)
	{
		if (message.length() < 1) { return; }

		final String[] fragments = message.split(" ");
		final int length = fragments.length;

		if (length < 1) { return; }

		final String prefix = (fragments[0].length() > 1) ? fragments[0].substring(1) : "";
		final User user = bot.getManager().getUser(nick);
		int permission = 0;

		if (user != null)
			permission = user.getPermission();

		if ((prefix.equals("")) || (user == null))
			return;

		if (prefix.equals("java-test"))
		{
			bot.notice(nick, "Java Test successfully works!");
		}
		else if ((prefix.equals("kernel")) && (length > 1))
		{
			if (permission >= 90)
			{
				if (fragments[1].equals("load"))
				{
					String module = "";

					if (length > 2)
					{
						ClassLoader loader = ClassLoader.getSystemClassLoader();

						module = fragments[2];
						Kernel kernel = Kernel.getInstance();
						kernel.loadModule(module, loader);
						bot.notice(nick, "Sent request to Kernel");
					}
					else
					{
						bot.notice(nick, "More parameters required");
					}
				}
			}
			else
			{
				bot.notice(nick, "Access Denied");
			}
		}
	}

	/**
	 * Overidden
	 * 
	 * @see org.rhinobot.bot.IrcModule#onAction(java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String, org.rhinobot.bot.RhinoBot)
	 */
	public final void onAction (final String action, final String channel, final String nick,
			final String ident, final String hostmask, final RhinoBot bot)
	{
	}

	/**
	 * Overidden
	 * 
	 * @see org.rhinobot.bot.IrcModule#onNotice(java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String, org.rhinobot.bot.RhinoBot)
	 */
	public final void onNotice (final String message, final String channel, final String nick,
			final String ident, final String hostmask, final RhinoBot bot)
	{
	}

	/**
	 * Overidden
	 * 
	 * @see org.rhinobot.bot.IrcModule#onWallops(java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, org.rhinobot.bot.RhinoBot)
	 */
	public final void onWallops (final String message, final String nick, final String ident,
			final String hostmask, final RhinoBot bot)
	{
	}

	/**
	 * Overidden
	 * 
	 * @see org.rhinobot.bot.IrcModule#onCTCP(java.lang.String, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String, java.lang.String, org.rhinobot.bot.RhinoBot)
	 */
	public final void onCTCP (final String command, final String extra, final String audience,
			final String nick, final String ident, final String hostmask, final RhinoBot bot)
	{
	}

	/**
	 * Overidden
	 * 
	 * @see org.rhinobot.bot.IrcModule#onVersion(java.lang.String, java.lang.String, java.lang.String,
	 *      org.rhinobot.bot.RhinoBot)
	 */
	public final void onVersion (final String nick, final String ident, final String hostmask,
			final RhinoBot bot)
	{
	}
}
