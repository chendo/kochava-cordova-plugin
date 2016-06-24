/*
 * File Name  : KochavaEvents.m
 * Author     : Kochava
 * Description : This is the plugin implimentation class file.
 */

#import "KochavaEvents.h"

@implementation KochavaEvents

@synthesize kochavaTracker;

- (void)initKochava:(CDVInvokedUrlCommand*)args		{
	
	id initData = [args.arguments objectAtIndex:0];
	if ( ![initData isKindOfClass:[NSDictionary class]] )	{
		NSLog(@"Initialization data is not a NSDictionary - iOS native cannot initialize.");
	}

	
	NSMutableDictionary *initDict = [[NSMutableDictionary alloc] init];
	
	if ( [initData objectForKey:@"kochava_ios_app_id"] != nil )		{
		[initDict addEntriesFromDictionary:[NSDictionary dictionaryWithObject:[initData objectForKey:@"kochava_ios_app_id"] forKey:@"kochavaAppId"]];
	}

	if ( [initData objectForKey:@"currency"] != nil )		{
		[initDict addEntriesFromDictionary:[NSDictionary dictionaryWithObject:[initData objectForKey:@"currency"] forKey:@"currency"]];
	}
	
	if ( [initData objectForKey:@"app_limit_tracking"] != nil )		{
		[initDict addEntriesFromDictionary:[NSDictionary dictionaryWithObject:[initData objectForKey:@"app_limit_tracking"] forKey:@"limitAdTracking"]];
	}
	
	if ( [initData objectForKey:@"debug"] != nil )		{
		[initDict addEntriesFromDictionary:[NSDictionary dictionaryWithObject:[initData objectForKey:@"debug"] forKey:@"enableLogging"]];
	}
	
	if ( [initData objectForKey:@"request_attribution"] != nil )		{
		[initDict addEntriesFromDictionary:[NSDictionary dictionaryWithObject:[initData objectForKey:@"request_attribution"] forKey:@"retrieveAttribution"]];
	}
	
	if ( [initData objectForKey:@"identity_link"] != nil )		{
		[initDict addEntriesFromDictionary:[NSDictionary dictionaryWithObject:[initData objectForKey:@"identity_link"] forKey:@"identityLink"]];
	}
	
	[initDict addEntriesFromDictionary:[NSDictionary dictionaryWithObject:@"-cordova" forKey:@"versionExtension"]];

	// this cannot be run in background or will crash trying to collect user agent
	kochavaTracker = [[KochavaTracker alloc] initKochavaWithParams:initDict];
	kochavaTracker.trackerDelegate = self;
	
	
}

- (void)TrackEvent:(CDVInvokedUrlCommand*)args		{

	[self.commandDelegate runInBackground:^{

		NSString *eventName = [args.arguments objectAtIndex:0];
		NSString *eventValue = [args.arguments objectAtIndex:1];

		
		[kochavaTracker trackEvent:eventName :eventValue];
		
		CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"EventMethod call sucess"];

		[self.commandDelegate sendPluginResult:pluginResult callbackId:args.callbackId];
	}];
}

- (void)TrackEventWithReceipt:(CDVInvokedUrlCommand*)args		{
	
	[self.commandDelegate runInBackground:^{
		
		NSString *eventName = [args.arguments objectAtIndex:0];
		NSString *eventValue = [args.arguments objectAtIndex:1];
		NSString *eventReceipt = [args.arguments objectAtIndex:2];
		
		
		[kochavaTracker trackEvent:eventName withValue:eventValue andReceipt:eventReceipt];
		
		CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"EventMethod call sucess"];
		
		[self.commandDelegate sendPluginResult:pluginResult callbackId:args.callbackId];
	}];
}

- (void)SpatialEvent:(CDVInvokedUrlCommand*)args	{

	[self.commandDelegate runInBackground:^{
		
		NSString *spatialCommand = (NSString *)[args.arguments objectAtIndex:0];
		float spatialX = [[args.arguments objectAtIndex:1] floatValue];
		float spatialY = [[args.arguments objectAtIndex:2] floatValue];
		float spatialZ= [[args.arguments objectAtIndex:3] floatValue];
		
		[kochavaTracker spatialEvent:spatialCommand:spatialX:spatialY:spatialZ];
		
		CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"SpatialEvents method call sucess"];

		[self.commandDelegate sendPluginResult:pluginResult callbackId:args.callbackId];
	}];
}

- (void)DeeplinkEvent:(CDVInvokedUrlCommand*)args	{
	
	[self.commandDelegate runInBackground:^{
		
		NSURL *deeplinkURL = [NSURL URLWithString:[(NSString *)[args.arguments objectAtIndex:0]
												   stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
		NSString *sourceApplication = (NSString *)[args.arguments objectAtIndex:1];
		[kochavaTracker sendDeepLink:deeplinkURL:sourceApplication];
		CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"Deeplink sucess"];

		[self.commandDelegate sendPluginResult:pluginResult callbackId:args.callbackId];
	}];
}

- (void)EnableLogging:(CDVInvokedUrlCommand*)args	{

	[self.commandDelegate runInBackground:^{
		
		bool loggingState = [[args.arguments objectAtIndex:0] boolValue];
		
		[kochavaTracker enableConsoleLogging:loggingState];
		
		CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"Logging toggled"];

		[self.commandDelegate sendPluginResult:pluginResult callbackId:args.callbackId];
	}];
}

