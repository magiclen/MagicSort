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
package org.magiclen.magicsort.comparators;

import java.util.Comparator;

/**
 * 排序包含數字的字串。
 *
 * @author Magic Len
 */
public class StringWithNumberComparator implements Comparator<String> {

    // -----類別變數-----
    /**
     * 儲存比較器的實體。
     */
    private static StringWithNumberComparator comparator;

    // -----類別方法-----
    /**
     * 取得比較器的實體。
     *
     * @return 傳回比較器的實體
     */
    public static synchronized StringWithNumberComparator getInstance() {
	if (comparator == null) {
	    comparator = new StringWithNumberComparator();
	}
	return comparator;
    }

    // -----建構子-----
    /**
     * 儲存含有數字的字串比較器。
     */
    private StringWithNumberComparator() {
    }

    // -----物件方法-----
    @Override
    public int compare(final String str1, final String str2) {
	char[] c1 = str1.toCharArray(), c2 = str2.toCharArray(); // 將字串str1和str2轉成字元陣列c1,c2
	final int l1 = c1.length, l2 = c2.length; // 取得str1和str2字串的長度
	int p1 = 0, p2 = 0; // c1，c2目前讀取到的位置
	while (p1 < l1 && p2 < l2) { // 如果c1和c2都還沒讀取完
	    char ca = c1[p1++]; // 取得目前c1讀取到的字元
	    char cb = c2[p2++]; // 取得目前c2讀取到的字元
	    // 判斷ca、cb是否都是數值
	    if (ca >= '0' && ca <= '9' && cb >= '0' && cb <= '9') {
		// 若兩個都是數值
		double da = ca - '0'; // 字元轉成數值
		double db = cb - '0'; // 字元轉成數值
		while (p1 < l1) { // 繼續讀取c1，計算整個數字的數值是多少
		    ca = c1[p1];
		    if (ca >= '0' && ca <= '9') { // 如果下一個字元還是數字
			da = da * 10 + ca - '0'; // 重新計算數值
			++p1;
		    } else {
			break; // 跳出迴圈
		    }
		}
		while (p2 < l2) { // 繼續讀取c2，計算整個數字的數值是多少
		    cb = c2[p2];
		    if (cb >= '0' && cb <= '9') { // 如果下一個字元還是數字
			db = db * 10 + cb - '0'; // 重新計算數值
			++p2;
		    } else {
			break; // 跳出迴圈
		    }
		}

		final double d = da - db; // 計算數值的差
		// 如果數值不相等，就回傳1或-1
		if (Math.abs(d) > 0.1) {
		    return d > 0 ? 1 : -1;
		}
		// 如果數值相等，就繼續判斷下去
	    } else {
		// 若兩個不都是數值
		// 如果字元值不相等
		if (ca != cb) {
		    // 判斷是否為全形字
		    if (ca > 255 ^ cb > 255) { // 如果ca或cb是全形字
			return cb - ca;
		    } else {
			return ca - cb;
		    }
		}
		// 如果字元值相等，就繼續判斷下去
	    }
	}
	// 如果c1，c2之中有任何一個讀取完了
	final int d = p1 - p2; // 計算它們最後的讀取位置差
	if (d < 0) {
	    return -1;
	} else if (d > 0) {
	    return 1;
	} else {
	    // 比較字串長度
	    return l1 - l2;
	}
    }
}
