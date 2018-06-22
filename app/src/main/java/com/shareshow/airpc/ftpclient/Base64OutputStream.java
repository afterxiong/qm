/*     */ package com.shareshow.airpc.ftpclient;
/*     */ 
/*     */

import java.io.IOException;
import java.io.OutputStream;

/*     */

/*     */
/*     */ class Base64OutputStream extends OutputStream
/*     */ {
/*  41 */   private OutputStream outputStream = null;
/*     */ 
/*  46 */   private int buffer = 0;
/*     */ 
/*  51 */   private int bytecounter = 0;
/*     */ 
/*  56 */   private int linecounter = 0;
/*     */ 
/*  61 */   private int linelength = 0;
/*     */ 
/*     */   public Base64OutputStream(OutputStream outputStream)
/*     */   {
/*  78 */     this(outputStream, 76);
/*     */   }
/*     */ 
/*     */   public Base64OutputStream(OutputStream outputStream, int wrapAt)
/*     */   {
/* 101 */     this.outputStream = outputStream;
/* 102 */     this.linelength = wrapAt;
/*     */   }
/*     */ 
/*     */   public void write(int b) throws IOException {
/* 106 */     int value = (b & 0xFF) << 16 - this.bytecounter * 8;
/* 107 */     this.buffer |= value;
/* 108 */     this.bytecounter += 1;
/* 109 */     if (this.bytecounter == 3)
/* 110 */       commit();
/*     */   }
/*     */ 
/*     */   public void close() throws IOException
/*     */   {
/* 115 */     commit();
/* 116 */     this.outputStream.close();
/*     */   }
/*     */ 
/*     */   protected void commit()
/*     */     throws IOException
/*     */   {
/* 125 */     if (this.bytecounter > 0) {
/* 126 */       if ((this.linelength > 0) && (this.linecounter == this.linelength)) {
/* 127 */         this.outputStream.write("\r\n".getBytes());
/* 128 */         this.linecounter = 0;
/*     */       }
/* 130 */       char b1 = Base64.chars.charAt(this.buffer << 8 >>> 26);
/* 131 */       char b2 = Base64.chars.charAt(this.buffer << 14 >>> 26);
/* 132 */       char b3 = this.bytecounter < 2 ? Base64.pad : Base64.chars.charAt(this.buffer << 20 >>> 26);
/* 133 */       char b4 = this.bytecounter < 3 ? Base64.pad : Base64.chars.charAt(this.buffer << 26 >>> 26);
/* 134 */       this.outputStream.write(b1);
/* 135 */       this.outputStream.write(b2);
/* 136 */       this.outputStream.write(b3);
/* 137 */       this.outputStream.write(b4);
/* 138 */       this.linecounter += 4;
/* 139 */       this.bytecounter = 0;
/* 140 */       this.buffer = 0;
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.connectors.Base64OutputStream
 * JD-Core Version:    0.6.2
 */