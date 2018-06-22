package com.shareshow.airpc.util;

import android.app.Activity;
import android.widget.BaseAdapter;

import com.shareshow.airpc.Collocation;
import com.shareshow.airpc.model.QMLocalFile;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

//降序用resever
public class SortUtils {

	public static void sort(Activity mContext, int which, final BaseAdapter adapter, List<QMLocalFile> al){
		int a =Collocation.getCollocation().getSort();

		if (a==which){
			Collections.reverse(al);
			adapter.notifyDataSetChanged();
			return;
		}

		if (which==0){
		//按照时间进行排序
			Collocation.getCollocation().saveSort(0);
		    Collections.sort(al,new Comparator<QMLocalFile>(){
			@Override
			public int compare(QMLocalFile lhs, QMLocalFile rhs) {
				if(lhs.getUpdate()>rhs.getUpdate()) {
					return -1;
				}
				else if(lhs.getUpdate()==rhs.getUpdate()) {
					Collator instance = Collator.getInstance(Locale.CHINA);
					return instance.compare(lhs.getName(), rhs.getName());
				}
				else {
					return 1;
				}
			}
		});

	}else if(which==1){
		//按照大小进行排序
			Collocation.getCollocation().saveSort(1);
		    Collections.sort(al, new Comparator<QMLocalFile>() {

			@Override
			public int compare(QMLocalFile file1, QMLocalFile file2) {
				return compare3(file1.getSize(), file2.getSize());
				// TODO Auto-generated method stub
				
			}
		});
		
	}else if(which==2) {
		//按照文件类型进行排序
			Collocation.getCollocation().saveSort(2);
		Collections.sort(al, new Comparator<QMLocalFile>() {

			@Override
			public int compare(QMLocalFile file1, QMLocalFile file2) {
				
				if (file1.getName().lastIndexOf(".")<0) {
					return -1;
				}
				if (file1.getName().lastIndexOf(".")>=0) {
					{
						//比较类型
						String file1Type = file1.getName().substring(file1.getName().lastIndexOf(".") + 1).toLowerCase();
						String file2Type = file2.getName().substring(file2.getName().lastIndexOf(".") + 1).toLowerCase();
						//截取其后缀进行编写，获得其类型
						if (file1Type.equals(file2Type)||file1.getType()==file2.getType()){
							//类型相同,比较时间
							if (file1.getUpdate() == file2.getUpdate()) {
								//时间相同,按照名称排序
								Collator instance = Collator.getInstance(Locale.CHINA);
								return instance.compare(file1.getName().toLowerCase(), file2.getName().toLowerCase());
							}else {
								//比较时间
								if (file1.getUpdate() > file2.getUpdate()) {
									return -1;
								} else if (file1.getUpdate() == file2.getUpdate()) {
									Collator instance = Collator.getInstance(Locale.CHINA);
									return instance.compare(file1.getName(), file2.getName());
								} else {
									return 1;
								}
//								return compare3(file1.getUpdate(), file2.getUpdate());
							}
						}
						else {
							if (file1.getType()!=file2.getType())
							return compare2(file1Type, file2Type);
						}
					}

				}
				return 1;
				// TODO Auto-generated method stub
			}
		});
	}else if(which==3) {
		//按照名称进行排序
			Collocation.getCollocation().saveSort(3);
		Collections.sort(al, new Comparator<QMLocalFile>() {

			@Override
			public int compare(QMLocalFile file1, QMLocalFile file2) {
				// TODO Auto-generated method stub
				Collator instance = Collator.getInstance(Locale.CHINA);
				return instance.compare(file1.getName(), file2.getName());
			}
		});
	}
		mContext.runOnUiThread(new Runnable(){
			@Override
			public void run() {
				adapter.notifyDataSetChanged();
			}
		});

	}

	//倒转
	public static void resever(BaseAdapter adapter, List<QMLocalFile> al){
		Collections.reverse(al);
		adapter.notifyDataSetChanged();

	}


	    //按类型
		private static int compare2(String s1, String s2) {
			int len1 = s1.length();
			int len2 = s2.length();
			int n = Math.min(len1, len2);
			char v1[] = s1.toCharArray();
			char v2[] = s2.toCharArray();
			int pos = 0;
			while (n-- != 0) {
				char c1 = v1[pos];
				char c2 = v2[pos];
				if (c1 != c2) {
					return c1 - c2;
				}
				pos++;
			}
			return len1 - len2; 	
		}
		//按大小进行排序
		public static int compare3(Long o1, Long o2) {
			long val1 = o1.longValue();
			long val2 = o2.longValue();
			return (val1 < val2 ? -1: (val1 == val2 ? 0 : 1));
		}
}
