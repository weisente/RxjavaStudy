package example.weisente.top.rxjavastudy.demo1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import example.weisente.top.rxjavastudy.R;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by san on 2017/12/18.
 */

public class PollingActivity extends AppCompatActivity {
    private static final String TAG = PollingActivity.class.getSimpleName();

    private TextView mTvSimple;
    private TextView mTvAdvance;
    private CompositeDisposable mCompositeDisposable;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_polling);
        mTvSimple = (TextView) findViewById(R.id.tv_simple);
        mTvSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSimplePolling();
            }
        });
        mTvAdvance = (TextView) findViewById(R.id.tv_advance);
        mTvAdvance.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startAdvancePolling();
            }

        });
        mCompositeDisposable = new CompositeDisposable();

    }

    private void startAdvancePolling() {
        Log.d(TAG, "startAdvancePolling click");
        Observable<Long> observable = Observable.just(0L).doOnComplete(new Action() {
            @Override
            public void run() throws Exception {
                doWork();
            }
            //Function 把对象转换
        }).repeatWhen(new Function<Observable<Object>, ObservableSource<Long>>() {

            private long mRepeatCount;

            @Override
            public ObservableSource<Long> apply(Observable<Object> objectObservable) throws Exception {
                if (++mRepeatCount > 4) {
                    //return Observable.empty(); //发送onComplete消息，无法触发下游的onComplete回调。
                    return Observable.error(new Throwable("Polling work finished")); //发送onError消息，可以触发下游的onError回调。
                }
                return Observable.timer(3000 + mRepeatCount * 1000, TimeUnit.MILLISECONDS);
            }
        });

        DisposableObserver<Long> disposableObserver = getDisposableObserver();
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);


    }

    private void startSimplePolling() {
        Log.d(TAG, "startSimplePolling");
        Observable<Long> observable  = Observable.intervalRange(0, 5, 0, 3000, TimeUnit.MILLISECONDS).take(5).doOnNext(new Consumer<Long>() {
            @Override
            public void accept(Long aLong) throws Exception {
                doWork();
            }
        });
        DisposableObserver<Long> disposableObserver = getDisposableObserver();
        observable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);
    }

    private DisposableObserver<Long> getDisposableObserver() {
        //这个只是正常写  只是检测生命周期
        return new DisposableObserver<Long>() {
            @Override
            public void onNext(Long value) {

            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "DisposableObserver onError, threadId=" + Thread.currentThread().getId() + ",reason=" + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "DisposableObserver onComplete, threadId=" + Thread.currentThread().getId());
            }
        };

    }

    private void doWork() {
        long workTime = (long) (Math.random() * 500) + 500;
        Log.d(TAG, "doWork start,  threadId=" + Thread.currentThread().getId());
        try {
            Thread.sleep(workTime);
            Log.d(TAG, "doWork finished");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }
}
