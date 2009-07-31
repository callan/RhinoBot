// include basicInformation.js

function onNotice (message, channel, nick, ident, hostmask, permission, bot)
{
	if ((nick.toLowerCase() == "authserv") && (message.toLowerCase().indexOf('i recognize you') != -1))
	{
		bot.mode(botNick, "+x");
		bot.join("#PHP");
	}
/*
	if (channel == botNick)
	{
		bot.notice(nick, "Notice seems to be working fine!");
	}
	else
	{
		bot.notice(channel, "Notice seems to be working quite nicely!");
	}
*/
}