package com.reactnativeorbaone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;

import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.orbaone.orba_one_capture_sdk_core.OrbaOne;
import com.orbaone.orba_one_capture_sdk_core.helpers.Step;

import java.util.ArrayList;

@ReactModule(name = OrbaOneModule.NAME)
public class OrbaOneModule extends ReactContextBaseJavaModule {
  public static final String NAME = "OrbaOne";
  private OrbaOne oneSdk;

  public OrbaOneModule(ReactApplicationContext reactContext) {
    super(reactContext);
  }

  @Override
  @NonNull
  public String getName() {
    return NAME;
  }

  private void sendEvent(ReactContext reactContext,
                         String eventName,
                         @Nullable WritableMap params) {
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(eventName, params);
  }

  @ReactMethod
  public void initialize(String pubKey, String applicantId, ReadableArray steps, Promise promise ) {
    WritableMap params = Arguments.createMap();
    try {
      ArrayList<Step> flowList = new ArrayList<>();
      ArrayList stepList = steps.toArrayList();
      if (!stepList.isEmpty()) {
        for (Object step : stepList) {
          switch (step.toString()) {
            case "INTRO":
              flowList.add(Step.INTRO);
              break;
            case "ID":
              flowList.add(Step.ID);
              break;
            case "FACE":
              flowList.add(Step.FACESCAN);
              break;
            case "COMPLETE":
              flowList.add(Step.COMPLETE);
              break;
          }
        }
      }
      if (!flowList.isEmpty()) {
        Step[] flowStep = (Step[]) flowList.toArray();
        oneSdk = new OrbaOne.Builder().setApiKey(pubKey).setApplicantId(applicantId).setFlow(flowStep).create();
      } else {
        oneSdk = new OrbaOne.Builder().setApiKey(pubKey).setApplicantId(applicantId).create();
      }
      params.putBoolean("success", true);
      params.putString("message", "The Orba One verification api is ready.");
      promise.resolve(params);
    } catch (Exception e){
      params.putBoolean("error", true);
      params.putString("message", e.getLocalizedMessage());
      promise.reject(e, params);
    }
  }

  @ReactMethod
  public void startVerification(Promise promise) {
    WritableMap params = Arguments.createMap();
    try {
      oneSdk.startVerification((AppCompatActivity) getCurrentActivity());
      oneSdk.onStartVerification(new OrbaOne.Response() {
        @Override
        public void onSuccess() {
          params.putBoolean("success", true);
          params.putString("message", "Orba One Verification started.");
          promise.resolve(params);
        }

        @Override
        public void onFailure(String message) {
          params.putBoolean("error", true);
          params.putString("message", message);
          promise.reject(new IllegalStateException(message), params);
        }
      });
      oneSdk.onCompleteVerification(new OrbaOne.Callback() {
        @Override
        public void execute(String key) {
          WritableMap params = Arguments.createMap();
          params.putBoolean("success", true);
          params.putString("authKey", key);
          params.putString("message", "The Orba One verification flow was completed.");
          sendEvent(getReactApplicationContext(), "onCompleteOrbaOneVerification", params);
        }
      });
      oneSdk.onCancelVerification(new OrbaOne.Callback() {
        @Override
        public void execute() {
          WritableMap params = Arguments.createMap();
          params.putBoolean("error", true);
          params.putString("message", "The Orba One verification flow was cancelled.");
          sendEvent(getReactApplicationContext(), "onCancelOrbaOneVerification", params);
        }
      });
    } catch (Exception e) {
      params.putBoolean("error", true);
      params.putString("message", e.getLocalizedMessage());
      promise.reject(e, params);
    }
  }
}
