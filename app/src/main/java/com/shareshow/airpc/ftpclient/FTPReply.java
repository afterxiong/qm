/*    */ package com.shareshow.airpc.ftpclient;
/*    */ 
/*    */ public class FTPReply
/*    */ {
/* 31 */   private int code = 0;
/*    */   private String[] messages;
/*    */ 
/*    */   FTPReply(int code, String[] messages)
/*    */   {
/* 47 */     this.code = code;
/* 48 */     this.messages = messages;
/*    */   }
/*    */ 
/*    */   public int getCode()
/*    */   {
/* 57 */     return this.code;
/*    */   }
/*    */ 
/*    */   public boolean isSuccessCode()
/*    */   {
/* 68 */     int aux = this.code - 200;
/* 69 */     return (aux >= 0) && (aux < 100);
/*    */   }
/*    */ 
/*    */   public String[] getMessages()
/*    */   {
/* 78 */     return this.messages;
/*    */   }
/*    */ 
/*    */   public String toString() {
/* 82 */     StringBuffer buffer = new StringBuffer();
/* 83 */     buffer.append(getClass().getName());
/* 84 */     buffer.append(" [code=");
/* 85 */     buffer.append(this.code);
/* 86 */     buffer.append(", message=");
/* 87 */     for (int i = 0; i < this.messages.length; i++) {
/* 88 */       if (i > 0) {
/* 89 */         buffer.append(" ");
/*    */       }
/* 91 */       buffer.append(this.messages[i]);
/*    */     }
/* 93 */     buffer.append("]");
/* 94 */     return buffer.toString();
/*    */   }
/*    */ }

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.FTPReply
 * JD-Core Version:    0.6.2
 */