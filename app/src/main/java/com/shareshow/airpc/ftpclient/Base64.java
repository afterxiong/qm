/*     */ package com.shareshow.airpc.ftpclient;
/*     */ 
/*     */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */
/*     */

/*     */
/*     */ class Base64
/*     */ {
/*  43 */   static String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";
/*     */ 
/*  45 */   static char pad = '=';
/*     */ 
/*     */   public static String encode(String str)
/*     */     throws RuntimeException
/*     */   {
/*  63 */     byte[] bytes = str.getBytes();
/*  64 */     byte[] encoded = encode(bytes);
/*     */     try {
/*  66 */       return new String(encoded, "ASCII");
/*     */     } catch (UnsupportedEncodingException e) {
/*  68 */       throw new RuntimeException("ASCII is not supported!", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String encode(String str, String charset)
/*     */     throws RuntimeException
/*     */   {
/*     */     byte[] bytes;
/*     */     try
/*     */     {
/*  94 */       bytes = str.getBytes(charset);
/*     */     } catch (UnsupportedEncodingException e) {
/*  96 */       throw new RuntimeException("Unsupported charset: " + charset, e);
/*     */     }
/*  98 */     byte[] encoded = encode(bytes);
/*     */     try {
/* 100 */       return new String(encoded, "ASCII");
/*     */     } catch (UnsupportedEncodingException e) {
/* 102 */       throw new RuntimeException("ASCII is not supported!", e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static String decode(String str)
/*     */     throws RuntimeException
/*     */   {
/*     */     byte[] bytes;
/*     */     try
/*     */     {
/* 124 */       bytes = str.getBytes("ASCII");
/*     */     } catch (UnsupportedEncodingException e) {
/* 126 */       throw new RuntimeException("ASCII is not supported!", e);
/*     */     }
/* 128 */     byte[] decoded = decode(bytes);
/* 129 */     return new String(decoded);
/*     */   }
/*     */ 
/*     */   public static String decode(String str, String charset)
/*     */     throws RuntimeException
/*     */   {
/*     */     byte[] bytes;
/*     */     try
/*     */     {
/* 154 */       bytes = str.getBytes("ASCII");
/*     */     } catch (UnsupportedEncodingException e) {
/* 156 */       throw new RuntimeException("ASCII is not supported!", e);
/*     */     }
/* 158 */     byte[] decoded = decode(bytes);
/*     */     try {
/* 160 */       return new String(decoded, charset);
/*     */     } catch (UnsupportedEncodingException e) {
/* 162 */       throw new RuntimeException("Unsupported charset: " + charset, e);
/*     */     }
/*     */   }
/*     */ 
/*     */   public static byte[] encode(byte[] bytes)
/*     */     throws RuntimeException
/*     */   {
/* 184 */     return encode(bytes, 0);
/*     */   }
/*     */ 
/*     */   public static byte[] encode(byte[] bytes, int wrapAt)
/*     */     throws RuntimeException
/*     */   {
/* 211 */     ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
/* 212 */     ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
/*     */     try {
/* 214 */       encode(inputStream, outputStream, wrapAt);
/*     */     } catch (IOException e) {
/* 216 */       throw new RuntimeException("Unexpected I/O error", e);
/*     */     } finally {
/*     */       try {
/* 219 */         inputStream.close();
/*     */       }
/*     */       catch (Throwable t) {
/*     */       }
/*     */       try {
/* 224 */         outputStream.close();
/*     */       }
/*     */       catch (Throwable t) {
/*     */       }
/*     */     }
/* 229 */     return outputStream.toByteArray();
/*     */   }
/*     */ 
/*     */   public static byte[] decode(byte[] bytes)
/*     */     throws RuntimeException
/*     */   {
/* 250 */     ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
/* 251 */     ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
/*     */     try {
/* 253 */       decode(inputStream, outputStream);
/*     */     } catch (IOException e) {
/* 255 */       throw new RuntimeException("Unexpected I/O error", e);
/*     */     } finally {
/*     */       try {
/* 258 */         inputStream.close();
/*     */       }
/*     */       catch (Throwable t) {
/*     */       }
/*     */       try {
/* 263 */         outputStream.close();
/*     */       }
/*     */       catch (Throwable t) {
/*     */       }
/*     */     }
/* 268 */     return outputStream.toByteArray();
/*     */   }
/*     */ 
/*     */   public static void encode(InputStream inputStream, OutputStream outputStream)
/*     */     throws IOException
/*     */   {
/* 293 */     encode(inputStream, outputStream, 0);
/*     */   }
/*     */ 
/*     */   public static void encode(InputStream inputStream, OutputStream outputStream, int wrapAt)
/*     */     throws IOException
/*     */   {
/* 322 */     Base64OutputStream aux = new Base64OutputStream(outputStream, wrapAt);
/* 323 */     copy(inputStream, aux);
/* 324 */     aux.commit();
/*     */   }
/*     */ 
/*     */   public static void decode(InputStream inputStream, OutputStream outputStream)
/*     */     throws IOException
/*     */   {
/* 349 */     copy(new Base64InputStream(inputStream), outputStream);
/*     */   }
/*     */ 
/*     */   public static void encode(File source, File target, int wrapAt)
/*     */     throws IOException
/*     */   {
/* 372 */     InputStream inputStream = null;
/* 373 */     OutputStream outputStream = null;
/*     */     try {
/* 375 */       inputStream = new FileInputStream(source);
/* 376 */       outputStream = new FileOutputStream(target);
/* 377 */       encode(inputStream, outputStream, wrapAt);
/*     */     } finally {
/* 379 */       if (outputStream != null)
/*     */         try {
/* 381 */           outputStream.close();
/*     */         }
/*     */         catch (Throwable t)
/*     */         {
/*     */         }
/* 386 */       if (inputStream != null)
/*     */         try {
/* 388 */           inputStream.close();
/*     */         }
/*     */         catch (Throwable t)
/*     */         {
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void encode(File source, File target)
/*     */     throws IOException
/*     */   {
/* 411 */     InputStream inputStream = null;
/* 412 */     OutputStream outputStream = null;
/*     */     try {
/* 414 */       inputStream = new FileInputStream(source);
/* 415 */       outputStream = new FileOutputStream(target);
/* 416 */       encode(inputStream, outputStream);
/*     */     } finally {
/* 418 */       if (outputStream != null)
/*     */         try {
/* 420 */           outputStream.close();
/*     */         }
/*     */         catch (Throwable t)
/*     */         {
/*     */         }
/* 425 */       if (inputStream != null)
/*     */         try {
/* 427 */           inputStream.close();
/*     */         }
/*     */         catch (Throwable t)
/*     */         {
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   public static void decode(File source, File target)
/*     */     throws IOException
/*     */   {
/* 450 */     InputStream inputStream = null;
/* 451 */     OutputStream outputStream = null;
/*     */     try {
/* 453 */       inputStream = new FileInputStream(source);
/* 454 */       outputStream = new FileOutputStream(target);
/* 455 */       decode(inputStream, outputStream);
/*     */     } finally {
/* 457 */       if (outputStream != null)
/*     */         try {
/* 459 */           outputStream.close();
/*     */         }
/*     */         catch (Throwable t)
/*     */         {
/*     */         }
/* 464 */       if (inputStream != null)
/*     */         try {
/* 466 */           inputStream.close();
/*     */         }
/*     */         catch (Throwable t)
/*     */         {
/*     */         }
/*     */     }
/*     */   }
/*     */ 
/*     */   private static void copy(InputStream inputStream, OutputStream outputStream)
/*     */     throws IOException
/*     */   {
/* 487 */     byte[] b = new byte[1024];
/*     */     int len;
/* 489 */     while ((len = inputStream.read(b)) != -1)
/* 490 */       outputStream.write(b, 0, len);
/*     */   }
/*     */ }

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.connectors.Base64
 * JD-Core Version:    0.6.2
 */