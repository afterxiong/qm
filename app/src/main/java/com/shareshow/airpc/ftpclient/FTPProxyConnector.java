/*     */ package com.shareshow.airpc.ftpclient;
/*     */ 
/*     */

import java.io.IOException;
import java.net.Socket;

/*     */

/*     */
/*     */ public class FTPProxyConnector extends FTPConnector
/*     */ {
/*  44 */   public static int STYLE_SITE_COMMAND = 0;
/*     */ 
/*  50 */   public static int STYLE_OPEN_COMMAND = 1;
/*     */   private String proxyHost;
/*     */   private int proxyPort;
/*     */   private String proxyUser;
/*     */   private String proxyPass;
/*  75 */   public int style = STYLE_SITE_COMMAND;
/*     */ 
/*     */   public FTPProxyConnector(String proxyHost, int proxyPort, String proxyUser, String proxyPass)
/*     */   {
/*  93 */     super(true);
/*  94 */     this.proxyHost = proxyHost;
/*  95 */     this.proxyPort = proxyPort;
/*  96 */     this.proxyUser = proxyUser;
/*  97 */     this.proxyPass = proxyPass;
/*     */   }
/*     */ 
/*     */   public FTPProxyConnector(String proxyHost, int proxyPort)
/*     */   {
/* 111 */     this(proxyHost, proxyPort, "anonymous", "ftp4j");
/*     */   }
/*     */ 
/*     */   public void setStyle(int style)
/*     */   {
/* 131 */     if ((style != STYLE_OPEN_COMMAND) && (style != STYLE_SITE_COMMAND)) {
/* 132 */       throw new IllegalArgumentException("Invalid style");
/*     */     }
/* 134 */     this.style = style;
/*     */   }
/*     */ 
/*     */   public Socket connectForCommunicationChannel(String host, int port) throws IOException
/*     */   {
/* 139 */     Socket socket = tcpConnectForCommunicationChannel(this.proxyHost, this.proxyPort);
/* 140 */     FTPCommunicationChannel communication = new FTPCommunicationChannel(socket, "ASCII");
/*     */     FTPReply r;
/*     */     try {
/* 145 */       r = communication.readFTPReply();
/*     */     } catch (FTPIllegalReplyException e) {
/* 147 */       throw new IOException("Invalid proxy response");
/*     */     }
/*     */ 
/* 150 */     if (r.getCode() != 220)
/*     */     {
/* 152 */       throw new IOException("Invalid proxy response");
/*     */     }
/* 154 */     if (this.style == STYLE_SITE_COMMAND)
/*     */     {
/* 158 */       communication.sendFTPCommand("USER " + this.proxyUser);
/*     */       try {
/* 160 */         r = communication.readFTPReply();
/*     */       } catch (FTPIllegalReplyException e) {
/* 162 */         throw new IOException("Invalid proxy response");
/*     */       }
/*     */       boolean passwordRequired;
/* 164 */       switch (r.getCode())
/*     */       {
/*     */       case 230:
/* 167 */         passwordRequired = false;
/* 168 */         break;
/*     */       case 331:
/* 171 */         passwordRequired = true;
/* 172 */         break;
/*     */       default:
/* 175 */         throw new IOException("Proxy authentication failed");
/*     */       }
/*     */ 
/* 178 */       if (passwordRequired)
/*     */       {
/* 180 */         communication.sendFTPCommand("PASS " + this.proxyPass);
/*     */         try {
/* 182 */           r = communication.readFTPReply();
/*     */         } catch (FTPIllegalReplyException e) {
/* 184 */           throw new IOException("Invalid proxy response");
/*     */         }
/* 186 */         if (r.getCode() != 230)
/*     */         {
/* 188 */           throw new IOException("Proxy authentication failed");
/*     */         }
/*     */       }
/* 191 */       communication.sendFTPCommand("SITE " + host + ":" + port);
/* 192 */     } else if (this.style == STYLE_OPEN_COMMAND) {
/* 193 */       communication.sendFTPCommand("OPEN " + host + ":" + port);
/*     */     }
/* 195 */     return socket;
/*     */   }
/*     */ 
/*     */   public Socket connectForDataTransferChannel(String host, int port) throws IOException
/*     */   {
/* 200 */     return tcpConnectForDataTransferChannel(host, port);
/*     */   }
/*     */ }

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.connectors.FTPProxyConnector
 * JD-Core Version:    0.6.2
 */