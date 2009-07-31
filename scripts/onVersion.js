// include basicInformation.js

function onVersion (nick, ident, hostmask, bot)
{
	bot.ctcpReply(nick, "VERSION", bot.version);
}