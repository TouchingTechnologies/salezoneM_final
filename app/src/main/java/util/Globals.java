package util;

public class Globals {
	//IPHONE
	public static final String server = "http://172.20.10.7:8080";
	public static final String serverImages = "http://172.20.10.7"; 

	
	//TECNO
//	public static final String server = "http://192.168.43.225:8080";
//	public static final String serverImages = "http://192.168.43.225"; 
	
	//OUTBOX
//	public static final String server = "http://192.168.1.86:8080";
//	public static final  String serverImages = "http://192.168.1.86";
	
	//SMS STUFF
    public static Boolean CONNECTION = null;
	public static final String TABLE_SMSES = "sentSms";
	public static final String KEY_ID = "_id";
	public static final String KEY_SENDER = "sender";
	public static final String KEY_NAME = "name";
	public static final String KEY_RECEP = "recep";
	public static final String KEY_OTHERNUM = "otherNum";
	public static final String thread_id = "thread_id";
	public static final String KEY_MESSAGE = "message";
	public static final String COLUMN_DATETIME = "dateTime";
	
	public static final int MESSAGE_TYPE_INBOX = 1;
	public static final int MESSAGE_TYPE_SENT = 2;

	public static final int MESSAGE_IS_NOT_READ = 0;
	public static final int MESSAGE_IS_READ = 1;

	public static final int MESSAGE_IS_NOT_SEEN = 0;
	public static final int MESSAGE_IS_SEEN = 1;
	public static Long MESSAGE_ID = null;
	
	// BROADCAST FINALS
	public static final String MESS_REC = "suga.messageReceived";
   public static final String RELOAD = "reload";
	public static final String MESS_REC_HELP = "mobisquid.messageReceived";
	public static final String DONE_UPLOAD = "done.upload";
	public static final String MESS_DEL_SERV = "suga.messageReceivedServer";
	public static final String MESS_DEL_RECEP = "suga.messageReceivedRecep";
	public static final String MESS_DEL_RECEP_HELP = "receiver_got_message";
	public static String SingleTon = null;
	public static String SingleTonhelp = null;
	public static String addfav = "1";
	public static String runthis = "1";
	public static String recepnow = null;

	// MESSAGE IDENTIFIER
	public static final String MESS_IDENT = "messId";

	// TYPE OF MESSAGE KEYS
	public static final String MESS_TYPE_MESS_ACK = "messageAck";
	
	// FLAG FOR CONNECTED
	public final static String CONNECTED = "connected";
	// FLAG FOR CONNECTED
	public final static String DISCONNECTED = "disconnected";
	public static String NO_CONNECTION = "";
	public static final String CLEAR_NOTIFY = "clear_notifications";
	public static final String MESS_DEL_SERV_HELP ="help.messageReceivedServer";
	
}