- (void)LimitAdTracking:(CDVInvokedUrlCommand*)args	{
	
	[self.commandDelegate runInBackground:^{
		
		bool limitAdTrackingState = [[args.arguments objectAtIndex:0] boolValue];
		[kochavaTracker setLimitAdTracking:limitAdTrackingState];
		
		CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"Limit ad tracking toggled"];
		
		[self.commandDelegate sendPluginResult:pluginResult callbackId:args.callbackId];
	}];
}

- (void)IdentityLink:(CDVInvokedUrlCommand *)args	{
	
	[self.commandDelegate runInBackground:^{
		
		if ( [[args.arguments objectAtIndex:0] isKindOfClass:[NSDictionary class]] )			{
			NSDictionary *idLinkData = [args.arguments objectAtIndex:0];
			
			[kochavaTracker identityLinkEvent:idLinkData];
			CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"IdentityLinking sucess"];
			
			[self.commandDelegate sendPluginResult:pluginResult callbackId:args.callbackId];
			
		}
		else	{
			CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_ERROR messageAsString:@"IDLink not processed"];
			[self.commandDelegate sendPluginResult:pluginResult callbackId:args.callbackId];
		}
	}];
}

- (void)RetrieveAttribution:(CDVInvokedUrlCommand*)command	{
	
	NSDictionary *attributionResult = [kochavaTracker retrieveAttribution];

	// attributionResult will either be
	// nil -> return: ""
	// a key/value pair of attribution/false -> return: "false"
	// a dictionary of attribution key/value pairs -> return json of dictionary
	
	[self.commandDelegate runInBackground:^{
		
		if ( attributionResult == nil )		{
			CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@""];
			[self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
		}
		
		else if ( [attributionResult objectForKey:@"attribution"] != nil &&
			[[attributionResult objectForKey:@"attribution"] isKindOfClass:[NSString class]] &&
			[[attributionResult objectForKey:@"attribution"] isEqualToString:@"false"] )		{

			CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"false"];
			[self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];

		}
		else	{
			NSError *error;
			NSData *jsonData = [NSJSONSerialization dataWithJSONObject:attributionResult
															   options:0
																 error:&error];
			
			if (jsonData) {
				NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
				CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:jsonString];
				[self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
			} else {
				CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:@"false"];
				[self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
			}
		}
	}];
}

- (void)GetKochavaDeviceId:(CDVInvokedUrlCommand*)command
{
	[self.commandDelegate runInBackground:^{
		
		NSString *kochavaDeviceId = [kochavaTracker getKochavaDeviceId];
		CDVPluginResult *pluginResult = [CDVPluginResult resultWithStatus:CDVCommandStatus_OK messageAsString:kochavaDeviceId];
		[self.commandDelegate sendPluginResult:pluginResult callbackId:command.callbackId];
	}];
}

- (void) Kochava_attributionResult:(NSDictionary *)attributionResult
{
	// attributionResult will either be
	// a key/value pair of attribution/false -> return: "false"
	// a dictionary of attribution key/value pairs -> return json of dictionary

	if ( [attributionResult objectForKey:@"attribution"] != nil &&
		[[attributionResult objectForKey:@"attribution"] isKindOfClass:[NSString class]] &&
		[[attributionResult objectForKey:@"attribution"] isEqualToString:@"false"] )		{

		NSString *jsStatement = [NSString stringWithFormat:@"window.attributionNotification.notificationCallback('%@');",@"false"];
		if ([self.webView isKindOfClass:[UIWebView class]]) {
			UIWebView *localWebView = (UIWebView*)self.webView;
			dispatch_async(dispatch_get_main_queue(), ^{
				[localWebView stringByEvaluatingJavaScriptFromString:jsStatement];
			});
		}
	}
	else	{
		NSError *error;
		NSData *jsonData = [NSJSONSerialization dataWithJSONObject:attributionResult
														   options:0
															 error:&error];
		
		if (jsonData) {
			NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
			NSString *jsStatement = [NSString stringWithFormat:@"window.attributionNotification.notificationCallback('%@');",jsonString];
			if ([self.webView isKindOfClass:[UIWebView class]]) {
				UIWebView *localWebView = (UIWebView*)self.webView;
				dispatch_async(dispatch_get_main_queue(), ^{
					[localWebView stringByEvaluatingJavaScriptFromString:jsStatement];
				});
			}
		}
		else	{
			NSString *jsStatement = [NSString stringWithFormat:@"window.attributionNotification.notificationCallback('%@');",@"false"];
			if ([self.webView isKindOfClass:[UIWebView class]]) {
				UIWebView *localWebView = (UIWebView*)self.webView;
				dispatch_async(dispatch_get_main_queue(), ^{
					[localWebView stringByEvaluatingJavaScriptFromString:jsStatement];
				});
			}
		}
	}
}

@end
