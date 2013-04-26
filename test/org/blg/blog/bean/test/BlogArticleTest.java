package org.blg.blog.bean.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.blg.blog.bean.BlgArticle;
import org.junit.Test;

public class BlogArticleTest {
	
	@Test
	public void tryMe() {
//		System.out.println("a - "+ ((int)'a'));
//		System.out.println("A - "+ ((int)'A'));
//		System.out.println("z - "+ ((int)'z'));
//		System.out.println("Z - "+ ((int)'Z'));
		
//		System.out.println(Integer.valueOf(""));
//		System.out.println(Integer.class);
		
		System.out.println( 5 % 2);
		System.out.println( 5 / 2);
		
	}
	
	////////////////////////////////////////////////////
	//				recordActive test
	////////////////////////////////////////////////////
	
	// save
	@Test
	public void save() {
		BlgArticle ba = new BlgArticle(45, 12, "title-id", "content-val");
		System.out.println("resutl: "+ ba.save(ba));
	}
	@Test
	public void save1() {
		List<BlgArticle> beans = new ArrayList<BlgArticle>();
		BlgArticle ba1 = new BlgArticle(7, 12, "title", "content");
		BlgArticle ba2 = new BlgArticle(8, 12, "title", "content");		
		beans.add(ba1);
		beans.add(ba2);
		Map<String, Integer> rtns = BlgArticle.blgArticle.save(beans);
//		Map<String, Integer> rtns = new BlgArticle().save(beans);
		Set<Entry<String, Integer>> entry = rtns.entrySet();
		for(Entry<String, Integer> e : entry) {
			System.out.println(e.getKey() +" -> "+ e.getValue());
		}
	}
	// delete

	@Test
	public void delete(){
		int rtn = BlgArticle.blgArticle.delete(1);
		System.out.println("result : "+ rtn);
	}
	@Test
	public void delete1(){
		BlgArticle ba = new BlgArticle(2);
		ba.delete(ba);
	}
	@Test
	public void delete2(){
		int rtn = BlgArticle.blgArticle.delete("id", "8");
		System.out.println("result : "+ rtn);
	}
	@Test
	public void delete3(){
		HashMap<String, String> map = new HashMap<String, String>();
		map.put("4", "id");
		map.put("5", "id");
		map.put("6", "id");
		Map<String, Integer> rtns = BlgArticle.blgArticle.delete(map);
		Set<Entry<String, Integer>> entry = rtns.entrySet();
		System.out.println("result : \r");
		for(Entry<String, Integer> i : entry) {
			System.out.println("\t key: "+ i.getKey() +" result: "+ i.getValue());
		}
		
	}
	@Test
	public void delete4(){
		int rtn = new BlgArticle(18).delete();
		System.out.println("result : "+ rtn);
	}
	// update
	@Test
	public void update(){
		BlgArticle ba = new BlgArticle(7, 22, "title-111", "content-111");
		int rtn = ba.update(ba);
		System.out.println("result : "+ rtn);
	}
	@Test
	public void update1(){
		BlgArticle ba1 = new BlgArticle(7, 12, "title", "content");
		BlgArticle ba2 = new BlgArticle(8, 12, "title", "content");
		List<BlgArticle> bas = new ArrayList<BlgArticle>();
		bas.add(ba1);
		bas.add(ba2);
		Map<String, Integer> rtns = BlgArticle.blgArticle.update(bas);
		
		Set<Entry<String, Integer>> entry = rtns.entrySet();
		System.out.println("result : ");
		for(Entry<String, Integer> i : entry) {
			System.out.println("\t key: "+ i.getKey() +"-> result: "+ i.getValue());
		}
	}
	// read (query)
	@Test
	public void get() {
		BlgArticle ba = BlgArticle.blgArticle.get(7);
//		BlgArticle ba = (BlgArticle)BlgArticle.blgArticle.get(7);
		System.out.println(
				"\r - "  +ba.getId() +
				"\r - " +ba.getSubId()+
				"\r - " +ba.getTitle()+
				"\r - " +ba.getContent());
	}
	@Test
	public void get1() {
		List<BlgArticle> bas = BlgArticle.blgArticle.get("id", 8);
		for(BlgArticle ba : bas) {
			System.out.println(ba.getId() +
					"\r" +ba.getSubId()+
					"\r" +ba.getTitle()+
					"\r" +ba.getContent());
		}
	}
	@Test
	public void get2() {
		List<BlgArticle> beans = BlgArticle.blgArticle.get();
		for(BlgArticle b : beans) {
			System.out.println(b.getId() +" - "+ b.getSubId() +" - "+ b.getTitle() +" - "+ b.getContent());
		}
	}
	@Test
	public void get3() {
		Set<Object> params = new HashSet<Object>();
		List<BlgArticle> beans = BlgArticle.blgArticle.get("id", params);
		for(BlgArticle b : beans) {
			System.out.println(b.getId() +" - "+ b.getSubId() +" - "+ b.getTitle() +" - "+ b.getContent());
		}
	}
	@Test
	public void get4() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("id", "8");
		params.put("sub_id", "12");
		params.put("title", "title");
		List<BlgArticle> beans = BlgArticle.blgArticle.get(params);
		for(BlgArticle b : beans) {
			System.out.println(b.getId() +" - "+ b.getSubId() +" - "+ b.getTitle() +" - "+ b.getContent());
		}
	}
	@Test
	public void get5() {
		List<BlgArticle> beans = BlgArticle.blgArticle.get(7, 1);  // ('鍚捣', '鍋忕Щ閲�)
		for(BlgArticle b : beans) {
			System.out.println(b.getId() +" - "+ b.getSubId() +" - "+ b.getTitle() +" - "+ b.getContent());
		}
	}
	
	@Test
	public void count() {
		System.out.println(BlgArticle.blgArticle.count());
	}

	
	

	////////////////////////////////////////////////////
	//				reflect test
	////////////////////////////////////////////////////
	
	@Test
	public void getFields(){
		BlgArticle ba = new BlgArticle();
		Map<String, String> fs = ba.getFields();
		Set<Entry<String, String>> ent = fs.entrySet();
		for (Entry<String, String>  e : ent) {
			System.out.println(e.getKey() +" -> "+ e.getValue());
		}
	}

	@Test
	public void getType() {
		BlgArticle ba = new BlgArticle();
		try {
			System.out.println(ba.getType("id"));
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}
	}
	
}
