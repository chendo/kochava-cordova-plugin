
/*
 * File Nmame  : kochava.js
 * Author     : Kochava
 * Description : This file initiates the call to the native library from the plugin
 */

function callCordova(command) {
	var args = Array.prototype.slice.call(arguments, 1);
	cordova.exec(function callback(data) { },
				 function errorHandler(err) { },
				 'KochavaEvents',
				 command,
				 args
				 );
}

function callCordovaCallback(command, callback) {
	var args = Array.prototype.slice.call(arguments, 2);
	cordova.exec(callback,
				 function errorHandler(err) { },
				 'KochavaEvents',
				 command,
				 args
				 );
}


var Kochava = {
	
	Event:function(eventName, eventValue)	{
		callCordova('TrackEvent', eventName, eventValue);
	},
		
	EventWithReceipt:function(eventName, eventValue, eventReceipt)	{
		callCordova('TrackEventWithReceipt', eventName, eventValue, eventReceipt);
	},
		
	SpatialEvent:function(spatialEvent, x, y, z)	{
		callCordova('SpatialEvent', spatialEvent, x, y, z);
	},
		
	DeeplinkEvent:function(uri, callingApp)	{
		callCordova('DeeplinkEvent', uri, callingApp);
	},
		
	EnableLogging:function(state){
		callCordova('EnableLogging', state);
	},
		
	LimitAdTracking:function(state)	{
		callCordova('LimitAdTracking', state);
	},
		
	IdentityLink:function(parms)		{
		callCordova('IdentityLink', parms);
	},
		
	RetrieveAttribution:function(callback)	{
		callCordovaCallback('RetrieveAttribution', callback);
	},
		
	GetKochavaDeviceId:function(callback)		{
		callCordovaCallback('GetKochavaDeviceId', callback);
	},
	Init: function(options) {
		callCordova('initKochava', options)
	},
};

module.exports = Kochava;

