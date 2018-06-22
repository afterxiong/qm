/*    */ package com.shareshow.airpc.ftpclient;
/*    */ 
/*    */ public class FTPException extends Exception
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private int code;
/*    */   private String message;
/*    */ 
/*    */   public FTPException(int code)
/*    */   {
/* 36 */     this.code = code;
/*    */   }
/*    */ 
/*    */   public FTPException(int code, String message) {
/* 40 */     this.code = code;
/* 41 */     this.message = message;
/*    */   }
/*    */ 
/*    */   public FTPException(FTPReply reply) {
/* 45 */     StringBuffer message = new StringBuffer();
/* 46 */     String[] lines = reply.getMessages();
/* 47 */     for (int i = 0; i < lines.length; i++) {
/* 48 */       if (i > 0) {
/* 49 */         message.append(System.getProperty("line.separator"));
/*    */       }
/* 51 */       message.append(lines[i]);
/*    */     }
/* 53 */     this.code = reply.getCode();
/* 54 */     this.message = message.toString();
/*    */   }
/*    */ 
/*    */   public int getCode()
/*    */   {
/* 63 */     return this.code;
/*    */   }
/*    */ 
/*    */   public String getMessage()
/*    */   {
/* 72 */     return this.message;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 76 */     return getClass().getName() + " [code=" + this.code + ", message= " + this.message + "]";
/*    */   }
/*    */ }

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.FTPException
 * JD-Core Version:    0.6.2
 */