package com.refresh.www.Application;

/**
 * Created by yy on 2018/2/26.
 */
public class PublicUrl {
    public final static String USER_AGENT_APPEND=" TxyApp-Android";
    public static String OperatingSystem = "Android";

    public static boolean LogState = true;
    public static String HomeUrl = "https://signin.booker.com/auth/i/login?";
    public static String BobTan = "https://app.secure-booker.com/App/SpaAdmin/Customers/EditCustomer/Summary.aspx?CustomerID=75204056";

    //=================人脸识别=================//
    public static String apiKey = "GlujiTqgKtUzZg6PIaa7Qfke";
    public static String secretKey = "dxE3P6Ev65L3CvdFalM3YQka9xesUS2n";
    public static String licenseID = "Refresh-face-android";
    public static String licenseFileName = "idl-license.face-android";
    /*
     * <p>
     * 每个开发者账号只能创建一个人脸库；groupID用于标识人脸库
     * <p>
     * 人脸识别 接口 https://aip.baidubce.com/rest/2.0/face/v3/search
     * 人脸注册 接口 https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add
     */
    public static String groupID = "Member";

}
