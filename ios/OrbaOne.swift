import OrbaOneSdk

@objc(OrbaOne)
class OrbaOne: RCTEventEmitter {
    private var sdk: OrbaOneFlow!
    
    @objc
    func initialize(_ pubKey: String,
                    appId applicantId: String,
                    flow steps: Array<String>,
                    excludeDocuments documents: Array<String>,
                    excludeCountries countries: Array<String>,
                    appearance theme: [String: Any],
                    resolve:RCTPromiseResolveBlock,
                    reject:RCTPromiseRejectBlock) {
        var params: [String: Any] = [:]
        do {
            
            var configBuilder = OrbaOneConfig().setApiKey(pubKey).setApplicantId(applicantId)
            if let flowList = getFlowSteps(flow: steps) {
                configBuilder = configBuilder.setFlow(flowList)
            }
            if let captureStep = getCaptureStep(excludedDocuments: documents, excludedCountries: countries) {
                configBuilder = configBuilder.supportsDocument(captureStep)
            }
            if let ui = getTheme(appearance: theme) {
                configBuilder = configBuilder.setAppearance(ui)
            }
            let config = configBuilder.build()
            sdk = try OrbaOneFlow(configuration: config)
            params["success"] = true
            params["message"] = "The Orba One verification api is ready."
            resolve(params)
        } catch let error {
            reject("E_FLOW", "\(error)", error)
        }
    }
    
    @objc
    func startVerification(_ resolve: @escaping RCTPromiseResolveBlock,reject: @escaping RCTPromiseRejectBlock) {
        var params: [String: Any] = [:]
        
        sdk.with(responseHandler: {response in
            switch response {
            case .success(let result):
                params["success"] = true
                params["authKey"] = result
                params["message"] = "The Orba One verification flow was completed."
                self.sendEvent(withName: "onCompleteOrbaOneVerification", body: params)
                break
            case .failure( _):
                params["error"] = true
                params["message"] = "The Orba One verification flow was cancelled."
                self.sendEvent(withName: "onCancelOrbaOneVerification", body: params)
                break
            case .start:
                params["success"] = true
                params["message"] = "Orba One Verification started."
                resolve(params)
                break
            case .error(let error):
                reject("E_FAIL", "\(error)", error)
                break
            }
        })
        
        var presentationStyle: UIModalPresentationStyle = .fullScreen
        
        if UIDevice.current.userInterfaceIdiom == .pad {
            presentationStyle = .formSheet
        }
        
        DispatchQueue.main.async {
            do {
                guard let presentedView = RCTPresentedViewController() else {
                    return
                }
                try self.sdk.startVerification(origin: presentedView, style: presentationStyle)
            } catch let error {
                reject("E_FLOW", "\(error)", error)
            }
        }
        
    }
    
    func getFlowSteps(flow steps: Array<String>) -> [Step]? {
        var list: [Step] = []
        if (steps.count != 0) {
            for step in steps {
                switch step {
                case "INTRO":
                    list.append(.INTRO)
                    break
                case "ID":
                    list.append(.ID)
                    break
                case "FACE":
                    list.append(.FACESCAN)
                    break
                case "COMPLETE":
                    list.append(.COMPLETE)
                    break
                default:
                    break
                }
            }
        }
        if list.isEmpty {
            return nil
        }
        return list
    }
    
    func getCaptureStep(excludedDocuments docs: Array<String>, excludedCountries codes: Array<String>) -> DocumentCaptureStep? {
        let exDocuments = getDocuments(excludedDocuments: docs)
        let exCountries = getCountries(excludedCountries: codes)
        if !(exDocuments?.isEmpty ?? true) || !(exCountries?.isEmpty ?? true) {
            var captureConfig = DocumentCaptureConfig()
            if let documents = exDocuments {
                captureConfig = captureConfig.excludeDocument(documents)
            }
            if let countries = exCountries {
                captureConfig = captureConfig.excludeCountry(countries)
            }
            return captureConfig.build()
        }
        return nil
    }
    
    func getDocuments(excludedDocuments documents: Array<String>) -> [DocumentTypes]? {
        var list: [DocumentTypes] = []
        if (documents.count != 0) {
            for document in documents {
                if let id = DocumentTypes(rawValue: document) {
                    list.append(id)
                }
            }
        }
        if list.isEmpty {
            return nil
        }
        return list
    }
    
    func getCountries(excludedCountries countries: Array<String>) -> [CountryCode]? {
        var list: [CountryCode] = []
        if (countries.count != 0) {
            for country in countries {
                if let code = CountryCode(rawValue: country) {
                    list.append(code)
                }
            }
        }
        if list.isEmpty {
            return nil
        }
        return list
    }
    
    func getTheme(appearance theme: [String: Any]) -> Theme? {
        if !theme.isEmpty {
            if let darkMode = theme["enableDarkMode"] {
               return Theme(enableDarkMode: darkMode as! Bool)
            }
        }
        return nil
    }
    
    override func supportedEvents() -> [String]! {
        return ["onCompleteOrbaOneVerification","onCancelOrbaOneVerification"]
    }
}
