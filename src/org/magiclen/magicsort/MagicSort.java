/*
 *
 * Copyright 2015-2016 magiclen.org
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.magiclen.magicsort;

import java.util.Comparator;

/**
 * <p>
 * 排序類別。用法如下：
 * </p>
 * <ol>
 * <li>使用setCompare方法可以自行決定排序的方式</li>
 * <li>繼承MagicSort.MagicSortCallback介面可以非同步的方式處理排序，實作sortFinished方法決定排序完成後的行為</li>
 * <li>在排序過程中還可用getProgress來取得排序進度。</li>
 * </ol>
 *
 *
 * @author Magic Len
 *
 * @param <T> 要排序的類別。
 */
public final class MagicSort<T> {

    // -----物件變數-----
    private T[] data; //資料
    private boolean clonable; //排序前是否先複製資料(若為true，排序將不會動到原始的資料順序，排序結果需用getData來取得)
    private int sortedLength; //儲存已排序的長度
    private int sortsLength; //儲存總共要排序的長度
    private boolean sorting; //儲存是否正在排序中
    private MagicSortCallback<? super T> callback; //非同步回呼介面
    private Comparator<? super T> comparator = new Comparator<T>() { //預設排序方式

	@Override
	public int compare(final T t1, final T t2) {
	    if (t1 instanceof Comparable && t2 instanceof Comparable) {
		return ((Comparable) t1).compareTo(t2);
	    }
	    return t1.toString().compareTo(t2.toString()); //轉成字串後使用自然排序
	}
    };

    // -----建構子-----
    /**
     * 建構子，預設排序前不事先複製資料。
     */
    public MagicSort() {
	setClonable(false);
    }

    /**
     * 建構子，設定排序前是否先複製資料。
     *
     * @param clonable 排序前是否先複製資料
     */
    public MagicSort(final boolean clonable) {
	setClonable(clonable);
    }

    /**
     * 建構子，傳入要排序的資料陣列，預設排序前不事先複製資料。
     *
     * @param data 要排序的陣列
     */
    public MagicSort(final T[] data) {
	this(data, false);
    }

    /**
     * 建構子，傳入要排序的資料陣列，設定排序前是否先複製資料。
     *
     * @param data 要排序的陣列
     * @param clonable 排序前是否先複製資料
     */
    public MagicSort(final T[] data, final boolean clonable) {
	setData(data);
	setClonable(clonable);
    }

    //--------------物件方法--------------
    /**
     * 設定回呼物件，若是想要使用非同步的方式去計算，可以設定這個方法，用MagicSortCallback來接收結果。
     *
     * @param callback 傳入回呼物件
     */
    public synchronized void setCallback(final MagicSortCallback<? super T> callback) {
	this.callback = callback;
    }

    /**
     * 取得回呼物件。
     *
     * @return 傳回回呼物件
     */
    public MagicSortCallback getCallback() {
	return callback;
    }

    /**
     * 設定資料的排序方式。
     *
     * @param comparator 傳入Comparator
     */
    public synchronized void setComparator(final Comparator<? super T> comparator) {
	if (comparator != null) {
	    this.comparator = comparator;
	}
    }

    /**
     * 取得資料的排序方式。
     *
     * @return 傳回資料的排序方式。
     */
    public Comparator getComparator() {
	return comparator;
    }

    /**
     * 設定要排序的陣列。
     *
     * @param data 傳入要排序的陣列
     */
    public synchronized void setData(final T[] data) {
	this.data = data;
    }

    /**
     * 取得已排序過或是未排序過的陣列。
     *
     * @return 傳回已排序過或是未排序過的陣列
     */
    public T[] getData() {
	return data;
    }

    /**
     * 設定排序前是否先複製資料。
     *
     * @param clonable 傳入排序前是否先複製資料
     */
    public synchronized void setClonable(final boolean clonable) {
	this.clonable = clonable;
    }

    /**
     * 是否先複製資料
     *
     * @return 傳回是否先複製資料。
     */
    public boolean isClonable() {
	return clonable;
    }

    /**
     * 是否正在排序中。
     *
     * @return 傳回是否正在排序中。
     */
    public boolean isSorting() {
	return sorting;
    }

