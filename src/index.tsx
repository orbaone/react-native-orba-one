import { EventEmitter, NativeEventEmitter, NativeModules } from 'react-native';

interface Result {
  success: boolean;
  error: boolean;
  message: string;
  authKey: string;
}
export enum OrbaOneFlowStep {
  intro = 'INTRO',
  identification = 'ID',
  face = 'FACE',
  complete = 'COMPLETE'
}

class OrbaOneModule {
  private readonly module: any;
  private readonly emitter: EventEmitter;

  constructor() {
    this.module = NativeModules.OrbaOne;
    this.emitter = new NativeEventEmitter(this.module);
  }

  /**
  * Function 'init' takes the following parameters:
  *
  * 1. String apiKey: A string representing the Publishable API Key of your Orba One Account.
  * 2. String applicantId: A string representing the Applicant Id of the mobile app user.
  * 3. Array steps: An array of Orba One Flow Steps.
  * 
  * Returns a Promise
  */
  public init(apiKey: string, applicantId: string, steps: Array<OrbaOneFlowStep>): Promise<Result> {
    let a = null;
    if (steps && steps.length > 0) {
      a = steps;
    } else {
      a = [
        OrbaOneFlowStep.intro,
        OrbaOneFlowStep.identification,
        OrbaOneFlowStep.face,
      ];
    }
    return this.module.initialize(apiKey, applicantId, a);
  }

  public startVerification(): Promise<Result> {
    return this.module.startVerification();
  }

  public onCompleteVerification(callback: (...args: any[]) => any) {
    this.emitter.addListener('onCompleteOrbaOneVerification', callback);
  }

  public onCancelVerification(callback: (...args: any[]) => any) {
    this.emitter.addListener('onCancelOrbaOneVerification', callback);
  }

  public removeListeners() {
    this.emitter.removeListener('onCompleteOrbaOneVerification', () => {});
    this.emitter.removeListener('onCancelOrbaOneVerification', () => {});
  }
}

export const OrbaOne = new OrbaOneModule();
