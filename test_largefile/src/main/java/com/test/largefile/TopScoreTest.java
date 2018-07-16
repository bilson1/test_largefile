/**   
 * @Project: test_largefile 
 * @Title: TopScoreTest.java 
 * @Package com.test.largefile 
 * @Description: TODO 
 * @author libsh 
 * @date 2018年7月16日 下午5:06:57 
 * @Copyright: 2018 年 研信科技. All rights reserved  
 * @version V1.0   
 */
package com.test.largefile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.PriorityQueue;

/** 
 * @ClassName TopScoreTest  
 * @author libsh 
 * @date 2018年7月16日  
 *   
 */
public class TopScoreTest {
	
	//Top 队列
	class TopQueue {
		
		private Integer size;
		
		private PriorityQueue<UserEntity> queue;
		
		public TopQueue(Integer size){
			this.size = size;
			this.queue = new PriorityQueue<UserEntity>(size);
		}
		
		public void put(UserEntity user) {
            if (queue.size() < size) {
                queue.add(user);
            } else {
            	UserEntity maxUser = queue.peek();
                if (maxUser.compareTo(user) < 0) {
                    queue.poll();
                    queue.add(user);
                }
            }
        }
		
		public UserEntity[] getUsers() {
			UserEntity[] users =new UserEntity[size];
            queue.toArray(users);
            return users;
        }
	}
	
	
	public void getTopUser(Integer size){
		long startTime = System.currentTimeMillis();
		BufferedReader reader = null;
		TopQueue queue = new TopQueue(size);
		try {
			reader = new BufferedReader(new FileReader(new File("E:/user_score.csv")), 1024*1024);
			String line = null;
            while ((line = reader.readLine()) != null) {
                queue.put(new UserEntity(line.substring(0,line.lastIndexOf(",")), line.substring(line.lastIndexOf(",") + 1)));
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
		System.out.println("Top 100 用户："+ Arrays.deepToString(queue.getUsers()));
	}

	/** 
	 * @Title: main 
	 * @param args 参数说明
	 * @return void    返回类型
	 */
	public static void main(String[] args) {
		TopScoreTest test = new TopScoreTest();
		test.getTopUser(100);
	}

}
