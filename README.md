# react-native-orba-one

Official React-Native wrapper for Orba One SDK.

## Installation

```sh
npm install react-native-orba-one
```

## Usage

```js
import { OrbaOne, OrbaOneFlowStep } from 'react-native-orba-one';

// ...

const init = await OrbaOne.init('publishable-api-key', 'applicant-id', [
    OrbaOneFlowStep.intro,
    OrbaOneFlowStep.identification,
    OrbaOneFlowStep.face,
  ]);
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
import { OrbaOne, OrbaOneFlowStep } from 'react-native-orba-one';

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
