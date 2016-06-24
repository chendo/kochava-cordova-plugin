/*
 * File Nmame  : KochavaEvents.h
 * Author     : Kochava
 * Description : This is the plugin class header file.
 */

#import <Cordova/CDV.h>
#import "TrackAndAd.h"



@interface KochavaEvents : CDVPlugin <KochavaTrackerClientDelegate>

- (void)initKochava:(CDVInvokedUrlCommand*)args;
- (void)TrackEvent:(CDVInvokedUrlCommand*)args;
- (void)TrackEventWithReceipt:(CDVInvokedUrlCommand*)args;
- (void)SpatialEvent:(CDVInvokedUrlCommand*)args;
- (void)DeeplinkEvent:(CDVInvokedUrlCommand*)args;
- (void)EnableLogging:(CDVInvokedUrlCommand*)args;
- (void)LimitAdTracking:(CDVInvokedUrlCommand*)args;
- (void)IdentityLink:(CDVInvokedUrlCommand*)args;
- (void)RetrieveAttribution:(CDVInvokedUrlCommand*)command;
- (void)GetKochavaDeviceId:(CDVInvokedUrlCommand*)command;

@property(readonly) KochavaTracker *kochavaTracker;

@end
