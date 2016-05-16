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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>
 * 排序類別。特色如下：
 * </p>
 * <ol>
 * <li>使用setCompare方法可以自行決定排序的方式。</li>
 * <li>繼承MagicSort.MagicSortCallback介面可以非同步的方式處理排序，實作sortFinished方法決定排序完成後的行為。</li>
 * <li>可以使用setAlgorithm演算法來改變要使用的排序演算法。</li>
 * <li>在排序過程中還可用getProgress來取得排序進度。</li>
 * </ol>
 *
 *
 * @author Magic Len
 *
 * @param <T> 要排序的類別。
 */
public final class MagicSort<T> {

    // -----類別常數-----
    /**
     * 執行緒的數量。
     */
    private static final int MAX_THREAD = Runtime.getRuntime().availableProcessors();

    // -----類別方法-----
    /**
     * 印出陣列所有元素。
     *
     * @param <T> 陣列的型態
     * @param array 傳入陣列
     */
    public static <T> void printArray(final T[] array) {
        printArray(array, 0, array.length - 1);
    }

    /**
     * 印出陣列的指定範圍元素。
     *
     * @param <T> 陣列的型態
     * @param array 傳入陣列
     * @param start 傳入開始索引位置
     * @param end 傳入結束索引位置
     */
    public static <T> void printArray(final T[] array, final int start, final int end) {
        final int length = array.length;
        if (start < 0 || end > length || start >= end) {
            System.out.println("[]");
        } else {
            final StringBuilder sb = new StringBuilder("[");
            for (int i = start; i < end; ++i) {
                sb.append(array[i].toString()).append(", ");
            }
            sb.append(array[end].toString());
            sb.append("]");
            System.out.println(sb.toString());
        }
    }

    /**
     * 指定演算法測試其正確性和排序速度。
     *
     * @param sort 傳入MagicSort物件
     * @param data 傳入要排序的陣列
     * @param algorithm 傳入排序時要使用的演算法
     * @return 傳回排序是否成功
     */
    private static boolean test(final MagicSort sort, final Object[] data, final Algorithm algorithm) {
        System.out.println("測試".concat(algorithm.toString()));
        sort.setData(data);
        sort.setAlgorithm(algorithm);
        final long startTime = System.currentTimeMillis();
        sort.sort();
        final long endTime = System.currentTimeMillis();
        final boolean sorted = sort.isSorted();
        System.out.println(sorted + " " + (endTime - startTime) + " ms\n");
        return sorted;
    }

    /**
     * 自我測試程式，會產生亂數陣列，測試每個演算法的正確性和速度。
     */
    private static void autoTestMyself() {
        System.out.println("產生亂數陣列中......\n");
        Double[] array = new Double[75000];
        for (int i = 0; i < array.length; ++i) {
            array[i] = Math.random();
        }

        final MagicSort<Double> sort = new MagicSort<>(true);

        // 先排序，優化記憶體
        sort.setData(array);
        sort.sort();

        test(sort, array, Algorithm.SELECTIONSORT);
        test(sort, array, Algorithm.INSERTIONSORT);
        test(sort, array, Algorithm.BUBBLESORT);
        test(sort, array, Algorithm.BUBBLESORT_OPTIMIZED);
        test(sort, array, Algorithm.EXCHANGESORT);
        test(sort, array, Algorithm.MERGESORT);
        test(sort, array, Algorithm.QUICKSORT_OPTIMIZED);
        test(sort, array, Algorithm.QUICKSORT_MULTITHREAD);
        test(sort, array, Algorithm.QUICKSORT);

        System.out.println("反序陣列產生中......\n");
        final Double[] sorted = sort.getData();
        for (int i = 0; i < array.length; ++i) {
            array[i] = sorted[array.length - i - 1];
        }

        test(sort, array, Algorithm.SELECTIONSORT);
        test(sort, array, Algorithm.INSERTIONSORT);
        test(sort, array, Algorithm.BUBBLESORT);
        test(sort, array, Algorithm.BUBBLESORT_OPTIMIZED);
        test(sort, array, Algorithm.EXCHANGESORT);
        test(sort, array, Algorithm.MERGESORT);
        test(sort, array, Algorithm.QUICKSORT_OPTIMIZED);
        test(sort, array, Algorithm.QUICKSORT_MULTITHREAD);
        test(sort, array, Algorithm.QUICKSORT);

        System.out.println("正序陣列產生中......\n");

        array = sort.getData();

        test(sort, array, Algorithm.SELECTIONSORT);
        test(sort, array, Algorithm.INSERTIONSORT);
        test(sort, array, Algorithm.BUBBLESORT);
        test(sort, array, Algorithm.BUBBLESORT_OPTIMIZED);
        test(sort, array, Algorithm.EXCHANGESORT);
        test(sort, array, Algorithm.MERGESORT);
        test(sort, array, Algorithm.QUICKSORT_OPTIMIZED);
        test(sort, array, Algorithm.QUICKSORT_MULTITHREAD);
        test(sort, array, Algorithm.QUICKSORT);
    }

