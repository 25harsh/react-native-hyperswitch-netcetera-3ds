package com.reactnativehyperswitchnetcetera3ds

import android.app.Application
import com.netcetera.threeds.sdk.api.configparameters.ConfigParameters
import com.netcetera.threeds.sdk.api.configparameters.builder.ConfigurationBuilder
import com.netcetera.threeds.sdk.api.configparameters.builder.SchemeConfiguration
import com.netcetera.threeds.sdk.api.transaction.challenge.ChallengeParameters
import com.netcetera.threeds.sdk.api.ui.logic.ButtonCustomization
import com.netcetera.threeds.sdk.api.ui.logic.LabelCustomization
import com.netcetera.threeds.sdk.api.ui.logic.TextBoxCustomization
import com.netcetera.threeds.sdk.api.ui.logic.ToolbarCustomization
import com.netcetera.threeds.sdk.api.ui.logic.UiCustomization
import com.netcetera.threeds.sdk.api.ui.logic.ViewCustomization
import org.json.JSONObject
import java.util.Locale

class HsNetceteraConfigurator {
  companion object {

    lateinit var configParams: ConfigParameters
    lateinit var hsSDKEnviroment: HsSDKEnviroment
    lateinit var challengeCustomisationObject: HashMap<UiCustomization.UiCustomizationType, UiCustomization>
    lateinit var locale: String


    @JvmStatic
    fun setConfigParameters(
      application: Application,
      hsSDKEnvironment: HsSDKEnviroment,
      apiKey: String,
      challengeCustomisationObject: String
    ) {
      val assetManager = application.assets
      var configParamsBuilder: ConfigurationBuilder = ConfigurationBuilder().apiKey(apiKey)
      this.hsSDKEnviroment = hsSDKEnvironment
      this.challengeCustomisationObject =
        this.getChallengeUICustomisation(challengeCustomisationObject)

      if (hsSDKEnvironment == HsSDKEnviroment.SANDBOX || hsSDKEnvironment == HsSDKEnviroment.INTEG
      ) {
        configParamsBuilder
          .configureScheme(
            SchemeConfiguration.mastercardSchemeConfiguration()
              .rootPublicKeyFromAssetCertificate(assetManager, "nca_demo_root.crt")
              .build()
          )
          .configureScheme(
            SchemeConfiguration.visaSchemeConfiguration()
              .rootPublicKeyFromAssetCertificate(assetManager, "nca_demo_root.crt")
              .build()
          )
          .configureScheme(
            SchemeConfiguration.amexConfiguration()
              .rootPublicKeyFromAssetCertificate(assetManager, "nca_demo_root.crt")
              .build()
          )
          .configureScheme(
            SchemeConfiguration.dinersSchemeConfiguration()
              .rootPublicKeyFromAssetCertificate(assetManager, "nca_demo_root.crt")
              .build()
          )
          .configureScheme(
            SchemeConfiguration.unionSchemeConfiguration()
              .rootPublicKeyFromAssetCertificate(assetManager, "nca_demo_root.crt")
              .build()
          )
          .configureScheme(
            SchemeConfiguration.jcbConfiguration()
              .rootPublicKeyFromAssetCertificate(assetManager, "nca_demo_root.crt")
              .build()
          )
          .configureScheme(
            SchemeConfiguration.eftposConfiguration()
              .rootPublicKeyFromAssetCertificate(assetManager, "nca_demo_root.crt")
              .build()
          )
      }

      configParams = configParamsBuilder.build()
    }

    @JvmStatic
    fun getChallengeParams(
      acsRefNumber: String,
      acsSignedContent: String,
      acsTransactionId: String,
      threeDSRequestorAppURL: String,
      threeDSServerTransID: String,
    ): ChallengeParameters {

      val challengeParameters =
        ChallengeParameters().apply {
          set3DSServerTransactionID(threeDSServerTransID)
          setAcsRefNumber(acsRefNumber)
          setAcsSignedContent(acsSignedContent)
          this.acsRefNumber = acsRefNumber
          this.acsTransactionID = acsTransactionId
          this.threeDSRequestorAppURL = threeDSRequestorAppURL
        }

      return challengeParameters
    }

    @JvmStatic
    fun getChallengeUICustomisation(challengeCustomisationObject: String?): HashMap<UiCustomization.UiCustomizationType, UiCustomization> {

      fun getUiCustomizationFromJson(jsonObject: JSONObject): UiCustomization {
        val uiCustomization = UiCustomization()

        this.locale = getLocale(jsonObject.optString("locale", "en"))?.language.toString()


        jsonObject.optJSONObject("labelCustomization")?.let { labelJson ->
          uiCustomization.labelCustomization = LabelCustomization().apply {
            textFontName = labelJson.optString("textFontName", "sans-serif")
            textColor = labelJson.optString("textColor", "#000000")
            textFontSize = labelJson.optInt("textFontSize", 16)
            headingTextFontName = labelJson.optString("headingTextFontName", "sans-serif")
            headingTextColor = labelJson.optString("headingTextColor", "#000000")
            headingTextFontSize = labelJson.optInt("headingTextFontSize", 24)
          }
        }


        jsonObject.optJSONObject("textBoxCustomization")?.let { textBoxJson ->
          uiCustomization.textBoxCustomization = TextBoxCustomization().apply {
            textFontName = textBoxJson.optString("textFontName", "sans-serif")
            textColor = textBoxJson.optString("textColor", "#565a5c")
            textFontSize = textBoxJson.optInt("textFontSize", 16)
            borderWidth = textBoxJson.optInt("borderWidth", 2)
            borderColor = textBoxJson.optString("borderColor", "#e4e4e4")
            cornerRadius = textBoxJson.optInt("cornerRadius", 20)
          }
        }

        // Toolbar Customization
        jsonObject.optJSONObject("toolbarCustomization")?.let { toolbarJson ->
          uiCustomization.toolbarCustomization = ToolbarCustomization().apply {
            backgroundColor = toolbarJson.optString("backgroundColor", "#ec5851")
            textColor = toolbarJson.optString("textColor", "#ffffff")
            buttonText = toolbarJson.optString("buttonText", "Cancel")
            headerText = toolbarJson.optString("headerText", "Secure Checkout")
          }
        }


        jsonObject.optJSONObject("buttonCustomization")?.let { buttonJson ->
          val submitButtonCustomization = ButtonCustomization().apply {
            backgroundColor = buttonJson.optString("submitBackgroundColor", "#ec5851")
            textColor = buttonJson.optString("submitTextColor", "#ffffff")
            textFontSize = buttonJson.optInt("submitTextFontSize", 14)
            cornerRadius = buttonJson.optInt("submitCornerRadius", 20)
          }
          uiCustomization.setButtonCustomization(
            submitButtonCustomization,
            UiCustomization.ButtonType.SUBMIT
          )

          val cancelButtonCustomization = ButtonCustomization().apply {
            textColor = buttonJson.optString("cancelTextColor", "#ffffff")
            textFontSize = buttonJson.optInt("cancelTextFontSize", 14)
          }
          uiCustomization.setButtonCustomization(
            cancelButtonCustomization,
            UiCustomization.ButtonType.CANCEL
          )
        }


        jsonObject.optJSONObject("viewCustomization")?.let { viewJson ->
          uiCustomization.viewCustomization = ViewCustomization().apply {
            challengeViewBackgroundColor =
              viewJson.optString("challengeViewBackgroundColor", "#ffffff")
            progressViewBackgroundColor =
              viewJson.optString("progressViewBackgroundColor", "#ffffff")
          }
        }


        return uiCustomization
      }

      val uiCustomizationMap = hashMapOf<UiCustomization.UiCustomizationType, UiCustomization>()


      val lightModeJson = JSONObject(challengeCustomisationObject).optJSONObject("lightMode")
      val darkModeJson = JSONObject(challengeCustomisationObject).optJSONObject("darkMode")

      uiCustomizationMap.apply {
        put(UiCustomization.UiCustomizationType.DEFAULT, getUiCustomizationFromJson(lightModeJson))
        put(UiCustomization.UiCustomizationType.DARK, getUiCustomizationFromJson(darkModeJson))
      }

      return uiCustomizationMap
    }


    private val localeMap: Map<String, Locale> = mapOf(
      "ar" to Locale("ar"),
      "he" to Locale("he"),
      "de" to Locale("de"),
      "en" to Locale("en"),
      "en-GB" to Locale("en", "GB"),
      "ja" to Locale("ja"),
      "fr" to Locale("fr"),
      "fr-BE" to Locale("fr", "BE"),
      "es" to Locale("es"),
      "ca" to Locale("ca"),
      "pt" to Locale("pt"),
      "it" to Locale("it"),
      "pl" to Locale("pl"),
      "nl" to Locale("nl"),
      "nl-BE" to Locale("nl", "BE"),
      "sv" to Locale("sv"),
      "ru" to Locale("ru"),
      "lt" to Locale("lt"),
      "cs" to Locale("cs"),
      "sk" to Locale("sk"),
      "is" to Locale("is"),
      "cy" to Locale("cy"),
      "el" to Locale("el"),
      "et" to Locale("et"),
      "fi" to Locale("fi"),
      "nb" to Locale("nb"),
      "bs" to Locale("bs"),
      "da" to Locale("da"),
      "ms" to Locale("ms"),
      "tr-CY" to Locale("tr", "CY")
    )

    fun getLocale(languageCode: String): Locale? {
      return localeMap[languageCode]
    }

  }


}
