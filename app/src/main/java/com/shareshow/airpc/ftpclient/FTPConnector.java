/*     */ package com.shareshow.airpc.ftpclient;
/*     */ 
/*     */

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/*     */
/*     */

/*     */
/*     */ public abstract class FTPConnector
/*     */ {
/*  38 */   protected int connectionTimeout = 10;
/*     */ 
/*  45 */   protected int readTimeout = 10;
/*     */ 
/*  52 */   protected int closeTimeout = 10;
/*     */   private boolean useSuggestedAddressForDataConnections;
/*     */   private Socket connectingCommunicationChannelSocket;
/*     */ 
/*     */   protected FTPConnector(boolean useSuggestedAddressForDataConnectionsDefValue)
/*     */   {
/*  80 */     String sysprop = System.getProperty("ftp4j.passiveDataTransfer.useSuggestedAddress");
/*  81 */     if (("true".equalsIgnoreCase(sysprop)) || ("yes".equalsIgnoreCase(sysprop)) || ("1".equals(sysprop)))
/*  82 */       this.useSuggestedAddressForDataConnections = true;
/*  83 */     else if (("false".equalsIgnoreCase(sysprop)) || ("no".equalsIgnoreCase(sysprop)) || ("0".equals(sysprop)))
/*  84 */       this.useSuggestedAddressForDataConnections = false;
/*     */     else
/*  86 */       this.useSuggestedAddressForDataConnections = useSuggestedAddressForDataConnectionsDefValue;
/*     */   }
/*     */ 
/*     */   protected FTPConnector()
/*     */   {
/*  97 */     this(false);
/*     */   }
/*     */ 
/*     */   public void setConnectionTimeout(int connectionTimeout)
/*     */   {
/* 108 */     this.connectionTimeout = connectionTimeout;
/*     */   }
/*     */ 
/*     */   public void setReadTimeout(int readTimeout)
/*     */   {
/* 119 */     this.readTimeout = readTimeout;
/*     */   }
/*     */ 
/*     */   public void setCloseTimeout(int closeTimeout)
/*     */   {
/* 130 */     this.closeTimeout = closeTimeout;
/*     */   }
/*     */ 
/*     */   public void setUseSuggestedAddressForDataConnections(boolean value)
/*     */   {
/* 167 */     this.useSuggestedAddressForDataConnections = value;
/*     */   }
/*     */ 
/*     */   boolean getUseSuggestedAddressForDataConnections()
/*     */   {
/* 178 */     return this.useSuggestedAddressForDataConnections;
/*     */   }
/*     */ 
/*     */   protected Socket tcpConnectForCommunicationChannel(String host, int port)
/*     */     throws IOException
/*     */   {
/*     */     try
/*     */     {
/* 204 */       this.connectingCommunicationChannelSocket = new Socket();
/* 205 */       this.connectingCommunicationChannelSocket.setKeepAlive(true);
/* 206 */       this.connectingCommunicationChannelSocket.setSoTimeout(this.readTimeout * 1000);
/* 207 */       this.connectingCommunicationChannelSocket.setSoLinger(true, this.closeTimeout);
/* 208 */       this.connectingCommunicationChannelSocket.connect(new InetSocketAddress(host, port), this.connectionTimeout * 1000);
/* 209 */       return this.connectingCommunicationChannelSocket;
/*     */     } finally {
/* 211 */       this.connectingCommunicationChannelSocket = null;
/*     */     }
/*     */   }
/*     */ 
/*     */   protected Socket tcpConnectForDataTransferChannel(String host, int port)
/*     */     throws IOException
/*     */   {
/* 236 */     Socket socket = new Socket();
/* 237 */     socket.setSoTimeout(this.readTimeout * 1000);
/* 238 */     socket.setSoLinger(true, this.closeTimeout);
/* 239 */     socket.setReceiveBufferSize(524288);
/* 240 */     socket.setSendBufferSize(524288);
/* 241 */     socket.connect(new InetSocketAddress(host, port), this.connectionTimeout * 1000);
/* 242 */     return socket;
/*     */   }
/*     */ 
/*     */   public void abortConnectForCommunicationChannel()
/*     */   {
/* 251 */     if (this.connectingCommunicationChannelSocket != null)
/*     */       try {
/* 253 */         this.connectingCommunicationChannelSocket.close();
/*     */       }
/*     */       catch (Throwable t)
/*     */       {
/*     */       }
/*     */   }
/*     */ 
/*     */   public abstract Socket connectForCommunicationChannel(String paramString, int paramInt)
/*     */     throws IOException;
/*     */ 
/*     */   public abstract Socket connectForDataTransferChannel(String paramString, int paramInt)
/*     */     throws IOException;
/*     */ }

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.FTPConnector
 * JD-Core Version:    0.6.2
 */