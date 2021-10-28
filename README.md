# @orbaone/react-native-orba-one

Official [React-Native](https://github.com/facebook/react-native) wrapper for the Orba One SDK.

## Installation

```sh
npm install @orbaone/react-native-orba-one
# OR
yarn add @orbaone/react-native-orba-one
```

## Linking
Linking is automatic, however, you still need to perform a few steps for iOS.

### iOS
The Orba One SDK requires that the following permissions be added to the application's `info.plist` file:

```
<key>NSCameraUsageDescription</key>
<string>Required for Facial and Document capture.</string>
<key>NSMicrophoneUsageDescription</key>
<string>Required for Audio capture.</string>
```

- Ensure that `use_frameworks!` is added to your app target in your Podfile.
- Run `pod install` to retrieve the sdk.

### Android
No additional setup is necessary.

## Usage

```js
import { OrbaOne, OrbaOneConfig, OrbaOneFlowStep, OrbaOneDocuments } from '@orbaone/react-native-orba-one';
```

## Starting the Verification Flow 
```js
// Initializing the Flow with default settings
const init = await OrbaOne.init('publishable-api-key', 'applicant-id');
if(init.success) {
  console.log(init.message)  
} 

// Starting the Flow
const res = await OrbaOne.startVerification();
if(res.success) {
  console.log(res.message)  
} 
```
## Adding Customizations
```js
// Customizing the Flow
const verificationConfig = OrbaOneConfig.setFlowSteps([
  OrbaOneFlowStep.intro, // Welcome step - gives your user a short overview of the flow. [Optional, Default].
  OrbaOneFlowStep.identification, // Photo ID step - captures the user's identification document. [Default].
  OrbaOneFlowStep.face, // Selfie Video step - captures a video of the user for liveness detection. [Default].
  OrbaOneFlowStep.complete // Final Step - informs the user that the verification process is completed. [Optional].
])
// Customizing the Theme
.setAppearance({
  colorPrimary: '#000000' <Hex String>,
  colorButtonPrimary: '#000000' <Hex String>,
  colorTextPrimary: '#000000' <Hex String>,
  colorButtonPrimaryPressed: '#000000' <Hex String>,
  enableDarkMode: true <Bool>
})
// Customizing the Document Capture Step
.setExcludeDocument([
  OrbaOneDocuments.passport, // this will remove the Passport option
  OrbaOneDocuments.driverslicense, // this will remove the Driver's License option
  OrbaOneDocuments.nationalid // this will remove the National ID option
])
// Customizing the Country List
.setExcludeCountry([
  'JM', // this will remove Jamaica from the list of available countries
  'US' // this will remove the United States from the list of available countries
])
.build();

const init = await OrbaOne.init('publishable-api-key', 'applicant-id', verificationConfig);
```
## Handling Verifications

```js
componentDidMount() {
  OrbaOne.onCompleteVerification((event: any) => {
    console.log(event.authKey)
  });

  OrbaOne.onCancelVerification((event: any) => {
    console.log(event.message)
  });
}

componentWillUnmount = () => {
  OrbaOne.removeListeners();
};
```

## Troubleshooting
When installing or using `@orbaone/react-native-orba-one` you may encounter the following problems:

[iOS] - If you are using `@react-native-firebase` in your project, along with `use_frameworks!`, you may encounter an error with `RNFirebase`. To avoid this, add `$RNFirebaseAsStaticFramework = true` at the top of your `Podfile`. 

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
