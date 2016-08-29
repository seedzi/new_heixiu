package com.xiuxiuchat.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 *
 * 集合处理辅助类
 * @author 高航
 * @date 2013-1-11
 *
 */
public class CollectionUtil {

	/**
	 * 获取集合中指定索引的元素
	 * @param index 制定索引
	 * @return
	 */
	public static <T> T get(Collection<T> col, int index) {
		try {
			if(col == null || col.size() <= index) {
				return null;
			}

			if(col instanceof List) {
				return ((List<T>)col).get(index);
			}

			ArrayList<T> tempList = new ArrayList<T>();
			tempList.addAll(col);
			return tempList.get(index);
		}catch (Exception e){
			e.printStackTrace();
		}

		return null;
	}

	public static <T> void addAll(Collection<T> col, T[] adds) {
		if(col == null || adds == null || adds.length == 0) {
			return;
		}

		for (T t : adds) {
			col.add(t);
		}
	}

	public static <T> int removeALl(Collection<T> col, T[] adds) {
		if(col == null || adds == null || adds.length == 0) {
			return -1;
		}

		int count = 0;
		for (T t : adds) {
			count += col.remove(t) ? 1 : 0;
		}

		return count;
	}

	/**
	 * 将原集合中指定个数的元素拷贝到目标集合中
	 * @param d 目标集合
	 * @param s 原集合
	 * @param size 待拷贝元素个数
	 */
	public static <T> Collection<T> add(Collection<T> d, Collection<T> s, int size) {
		if(d == null || s == null || size <= 0) {
			return d;
		}

		int copySize = Math.min(size, s.size());
		Iterator<T> it = s.iterator();
		while(copySize-- > 0) {
			d.add(it.next());
		}

		return d;
	}

	/**
	 * 判断给定集合是否为空
	 * @param col 待检验集合
	 * @return true: 为空, false: 不为空
	 */
	public static <T> boolean isEmpty(Collection<T> col) {
		return col == null || col.size() == 0;
	}

	public static <T> boolean hasItem(Collection<T> col, T t) {
		if(isEmpty(col) || t == null) {
			return false;
		}

		return col.contains(t);
	}

}
