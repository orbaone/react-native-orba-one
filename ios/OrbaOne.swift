import OrbaOneSdk

@objc(OrbaOne)
class OrbaOne: RCTEventEmitter {
    private var sdk: OrbaOneFlow!
    
    @objc
    func initialize(_ pubKey: String, appId applicantId: String, flow steps: Array<String>, resolve:RCTPromiseResolveBlock, reject:RCTPromiseRejectBlock) {
        var params: [String: Any] = [:]
        do {
            var flowList: [Step] = []
            if (steps.count != 0) {
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
//                    case "COMPLETE":
//                        flowList.append(.COMPLETE)
//                        break
                    default:
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
            reject("E_FLOW", error.localizedDescription, error)
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
                reject("E_FAIL", error.localizedDescription, error)
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
                reject("E_FLOW", error.localizedDescription, error)
            }
        }
        
    }
    
    override func supportedEvents() -> [String]! {
        return ["onCompleteOrbaOneVerification","onCancelOrbaOneVerification"]
    }
}
