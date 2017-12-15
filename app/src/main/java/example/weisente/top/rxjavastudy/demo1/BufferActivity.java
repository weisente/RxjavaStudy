package example.weisente.top.rxjavastudy.demo1;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import java.util.List;
import java.util.concurrent.TimeUnit;

import example.weisente.top.rxjavastudy.R;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by san on 2017/12/15.
 */

public class BufferActivity extends AppCompatActivity {
    private PublishSubject<Double> mPublishSubject;
    private CompositeDisposable mCompositeDisposable;
    private TextView mTv;
    private SourceHandler mSourceHandler;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buffer);
        mTv = (TextView) findViewById(R.id.tv_buffer);
        mPublishSubject = PublishSubject.create();//可以缓存一下信息


        DisposableObserver<List<Double>> disposableObserver = new DisposableObserver<List<Double>>(){

            @Override
            public void onNext(List<Double> value) {
                double result = 0;
                if (value.size() > 0) {
                    for (Double d : value) {
                        result += d;
                    }
                    result = result / value.size();
                }
                Log.d("BufferActivity", "更新平均温度：" + result);
                mTv.setText("过去3秒收到了" + value.size() + "个数据， 平均温度为：" + result);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };

        mPublishSubject.buffer(3000, TimeUnit.MILLISECONDS).subscribeOn(AndroidSchedulers.mainThread()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);

        mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(disposableObserver);

        mSourceHandler = new SourceHandler();
        mSourceHandler.sendEmptyMessage(0);

    }

    private class SourceHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            double temperature = Math.random() * 25 + 5;
            updateTemperature(temperature);
            //循环地发送。
            sendEmptyMessageDelayed(0, 250 + (long) (250 * Math.random()));//125
        }
    }

    public void updateTemperature(double temperature) {
        Log.d("BufferActivity", "温度测量结果：" + temperature);
        mPublishSubject.onNext(temperature);
    }
}
