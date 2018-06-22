package com.shareshow.airpc.util;

import com.shareshow.App;
import com.shareshow.aide.util.Fixed;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by wulong on 2017/6/22 0022.
 */

public class AdSettingUtils {

    private static AdSettingUtils adSettingUtils;

    private AdSettingUtils(){

    }

    public static AdSettingUtils getSingle(){
        if (adSettingUtils == null){
            synchronized (App.getApp()){
                if (adSettingUtils == null){
                    adSettingUtils = new AdSettingUtils();
                }
            }
        }
        return adSettingUtils;
    }

    public  void creatAdConfig(){
        try {
            File file = new File(Fixed.getAdZipPath() + File.separator + "AdConfig.xml");
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
                StringBuilder sb = new StringBuilder();
                sb.append("<?xml version='1.0' encoding='UTF-8'?>\n"
                        + "<AdSetting>\n"
                        + "		<interval_screen>3</interval_screen>\n"
                        + "		<waitTime_pic>300</waitTime_pic>\n"
                        + "		<interval_hot>3</interval_hot>\n"
                        + "</AdSetting>");
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(sb.toString());
                fileWriter.flush();
                fileWriter.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public  void creatAdOfficialConfig(){
        try {
            File officialFile = new File(FileContent.JSONFILE_AD+File.separator+"official","official.xml");
            if (!officialFile.exists()) {
                officialFile.getParentFile().mkdirs();
                officialFile.createNewFile();
                StringBuilder sb = new StringBuilder();
                sb.append("<?xml version='1.0' encoding='UTF-8'?>\n"
                        + "<Official>\n"
                        + "		<name></name>\n"
                        + "		<url></url>\n"
                        + "</Official>");
                FileWriter fileWriter = new FileWriter(officialFile);
                fileWriter.write(sb.toString());
                fileWriter.flush();
                fileWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  void creatExceptionAdConfig(){
        try {
            File file = new File(Fixed.getBoxPath() + File.separator + "AdConfig.xml");
            if (file.exists()) {
                StringBuilder sb = new StringBuilder();
                sb.append("<?xml version='1.0' encoding='UTF-8'?>\n"
                        + "<AdSetting>\n"
                        + "		<interval_screen>3</interval_screen>\n"
                        + "		<waitTime_pic>300</waitTime_pic>\n"
                        + "		<interval_hot>3</interval_hot>\n"
                        + "</AdSetting>");
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(sb.toString());
                fileWriter.flush();
                fileWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * 更新广告位置
     */
    public void updataElement(String elememt, int flag){
        try {
            File file = new File(Fixed.getAdZipPath()+ File.separator +"AdConfig.xml");
            if (!file.exists()){
                creatAdConfig();
            }
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(file);
            Element rootElement = document.getRootElement();
            Element aduVideoflag = rootElement.element(elememt);
            if (aduVideoflag==null){
                aduVideoflag = rootElement.addElement(elememt);
            }
            aduVideoflag.setText(""+flag);
            XMLWriter xmlWriter = new XMLWriter(new FileWriter(file));
            xmlWriter.write(document);
            xmlWriter.close();
         }catch (DocumentException e){
            e.printStackTrace();
            creatExceptionAdConfig();
         }catch (Exception e){
            e.printStackTrace();
         }
    }

    public void updataOfficialElement(String elememt, String value){
        try {
            File file = new File(FileContent.JSONFILE_AD_OFFICIAL_DIR + File.separator + "official.xml");
            if (!file.exists()){
                file.createNewFile();
            }
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(file);
            Element rootElement = document.getRootElement();
            Element subElement = rootElement.element(elememt);
            if (subElement==null){
                subElement = rootElement.addElement(elememt);
            }
            subElement.setText(""+value);
            XMLWriter xmlWriter = new XMLWriter(new FileWriter(file));
            xmlWriter.write(document);
            xmlWriter.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getElement(String elememt,String normolFlag){
        try {
            File file = new File(FileContent.XT_CONFIG + File.separator + "AdConfig.xml");
            if (!file.exists()){
                return ""+normolFlag;
            }
            Document document = null;
            SAXReader saxReader = new SAXReader();
            document = saxReader.read(file);
            Element rootElement = document.getRootElement();
            Element resultelememt = rootElement.element(elememt);
            if (resultelememt == null){
                return ""+normolFlag;
            }
            return resultelememt.getTextTrim();
        } catch (DocumentException e) {
            e.printStackTrace();
            creatExceptionAdConfig();
            return ""+normolFlag;
        }
    }
    public String getOfficialElement(String elememt){
        try {
            File officialFile = new File(FileContent.JSONFILE_AD_OFFICIAL_DIR + File.separator + "official.xml");
            if (!officialFile.exists()){
                return null;
            }
            SAXReader saxReader = new SAXReader();
            Document document = saxReader.read(officialFile);
            Element rootElement = document.getRootElement();
            Element resultelememt = rootElement.element(elememt);
            if (resultelememt == null){
                return null;
            }
            return resultelememt.getTextTrim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
