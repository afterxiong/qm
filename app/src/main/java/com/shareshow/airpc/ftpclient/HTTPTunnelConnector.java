/*     */ package com.shareshow.airpc.ftpclient;
/*     */ 
/*     */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */

/*     */
/*     */ public class HTTPTunnelConnector extends FTPConnector
/*     */ {
/*     */   private String proxyHost;
/*     */   private int proxyPort;
/*     */   private String proxyUser;
/*     */   private String proxyPass;
/*     */ 
/*     */   public HTTPTunnelConnector(String proxyHost, int proxyPort, String proxyUser, String proxyPass)
/*     */   {
/*  76 */     this.proxyHost = proxyHost;
/*  77 */     this.proxyPort = proxyPort;
/*  78 */     this.proxyUser = proxyUser;
/*  79 */     this.proxyPass = proxyPass;
/*     */   }
/*     */ 
/*     */   public HTTPTunnelConnector(String proxyHost, int proxyPort)
/*     */   {
/*  91 */     this(proxyHost, proxyPort, null, null);
/*     */   }
/*     */ 
/*     */   private Socket httpConnect(String host, int port, boolean forDataTransfer) throws IOException
/*     */   {
/*  96 */     byte[] CRLF = "\r\n".getBytes("UTF-8");
/*     */ 
/*  98 */     String connect = "CONNECT " + host + ":" + port + " HTTP/1.1";
/*  99 */     String hostHeader = "Host: " + host + ":" + port;
/*     */ 
/* 101 */     boolean connected = false;
/*     */ 
/* 103 */     Socket socket = null;
/* 104 */     InputStream in = null;
/* 105 */     OutputStream out = null;
/*     */     try
/*     */     {
/* 108 */       if (forDataTransfer)
/* 109 */         socket = tcpConnectForDataTransferChannel(this.proxyHost, this.proxyPort);
/*     */       else {
/* 111 */         socket = tcpConnectForCommunicationChannel(this.proxyHost, this.proxyPort);
/*     */       }
/* 113 */       in = socket.getInputStream();
/* 114 */       out = socket.getOutputStream();
/*     */ 
/* 116 */       out.write(connect.getBytes("UTF-8"));
/* 117 */       out.write(CRLF);
/* 118 */       out.write(hostHeader.getBytes("UTF-8"));
/* 119 */       out.write(CRLF);
/*     */ 
/* 121 */       if ((this.proxyUser != null) && (this.proxyPass != null)) {
/* 122 */         String header = "Proxy-Authorization: Basic " + Base64.encode(new StringBuffer().append(this.proxyUser).append(":").append(this.proxyPass).toString());
/*     */ 
/* 124 */         out.write(header.getBytes("UTF-8"));
/* 125 */         out.write(CRLF);
/*     */       }
/* 127 */       out.write(CRLF);
/* 128 */       out.flush();
/*     */ 
/* 130 */       ArrayList responseLines = new ArrayList();
/* 131 */       BufferedReader reader = new BufferedReader(new InputStreamReader(in));
/*     */ 
/* 133 */       for (String line = reader.readLine();
/* 134 */         (line != null) && (line.length() > 0); line = reader.readLine()) {
/* 135 */         responseLines.add(line);
/*     */       }
/*     */ 
/* 138 */       int size = responseLines.size();
/* 139 */       if (size < 1) {
/* 140 */         throw new IOException("HTTPTunnelConnector: invalid proxy response");
/*     */       }
/*     */ 
/* 143 */       String code = null;
/* 144 */       String response = (String)responseLines.get(0);
/* 145 */       if ((response.startsWith("HTTP/")) && (response.length() >= 12))
/* 146 */         code = response.substring(9, 12);
/*     */       else {
/* 148 */         throw new IOException("HTTPTunnelConnector: invalid proxy response");
/*     */       }
/*     */ 
/* 151 */       if (!"200".equals(code)) {
/* 152 */         StringBuffer msg = new StringBuffer();
/* 153 */         msg.append("HTTPTunnelConnector: connection failed\r\n");
/* 154 */         msg.append("Response received from the proxy:\r\n");
/* 155 */         for (int i = 0; i < size; i++) {
/* 156 */           String line = (String)responseLines.get(i);
/* 157 */           msg.append(line);
/* 158 */           msg.append("\r\n");
/*     */         }
/* 160 */         throw new IOException(msg.toString());
/*     */       }
/* 162 */       connected = true;
/*     */     } catch (IOException e) {
/* 164 */       throw e;
/*     */     } finally {
/* 166 */       if (!connected) {
/* 167 */         if (out != null)
/*     */           try {
/* 169 */             out.close();
/*     */           }
/*     */           catch (Throwable t)
/*     */           {
/*     */           }
/* 174 */         if (in != null)
/*     */           try {
/* 176 */             in.close();
/*     */           }
/*     */           catch (Throwable t)
/*     */           {
/*     */           }
/* 181 */         if (socket != null)
/*     */           try {
/* 183 */             socket.close();
/*     */           }
/*     */           catch (Throwable t)
/*     */           {
/*     */           }
/*     */       }
/*     */     }
/* 190 */     return socket;
/*     */   }
/*     */ 
/*     */   public Socket connectForCommunicationChannel(String host, int port) throws IOException
/*     */   {
/* 195 */     return httpConnect(host, port, false);
/*     */   }
/*     */ 
/*     */   public Socket connectForDataTransferChannel(String host, int port) throws IOException
/*     */   {
/* 200 */     return httpConnect(host, port, true);
/*     */   }
/*     */ }

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.connectors.HTTPTunnelConnector
 * JD-Core Version:    0.6.2
 */