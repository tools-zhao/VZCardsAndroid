package com.bitjini.vzcards;

/**
 * Created by bitjini on 25/1/17.
 */

public class BaseURLs {
    //Live Url
//    static String liveUrl="https://vzcards-api.herokuapp.com/";
//    static String liveAccessToken="jUUMHSnuGys5nr6qr8XsNEx6rbUyNu";
//    static String baseUrl=liveUrl;
//    static String access_Token=liveAccessToken;

    // Staging url
    static String stagingUrl="https://staging-vzcards-api.herokuapp.com/";
    static String stagingAccessToken="gWgLsmgEafve3TEUewVf26rh9tuq69";
    static String baseUrl=stagingUrl;
    static String access_Token=stagingAccessToken;


    public static String URL_REGISTER = baseUrl+"user_register/?access_token="+access_Token;;
    public static String URL_VERIFY = baseUrl+"verify/?access_token="+access_Token;;
    public static String URL_RESEND=baseUrl+"send_again/?access_token="+access_Token;;

    public static  String URL_PROFILE_UPDATE = baseUrl+"my_profile/update/?access_token=";
    public static  String URL_GET_PROFILE = baseUrl+"my_profile/?access_token=";

    public static  String URL_UPLOAD_IMAGE = baseUrl+"upload_image/?access_token="+access_Token;;
    public static String URL_Cloudynary_Image_Path="http://res.cloudinary.com/harnesymz/image/upload/vzcards/";

    public static String URL_CREATE_TICKET = baseUrl+"ticket_create/?access_token=";
    public static String URL_CONNECT = baseUrl+"connect/?access_token=";


    //  Get Feeds
    public static String URL_GETLIST=baseUrl+"get_list/?access_token=";

    // To sync contacts
    public static String SYNC_CONTACT_URL=baseUrl+"sync/?access_token="+access_Token;;

     // get history
    public static String HISTORY_URL = baseUrl+"history/?access_token=";


    public static String URL_INVITE_FRNDS = baseUrl+"invite_friends/?access_token=";
    public static String VZFRIENDS_URL = baseUrl+"get_my_friends/?access_token=";



}
