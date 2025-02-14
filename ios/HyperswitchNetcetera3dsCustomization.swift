//
//  HyperswitchNetcetera3dsCustomization.swift
//  Hyperswitch
//
//  Created by Kuntimaddi Manideep on 13/02/25.
//

import Foundation
import ThreeDS_SDK

func parseUiCustomization(from json: [String: Any]) -> UiCustomization {
  let uiCustomization = UiCustomization()

  func safelySet<T>(_ value: T?, _ setter: (T) throws -> Void) {
    if let value = value {
      do {
        try setter(value)
      } catch {
      }
    }
  }

  func buttonTypeMapper (_ buttonType: String?) -> UiCustomization.ButtonType {
    if let string = buttonType {
      switch string {
      case "SUBMIT":
        return .SUBMIT
      case "CONTINUE":
        return .CONTINUE
      case "NEXT":
        return .NEXT
      case "RESEND":
        return .RESEND
      case "OPEN_OOB_APP":
        return .OPEN_OOB_APP
      case "ADD_CH":
        return .ADD_CH
      case "CANCEL":
        return .CANCEL
      default:
        return .SUBMIT
      }
    }
    return .SUBMIT
  }

  if let labelJson = json["labelCustomization"] as? [String: Any] {
    let labelCustomization = LabelCustomization()
    safelySet(labelJson["textFontName"] as? String) { try labelCustomization.setTextFontName(fontName: $0) }
    safelySet(labelJson["textColor"] as? String) { try labelCustomization.setTextColor(hexColorCode: $0) }
    safelySet(labelJson["textFontSize"] as? Int) { try labelCustomization.setTextFontSize(fontSize: $0) }
    safelySet(labelJson["headingTextFontName"] as? String) { try labelCustomization.setHeadingTextFontName(fontName: $0) }
    safelySet(labelJson["headingTextColor"] as? String) { try labelCustomization.setHeadingTextColor(hexColorCode: $0) }
    safelySet(labelJson["headingTextFontSize"] as? Int) { try labelCustomization.setHeadingTextFontSize(fontSize: $0) }
    uiCustomization.setLabelCustomization(labelCustomization: labelCustomization)
  }

  if let toolbarJson = json["toolbarCustomization"] as? [String: Any] {
    let toolbarCustomization = ToolbarCustomization()
    safelySet(toolbarJson["textFontName"] as? String) { try toolbarCustomization.setTextFontName(fontName: $0) }
    safelySet(toolbarJson["textColor"] as? String) { try toolbarCustomization.setTextColor(hexColorCode: $0) }
    safelySet(toolbarJson["textFontSize"] as? Int) { try toolbarCustomization.setTextFontSize(fontSize: $0) }
    safelySet(toolbarJson["backgroundColor"] as? String) { try toolbarCustomization.setBackgroundColor(hexColorCode: $0) }
    safelySet(toolbarJson["headerText"] as? String) { try toolbarCustomization.setHeaderText(headerText: $0) }
    safelySet(toolbarJson["buttonText"] as? String) { try toolbarCustomization.setButtonText(buttonText: $0) }
    uiCustomization.setToolbarCustomization(toolbarCustomization: toolbarCustomization)
  }

  if let textBoxJson = json["textBoxCustomization"] as? [String: Any] {
    let textboxCustomization = TextBoxCustomization()
    safelySet(textBoxJson["textFontName"] as? String) { try textboxCustomization.setTextFontName(fontName: $0) }
    safelySet(textBoxJson["textColor"] as? String) { try textboxCustomization.setTextColor(hexColorCode: $0) }
    safelySet(textBoxJson["textFontSize"] as? Int) { try textboxCustomization.setTextFontSize(fontSize: $0) }
    safelySet(textBoxJson["borderWidth"] as? Int) { try textboxCustomization.setBorderWidth(borderWidth: $0) }
    safelySet(textBoxJson["borderColor"] as? String) { try textboxCustomization.setBorderColor(hexColorCode: $0) }
    safelySet(textBoxJson["cornerRadius"] as? Int) { try textboxCustomization.setCornerRadius(cornerRadius: $0) }
    uiCustomization.setTextBoxCustomization(textBoxCustomization: textboxCustomization)
  }
  //for new version

//  if let viewBoxJson = json["viewCustomization"] as? [String: Any] {
//    let viewCustomization = ViewCustomization()
//    safelySet(viewBoxJson["challengeViewBackgroundColor"] as? String) { try viewCustomization.setChallengeViewBackgroundColor(hexColorCode: $0) }
//    safelySet(viewBoxJson["progressViewBackgroundColor"] as? String) { try viewCustomization.setProgressViewBackgroundColor(hexColorCode: $0) }
//    uiCustomization.setViewCustomization(viewCustomization: viewCustomization)
//  }

  if let buttonJson = json["buttonCustomization"] as? [[String :Any]] {

    buttonJson.forEach() {
      let buttonCustomization = ButtonCustomization()
      safelySet($0["textFontName"] as? String) { try! buttonCustomization.setTextFontName(fontName: $0) }
      safelySet($0["backgroundColor"] as? String) { try! buttonCustomization.setBackgroundColor(hexColorCode: $0) }
      safelySet($0["cornerRadius"] as? Int) { try! buttonCustomization.setCornerRadius(cornerRadius: $0) }
      safelySet($0["textFontSize"] as? Int) { try! buttonCustomization.setTextFontSize(fontSize: $0) }
      safelySet($0["textColor"] as? String) { try! buttonCustomization.setTextColor(hexColorCode: $0) }
      safelySet($0["buttonType"] as? String) {
        uiCustomization.setButtonCustomization(buttonCustomization: buttonCustomization, buttonType: buttonTypeMapper($0))  }
    }
  }

  return uiCustomization
}

func getChallengeUICustomisation(challengeCustomisationObject: String) -> [String: UiCustomization] {
  var uiCustomizationMap: [String: UiCustomization] = [:]
  guard let data = challengeCustomisationObject.data(using: .utf8),
        let jsonObject = try? JSONSerialization.jsonObject(with: data, options: []),
        let jsonDict = jsonObject as? [String: Any] else {
    return uiCustomizationMap
  }

  if let lightModeJson = jsonDict["lightMode"] as? [String: Any] {
    uiCustomizationMap["DEFAULT"] = parseUiCustomization(from: lightModeJson)
  }
  if let darkModeJson = jsonDict["darkMode"] as? [String: Any] {
    uiCustomizationMap["DARK"] = parseUiCustomization(from: darkModeJson)
  }

  return uiCustomizationMap
}

func getChallengeLocale(challengeCustomisationObject: String) -> String {
  guard let data = challengeCustomisationObject.data(using: .utf8),
        let jsonObject = try? JSONSerialization.jsonObject(with: data, options: []),
        let jsonDict = jsonObject as? [String: Any] else {
    return "en"
  }
  return jsonDict["locale"] as? String ?? "en"
}