    /**
     * 選擇排序法。
     *
     * @param start 排序起點位置
     * @param end 排序終點位置
     */
    private void sellectionSort(final int start, final int end) {
	final int e = end - 1;
	for (int i = start; i < e; i++) {
	    int temp = i;
	    for (int j = i + 1; j <= e; j++) {
		if (compare(data[temp], data[j]) > 0) {
		    temp = j;
		}
	    }

	    if (i != temp) {
		swap(i, temp);
	    }
	}
	sortedLength += end - start;
    }

    /**
     * 無遞迴的快速排序法。
     *
     * @param start 排序起點位置
     * @param end 排序終點位置
     */
    private void quickSortIterative(final int start, final int end) {
	final int[] stack = new int[end - start + 1]; // 建立堆疊空間
	int top = -1;
	int s, e;
	stack[++top] = start;
	stack[++top] = end - 1;
	while (top >= 0) {
	    int tempSortedLength = this.sortedLength;
	    e = stack[top--];
	    s = stack[top--];
	    tempSortedLength += (e - s + 1);
	    // 採用random pivot
	    swap(random(s, e), e); // 先將random出來的pivot與最右邊交換。
	    // Partition
	    final T x = data[e];
	    int i = s - 1;
	    for (int j = s; j < e; j++) {
		if (compare(data[j], x) < 0) {
		    i++;
		    swap(i, j);
		}
	    }

	    final int p = i + 1; // 新的中間點
	    swap(p, e);
	    // divide
	    final int ls = s, le = p - 1;
	    final int rs = p + 1, re = e;
	    final int ll = le - ls + 1, rl = re - rs + 1;
	    if (ll > 1) {
		tempSortedLength -= ll;
	    }
	    if (rl > 1) {
		tempSortedLength -= rl;
	    }
	    this.sortedLength = tempSortedLength;
	    if (ll > 7) {
		stack[++top] = ls;
		stack[++top] = le;
	    } else if (ll > 1) {
		sellectionSort(ls, le + 1);
	    }
	    if (rl > 7) {
		stack[++top] = rs;
		stack[++top] = re;
	    } else if (rl > 1) {
		sellectionSort(rs, re + 1);
	    }
	}
    }

    /**
     * 在某範圍內取得隨機的值。
     *
     * @param min 最小值
     * @param max 最大值
     * @return min~max中的隨機整數
     */
    private int random(final int min, final int max) {
	final int l = max - min + 1;
	return (int) (l * Math.random()) + min;
    }

    /**
     * 交換索引a,b的資料。
     *
     * @param a 索引a
     * @param b 索引b
     */
    private void swap(final int a, final int b) {
	final T tmp = data[a];
	data[a] = data[b];
	data[b] = tmp;
    }

    /**
     * 排序。
     *
     * @param start 排序起點位置
     * @param end 排序終點位置
     */
    public synchronized void sort(final int start, final int end) {
	sorting = true;
	if (isClonable()) { //如果排序前要先複製
	    data = data.clone();  //複製陣列
	}
	if (end > start) { //如果需要排序
	    sortsLength = end - start;
	    sortedLength = 0;
	    quickSortIterative(start, end);
	} else {
	    sortsLength = 1;
	    sortedLength = 1;
	}
	sorting = false;
	if (callback != null) {
	    callback.sortFinished(data); //回呼
	}
    }

    /**
     * 排序。
     */
    public void sort() {
	sort(0, data.length);
    }

    /**
     * 排序比較方式，若要改寫排序方式，請使用setComparator方法。
     *
     * @param data1 資料1
     * @param data2 資料2
     * @return 大於0，表示data1比data2大；等於0，表示data1和data2一樣大；小於0，表示data1比data2小
     */
    public final int compare(final T data1, final T data2) {
	return comparator.compare(data1, data2);
    }

    /**
     * 取得排序進度。
     *
     * @return 傳回進度
     */
    public double getProgress() {
	return (sortedLength * 1.0 / sortsLength);
    }

    /**
     * 取得資料陣列。
     *
     * @return 傳回資料陣列。
     */
    @Override
    public String toString() {
	return java.util.Arrays.toString(data);
    }

    /**
     * 回呼介面。
     *
     * @param <E> 資料型態
     */
    public static interface MagicSortCallback<E> {

	/**
	 * 排序完成。
	 *
	 * @param data 傳入排序結果
	 */
	public void sortFinished(final E[] data);
    }
}
