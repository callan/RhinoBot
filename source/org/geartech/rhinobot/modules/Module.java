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
package org.geartech.rhinobot.modules;

import java.util.ArrayList;

import org.geartech.rhinobot.RhinoBot;
import org.geartech.rhinobot.manager.Channel;
import org.geartech.rhinobot.manager.Mode;
import org.geartech.rhinobot.manager.User;

/**
 * 
 */
public interface Module
{
	void onInit (RhinoBot bot);
	
	void onConnect (String network, String server, int port, String driver);
	void onPostConnect ();

	void onPreDisconnect ();
	void onDisconnect ();
	
	void onNumeric (int numeric, String[] data, User user);
	
	void onQuit (String reason, User user);
	void onJoin (Channel channel, User user);
	void onPart (Channel channel, String reason, User user);
	void onKick (Channel channel, String reason, User user);
	void onMode (Channel channel, ArrayList<Mode> modes, User user);
	void onInvite (Channel channel, User user);
	void onNick (String previousNick, User user);
	
	void onMessage (String message, User user);
	void onMessage (String message, Channel channel, User user);

	void onAction (String message, User user);
	void onAction (String message, Channel channel, User user);

	void onCTCP (String message, User user);
	void onCTCP (String message, Channel channel, User user);
	
	void onNotice (String message, User user);
	void onNotice (String message, Channel channel, User user);

	void onWallops (String message, User user);
	
	void onRaw (String rawMessage, User user);
}
