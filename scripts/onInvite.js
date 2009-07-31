// include basicInformation.js

function onInvite (channel, nick, ident, hostmask, permission, bot)
{
	if (permission >= 70)
	{
		bot.notice(nick, "Joining " + channel + " by your invite");
		bot.join(channel);
	}
	else
	{
		bot.notice(nick, "Sorry, you do not have the permission to use INVITE");
	}
}