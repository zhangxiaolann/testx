package com.shhb.gd.shop.fragment;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.shhb.gd.shop.R;
import com.shhb.gd.shop.module.JsObject;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebViewExtension;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * Created by superMoon on 2017/3/15.
 */

public class Fragment4 extends BaseFragment {

    public static Fragment4 newInstance() {
        Fragment4 fragment = new Fragment4();
        return fragment;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.web_activity, container, false);
        fatherView = (RelativeLayout) view.findViewById(R.id.fatherView);
        webView = new WebView(context);
        RelativeLayout.LayoutParams webParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        webView.setLayoutParams(webParams);

        fatherView.addView(webView);
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("http://es1.laizhuan.com/huiTao//html/page/my.html");
            }
        });
        //精致长按事件
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);//支持JavaScript
        webView.getSettings().setAllowFileAccess(true);//允许访问文件
        webView.getSettings().setAllowFileAccessFromFileURLs(true);//通过此API可以设置是否允许通过file url加载的Javascript读取其他的本地文件
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);//通过此API可以设置是否允许通过file url加载的Javascript可以访问其他的源，包括其他的文件和http,https等其他的源
        //图片显示
        webView.getSettings().setLoadsImagesAutomatically(true);
        //自适应屏幕
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new JsObject(4,webView,context), "native_android");
        webView.setWebViewClient(new WebViewClient());
        webView.setScrollbarFadingEnabled(false);
        //关闭缩放
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setDisplayZoomControls(false);
        IX5WebViewExtension ix5 = webView.getX5WebViewExtension();
        if (null != ix5) {
            ix5.setScrollBarFadingEnabled(false);
        }
//        initBroadcastReceiver();
        return view;
    }

//    private void initBroadcastReceiver() {
//        IntentFilter intentFilter = new IntentFilter(Constants.SENDMSG_LOGIN);
//        receiver = new broadcastReceiver();
//        context.registerReceiver(receiver, intentFilter);
//    }
//
//    class broadcastReceiver extends BroadcastReceiver{
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            try {
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.put("userId",intent.getStringExtra("userId"));
//                jsObject.callBack("js_returnUserId",jsonObject.toString());
//            } catch (Exception e){
//                e.printStackTrace();
//            }
//        }
//    }

    @Override
    public void onPause() {
        super.onPause();
//        Log.e("onPause","执行");
    }

    @Override
    public void onStop() {
        super.onStop();
//        Log.e("onStop","执行");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        Log.e("onDestroyView","执行");
        webView.removeAllViews();
        webView.destroy();
//        context.unregisterReceiver(receiver);
    }
}
