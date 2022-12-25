package testJava;

import java.lang.reflect.Proxy;

/**
 * Create by Greyson on 2022/12/25
 */
class TestDynamicProxy {

    public static void main(String[] args) {

        FinanceBusiness businessService = new FinanceBusiness();
        ServiceDynamicProxy proxy = new ServiceDynamicProxy(businessService);
        IBusiness business = proxy.create(IBusiness.class);

        business.doBusiness();
        System.out.println("调用者获取数据：" + business.getData());


        ServiceDynamicProxy proxy2 = new ServiceDynamicProxy(new PublicationBusiness());
        IBusiness business2 = proxy2.create(IBusiness.class);

        business2.doBusiness();


        System.out.println();

        Running runningPrincipal = new Running();
        ServiceDynamicProxy proxy3 = new ServiceDynamicProxy(runningPrincipal);
        IEntertainment entertainment = proxy3.create(IEntertainment.class);
        entertainment.playSomething();
    }
}


interface IBusiness {
    boolean doBusiness();

    double getData();
}

class FinanceBusiness implements IBusiness {
    public boolean doBusiness() {
        System.out.println("执行 金融业务");
        return Math.random() > 0.3f;
    }

    @Override
    public double getData() {
        return 99.99;
    }
}

class PublicationBusiness implements IBusiness {
    @Override
    public boolean doBusiness() {
        System.out.println("出版图书业务");
        return false;
    }

    @Override
    public double getData() {
        return 100.2;
    }
}


interface IEntertainment {
    void playSomething();
    long getPlayTime();
}

class Running implements IEntertainment {
    @Override
    public void playSomething() {
        System.out.println("娱乐 跑步~~");
    }

    @Override
    public long getPlayTime() {
        return 60;
    }
}


class ServiceDynamicProxy {

    private final Object principal;

    public ServiceDynamicProxy(Object obj) {
        principal = obj;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(final Class<T> serviceClazz) {
        return (T) Proxy.newProxyInstance(
                serviceClazz.getClassLoader(),
                new Class<?>[]{serviceClazz},
                (proxy, method, args) -> {
                    System.out.println("代理 开始调用:");
                    Object ret = method.invoke(principal, args);
                    System.out.println("代理 调用结束。");
                    return ret;
                }

        );
    }
}