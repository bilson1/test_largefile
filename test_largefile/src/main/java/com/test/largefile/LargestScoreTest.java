/**   
 * @Project: test_largefile 
 * @Title: LargestUserTest.java 
 * @Package com.test.largefile 
 * @Description: TODO 
 * @author libsh 
 * @date 2018年7月16日 下午6:46:21 
 * @Copyright: 2018 年 研信科技. All rights reserved  
 * @version V1.0   
 */
package com.test.largefile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;



/** 
 * @ClassName LargestUserTest  
 * @Description TODO 
 * @author libsh 
 * @date 2018年7月16日  
 *   
 */
public class LargestScoreTest {
	
	public void getLargestScore(String filePath){
		long startTime = System.currentTimeMillis();
		HashMap<Integer, Integer> largestScoreMap = new HashMap<Integer, Integer>();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(filePath)), 1024*1024);
			String line = null;
            while ((line = reader.readLine()) != null) {
            	UserEntity lineUser = new UserEntity(line.substring(0,line.lastIndexOf(",")), line.substring(line.lastIndexOf(",") + 1));
            	Integer score = lineUser.getScore();
            	Integer reapteNum = largestScoreMap.get(score) == null? 0:largestScoreMap.get(score) +1;
            	largestScoreMap.put(score, reapteNum);
            }
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		long endTime = System.currentTimeMillis();
		System.out.println("耗时："+(endTime-startTime)+" ms");
		System.out.println("重复最多的10个分数为：");
		
		List<Map.Entry<Integer, Integer>> largestScoreList = new ArrayList<Map.Entry<Integer, Integer>>(largestScoreMap.entrySet());
		Collections.sort(largestScoreList, new Comparator<Map.Entry<Integer, Integer>>(){
			@Override
			public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
		for (int i = 0; i < 10; i++) {
			System.out.println("score: "+ largestScoreList.get(i).getKey() + " ,reapteNum: "+largestScoreList.get(i).getValue());
		}
	}

	/** 
	 * @Title: main 
	 * @param args 参数说明
	 * @return void    返回类型
	 */
	public static void main(String[] args) {
		LargestScoreTest test = new LargestScoreTest();
		test.getLargestScore(args[0]);
	}

}
