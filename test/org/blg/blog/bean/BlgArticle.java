package org.blg.blog.bean;

import org.record.avtice.RecordActive;

/**
 * BlgArticle entity.
 * 
 * @author MyEclipse Persistence Tools
 */

public class BlgArticle extends RecordActive<BlgArticle> {

	public static BlgArticle DAO = null;

	public static BlgArticle blgArticle = new BlgArticle();

	private Integer id;

	private Integer sub_Id;

	private String title;
	
	private String content;


	
	public BlgArticle() {
		super();
		DAO = new BlgArticle();
		// TODO Auto-generated constructor stub
	}
	public BlgArticle(Integer id, Integer sub_Id, String title, String content) {
		super();
		this.id = id;
		this.sub_Id = sub_Id;
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
	public Integer getSub_Id() {
		return sub_Id;
	}
	public void setSub_Id(Integer sub_Id) {
		this.sub_Id = sub_Id;
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