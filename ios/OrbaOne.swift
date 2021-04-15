import OrbaOneSdk

@objc(OrbaOne)
class OrbaOne: NSObject, RCTEventEmitter {
    private var sdk: OrbaOneFlow!
    
    @objc
    func initilialize(_ pubKey: String, appId applicantId: String, flow steps: Array<String>, resolve:RCTPromiseResolveBlock, reject:RCTPromiseRejectBlock) {
        var params: [String: Any] = [:]
        do {
            var flowList: [Step] = []
            if steps.count {
                for step in steps {
                    switch step {
                    case "INTRO":
                        flowList.append(.INTRO)
                        break
                    case "ID":
                        flowList.append(.ID)
                        break
                    case "FACE":
                        flowList.append(.FACESCAN)
                        break
                    case "COMPLETE":
                        flowList.append(.COMPLETE)
                        break
                    }
                }
            }
            let config = OrbaOneConfig().setApiKey(pubKey).setApplicantId(applicantId).setFlow(flowList).build()
            sdk = try OrbaOneFlow(configuration: config)
            params["success"] = true
            params["message"] = "The Orba One verification api is ready."
            resolve(params)
        } catch let error {
            params["error"] = true
            params["message"] = error.localizedDescription
            reject("E_FLOW", params, error)
        }
    }
    
    @objc
    func startVerification(_ resolve:RCTPromiseResolveBlock,reject:RCTPromiseRejectBlock) {
        var params: [String: Any] = [:]
        do {
            sdk.with(responseHandler: {response in
                switch response {
                case .success(let result):
                    params["success"] = true
                    params["authKey"] = key
                    params["message"] = "The Orba One verification flow was completed."
                    sendEvent(withName: "onCompleteOrbaOneVerification", body: params)
                    break
                case .failure(let error):
                    params["error"] = true
                    params["message"] = "The Orba One verification flow was cancelled."
                    sendEvent(withName: "onCancelOrbaOneVerification", body: params)
                    break
                case .start:
                    params["success"] = true
                    params["message"] = "Orba One Verification started."
                    resolve(params)
                    break
                case .error(let error):
                    params["error"] = true
                    params["message"] = error
                    reject("E_FAIL", params, error)
                    break
                }
            })
            
            var presentationStyle: UIModalPresentationStyle = .fullScreen
            
            if UIDevice.current.userInterfaceIdiom == .pad {
                presentationStyle = .formSheet
            }
            
            try sdk.startVerification(origin: self, style: presentationStyle)
            
        } catch let error {
            params["error"] = true
            params["message"] = error.localizedDescription
            reject("E_FLOW", params, error)
        }
    }
    
    override func supportedEvents() -> [String]! {
      return ["onCompleteOrbaOneVerification","onCancelOrbaOneVerification"]
    }
}
