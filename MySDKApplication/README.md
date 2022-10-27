[![FPTPlay](https://fptplay.vn/images/logo-2.png)](https://fptplay.vn/)
[![Android](https://www.gstatic.com/devrel-devsite/prod/v329b39deca73fc0f4b4862903640085cfb4d3102e48d211dd97ad63f3860a376/android/images/lockup.svg)](https://codecov.io/gh/rakutentech/android-miniapp)
# MiniApp SDK for Android
Provides a set of tools and capabilities to show mini app in Android Applications. The SDK offers features like fetching, caching and displaying of mini app. For instructions on implementing in an android application, see the [User Guide]()

## What is Mini App?
Here are some [Guides for the Mini App](https://www.w3.org/TR/mini-app-white-paper/#what-is-miniapp )

## App Management
Provides a set of tools and capabilities to manage MiniApp SDK in Android Applications
### 1.Map SDK
To be connected and to interact with SDK mini app , Client should be defined method the same as SDK.

[Note]() : Client must register SDK mini app before
``` xml 
    inner class WebViewJavascriptInterface {
        // define function of gatewaySdk
        /**
         * function init : create mini app ,callbacks data and  webview show data
         * */
        @JavascriptInterface
        fun init(data: String,phone:String) {
            Log.e("WebviewCallback", data)
            val handler = Handler(Looper.getMainLooper())
            handler.post {Toast.makeText(this@MainActivity, data, Toast.LENGTH_SHORT).show()}
        }
        /**
         * function getMethods : interacting with mini app
         * */
        @JavascriptInterface
        fun getMethods() : String{
            Log.e("getMethods", Gson().toJson(AccountMenu("1", "A", "A")))
            textView.text = Gson().toJson(AccountMenu("1", "A", "A"))
            return Gson().toJson(AccountMenu("1", "A", "A"))
        }
    }
```
### How to Test the Sample SDK
#### How to test
1.Run Android Applications

2.Register SDK of MiniApp for webView:
```
    xml webview.addJavascriptInterface(WebViewJavascriptInterface(), "gatewaySdk")
```
3. WebView load url or WebView load resources of MiniApp
```
        webview.loadUrl("file:///android_asset/index.html")
```
4.Call method of  SDK of MiniApp to be defined in class [WebViewJavascriptInterface]()
``` xml private fun setTestJavascript() {
        webview.loadUrl(
            "javascript:( " +
                    "function () {" +
                    " var result = document.documentElement.gatewaySdk; window.gatewaySdk.getMethods(); " +
                    "} ) ()"
        )
     }
```
5.Then application is running and connected to Mini App SDK , data will be passed in class [WebViewJavascriptInterface]()

6.You can debug webView , see the [Guides](https://blog.vuplex.com/debugging-webviews)
### 2.Management App
Provides a set of services to management mini app in Android Applications. The SDK offers features like checkVersionSDK,checkVersionMiniApp,download resources of mini app, check status of mini app (process,fail).
For instructions on implementing in an android application
### Public methods of MiniAppManagement
``` xml
class MiniAppManagement()
```
Parameters:  | Description |
------------- | -------------
Context |Context: an Activity Context to access application assets This value cannot be null.

#### init()
initialize class MiniAppManagement
```
 miniAppManagement.init()
```
####stop()
remove class MiniAppManagement
```
  miniAppManagement.stop()
```
####checkVersionSDKMiniApp()
provides a function to check version sdk of Mini app
```
 val isUpdate:boolean =  miniAppManagement.checkVersionSDKMiniApp()
```
####currentVersionSDKMiniApp()
provides a function to get current version sdk of Mini app
```
  val curVersion =  miniAppManagement.currentVersionSDKMiniApp()

```
####isExistMiniLocal()
provides a function to check Mini app which is downloaded in local EXTERNAL_STORAGE
``` base 
#MINI_APP_ID : UNIQUE of MINIAPP in databse

val isExist:boolean =  miniAppManagement.isExistMiniLocal(MINI_APP_ID)
```
####miniPermissionRequest()
Mini apps are able to make requests which are defined by the Mini App SDK.
``` base
#MiniAppPermissionType has been deprecated
    enum class MiniAppPermissionType{
        LOCATION,
        USER_NAME,
        CAMERA,
        CONTACT_LIST
        READ_EXTERNAL_STORAGE,
        WRITE_EXTERNAL_STORAGE
        ...
    }
 val listMiniAppPermissionType:List<MiniAppPermissionType>?=  miniAppManagement.miniPermissionRequest(VERSION_SDK_MINI_APP)
```
####setUserInfoBridge()
The mini app is able to request data about the current user from your App.
```
 miniAppManagement.setUserInfoBridge(user_id:String,phone:String,token:String)
```
####Send Native Events
Your app are able to send events to Mini App. These events include things like external webview close, pause, resume ,destroy from your app
``` base
#MiniAppNativeInterface has been deprecated
    enum class NativeEventType{
        MINIAPP_CLOSE,
        MINIAPP_ON_PAUSE,
        MINIAPP_ON_RESUME,
        MINIAPP_ON_MINIAPP_ON_DETROY,
        MINIAPP_ON_MINIAPP_ON_CREATE
        MINIAPP_ON_MINIAPP_ON_CREATED_VIEW
        ...
    }
 miniAppManagement.sendNativeEvent(NativeEventType.MINIAPP_CLOSE)

```
#### ListMiniApp
Your app use [miniAppManagement.listMiniApp]() if you want a list of all Mini Apps:
```
CoroutineScope(Dispatchers.IO).launch {
    try {
    val miniAppList = miniAppManagement.listMiniApp()
    } catch(e: MiniAppSdkException) {
    Log.e("MiniApp", "There was an error retrieving the list", e)
    }
    }
```

####Downloaded Mini App
In Your App, we can get the downloaded resources of Mini App

use [MiniAppFileDownloader]()
```base
CoroutineScope(Dispatchers.IO).launch {
try {
if(!miniAppManagement.isExistMiniLocal(MINI_APP_ID)){
val miniAppExternalUrlLoader = miniAppManagement.miniAppExternalUrlLoader(MINI_APP_ID,object : MiniAppFileDownloader{
  override fun onStartFileDownload(
      fileName: String,
      url: String,
      headers: Map<String, String>,
      onDownloadSuccess: (String) -> Unit,
      onDownloadFailed: (Error) -> Unit
  ) {
         //.. Download the file
    }
})}
} catch(e: MiniAppSdkException) {
Log.e("MiniApp", "There was an error retrieving the list", e)
}
}

```
###3.Security
