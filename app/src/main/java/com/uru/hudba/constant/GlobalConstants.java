package com.uru.hudba.constant;


public class GlobalConstants {

    private static final String ApiVersion = "&api_version=1";
    private static final String Client = "?client=android";
//    public static final String BaseUrl = "http://192.168.0.76/hudba/mobile/";

    public static final String BaseUrl = "http://hudba.app/hudba/mobile/";


    public static final String SignupemailUrl = BaseUrl + "main/signup_email" + Client + ApiVersion;
    public static final String SignupphoneUrl = BaseUrl + "main/signup_phone" + Client + ApiVersion;
    public static final String LoginUrl = BaseUrl + "main/login" + Client + ApiVersion;
    public static final String ForgotUrl = BaseUrl + "main/forgot_password" + Client + ApiVersion;
    public static final String FaceBookLoginUrl = BaseUrl + "main/login_facebook" + Client + ApiVersion;


    public static final String UpdateProfileUrl = BaseUrl + "user/update_profile" + Client + ApiVersion;
    public static final String UpdateProfileImageUrl = BaseUrl + "user/update_profile_image" + Client + ApiVersion;
    public static final String LogoutUrl = BaseUrl + "main/logout" + Client + ApiVersion + "&memberKey=";


    public static final String HOME_LIST = BaseUrl + "main/getTotalList" + Client + ApiVersion;
    public static final String TRACK_DETAIL_LIST = BaseUrl + "main/getTrackListDetail" + Client + ApiVersion;
    public static final String SELECT_ALL_LIST = BaseUrl + "main/getSelectAllList" + Client + ApiVersion;
    public static final String SEARCH_VIDEOID = BaseUrl + "main/searchVideoID" + Client + ApiVersion;
    public static final String SEARCH_LIST = BaseUrl + "main/getSearchList" + Client + ApiVersion;
    public static final String SEARCH_DETAIL = BaseUrl + "main/getSearchDetail" + Client + ApiVersion;
    public static final String ADD_FAVORITE = BaseUrl + "main/add_favorite" + Client + ApiVersion;
    public static final String ADD_DOWNLOAD = BaseUrl + "main/add_download" + Client + ApiVersion;

    public static final String SAVED_LIST = BaseUrl + "library/saved_tracklist" + Client + ApiVersion;
    public static final String MYPLAY_LIST = BaseUrl + "library/get_user_playlist" + Client + ApiVersion +"&memberKey=";
    public static final String CREATE_PLAYLIST = BaseUrl + "library/create_playlist" + Client + ApiVersion +"&memberKey=";
    public static final String RENAME_PLAYLIST = BaseUrl + "library/rename_playlist" + Client + ApiVersion +"&memberKey=";
    public static final String RENAME_SONG = BaseUrl + "library/rename_song" + Client + ApiVersion +"&memberKey=";
    public static final String REMOVE_PLAYLIST = BaseUrl + "library/remove_playlist" + Client + ApiVersion +"&memberKey=";
    public static final String REMOVE_PLAYLIST_TRACK = BaseUrl + "library/remove_playlist_track" + Client + ApiVersion +"&memberKey=";
    public static final String DELETE_SAVED_SONG = BaseUrl + "library/delete_saved_song" + Client + ApiVersion +"&memberKey=";
    public static final String AVAILABLE_PLAYLIST_TRACKS = BaseUrl + "library/available_playlist_tracks" + Client + ApiVersion +"&memberKey=";
    public static final String MYPLAY_PLAYLIST_DETAIL = BaseUrl + "library/get_myplaylist" + Client + ApiVersion +"&memberKey=";
    public static final String ADD_TRACKS_PLAYLIST = BaseUrl + "library/add_playlist_tracks" + Client + ApiVersion +"&memberKey=";
    public static final String PLAYLIST_TRACK_DETAIL = BaseUrl + "library/my_playlist_tracks" + Client + ApiVersion;

    public static final String SEARCH = BaseUrl + "search/index" + Client + ApiVersion;


    public static final String term_link = "https://hudba.app/termsAndConditions";
    public static final String privacy_link = "https://hudba.app/privacy";
    public static final String office_link = "https://hudba.app";
    public static final String find_face_link = "https://www.facebook.com/hudbaapp-112917347291626";
    public static final String find_instagram_link = "https://www.instagram.com/hudba.app/";
    public static final String find_twitter_link = "https://twitter.com/hudba09";
    public static final String contact_link = "https://m.me/hudbaapp-112917347291626";

    public static final int PICK_FROM_CAMERA = 101;
    public static final int PICK_FROM_FILE = 102;


    //share preference
    public static final String PRE_MEMBER_KEY = "member_key";
    public static final String PRE_USER_ID = "user_id";
    public static final String PRE_USER_EMAIL = "user_email";
    public static final String PRE_USER_PASS = "user_password";
    public static final String PRE_USER_NAME = "user_name";
    public static final String PRE_USER_FIRSTNAME = "first_name";
    public static final String PRE_USER_LASTNAME = "last_name";
    public static final String PRE_USER_IMAGE = "user_image";

    public static final String PRE_PLAYING_MODE = "playing_mode";
    public static final String PRE_VOLUME_MODE = "volume_mode";



}
