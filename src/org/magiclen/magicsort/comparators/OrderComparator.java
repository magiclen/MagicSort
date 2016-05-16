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
 * 順序比較器，可以按照順序使用不同的比較器來排序物件。
 *
 * @author Magic Len
 * @param <T> 要排序的類別。
 */
public class OrderComparator<T> implements Comparator<T> {

    // -----類別方法-----
    /**
     * 取得比較器的實體。
     *
     * @param <T> 要排序的類別。
     * @param comparators 按照順序傳入要使用的比較器
     * @return 傳回比較器的實體
     */
    public static synchronized <T> OrderComparator<T> getInstance(final Comparator<T>... comparators) {
	return new OrderComparator<>(comparators);
    }

    // -----物件常數-----
    /**
     * 儲存要使用的比較器。
     */
    private final Comparator<T>[] comparators;

    // -----建構子-----
    /**
     * 私有建構子，無法直接使用new運算子來實體化。
     *
     * @param comparator 傳入要反轉的比較器
     */
    private OrderComparator(final Comparator<T>... comparators) {
	this.comparators = comparators;
    }

    // -----物件方法-----
    @Override
    public int compare(final T t1, final T t2) {
	for (final Comparator comparator : comparators) {
	    final int compare = comparator.compare(t1, t2);
	    if (compare < 0) {
		return -1;
	    } else if (compare > 0) {
		return 1;
	    }
	}
	return 0;
    }
}
