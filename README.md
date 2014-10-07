## Click Jogos Single Sign-on Android Example

Create a webview with javascript enabled

**Example:**

```java
WebView webview = (WebView) findViewById(R.id.webView);
webview.getSettings().setJavaScriptEnabled(true);
```

Load an url in this way:

```java
webview.loadUrl("http://accounts.gruponzn.com/sso/:KEY/");
```

Create a `WebViewClient` to intercept all requests to the webview that you created, that way we will send you the `uid` as a parameter wen we make a request to your callback url

```java
webview.setWebViewClient(new WebViewClient() {
  @Override
   public boolean shouldOverrideUrlLoading(WebView view, String url) {
      Uri callbackUri = Uri.parse(url);
      String uid = callbackUri.getQueryParameter("uid");

      if (uid != null) {
        System.out.println("uid: " + uid);
        // you can close webView here
      }

      return true;
  }
});
```

After you have obtained the `uid` all that is left for you to do is make a request to:

`http://accounts.gruponzn.com/sso/user/:KEY/:SECRET/:UID`

and the response will be a JSON containing the user information.