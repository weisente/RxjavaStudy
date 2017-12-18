package example.weisente.top.rxjavastudy.demo1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

import example.weisente.top.rxjavastudy.R;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.PublishSubject;


/**
 * Created by san on 2017/12/15.
 */

public class SearchActivity extends AppCompatActivity {

    private EditText mEtSearch;
    private TextView mTvSearch;
    private PublishSubject<String> mPublishSubject;
    private DisposableObserver<String> DisposableObserver;
    private CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mEtSearch = (EditText) findViewById(R.id.et_search);
        mTvSearch = (TextView) findViewById(R.id.tv_search_result);
        mEtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //每次字的改变  就通知
                startSearch(s.toString());
            }
        });
        mPublishSubject = PublishSubject.create();
        /**
         * 消费者
         */
        DisposableObserver = new DisposableObserver<String>() {

            @Override
            public void onNext(String s) {
                mTvSearch.setText(s);
            }

            @Override
            public void onError(Throwable throwable) {

            }

            @Override
            public void onComplete() {
                Toast.makeText(SearchActivity.this,"llll",Toast.LENGTH_SHORT).show();
                Log.d("SearchActivity", "DisposableObserver");
            }
        };
        mPublishSubject.debounce(200, TimeUnit.MILLISECONDS).filter(new Predicate<String>() {
            @Override
            public boolean test(String s) throws Exception {
                return s.length() > 0;
            }
        }).switchMap(new Function<String, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(final String s) throws Exception {
                return Observable.create(new ObservableOnSubscribe<String>() {
                    @Override
                    public void subscribe(ObservableEmitter<String> e) throws Exception {
                        e.onNext("完成搜索，关键词为：" + s);
                        e.onComplete();
                    }
                });
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(DisposableObserver);

        mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(DisposableObserver);
    }

    private void startSearch(String query) {
        mPublishSubject.onNext(query);
    }

}
