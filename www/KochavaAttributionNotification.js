
(function(cordova) {
 
	function AttributionNotification() {}

// Event spawned when a notification is received while the application is active
AttributionNotification.prototype.notificationCallback = function(notification) {

    var ev = document.createEvent('HTMLEvents');
    ev.notification = notification;
    ev.initEvent('attribution-notification', true, true, arguments);
    document.dispatchEvent(ev);
};

cordova.addConstructor(function() {

                       console.log('RECEIVED NOTIFICATION! attribution-notification! ' );
                       if(!window) window = {};
                       window.attributionNotification = new AttributionNotification();
                       });

})(window.cordova || window.Cordova || window.PhoneGap);

