package com.theclcode.datastructures.heap;

import java.util.Arrays;

//Unfinished
public class Heap {

    public static void main(String[] args) {
        int[] newArr = {13, 2, 1, 5, 3, 14, 6};
        for(int i=0; i<newArr.length; i++){
            insert(newArr, i);
        }
        delete(newArr, newArr.length-1);
        System.out.println(Arrays.toString(newArr));

    }

    static void insert(int arr[], int index){
        int value = arr[index];
        int parent = index/2;
        if(index > 0 && value > arr[parent]){
            swap(arr, index, parent );
            insert(arr, parent);
        } else {
            arr[index] = value;
        }

    }

    //@TODO
    static int delete(int[] arr, int index){
        int removed = arr[0];
        int value = arr[index];
        swap(arr, 0, index);
        replace(arr, 0);
        return removed;

    }

    static void replace(int[] arr, int index){
        if(index < arr.length){
            int value = arr[index];
            int left = arr[index+1];
            int right = arr[index+2];

            if(value <= left || value <= right){
                if(left >= right){
                    swap(arr, index, index+1);
                    replace(arr, index+1);
                } else {
                    swap(arr, index, index+2);
                    replace(arr, index+2);
                }
            }
        }
    }

    static void swap(int[] arr, int left, int right){
        int temp = arr[left];
        arr[left] = arr[right];
        arr[right] = temp;
    }
}
