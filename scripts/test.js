
#define LOL function

#include 'base.js'

#ifdef ON_MESSAGE
#include 'onMessage.js'
#include 'basicInformation.js'
#include 'debugInfo.js'
#elif ON_ACTION
#include 'onAction.js'
#endif

var eventHandler = Rhino.getEventHandler();
eventHandler.register(Rhino.event('onMessage'), eventCall);

function eventCall ()
{
	assert('eventHandler != null', true);
	if (eventHandler.getEvent() == 'onMessage')
	{
		var onMessage = eventHandler.to(onMessage);
		var msg = onMessage.getMessage();
		var chn = onMessage.getChannel();
		var usr = onMessage.getUser();
		var per = onMessage.getPermission();
	}
#ifdef ON_ACTION
	if (eventHandler.getEvent() == 'onAction')
	{
		var onAction = eventHandler.to(onAction);
	}
#endif
}

LOL test ()
{
	return true;
}