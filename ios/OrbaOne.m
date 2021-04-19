#import <React/RCTBridgeModule.h>
#import <React/RCTEventEmitter.h>


@interface RCT_EXTERN_MODULE(OrbaOne, RCTEventEmitter)

RCT_EXTERN_METHOD(initialize:(NSString*)pubKey
                  appId:(NSString*)applicantId
                  flow:(NSArray)steps
                  resolve:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject
                  )

RCT_EXTERN_METHOD(startVerification:(RCTPromiseResolveBlock)resolve
                  reject:(RCTPromiseRejectBlock)reject
                  )
@end
