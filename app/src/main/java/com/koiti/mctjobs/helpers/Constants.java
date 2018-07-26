package com.koiti.mctjobs.helpers;

public class Constants {

    public static final String URL_OAUTH_TOKEN = "https://esb.mct.com.co:9446/oauth2/token";

    public static final String URL_GET_WORKS_EXIST = "http://esb.mct.com.co:8280/services/pxBplWorksIncoursePartner";

    public static final String URL_GET_WORK_API = "http://esb.mct.com.co:8280/services/pxDetailsWork";

    public static final String URL_GET_WORKS_API = "http://esb.mct.com.co:8280/services/pxBplWorksPartner";

    public static final String URL_POST_REPORT_API = "http://esb.mct.com.co:8280/services/pxBplNotificationService";

    public static final String URL_POST_DOCUMENT_API = "http://esb.mct.com.co:8280/services/pxImagesForReport";

    public static final String URL_LOGIN_API = "http://esb.mct.com.co:8280/services/px_Bp_LoginEncry";

    public static final String URL_LOGIN_VALID_PHONE_API = "http://esb.mct.com.co:8280/services/px_ValidaExterno2Login";

    public static final String URL_LOGIN_PHONE_API = "http://esb.mct.com.co:8280/services/px_Bp_LoginCell";

    public static final String URL_TURN_API = "http://esb.mct.com.co:8280/services/pxEnturnamientobyPosition_secure";

    public static final String URL_TURN_ON_API = "http://esb.mct.com.co:8280/services/pxcreaEnturnamientoGPS";

    public static final String URL_TERMS_ON_API = "http://esb.mct.com.co:8280/services/proxy_secureregistrypolicydata";

    public static final String URL_TOKEN_PUSH_API = "http://esb.mct.com.co:8280/services/px_securecrearToken";

    public static final String URL_GET_QR_MANIFEST = "http://esb.mct.com.co:8280/services/pxGetImageqrPartner";

    public static final String URL_POST_SEND_SMS_VERIFICATION = "https://api.authy.com/protected/json/phones/verification/start";

    public static final String URL_GET_CHECK_VERIFICATION = "https://api.authy.com/protected/json/phones/verification/check";

    public static final String OAUTH_USERNAME = "appworks";

    public static final String OAUTH_PASSWORD = "appworks";

    public static final String OAUTH_GRANT_TYPE = "password";

    public static final String OAUTH_AUTHORIZATION = "Basic WkZXRncyc1B2OGZON09SeVBzalc0NGVROUIwYTpMdUdSVXNFazNkb29zQVg3d1o3UVEzWTVQeGth";

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

    public final static int RESULT_DISCARD = 20;

    public final static int RESULT_REFRESH_DISCARD_ATTENDING = 21;

    public static final  String GALLERY_NAME = "Mct";

    public static final String JPEG_FILE_SUFFIX = ".jpg";

    public static final int DEFAULT_MAX_RETRIES = 3;

    public static final int DEFAULT_TIMEOUT = 1000;

    public static final int INTENT_CLOSE_WORK = 508;

    public static final int INTENT_DEFAULT = 0;

    public static final int INTENT_REPORT = 1;

    public static final int INTENT_PAUSE = 2;

    public static final int INTENT_IGNORE = 3;

    public static final int INTENT_UNPAUSE = 4;

    public static final int INTENT_CANCEL = 5;

    public static final int INTENT_DISCARD = 6;

    public static final int PRIVATE_MODE = 0;

    public static final int STEP_FORWARD_DOCUMENTS = 0;

    public static final String ACTION_MEMORY_EXIT = "com.koiti.mctjobs.action.MEMORY_EXIT";

    public static final String TWILIO_API_KEY = "OqhyDljLBkiVGCsb0F0vlx4CCmjE6L0n";

    public static final String COUNTRY_CODE = "57";

    public static final String MESSAGE_SUCCESS = "message_success";

    public static final String MESSAGE_TITLE = "message_title";

    public static final String MESSAGE_BODY = "message_body";
}
