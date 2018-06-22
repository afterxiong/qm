/*     */ package com.shareshow.airpc.ftpclient;
/*     */ 
/*     */

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.StringTokenizer;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */

/*     */
/*     */ class FTPDataTransferServer
/*     */   implements FTPDataTransferConnectionProvider, Runnable
/*     */ {
/*  40 */   private ServerSocket serverSocket = null;
/*     */   private Socket socket;
/*     */   private IOException exception;
/*     */   private Thread thread;
/*     */ 
/*     */   public FTPDataTransferServer()
/*     */     throws FTPDataTransferException
/*     */   {
/*  64 */     boolean useRange = false;
/*  65 */     String aux = System.getProperty("ftp4j.activeDataTransfer.portRange");
/*  66 */     int start = 0;
/*  67 */     int stop = 0;
/*  68 */     if (aux != null) {
/*  69 */       boolean valid = false;
/*  70 */       StringTokenizer st = new StringTokenizer(aux, "-");
/*  71 */       if (st.countTokens() == 2) { String s1 = st.nextToken();
/*  73 */         String s2 = st.nextToken();
/*     */         int v1;
/*     */         try { v1 = Integer.parseInt(s1);
/*     */         } catch (NumberFormatException e) {
/*  78 */           v1 = 0;
/*     */         }
/*     */         int v2;
/*     */         try {
/*  82 */           v2 = Integer.parseInt(s2);
/*     */         } catch (NumberFormatException e) {
/*  84 */           v2 = 0;
/*     */         }
/*  86 */         if ((v1 > 0) && (v2 > 0) && (v2 >= v1)) {
/*  87 */           start = v1;
/*  88 */           stop = v2;
/*  89 */           valid = true;
/*  90 */           useRange = true;
/*     */         }
/*     */       }
/*  93 */       if (!valid)
/*     */       {
/*  95 */         System.err.println("WARNING: invalid value \"" + aux + "\" for the " + "ftp4j.activeDataTransfer.portRange" + " system property. The value should " + "be in the start-stop form, with " + "start > 0, stop > 0 and start <= stop.");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 102 */     if (useRange) {
/* 103 */       ArrayList availables = new ArrayList();
/* 104 */       for (int i = start; i <= stop; i++) {
/* 105 */         availables.add(new Integer(i));
/*     */       }
/*     */ 
/* 108 */       boolean done = false;
/*     */       int size;
/* 109 */       while ((!done) && ((size = availables.size()) > 0)) {
/* 110 */         int rand = (int) Math.floor(Math.random() * size);
/* 111 */         int port = ((Integer)availables.remove(rand)).intValue();
/*     */         try
/*     */         {
/* 114 */           this.serverSocket = new ServerSocket();
/* 115 */           this.serverSocket.setReceiveBufferSize(524288);
/* 116 */           this.serverSocket.bind(new InetSocketAddress(port));
/* 117 */           done = true;
/*     */         }
/*     */         catch (IOException e) {
/*     */         }
/*     */       }
/* 122 */       if (!done) {
/* 123 */         throw new FTPDataTransferException("Cannot open the ServerSocket. No available port found in range " + aux);
/*     */       }
/*     */     }
/*     */     else
/*     */     {
/*     */       try
/*     */       {
/* 130 */         this.serverSocket = new ServerSocket();
/* 131 */         this.serverSocket.setReceiveBufferSize(524288);
/* 132 */         this.serverSocket.bind(new InetSocketAddress(0));
/*     */       } catch (IOException e) {
/* 134 */         throw new FTPDataTransferException("Cannot open the ServerSocket", e);
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/* 139 */     this.thread = new Thread(this);
/* 140 */     this.thread.start();
/*     */   }
/*     */ 
/*     */   public int getPort()
/*     */   {
/* 149 */     return this.serverSocket.getLocalPort();
/*     */   }
/*     */ 
/*     */   public void run(){
/* 153 */     int timeout = 30000;
/* 154 */     String aux = System.getProperty("ftp4j.activeDataTransfer.acceptTimeout");
/* 155 */     if (aux != null) { boolean valid = false;
/*     */       int value;
/*     */       try {
/* 159 */         value = Integer.parseInt(aux);
/*     */       } catch (NumberFormatException e) {
/* 161 */         value = -1;
/*     */       }
/* 163 */       if (value >= 0) {
/* 164 */         timeout = value;
/* 165 */         valid = true;
/*     */       }
/* 167 */       if (!valid)
/*     */       {
/* 169 */         System.err.println("WARNING: invalid value \"" + aux + "\" for the " + "ftp4j.activeDataTransfer.acceptTimeout" + " system property. The value should " + "be an integer greater or equal to 0.");
/*     */       }
/*     */ 
/*     */     }
/*     */ 
/*     */     try
/*     */     {
/* 177 */       this.serverSocket.setSoTimeout(timeout);
/*     */ 
/* 179 */       this.socket = this.serverSocket.accept();

/* 180 */       this.socket.setSendBufferSize(524288);
/*     */     } catch (IOException e) {
/* 182 */       this.exception = e;
/*     */     }
/*     */     finally {
/*     */       try {
/* 186 */         this.serverSocket.close();
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void dispose()
/*     */   {
/* 198 */     if (this.serverSocket != null)
/*     */       try {
/* 200 */         this.serverSocket.close();
/*     */       }
/*     */       catch (IOException e)
/*     */       {
/*     */       }
/*     */   }
/*     */ 
/*     */   public Socket openDataTransferConnection() throws FTPDataTransferException {
/* 208 */     if ((this.socket == null) && (this.exception == null))
/*     */       try {
/* 210 */         this.thread.join();
/*     */       }
/*     */       catch (Exception e)
/*     */       {
/*     */       }
/* 215 */     if (this.exception != null) {
/* 216 */       throw new FTPDataTransferException("Cannot receive the incoming connection", this.exception);
/*     */     }
/*     */ 
/* 219 */     if (this.socket == null) {
/* 220 */       throw new FTPDataTransferException("No socket available");
/*     */     }
/* 222 */     return this.socket;
/*     */   }
/*     */ }

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.FTPDataTransferServer
 * JD-Core Version:    0.6.2
 */