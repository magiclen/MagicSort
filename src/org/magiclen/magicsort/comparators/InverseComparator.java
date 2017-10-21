/*
 *
 * Copyright 2015-2017 magiclen.org
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
 * 反轉比較器，用來反轉比較器的排序結果。
 *
 * @author Magic Len
 * @param <T> 要排序的類別。
 */
public class InverseComparator<T> implements Comparator<T> {

    // -----類別方法-----
    /**
     * 取得比較器的實體。
     *
     * @param <T> 要排序的類別。
     * @param comparator 傳入要反轉的比較器
     * @return 傳回比較器的實體
     */
    public static synchronized <T> InverseComparator<T> getInstance(final Comparator<T> comparator) {
	return new InverseComparator<>(comparator);
    }

    // -----物件常數-----
    /**
     * 儲存要反轉的比較器。
     */
    private final Comparator<T> comparator;

    // -----建構子-----
    /**
     * 私有建構子，無法直接使用new運算子來實體化。
     *
     * @param comparator 傳入要反轉的比較器
     */
    private InverseComparator(final Comparator<T> comparator) {
	this.comparator = comparator;
    }

    // -----物件方法-----
    @Override
    public int compare(final T t1, final T t2) {
	// 乘上-1反轉
	return -1 * comparator.compare(t1, t2);
    }
}
