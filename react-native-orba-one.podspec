require "json"

package = JSON.parse(File.read(File.join(__dir__, "package.json")))

Pod::Spec.new do |s|
  s.name         = "react-native-orba-one"
  s.version      = package["version"]
  s.summary      = package["description"]
  s.homepage     = package["homepage"]
  s.license      = package["license"]
  s.authors      = package["author"]

  s.platforms    = { :ios => "13.0" }
  s.source       = { :git => "https://github.com/orbaone/react-native-orba-one.git" }

  s.source_files = "ios/**/*.{h,m,swift}"
  s.requires_arc = true
  
  s.dependency "React-Core"
  s.dependency "OrbaOneSdk", "0.0.13"
end
