package com.reactnativehyperswitchnetcetera3ds

import android.app.Application
import android.util.Log
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
    fun getButtonType(buttonType: String): UiCustomization.ButtonType {
      return UiCustomization.ButtonType.valueOf(buttonType)
    }


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
            labelJson.optString("textFontName").takeIf { it.isNotEmpty()  }?.let {
              textFontName = it
            }
            labelJson.optString("textColor").takeIf { it.isNotEmpty()  }?.let {
              textColor = it
            }
            labelJson.optInt("textFontSize", -1).takeIf { it != -1 }?.let {
              textFontSize = it
            }
            labelJson.optString("headingTextFontName").takeIf { it.isNotEmpty()  }?.let {
              headingTextFontName = it
            }
            labelJson.optString("headingTextColor").takeIf { it.isNotEmpty() }?.let {
              headingTextColor = it
            }
            labelJson.optInt("headingTextFontSize",-1).takeIf { it != -1 }?.let {
              headingTextFontSize = it
            }
          }
        }


        jsonObject.optJSONObject("textBoxCustomization")?.let { textBoxJson ->
          uiCustomization.textBoxCustomization = TextBoxCustomization().apply {
            textBoxJson.optString("textFontName").takeIf { it.isNotEmpty()  }?.let {
              textFontName = it
            }
            textBoxJson.optString("textColor").takeIf { it.isNotEmpty()  }?.let {
              textColor = it
            }
            textBoxJson.optInt("textFontSize", -1).takeIf { it != -1 }?.let {
              textFontSize = it
            }
            textBoxJson.optInt("borderWidth",-1).takeIf { it != -1  }?.let {
              borderWidth = it
            }
            textBoxJson.optString("borderColor").takeIf { it.isNotEmpty()  }?.let {
              borderColor = it
            }
            textBoxJson.optInt("cornerRadius",-1).takeIf { it != -1 }?.let {
              cornerRadius = it
            }
          }
        }

        // Toolbar Customization
        jsonObject.optJSONObject("toolbarCustomization")?.let { toolbarJson ->
          uiCustomization.toolbarCustomization = ToolbarCustomization().apply {
            toolbarJson.optString("textFontName").takeIf { it.isNotEmpty()  }?.let {
              textFontName = it
            }
            toolbarJson.optString("textColor").takeIf { it.isNotEmpty()  }?.let {
              textColor = it
            }
            toolbarJson.optInt("textColor",-1).takeIf { it != -1 }?.let {
              textFontSize = it
            }
            toolbarJson.optString("backgroundColor").takeIf { it.isNotEmpty()  }?.let {
              backgroundColor = it
            }
            toolbarJson.optString("buttonText").takeIf { it.isNotEmpty()  }?.let {
              buttonText = it
            }
            toolbarJson.optString("headerText").takeIf { it.isNotEmpty()  }?.let {
              headerText = it
            }
          }
        }


        jsonObject.optJSONArray("buttonCustomization")?.let { buttonJsonArray ->
            for (i in 0 until buttonJsonArray.length()) {
              val buttonJson = buttonJsonArray.optJSONObject(i)
              buttonJson?.let {
                val buttonCustomization = ButtonCustomization().apply {
                  buttonJson.optString("textFontName").takeIf { it.isNotEmpty() }?.let {
                    textFontName = it
                  }
                  buttonJson.optString("textColor").takeIf { it.isNotEmpty() }?.let {
                    textColor = it
                  }
                  buttonJson.optInt("textFontSize", -1).takeIf { it != -1 }?.let {
                    textFontSize = it
                  }
                  buttonJson.optString("backgroundColor").takeIf { it.isNotEmpty() }?.let {
                    backgroundColor = it
                  }
                  buttonJson.optInt("cornerRadius", -1).takeIf { it != -1 }?.let {
                    cornerRadius = it
                  }
                }
                uiCustomization.setButtonCustomization(buttonCustomization, this.getButtonType(buttonJson.optString("buttonType")))
              }
            }
        }


        jsonObject.optJSONObject("viewCustomization")?.let { viewJson ->
          uiCustomization.viewCustomization = ViewCustomization().apply {
            viewJson.optString("challengeViewBackgroundColor").takeIf {
              it.isNotEmpty()
            }?.let {
              challengeViewBackgroundColor = it
            }
            viewJson.optString("progressViewBackgroundColor").takeIf {
              it.isNotEmpty()
            }?.let {
              progressViewBackgroundColor = it
            }
          }
        }


        return uiCustomization
      }

      val uiCustomizationMap = hashMapOf<UiCustomization.UiCustomizationType, UiCustomization>()


      val lightModeJson = JSONObject(challengeCustomisationObject).optJSONObject("lightMode")
      val darkModeJson = JSONObject(challengeCustomisationObject).optJSONObject("darkMode")

      uiCustomizationMap.apply {
        if (lightModeJson != null){
          put(UiCustomization.UiCustomizationType.DEFAULT, getUiCustomizationFromJson(lightModeJson))
        }
        if (darkModeJson != null) {
          put(UiCustomization.UiCustomizationType.DARK, getUiCustomizationFromJson(darkModeJson))
        }
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
