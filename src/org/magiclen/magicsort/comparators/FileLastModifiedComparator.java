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

import java.io.File;
import java.util.Comparator;

/**
 * 排序檔案，依照檔案最後修改時間。
 *
 * @author Magic Len
 */
public class FileLastModifiedComparator implements Comparator<File> {

    // -----類別變數-----
    /**
     * 儲存比較器的實體。
     */
    private static FileLastModifiedComparator comparator;

    // -----類別方法-----
    /**
     * 取得比較器的實體。
     *
     * @return 傳回比較器的實體
     */
    public static synchronized FileLastModifiedComparator getInstance() {
	if (comparator == null) {
	    comparator = new FileLastModifiedComparator();
	}
	return comparator;
    }

    // -----建構子-----
    /**
     * 私有建構子，無法直接使用new運算子來實體化。
     */
    private FileLastModifiedComparator() {

    }

    // -----物件方法-----
    @Override
    public int compare(final File file1, final File file2) {
	final long l1 = file1.lastModified(), l2 = file2.lastModified();
	if (l1 < l2) {
	    return -1;
	} else if (l1 == l2) {
	    return 0;
	}
	return 1;
    }

}
