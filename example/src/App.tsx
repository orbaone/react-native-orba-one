import React, { Component } from 'react';
import { StyleSheet, View, Text, TouchableOpacity } from 'react-native';
import { OrbaOne, OrbaOneFlowStep } from 'react-native-orba-one';

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
      const init = await OrbaOne.init('ace3ae4256f94374ad0f41b9418bf092', 'GUEST', [
        OrbaOneFlowStep.intro,
        OrbaOneFlowStep.identification,
        OrbaOneFlowStep.face,
      ]);
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
