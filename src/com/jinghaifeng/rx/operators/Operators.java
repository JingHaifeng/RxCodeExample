package com.jinghaifeng.rx.operators;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.*;
import rx.observables.ConnectableObservable;
import rx.observables.GroupedObservable;
import rx.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by alex on 16-4-18.
 */
public class Operators {

    public static void main(String[] args) {
        replay();
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
                .debounce(100, TimeUnit.MILLISECONDS)
                .subscribe(getObserver());
    }

    public static void distinct() {
        Observable.just(1, 2, 3, 1, 1, 4, 5, 6, 3)
                .distinct()
                .subscribe(getObserver());
    }

    public static void elementAt() {
        Observable.range(0, 10)
                .elementAt(9)
                .subscribe(getObserver());
    }

    public static void filter() {
        Observable.range(0, 100)
                .filter(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer % 2 == 0;
                    }
                })
                .subscribe(getObserver());
    }

    public static void first() {
        Observable.just(1, 2, 3)
                .first()
                .subscribe(getObserver());
    }

    public static void ignoreElements() {
        Observable.range(1, 10)
                .ignoreElements()
                .subscribe(getObserver());
    }

    public static void last() {
        Observable.just(1, 2, 3)
                .last()
                .subscribe(getObserver());

        Observable.range(1, 10)
                .last(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer == 9;
                    }
                })
                .subscribe(getObserver());
    }

    public static void sample() {
        Observable.interval(10, TimeUnit.MILLISECONDS, Schedulers.trampoline())
                .sample(50, TimeUnit.MILLISECONDS)
                .subscribe(getObserver());
    }

    public static void skip() {
        Observable.range(0, 10)
                .skip(9)
                .subscribe(getObserver());
    }

    public static void skipLast() {
        Observable.range(0, 100)
                .skipLast(80)
                .subscribe(getObserver());
    }

    public static void take() {
        Observable.range(0, 100)
                .take(50)
                .subscribe(getObserver());
    }

    public static void takeLast() {
        Observable.range(0, 100)
                .takeLast(10)
                .subscribe(getObserver());
    }

    public static void combineLastest() {
        List<Observable<?>> observables = new ArrayList<>();
        observables.add(Observable.defer(new Func0<Observable<String>>() {
            @Override
            public Observable<String> call() {
                return Observable.just("Hello");
            }
        }));
        observables.add(Observable.defer(new Func0<Observable<Integer>>() {
            @Override
            public Observable<Integer> call() {
                return Observable.range(0, 10);
            }
        }));
        Observable.combineLatest(observables, new FuncN<String>() {
            @Override
            public String call(Object... objects) {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < objects.length; i++) {
                    stringBuilder.append(objects[i]);
                }
                return stringBuilder.toString();
            }

        })
                .subscribe(getObserver());
    }

    /**
     * ?
     */
    public static void join() {
        Observable.interval(1000, TimeUnit.MILLISECONDS, Schedulers.trampoline())
                .join(Observable.interval(1000, TimeUnit.MILLISECONDS, Schedulers.trampoline()),
                        new Func1<Long, Observable<Long>>() {
                            @Override
                            public Observable<Long> call(Long aLong) {
                                return Observable.timer(2, TimeUnit.SECONDS);
                            }
                        },
                        new Func1<Long, Observable<Long>>() {
                            @Override
                            public Observable<Long> call(Long aLong) {
                                return Observable.timer(1, TimeUnit.SECONDS);
                            }
                        }
                        , new Func2<Long, Long, String>() {
                            @Override
                            public String call(Long aLong, Long aLong2) {
                                return aLong + "---" + aLong2;
                            }
                        }).subscribe(getObserver());
    }

    public static void merge() {
        Observable.just(1, 2, 3, 4)
                .mergeWith(Observable.just(2, 3, 4, 5, 6, 7, 9))
                .distinct()
                .subscribe(getObserver());
    }

    public static void startWith() {
        Observable.just(0, 1, 2, 3)
                .startWith(-1)
                .subscribe(getObserver());
    }

    public static void _switch() {
        Observable.just(0, 100)
                .switchOnNext(Observable.create(new Observable.OnSubscribe<Observable<Integer>>() {
                    @Override
                    public void call(Subscriber<? super Observable<Integer>> subscriber) {
                        if (!subscriber.isUnsubscribed()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                subscriber.onError(e);
                            }
                            subscriber.onNext(Observable.just(-1));
                        }
                        subscriber.onCompleted();
                    }
                }).delay(1, TimeUnit.SECONDS, Schedulers.trampoline()))
                .subscribe(getObserver());
    }

    public static void zip() {
        Observable.range(0, 100)
                .zipWith(Observable.range(200, 300), new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer, Integer integer2) {
                        return integer + integer2;
                    }
                }).subscribe(getObserver());
    }

    public static void _catch() {
        Observable.error(new Throwable("This is error!")).delay(1, TimeUnit.SECONDS)
                .onErrorReturn(new Func1<Throwable, String>() {
                    @Override
                    public String call(Throwable throwable) {
                        return throwable.getMessage();
                    }
                })
                .subscribe(getObserver());
    }

    public static void retry() {
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext("Start");
                    subscriber.onError(new Throwable("RUN --- ERROR"));
                }
            }
        })
                .retry(3)
                .subscribe(getObserver());
    }

    //
    public static void delay() {
        Observable.interval(10, TimeUnit.MILLISECONDS, Schedulers.immediate())
                .delay(100, TimeUnit.MILLISECONDS, Schedulers.immediate())
                .subscribe(getObserver());
    }

    public static void _do() {
        Observable.just(1, 2, 3)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("doOnSubscribe");
                    }
                })
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        System.out.println("doOnNext:" + integer);
                    }
                })
                .doOnCompleted(new Action0() {
                    @Override
                    public void call() {
                        System.out.println("doOnCompleted");
                    }
                })
                .subscribe(getObserver());
    }

    public static void materializeAndDematerialize() {
        Observable.just(1, 2, 3)
                .materialize()
                .dematerialize()
                .subscribe(getObserver());
    }

    public static void timeInterval() {
        Observable.interval(10, TimeUnit.MILLISECONDS, Schedulers.immediate())
                .timeInterval()
                .subscribe(getObserver());
    }

    public static void timeout() {
        Observable.just(1)
                .delay(2, TimeUnit.SECONDS, Schedulers.immediate())
                .timeout(1, TimeUnit.SECONDS)
                .subscribe(getObserver());
    }

    public static void timestamp() {
        Observable.range(0, 10)
                .timestamp()
                .subscribe(getObserver());
    }

    public static void using() {
        //todo
    }

    public static void to() {
        Observable.range(0, 10)
                .toList()
                .subscribe(getObserver());
        Iterator i = Observable.range(0, 10)
                .toBlocking()
                .getIterator();
        while (i.hasNext()) {
            System.out.print(i.next() + "\t");
        }
    }

    public static void all() {
        Observable.range(0, 10)
                .all(new Func1<Integer, Boolean>() {
                    @Override
                    public Boolean call(Integer integer) {
                        return integer < 10;
                    }
                })
                .subscribe(getObserver());
    }

    public static void amb() {
        Observable
                .merge(Observable
                                .just(1)
                                .delay(2, TimeUnit.SECONDS, Schedulers.trampoline()).timestamp(),
                        Observable
                                .just(2)
                                .delay(1, TimeUnit.SECONDS, Schedulers.trampoline())).timestamp()
                .subscribe(getObserver());
    }

    public static void contains() {
        Observable.just(1, 2, 3)
                .contains(1)
                .subscribe(getObserver());
    }

    public static void sequenceEqual() {
        Observable.sequenceEqual(Observable.just(1, 2, 3)
                , Observable.just(1, 2, 3))
                .subscribe(getObserver());
    }

    public static void skipUntil() {
        Observable.interval(1, TimeUnit.SECONDS, Schedulers.immediate())
                .skipUntil(Observable.just(1).delay(5, TimeUnit.SECONDS))
                .subscribe(getObserver());
    }

    public static void skipWhile() {
        Observable.interval(100, TimeUnit.MILLISECONDS, Schedulers.immediate())
                .skipWhile(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        return aLong < 10;
                    }
                })
                .subscribe(getObserver());
    }

    public static void takeUntil() {
        Observable.interval(1, TimeUnit.MILLISECONDS, Schedulers.immediate())
                .takeUntil(new Func1<Long, Boolean>() {
                    @Override
                    public Boolean call(Long aLong) {
                        return aLong == 10;
                    }
                })
                .subscribe(getObserver());
    }

    public static void count() {
        Observable.range(0, 10)
                .count()
                .subscribe(getObserver());
    }

    public static void concat() {
        Observable.range(0, 100)
                .concatWith(Observable.just(-1, -2))
                .subscribe(getObserver());
    }

    public static void reduce() {
        Observable.range(0, 10)
                .reduce(new Func2<Integer, Integer, Integer>() {
                    @Override
                    public Integer call(Integer integer, Integer integer2) {
                        return integer + integer2;
                    }
                })
                .subscribe(getObserver());
    }

    public static void collect() {
        Observable.range(0, 10)
                .collect(new Func0<List<Integer>>() {
                    @Override
                    public List<Integer> call() {
                        return new ArrayList<Integer>();
                    }
                }, new Action2<List<Integer>, Integer>() {
                    @Override
                    public void call(List<Integer> integers, Integer integer) {
                        integers.add(integer);
                    }
                })
                .subscribe(getObserver());
    }

    public static void publish() {
        ConnectableObservable observable = Observable.range(0, 100).publish();
        observable.subscribe(getObserver());
        observable.subscribe(getObserver());
        observable.connect();
    }

    /**
     * ?
     */
    public static void refCount() {
        ConnectableObservable observable = Observable.range(0, 100).publish();
        observable.subscribe(getObserver());
        observable.connect();
        observable.subscribe(getObserver());
        observable.refCount();
    }

    /**
     * ?
     */
    public static void replay() {
        ConnectableObservable observable = Observable.range(0, 100).replay(20);
        observable.subscribe(getObserver());
        observable.subscribe(getObserver());
        observable.connect();
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