    /**
     * 程式進入點，自動執行自我測試程式。
     *
     * @param args 不需傳入參數
     */
    public static void main(final String[] args) {
        autoTestMyself();
    }

    // -----類別列舉-----
    /**
     * 排序時使用的演算法。
     */
    public static enum Algorithm {
        /**
         * 多執行緒、無遞迴的快速排序法，在多執行緒的機器下可以發揮很大的效用。
         */
        QUICKSORT_MULTITHREAD,
        /**
         * 改良版、無遞迴的快速排序法，採用隨機pivot並在元素數量不超過7個時使用選擇排序法，在大多數的案例中都可以有很好的成效，為預設排序法。
         */
        QUICKSORT_OPTIMIZED,
        /**
         * 無遞迴的快速排序法，在各方面表現普通。
         */
        QUICKSORT,
        /**
         * 選擇排序法，在大多數的案例中，為O(n<sup>2</sup>)排序演算法中最快的一個。
         */
        SELECTIONSORT,
        /**
         * 氣泡排序法，極適合用在幾乎確定陣列已經排序完成的案例。
         */
        BUBBLESORT,
        /**
         * 改良版的氣泡排序法，為雙路版本，可由外而內將擠壓陣列元素完成排列，極適合用在幾乎確定陣列已經排序完成的案例。
         */
        BUBBLESORT_OPTIMIZED,
        /**
         * 交換排序法，大多數的案例中，為O(n<sup>2</sup>)排序演算法中最慢的一個。
         */
        EXCHANGESORT,
        /**
         * 插入排序法，在大多數的案例中，為O(n<sup>2</sup>)排序演算法中第二最快的，同時也適合用在幾乎確定陣列已經排序完成的案例。
         */
        INSERTIONSORT,
        /**
         * 合併排序法，在所有案例中都有好的成效。
         */
        MERGESORT
    }

    // -----物件變數-----
    private T[] data; //資料
    private boolean clonable; //排序前是否先複製資料(若為true，排序將不會動到原始的資料順序，排序結果需用getData來取得)
    private AtomicInteger sortedLength = new AtomicInteger(); //儲存已排序的長度
    private int sortsLength; //儲存總共要排序的長度
    private boolean sorting; //儲存是否正在排序中
    private ExecutorService executorService; // 執行緒池
    private Algorithm algorithm = Algorithm.QUICKSORT_OPTIMIZED; //儲存排序時使用的演算法
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
     * 設定排序演算法。
     *
     * @param algorithm 傳入排序演算法，若為null，則使用預設值
     */
    public synchronized void setAlgorithm(final Algorithm algorithm) {
        if (algorithm == null) {
            this.algorithm = Algorithm.QUICKSORT_OPTIMIZED;
        } else {
            this.algorithm = algorithm;

        }
    }

