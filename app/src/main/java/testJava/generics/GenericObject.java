package testJava.generics;

import testJava.commonClass.Animal;

public class GenericObject {


        public <T extends Animal & Comparable<Integer>> void printThing(T obj) {
            System.out.println("");
            int max = 10_00_00;
        }


}