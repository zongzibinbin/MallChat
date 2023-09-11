package com.abin.frequencycontrol.service.frequencycontrol;

import java.util.function.Supplier;

    public class GenericMethodWithGenericClass<T> {

        public <E> T get(E value, Supplier<T> supplier) throws Throwable {
            if (value == null) {
                throw new Exception("Error");
            }
            try {
                return supplier.get();
            } finally {
                // 不管成功还是失败，都增加次数
                System.out.println("execute");
            }
        }

        // 泛型方法
        public <E> void printArray(E[] array) {
            for (E item : array) {
                System.out.println(item);
            }
        }


        public static void main(String[] args) {
            GenericMethodWithGenericClass<Double> example = new GenericMethodWithGenericClass<>();

            Integer[] intArray = {1, 2, 3, 4, 5};
            String[] stringArray = {"Hello", "World"};

            example.printArray(intArray);
            example.printArray(stringArray);

            try {
                System.out.println(example.get("hello", Math::random));
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }
