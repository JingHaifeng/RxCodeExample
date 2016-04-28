package com.jinghaifeng.rx.operators;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by alex on 16-4-18.
 */
public class Operators {

    public static void main(String[] args) {
        debounce();
    }

    /**
     * create
     */
    public static void create() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                try {
                    if (!subscriber.isUnsubscribed()) {
                        for (int i = 1; i < 5; i++) {
                            subscriber.onNext(i);
                        }
                        subscriber.onCompleted();
                    }
                } catch (Exception e) {
                    subscriber.onError(e);
                }
            }
        })
                .subscribe(getObserver());
    }

    /**
     * defer
     */
    public static void defer() {
        Observable<String> observable = Observable.defer(new Func0<Observable<String>>() {
            int count = 0;

            @Override
            public Observable<String> call() {
                return Observable.just("Observable:" + count++);
            }
        });

        observable.subscribe(getObserver());

        observable.subscribe(getObserver());
    }

    /**
     * from
     */
    public static void from() {
        Integer[] items = {0, 1, 2, 3, 4, 5, 6};
        Observable<Integer> observable = Observable.from(items);
        observable.subscribe(getObserver());

        Observable<String> observableFuture = Observable.from(new Future<String>() {
            @Override
            public boolean cancel(boolean b) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public String get() throws InterruptedException, ExecutionException {
                return "This is Future";
            }

            @Override
            public String get(long l, TimeUnit timeUnit) throws InterruptedException, ExecutionException, TimeoutException {
                return get();
            }
        });

        observableFuture.subscribe(getObserver());
    }

    public static void interval() {
        //To-Do 经常无法正常使用,原因是 调度器问题
        Observable.interval(1, TimeUnit.SECONDS, Schedulers.immediate())
                .subscribe(getObserver());
    }

    public static void just() {
        Observable.just(0, 1, 2, 3, 4, 5, 6, 7, 8).subscribe(getObserver());
    }

    public static void range() {
        Observable.range(0, 100, Schedulers.trampoline()).subscribe(getObserver());
    }

    public static void repeat() {
        Observable.just(0, 1, 2).repeat(3).subscribe(getObserver());
    }

    public static void start() {
        // rxjava-async 扩展中实现
    }

    public static void timer() {
        System.out.println(System.currentTimeMillis());
        Observable.timer(3, TimeUnit.SECONDS, Schedulers.trampoline()).subscribe(getObserver());
        System.out.println(System.currentTimeMillis());
    }

    public static void buffer() {
        Observable.range(0, 100).buffer(3).subscribe(getObserver());
    }

    public static void map() {
        Observable.just(1, 2, 3).map(new Func1<Integer, String>() {
            @Override
            public String call(Integer integer) {
                return "The " + integer + "st.";
            }
        }).subscribe(getObserver());
    }

    public static void flatMap() {
        Observable.just(100, 10, 1)
                .flatMap(new Func1<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(Integer s) {
                        return Observable.range(0, s);
                    }
                }).subscribe(getObserver());

        Observable.just(100, 10, 1)
                .concatMap(new Func1<Integer, Observable<?>>() {
                    @Override
                    public Observable<?> call(Integer integer) {
                        return Observable.range(0, integer);
                    }
                }).subscribe(getObserver());
    }

    public static void groupBy() {
        Observable.range(1, 100).groupBy(new Func1<Integer, Integer>() {
            @Override
            public Integer call(Integer integer) {
                return integer % 2;
            }
        }).subscribe(new Subscriber<GroupedObservable<Integer, Integer>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onNext(final GroupedObservable<Integer, Integer> groupedObservable) {
                groupedObservable.map(new Func1<Integer, String>() {
                    @Override
                    public String call(Integer integer) {
                        return integer + " with key " + groupedObservable.getKey();
                    }
                }).subscribe(getObserver());
            }
        });
    }

    public static void scan() {
        Observable.range(1, 100)
                .scan(new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer, Integer integer2) {
                        return integer + integer2;
                    }
                }).subscribe(getObserver());
    }

    public static void window() {
        Observable.range(1, 100)
                .window(10)
                .subscribe(new Action1<Observable<Integer>>() {
                    int counter;

                    @Override
                    public void call(Observable<Integer> integerObservable) {
                        System.out.println("counter:" + counter++);
                        integerObservable.subscribe(getObserver());
                    }
                });
    }

    public static void debounce() {
        Observable.create(new Observable.OnSubscribe<Integer>() {
            int counter = 0;

            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    while (counter < 100) {
                        try {
                            Thread.sleep(100);
                            subscriber.onNext(counter++);
                        } catch (InterruptedException e) {
                            subscriber.onError(e);
                        }
                    }
                }
                subscriber.onCompleted();
            }
        })
                .debounce(new Func1<Integer, Observable<Integer>>() {
                    @Override
                    public Observable<Integer> call(Integer integer) {
                        return Observable.just(integer);
                    }
                })
                .subscribe(getObserver());
    }

    private static <T> Observer getObserver() {
        return new Observer<T>() {
            @Override
            public void onCompleted() {
                System.out.println("Operators.onCompleted");
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("throwable = " + throwable);
            }

            @Override
            public void onNext(T t) {
                System.out.println("t = " + t);
            }
        };
    }
}
