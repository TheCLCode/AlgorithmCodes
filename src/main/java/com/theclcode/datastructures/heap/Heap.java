package com.theclcode.datastructures.heap;

import java.util.Arrays;

//Unfinished
public class Heap {

    public static void main(String[] args) {
        int[] newArr = {15,13,1,2,5,13,12,10,9,7,9,8,4,4,3,2,0,1,7,-32, 32,-1, 4, 5,9, 10, 12, -12, -12, -11, 40};
        for(int i=0; i<newArr.length; i++){
            insert(newArr, i);
        }
        System.out.println(Arrays.toString(newArr));
        int l = newArr.length-1;
        while(l > 0){
            delete(newArr, l--);
        }
        System.out.println(Arrays.toString(newArr));

    }

    static void insert(int[] arr, int index){
        int parent = ((index+1)/2)-1;
        while(parent >= 0 && arr[index] > arr[parent]){
            swap(arr, index, parent);
            index = parent;
            parent = ((index+1)/2)-1;
        }
    }

    static void delete(int[] arr, int lastIndex){
        swap(arr, 0, lastIndex);
        int index = 0;
        int left = ((index+1)*2)-1;
        int right = ((index+1)*2+1)-1;
        while(left < lastIndex || right < lastIndex){
            if(left < lastIndex && right < lastIndex){
                int max = arr[left] >= arr[right] ? left : right;
                if(arr[index] >= arr[max]){
                    break;
                }
                swap(arr, max, index);;
                index = max;

            } else if(left < lastIndex && arr[left] > arr[index]){
                swap(arr, left, index);
                index = left;
            } else if(right < lastIndex && arr[right] > arr[index]){
                swap(arr, right, index);
                index = right;
            } else {
                break;
            }
            left = ((index+1)*2)-1;
            right = ((index+1)*2+1)-1;
        }

    }

    static void swap(int[] arr, int left, int right){
        int temp = arr[left];
        arr[left] = arr[right];
        arr[right] = temp;
    }
}