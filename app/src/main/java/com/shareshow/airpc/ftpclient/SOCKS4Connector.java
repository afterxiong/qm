/*     */ package com.shareshow.airpc.ftpclient;
/*     */ 
/*     */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/*     */
/*     */
/*     */
/*     */

/*     */
/*     */ public class SOCKS4Connector extends FTPConnector
/*     */ {
/*     */   private String socks4host;
/*     */   private int socks4port;
/*     */   private String socks4user;
/*     */ 
/*     */   public SOCKS4Connector(String socks4host, int socks4port, String socks4user)
/*     */   {
/*  65 */     this.socks4host = socks4host;
/*  66 */     this.socks4port = socks4port;
/*  67 */     this.socks4user = socks4user;
/*     */   }
/*     */ 
/*     */   public SOCKS4Connector(String socks4host, int socks4port)
/*     */   {
/*  79 */     this(socks4host, socks4port, null);
/*     */   }
/*     */ 
/*     */   private Socket socksConnect(String host, int port, boolean forDataTransfer) throws IOException {
/*  84 */     boolean socks4a = false;
/*     */     byte[] address;
/*     */     try {
/*  87 */       address = InetAddress.getByName(host).getAddress();
/*     */     }
/*     */     catch (Exception e) {
/*  90 */       socks4a = true;
/*  91 */       address = new byte[] { 0, 0, 0, 1 };
/*     */     }
/*     */ 
/*  94 */     boolean connected = false;
/*     */ 
/*  96 */     Socket socket = null;
/*  97 */     InputStream in = null;
/*  98 */     OutputStream out = null;
/*     */     try
/*     */     {
/* 101 */       if (forDataTransfer)
/* 102 */         socket = tcpConnectForDataTransferChannel(this.socks4host, this.socks4port);
/*     */       else {
/* 104 */         socket = tcpConnectForCommunicationChannel(this.socks4host, this.socks4port);
/*     */       }
/* 106 */       in = socket.getInputStream();
/* 107 */       out = socket.getOutputStream();
/*     */ 
/* 110 */       out.write(4);
/*     */ 
/* 112 */       out.write(1);
/*     */ 
/* 114 */       out.write(port >> 8);
/* 115 */       out.write(port);
/*     */ 
/* 117 */       out.write(address);
/*     */ 
/* 119 */       if (this.socks4user != null) {
/* 120 */         out.write(this.socks4user.getBytes("UTF-8"));
/*     */       }
/*     */ 
/* 123 */       out.write(0);
/*     */ 
/* 125 */       if (socks4a) {
/* 126 */         out.write(host.getBytes("UTF-8"));
/* 127 */         out.write(0);
/*     */       }
/*     */ 
/* 130 */       int aux = read(in);
/* 131 */       if (aux != 0) {
/* 132 */         throw new IOException("SOCKS4Connector: invalid proxy response");
/*     */       }
/* 134 */       aux = read(in);
/* 135 */       switch (aux) {
/*     */       case 90:
/* 137 */         in.skip(6L);
/* 138 */         connected = true;
/* 139 */         break;
/*     */       case 91:
/* 141 */         throw new IOException("SOCKS4Connector: connection refused/failed");
/*     */       case 92:
/* 144 */         throw new IOException("SOCKS4Connector: cannot validate the user");
/*     */       case 93:
/* 147 */         throw new IOException("SOCKS4Connector: invalid user");
/*     */       default:
/* 149 */         throw new IOException("SOCKS4Connector: invalid proxy response");
/*     */       }
/*     */     } catch (IOException e) {
/* 152 */       throw e;
/*     */     } finally {
/* 154 */       if (!connected) {
/* 155 */         if (out != null)
/*     */           try {
/* 157 */             out.close();
/*     */           }
/*     */           catch (Throwable t)
/*     */           {
/*     */           }
/* 162 */         if (in != null)
/*     */           try {
/* 164 */             in.close();
/*     */           }
/*     */           catch (Throwable t)
/*     */           {
/*     */           }
/* 169 */         if (socket != null)
/*     */           try {
/* 171 */             socket.close();
/*     */           }
/*     */           catch (Throwable t)
/*     */           {
/*     */           }
/*     */       }
/*     */     }
/* 178 */     return socket;
/*     */   }
/*     */ 
/*     */   private int read(InputStream in) throws IOException {
/* 182 */     int aux = in.read();
/* 183 */     if (aux < 0) {
/* 184 */       throw new IOException("SOCKS4Connector: connection closed by the proxy");
/*     */     }
/*     */ 
/* 187 */     return aux;
/*     */   }
/*     */ 
/*     */   public Socket connectForCommunicationChannel(String host, int port) throws IOException
/*     */   {
/* 192 */     return socksConnect(host, port, false);
/*     */   }
/*     */ 
/*     */   public Socket connectForDataTransferChannel(String host, int port) throws IOException
/*     */   {
/* 197 */     return socksConnect(host, port, true);
/*     */   }
/*     */ }

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.connectors.SOCKS4Connector
 * JD-Core Version:    0.6.2
 */