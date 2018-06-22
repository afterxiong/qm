/*      */ package com.shareshow.airpc.ftpclient;
/*      */ 
/*      */

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.shareshow.airpc.QMCommander;
import com.shareshow.airpc.ftpserver.Constans;
import com.shareshow.airpc.ftpserver.FTPServerService;
import com.shareshow.airpc.model.QMLocalFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.InetAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.net.ssl.SSLSocketFactory;

/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */
/*      */

/*      */
/*      */ public class FTPClient
/*      */ {
/*      */   public static final int SECURITY_FTP = 0;
/*      */   public static final int SECURITY_FTPS = 1;
/*      */   public static final int SECURITY_FTPES = 2;
/*      */   public static final int TYPE_AUTO = 0;
/*      */   public static final int TYPE_TEXTUAL = 1;
/*      */   public static final int TYPE_BINARY = 2;
/*      */   public static final int MLSD_IF_SUPPORTED = 0;
/*      */   public static final int MLSD_ALWAYS = 1;
/*      */   public static final int MLSD_NEVER = 2;
/*      */   private static final int SEND_AND_RECEIVE_BUFFER_SIZE = 65536;
/*  156 */   private static final DateFormat MDTM_DATE_FORMAT = new SimpleDateFormat("yyyyMMddHHmmss");
/*      */ 
/*  162 */   private static final Pattern PASV_PATTERN = Pattern.compile("\\d{1,3},\\d{1,3},\\d{1,3},\\d{1,3},\\d{1,3},\\d{1,3}");
/*      */ 
/*  168 */   private static final Pattern PWD_PATTERN = Pattern.compile("\"/.*\"");

	        // private static final long TIME_OUT =2000;
	/*      */
/*  173 */   private FTPConnector connector = new DirectConnector();
/*      */ 
/*  178 */   private SSLSocketFactory sslSocketFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
/*      */ 
/*  184 */   private ArrayList communicationListeners = new ArrayList();
/*      */ 
/*  189 */   private ArrayList listParsers = new ArrayList();
/*      */ 
/*  194 */   private FTPTextualExtensionRecognizer textualExtensionRecognizer = DefaultTextualExtensionRecognizer.getInstance();
/*      */ 
/*  200 */   private FTPListParser parser = null;
/*      */ 
/*  205 */   private String host = null;
/*      */ 
/*  210 */   private int port = 0;
/*      */ 
/*  217 */   private int security = 0;
/*      */   private String username;
/*      */   private String password;
/*  232 */   private boolean connected = false;
/*      */ 
/*  237 */   private boolean authenticated = false;
/*      */ 
/*  243 */   private boolean passive = true;
/*      */ 
/*  251 */   private int type = 0;
/*      */ 
/*  259 */   private int mlsdPolicy = 0;
/*      */ 
/*  268 */   private long autoNoopTimeout = 0L;
/*      */   private AutoNoopTimer autoNoopTimer;
/*      */   private long nextAutoNoopTime;
/*  285 */   private boolean restSupported = false;
/*      */ 
/*  293 */   private String charset = null;
/*      */ 
/*  300 */   private boolean compressionEnabled = false;
/*      */ 
/*  306 */   private boolean utf8Supported = false;
/*      */ 
/*  312 */   private boolean mlsdSupported = false;
/*      */ 
/*  318 */   private boolean modezSupported = false;
/*      */ 
/*  323 */   private boolean modezEnabled = false;
/*      */ 
/*  328 */   private boolean dataChannelEncrypted = false;
/*      */ 
/*  335 */   private boolean ongoingDataTransfer = false;
/*      */ 
/*  340 */   private InputStream dataTransferInputStream = null;
/*      */ 
/*  345 */   private OutputStream dataTransferOutputStream = null;
/*      */ 
/*  351 */   private boolean aborted = false;
/*      */ 
/*  356 */   private boolean consumeAborCommandReply = false;
/*      */ 
/*  361 */   private Object lock = new Object();
/*      */ 
/*  366 */   private Object abortLock = new Object();
/*      */ 
/*  371 */   private FTPCommunicationChannel communication = null;
/*      */ 
/*      */   public FTPClient()
/*      */   {
/*  378 */     addListParser(new UnixListParser());
/*  379 */     addListParser(new DOSListParser());
/*  380 */     addListParser(new EPLFListParser());
/*  381 */     addListParser(new NetWareListParser());
/*  382 */     addListParser(new MLSDListParser());
/*      */   }
/*      */ 
/*      */   public FTPConnector getConnector()
/*      */   {
/*  391 */     synchronized (this.lock){
/*  392 */       return this.connector;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setConnector(FTPConnector connector)
/*      */   {
/*  407 */     synchronized (this.lock){
/*  408 */       this.connector = connector;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setSSLSocketFactory(SSLSocketFactory sslSocketFactory)
/*      */   {
/*  421 */     synchronized (this.lock){
/*  422 */       this.sslSocketFactory = sslSocketFactory;
/*      */     }
/*      */   }
/*      */ 
/*      */   public SSLSocketFactory getSSLSocketFactory()
/*      */   {
/*  434 */     synchronized (this.lock) {
/*  435 */       return this.sslSocketFactory;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setSecurity(int security)
/*      */     throws IllegalStateException, IllegalArgumentException
/*      */   {
/*  464 */     if ((security != 0) && (security != 1) && (security != 2)) {
/*  465 */       throw new IllegalArgumentException("Invalid security");
/*      */     }
/*  467 */     synchronized (this.lock) {
/*  468 */       if (this.connected) {
/*  469 */         throw new IllegalStateException("The security level of the connection can't be changed while the client is connected");
/*      */       }
/*      */ 
/*  473 */       this.security = security;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getSecurity()
/*      */   {
/*  486 */     return this.security;
/*      */   }
/*      */ 
/*      */   private Socket ssl(Socket socket, String host, int port)
/*      */     throws IOException
/*      */   {
/*  503 */     return this.sslSocketFactory.createSocket(socket, host, port, true);
/*      */   }
/*      */ 
/*      */   public void setPassive(boolean passive)
/*      */   {
/*  513 */     synchronized (this.lock) {
/*  514 */       this.passive = passive;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setType(int type)
/*      */     throws IllegalArgumentException
/*      */   {
/*  546 */     if ((type != 0) && (type != 2) && (type != 1)) {
/*  547 */       throw new IllegalArgumentException("Invalid type");
/*      */     }
/*  549 */     synchronized (this.lock) {
/*  550 */       this.type = type;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getType()
/*      */   {
/*  563 */     synchronized (this.lock) {
/*  564 */       return this.type;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setMLSDPolicy(int mlsdPolicy)
/*      */     throws IllegalArgumentException
/*      */   {
/*  596 */     if ((this.type != 0) && (this.type != 1) && (this.type != 2)) {
/*  597 */       throw new IllegalArgumentException("Invalid MLSD policy");
/*      */     }
/*  599 */     synchronized (this.lock) {
/*  600 */       this.mlsdPolicy = mlsdPolicy;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getMLSDPolicy()
/*      */   {
/*  615 */     synchronized (this.lock) {
/*  616 */       return this.mlsdPolicy;
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getCharset()
/*      */   {
/*  630 */     synchronized (this.lock) {
/*  631 */       return this.charset;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setCharset(String charset)
/*      */   {
/*  647 */     synchronized (this.lock) {
/*  648 */       this.charset = charset;
/*  649 */       if (this.connected)
/*      */         try {
/*  651 */           this.communication.changeCharset(pickCharset());
/*      */         } catch (IOException e) {
/*  653 */           e.printStackTrace();
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isResumeSupported()
/*      */   {
/*  667 */     synchronized (this.lock) {
/*  668 */       return this.restSupported;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isCompressionSupported()
/*      */   {
/*  688 */     return this.modezSupported;
/*      */   }
/*      */ 
/*      */   public void setCompressionEnabled(boolean compressionEnabled)
/*      */   {
/*  707 */     this.compressionEnabled = compressionEnabled;
/*      */   }
/*      */ 
/*      */   public boolean isCompressionEnabled()
/*      */   {
/*  723 */     return this.compressionEnabled;
/*      */   }
/*      */ 
/*      */   public FTPTextualExtensionRecognizer getTextualExtensionRecognizer()
/*      */   {
/*  735 */     synchronized (this.lock) {
/*  736 */       return this.textualExtensionRecognizer;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setTextualExtensionRecognizer(FTPTextualExtensionRecognizer textualExtensionRecognizer)
/*      */   {
/*  756 */     synchronized (this.lock) {
/*  757 */       this.textualExtensionRecognizer = textualExtensionRecognizer;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isAuthenticated()
/*      */   {
/*  767 */     synchronized (this.lock) {
/*  768 */       return this.authenticated;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isConnected()
/*      */   {
/*  779 */     synchronized (this.lock) {
/*  780 */       return this.connected;
/*      */     }
/*      */   }
/*      */ 
/*      */   public boolean isPassive()
/*      */   {
/*  790 */     synchronized (this.lock) {
/*  791 */       return this.passive;
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getHost()
/*      */   {
/*  801 */     synchronized (this.lock) {
/*  802 */       return this.host;
/*      */     }
/*      */   }
/*      */ 
/*      */   public int getPort()
/*      */   {
/*  812 */     synchronized (this.lock) {
/*  813 */       return this.port;
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getPassword()
/*      */   {
/*  823 */     synchronized (this.lock) {
/*  824 */       return this.password;
/*      */     }
/*      */   }
/*      */ 
/*      */   public String getUsername()
/*      */   {
/*  834 */     synchronized (this.lock) {
/*  835 */       return this.username;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void setAutoNoopTimeout(long autoNoopTimeout)
/*      */   {
/*  857 */     synchronized (this.lock) {
/*  858 */       if ((this.connected) && (this.authenticated)){
/*  859 */         stopAutoNoopTimer();
/*      */       }
/*  861 */       long oldValue = this.autoNoopTimeout;
/*  862 */       long newValue = autoNoopTimeout;
/*  863 */       this.autoNoopTimeout = autoNoopTimeout;
/*  864 */       if ((oldValue != 0L) && (newValue != 0L) && (this.nextAutoNoopTime > 0L)) {
/*  865 */         this.nextAutoNoopTime -= oldValue - newValue;
/*      */       }
/*  867 */       if ((this.connected) && (this.authenticated))
/*  868 */         startAutoNoopTimer();
/*      */     }
/*      */   }
/*      */ 
/*      */   public long getAutoNoopTimeout()
/*      */   {
/*  883 */     synchronized (this.lock) {
/*  884 */       return this.autoNoopTimeout;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addCommunicationListener(FTPCommunicationListener listener)
/*      */   {
/*  895 */     synchronized (this.lock) {
/*  896 */       this.communicationListeners.add(listener);
/*  897 */       if (this.communication != null)
/*  898 */         this.communication.addCommunicationListener(listener);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeCommunicationListener(FTPCommunicationListener listener)
/*      */   {
/*  911 */     synchronized (this.lock) {
/*  912 */       this.communicationListeners.remove(listener);
/*  913 */       if (this.communication != null)
/*  914 */         this.communication.removeCommunicationListener(listener);
/*      */     }
/*      */   }
/*      */ 
/*      */   public FTPCommunicationListener[] getCommunicationListeners()
/*      */   {
/*  926 */     synchronized (this.lock) {
/*  927 */       int size = this.communicationListeners.size();
/*  928 */       FTPCommunicationListener[] ret = new FTPCommunicationListener[size];
/*  929 */       for (int i = 0; i < size; i++) {
/*  930 */         ret[i] = ((FTPCommunicationListener)this.communicationListeners.get(i));
/*      */       }
/*  932 */       return ret;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void addListParser(FTPListParser listParser)
/*      */   {
/*  943 */     synchronized (this.lock) {
/*  944 */       this.listParsers.add(listParser);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void removeListParser(FTPListParser listParser)
/*      */   {
/*  956 */     synchronized (this.lock) {
/*  957 */       this.listParsers.remove(listParser);
/*      */     }
/*      */   }
/*      */ 
/*      */   public FTPListParser[] getListParsers()
/*      */   {
/*  968 */     synchronized (this.lock) {
/*  969 */       int size = this.listParsers.size();
/*  970 */       FTPListParser[] ret = new FTPListParser[size];
/*  971 */       for (int i = 0; i < size; i++) {
/*  972 */         ret[i] = ((FTPListParser)this.listParsers.get(i));
/*      */       }
/*  974 */       return ret;
/*      */     }
/*      */   }
/*      */ 
/*      */   public String[] connect(String host)
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException
/*      */   {
/*      */     int def;
/*      */   
/*  998 */     if (this.security == 1)
/*  999 */       def = 990;
/*      */     else {
/* 1001 */       def = 21;
/*      */     }
/* 1003 */     return connect(host, def);
/*      */   }
/*      */ 
/*      */   public String[] connect(String host, int port)
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException
/*      */   {
/* 1026 */     synchronized (this.lock)
/*      */     {
/* 1028 */       if (this.connected) {
/* 1029 */         throw new IllegalStateException("Client already connected to " + host + " on port " + port);
/*      */       }
/*      */ 
/* 1033 */       Socket connection = null;
/*      */       try
/*      */       {
/* 1036 */         connection = this.connector.connectForCommunicationChannel(host, port);
/* 1037 */         if (this.security == 1){
/* 1038 */           connection = ssl(connection, host, port);
/*      */         }
/*      */ 
/* 1041 */         this.communication = new FTPCommunicationChannel(connection, pickCharset());
/* 1042 */         for (Iterator i = this.communicationListeners.iterator(); i.hasNext(); ) {
/* 1043 */           this.communication.addCommunicationListener((FTPCommunicationListener)i.next());
/*      */         }
/*      */ 
/* 1046 */         FTPReply wm = this.communication.readFTPReply();
/*      */ 
/* 1048 */         if (!wm.isSuccessCode())
/*      */         {
/* 1050 */           throw new FTPException(wm);
/*      */         }
/*      */ 
/* 1053 */         this.connected = true;
/* 1054 */         this.authenticated = false;
/* 1055 */         this.parser = null;
/* 1056 */         this.host = host;
/* 1057 */         this.port = port;
/* 1058 */         this.username = null;
/* 1059 */         this.password = null;
/* 1060 */         this.utf8Supported = false;
/* 1061 */         this.restSupported = false;
/* 1062 */         this.mlsdSupported = false;
/* 1063 */         this.modezSupported = false;
/* 1064 */         this.dataChannelEncrypted = false;
/*      */ 
/* 1066 */         String[] arrayOfString = wm.getMessages();
/*      */ 
/* 1072 */         if ((!this.connected) && 
/* 1073 */           (connection != null))
/*      */           try
/*      */           {
/* 1076 */             connection.close();
/*      */           } catch (Throwable t) {
/*      */           }
/* 1079 */         return arrayOfString;
/*      */       }
/*      */       catch (IOException e)
/*      */       {
/* 1069 */         throw e;
/*      */       }
/*      */       finally {
/* 1072 */         if ((!this.connected) && 
/* 1073 */           (connection != null))
/*      */           try
/*      */           {
/* 1076 */             connection.close();
/*      */           }
/*      */           catch (Throwable t)
/*      */           {
/*      */           }
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void abortCurrentConnectionAttempt()
/*      */   {
/* 1094 */     this.connector.abortConnectForCommunicationChannel();
/*      */   }
/*      */ 
/*      */   public void disconnect(boolean sendQuitCommand)
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException
/*      */   {
/* 1120 */     synchronized (this.lock)
/*      */     {
/* 1122 */       if (!this.connected) {
/* 1123 */         throw new IllegalStateException("Client not connected");
/*      */       }
/*      */ 
/* 1126 */       if (this.authenticated) {
/* 1127 */         stopAutoNoopTimer();
/*      */       }
/*      */ 
/* 1130 */       if (sendQuitCommand)
/*      */       {
/* 1132 */         this.communication.sendFTPCommand("QUIT");
/* 1133 */         FTPReply r = this.communication.readFTPReply();
/* 1134 */         if (!r.isSuccessCode()){
/* 1135 */           throw new FTPException(r);
/*      */         }
/*      */       }
/*      */ 
/* 1139 */       this.communication.close();
/* 1140 */       this.communication = null;
/*      */ 
/* 1142 */       this.connected = false;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void abruptlyCloseCommunication()
/*      */   {
/* 1156 */     if (this.communication != null) {
/* 1157 */       this.communication.close();
/* 1158 */       this.communication = null;
/*      */     }
/*      */ 
/* 1161 */     this.connected = false;
/*      */ 
/* 1163 */     stopAutoNoopTimer();
/*      */   }
/*      */ 
/*      */   public void login(String username, String password)
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException
/*      */   {
/* 1186 */     login(username, password, null);
/*      */   }
/*      */ 
/*      */   public void login(String username, String password, String account)
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException
/*      */   {
/* 1212 */     synchronized (this.lock)
/*      */     {
/* 1214 */       if (!this.connected) {
/* 1215 */         throw new IllegalStateException("Client not connected");
/*      */       }
/*      */ 
/* 1218 */       if (this.security == 2) {
/* 1219 */         this.communication.sendFTPCommand("AUTH TLS");
/* 1220 */         FTPReply r = this.communication.readFTPReply();
/* 1221 */         if (r.isSuccessCode()) {
/* 1222 */           this.communication.ssl(this.sslSocketFactory);
/*      */         } else {
/* 1224 */           this.communication.sendFTPCommand("AUTH SSL");
/* 1225 */           r = this.communication.readFTPReply();
/* 1226 */           if (r.isSuccessCode())
/* 1227 */             this.communication.ssl(this.sslSocketFactory);
/*      */           else {
/* 1229 */             throw new FTPException(r.getCode(), "SECURITY_FTPES cannot be applied: the server refused both AUTH TLS and AUTH SSL commands");
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1235 */       this.authenticated = false;
/*      */ 
/* 1240 */       this.communication.sendFTPCommand("USER " + username);
/* 1241 */       FTPReply r = this.communication.readFTPReply();
/*      */       boolean passwordRequired;
/*      */       boolean accountRequired;
/* 1242 */       switch (r.getCode())
/*      */       {
/*      */       case 230:
/* 1245 */         passwordRequired = false;
/* 1246 */         accountRequired = false;
/* 1247 */         break;
/*      */       case 331:
/* 1250 */         passwordRequired = true;
/*      */ 
/* 1252 */         accountRequired = false;
/* 1253 */         break;
/*      */       case 332:
/* 1256 */         passwordRequired = false;
/* 1257 */         accountRequired = true;
/*      */       default:
/* 1260 */         throw new FTPException(r);
/*      */       }
/*      */ 
/* 1263 */       if (passwordRequired) {
/* 1264 */         if (password == null){
/* 1265 */           throw new FTPException(331);
/*      */         }
/*      */ 
/* 1268 */         this.communication.sendFTPCommand("PASS " + password);
/* 1269 */         r = this.communication.readFTPReply();
/* 1270 */         switch (r.getCode())
/*      */         {
/*      */         case 230:
/* 1273 */           accountRequired = false;
/* 1274 */           break;
/*      */         case 332:
/* 1277 */           accountRequired = true;
/* 1278 */           break;
/*      */         default:
/* 1281 */           throw new FTPException(r);
/*      */         }
/*      */       }
/*      */ 
/* 1285 */       if (accountRequired){
/* 1286 */         if (account == null){
/* 1287 */           throw new FTPException(332);
/*      */         }
/*      */ 
/* 1290 */         this.communication.sendFTPCommand("ACCT " + account);
/* 1291 */         r = this.communication.readFTPReply();
/* 1292 */         switch (r.getCode())
/*      */         {
/*      */         case 230:
/* 1295 */           break;
/*      */         default:
/* 1298 */           throw new FTPException(r);
/*      */         }
/*      */       }
/*      */ 
/* 1303 */       this.authenticated = true;
/* 1304 */       this.username = username;
/* 1305 */       this.password = password;
/*      */     }
/*      */ 
/* 1308 */     postLoginOperations();
	          // setAutoNoopTimeout(TIME_OUT);
/*      */ 
/* 1310 */     startAutoNoopTimer();
/*      */   }
/*      */ 
/*      */   private void postLoginOperations()
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException
/*      */   {
/* 1329 */     synchronized (this.lock){
/* 1330 */       this.utf8Supported = false;
/* 1331 */       this.restSupported = false;
/* 1332 */       this.mlsdSupported = false;
/* 1333 */       this.modezSupported = false;
/* 1334 */       this.dataChannelEncrypted = false;
/* 1335 */       this.communication.sendFTPCommand("FEAT");
/* 1336 */       FTPReply r = this.communication.readFTPReply();
/* 1337 */       if (r.getCode() == 211){
/* 1338 */         String[] lines = r.getMessages();
/* 1339 */         for (int i = 1; i < lines.length - 1; i++) {
/* 1340 */           String feat = lines[i].trim().toUpperCase();
/*      */ 
/* 1342 */           if ("REST STREAM".equalsIgnoreCase(feat)) {
/* 1343 */             this.restSupported = true;
/*      */           }
/* 1347 */           else if ("UTF8".equalsIgnoreCase(feat)) {
/* 1348 */             this.utf8Supported = true;
/* 1349 */             this.communication.changeCharset("UTF-8");
/*      */           }
/* 1353 */           else if ("MLSD".equalsIgnoreCase(feat)) {
/* 1354 */             this.mlsdSupported = true;
/*      */           }
/* 1358 */           else if (("MODE Z".equalsIgnoreCase(feat)) || (feat.startsWith("MODE Z "))) {
/* 1359 */             this.modezSupported = true;
/*      */           }
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 1365 */       if (this.utf8Supported){
/* 1366 */         this.communication.sendFTPCommand("OPTS UTF8 ON");
/* 1367 */         this.communication.readFTPReply();
/*      */       }
/*      */ 
/* 1370 */       if ((this.security == 1) || (this.security == 2)) {
/* 1371 */         this.communication.sendFTPCommand("PBSZ 0");
/* 1372 */         this.communication.readFTPReply();
/* 1373 */         this.communication.sendFTPCommand("PROT P");
/* 1374 */         FTPReply reply = this.communication.readFTPReply();
/* 1375 */         if (reply.isSuccessCode())
/* 1376 */           this.dataChannelEncrypted = true;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void logout()
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException
/*      */   {
/* 1399 */     synchronized (this.lock)
/*      */     {
/* 1401 */       if (!this.connected) {
/* 1402 */         throw new IllegalStateException("Client not connected");
/*      */       }
/*      */ 
/* 1405 */       if (!this.authenticated) {
/* 1406 */         throw new IllegalStateException("Client not authenticated");
/*      */       }
/*      */ 
/* 1409 */       this.communication.sendFTPCommand("REIN");
/* 1410 */       FTPReply r = this.communication.readFTPReply();
/* 1411 */       if (!r.isSuccessCode()) {
/* 1412 */         throw new FTPException(r);
/*      */       }
/*      */ 
/* 1415 */       stopAutoNoopTimer();
/*      */ 
/* 1417 */       this.authenticated = false;
/* 1418 */       this.username = null;
/* 1419 */       this.password = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void noop()
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException
/*      */   {
/* 1438 */     synchronized (this.lock)
/*      */     {
/* 1440 */       if (!this.connected){
/* 1441 */         throw new IllegalStateException("Client not connected");
/*      */       }
/*      */ 
/* 1444 */       if (!this.authenticated) {
/* 1445 */         throw new IllegalStateException("Client not authenticated");
/*      */       }
/*      */ 
/*      */       try
/*      */       {
/* 1450 */         this.communication.sendFTPCommand("NOOP");
			       Log.i("test","noop====");
/* 1451 */         FTPReply r = this.communication.readFTPReply();
/* 1452 */         if (!r.isSuccessCode())
/* 1453 */           throw new FTPException(r);
/*      */       }
/*      */       finally
/*      */       {
/* 1457 */         touchAutoNoopTimer();
/*      */       }
/*      */     }
/*      */   }


/*      */   //发送FTP的信令  TODO
/*      */   public FTPReply sendCustomCommand(String command)
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException
/*      */   {
/* 1480 */     synchronized (this.lock)
/*      */     {
/* 1482 */       if (!this.connected) {
/* 1483 */         throw new IllegalStateException("Client not connected");
/*      */       }
/*      */ 
/* 1486 */       this.communication.sendFTPCommand(command);
/*      */ 
/* 1488 */       touchAutoNoopTimer();
/*      */
/* 1490 */       return this.communication.readFTPReply();
/*      */     }
/*      */   }
/*      */ 
/*      */   public FTPReply sendSiteCommand(String command)
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException
/*      */   {
/* 1510 */     synchronized (this.lock)
/*      */     {
/* 1512 */       if (!this.connected){
/* 1513 */         throw new IllegalStateException("Client not connected");
/*      */       }
/*      */
/* 1516 */       this.communication.sendFTPCommand("SITE " + command);
/*      */
/* 1518 */       touchAutoNoopTimer();
/*      */
/* 1520 */       return this.communication.readFTPReply();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void changeAccount(String account)
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException
/*      */   {
/* 1542 */     synchronized (this.lock)
/*      */     {
/* 1544 */       if (!this.connected) {
/* 1545 */         throw new IllegalStateException("Client not connected");
/*      */       }
/*      */ 
/* 1548 */       if (!this.authenticated) {
/* 1549 */         throw new IllegalStateException("Client not authenticated");
/*      */       }
/*      */ 
/* 1552 */       this.communication.sendFTPCommand("ACCT " + account);
/*      */ 
/* 1554 */       FTPReply r = this.communication.readFTPReply();
/*      */ 
/* 1556 */       touchAutoNoopTimer();
/*      */ 
/* 1558 */       if (!r.isSuccessCode())
/* 1559 */         throw new FTPException(r);
/*      */     }
/*      */   }
/*      */ 
/*      */   public String currentDirectory()
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException
/*      */   {
/* 1579 */     synchronized (this.lock)
/*      */     {
/* 1581 */       if (!this.connected) {
/* 1582 */         throw new IllegalStateException("Client not connected");
/*      */       }
/*      */ 
/* 1585 */       if (!this.authenticated) {
/* 1586 */         throw new IllegalStateException("Client not authenticated");
/*      */       }
/*      */ 
/* 1589 */       this.communication.sendFTPCommand("PWD");
/* 1590 */       FTPReply r = this.communication.readFTPReply();
/* 1591 */       touchAutoNoopTimer();
/* 1592 */       if (!r.isSuccessCode()) {
/* 1593 */         throw new FTPException(r);
/*      */       }
/*      */ 
/* 1596 */       String[] messages = r.getMessages();
/* 1597 */       if (messages.length != 1) {
/* 1598 */         throw new FTPIllegalReplyException();
/*      */       }
/* 1600 */       Matcher m = PWD_PATTERN.matcher(messages[0]);
/* 1601 */       if (m.find()) {
/* 1602 */         return messages[0].substring(m.start() + 1, m.end() - 1);
/*      */       }
/* 1604 */       throw new FTPIllegalReplyException();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void changeDirectory(String path)
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException
/*      */   {
/* 1625 */     synchronized (this.lock)
/*      */     {
/* 1627 */       if (!this.connected) {
/* 1628 */         throw new IllegalStateException("Client not connected");
/*      */       }
/*      */ 
/* 1631 */       if (!this.authenticated) {
/* 1632 */         throw new IllegalStateException("Client not authenticated");
/*      */       }
/*      */ 
/* 1635 */       this.communication.sendFTPCommand("CWD " + path);
/* 1636 */       FTPReply r = this.communication.readFTPReply();
/* 1637 */       touchAutoNoopTimer();
/* 1638 */       if (!r.isSuccessCode())
/* 1639 */         throw new FTPException(r);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void changeDirectoryUp()
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException
/*      */   {
/* 1658 */     synchronized (this.lock)
/*      */     {
/* 1660 */       if (!this.connected) {
/* 1661 */         throw new IllegalStateException("Client not connected");
/*      */       }
/*      */ 
/* 1664 */       if (!this.authenticated) {
/* 1665 */         throw new IllegalStateException("Client not authenticated");
/*      */       }
/*      */ 
/* 1668 */       this.communication.sendFTPCommand("CDUP");
/* 1669 */       FTPReply r = this.communication.readFTPReply();
/* 1670 */       touchAutoNoopTimer();
/* 1671 */       if (!r.isSuccessCode())
/* 1672 */         throw new FTPException(r);
/*      */     }
/*      */   }
/*      */ 
/*      */   public Date modifiedDate(String path)
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException
/*      */   {
/* 1695 */     synchronized (this.lock)
/*      */     {
/* 1697 */       if(!this.connected){
/* 1698 */         throw new IllegalStateException("Client not connected");
/*      */       }
/*      */ 
/* 1701 */       if (!this.authenticated){
/* 1702 */         throw new IllegalStateException("Client not authenticated");
/*      */       }
/*      */ 
/* 1705 */       this.communication.sendFTPCommand("MDTM " + path);
/* 1706 */       FTPReply r = this.communication.readFTPReply();
/* 1707 */       touchAutoNoopTimer();
/* 1708 */       if (!r.isSuccessCode()) {
/* 1709 */         throw new FTPException(r);
/*      */       }
/* 1711 */       String[] messages = r.getMessages();
/* 1712 */       if (messages.length != 1)
/* 1713 */         throw new FTPIllegalReplyException();
/*      */       try
/*      */       {
/* 1716 */         return MDTM_DATE_FORMAT.parse(messages[0]);
/*      */       } catch (ParseException e) {
/* 1718 */         throw new FTPIllegalReplyException();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public long fileSize(String path)
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException
/*      */   {
/* 1741 */     synchronized (this.lock)
/*      */     {
/* 1743 */       if (!this.connected) {
/* 1744 */         throw new IllegalStateException("Client not connected");
/*      */       }
/*      */ 
/* 1747 */       if (!this.authenticated) {
/* 1748 */         throw new IllegalStateException("Client not authenticated");
/*      */       }
/*      */ 
/* 1751 */       this.communication.sendFTPCommand("TYPE I");
/* 1752 */       FTPReply r = this.communication.readFTPReply();
/* 1753 */       touchAutoNoopTimer();
/* 1754 */       if (!r.isSuccessCode()) {
/* 1755 */         throw new FTPException(r);
/*      */       }
/*      */ 
/* 1758 */       this.communication.sendFTPCommand("SIZE " + path);
/* 1759 */       r = this.communication.readFTPReply();
/* 1760 */       touchAutoNoopTimer();
/* 1761 */       if (!r.isSuccessCode()) {
/* 1762 */         throw new FTPException(r);
/*      */       }
/* 1764 */       String[] messages = r.getMessages();
/* 1765 */       if (messages.length != 1)
/* 1766 */         throw new FTPIllegalReplyException();
/*      */       try
/*      */       {
/* 1769 */         return Long.parseLong(messages[0]);
/*      */       } catch (Throwable t) {
/* 1771 */         throw new FTPIllegalReplyException();
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void rename(String oldPath, String newPath)
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException
/*      */   {
/* 1807 */     synchronized (this.lock)
/*      */     {
/* 1809 */       if (!this.connected) {
/* 1810 */         throw new IllegalStateException("Client not connected");
/*      */       }
/*      */ 
/* 1813 */       if (!this.authenticated) {
/* 1814 */         throw new IllegalStateException("Client not authenticated");
/*      */       }
/*      */ 
/* 1817 */       this.communication.sendFTPCommand("RNFR " + oldPath);
/* 1818 */       FTPReply r = this.communication.readFTPReply();
/* 1819 */       touchAutoNoopTimer();
/* 1820 */       if (r.getCode() != 350) {
/* 1821 */         throw new FTPException(r);
/*      */       }
/*      */ 
/* 1824 */       this.communication.sendFTPCommand("RNTO " + newPath);
/* 1825 */       r = this.communication.readFTPReply();
/* 1826 */       touchAutoNoopTimer();
/* 1827 */       if (!r.isSuccessCode())
/* 1828 */         throw new FTPException(r);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void deleteFile(String path)
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException
/*      */   {
/* 1849 */     synchronized (this.lock)
/*      */     {
/* 1851 */       if (!this.connected){
/* 1852 */         throw new IllegalStateException("Client not connected");
/*      */       }
/*      */ 
/* 1855 */       if (!this.authenticated) {
/* 1856 */         throw new IllegalStateException("Client not authenticated");
/*      */       }

		         this.sendCustomCommand("VERSION "+ Constans.CLIENT_VERSION);
/* 1859 */       this.communication.sendFTPCommand("DELE " + path);
/* 1860 */       FTPReply r = this.communication.readFTPReply();
/* 1861 */       touchAutoNoopTimer();
/* 1862 */       if (!r.isSuccessCode())
/* 1863 */         throw new FTPException(r);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void deleteDirectory(String path)
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException
/*      */   {
/* 1884 */     synchronized (this.lock)
/*      */     {
/* 1886 */       if (!this.connected) {
/* 1887 */         throw new IllegalStateException("Client not connected");
/*      */       }
/*      */ 
/* 1890 */       if (!this.authenticated) {
/* 1891 */         throw new IllegalStateException("Client not authenticated");
/*      */       }
/*      */ 
/* 1894 */       this.communication.sendFTPCommand("RMD " + path);
/* 1895 */       FTPReply r = this.communication.readFTPReply();
/* 1896 */       touchAutoNoopTimer();
/* 1897 */       if (!r.isSuccessCode())
/* 1898 */         throw new FTPException(r);
/*      */     }
/*      */   }
/*      */ 
/*      */   public void createDirectory(String directoryName)
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException
/*      */   {
/* 1920 */     synchronized (this.lock)
/*      */     {
/* 1922 */       if (!this.connected) {
/* 1923 */         throw new IllegalStateException("Client not connected");
/*      */       }
/*      */ 
/* 1926 */       if (!this.authenticated) {
/* 1927 */         throw new IllegalStateException("Client not authenticated");
/*      */       }
/*      */ 
/* 1930 */       this.communication.sendFTPCommand("MKD " + directoryName);
/* 1931 */       FTPReply r = this.communication.readFTPReply();
/* 1932 */       touchAutoNoopTimer();
/* 1933 */       if (!r.isSuccessCode())
/* 1934 */         throw new FTPException(r);
/*      */     	}
/*      */   }
/*      */ 
/*      */   public String[] help()
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException
/*      */   {
/* 1955 */     synchronized (this.lock)
/*      */     {
/* 1957 */       if (!this.connected) {
/* 1958 */         throw new IllegalStateException("Client not connected");
/*      */       }
/*      */ 
/* 1961 */       if (!this.authenticated) {
/* 1962 */         throw new IllegalStateException("Client not authenticated");
/*      */       }
/*      */ 
/* 1965 */       this.communication.sendFTPCommand("HELP");
/* 1966 */       FTPReply r = this.communication.readFTPReply();
/* 1967 */       touchAutoNoopTimer();
/* 1968 */       if (!r.isSuccessCode()) {
/* 1969 */         throw new FTPException(r);
/*      */       }
/* 1971 */       return r.getMessages();
/*      */     }
/*      */   }
	/*      */    //文件共享发送心跳
	         public void sendServerHearte()
		     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException
			 {
			    synchronized (this.lock){
				if (!this.connected){
/* 1958 */         throw new IllegalStateException("Client not connected");
/*      */        }
/*      */
/* 1961 */       if (!this.authenticated){
/* 1962 */         throw new IllegalStateException("Client not authenticated");
/*      */        }

				 this.communication.sendFTPCommand("HEARTE");
/* 1966 */       FTPReply r = this.communication.readFTPReply();
/* 1967 */       touchAutoNoopTimer();
					Log.i("test","hearte:"+r.getCode());
/* 1968 */       if (!r.isSuccessCode()&&r.getCode()!=150&&r.getCode()!=502){
/* 1969 */           throw new FTPException(r);
/*      */        }
				}

			 }

/*      */ /**



/*      */    //请求客户端版本号
/*      */   public String[] serverStatus()
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException
/*      */   {
/* 1991 */     synchronized (this.lock)
/*      */     {
/* 1993 */       if (!this.connected) {
/* 1994 */         throw new IllegalStateException("Client not connected");
/*      */       }
/*      */ 
/* 1997 */       if (!this.authenticated) {
/* 1998 */         throw new IllegalStateException("Client not authenticated");
/*      */       }
/*      */ 
/* 2001 */       this.communication.sendFTPCommand("STAT");
/* 2002 */       FTPReply r = this.communication.readFTPReply();
/* 2003 */       touchAutoNoopTimer();
/* 2004 */       if (!r.isSuccessCode()){
/* 2005 */         throw new FTPException(r);
/*      */       }
/* 2007 */       return r.getMessages();
/*      */     }
/*      */   }
/*      */ /**
 * 
 * @param fileSpec   根目录-相册
 * @param type        设备类型
 */
/*      */   public FTPFile[] list(String fileSpec, int type)
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException, FTPListParseException
/*      */   {
/* 2061 */     synchronized (this.lock)
/*      */     {
/* 2063 */       if (!this.connected) {
/* 2064 */         throw new IllegalStateException("Client not connected");
/*      */       }
/*      */ 
/* 2067 */       if (!this.authenticated) {
/* 2068 */         throw new IllegalStateException("Client not authenticated");
/*      */       }
/*      */ 
/* 2071 */       this.communication.sendFTPCommand("TYPE A");
/* 2072 */       FTPReply r = this.communication.readFTPReply();
/* 2073 */       touchAutoNoopTimer();
/* 2074 */       if (!r.isSuccessCode()) {
/* 2075 */         throw new FTPException(r);
/*      */       }
/*      */ 
/* 2078 */       FTPDataTransferConnectionProvider provider = openDataTransferChannel();
/*      */       boolean mlsdCommand;
/*      */ 
/* 2081 */       if (this.mlsdPolicy == 0) {
/* 2082 */         mlsdCommand = this.mlsdSupported;
/*      */       }
/*      */       else
/*      */       {
/*      */       
/* 2083 */         if (this.mlsdPolicy == 1)
/* 2084 */           mlsdCommand = true;
/*      */         else
/* 2086 */           mlsdCommand = false;
/*      */       }
/* 2088 */       String command = mlsdCommand ? "MLSD" : "LIST";
/*      */ 
/* 2090 */       if ((fileSpec != null) && (fileSpec.length() > 0)){
/* 2091 */            command = command + " " + fileSpec;
/*      */       }
/*      */ 
/* 2094 */       ArrayList lines = new ArrayList();
/*      */ 
/* 2096 */       boolean wasAborted = false;
/*      */ 
/* 2098 */       this.communication.sendFTPCommand(command);
/*      */       try {
/*      */         Socket dtConnection;
/*      */         try {
/* 2102 */           dtConnection = provider.openDataTransferConnection();
/*      */         } finally {
/* 2104 */           provider.dispose();
/*      */         }
/*      */ 
/* 2107 */         synchronized (this.abortLock) {
/* 2108 */           this.ongoingDataTransfer = true;
/* 2109 */           this.aborted = false;
/* 2110 */           this.consumeAborCommandReply = false;
/*      */         }
/*      */ 
/* 2113 */         NVTASCIIReader dataReader = null;
/*      */         try
/*      */         {
/* 2116 */           this.dataTransferInputStream = dtConnection.getInputStream();
/*      */ 
/* 2118 */           if (this.modezEnabled) {
/* 2119 */             this.dataTransferInputStream = new InflaterInputStream(this.dataTransferInputStream);
/*      */           }
/*      */ 
/* 2122 */           dataReader = new NVTASCIIReader(this.dataTransferInputStream, mlsdCommand ? "UTF-8" : pickCharset());
/*      */           String line;
/* 2124 */           while ((line = dataReader.readLine()) != null)
/* 2125 */             if (line.length() > 0)
/* 2126 */               lines.add(line);

/*      */         }
/*      */         catch (IOException e)
/*      */         {
/* 2130 */           synchronized (this.abortLock){
/* 2131 */             if (this.aborted) {
/* 2132 */               throw new FTPAbortedException();
/*      */             }
/* 2134 */             throw new FTPDataTransferException("I/O error in data transfer", e);
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/* 2139 */           if (dataReader != null)
/*      */             try {
/* 2141 */               dataReader.close();
/*      */             }
/*      */             catch (Throwable t)
/*      */             {
/*      */             }
/*      */           try {
/* 2147 */             dtConnection.close();
/*      */           }
/*      */           catch (Throwable t)
/*      */           {
/*      */           }
/* 2152 */           this.dataTransferInputStream = null;
/*      */ 
/* 2154 */           synchronized (this.abortLock) {
/* 2155 */             wasAborted = this.aborted;
/* 2156 */             this.ongoingDataTransfer = false;
/* 2157 */             this.aborted = false;
/*      */           }
/*      */         }
/*      */       } finally {
/* 2161 */         r = this.communication.readFTPReply();
/* 2162 */         touchAutoNoopTimer();
/* 2163 */         if ((r.getCode() != 150) && (r.getCode() != 125)) {
/* 2164 */           throw new FTPException(r);
/*      */         }
/*      */ 
/* 2167 */         r = this.communication.readFTPReply();
/* 2168 */         if ((!wasAborted) && (r.getCode() != 226)) {
/* 2169 */           throw new FTPException(r);
/*      */         }
/*      */ 
/* 2172 */         if (this.consumeAborCommandReply) {
/* 2173 */           this.communication.readFTPReply();
/* 2174 */           this.consumeAborCommandReply = false;
/*      */         }
/*      */       }

/*      */ 
/* 2178 */       int size = lines.size();
/* 2179 */       String[] list = new String[size];
/* 2180 */       for (int i = 0; i < size; i++) {

/* 2181 */         list[i] = ((String)lines.get(i));
/*      */       }


/*      */ 
/* 2184 */       FTPFile[] ret = null;
/*      */     
/* 2185 */       if (mlsdCommand)
/*      */       {
/* 2187 */         MLSDListParser parser = new MLSDListParser();
/* 2188 */         ret = parser.parse(list,type);
/*      */       }
/*      */       else {
/* 2191 */         if (this.parser != null) {
/*      */           try
/*      */           {
/* 2194 */             ret = this.parser.parse(list,type);
/*      */           }
/*      */           catch (FTPListParseException e) {
/* 2197 */             this.parser = null;
/*      */           }
/*      */         }
/*      */ 
/* 2201 */         if (ret == null)
/*      */         {
					switch (type) {
					case 0:
						ret=new FTPFile[list.length];
						try {
						for (int i = 0; i < list.length; i++) {
							String[] lista=list[i].split(QMCommander.icon);
							FTPFile mv=new FTPFile();
							if(lista[0].startsWith("-r"))
								mv.setDir(1);
							else
								mv.setDir(0);
							mv.setName(lista[6]);
							mv.setLink(lista[3]);
							mv.setModifiedDate(new SimpleDateFormat("yyyy MMM dd HH:mm", Locale.US).parse(lista[5]));
							mv.setSize(Long.parseLong(lista[4]));
							if(lista[2].equals("allowDownload"))
								mv.setPermit(0);
							else
								mv.setPermit(1);
							ret[i]=mv;
						}
						} catch (Exception e) {
							e.printStackTrace();
						}
						break;
					default:
						for (Iterator i = this.listParsers.iterator(); i
								.hasNext();) {
							FTPListParser aux = (FTPListParser) i.next();
							try {
								ret = aux.parse(list,type);

								this.parser = aux;
							} catch (FTPListParseException e) {
							}
						}
						break;

					}
	
					/*for (Iterator i = this.listParsers.iterator(); i
							.hasNext();) {
						FTPListParser aux = (FTPListParser) i.next();
						try {
							ret = aux.parse(list,type);
				
							this.parser = aux;
						} catch (FTPListParseException e) {
						}
					}*/
					
/*      */         }
/*      */ 
/*      */       }
/*      */ 
/* 2219 */       if (ret == null)
/*      */       {
/* 2221 */         throw new FTPListParseException();
/*      */       }
/*      */ 
/* 2224 */       return ret;
/*      */     }



/*      */   }

/*     */ 

/*     */ 
/*      */ 
/*      */   public FTPFile[] list(int type)
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException, FTPListParseException
/*      */   {
/* 2273 */     return list(null,type);
/*      */   }
/*      */ 
/*      */   public String[] listNames()
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException, FTPListParseException
/*      */   {
/* 2314 */     synchronized (this.lock)
/*      */     {
/* 2316 */       if (!this.connected) {
/* 2317 */         throw new IllegalStateException("Client not connected");
/*      */       }
/*      */ 
/* 2320 */       if (!this.authenticated) {
/* 2321 */         throw new IllegalStateException("Client not authenticated");
/*      */       }
/*      */ 
/* 2324 */       this.communication.sendFTPCommand("TYPE A");
/* 2325 */       FTPReply r = this.communication.readFTPReply();
/* 2326 */       touchAutoNoopTimer();
/* 2327 */       if (!r.isSuccessCode()) {
/* 2328 */         throw new FTPException(r);
/*      */       }
/*      */ 
/* 2331 */       ArrayList lines = new ArrayList();
/*      */ 
/* 2333 */       boolean wasAborted = false;
/*      */ 
/* 2335 */       FTPDataTransferConnectionProvider provider = openDataTransferChannel();
/*      */ 
/* 2337 */       this.communication.sendFTPCommand("NLST");
/*      */       try {
/*      */         Socket dtConnection;
/*      */         try {
/* 2341 */           dtConnection = provider.openDataTransferConnection();
/*      */         } finally {
/* 2343 */           provider.dispose();
/*      */         }
/*      */ 
/* 2346 */         synchronized (this.abortLock) {
/* 2347 */           this.ongoingDataTransfer = true;
/* 2348 */           this.aborted = false;
/* 2349 */           this.consumeAborCommandReply = false;
/*      */         }
/*      */ 
/* 2352 */         NVTASCIIReader dataReader = null;
/*      */         try
/*      */         {
/* 2355 */           this.dataTransferInputStream = dtConnection.getInputStream();
/*      */ 
/* 2357 */           if (this.modezEnabled) {
/* 2358 */             this.dataTransferInputStream = new InflaterInputStream(this.dataTransferInputStream);
/*      */           }
/*      */ 
/* 2361 */           dataReader = new NVTASCIIReader(this.dataTransferInputStream, pickCharset());
/*      */           String line;
/* 2363 */           while ((line = dataReader.readLine()) != null)
/* 2364 */             if (line.length() > 0)
/* 2365 */               lines.add(line);
					System.out.println(line);
/*      */         }
/*      */         catch (IOException e)
/*      */         {
/* 2369 */           synchronized (this.abortLock) {
/* 2370 */             if (this.aborted) {
/* 2371 */               throw new FTPAbortedException();
/*      */             }
/* 2373 */             throw new FTPDataTransferException("I/O error in data transfer", e);
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/* 2378 */           if (dataReader != null)
/*      */             try {
/* 2380 */               dataReader.close();
/*      */             }
/*      */             catch (Throwable t)
/*      */             {
/*      */             }
/*      */           try {
/* 2386 */             dtConnection.close();
/*      */           }
/*      */           catch (Throwable t)
/*      */           {
/*      */           }
/* 2391 */           this.dataTransferInputStream = null;
/*      */ 
/* 2393 */           synchronized (this.abortLock) {
/* 2394 */             wasAborted = this.aborted;
/* 2395 */             this.ongoingDataTransfer = false;
/* 2396 */             this.aborted = false;
/*      */           }
/*      */         }
/*      */       } finally {
/* 2400 */         r = this.communication.readFTPReply();
/* 2401 */         if ((r.getCode() != 150) && (r.getCode() != 125)) {
/* 2402 */           throw new FTPException(r);
/*      */         }
/*      */ 
/* 2405 */         r = this.communication.readFTPReply();
/* 2406 */         if ((!wasAborted) && (r.getCode() != 226)) {
/* 2407 */           throw new FTPException(r);
/*      */         }
/*      */ 
/* 2410 */         if (this.consumeAborCommandReply) {
/* 2411 */           this.communication.readFTPReply();
/* 2412 */           this.consumeAborCommandReply = false;
/*      */         }
/*      */       }
/*      */ 
/* 2416 */       int size = lines.size();
/* 2417 */       String[] list = new String[size];
/* 2418 */       for (int i = 0; i < size; i++) {
/* 2419 */         list[i] = ((String)lines.get(i));
/*      */       }
/* 2421 */       return list;
/*      */     }
/*      */   }
/*      */ 
/*      */   public void upload(QMLocalFile file)
/*      */     throws IllegalStateException, FileNotFoundException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException
/*      */   {
/* 2457 */     upload(file, null,0L, null);
/*      */   }
/*      */ 
/*      */   public void upload(QMLocalFile file, String dir, FTPDataTransferListener listener)
/*      */     throws IllegalStateException, FileNotFoundException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException
/*      */   {
/* 2495 */     upload(file,dir, 0L, listener);
/*      */   }
/*      */ 
/*      */   public void upload(QMLocalFile file, long restartAt)
/*      */     throws IllegalStateException, FileNotFoundException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException
/*      */   {
/* 2534 */     upload(file,null, restartAt, null);
/*      */   }
/*      */ 
/*      */   public void upload(QMLocalFile file, String dir, long restartAt, FTPDataTransferListener listener)
/*      */     throws IllegalStateException, FileNotFoundException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException
/*      */   {
/* 2579 */     InputStream inputStream = null;
/*      */     try {
/* 2581 */       inputStream = new FileInputStream(new File(file.getPath()));
/*      */     } catch (IOException e) {
/* 2583 */       throw new FTPDataTransferException(e);
/*      */     }
/*      */     try {
				if(dir!=null)
/* 2586 */       	upload(dir+file.getRemoteName(), inputStream, restartAt, restartAt, listener,file);
				else
				     upload(file.getRemoteName(), inputStream, restartAt, restartAt, listener,file);
/*      */     } catch (IllegalStateException e) {
/* 2588 */       throw e;
/*      */     } catch (IOException e){
/* 2590 */       throw e;
/*      */     } catch (FTPIllegalReplyException e) {
/* 2592 */       throw e;
/*      */     } catch (FTPException e) {
/* 2594 */       throw e;
/*      */     } catch (FTPDataTransferException e) {
/* 2596 */       throw e;
/*      */     } catch (FTPAbortedException e) {
/* 2598 */       throw e;
/*      */     } finally {
/* 2600 */       if (inputStream != null)
/*      */         try {
/* 2602 */           inputStream.close();
/*      */         }
/*      */         catch (Throwable t)
/*      */         {
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   @SuppressWarnings("resource")
public void upload(String fileName, InputStream inputStream, long restartAt, long streamOffset, FTPDataTransferListener listener, QMLocalFile file)
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException
/*      */   {
/* 2652 */     synchronized (this.lock)
/*      */     {
/* 2654 */       if (!this.connected) {
/* 2655 */         throw new IllegalStateException("Client not connected");
/*      */       }
/*      */ 
/* 2658 */       if (!this.authenticated) {
/* 2659 */         throw new IllegalStateException("Client not authenticated");
/*      */       }
/*      */ 
/* 2662 */       int tp = this.type;
/* 2663 */       if (tp == 0) {
/* 2664 */         tp = detectType(fileName);
/*      */       }
		         this.sendCustomCommand("UPLOADSIZE "+ String.valueOf(file.getSize()));
/* 2666 */       if (tp == 1)
/* 2667 */         this.communication.sendFTPCommand("TYPE A");
/* 2668 */       else if (tp == 2) {
/* 2669 */         this.communication.sendFTPCommand("TYPE I");
/*      */       }
/* 2671 */       FTPReply r = this.communication.readFTPReply();
/* 2672 */       touchAutoNoopTimer();
/* 2673 */       if (!r.isSuccessCode()) {
/* 2674 */         throw new FTPException(r);
/*      */       }
/*      */ 
/* 2677 */       FTPDataTransferConnectionProvider provider = openDataTransferChannel();
/*      */ 
/* 2679 */       if ((this.restSupported) || (restartAt > 0L)) {
/* 2680 */         boolean done = false;
/*      */         try {
/* 2682 */           this.communication.sendFTPCommand("REST " + restartAt);
/* 2683 */           r = this.communication.readFTPReply();
/* 2684 */           touchAutoNoopTimer();
/* 2685 */           if ((r.getCode() != 350) && (((r.getCode() != 501) && (r.getCode() != 502)) || (restartAt > 0L))) {
/* 2686 */             throw new FTPException(r);
/*      */           }
/* 2688 */           done = true;
/*      */         } finally {
/* 2690 */           if (!done){
/* 2691 */             provider.dispose();
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 2696 */       boolean wasAborted = false;
/*      */ 
/* 2698 */       this.communication.sendFTPCommand("STOR " + fileName);
/*      */       try {
/*      */         Socket dtConnection;
/*      */         try {
/* 2702 */           dtConnection = provider.openDataTransferConnection();


/*      */         } finally {
/* 2704 */           provider.dispose();
/*      */         }
/*      */ 
/* 2707 */         synchronized (this.abortLock) {
/* 2708 */           this.ongoingDataTransfer = true;
/* 2709 */           this.aborted = false;
/* 2710 */           this.consumeAborCommandReply = false;
/*      */         }
/*      */ 
/*      */         try
/*      */         {

/* 2715 */           inputStream.skip(streamOffset);
/*      */
/* 2717 */           this.dataTransferOutputStream = dtConnection.getOutputStream();
/*      */
/* 2719 */           if (this.modezEnabled) {
/* 2720 */             this.dataTransferOutputStream = new DeflaterOutputStream(this.dataTransferOutputStream);
/*      */           }

/*      */
/* 2723 */           if (listener != null) {
/* 2724 */             listener.started();
/*      */           }
/*      */

/* 2727 */           if (tp == 1) {
/* 2728 */             Object reader = new InputStreamReader(inputStream);
/* 2729 */             Object writer = new OutputStreamWriter(this.dataTransferOutputStream, pickCharset());
/*      */
/* 2731 */             char[] buffer = new char[65536];
/*      */             int l;
/* 2733 */             while ((l = ((Reader)reader).read(buffer)) != -1){
/* 2734 */               ((Writer)writer).write(buffer, 0, l);
/* 2735 */               ((Writer)writer).flush();
/* 2736 */               if (listener != null)
/* 2737 */                 listener.transferred(l);
						int buf=FTPServerService.getSendSpeed();
						if(buf!=-1){
							buffer=new char[buf];
						}
/*      */             }
/*      */           }
/* 2740 */           else if (tp == 2){
/* 2741 */             byte[] buffer = new byte[65536];
/*      */             int l;
/* 2743 */             while ((l = inputStream.read(buffer)) != -1){
/* 2744 */               this.dataTransferOutputStream.write(buffer, 0, l);
/* 2745 */               this.dataTransferOutputStream.flush();
/* 2746 */               if (listener != null)
/* 2747 */                 listener.transferred(l);
						int buf=FTPServerService.getSendSpeed();
						if(buf!=-1){
							buffer=new byte[buf];
						}
/*      */               }

/*      */           }
/*      */         }
/*      */         catch (Exception e){
				    e.printStackTrace();
/* 2752 */           synchronized (this.abortLock) {
/* 2753 */             if (this.aborted) {
/* 2754 */               if (listener != null) {
/* 2755 */                 listener.aborted();
/*      */               }
/* 2757 */               throw new FTPAbortedException();
/*      */             }
/* 2759 */             if (listener != null){
/* 2760 */               listener.failed();
/*      */             }
/* 2762 */             throw new FTPDataTransferException("I/O error in data transfer", e);
/*      */           }
/*      */ 
/*      */         }
/*      */         finally
/*      */         {
/* 2768 */           if (this.dataTransferOutputStream != null)
/*      */             try {
/* 2770 */               this.dataTransferOutputStream.close();
/*      */             }
/*      */             catch (Throwable t)
/*      */             {
/*      */             }
/*      */           try {
/* 2776 */             dtConnection.close();
/*      */           }
/*      */           catch (Throwable t)
/*      */           {
/*      */           }
/* 2781 */           this.dataTransferOutputStream = null;
/*      */ 
/* 2783 */           synchronized (this.abortLock) {
/* 2784 */             wasAborted = this.aborted;
/* 2785 */             this.ongoingDataTransfer = false;
/* 2786 */             this.aborted = false;
/*      */           }
/*      */         }
/*      */       }

/*      */       finally{
/* 2791 */         r = this.communication.readFTPReply();
/* 2792 */         touchAutoNoopTimer();
/* 2793 */         if ((r.getCode() != 150) && (r.getCode() != 125)) {
/* 2794 */           throw new FTPException(r);
/*      */         }
/*      */
/* 2797 */         r = this.communication.readFTPReply();
/* 2798 */         if ((!wasAborted) && (r.getCode() != 226)){
/* 2799 */             throw new FTPException(r);
/*      */         }
/*      */ 
/* 2802 */         if (this.consumeAborCommandReply){
/* 2803 */           this.communication.readFTPReply();
/* 2804 */           this.consumeAborCommandReply = false;
/*      */         }

/*      */       }
/*      */
/* 2808 */       if (listener != null)
/* 2809 */         listener.completed();
/*      */       }
/*      */   }
/*      */ 
/*      */   public void append(File file)
/*      */     throws IllegalStateException, FileNotFoundException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException
/*      */   {
/* 2849 */     append(file, null);
/*      */   }
/*      */ 
/*      */   public void append(File file, FTPDataTransferListener listener)
/*      */     throws IllegalStateException, FileNotFoundException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException
/*      */   {
/* 2889 */     if (!file.exists()) {
/* 2890 */       throw new FileNotFoundException(file.getAbsolutePath());
/*      */     }
/* 2892 */     InputStream inputStream = null;
/*      */     try {
/* 2894 */       inputStream = new FileInputStream(file);
/*      */     } catch (IOException e) {
/* 2896 */       throw new FTPDataTransferException(e);
/*      */     }
/*      */     try {
/* 2899 */       append(file.getName(), inputStream, 0L, listener);
/*      */     } catch (IllegalStateException e) {
/* 2901 */       throw e;
/*      */     } catch (IOException e) {
/* 2903 */       throw e;
/*      */     } catch (FTPIllegalReplyException e) {
/* 2905 */       throw e;
/*      */     } catch (FTPException e) {
/* 2907 */       throw e;
/*      */     } catch (FTPDataTransferException e) {
/* 2909 */       throw e;
/*      */     } catch (FTPAbortedException e) {
/* 2911 */       throw e;
/*      */     } finally {
/* 2913 */       if (inputStream != null)
/*      */         try {
/* 2915 */           inputStream.close();
/*      */         }
/*      */         catch (Throwable t)
/*      */         {
/*      */         }
/*      */     }
/*      */   }
/*      */ 
/*      */   public void append(String fileName, InputStream inputStream, long streamOffset, FTPDataTransferListener listener)
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException
/*      */   {
/* 2962 */     synchronized (this.lock)
/*      */     {
/* 2964 */       if (!this.connected) {
/* 2965 */         throw new IllegalStateException("Client not connected");
/*      */       }
/*      */ 
/* 2968 */       if (!this.authenticated) {
/* 2969 */         throw new IllegalStateException("Client not authenticated");
/*      */       }
/*      */ 
/* 2972 */       int tp = this.type;
/* 2973 */       if (tp == 0) {
/* 2974 */         tp = detectType(fileName);
/*      */       }
/* 2976 */       if (tp == 1)
/* 2977 */         this.communication.sendFTPCommand("TYPE A");
/* 2978 */       else if (tp == 2) {
/* 2979 */         this.communication.sendFTPCommand("TYPE I");
/*      */       }
/* 2981 */       FTPReply r = this.communication.readFTPReply();
/* 2982 */       touchAutoNoopTimer();
/* 2983 */       if (!r.isSuccessCode()) {
/* 2984 */         throw new FTPException(r);
/*      */       }
/*      */ 
/* 2987 */       boolean wasAborted = false;
/*      */ 
/* 2989 */       FTPDataTransferConnectionProvider provider = openDataTransferChannel();
/*      */ 
/* 2991 */       this.communication.sendFTPCommand("APPE " + fileName);
/*      */       try {
/*      */         Socket dtConnection;
/*      */         try {
/* 2995 */           dtConnection = provider.openDataTransferConnection();
/*      */         } finally {
/* 2997 */           provider.dispose();
/*      */         }
/*      */ 
/* 3000 */         synchronized (this.abortLock) {
/* 3001 */           this.ongoingDataTransfer = true;
/* 3002 */           this.aborted = false;
/* 3003 */           this.consumeAborCommandReply = false;
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/* 3008 */           inputStream.skip(streamOffset);
/*      */ 
/* 3010 */           this.dataTransferOutputStream = dtConnection.getOutputStream();
/*      */ 
/* 3012 */           if (this.modezEnabled) {
/* 3013 */             this.dataTransferOutputStream = new DeflaterOutputStream(this.dataTransferOutputStream);
/*      */           }
/*      */ 
/* 3016 */           if (listener != null) {
/* 3017 */             listener.started();
/*      */           }
/*      */ 
/* 3020 */           if (tp == 1) {
/* 3021 */             Object reader = new InputStreamReader(inputStream);
/* 3022 */             Object writer = new OutputStreamWriter(this.dataTransferOutputStream, pickCharset());
/*      */ 
/* 3024 */             char[] buffer = new char[65536];
/*      */             int l;
/* 3026 */             while ((l = ((Reader)reader).read(buffer)) != -1) {
/* 3027 */               ((Writer)writer).write(buffer, 0, l);
/* 3028 */               ((Writer)writer).flush();
/* 3029 */               if (listener != null)
/* 3030 */                 listener.transferred(l);
/*      */             }
/*      */           }
/* 3033 */           else if (tp == 2) {
/* 3034 */             byte[] buffer = new byte[65536];
/*      */             int l;
/* 3036 */             while ((l = inputStream.read(buffer)) != -1) {
/* 3037 */               this.dataTransferOutputStream.write(buffer, 0, l);
/* 3038 */               this.dataTransferOutputStream.flush();
/* 3039 */               if (listener != null)
/* 3040 */                 listener.transferred(l);
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (IOException e) {
/* 3045 */           synchronized (this.abortLock) {
/* 3046 */             if (this.aborted) {
/* 3047 */               if (listener != null) {
/* 3048 */                 listener.aborted();
/*      */               }
/* 3050 */               throw new FTPAbortedException();
/*      */             }
/* 3052 */             if (listener != null) {
/* 3053 */               listener.failed();
/*      */             }
/* 3055 */             throw new FTPDataTransferException("I/O error in data transfer", e);
/*      */           }
/*      */         }
/*      */         finally
/*      */         {
/* 3061 */           if (this.dataTransferOutputStream != null)
/*      */             try {
/* 3063 */               this.dataTransferOutputStream.close();
/*      */             }
/*      */             catch (Throwable t)
/*      */             {
/*      */             }
/*      */           try {
/* 3069 */             dtConnection.close();
/*      */           }
/*      */           catch (Throwable t)
/*      */           {
/*      */           }
/* 3074 */           this.dataTransferOutputStream = null;
/*      */ 
/* 3076 */           synchronized (this.abortLock) {
/* 3077 */             wasAborted = this.aborted;
/* 3078 */             this.ongoingDataTransfer = false;
/* 3079 */             this.aborted = false;
/*      */           }
/*      */         }
/*      */       } finally {
/* 3083 */         r = this.communication.readFTPReply();
/* 3084 */         touchAutoNoopTimer();
/* 3085 */         if ((r.getCode() != 150) && (r.getCode() != 125)) {
/* 3086 */           throw new FTPException(r);
/*      */         }
/*      */ 
/* 3089 */         r = this.communication.readFTPReply();
/* 3090 */         if ((!wasAborted) && (r.getCode() != 226)) {
/* 3091 */           throw new FTPException(r);
/*      */         }
/*      */ 
/* 3094 */         if (this.consumeAborCommandReply) {
/* 3095 */           this.communication.readFTPReply();
/* 3096 */           this.consumeAborCommandReply = false;
/*      */         }
/*      */       }
/*      */ 
/* 3100 */       if (listener != null)
/* 3101 */         listener.completed();
/*      */     }
/*      */   }
/*      */ 
/*      */   public void download(String remoteFileName, File localFile)
/*      */     throws IllegalStateException, FileNotFoundException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException
/*      */   {
/* 3141 */     download(remoteFileName, localFile, 0L, null);
/*      */   }
/*      */ 
/*      */   public void download(String remoteFileName, File localFile, FTPDataTransferListener listener)
/*      */     throws IllegalStateException, FileNotFoundException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException
/*      */   {
/* 3181 */     download(remoteFileName, localFile, 0L, listener);
/*      */   }
/*      */ 
/*      */   public void download(String remoteFileName, File localFile, long restartAt)
/*      */     throws IllegalStateException, FileNotFoundException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException
/*      */   {
/* 3224 */     download(remoteFileName, localFile, restartAt, null);
/*      */   }
/*      */ 
/*      */   public void download(String remoteFileName, File localFile, long restartAt, FTPDataTransferListener listener)
/*      */     throws IllegalStateException, FileNotFoundException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException
/*      */   {
/* 3269 */     OutputStream outputStream = null;
/*      */     try {
/* 3271 */       outputStream = new FileOutputStream(localFile, restartAt > 0L);
/*      */     } catch (IOException e) {
/* 3273 */       throw new FTPDataTransferException(e);
/*      */     }
/*      */     try {
/* 3276 */       download(remoteFileName, outputStream, restartAt, listener);
/*      */     } catch (IllegalStateException e) {
/* 3278 */       throw e;
/*      */     } catch (IOException e) {
/* 3280 */       throw e;
/*      */     } catch (FTPIllegalReplyException e) {
/* 3282 */       throw e;
/*      */     } catch (FTPException e) {
/* 3284 */       throw e;
/*      */     } catch (FTPDataTransferException e) {
/* 3286 */       throw e;
/*      */     } catch (FTPAbortedException e) {
/* 3288 */       throw e;
/*      */     } finally {
/* 3290 */       if (outputStream != null)
/*      */         try {
/* 3292 */           outputStream.close();
/*      */         }
/*      */         catch (Throwable t)
/*      */         {
/*      */         }
/*      */     }
/*      */   }

		public void download2(Activity context, String remoteFileName, String localFileName, FTPDataTransferListener listener)
/*      */     throws IllegalStateException, FileNotFoundException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException
/*      */   {
/* 3269 */     OutputStream outputStream = null;
/*      */     try {
/* 3271 */       outputStream = context.openFileOutput(localFileName, Context.MODE_PRIVATE);
/*      */     } catch (IOException e) {
/* 3273 */       throw new FTPDataTransferException(e);
/*      */     }
/*      */     try {
/* 3276 */       download(remoteFileName, outputStream, 0L, listener);
/*      */     } catch (IllegalStateException e) {
/* 3278 */       throw e;
/*      */     } catch (IOException e) {
/* 3280 */       throw e;
/*      */     } catch (FTPIllegalReplyException e) {
/* 3282 */       throw e;
/*      */     } catch (FTPException e) {
/* 3284 */       throw e;
/*      */     } catch (FTPDataTransferException e) {
/* 3286 */       throw e;
/*      */     } catch (FTPAbortedException e) {
/* 3288 */       throw e;
/*      */     } finally {
/* 3290 */       if (outputStream != null)
/*      */         try {
/* 3292 */           outputStream.close();
/*      */         }
/*      */         catch (Throwable t)
/*      */         {
/*      */         }
/*      */     }
/*      */   }

/*      */ 
/*      */   public void download(String fileName, OutputStream outputStream, long restartAt, FTPDataTransferListener listener)
/*      */     throws IllegalStateException, IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException, FTPAbortedException
/*      */   {
/* 3340 */     synchronized (this.lock)
/*      */     {
/* 3342 */       if (!this.connected) {
/* 3343 */         throw new IllegalStateException("Client not connected");
/*      */       }
/*      */ 
/* 3346 */       if (!this.authenticated) {
/* 3347 */         throw new IllegalStateException("Client not authenticated");
/*      */       }
/*      */ 
/* 3350 */       int tp = this.type;
/* 3351 */       if (tp == 0) {
/* 3352 */         tp = detectType(fileName);
/*      */       }
/* 3354 */       if (tp == 1)
/* 3355 */         this.communication.sendFTPCommand("TYPE A");
/* 3356 */       else if (tp == 2) {
/* 3357 */         this.communication.sendFTPCommand("TYPE I");
/*      */       }
/* 3359 */       FTPReply r = this.communication.readFTPReply();
/* 3360 */       touchAutoNoopTimer();
/* 3361 */       if (!r.isSuccessCode()) {
/* 3362 */         throw new FTPException(r);
/*      */       }
/*      */ 
/* 3365 */       FTPDataTransferConnectionProvider provider = openDataTransferChannel();
/*      */ 
/* 3367 */       if ((this.restSupported) || (restartAt > 0L)) {
/* 3368 */         boolean done = false;
/*      */         try {
/* 3370 */           this.communication.sendFTPCommand("REST " + restartAt);
/* 3371 */           r = this.communication.readFTPReply();
/* 3372 */           touchAutoNoopTimer();
/* 3373 */           if ((r.getCode() != 350) && (((r.getCode() != 501) && (r.getCode() != 502)) || (restartAt > 0L))) {
/* 3374 */             throw new FTPException(r);
/*      */           }
/* 3376 */           done = true;
/*      */         } finally {
/* 3378 */           if (!done) {
/* 3379 */             provider.dispose();
/*      */           }
/*      */         }
/*      */       }
/*      */ 
/* 3384 */       boolean wasAborted = false;
/*      */ 
/* 3386 */       this.communication.sendFTPCommand("RETR " + fileName);
/*      */       try {
/*      */         Socket dtConnection;
/*      */         try {
/* 3390 */           dtConnection = provider.openDataTransferConnection();
/*      */         } finally {
/* 3392 */           provider.dispose();
/*      */         }
/*      */ 
/* 3395 */         synchronized (this.abortLock){
/* 3396 */           this.ongoingDataTransfer = true;
/* 3397 */           this.aborted = false;
/* 3398 */           this.consumeAborCommandReply = false;
/*      */         }
/*      */ 
/*      */         try
/*      */         {
/* 3403 */           this.dataTransferInputStream = dtConnection.getInputStream();
/*      */ 
/* 3405 */           if (this.modezEnabled) {
/* 3406 */             this.dataTransferInputStream = new InflaterInputStream(this.dataTransferInputStream);
/*      */           }
/*      */ 
/* 3409 */           if (listener != null) {
/* 3410 */             listener.started();
/*      */           }
/*      */ 
/* 3413 */           if (tp == 1) {
/* 3414 */             Object reader = new InputStreamReader(this.dataTransferInputStream, pickCharset());
/*      */ 
/* 3416 */             Object writer = new OutputStreamWriter(outputStream);
/* 3417 */             char[] buffer = new char[30000];
/*      */             int l;
/* 3419 */             while ((l = ((Reader)reader).read(buffer, 0, buffer.length)) != -1) {
/* 3420 */               ((Writer)writer).write(buffer, 0, l);
/* 3421 */               ((Writer)writer).flush();
/* 3422 */               if (listener != null)
/* 3423 */                 listener.transferred(l);
/*      */             }
/*      */           }
/* 3426 */           else if (tp == 2) {
/* 3427 */             byte[] buffer = new byte[30000];//65536
/*      */             int l;
/* 3430 */             while ((l = this.dataTransferInputStream.read(buffer, 0, buffer.length)) != -1) {
/* 3431 */               outputStream.write(buffer, 0, l);
/* 3432 */               if (listener != null)
/* 3433 */                 listener.transferred(l);
/*      */             }
/*      */           }
/*      */         }
/*      */         catch (IOException e) {
/* 3438 */           synchronized (this.abortLock) {
/* 3439 */             if (this.aborted) {
/* 3440 */               if (listener != null) {
/* 3441 */                 listener.aborted();
/*      */               }
/* 3443 */               throw new FTPAbortedException();
/*      */             }
/* 3445 */             if (listener != null) {
/* 3446 */               listener.failed();
/*      */             }
/* 3448 */             throw new FTPDataTransferException("I/O error in data transfer", e);
/*      */           }
/*      */ 
/*      */         }
/*      */         finally
/*      */         {
/* 3454 */           if (this.dataTransferInputStream != null)
/*      */             try {
/* 3456 */               this.dataTransferInputStream.close();
/*      */             }
/*      */             catch (Throwable t)
/*      */             {
/*      */             }
/*      */           try {
/* 3462 */             dtConnection.close();
/*      */           }
/*      */           catch (Throwable t)
/*      */           {
/*      */           }
/* 3467 */           this.dataTransferInputStream = null;
/*      */ 
/* 3469 */           synchronized (this.abortLock) {
/* 3470 */             wasAborted = this.aborted;
/* 3471 */             this.ongoingDataTransfer = false;
/* 3472 */             this.aborted = false;
/*      */           }
/*      */         }
/*      */       } finally {
/* 3476 */         r = this.communication.readFTPReply();
/* 3477 */         touchAutoNoopTimer();
/* 3478 */         if ((r.getCode() != 150) && (r.getCode() != 125)) {
/* 3479 */           throw new FTPException(r);
/*      */         }
/*      */ 
/* 3482 */         r = this.communication.readFTPReply();
/* 3483 */         if ((!wasAborted) && (r.getCode() != 226)) {
/* 3484 */           throw new FTPException(r);
/*      */         }
/*      */ 
/* 3487 */         if (this.consumeAborCommandReply) {
/* 3488 */           this.communication.readFTPReply();
/* 3489 */           this.consumeAborCommandReply = false;
/*      */         }
/*      */       }
/*      */ 
/* 3493 */       if (listener != null)
/* 3494 */         listener.completed();
/*      */     }
/*      */   }
/*      */ 
/*      */   private int detectType(String fileName)
/*      */     throws IOException, FTPIllegalReplyException, FTPException
/*      */   {
/* 3504 */     int start = fileName.lastIndexOf('.') + 1;
/* 3505 */     int stop = fileName.length();
/* 3506 */     if ((start > 0) && (start < stop - 1)) {
/* 3507 */       String ext = fileName.substring(start, stop);
/* 3508 */       ext = ext.toLowerCase();
/* 3509 */       if (this.textualExtensionRecognizer.isTextualExt(ext)) {
/* 3510 */         return 1;
/*      */       }
/* 3512 */       return 2;
/*      */     }
/*      */ 
/* 3515 */     return 2;
/*      */   }
/*      */ 
/*      */   private FTPDataTransferConnectionProvider openDataTransferChannel()
/*      */     throws IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException
/*      */   {
/* 3526 */     if ((this.modezSupported) && (this.compressionEnabled)) {
/* 3527 */       if (!this.modezEnabled)
/*      */       {
/* 3529 */         this.communication.sendFTPCommand("MODE Z");
/* 3530 */         FTPReply r = this.communication.readFTPReply();
/* 3531 */         touchAutoNoopTimer();
/* 3532 */         if (r.isSuccessCode()) {
/* 3533 */           this.modezEnabled = true;
/*      */         }
/*      */       }
/*      */     }
/* 3537 */     else if (this.modezEnabled)
/*      */     {
/* 3539 */       this.communication.sendFTPCommand("MODE S");
/* 3540 */       FTPReply r = this.communication.readFTPReply();
/* 3541 */       touchAutoNoopTimer();
/* 3542 */       if (r.isSuccessCode()) {
/* 3543 */         this.modezEnabled = false;
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3548 */     if (this.passive) {
/* 3549 */       return openPassiveDataTransferChannel();
/*      */     }
/* 3551 */     return openActiveDataTransferChannel();
/*      */   }
/*      */ 
/*      */   private FTPDataTransferConnectionProvider openActiveDataTransferChannel()
/*      */     throws IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException
/*      */   {
/* 3562 */     FTPDataTransferServer server = new FTPDataTransferServer()
/*      */     {
/*      */       public Socket openDataTransferConnection() throws FTPDataTransferException {
/* 3565 */         Socket socket = super.openDataTransferConnection();
/* 3566 */         if (FTPClient.this.dataChannelEncrypted){
/*      */           try {
/* 3568 */             socket = FTPClient.this.ssl(socket, socket.getInetAddress().getHostName(), socket.getPort());
/*      */           } catch (IOException e){
/*      */             try {
/* 3571 */               socket.close();
/*      */             } catch (Throwable t) {
/*      */             }
/* 3574 */             throw new FTPDataTransferException(e);
/*      */           }
/*      */         }
/* 3577 */         return socket;
/*      */       }
/*      */     };
/* 3580 */     int port = server.getPort();
/* 3581 */     int p1 = port >>> 8;
/* 3582 */     int p2 = port & 0xFF;
/* 3583 */     int[] addr = pickLocalAddress();
/*      */ 
/* 3585 */     this.communication.sendFTPCommand("PORT " + addr[0] + "," + addr[1] + "," + addr[2] + "," + addr[3] + "," + p1 + "," + p2);
/*      */ 
/* 3587 */     FTPReply r = this.communication.readFTPReply();
/* 3588 */     touchAutoNoopTimer();
/* 3589 */     if (!r.isSuccessCode())
/*      */     {
/* 3591 */       server.dispose();
/*      */       try
/*      */       {
/* 3594 */         Socket aux = server.openDataTransferConnection();
/* 3595 */         aux.close();
/*      */       }
/*      */       catch (Throwable t)
/*      */       {
/*      */       }
/* 3600 */       throw new FTPException(r);
/*      */     }
/* 3602 */     return server;
/*      */   }
/*      */ 
/*      */   private FTPDataTransferConnectionProvider openPassiveDataTransferChannel()
/*      */     throws IOException, FTPIllegalReplyException, FTPException, FTPDataTransferException
/*      */   {
/* 3612 */     this.communication.sendFTPCommand("PASV");
/*      */ 
/* 3614 */     FTPReply r = this.communication.readFTPReply();
/* 3615 */     touchAutoNoopTimer();
/* 3616 */     if (!r.isSuccessCode()) {
/* 3617 */       throw new FTPException(r);
/*      */     }
/*      */ 
/* 3620 */     String addressAndPort = null;
/* 3621 */     String[] messages = r.getMessages();
/* 3622 */     for (int i = 0; i < messages.length; i++) {
/* 3623 */       Matcher m = PASV_PATTERN.matcher(messages[i]);
/* 3624 */       if (m.find()) {
/* 3625 */         int start = m.start();
/* 3626 */         int end = m.end();
/* 3627 */         addressAndPort = messages[i].substring(start, end);
/* 3628 */         break;
/*      */       }
/*      */     }
/* 3631 */     if (addressAndPort == null)
/*      */     {
/* 3634 */       throw new FTPIllegalReplyException();
/*      */     }
/*      */ 
/* 3637 */     StringTokenizer st = new StringTokenizer(addressAndPort, ",");
/* 3638 */     int b1 = Integer.parseInt(st.nextToken());
/* 3639 */     int b2 = Integer.parseInt(st.nextToken());
/* 3640 */     int b3 = Integer.parseInt(st.nextToken());
/* 3641 */     int b4 = Integer.parseInt(st.nextToken());
/* 3642 */     int p1 = Integer.parseInt(st.nextToken());
/* 3643 */     int p2 = Integer.parseInt(st.nextToken());
/* 3644 */     final String pasvHost = b1 + "." + b2 + "." + b3 + "." + b4;
/* 3645 */     final int pasvPort = p1 << 8 | p2;
/* 3646 */     FTPDataTransferConnectionProvider provider = new FTPDataTransferConnectionProvider() {
         
       
             public Socket openDataTransferConnection() throws FTPDataTransferException { Socket dtConnection;
               try { String selectedHost = FTPClient.this.connector.getUseSuggestedAddressForDataConnections() ? pasvHost : FTPClient.this.host;
           dtConnection = FTPClient.this.connector.connectForDataTransferChannel(selectedHost, pasvPort);
            if (FTPClient.this.dataChannelEncrypted)
              dtConnection = FTPClient.this.ssl(dtConnection, selectedHost, pasvPort);
               } catch (IOException e)
               {
           throw new FTPDataTransferException("Cannot connect to the remote server", e);
               }
         return dtConnection;
             }
       
             public void dispose()
             {
             }
           };
     return provider;
         }
/*      */ 
/*      */   public void abortCurrentDataTransfer(boolean sendAborCommand)
/*      */     throws IOException, FTPIllegalReplyException
/*      */   {
/* 3688 */     synchronized (this.abortLock) {
/* 3689 */       if ((this.ongoingDataTransfer) && (!this.aborted)) {
/* 3690 */         if (sendAborCommand) {
/* 3691 */           this.communication.sendFTPCommand("ABOR");
/* 3692 */           touchAutoNoopTimer();
/* 3693 */           this.consumeAborCommandReply = true;
/*      */         }
/* 3695 */         if (this.dataTransferInputStream != null)
/*      */           try {
/* 3697 */             this.dataTransferInputStream.close();
/*      */           }
/*      */           catch (Throwable t)
/*      */           {
/*      */           }
/* 3702 */         if (this.dataTransferOutputStream != null)
/*      */           try {
/* 3704 */             this.dataTransferOutputStream.close();
/*      */           }
/*      */           catch (Throwable t)
/*      */           {
/*      */           }
/* 3709 */         this.aborted = true;
/*      */       }
/*      */     }
/*      */   }
/*      */ 
/*      */   private String pickCharset()
/*      */   {
/* 3722 */     if (this.charset != null)
/* 3723 */       return this.charset;
/* 3724 */     if (this.utf8Supported) {
/* 3725 */       return "UTF-8";
/*      */     }
/* 3727 */     return System.getProperty("file.encoding");
/*      */   }
/*      */ 
/*      */   private int[] pickLocalAddress()
/*      */     throws IOException
/*      */   {
/* 3741 */     int[] ret = pickForcedLocalAddress();
/*      */ 
/* 3743 */     if (ret == null) {
/* 3744 */       ret = pickAutoDetectedLocalAddress();
/*      */     }
/*      */ 
/* 3747 */     return ret;
/*      */   }
/*      */ 
/*      */   private int[] pickForcedLocalAddress()
/*      */   {
/* 3758 */     int[] ret = null;
/* 3759 */     String aux = System.getProperty("ftp4j.activeDataTransfer.hostAddress");
/* 3760 */     if (aux != null) {
/* 3761 */       boolean valid = false;
/* 3762 */       StringTokenizer st = new StringTokenizer(aux, ".");
/* 3763 */       if (st.countTokens() == 4) {
/* 3764 */         valid = true;
/* 3765 */         int[] arr = new int[4];
/* 3766 */         for (int i = 0; i < 4; i++) { 
/* 3767 */           String tk = st.nextToken();
/*      */           try {
/* 3769 */             arr[i] = Integer.parseInt(tk);
/*      */           } catch (NumberFormatException e) {
/* 3771 */             arr[i] = -1;
/*      */           }
/* 3773 */           if ((arr[i] < 0) || (arr[i] > 255)) {
/* 3774 */             valid = false;
/* 3775 */             break;
/*      */           }
/*      */         }
/* 3778 */         if (valid) {
/* 3779 */           ret = arr;
/*      */         }
/*      */       }
/* 3782 */       if (!valid)
/*      */       {
/* 3784 */         System.err.println("WARNING: invalid value \"" + aux + "\" for the " + "ftp4j.activeDataTransfer.hostAddress" + " system property. The value should " + "be in the x.x.x.x form.");
/*      */       }
/*      */ 
/*      */     }
/*      */ 
/* 3790 */     return ret;
/*      */   }
/*      */ 
/*      */   private int[] pickAutoDetectedLocalAddress()
/*      */     throws IOException
/*      */   {
/* 3803 */     InetAddress addressObj = InetAddress.getLocalHost();
/* 3804 */     byte[] addr = addressObj.getAddress();
/* 3805 */     int b1 = addr[0] & 0xFF;
/* 3806 */     int b2 = addr[1] & 0xFF;
/* 3807 */     int b3 = addr[2] & 0xFF;
/* 3808 */     int b4 = addr[3] & 0xFF;
/* 3809 */     int[] ret = { b1, b2, b3, b4 };
/* 3810 */     return ret;
/*      */   }
/*      */ 
/*      */   public String toString(){
/* 3814 */     synchronized (this.lock){
/* 3815 */       StringBuffer buffer = new StringBuffer();
/* 3816 */       buffer.append(getClass().getName());
/* 3817 */       buffer.append(" [connected=");
/* 3818 */       buffer.append(this.connected);
/* 3819 */       if (this.connected) {
/* 3820 */         buffer.append(", host=");
/* 3821 */         buffer.append(this.host);
/* 3822 */         buffer.append(", port=");
/* 3823 */         buffer.append(this.port);
/*      */       }
/* 3825 */       buffer.append(", connector=");
/* 3826 */       buffer.append(this.connector);
/* 3827 */       buffer.append(", security=");
/* 3828 */       switch (this.security) {
/*      */       case 0:
/* 3830 */         buffer.append("SECURITY_FTP");
/* 3831 */         break;
/*      */       case 1:
/* 3833 */         buffer.append("SECURITY_FTPS");
/* 3834 */         break;
/*      */       case 2:
/* 3836 */         buffer.append("SECURITY_FTPES");
/*      */       }
/*      */ 
/* 3839 */       buffer.append(", authenticated=");
/* 3840 */       buffer.append(this.authenticated);
/* 3841 */       if (this.authenticated) {
/* 3842 */         buffer.append(", username=");
/* 3843 */         buffer.append(this.username);
/* 3844 */         buffer.append(", password=");
/* 3845 */         StringBuffer buffer2 = new StringBuffer();
/* 3846 */         for (int i = 0; i < this.password.length(); i++) {
/* 3847 */           buffer2.append('*');
/*      */         }
/* 3849 */         buffer.append(buffer2);
/* 3850 */         buffer.append(", restSupported=");
/* 3851 */         buffer.append(this.restSupported);
/* 3852 */         buffer.append(", utf8supported=");
/* 3853 */         buffer.append(this.utf8Supported);
/* 3854 */         buffer.append(", mlsdSupported=");
/* 3855 */         buffer.append(this.mlsdSupported);
/* 3856 */         buffer.append(", mode=modezSupported");
/* 3857 */         buffer.append(this.modezSupported);
/* 3858 */         buffer.append(", mode=modezEnabled");
/* 3859 */         buffer.append(this.modezEnabled);
/*      */       }
/* 3861 */       buffer.append(", transfer mode=");
/* 3862 */       buffer.append(this.passive ? "passive" : "active");
/* 3863 */       buffer.append(", transfer type=");
/* 3864 */       switch (this.type) {
/*      */       case 0:
/* 3866 */         buffer.append("TYPE_AUTO");
/* 3867 */         break;
/*      */       case 2:
/* 3869 */         buffer.append("TYPE_BINARY");
/* 3870 */         break;
/*      */       case 1:
/* 3872 */         buffer.append("TYPE_TEXTUAL");
/*      */       }
/*      */ 
/* 3875 */       buffer.append(", textualExtensionRecognizer=");
/* 3876 */       buffer.append(this.textualExtensionRecognizer);
/* 3877 */       FTPListParser[] listParsers = getListParsers();
/* 3878 */       if (listParsers.length > 0) {
/* 3879 */         buffer.append(", listParsers=");
/* 3880 */         for (int i = 0; i < listParsers.length; i++) {
/* 3881 */           if (i > 0) {
/* 3882 */             buffer.append(", ");
/*      */           }
/* 3884 */           buffer.append(listParsers[i]);
/*      */         }
/*      */       }
/* 3887 */       FTPCommunicationListener[] communicationListeners = getCommunicationListeners();
/* 3888 */       if (communicationListeners.length > 0) {
/* 3889 */         buffer.append(", communicationListeners=");
/* 3890 */         for (int i = 0; i < communicationListeners.length; i++) {
/* 3891 */           if (i > 0) {
/* 3892 */             buffer.append(", ");
/*      */           }
/* 3894 */           buffer.append(communicationListeners[i]);
/*      */         }
/*      */       }
/* 3897 */       buffer.append(", autoNoopTimeout=");
/* 3898 */       buffer.append(this.autoNoopTimeout);
/* 3899 */       buffer.append("]");
/* 3900 */       return buffer.toString();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void startAutoNoopTimer()
/*      */   {
/* 3908 */     if (this.autoNoopTimeout > 0L) {
/* 3909 */       this.autoNoopTimer = new AutoNoopTimer();
/* 3910 */       this.autoNoopTimer.start();
/*      */     }
/*      */   }
/*      */ 
/*      */   private void stopAutoNoopTimer()
/*      */   {
/* 3920 */     if (this.autoNoopTimer != null) {
/* 3921 */       this.autoNoopTimer.interrupt();
/* 3922 */       this.autoNoopTimer = null;
/*      */     }
/*      */   }
/*      */ 
/*      */   private void touchAutoNoopTimer()
/*      */   {
/* 3930 */     if (this.autoNoopTimer != null)
/* 3931 */       this.nextAutoNoopTime = (System.currentTimeMillis() + this.autoNoopTimeout);
/*      */   }
/*      */ 
/*      */   private class AutoNoopTimer extends Thread
/*      */   {
/*      */     private AutoNoopTimer()
/*      */     {
/*      */     }
/*      */ 
/*      */     public void run() {
/* 3941 */       synchronized (FTPClient.this.lock){
/* 3942 */         if ((FTPClient.this.nextAutoNoopTime <= 0L) && (FTPClient.this.autoNoopTimeout > 0L)) {
/* 3943 */           FTPClient.this.nextAutoNoopTime = (System.currentTimeMillis() + FTPClient.this.autoNoopTimeout);
/*      */         }
/* 3945 */         while ((!Thread.interrupted()) && (FTPClient.this.autoNoopTimeout > 0L))
/*      */         {
/* 3947 */           long delay = FTPClient.this.nextAutoNoopTime - System.currentTimeMillis();
/* 3948 */           if (delay > 0L){
/*      */             try {
/* 3950 */               FTPClient.this.lock.wait(delay);
/*      */             } catch (InterruptedException e){
/* 3952 */               break;
/*      */             }
/*      */           }
/*      */
			        //  Log.i("test","当前时间："+System.currentTimeMillis()+"nextAutoNoopTime:"+nextAutoNoopTime);
/* 3956 */            if (System.currentTimeMillis() >= FTPClient.this.nextAutoNoopTime){

				     try
/*      */             {
/* 3959 */               FTPClient.this.noop();
/*      */             }
/*      */             catch (Throwable t)
/*      */             {
					 //  Log.i("test","t====");
					  //   EventBus.getDefault().post(new FtpFileEvent(false));
/*      */             }

			        }
/*      */
/*      */         }
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */   }
/*      */
public void setConnected(boolean b) {
	synchronized (this.lock) {
		/*  408 */       this.connected = b;
		/*      */     }
	
} }

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.FTPClient
 * JD-Core Version:    0.6.2
 */