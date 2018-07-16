package com.test.largefile;

import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LargeFileTest {
	
	private long score = 0;
	
	private boolean recordFlag = false;
	
	private long rank = 1;
	
	private static final int blockSize = 100;
	
	private static final int threadNum = 1;

	public void getUserRank(String userId, String filePath) {
		long starTime = System.currentTimeMillis();
		RandomAccessFile raf = null;
		//开始坐标数组
		long[] beginIndexs = new long[blockSize];
		// 结束坐标数组
		long[] endIndexs = new long[blockSize];
		try {
			raf = new RandomAccessFile(filePath, "r");
			//计算出每个区块的开始结束位置
			splitLargeFile(raf,beginIndexs,endIndexs);	
			raf.close();
			
			//开始查找用户分数
			ExecutorService executorService = Executors.newFixedThreadPool(threadNum);
	        CountDownLatch latch = new CountDownLatch(blockSize);
			for (int i = 0; i < blockSize; i++) {
				executorService.execute(new getScoreThread(userId,filePath,beginIndexs[i],endIndexs[i],latch));
			}
			latch.await();
			long endTime = System.currentTimeMillis();
			System.out.println("找到用户:"+ userId +" ,分数：" + score);
			System.out.println("耗时："+(endTime-starTime)+" ms");
			
			executorService.shutdown();
			executorService.shutdownNow();
			
			System.out.println("开始找用户排名...");
			//开始查找用户排名
			executorService = Executors.newFixedThreadPool(threadNum);
	        CountDownLatch ranklatch = new CountDownLatch(blockSize);
	        for (int i = 0; i < 100; i++) {
	        	executorService.execute(new getRankThread(userId,filePath,beginIndexs[i],endIndexs[i],ranklatch));
			}
	        ranklatch.await();
	        endTime = System.currentTimeMillis();
			System.out.println("找到用户:" + userId + " ,分数：" + score + " ,排名：" + rank);
			System.out.println("耗时："+(endTime-starTime)+" ms");
	        
	        executorService.shutdown();
			executorService.shutdownNow();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 计算出开始结束坐标位置
	 * @Title: splitLargeFile 
	 * @param raf
	 * @param fc
	 * @return 参数说明
	 * @return Map<Integer,MappedByteBuffer>    返回类型
	 */
	public void splitLargeFile(RandomAccessFile raf,long[] beginIndexs,long[] endIndexs){
		try {
			// 分成100块
			long tempBlockSize = raf.length() / blockSize;
			//开始坐标
			long beginIndex = 0;
			// 开始结束坐标
			long endIndex = 0;
			
			//计算出每个区块的MappedByteBuffer
			for (int i = 0; i < blockSize; i++) {
				beginIndex = endIndex;
				beginIndexs[i] = beginIndex;
				if (i + 1 == blockSize) {
					endIndex = raf.length();
					endIndexs[i] = endIndex;
					break;
				}
				//计算结束坐标
				endIndex += tempBlockSize;
				// 计算出完整的结束位置
				endIndex += getCompleteEndIndex(endIndex, raf, '\n');
				endIndexs[i] = endIndex;
				endIndex++;
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 计算完整结束位置
	 * @param endIndex
	 * @param raf
	 * @param separator
	 */
	public long getCompleteEndIndex(long endIndex,RandomAccessFile raf, char separator) {
		long count = 0;
		try {
			raf.seek(endIndex);
			// 遇到分隔符结束
			while (raf.read() != separator) {
				count++;
			}
			count++;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return count;
	}
	
	/**
	 * 寻找用户分数线程
	 * @author Administrator
	 */
	class getScoreThread implements Runnable {
		private String userId;
		private RandomAccessFile raf;
		private long beginIndex;
		private long endIndex;
		private CountDownLatch latch;
		
		public getScoreThread(String userId,String filePath,long beginIndex,long endIndex, CountDownLatch latch){
			super();
			this.userId = userId;
			this.beginIndex = beginIndex;
			this.endIndex = endIndex;
			this.latch = latch;
			try {
				this.raf = new RandomAccessFile(filePath,"r");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			boolean findFlag = false;
			String blockStr = new String(readBlock());
			String lineUserId = "";
			String[] lineStrs = blockStr.split(System.lineSeparator());
			for(String lineStr : lineStrs){
				lineUserId = lineStr.substring(0, lineStr.indexOf(","));
				if(lineUserId.equals(userId)){
					score = Integer.valueOf(lineStr.substring(lineStr.indexOf(",")+1)).longValue();
					findFlag = true;
					break;
				}
			}
			//找到分数结束线程
			if(findFlag){
				long curCount = latch.getCount();
				for (long i = 0; i < curCount; i++) {
					latch.countDown();
				}
			}else{
				latch.countDown();
			}
		}
		
		//读取出每个区块的内容
		private byte[] readBlock(){
			byte[] blockByte = new byte[(int) (endIndex-beginIndex)];
			try {
				raf.seek(beginIndex);
				raf.read(blockByte);
				raf.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return blockByte;
		}
	}
	
	/**
	 * 寻找用户排名线程
	 * @author Administrator
	 */
	class getRankThread implements Runnable {
		private String userId;
		private RandomAccessFile raf;
		private long beginIndex;
		private long endIndex;
		private CountDownLatch latch;
		
		public getRankThread(String userId,String filePath,long beginIndex,long endIndex, CountDownLatch latch){
			super();
			this.userId = userId;
			this.beginIndex = beginIndex;
			this.endIndex = endIndex;
			this.latch = latch;
			try {
				this.raf = new RandomAccessFile(filePath,"r");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			String blockStr = new String(readBlock());
			String lineUserId = "";
			long lineScore = 0;
			String[] lineStrs = blockStr.split(System.lineSeparator());
			for(String lineStr : lineStrs){
				lineUserId = lineStr.substring(0, lineStr.indexOf(","));
				lineScore = Integer.valueOf(lineStr.substring(lineStr.indexOf(",")+1)).longValue();
				if(lineScore < score){
					addOne();
				}else if(score == lineScore){
					//在前面已经找到了这条记录，所以后面出现相同分数，加一
					if(recordFlag){
						addOne();
					}else if(lineUserId == userId){//找到记录改变全局变量
						changeRecord();
					}
				}
			}
			latch.countDown();
		}
		
		//读取出每个区块的内容
		private byte[] readBlock(){
			byte[] blockByte = new byte[(int) (endIndex-beginIndex)];
			try {
				raf.seek(beginIndex);
				raf.read(blockByte);
				raf.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return blockByte;
		}
	}
	
	public synchronized void changeRecord(){
		recordFlag = true;
	}
	
	public synchronized void addOne(){
		rank ++;
	}

	public static void main(String[] args) {
		LargeFileTest test = new LargeFileTest();
		// 源文件路径
		String inputFilePath = "E:/user_score.csv";
		// 取用户名次
		test.getUserRank("4bed281b-56c7-42df-8e2b-e244151831df", inputFilePath);
	}
}
