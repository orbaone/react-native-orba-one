import { NativeModules } from 'react-native';

type OrbaOneType = {
  multiply(a: number, b: number): Promise<number>;
};

const { OrbaOne } = NativeModules;

export default OrbaOne as OrbaOneType;
