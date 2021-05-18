import { EventEmitter, NativeEventEmitter, NativeModules } from 'react-native';

interface Result {
  success: boolean;
  error: boolean;
  message: string;
  authKey: string;
}

interface OrbaOneConfig {
  flow?: Array<OrbaOneFlowStep>;
  documents?: Array<OrbaOneDocuments>;
  countries?: Array<string>;
  theme?: Theme;
}

interface Theme {
  /// Defines the primary accent color for bullet points and highlights.
  colorPrimary?: string;
  /// Defines the text color of titles.
  colorTextPrimary?: string;
  /// Defines the background color of primary buttons and the text color of secondary buttons
  colorButtonPrimary?: string;
  /// Defines the text color of primary buttons
  colorButtonPrimaryText?: string;
  /// Defines the background color of primary buttons when pressed
  colorButtonPrimaryPressed?: string;
  /// Defines the dark mode allowed setting for the SDK
  enableDarkMode?: Boolean
}

export enum OrbaOneFlowStep {
  intro = 'INTRO',
  identification = 'ID',
  face = 'FACE',
  complete = 'COMPLETE'
}

export enum OrbaOneDocuments {
  passport = 'PASSPORT',
  driverslicense = 'DRIVERSLICENSE',
  nationalid = 'NATIONALID'
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
  * 3. OrbaOneConfig config: Any additional configuration.
  * 
  * Returns a Promise
  */
  public init(apiKey: string, applicantId: string, config: OrbaOneConfig = {documents: [], countries: [], theme: {}, flow: []}): Promise<Result> {
    return this.module.initialize(apiKey, applicantId, config.flow, config.documents, config.countries, config.theme);
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
    this.emitter.removeListener('onCompleteOrbaOneVerification', () => { });
    this.emitter.removeListener('onCancelOrbaOneVerification', () => { });
  }
}

class ConfigBuilder {
  private flowSteps: Array<OrbaOneFlowStep>;
  private excludedDocuments: Array<OrbaOneDocuments>;
  private excludedCountries: Array<string>;
  private appearance: Theme;

  constructor() {
    this.flowSteps = [];
    this.excludedDocuments = [];
    this.excludedCountries = [];
    this.appearance = {};
  }

  public setFlowSteps(steps: Array<OrbaOneFlowStep>) {
    let a = null;
    if (steps && steps.length > 0) {
      a = steps;
    } else {
      a = [
        OrbaOneFlowStep.intro,
        OrbaOneFlowStep.identification,
        OrbaOneFlowStep.face,
        OrbaOneFlowStep.complete
      ];
    }
    this.flowSteps = a;
    return this;
  }

  public setExcludeDocument(documents: Array<OrbaOneDocuments>): ConfigBuilder {
    this.excludedDocuments = documents;
    return this;
  }

  public setExcludeCountry(countries: Array<string>): ConfigBuilder {
    this.excludedCountries = countries;
    return this;
  }

  /* iOS only - for android, set appearance in colors.xml file*/
  public setAppearance(theme: Theme): ConfigBuilder {
    this.appearance = theme;
    return this;
  }

  public build(): OrbaOneConfig {
    return { documents: this.excludedDocuments, countries: this.excludedCountries, theme: this.appearance, flow: this.flowSteps }
  }
}

export const OrbaOne = new OrbaOneModule();
export const OrbaOneConfig = new ConfigBuilder();
