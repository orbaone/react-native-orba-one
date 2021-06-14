# @orbaone/react-native-orba-one

Official React-Native wrapper for the Orba One SDK.

## Installation

```sh
npm install @orbaone/react-native-orba-one
```
- ## iOS
  The Orba One SDK requires that the following permissions be added to the application's `info.plist` file:

  ```
  <key>NSCameraUsageDescription</key>
  <string>Required for Facial and Document capture.</string>
  <key>NSMicrophoneUsageDescription</key>
  <string>Required for Audio capture.</string>
  ```

  - Ensure that `use_frameworks!` is added to your app target in your Podfile.
  - Run `pod install` to retrieve the sdk.

- ## Android
  No additional setup is necessary.

## Usage

```js
import { OrbaOne, OrbaOneConfig, OrbaOneFlowStep } from '@orbaone/react-native-orba-one';

// ...
const config = OrbaOneConfig.setFlowSteps([
  OrbaOneFlowStep.intro,
  OrbaOneFlowStep.identification,
  OrbaOneFlowStep.face,
  OrbaOneFlowStep.complete
]).build();

const init = await OrbaOne.init('publishable-api-key', 'applicant-id', config);
if(init.success) {
  console.log(init.message)  
} 

const res = await OrbaOne.startVerification();
if(res.success) {
  console.log(res.message)  
} 

```

## Handling Verifications
```js
import { OrbaOne } from '@orbaone/react-native-orba-one';

// ...

componentDidMount() {
  // ...

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

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
