package org.blg.blog.bean;

import org.record.avtice.ActiveRecord;

/**
 * BlgArticle entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class BlgArticle extends ActiveRecord<BlgArticle> {

//	public static BlgArticle DAO = null;
	public static BlgArticle DAO = new BlgArticle();

	public static BlgArticle blgArticle = new BlgArticle();

	private Integer id;

	private Integer subId;

	private String title;
	
	private String content;


	
	public BlgArticle() {
		super();
//		DAO = new BlgArticle();
		// TODO Auto-generated constructor stub
	}
	public BlgArticle(Integer id, Integer sub_Id, String title, String content) {
		super();
		this.id = id;
		this.subId = sub_Id;
		this.title = title;
		this.content = content;
	}
	public BlgArticle(Integer id) {
		super();
		this.id = id;
	}
	
	
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getSubId() {
		return subId;
	}
	public void setSubId(Integer sub_Id) {
		this.subId = sub_Id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

}