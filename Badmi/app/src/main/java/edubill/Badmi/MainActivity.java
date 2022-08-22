package edubill.Badmi;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.net.Uri;



public class MainActivity extends ActionBarActivity {
    private WebView webView;
    private Handler mHandler;
    private boolean mFlag = false;
 //   private WebChromeClient webChromeClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    //    setContentView(webView);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }


        //웹뷰생성
        webView = (WebView)findViewById(R.id.webview);
        WebChromeClient testChromeClient = new WebChromeClient();

        WebSettings webSettings = webView.getSettings();
        webSettings.setUseWideViewPort(true);
        webSettings.setSupportZoom(true); // 줌 서포터 표시
        webSettings.setBuiltInZoomControls(true); // 멀티터치 줌 지원
        webSettings.setJavaScriptEnabled(true);

        webView.clearCache(true);
     //   webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
     //   webSettings.setSupportMultipleWindows(false);
     //   webSettings.setLoadsImagesAutomatically(true);

      //  webView.setWebChromeClient(new WebChromeClient());

      //  webView.setWebViewClient(new CustomWebViewClient());

        webView.setWebViewClient(new webViewClient());
        webView.setWebChromeClient(new WebChromeClient());




        // 웹뷰에 웹 사이트 표시
        String url = "http://220.73.136.50/M";
        webView.loadUrl(url);

      //  setContentView(webView);

        // 핸들러 객체 설정 (onCreate() 메소드 안에 구현!!!)
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 0) {
                    mFlag = false;
                }
            }
        };




    }
    /**
     * 프로그램 종료시
     * */
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub

        super.onDestroy();

        webView.clearCache(true); // 웹뷰 캐시 삭제
        webView.destroy();
        webView = null;

        clearWebViewCache(null); // 웹뷰 캐시 삭제(전체)

        android.os.Process.killProcess(android.os.Process.myPid()); // pid kill
    }
    /**
     *  웹뷰 의 캐시 삭제
     * */

    private void clearWebViewCache(java.io.File dir){
        if(dir == null){
            dir = getCacheDir();
        }
        if(dir == null){
            return;
        }

        java.io.File[] content = dir.listFiles();

        try{
            for(int i=0; i<content.length; i++){
                if(content[i].isDirectory()){
                    clearWebViewCache(content[i]);
                }else{
                    content[i].delete();
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {

        // 백 키를 터치한 경우
        if(keyCode == KeyEvent.KEYCODE_BACK){

            // 이전 페이지를 볼 수 있다면 이전 페이지를 보여줌
            if(webView.canGoBack()){
                webView.goBack();
                return false;
            }

            // 이전 페이지를 볼 수 없다면 백키를 한번 더 터치해서 종료
            else {
                if(!mFlag) {
                    Toast.makeText(this, "'뒤로' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
                    mFlag = true;
                    mHandler.sendEmptyMessageDelayed(0, 2000); // 2초 내로 터치시
                    return false;
                } else {
                    finish();
                }
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    /**
     * WebViewClient 상속
     * */
    private class webViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith("tel:")) {
                Intent intent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse(url));
                startActivity(intent);
            }else if(url.startsWith("http:") || url.startsWith("https:")) {
                view.loadUrl(url);
            }
            return true;
        }

    }

    /**
     * WebChromeClient 상속
     * */
    private class webViewChromeClient extends WebChromeClient {

        @Override
        public boolean onCreateWindow(WebView view, boolean dialog,
                                      boolean userGesture, Message resultMsg) {
            // TODO Auto-generated method stub
            return super.onCreateWindow(view, dialog, userGesture, resultMsg);
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public boolean onJsAlert(WebView view, String url, String message,
                                 final JsResult result) {

            // TODO Auto-generated method stub
            // return super.onJsAlert(view, url, message, result);
            new AlertDialog.Builder(view.getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                    .setTitle("알림")
                    .setMessage(message)
                    .setPositiveButton(android.R.string.ok,
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    result.confirm();
                                }
                            }).setCancelable(false).create().show();
            return true;
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        @Override
        public boolean onJsConfirm(WebView view, String url, String message,
                                   final JsResult result) {
            // TODO Auto-generated method stub
            // return super.onJsConfirm(view, url, message, result);
            new AlertDialog.Builder(view.getContext(), AlertDialog.THEME_DEVICE_DEFAULT_LIGHT)
                    .setTitle("알림")
                    .setMessage(message)
                    .setPositiveButton("예",
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    result.confirm();
                                }
                            })
                    .setNegativeButton("아니오",
                            new AlertDialog.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    result.cancel();
                                }
                            }).setCancelable(false).create().show();
            return true;
        }
    }



   // class WebBrowserClient extends WebViewClient {

        /*
         * 웹뷰 내 링크 터치 시 새로운 창이 뜨지 않고
         * 해당 웹뷰 안에서 새로운 페이지가 로딩되도록 함
         */
    //    @Override
    //  public boolean shouldOverrideUrlLoading(WebView view, String url) {
   //        view.loadUrl(url);
   //        return true;
   //     }
  //  }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
