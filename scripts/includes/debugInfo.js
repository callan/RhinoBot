
function debugInfo (bot)
{
	var chn = "#devel";
	
	bot.privmsg(chn, "bot nick = " + botNick);
	bot.privmsg(chn, "bot ident = " + botIdent);
	bot.privmsg(chn, "id = " + instanceID);
	bot.privmsg(chn, "server = " + server);
	bot.privmsg(chn, "network = " + network);
	bot.privmsg(chn, "charset = " + charset);
	bot.privmsg(chn, "availChanModes = " + availChanModes);
	bot.privmsg(chn, "availUserModes = " + availUserModes);
	bot.privmsg(chn, "modesWithParameters = " + modesWithParameters);
	bot.privmsg(chn, "chanTypes = " + chanTypes);
	bot.privmsg(chn, "maxSilence = " + maxSilence);
	bot.privmsg(chn, "maxChannels = " + maxChannels);
	bot.privmsg(chn, "maxExceptions = " + maxExceptions);
	bot.privmsg(chn, "maxInvites = " + maxInvites);
	bot.privmsg(chn, "maxBans = " + maxBans);
	bot.privmsg(chn, "maxModes = " + maxModes);
	bot.privmsg(chn, "maxNickLength = " + maxNickLength);
	bot.privmsg(chn, "maxTopicLength = " + maxTopicLength);
	bot.privmsg(chn, "maxAwayLength = " + maxAwayLength);
	bot.privmsg(chn, "maxKickLength = " + maxKickLength);
	bot.privmsg(chn, "maxChannelLength = " + maxChannelLength);
	bot.privmsg(chn, "commands = " + commands);
}