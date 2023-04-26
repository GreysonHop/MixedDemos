package testJava.generics;

/**
 * Create by Greyson on 2023/04/21
 */
class TestGenerics {

    public static void main(String[] args) {

        System.out.println("maxInverse=" + "\n" + (~5) + " | " + (-4 >> 1) + " | " + (-127 >> 1));

        byte b1 = Byte.MAX_VALUE;
        byte b2 = -Byte.MAX_VALUE;
        byte b3 = ~(-Byte.MAX_VALUE);
        byte b4 = -0;
        byte b5 = 0;
        System.out.println("true: " + (b4 + 1) + " | " + (b5 + 1) + "\n" + b1 + " | " + b2 + " | " + b3 + " | " + (8 ^ -127));


        // 没有泛型的话：
        NonGenericContainer ngc = new NonGenericContainer();
        ngc.setData("123");
        //ngc.setData(321);

        String s = (String) ngc.getData();
        int i = (Integer) ngc.getData();
        System.out.println("s=" + s);
    }

    final static class NonGenericContainer {
        private Object data;

        public Object getData() {
            return data;
        }

        void setData(Object newData) {
            this.data = newData;
        }
    }

    final static class GenericContainer<T> {
        private T data;

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }

    class GenericObject {


        /*void <T> printThing(T obj) {
            System.out.println("");
            int max = 10_00_00;
        }*/


    }

   /* interface GenericInterface {
        void printThing(T obj);
    }*/

}
