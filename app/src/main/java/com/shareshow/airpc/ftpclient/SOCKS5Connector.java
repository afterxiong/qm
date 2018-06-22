/*     */ package com.shareshow.airpc.ftpclient;
/*     */ 
/*     */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/*     */
/*     */
/*     */

/*     */
/*     */ public class SOCKS5Connector extends FTPConnector
/*     */ {
/*     */   private String socks5host;
/*     */   private int socks5port;
/*     */   private String socks5user;
/*     */   private String socks5pass;
/*     */ 
/*     */   public SOCKS5Connector(String socks5host, int socks5port, String socks5user, String socks5pass)
/*     */   {
/*  73 */     this.socks5host = socks5host;
/*  74 */     this.socks5port = socks5port;
/*  75 */     this.socks5user = socks5user;
/*  76 */     this.socks5pass = socks5pass;
/*     */   }
/*     */ 
/*     */   public SOCKS5Connector(String socks5host, int socks5port)
/*     */   {
/*  88 */     this(socks5host, socks5port, null, null);
/*     */   }
/*     */ 
/*     */   private Socket socksConnect(String host, int port, boolean forDataTransfer) throws IOException
/*     */   {
/*  93 */     boolean authentication = (this.socks5user != null) && (this.socks5pass != null);
/*     */ 
/*  95 */     boolean connected = false;
/*     */ 
/*  97 */     Socket socket = null;
/*  98 */     InputStream in = null;
/*  99 */     OutputStream out = null;
/*     */     try
/*     */     {
/* 102 */       if (forDataTransfer)
/* 103 */         socket = tcpConnectForDataTransferChannel(this.socks5host, this.socks5port);
/*     */       else {
/* 105 */         socket = tcpConnectForCommunicationChannel(this.socks5host, this.socks5port);
/*     */       }
/* 107 */       in = socket.getInputStream();
/* 108 */       out = socket.getOutputStream();
/*     */ 
/* 111 */       out.write(5);
/*     */ 
/* 113 */       if (authentication)
/*     */       {
/* 115 */         out.write(1);
/* 116 */         out.write(2);
/*     */       }
/*     */       else {
/* 119 */         out.write(1);
/* 120 */         out.write(0);
/*     */       }
/*     */ 
/* 123 */       int aux = read(in);
/* 124 */       if (aux != 5) {
/* 125 */         throw new IOException("SOCKS5Connector: invalid proxy response");
/*     */       }
/* 127 */       aux = read(in);
/* 128 */       if (authentication) {
/* 129 */         if (aux != 2) {
/* 130 */           throw new IOException("SOCKS5Connector: proxy doesn't support username/password authentication method");
/*     */         }
/*     */ 
/* 135 */         byte[] user = this.socks5user.getBytes("UTF-8");
/* 136 */         byte[] pass = this.socks5pass.getBytes("UTF-8");
/* 137 */         int userLength = user.length;
/* 138 */         int passLength = pass.length;
/*     */ 
/* 140 */         if (userLength > 255) {
/* 141 */           throw new IOException("SOCKS5Connector: username too long");
/*     */         }
/* 143 */         if (passLength > 255) {
/* 144 */           throw new IOException("SOCKS5Connector: password too long");
/*     */         }
/*     */ 
/* 147 */         out.write(1);
/*     */ 
/* 149 */         out.write(userLength);
/* 150 */         out.write(user);
/*     */ 
/* 152 */         out.write(passLength);
/* 153 */         out.write(pass);
/*     */ 
/* 155 */         aux = read(in);
/* 156 */         if (aux != 1) {
/* 157 */           throw new IOException("SOCKS5Connector: invalid proxy response");
/*     */         }
/*     */ 
/* 160 */         aux = read(in);
/* 161 */         if (aux != 0) {
/* 162 */           throw new IOException("SOCKS5Connector: authentication failed");
/*     */         }
/*     */ 
/*     */       }
/* 166 */       else if (aux != 0) {
/* 167 */         throw new IOException("SOCKS5Connector: proxy requires authentication");
/*     */       }
/*     */ 
/* 173 */       out.write(5);
/*     */ 
/* 175 */       out.write(1);
/*     */ 
/* 177 */       out.write(0);
/*     */ 
/* 179 */       out.write(3);
/*     */ 
/* 181 */       byte[] domain = host.getBytes("UTF-8");
/* 182 */       if (domain.length > 255) {
/* 183 */         throw new IOException("SOCKS5Connector: domain name too long");
/*     */       }
/* 185 */       out.write(domain.length);
/* 186 */       out.write(domain);
/*     */ 
/* 188 */       out.write(port >> 8);
/* 189 */       out.write(port);
/*     */ 
/* 193 */       aux = read(in);
/* 194 */       if (aux != 5) {
/* 195 */         throw new IOException("SOCKS5Connector: invalid proxy response");
/*     */       }
/*     */ 
/* 198 */       aux = read(in);
/* 199 */       switch (aux)
/*     */       {
/*     */       case 0:
/* 202 */         break;
/*     */       case 1:
/* 204 */         throw new IOException("SOCKS5Connector: general failure");
/*     */       case 2:
/* 206 */         throw new IOException("SOCKS5Connector: connection not allowed by ruleset");
/*     */       case 3:
/* 209 */         throw new IOException("SOCKS5Connector: network unreachable");
/*     */       case 4:
/* 211 */         throw new IOException("SOCKS5Connector: host unreachable");
/*     */       case 5:
/* 213 */         throw new IOException("SOCKS5Connector: connection refused by destination host");
/*     */       case 6:
/* 216 */         throw new IOException("SOCKS5Connector: TTL expired");
/*     */       case 7:
/* 218 */         throw new IOException("SOCKS5Connector: command not supported / protocol error");
/*     */       case 8:
/* 221 */         throw new IOException("SOCKS5Connector: address type not supported");
/*     */       default:
/* 224 */         throw new IOException("SOCKS5Connector: invalid proxy response");
/*     */       }
/*     */ 
/* 227 */       in.skip(1L);
/*     */ 
/* 229 */       aux = read(in);
/* 230 */       if (aux == 1)
/*     */       {
/* 232 */         in.skip(4L);
/* 233 */       } else if (aux == 3)
/*     */       {
/* 235 */         aux = read(in);
/* 236 */         in.skip(aux);
/* 237 */       } else if (aux == 4)
/*     */       {
/* 239 */         in.skip(16L);
/*     */       } else {
/* 241 */         throw new IOException("SOCKS5Connector: invalid proxy response");
/*     */       }
/*     */ 
/* 244 */       in.skip(2L);
/*     */ 
/* 246 */       connected = true;
/*     */     } catch (IOException e) {
/* 248 */       throw e;
/*     */     } finally {
/* 250 */       if (!connected) {
/* 251 */         if (out != null)
/*     */           try {
/* 253 */             out.close();
/*     */           }
/*     */           catch (Throwable t)
/*     */           {
/*     */           }
/* 258 */         if (in != null)
/*     */           try {
/* 260 */             in.close();
/*     */           }
/*     */           catch (Throwable t)
/*     */           {
/*     */           }
/* 265 */         if (socket != null)
/*     */           try {
/* 267 */             socket.close();
/*     */           }
/*     */           catch (Throwable t)
/*     */           {
/*     */           }
/*     */       }
/*     */     }
/* 274 */     return socket;
/*     */   }
/*     */ 
/*     */   private int read(InputStream in) throws IOException {
/* 278 */     int aux = in.read();
/* 279 */     if (aux < 0) {
/* 280 */       throw new IOException("SOCKS5Connector: connection closed by the proxy");
/*     */     }
/*     */ 
/* 283 */     return aux;
/*     */   }
/*     */ 
/*     */   public Socket connectForCommunicationChannel(String host, int port) throws IOException
/*     */   {
/* 288 */     return socksConnect(host, port, false);
/*     */   }
/*     */ 
/*     */   public Socket connectForDataTransferChannel(String host, int port) throws IOException
/*     */   {
/* 293 */     return socksConnect(host, port, true);
/*     */   }
/*     */ }

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.connectors.SOCKS5Connector
 * JD-Core Version:    0.6.2
 */