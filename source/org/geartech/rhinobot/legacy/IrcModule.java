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
package org.geartech.rhinobot.legacy;

import java.util.ArrayList;

import org.geartech.rhinobot.Core;
import org.geartech.rhinobot.manager.Mode;


/**
 * Easy, common interface for modules in RhinoBot
 */
public interface IrcModule
{
	/**
	 * When the bot connects
	 * @param bot
	 */
	void onConnect (final Core bot);

	/**
	 * When the bot disconnects
	 * @param bot
	 */
	void onDisconnect (final Core bot);

	/**
	 * QUIT
	 * @param reason
	 * @param nick
	 * @param ident
	 * @param hostmask
	 * @param bot
	 */
	void onQuit (final String reason, final String nick, final String ident, final String hostmask, final Core bot);

	/**
	 * JOIN
	 * @param channel
	 * @param nick
	 * @param ident
	 * @param hostmask
	 * @param bot
	 */
	void onJoin (final String channel, final String nick, final String ident, final String hostmask, final Core bot);

	/**
	 * PART
	 * @param channel
	 * @param reason
	 * @param nick
	 * @param ident
	 * @param hostmask
	 * @param bot
	 */
	void onPart (final String channel, final String reason, final String nick, final String ident, final String hostmask,
			final Core bot);

	/**
	 * KICK
	 * @param channel
	 * @param kicked
	 * @param reason
	 * @param nick
	 * @param ident
	 * @param hostmask
	 * @param bot
	 */
	void onKick (final String channel, final String kicked, final String reason, final String nick, final String ident,
			final String hostmask, final Core bot);
	
	/**
	 * MODE
	 * @param channel
	 * @param modes
	 * @param nick
	 * @param ident
	 * @param hostmask
	 * @param bot
	 */
	void onMode (final String channel, final ArrayList<Mode> modes, final String nick, final String ident, final String hostmask,
			final Core bot);

	/**
	 * INVITE
	 * @param channel
	 * @param nick
	 * @param ident
	 * @param hostmask
	 * @param bot
	 */
	void onInvite (final String channel, final String nick, final String ident, final String hostmask, final Core bot);
	
	/**
	 * NICK
	 * @param newNick
	 * @param nick
	 * @param ident
	 * @param hostmask
	 * @param bot
	 */
	void onNick (final String newNick, final String nick, final String ident, final String hostmask, final Core bot);
	
	/**
	 * PRIVMSG
	 * @param message
	 * @param channel
	 * @param nick
	 * @param ident
	 * @param hostmask
	 * @param bot
	 */
	void onMessage (final String message, final String channel, final String nick, final String ident, final String hostmask,
			final Core bot);

	/**
	 * An emote, usually called action
	 * @param action
	 * @param channel
	 * @param nick
	 * @param ident
	 * @param hostmask
	 * @param bot
	 */
	void onAction (final String action, final String channel, final String nick, final String ident, final String hostmask,
			final Core bot);

	/**
	 * NOTICE
	 * @param message
	 * @param channel
	 * @param nick
	 * @param ident
	 * @param hostmask
	 * @param bot
	 */
	void onNotice (final String message, final String channel, final String nick, final String ident, final String hostmask,
			final Core bot);
	
	/**
	 * WALLOPS
	 * @param message
	 * @param nick
	 * @param ident
	 * @param hostmask
	 * @param bot
	 */
	void onWallops (final String message, final String nick, final String ident, final String hostmask, final Core bot);

	/**
	 * CTCP Command.
	 * @param command
	 * @param extra
	 * @param audience
	 * @param ident
	 * @param hostmask
	 * @param bot
	 */
	void onCTCP (final String command, final String extra, final String audience, final String nick, final String ident,
			final String hostmask, final Core bot);
	
	/**
	 * A CTCP version message
	 * @param nick
	 * @param ident
	 * @param hostmask
	 * @param bot
	 */
	void onVersion (final String nick, final String ident, final String hostmask, final Core bot);
}
