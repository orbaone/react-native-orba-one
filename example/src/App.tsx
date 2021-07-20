import React, { Component } from 'react';
import { StyleSheet, View, Text, TouchableOpacity } from 'react-native';
import { OrbaOne, OrbaOneConfig, OrbaOneDocuments, OrbaOneFlowStep } from '@orbaone/react-native-orba-one';

interface AppState {
  result: string;
}

export default class App extends Component<{}, AppState> {
  constructor(props: any) {
    super(props);
    this.state = {
      result: '',
    };
  }

  componentDidMount = async () => {
    try {
      const config = OrbaOneConfig.setFlowSteps([
        OrbaOneFlowStep.intro,
        OrbaOneFlowStep.identification,
        OrbaOneFlowStep.face,
        OrbaOneFlowStep.complete
      ]).setAppearance({ colorPrimary: '#34A0E3', colorButtonPrimary: '#34A0E3', colorTextPrimary: '#34A0E3', colorButtonPrimaryPressed: '#2D75FA', enableDarkMode: true }).setExcludeDocument([OrbaOneDocuments.passport]).setExcludeCountry(['BR']).build();
      const init = await OrbaOne.init('PUBLISHABLE-KEY', 'GUEST', config);
      if (init.success) {
        this.setState({ result: init.message });
      }
    } catch (error) {
      this.setState({ result: 'Error: ' + error });
    }

    OrbaOne.onCompleteVerification((event: any) => {
      this.setState({ result: event.authKey });
    });

    OrbaOne.onCancelVerification((event: any) => {
      this.setState({ result: event.message });
    });
  };

  componentWillUnmount = () => {
    OrbaOne.removeListeners();
  };

  startFlow = async () => {
    try {
      const res = await OrbaOne.startVerification();
      if (res.success) {
        this.setState({ result: res.message });
      }
    } catch (error) {
      this.setState({ result: 'Error: ' + error });
    }
  };

  render() {
    return (
      <View style={styles.container}>
        <Text>Result: {this.state.result}</Text>
        <TouchableOpacity
          style={styles.button}
          onPress={() => {
            this.startFlow();
          }}
        >
          <Text style={styles.buttonText}>Start Verification</Text>
        </TouchableOpacity>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
  },
  button: {
    marginTop: 20,
    padding: 10,
    backgroundColor: '#000000',
  },
  buttonText: {
    fontSize: 14,
    textAlign: 'center',
    color: '#FFFFFF',
  },
});
