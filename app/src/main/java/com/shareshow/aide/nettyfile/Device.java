package com.shareshow.aide.nettyfile;


import com.shareshow.airpc.Collocation;
import com.shareshow.db.SearchBean;

import java.util.ArrayList;

/**
 * Created by TEST on 2017/12/14.
 */

public class Device {

     private static  ArrayList<SearchBean> beans = new ArrayList<SearchBean>();

     public static boolean addDevice(SearchBean bean){
         if(bean.getIds()==null){
             return false;
         }

         String ids =  Collocation.getCollocation().getIDS();

         if(ids==null||!ids.equals(bean.getIds())){
             return false;
         }

         for (SearchBean bn : beans){
             if(bn.getIds().equals(bean.getIds())){
                 return false;
             }
         }

         beans.add(bean);
         return true;
     }

     public static void deleteDevice(SearchBean bean){

         for (int i = (beans.size()-1); i >=0 ; i--){
             if(beans.get(i).getIds().equals(bean.getIds())){
                 beans.remove(i);
                 break;
             }
         }

     }


     public static ArrayList<SearchBean> getBeans(){

         return beans;
     }

     public static int getDeviceSize(){
         return beans.size();
     }

     public static void clear(){
         beans.clear();
     }


}
