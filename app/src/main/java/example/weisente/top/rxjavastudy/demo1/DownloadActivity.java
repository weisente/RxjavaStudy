package example.weisente.top.rxjavastudy.demo1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import example.weisente.top.rxjavastudy.R;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by san on 2017/12/15.
 */

public class DownloadActivity extends AppCompatActivity {
    private TextView mTvDownload;
    private TextView mTvDownloadResult;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);
        mTvDownload = (TextView) findViewById(R.id.tv_download);
        Log.e("阿萨德阿斯","阿斯达岁的");
        mTvDownloadResult = (TextView) findViewById(R.id.tv_download_result);
        mTvDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDownload();
            }


        });
    }

//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
//        super.onCreate(savedInstanceState, persistentState);
//        setContentView(R.layout.activity_background);
////        mTvDownload = (TextView) findViewById(R.id.tv_download);
////        Log.e("阿萨德阿斯","阿斯达岁的");
////        mTvDownloadResult = (TextView) findViewById(R.id.tv_download_result);
////        mTvDownload.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                startDownload();
////            }
////
////
////        });
//    }

    private void startDownload() {
        Observable<Integer> observable = Observable.create(new ObservableOnSubscribe<Integer>() {

            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; i < 100; i++) {
                    if (i % 20 == 0) {
                        try {
                            Thread.sleep(500); //模拟下载的操作。
                        } catch (InterruptedException exception) {
                            if (!e.isDisposed()) {
                                e.onError(exception);
                            }
                        }
                        e.onNext(i);
                    }
                }
                e.onComplete();
            }
        });

        DisposableObserver<Integer> disposableObserver = new DisposableObserver<Integer>() {

            @Override
            public void onNext(Integer value) {
                Log.d("DownloadActivity", "onNext=" + value);
                mTvDownloadResult.setText("当前下载进度：" + value);
            }

            @Override
            public void onError(Throwable e) {
                Log.d("DownloadActivity", "onError=" + e);
                mTvDownloadResult.setText("下载失败");
            }

            @Override
            public void onComplete() {
                Log.d("DownloadActivity", "onComplete");
                mTvDownloadResult.setText("下载成功");
            }
        };

        observable.subscribeOn(Schedulers.single()).observeOn(AndroidSchedulers.mainThread()).subscribe(disposableObserver);
        mCompositeDisposable.add(disposableObserver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }
}
