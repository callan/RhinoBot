var botNick,
	botIdent,
	instanceID,
	server,
	network,
	charset,
	availChanModes,
	availUserModes,
	modesWithParameters,
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
	commands;

function basicInformation (	botNick1,
							botIdent1,
							instanceID1,
							server1,
							network1,
							charset1,
							availChanModes1,
							availUserModes1,
							modesWithParameters1,
							chanTypes1,
							maxSilence1,
							maxChannels1,
							maxExceptions1,
							maxInvites1,
							maxBans1,
							maxModes1,
							maxNickLength1,
							maxTopicLength1,
							maxAwayLength1,
							maxKickLength1,
							maxChannelLength1,
							commands1 )
{
	botNick = botNick1;
	botIdent = botIdent1;
	instanceID = instanceID1;
	server = server1;
	network = network1;
	charset = charset1;
	availChanModes = availChanModes1;
	availUserModes = availUserModes1;
	modesWithParameters = modesWithParameters1;
	chanTypes = chanTypes1;
	maxSilence = maxSilence1;
	maxChannels = maxChannels1;
	maxExceptions = maxExceptions1;
	maxInvites = maxInvites1;
	maxBans = maxBans1;
	maxModes = maxModes1;
	maxNickLength = maxNickLength1;
	maxTopicLength = maxTopicLength1;
	maxAwayLength = maxAwayLength1;
	maxKickLength = maxKickLength1;
	maxChannelLength = maxChannelLength1;
	commands = commands1;
}
