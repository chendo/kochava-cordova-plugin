
(function(cordova) {
 
	function KochavaBeaconNotification() {}

// Event spawned when a notification is received while the application is active
KochavaBeaconNotification.prototype.notificationCallback = function(notification) {

//    alert('success');

    var ev = document.createEvent('HTMLEvents');
    ev.notification = notification;
    ev.initEvent('beacon-notification', true, true, arguments);
    document.dispatchEvent(ev);
};

cordova.addConstructor(function() {

                       console.log('RECEIVED NOTIFICATION! Push-notification! ' );
                       if(!window) window = {};
                       window.beaconNotification = new KochavaBeaconNotification();
                       });

})(window.cordova || window.Cordova || window.PhoneGap);

