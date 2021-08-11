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
import { OrbaOne, OrbaOneConfig, OrbaOneFlowStep } from '@orbaone/react-native-orba-one';
```

## Starting the Verification Flow with Customizations
```
// Customizing the flow
const verificationConfig = OrbaOneConfig.setFlowSteps([
  OrbaOneFlowStep.intro,
  OrbaOneFlowStep.identification,
  OrbaOneFlowStep.face,
  OrbaOneFlowStep.complete
])
.setAppearance({
  colorPrimary: '#000000' <Hex String>,
  colorButtonPrimary: '#000000' <Hex String>,
  colorTextPrimary: '#000000' <Hex String>,
  colorButtonPrimaryPressed: '#000000' <Hex String>,
  enableDarkMode: true <Bool>
})
.build();

// Initializing the flow
const init = await OrbaOne.init('publishable-api-key', 'applicant-id', verificationConfig);
if(init.success) {
  console.log(init.message)  
} 

// Starting the flow
const res = await OrbaOne.startVerification();
if(res.success) {
  console.log(res.message)  
} 
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
