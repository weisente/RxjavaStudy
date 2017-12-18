package example.weisente.top.rxjavastudy.demo1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import example.weisente.top.rxjavastudy.R;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.subjects.PublishSubject;

/**
 * Created by san on 2017/12/18.
 */

public class CombineLatestActivity  extends AppCompatActivity {
    private EditText mEtName;
    private EditText mEtPassword;
    private Button mBtLogin;
    private PublishSubject<String> mNameSubject;
    private PublishSubject<String> mPasswordSubject;
    private CompositeDisposable mCompositeDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combine_latest);

        mEtName = (EditText) findViewById(R.id.et_name);
        mEtPassword = (EditText) findViewById(R.id.et_password);
        mBtLogin = (Button) findViewById(R.id.bt_login);
        mNameSubject = PublishSubject.create();
        mPasswordSubject = PublishSubject.create();
        //设置监听者
        mEtName.addTextChangedListener(new EditTextMonitor(mNameSubject));
        mEtPassword.addTextChangedListener(new EditTextMonitor(mPasswordSubject));

        Observable<Boolean> booleanObservable = Observable.combineLatest(mNameSubject, mPasswordSubject, new BiFunction<String, String, Boolean>() {
            @Override
            public Boolean apply(String name, String password) throws Exception {
                int nameLen = name.length();
                int passwordLen = password.length();
                return nameLen >= 2 && nameLen <= 8 && passwordLen >= 4 && passwordLen <= 16;
            }
        });

        DisposableObserver<Boolean> disposable = new DisposableObserver<Boolean>() {

            @Override
            public void onNext(Boolean value) {
                mBtLogin.setText(value ? "登录" : "用户名或密码无效");
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }

        };

        booleanObservable.subscribe(disposable);
        mCompositeDisposable = new CompositeDisposable();
        mCompositeDisposable.add(disposable);

    }


    //可以把输入的数据转换为string
    private class EditTextMonitor implements TextWatcher {

        private PublishSubject<String> mPublishSubject;

        EditTextMonitor(PublishSubject<String> publishSubject) {
            mPublishSubject = publishSubject;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            mPublishSubject.onNext(s.toString());
        }
    }
}
