package com.shhb.gd.shop.module;

/**
 * Created by superMoon on 2017/3/15.
 */
public class Constants {

    /** SD卡权限返回的code*/
    public static final int SD_CODE = 101;
    /** 手机权限返回的code*/
    public static final int PHONE_CODE = 102;
    /** 定位权限返回的code*/
    public static final int LOCATION_CODE = 103;

    /** 登录成功后的通知*/
    public static final String SENDMSG_LOGIN = "com.login";
    /** 单击分享时的通知*/
    public static final String SENDMSG_SHARE = "com.share";
    /** 显示主页面的通知*/
    public static final String SENDMSG_SHOW = "com.show";

    /** 创建App文件夹的名子*/
    public static final String APP_FILE_URL = "HuiAmoy";
    /** 热更新的更新文件*/
    public static final String APATCH_PATH = "/out.apatch";

    /** 服务器地址*/
//    public static final String REQUEST = "http://192.168.1.233/shopping_new/";//测试服务器
    public static final String REQUEST = "http://es3.laizhuan.com/shopping_new/";//正式服务器
    /** 下载补丁的地址*/
    public static final String PATCH_URL = "http://es1.laizhuan.com/shopping/Shop/android?name=out.apatch";
    /** 查找开化页的图片*/
    public static final String FIND_BY_IMG = REQUEST + "index/opening";
    /** 查找首页的banner图片*/
    public static final String FIND_BY_BANNER = REQUEST + "index/banners";
    /** 查找选项卡和对应的内容(上拉刷新的时候)*/
    public static final String FIND_BY_TABS_REFRESH = REQUEST + "GoodsShow/cateGoods";
    /** 查找选项卡和对应的内容(下拉加载的时候)*/
    public static final String FIND_BY_TABS_LOAD = REQUEST + "GoodsShow/showGoods";
    /** 首页商品*/
    public static final String FIND_BY_GOODS1 = REQUEST + "GoodsShow/soldGoods";
    /** 商品详情*/
    public static final String FIND_BY_DETAILS = REQUEST + "GoodsShow/detail";
    /** 商品分享成功后的请求*/
    public static final String DETAILS_SHARE = REQUEST + "GoodsShow/share";
    /** 打开QQ客服*/
    public static final String OPEN_QQ = "mqqwpa://im/chat?chat_type=wpa&uin=";
    /** 付款后发送给服务器*/
    public static final String SEND_ORDERS = REQUEST + "TaoBaoKe/addOrderId";
    /** 发送推送所需的DeviceToken*/
    public static final String SEND_DEVICE_TOKEN = REQUEST + "user/addPushAssocForApp";
    /** 发送失效的购物卷*/
    public static final String SEND_VOLUME = REQUEST + "/GoodsShow/goodsSwitch";
    /** 发送淘宝登录、退出登录成功的信息*/
    public static final String SEND_LOGIN_MSG = REQUEST + "/TaoBaoAuth/authInfo";
    /** 用户登录*/
    public static final String USER_LOGIN = REQUEST + "login/login";
    /** 用户注册*/
    public static final String USER_REGISTER = REQUEST + "login/register";
    /** 获取验证码*/
    public static final String GET_VCODE = REQUEST + "vcode/codeHandle";

}
