/*     */ package com.shareshow.airpc.ftpclient;
/*     */ 
/*     */

import java.io.IOException;
import java.io.InputStream;

/*     */

/*     */
/*     */ class Base64InputStream extends InputStream
/*     */ {
/*     */   private InputStream inputStream;
/*     */   private int[] buffer;
/*  53 */   private int bufferCounter = 0;
/*     */ 
/*  58 */   private boolean eof = false;
/*     */ 
/*     */   public Base64InputStream(InputStream inputStream)
/*     */   {
/*  69 */     this.inputStream = inputStream;
/*     */   }
/*     */ 
/*     */   public int read() throws IOException {
/*  73 */     if ((this.buffer == null) || (this.bufferCounter == this.buffer.length)) {
/*  74 */       if (this.eof) {
/*  75 */         return -1;
/*     */       }
/*  77 */       acquire();
/*  78 */       if (this.buffer.length == 0) {
/*  79 */         this.buffer = null;
/*  80 */         return -1;
/*     */       }
/*  82 */       this.bufferCounter = 0;
/*     */     }
/*  84 */     return this.buffer[(this.bufferCounter++)];
/*     */   }
/*     */ 
/*     */   private void acquire()
/*     */     throws IOException
/*     */   {
/*  92 */     char[] four = new char[4];
/*  93 */     int i = 0;
/*     */     do {
/*  95 */       int b = this.inputStream.read();
/*  96 */       if (b == -1) {
/*  97 */         if (i != 0) {
/*  98 */           throw new IOException("Bad base64 stream");
/*     */         }
/* 100 */         this.buffer = new int[0];
/* 101 */         this.eof = true;
/* 102 */         return;
/*     */       }
/*     */ 
/* 105 */       char c = (char)b;
/* 106 */       if ((Base64.chars.indexOf(c) != -1) || (c == Base64.pad))
/* 107 */         four[(i++)] = c;
/* 108 */       else if ((c != '\r') && (c != '\n'))
/* 109 */         throw new IOException("Bad base64 stream");
/*     */     }
/* 111 */     while (i < 4);
/* 112 */     boolean padded = false;
/* 113 */     for (i = 0; i < 4; i++)
/* 114 */       if (four[i] != Base64.pad) {
/* 115 */         if (padded) {
/* 116 */           throw new IOException("Bad base64 stream");
/*     */         }
/*     */       }
/* 119 */       else if (!padded)
/* 120 */         padded = true;
/*     */     int l;
/* 125 */     if (four[3] == Base64.pad) {
/* 126 */       if (this.inputStream.read() != -1) {
/* 127 */         throw new IOException("Bad base64 stream");
/*     */       }
/* 129 */       this.eof = true;
/* 130 */       if (four[2] == Base64.pad)
/* 131 */         l = 1;
/*     */       else
/* 133 */         l = 2;
/*     */     }
/*     */     else {
/* 136 */       l = 3;
/*     */     }
/* 138 */     int aux = 0;
/* 139 */     for (i = 0; i < 4; i++) {
/* 140 */       if (four[i] != Base64.pad) {
/* 141 */         aux |= Base64.chars.indexOf(four[i]) << 6 * (3 - i);
/*     */       }
/*     */     }
/* 144 */     this.buffer = new int[l];
/* 145 */     for (i = 0; i < l; i++)
/* 146 */       this.buffer[i] = (aux >>> 8 * (2 - i) & 0xFF);
/*     */   }
/*     */ 
/*     */   public void close() throws IOException
/*     */   {
/* 151 */     this.inputStream.close();
/*     */   }
/*     */ }

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.connectors.Base64InputStream
 * JD-Core Version:    0.6.2
 */