package com.shareshow.airpc.util;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PackZip {

	//zip压缩是否成功
	public static int ERROR = 0;
	public static int SUCCESS = 1;
	/**
	 * @param src 源地址
	 * @param dest 目标地址
	 * @return int zip压缩是否成功
	 * @throws IOException
	 */
	public static int zip(String src, String dest) throws IOException {
		//提供了一个数据项压缩成一个ZIP归档输出流
		ZipOutputStream out = null;
		try {
			File outFile = new File(dest);//源文件或者目录
			File fileOrDirectory = new File(src);//压缩文件路径
			out = new ZipOutputStream(new FileOutputStream(outFile));
			//如果此文件是一个文件，否则为false。
			if (fileOrDirectory.isFile()) {
				zipFileOrDirectory(out, fileOrDirectory, "");
			} else {
				//返回一个文件或空阵列。
				File[] entries = fileOrDirectory.listFiles();
				if (entries.length == 0){

					return ERROR;
				}else{
					for (int i = 0; i < entries.length; i++){
						// 递归压缩，更新curPaths
						zipFileOrDirectory(out, entries[i], "");
					}
				}


			}
			return SUCCESS;
		} catch (Exception ex) {
			ex.printStackTrace();
			return ERROR;
		} finally {
			//关闭输出流
			if (out != null) {
				try {
					out.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private static void zipFileOrDirectory(ZipOutputStream out,
										   File fileOrDirectory, String curPath) throws IOException {
		//从文件中读取字节的输入流
		FileInputStream in = null;
		try {
			//如果此文件是一个目录，否则返回false。
			if (!fileOrDirectory.isDirectory()) {
				// 压缩文件
				byte[] buffer = new byte[4096];
				int bytes_read;
				in = new FileInputStream(fileOrDirectory);
				//实例代表一个条目内的ZIP归档
				ZipEntry entry = new ZipEntry(curPath
						+ fileOrDirectory.getName());
				//条目的信息写入底层流
				//此处设置一存储方式压缩，才可满足开机启动图zip的要求
				entry.setMethod(ZipEntry.STORED);
				entry.setCompressedSize(fileOrDirectory.length());
				entry.setSize(fileOrDirectory.length());
				CRC32 crc = new CRC32();
				crc.update(getFileBytes(fileOrDirectory));
				entry.setCrc(crc.getValue());

				out.putNextEntry(entry);
				while ((bytes_read = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytes_read);
				}
				out.closeEntry();
			} else {
				// 压缩目录
				File[] entries = fileOrDirectory.listFiles();
				for (int i = 0; i < entries.length; i++) {
					// 递归压缩，更新curPaths
					zipFileOrDirectory(out, entries[i], curPath
							+ fileOrDirectory.getName() + "/");
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			// throw ex;
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
	}
	/**
	 *
	 * 方法名：getFileBytes<br>
	 * 描述：获取文件的bytes<br>
	 * 创建时间：2016-12-30 下午2:23:33<br>
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private static byte[] getFileBytes(File file) throws
			IOException {
		byte[] buffer;
		FileInputStream fis = new FileInputStream(file);
		ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
		byte[] b = new byte[1000];
		int n;
		while ((n = fis.read(b)) != -1) {
			bos.write(b, 0, n);
		}
		fis.close();
		bos.close();
		buffer = bos.toByteArray();
		return buffer;
	}


	public static String getFileMD5(File file) {
		try{
			if (!file.isFile()) {
				return null;
			}
			MessageDigest digest;
			FileInputStream in;
			byte buffer[] = new byte[1024];
			int len;
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
			BigInteger bigInt = new BigInteger(1, digest.digest());
			return bigInt.toString(16);
		   }catch (Exception e){
			e.printStackTrace();
			return "";
		   }
	 }

}
