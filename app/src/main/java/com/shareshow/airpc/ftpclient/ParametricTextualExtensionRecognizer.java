/*     */ package com.shareshow.airpc.ftpclient;
/*     */ 
/*     */ import java.util.ArrayList;

/*     */
/*     */ public class ParametricTextualExtensionRecognizer
/*     */   implements FTPTextualExtensionRecognizer
/*     */ {
/*  39 */   private ArrayList exts = new ArrayList();
/*     */ 
/*     */   public ParametricTextualExtensionRecognizer()
/*     */   {
/*     */   }
/*     */ 
/*     */   public ParametricTextualExtensionRecognizer(String[] exts)
/*     */   {
/*  55 */     for (int i = 0; i < exts.length; i++)
/*  56 */       addExtension(exts[i]);
/*     */   }
/*     */ 
/*     */   public ParametricTextualExtensionRecognizer(ArrayList exts)
/*     */   {
/*  67 */     int size = exts.size();
/*  68 */     for (int i = 0; i < size; i++) {
/*  69 */       Object aux = exts.get(i);
/*  70 */       if ((aux instanceof String)) {
/*  71 */         String ext = (String)aux;
/*  72 */         addExtension(ext);
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   public void addExtension(String ext)
/*     */   {
/*  84 */     synchronized (this.exts) {
/*  85 */       ext = ext.toLowerCase();
/*  86 */       this.exts.add(ext);
/*     */     }
/*     */   }
/*     */ 
/*     */   public void removeExtension(String ext)
/*     */   {
/*  97 */     synchronized (this.exts) {
/*  98 */       ext = ext.toLowerCase();
/*  99 */       this.exts.remove(ext);
/*     */     }
/*     */   }
/*     */ 
/*     */   public String[] getExtensions()
/*     */   {
/* 110 */     synchronized (this.exts) {
/* 111 */       int size = this.exts.size();
/* 112 */       String[] ret = new String[size];
/* 113 */       for (int i = 0; i < size; i++) {
/* 114 */         ret[i] = ((String)this.exts.get(i));
/*     */       }
/* 116 */       return ret;
/*     */     }
/*     */   }
/*     */ 
/*     */   public boolean isTextualExt(String ext) {
/* 121 */     synchronized (this.exts) {
/* 122 */       return this.exts.contains(ext);
/*     */     }
/*     */   }
/*     */ }

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.extrecognizers.ParametricTextualExtensionRecognizer
 * JD-Core Version:    0.6.2
 */