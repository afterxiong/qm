/*    */ package com.shareshow.airpc.ftpclient;
/*    */ 
/*    */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

/*    */
/*    */

/*    */
/*    */ public class DefaultTextualExtensionRecognizer extends ParametricTextualExtensionRecognizer
/*    */ {
/* 53 */   private static final Object lock = new Object();
/*    */ 
/* 58 */   private static DefaultTextualExtensionRecognizer instance = null;
/*    */ 
/*    */   public static DefaultTextualExtensionRecognizer getInstance()
/*    */   {
/* 66 */     synchronized (lock) {
/* 67 */       if (instance == null) {
/* 68 */         instance = new DefaultTextualExtensionRecognizer();
/*    */       }
/*    */     }
/* 71 */     return instance;
/*    */   }
/*    */ 
/*    */   private DefaultTextualExtensionRecognizer()
/*    */   {
/* 78 */     BufferedReader r = null;
/*    */     try {
/* 80 */       r = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("textualexts")));
/*    */       String line;
/* 83 */       while ((line = r.readLine()) != null) {
/* 84 */         StringTokenizer st = new StringTokenizer(line);
/* 85 */         while (st.hasMoreTokens())
/* 86 */           addExtension(st.nextToken());
/*    */       }
/*    */     }
/*    */     catch (Exception e) {
/*    */     }
/*    */     finally {
/* 92 */       if (r != null)
/*    */         try {
/* 94 */           r.close();
/*    */         }
/*    */         catch (Throwable t)
/*    */         {
/*    */         }
/*    */     }
/*    */   }
/*    */ }

/* Location:           C:\Users\tanwei\Desktop\ftp4j-1.7.2.jar
 * Qualified Name:     it.sauronsoftware.ftp4j.extrecognizers.DefaultTextualExtensionRecognizer
 * JD-Core Version:    0.6.2
 */