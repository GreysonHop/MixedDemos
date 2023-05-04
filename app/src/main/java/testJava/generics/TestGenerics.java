package testJava.generics;

import testJava.commonClass.Animal;
import testJava.commonClass.Aquatic;
import testJava.commonClass.Atmobios;
import testJava.commonClass.Bee;
import testJava.commonClass.Bird;
import testJava.commonClass.Cat;
import testJava.commonClass.Dog;
import testJava.commonClass.Fish;
import testJava.commonClass.Terrestrial;

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


        testAssignment();
        testNormalGenerics();
    }

    /**
     * 泛型的简单测试，泛型容器中数据的取出和放入，放入安全 和 取出安全 的对比。
     */
    private static void testNormalGenerics() {
        // 没有泛型的话：
        NonGenericContainer ngc = new NonGenericContainer();
        ngc.setData(new Cat());
        ngc.setData(new Atmobios()); // 去获取东西就只用自己记住哪种类型是最后被放入容器的，不然取的时候去强制转型容易崩溃
        Fish i1 = (Fish) ngc.getData(); // 不会有任何错误检察，编译时才会报错
        Terrestrial i2 = (Terrestrial) ngc.getData();
        System.out.println("s=" + i1);


        // 有泛型之后，像是有了类型检查机制，类型安全有保障
        GenericContainer<Aquatic> gc = new GenericContainer<>();
//        gc.setData(new Bird()); // 没有边界的泛型用法明确，就只有传泛型类型指定的类及其子类
//        gc.setData(new Animal());
        gc.setData(new Aquatic());
//        Fish i11 = (Fish) gc.getData();  // TODO: 2023/5/1 存泛型类型的对象，取时转换成对象的子类，会报错？！怎么搞？
        Animal i12 = (Animal) gc.getData();

        GenericContainer<Cat> catChild = new GenericContainer<>();
        catChild.setData(new Cat());
        GenericContainer<? extends Terrestrial> intChildGc = catChild; //new GenericContainer<Dog>();
        // 放入什么都不安全，因为容器指定类型是 Terrestrial 的子类，它的所有子类会呈树状散开，不知道具体会是哪个子类
//        intChildGc.setData(new Dog());
        // 取出是安全的，因为是 Terrestrial 的子类，也意味着一定是 Terrestrial 的父类 Animal 的子类
        Animal i21 = (Animal) intChildGc.getData();
//        Animal i23 = (Dog) intChildGc.getData(); // 取出时如果指定是泛型上界的其它子类（即不是数据中的类型），则无法检测出问题!!!
        Animal i22 = (Cat) intChildGc.getData();
        // 所以”取出安全“也是得以泛型类型 Terrestrial 为下界，往父类方向去强制转换才安全！

        GenericContainer<? super Atmobios> intParentGc = new GenericContainer<>();
        // 放入安全，但只能放入 Atmobios 的子类，因为 Atmobios 的父类肯定也是它的子类的父类
        intParentGc.setData(new Bird());
        intParentGc.setData(new Atmobios());
        intParentGc.setData(new Bee());
//        Animal s31 = (Terrestrial) intParentGc.getData(); // 编译时才报错！！！
//        Animal i32 = (Dog) intParentGc.getData(); // 编译时才报错！！！
        Animal i33 = (Animal) intParentGc.getData();
        Animal i34 = (Bird) intParentGc.getData();
        // 放入安全也得以泛型类 Atmobios 为上界往子类方向去强制转换才安全！
        System.out.println("s=" + i33);

    }

    /**
     * 测试泛型容器之间的相互赋值
     */
    private static void testAssignment() {
//        GenericContainer<Aquatic> gc = new GenericContainer<Fish>(); // 不用编译就不行！
        GenericContainer<? extends Aquatic> gc2 = new GenericContainer<Fish>(); //
//        gc2.setData(new Fish()); // 不用编译就会报错。

        GenericContainer<? super Aquatic> gc3 = new GenericContainer<Animal>(); //
        gc3.setData(new Aquatic());
        gc3.setData(new Fish());

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



}