    /**
     * 取得排序演算法。
     *
     * @return 傳回排序演算法
     */
    public Algorithm getAlgorithm() {
        return this.algorithm;
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
     * 是否已經排序完畢。
     *
     * @return 傳回陣列是否已經排序完畢。
     */
    public synchronized boolean isSorted() {
        final int length = data.length;
        for (int i = 1; i < length; ++i) {
            final T a = data[i - 1];
            final T b = data[i];
            if (compare(a, b) > 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * 合併排序法，在所有案例中都有好的成效。
     *
     * @param start 排序起點位置
     * @param end 排序終點位置
     */
    private void mergeSort(final int start, final int end) {
        final int baseSortedLength = sortedLength.get();
        final int length = end - start;
        final T[] temp = data.clone();

        for (int i = 1; i < length; i *= 2) {
            final int ii = i * 2;
            final int je = end - i;
            for (int j = start; j < je; j += ii) {
                int e = j + ii;
                if (e > end) {
                    e = end;
                }
                int ls = j;
                int rs = ls + i;
                int r = rs;
                int k = j;
                while (ls < rs && r < e) {
                    final int c = compare(temp[ls], temp[r]);
                    if (c > 0) {
                        data[k++] = temp[r++];
                    } else if (c < 0) {
                        data[k++] = temp[ls++];
                    } else {
                        ++k;
                        ++ls;
                    }
                }
                while (ls < rs) {
                    data[k++] = temp[ls++];
                }
                while (r < e) {
                    data[k++] = temp[r++];
                }
                System.arraycopy(data, j, temp, j, e - j);
            }
            sortedLength.set(baseSortedLength + i);
        }
        sortedLength.set(baseSortedLength + end - start);
    }

    /**
     * 插入排序法，在大多數的案例中，為O(n<sup>2</sup>)排序演算法中第二最快的，同時也適合用在幾乎確定陣列已經排序完成的案例。
     *
     * @param start 排序起點位置
     * @param end 排序終點位置
     */
    private void insertionSort(final int start, final int end) {
        final int baseSortedLength = sortedLength.get();
        for (int i = start + 1; i < end; ++i) {
            final T temp = data[i];
            int j = i - 1;
            while (j >= 0 && compare(data[j], temp) > 0) {
                data[j + 1] = data[j--];
            }
            data[j + 1] = temp;
            sortedLength.incrementAndGet();
        }
        sortedLength.set(baseSortedLength + end - start);
    }

    /**
     * 交換排序法，大多數的案例中，為O(n<sup>2</sup>)排序演算法中最慢的一個。
     *
     * @param start 排序起點位置
     * @param end 排序終點位置
     */
    private void exchangeSort(final int start, final int end) {
        final int baseSortedLength = sortedLength.get();
        final int e = end - 1;
        for (int i = start; i < e; ++i) {
            for (int j = i + 1; j <= e; ++j) {
                if (compare(data[i], data[j]) > 0) {
                    swap(i, j);
                }
            }
            sortedLength.incrementAndGet();
        }
        sortedLength.set(baseSortedLength + end - start);
    }

    /**
     * 氣泡排序法，雙路版本。
     *
     * @param start 排序起點位置
     * @param end 排序終點位置
     */
    private void bubbleSortOptimized(final int start, final int end) {
        final int baseSortedLength = sortedLength.get();

        final int e = end - 1;
        int i = 0;
        while (true) {
            boolean sorted = true;
            final int ee = e - i;
            for (int j = i; j < ee; ++j) {
                final int jj = j + 1;
                if (compare(data[j], data[jj]) > 0) {
                    swap(j, jj);
                    sorted = false;
                }
            }
            if (sorted) {
                break;
            }
            sorted = true;
            for (int j = ee - 1; j > i; --j) {
                final int jj = j - 1;
                if (compare(data[j], data[jj]) < 0) {
                    swap(j, jj);
                    sorted = false;
                }
            }
            if (sorted) {
                break;
            }
            ++i;
            sortedLength.incrementAndGet();
        }
        sortedLength.set(baseSortedLength + end - start);
    }

    /**
     * 氣泡排序法，極適合用在幾乎確定陣列已經排序完成的案例。
     *
     * @param start 排序起點位置
     * @param end 排序終點位置
     */
    private void bubbleSort(final int start, final int end) {
        final int baseSortedLength = sortedLength.get();
        for (int i = end - 1; i > 0; --i) {
            boolean sorted = true;
            for (int j = 0; j < i; ++j) {
                final int jj = j + 1;
                if (compare(data[j], data[jj]) > 0) {
                    swap(j, jj);
                    sorted = false;
                }
            }
            if (sorted) {
                break;
            }
            sortedLength.incrementAndGet();
        }
        sortedLength.set(baseSortedLength + end - start);
    }

    /**
     * 選擇排序法，在大多數的案例中，為O(n<sup>2</sup>)排序演算法中最快的一個。
     *
     * @param start 排序起點位置
     * @param end 排序終點位置
     */
    private void sellectionSort(final int start, final int end) {
        final int baseSortedLength = sortedLength.get();
        final int e = end - 1;
        for (int i = start; i < e; ++i) {
            int temp = i;
            for (int j = i + 1; j <= e; ++j) {
                if (compare(data[temp], data[j]) > 0) {
                    temp = j;
                }
            }

            if (i != temp) {
                swap(i, temp);
                sortedLength.set(baseSortedLength + i - start + 1);
            }
        }
        sortedLength.set(baseSortedLength + end - start);
    }

    /**
     * 多執行緒、無遞迴的快速排序法，在多執行緒的機器下可以發揮很大的效用。
     *
     * @param start 排序起點位置
     * @param end 排序終點位置
     */
    private void quickSortMultiThread(final int start, final int end) {
        int s = start, e = end - 1;
        int tempSortedLength = (e - s + 1);
        final T x = data[s]; // pivot
        int l = s + 1;
        int r = e;
        while (true) {
            while (r > s && compare(data[r], x) >= 0) {
                --r;
            }
            while (l <= r && compare(data[l], x) <= 0) {
                ++l;
            }
            if (l < r) {
                swap(l, r);
            } else {
                if (r > s) {
                    swap(r, s);
                }
                break;
            }
        }
        final int ls = s, le = r - 1;
        final int rs = r + 1, re = e;
        final int ll = le - ls + 1, rl = re - rs + 1;
        if (ll > 1) {
            tempSortedLength -= ll;
        }
        if (rl > 1) {
            tempSortedLength -= rl;
        }
        if (ll > 1) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    quickSortMultiThread(ls, le + 1);
                }
            });
        }
        if (rl > 1) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    quickSortMultiThread(rs, re + 1);
                }
            });
        }
        if (this.sortedLength.addAndGet(tempSortedLength) == data.length) {
            executorService.shutdown();
        }
    }

    /**
     * 無遞迴的快速排序法，在各方面表現普通。
     *
     * @param start 排序起點位置
     * @param end 排序終點位置
     */
    private void quickSort(final int start, final int end) {
        final int[] stack = new int[end - start]; // 建立堆疊空間
        int top = -1;
        int s, e;
        stack[++top] = start;
        stack[++top] = end - 1;
        while (top >= 0) {
            int tempSortedLength = this.sortedLength.get();
            e = stack[top--];
            s = stack[top--];
            tempSortedLength += (e - s + 1);
            final T x = data[s]; // pivot
            int l = s + 1;
            int r = e;
            while (true) {
                while (r > s && compare(data[r], x) >= 0) {
                    --r;
                }
                while (l <= r && compare(data[l], x) <= 0) {
                    ++l;
                }
                if (l < r) {
                    swap(l, r);
                } else {
                    if (r > s) {
                        swap(r, s);
                    }
                    break;
                }
            }

            final int ls = s, le = r - 1;
            final int rs = r + 1, re = e;
            final int ll = le - ls + 1, rl = re - rs + 1;
            if (ll > 1) {
                tempSortedLength -= ll;
            }
            if (rl > 1) {
                tempSortedLength -= rl;
            }
            this.sortedLength.set(tempSortedLength);
            if (ll > 1) {
                stack[++top] = ls;
                stack[++top] = le;
            }
            if (rl > 1) {
                stack[++top] = rs;
                stack[++top] = re;
            }
        }
    }

    /**
     * 改良版、無遞迴的快速排序法，採用隨機pivot並在元素數量不超過7個時使用選擇排序法，在大多數的案例中都可以有很好的成效，為預設排序法。
     *
     * @param start 排序起點位置
     * @param end 排序終點位置
     */
    private void quickSortOptimized(final int start, final int end) {
        final int[] stack = new int[end - start]; // 建立堆疊空間
        int top = -1;
        int s, e;
        stack[++top] = start;
        stack[++top] = end - 1;
        while (top >= 0) {
            int tempSortedLength = this.sortedLength.get();
            e = stack[top--];
            s = stack[top--];
            tempSortedLength += (e - s + 1);
            // 採用random pivot
            swap(random(s, e), s); // 先將random出來的pivot與最左邊交換
            final T x = data[s]; // pivot
            int l = s + 1;
            int r = e;
            while (true) {
                while (r > s && compare(data[r], x) >= 0) {
                    --r;
                }
                while (l <= r && compare(data[l], x) <= 0) {
                    ++l;
                }
                if (l < r) {
                    swap(l, r);
                } else {
                    if (r > s) {
                        swap(r, s);
                    }
                    break;
                }
            }

            final int ls = s, le = r - 1;
            final int rs = r + 1, re = e;
            final int ll = le - ls + 1, rl = re - rs + 1;
            if (ll > 1) {
                tempSortedLength -= ll;
            }
            if (rl > 1) {
                tempSortedLength -= rl;
            }
            this.sortedLength.set(tempSortedLength);
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
            sortedLength.set(0);
            switch (algorithm) {
                case QUICKSORT_MULTITHREAD:
                    executorService = Executors.newFixedThreadPool(MAX_THREAD);
                    quickSortMultiThread(start, end);
                    try {
                        executorService.awaitTermination(7, TimeUnit.DAYS);
                    } catch (final InterruptedException ex) {
                        throw new RuntimeException(ex);
                    }
                    break;
                case QUICKSORT_OPTIMIZED:
                    quickSortOptimized(start, end);
                    break;
                case QUICKSORT:
                    quickSort(start, end);
                    break;
                case SELECTIONSORT:
                    sellectionSort(start, end);
                    break;
                case BUBBLESORT:
                    bubbleSort(start, end);
                    break;
                case BUBBLESORT_OPTIMIZED:
                    bubbleSortOptimized(start, end);
                    break;
                case EXCHANGESORT:
                    exchangeSort(start, end);
                    break;
                case INSERTIONSORT:
                    insertionSort(start, end);
                    break;
                case MERGESORT:
                    mergeSort(start, end);
                    break;
            }
        } else {
            sortsLength = 1;
            sortedLength.set(1);
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
        return (sortedLength.get() * 1.0 / sortsLength);
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
