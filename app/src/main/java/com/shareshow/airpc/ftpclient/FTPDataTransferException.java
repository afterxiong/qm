/*    */ package com.shareshow.airpc.ftpclient;
/*    */ 
/*    */ public class FTPDataTransferException extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */ 
/*    */   public FTPDataTransferException()
/*    */   {
/*    */   }
/*    */ 
/*    */   public FTPDataTransferException(String message, Throwable cause)
/*    */   {
/* 35 */     super(message, cause);
/*    */   }
/*    */ 
/*    */   public FTPDataTransferException(String message) {
/* 39 */     super(message);
/*    */   }
/*    */ 
/*    */   public FTPDataTransferException(Throwable cause) {
/* 43 */     super(cause);
/*    */   }
/*    */ }

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.FTPDataTransferException
 * JD-Core Version:    0.6.2
 */