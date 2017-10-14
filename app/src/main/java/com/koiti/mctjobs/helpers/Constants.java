package com.koiti.mctjobs.helpers;

public class Constants {

    public static final String URL_OAUTH_TOKEN = "https://esb.mct.com.co:9446/oauth2/token";

    public static final String URL_GET_WORKS_API = "http://esb.mct.com.co:8280/services/pxBplWorksPartner_noSecure";

    public static final String URL_POST_REPORT_API = "http://esb.mct.com.co:8280/services/px_NosecureBplNotificationService";

    public static final String URL_LOGIN_API = "http://esb.mct.com.co:8280/services/px_Bp_Login";

    public static final String URL_LOGIN_PHONE_API = "http://esb.mct.com.co:8280/services/px_Bp_LoginCell";

    public static final String URL_TURN_API = "http://esb.mct.com.co:8280/services/pxEnturnamientobyPosition_secure";

    public static final String URL_TURN_ON_API = "http://esb.mct.com.co:8280/services/pxcreaEnturnamientoGPS";

    public static final String URL_TERMS_ON_API = "http://esb.mct.com.co:8280/services/proxy_registrypolicydata";

    public static final String URL_POST_SEND_SMS_VERIFICATION = "https://api.authy.com/protected/json/phones/verification/start";

    public static final String URL_GET_CHECK_VERIFICATION = "https://api.authy.com/protected/json/phones/verification/check";

    public static final int TURN_ANY_DESTINATION = 99;

    public static final int REQUEST_TAKE_PHOTO = 1;

    public final static int PICK_STEP_REQUEST = 10;

    public final static int RESULT_REFRESH_ATTENDING = 11;

    public final static int RESULT_NEXT_STEP = 12;

    public final static int RESULT_REFRESH_JOBS = 14;

    public final static int RESULT_CANCEL = 15;

    public final static int PICK_REPORT_REQUEST = 16;

    public final static int RESULT_SELECT_PICTURE = 17;

    public final static int RESULT_REFRESH_FINISHED = 18;

    public final static int RESULT_FINISHED = 19;

    public static final  String GALLERY_NAME = "Mct";

    public static final String JPEG_FILE_SUFFIX = ".jpg";

    public static final int DEFAULT_MAX_RETRIES = 3;

    public static final int DEFAULT_TIMEOUT = 1000;

    public static final int INTENT_REPORT = 1;

    public static final int INTENT_PAUSE = 2;

    public static final int INTENT_IGNORE = 3;

    public static final int INTENT_UNPAUSE = 4;

    public static final int INTENT_CANCEL = 5;

    public static final int PRIVATE_MODE = 0;

    public static final String ACTION_MEMORY_EXIT = "com.koiti.mctjobs.action.MEMORY_EXIT";

    public static final String TWILIO_API_KEY = "OqhyDljLBkiVGCsb0F0vlx4CCmjE6L0n";

    public static final String COUNTRY_CODE = "57";

    public static final String MESSAGE_SUCCESS = "message_success";

    public static final String MESSAGE_TITLE = "message_title";

    public static final String MESSAGE_BODY = "message_body";
}
