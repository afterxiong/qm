/*    */ package com.shareshow.airpc.ftpclient;
/*    */ 
/*    */

import java.io.IOException;
import java.net.Socket;

/*    */

/*    */
/*    */ public class DirectConnector extends FTPConnector
/*    */ {
/*    */   public Socket connectForCommunicationChannel(String host, int port)
/*    */     throws IOException
/*    */   {
/* 39 */     return tcpConnectForCommunicationChannel(host, port);
/*    */   }
/*    */ 
/*    */   public Socket connectForDataTransferChannel(String host, int port) throws IOException
/*    */   {
/* 44 */     return tcpConnectForDataTransferChannel(host, port);
/*    */   }
/*    */ }

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.connectors.DirectConnector
 * JD-Core Version:    0.6.2
 */