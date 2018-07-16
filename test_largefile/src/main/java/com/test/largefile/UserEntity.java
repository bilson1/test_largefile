/**   
 * @Project: test_largefile 
 * @Title: UserEntity.java 
 * @Package com.test.largefile 
 * @Description: TODO 
 * @author libsh 
 * @date 2018年7月16日 下午5:18:43 
 * @Copyright: 2018 年 研信科技. All rights reserved  
 * @version V1.0   
 */
package com.test.largefile;

/** 
 * @ClassName UserEntity  
 * @Description TODO 
 * @author libsh 
 * @date 2018年7月16日  
 *   
 */
public class UserEntity implements Comparable{
	
	private String customerId;
	
	private Integer score;
	
	public UserEntity(){
		
	}
	
	public UserEntity(String customerId, String score){
		this.customerId = customerId;
		this.score = Integer.valueOf(score);
	}

	/** 
	 * @return customerId 
	 */
	public String getCustomerId() {
		return customerId;
	}

	/** 
	 * @param customerId 要设置的 customerId 
	 */
	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	/** 
	 * @return score 
	 */
	public Integer getScore() {
		return score;
	}

	/** 
	 * @param score 要设置的 score 
	 */
	public void setScore(Integer score) {
		this.score = score;
	}
	
	@Override
    public String toString() {
        return customerId + "," + score;
    }

	@Override
	public int compareTo(Object obj) {
		UserEntity user = (UserEntity) obj;
		if (score > user.getScore()) {
			return 1;
		} else if (score < user.getScore()) {
			return -1;
		}
		return 0;
	}

}
