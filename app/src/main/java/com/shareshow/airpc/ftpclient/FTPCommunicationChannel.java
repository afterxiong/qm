/*     */ package com.shareshow.airpc.ftpclient;
/*     */ 
/*     */

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;

import javax.net.ssl.SSLSocketFactory;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */

/*     */
/*         socket的读写信令封装类

 */
/*     */ public class FTPCommunicationChannel
/*     */ {
/*  41 */   private ArrayList communicationListeners = new ArrayList();
/*     */ 
/*  46 */   private Socket connection = null;
/*     */ 
/*  52 */   private String charsetName = null;
/*     */ 
/*  57 */   private NVTASCIIReader reader = null;
/*     */ 
/*  62 */   private NVTASCIIWriter writer = null;
/*     */ 
/*     */   public FTPCommunicationChannel(Socket connection, String charsetName)
/*     */     throws IOException
/*     */   {
/*  77 */     this.connection = connection;
/*  78 */     this.charsetName = charsetName;
/*  79 */     InputStream inStream = connection.getInputStream();
/*  80 */     OutputStream outStream = connection.getOutputStream();
/*     */ 
/*  82 */     this.reader = new NVTASCIIReader(inStream, charsetName);
/*  83 */     this.writer = new NVTASCIIWriter(outStream, charsetName);
/*     */   }
/*     */ 
/*     */   public void addCommunicationListener(FTPCommunicationListener listener)
/*     */   {
/*  93 */     this.communicationListeners.add(listener);
/*     */   }
/*     */ 
/*     */   public void removeCommunicationListener(FTPCommunicationListener listener)
/*     */   {
/* 104 */     this.communicationListeners.remove(listener);
/*     */   }
/*     */ 
/*     */   public void close()
/*     */   {
/*     */     try
/*     */     {
/* 112 */       this.connection.close();
/*     */     }
/*     */     catch (Exception e)
/*     */     {
/*     */     }
/*     */   }
/*     */ 
/*     */   public FTPCommunicationListener[] getCommunicationListeners()
/*     */   {
/* 125 */     int size = this.communicationListeners.size();
/* 126 */     FTPCommunicationListener[] ret = new FTPCommunicationListener[size];
/* 127 */     for (int i = 0; i < size; i++) {
/* 128 */       ret[i] = ((FTPCommunicationListener)this.communicationListeners.get(i));
/*     */     }
/* 130 */     return ret;
/*     */   }
/*     */ 
/*     */   private String read()
/*     */     throws IOException
/*     */   {
/* 142 */     String line = this.reader.readLine();
/* 143 */     if (line == null) {
/* 144 */       throw new IOException("FTPConnection closed");
/*     */     }
/*     */ 
/* 148 */     for (Iterator iter = this.communicationListeners.iterator(); iter.hasNext(); ) {
/* 149 */       FTPCommunicationListener l = (FTPCommunicationListener)iter.next();
/* 150 */       l.received(line);
/*     */     }
/*     */ 
/* 153 */     return line;
/*     */   }
/*     */ 
/*     */   public void sendFTPCommand(String command)
/*     */     throws IOException
/*     */   {
/* 165 */     this.writer.writeLine(command);
/* 166 */     for (Iterator iter = this.communicationListeners.iterator(); iter.hasNext(); ){
/* 167 */       FTPCommunicationListener l = (FTPCommunicationListener)iter.next();
/* 168 */       l.sent(command);
/*     */     }
/*     */   }
/*     */ 
/*     */   public FTPReply readFTPReply()
/*     */     throws IOException, FTPIllegalReplyException
/*     */   {
/* 182 */     int code = 0;
/* 183 */     ArrayList messages = new ArrayList();
/*     */     while (true)
/*     */     {
/* 187 */       String statement = read();
/* 188 */       if (statement.trim().length() != 0) {
/* 189 */         if (statement.startsWith("\n")) {
/* 190 */           statement = statement.substring(1);
/*     */         }
/* 192 */         int l = statement.length();
/* 193 */         if ((code == 0) && (l < 3))
/* 194 */           throw new FTPIllegalReplyException();
/*     */         int aux;
/*     */         try
/*     */         {
/* 198 */           aux = Integer.parseInt(statement.substring(0, 3));
/*     */         } catch (Exception e) {
/* 200 */           if (code == 0){
/* 201 */             throw new FTPIllegalReplyException();
/*     */           }
/* 203 */           aux = 0;
/*     */         }
/*     */ 
/* 206 */         if ((code != 0) && (aux != 0) && (aux != code)){
/* 207 */           throw new FTPIllegalReplyException();
/*     */         }
/* 209 */         if (code == 0) {
/* 210 */           code = aux;
/*     */         }
/* 212 */         if (aux > 0) {
/* 213 */           if (l > 3) {
/* 214 */             char s = statement.charAt(3);
/* 215 */             String message = statement.substring(4, l);
/* 216 */             messages.add(message);
/* 217 */             if (s == ' ')
/*     */               break;
/* 219 */             if (s != '-')
/*     */             {
/* 222 */               throw new FTPIllegalReplyException();
/*     */             }
/*     */           } else { if (l == 3) {
/*     */               break;
/*     */             }
/* 227 */             messages.add(statement); }
/*     */         }
/*     */         else
/* 230 */           messages.add(statement);
/*     */       }
/*     */     }
/* 233 */     int size = messages.size();
/* 234 */     String[] m = new String[size];
/* 235 */     for (int i = 0; i < size; i++) {
/* 236 */       m[i] = ((String)messages.get(i));
/*     */     }
/* 238 */     return new FTPReply(code, m);
/*     */   }
/*     */ 
/*     */   public void changeCharset(String charsetName)
/*     */     throws IOException
/*     */   {
/* 251 */     this.charsetName = charsetName;
/* 252 */     this.reader.changeCharset(charsetName);
/* 253 */     this.writer.changeCharset(charsetName);
/*     */   }
/*     */ 
/*     */   public void ssl(SSLSocketFactory sslSocketFactory)
/*     */     throws IOException
/*     */   {
/* 266 */     String host = this.connection.getInetAddress().getHostName();
/* 267 */     int port = this.connection.getPort();
/* 268 */     this.connection = sslSocketFactory.createSocket(this.connection, host, port, true);
/* 269 */     InputStream inStream = this.connection.getInputStream();
/* 270 */     OutputStream outStream = this.connection.getOutputStream();
/* 271 */     this.reader = new NVTASCIIReader(inStream, this.charsetName);
/* 272 */     this.writer = new NVTASCIIWriter(outStream, this.charsetName);
/*     */   }
/*     */ }

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.FTPCommunicationChannel
 * JD-Core Version:    0.6.2
 */