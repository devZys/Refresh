package com.refresh.www.Application;

/**
 * Created by yy on 2018/2/26.
 */
public class PublicUrl {
    /*
        账号信息
        Account:  refreshgroup
        Username: Liuyang
        Password: Ly@4631214
     */
    public final static String USER_AGENT_APPEND=" TxyApp-Android";
    public static String OperatingSystem = "Android";

    public static boolean LogState = true;
    public static String HomeUrl = "https://signin.booker.com/auth/i/login?";
    public static String SearchCustomersUrl = "https://app.secure-booker.com/App/SpaAdmin/Customers/SearchCustomers.aspx";
    public static String ChooseShopUrl = "https://app.secure-booker.com/App/BrandAdmin/Spas/SearchSpas.aspx";
    public static String FindCustomerIDUrl = "https://app.secure-booker.com/App/SpaAdmin/Customers/EditCustomer/Appointments.aspx?CustomerID=";
    public static String CalendarUrl = "https://app.secure-booker.com/App/SpaAdmin/Appointments/Calendar.aspx";
    public static String ReLoginUrl = "https://signin.booker.com/auth/i/login?id=";
    public static String LoginUrl = "https://app.secure-booker.com/App/Admin/Login.aspx";
    /*
     * 百度人脸识别
     * 人脸识别 接口 https://aip.baidubce.com/rest/2.0/face/v3/search
     * 人脸注册 接口 https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add
     */
    public static String apiKey = "GlujiTqgKtUzZg6PIaa7Qfke";
    public static String secretKey = "dxE3P6Ev65L3CvdFalM3YQka9xesUS2n";
    public static String licenseID = "Refresh-face-android";
    public static String licenseFileName = "idl-license.face-android";
    public static String groupID = "Member";

    /*
     *  Face++人脸分析
     */
    public static String FacePlus_apiKey    = "sDz6x4T8x8bWVCg9HaK5FjkDV1h0fwrx";
    public static String FacePlus_apiSecret = "aA3CI9AT6XtZzX3SJ3jpkL-G26nBrJGR";

}
