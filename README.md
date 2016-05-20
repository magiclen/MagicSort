MagicSort
=================================

# Introduction

**MagicSort** is a Java library used for sorting any object array by using iterative quick sort and selection sort when the array size is small. Moreover, it provides some useful built-in comparators for sorting strings and files. It can sort files by file types, but when the file types are the same, sort them by their file names.

It will take a long time to sort large data. **MagicSort** supports asynchronous sorting. You can set you callback object and use a new thread to sort data. **MagicSort** can calculate the progress of its sorting task immediately.

# Usage

## MagicSort Class

**MagicSort** class is in the *org.magiclen.magicsort* package. It can help you sort any object array.

### Initialize

First, make sure you have already let your unsorted data in an object array. Then, you can use `new` operator and pass your array to parameter to create a **MagicSort** instance.

    MagicSort ms = new MagicSort(array);

You can also use generics to define what type of data you want to sort specifically.

    MagicSort<Integer> ms = new MagicSort<>(array);

### Set comparator

In order to sort data, it must exist a comparator to compare every two objects in this array to decide which one is bigger and which one is smaller. The default comparator is natural sort order. If you want to change, you can use **setComparator** method to input your comparator. You can also use the built-in comparators in *org.magiclen.magicsort.comparators* package.

    ms.setComparator(InverseComparator.getInstance(ms.getComparator())); // Inverse current comparator

### Sort data

You can use **sort** method to start your sort task. After the task finishes, you can use **getData** method to get the sorted array.

    ms.sort();
    System.out.println(Arrays.toString(ms.getData()));

If you want to sort data asynchronously, you can use **setCallback** method to add an **MagicSortCallback** object implemented by yourself. implemented. After the sort task finished, the **sortFinished** method in **MagicSortCallback** object that you set will be called.

When the sorting task is running, you can use **getProgress** method to get current progress.

### Implement an example

If we need to sort files mentioned in the first paragraph, we can use **MagicSort** to do that easily.

    final File directory = new File("").getAbsoluteFile();
    final File[] files = directory.listFiles();
    MagicSort<File> ms = new MagicSort<>(files);
    ms.setComparator(OrderComparator.getInstance(FileTypeComparator.getInstance(), FileNameComparator.getInstance()));
    ms.setCallback(data -> {
        Arrays.stream(data).forEach(System.out::println);
    });
    new Thread(ms::sort).start();
    while (true) {
        try {
          Thread.sleep(200);
        } catch (final Exception ex) {

        }
        final double progress = ms.getProgress();
        System.err.printf("%.0f%%%n", progress * 100);
        if (progress == 1) {
          break;
        }
    }

### Use other algorithms

If you are insterested in different sorting algorithms, you can use **setAlgorithm** method to change that. There are some algorithms which have been implemented:

- BUBBLESORT
- BUBBLESORT_OPTIMIZED
- EXCHANGESORT
- INSERTIONSORT
- MERGESORT
- QUICKSORT
- QUICKSORT_MULTITHREAD
- QUICKSORT_OPTIMIZED(default)
- SELECTIONSORT

# License

    Copyright 2015-2016 magiclen.org

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

# What's More?

Please check out our web page at

https://magiclen.org/magicsort/
