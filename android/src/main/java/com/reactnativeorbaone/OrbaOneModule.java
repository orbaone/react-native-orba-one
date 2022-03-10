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
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.module.annotations.ReactModule;

import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.orbaone.orba_one_capture_sdk_core.OrbaOne;
import com.orbaone.orba_one_capture_sdk_core.documentCapture.CountryCode;
import com.orbaone.orba_one_capture_sdk_core.documentCapture.DocumentCaptureStep;
import com.orbaone.orba_one_capture_sdk_core.helpers.DocumentTypes;
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
  public void initialize(String pubKey, String applicantId, ReadableArray steps, ReadableArray excludeDocuments, ReadableArray excludeCountries, ReadableMap theme, Promise promise ) {
    try {
      OrbaOne.Builder config = new OrbaOne.Builder().setApiKey(pubKey).setApplicantId(applicantId);
      Step[] flowStep = getFlowSteps(steps);
      if (flowStep.length > 0) {
        config.setFlow(flowStep);
      }
      DocumentCaptureStep captureConfig = getCaptureStep(excludeDocuments, excludeCountries);
      if (captureConfig != null) {
        config.setDocumentCapture(captureConfig);
      }
      oneSdk = config.create();
      WritableMap params = Arguments.createMap();
      params.putBoolean("success", true);
      params.putString("message", "The Orba One verification api is ready.");
      promise.resolve(params);
    } catch (Exception e){
      WritableMap params = Arguments.createMap();
      params.putBoolean("error", true);
      params.putString("message", e.getLocalizedMessage());
      promise.reject(e, params);
    }
  }

  @ReactMethod
  public void startVerification(Promise promise) {
    try {
      oneSdk.startVerification((AppCompatActivity) getCurrentActivity());
      oneSdk.onStartVerification(new OrbaOne.Response() {
        @Override
        public void onSuccess() {
          WritableMap params = Arguments.createMap();
          params.putBoolean("success", true);
          params.putString("message", "Orba One Verification started.");
          promise.resolve(params);
        }

        @Override
        public void onFailure(String message) {
          WritableMap params = Arguments.createMap();
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
      WritableMap params = Arguments.createMap();
      params.putBoolean("error", true);
      params.putString("message", e.getLocalizedMessage());
      promise.reject(e, params);
    }
  }

  private Step[] getFlowSteps(ReadableArray steps) {
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
      Step[] flowStep = new Step[flowList.size()];
      flowStep = flowList.toArray(flowStep);
      return flowStep;
    } else {
      return null;
    }
  }

  private DocumentCaptureStep getCaptureStep(ReadableArray documents, ReadableArray countries) {
    DocumentTypes[] excludedDocuments = getDocuments(documents);
    CountryCode[] excludedCountries = getCountries(countries);
    if (excludedDocuments != null || excludedCountries != null) {
      DocumentCaptureStep.Builder config = new DocumentCaptureStep.Builder();
      if (excludedDocuments != null) {
        config.excludeDocument(excludedDocuments);
      }
      if (excludedCountries != null) {
        config.excludeCountry(excludedCountries);
      }
      return config.create();
    }
    return null;
  }

  private DocumentTypes[] getDocuments(ReadableArray documents) {
    ArrayList<DocumentTypes> documentList = new ArrayList<>();
    ArrayList list = documents.toArrayList();
    if (!list.isEmpty()) {
      for (Object document : list) {
        try {
          DocumentTypes id = DocumentTypes.valueOf(document.toString());
          documentList.add(id);
        } catch (IllegalArgumentException e) {
          // not a valid country code
        }
      }
    }
    if (!documentList.isEmpty()) {
      DocumentTypes[] excludedDocuments = new DocumentTypes[documentList.size()];
      excludedDocuments = documentList.toArray(excludedDocuments);
      return excludedDocuments;
    } else {
      return null;
    }
  }

  private CountryCode[] getCountries(ReadableArray countries) {
    ArrayList<CountryCode> countryList = new ArrayList<>();
    ArrayList list = countries.toArrayList();
    if (!list.isEmpty()) {
      for (Object country : list) {
        try {
          CountryCode code = CountryCode.valueOf(country.toString());
          countryList.add(code);
        } catch (IllegalArgumentException e) {
          // not a valid country code
        }
      }
    }
    if (!countryList.isEmpty()) {
      CountryCode[] excludedCountries = new CountryCode[countryList.size()];
      excludedCountries = countryList.toArray(excludedCountries);
      return excludedCountries;
    } else {
      return null;
    }
  }

  @ReactMethod
  public void addListener(String eventName) {
    // Set up any upstream listeners or background tasks as necessary
  }

  @ReactMethod
  public void removeListeners(Integer count) {
    // Remove upstream listeners, stop unnecessary background tasks
  }
}
