package com.shhb.gd.shop.listener;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.shhb.gd.shop.module.Constants;
import com.shhb.gd.shop.module.UMShare;
import com.shhb.gd.shop.tools.PrefShared;

/**
 * Created by superMoon on 2017/5/23.
 */

public class ShareOrShowBReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String type = intent.getAction();
            if(TextUtils.equals(type, Constants.SENDMSG_SHARE)) {
                String result = intent.getStringExtra("result");
                Log.e("友盟分享",result);
                if (null != result) {
                    String userId = PrefShared.getString(context, "userId");
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    String numId = "", shareTitle = "", shareContent = "", shareImg = "", shareUrl = "";
                    numId = jsonObject.getString("numId");
                    shareTitle = jsonObject.getString("title");
                    shareContent = jsonObject.getString("shareContent");
                    shareImg = jsonObject.getString("shareImg");
                    shareUrl = jsonObject.getString("shareUrl");
//                        if (null != userId) {
//                            shareUrl = jsonObject.getString("share_url") + "?uid=" + PrefShared.getString(context, "userId");
//                        } else {
//                            shareUrl = "http://a.app.qq.com/o/simple.jsp?pkgname=com.shhb.gd.shop";
//                        }
                    new UMShare((Activity) context,numId).share(shareTitle, shareContent, shareImg, shareUrl);
                }
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
