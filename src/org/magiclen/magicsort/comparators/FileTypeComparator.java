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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLConnection;
import java.util.Comparator;
import java.util.HashMap;

/**
 * 排序檔案，依照檔案類型。
 *
 * @author Magic Len
 */
public class FileTypeComparator implements Comparator<File> {

    // -----類別類別-----
    /**
     * 檔案資訊類別。
     */
    private static class Information {

	boolean isDirctory;
	String extendedName = null;
	String contentType = null;
    }

    // -----類別方法-----
    /**
     * 取得比較器的實體。
     *
     * @return 傳回比較器的實體
     */
    public static synchronized FileTypeComparator getInstance() {
	return new FileTypeComparator();
    }

    // -----物件常數-----
    /**
     * 儲存含有數字的字串比較器。
     */
    private final StringWithNumberComparator stringWithNumberComparator;
    /**
     * 儲存檔案的資訊。
     */
    private final HashMap<Integer, Information> informationMap = new HashMap<>();

    // -----建構子-----
    /**
     * 私有建構子，無法直接使用new運算子來實體化。
     */
    private FileTypeComparator() {
	stringWithNumberComparator = StringWithNumberComparator.getInstance();
    }

    // -----物件方法-----
    @Override
    public int compare(final File file1, final File file2) {
	final Information i1 = getInformation(file1), i2 = getInformation(file2);

	// 判斷目錄
	if (i1.isDirctory) {
	    if (i2.isDirctory) {
		return 0;
	    } else {
		return -1;
	    }
	} else {
	    if (i2.isDirctory) {
		return 1;
	    }
	}

	// 如果都是檔案
	// 判斷副檔名
	final String e1 = getFileExtendedName(file1, i1), e2 = getFileExtendedName(file2, i2);
	final int e1Length = e1.length(), e2Length = e2.length();
	if (e1Length == 0) {
	    if (e2Length > 0) {
		return -1;
	    }
	} else {
	    if (e2Length == 0) {
		return 1;
	    } else {
		return stringWithNumberComparator.compare(e1, e2);
	    }
	}
	// 判斷內容類型
	final String mt1 = getFileContentType(file1, i1), mt2 = getFileContentType(file2, i2);
	final int mt1Length = mt1.length(), mt2Length = mt2.length();
	if (mt1Length == 0) {
	    if (mt2Length > 0) {
		return -1;
	    }
	} else {
	    if (mt2Length == 0) {
		return 1;
	    }
	}

	return 0;
    }

    /**
     * 取得檔案資訊。
     *
     * @param file 傳入檔案
     * @return 傳回檔案資訊
     */
    private Information getInformation(final File file) {
	final Integer hashCode = file.hashCode();
	if (informationMap.containsKey(hashCode)) {
	    return informationMap.get(hashCode);
	} else {
	    final Information i = new Information();
	    i.isDirctory = file.isDirectory();
	    informationMap.put(hashCode, i);
	    return i;
	}
    }

    /**
     * 取得檔案副檔名。
     *
     * @param file 傳入檔案
     * @param information 傳入檔案資訊
     * @return 傳回檔案副檔名
     */
    private String getFileExtendedName(final File file, final Information information) {
	if (information.extendedName != null) {
	    return information.extendedName;
	} else {
	    final String name = file.getName();
	    final int index = name.lastIndexOf('.');
	    final String extendedName;
	    if (index > 0) {
		extendedName = name.substring(index + 1);
	    } else {
		extendedName = "";
	    }
	    information.extendedName = extendedName;
	    return extendedName;
	}
    }

    /**
     * 取得檔案內容類型。
     *
     * @param file 傳入檔案
     * @param information 傳入檔案資訊
     * @return 傳回檔案的內容類型
     */
    private String getFileContentType(final File file, final Information information) {
	if (information.contentType != null) {
	    return information.contentType;
	} else {
	    String type = URLConnection.guessContentTypeFromName(file.getName());
	    if (type == null) {
		try {
		    try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
			type = URLConnection.guessContentTypeFromStream(bis);
		    }
		} catch (final Exception ex) {

		}
	    }
	    if (type == null) {
		type = "";
	    }
	    information.contentType = type;
	    return type;
	}
    }
}
