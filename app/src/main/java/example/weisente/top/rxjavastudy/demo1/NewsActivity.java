package example.weisente.top.rxjavastudy.demo1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.ArrayList;
import java.util.List;

import example.weisente.top.rxjavastudy.R;
import example.weisente.top.rxjavastudy.entity.NewsAdapter;
import example.weisente.top.rxjavastudy.entity.NewsApi;
import example.weisente.top.rxjavastudy.entity.NewsEntity;
import example.weisente.top.rxjavastudy.entity.NewsResultEntity;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by san on 2017/12/15.
 */

public class NewsActivity extends AppCompatActivity {
    private int mCurrentPage = 1;
    private NewsAdapter mNewsAdapter;
    private List<NewsResultEntity> mNewsResultEntities = new ArrayList<>();
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        initView();
    }

    private void initView() {
        Button btRefresh = (Button) findViewById(R.id.bt_refresh);
        btRefresh.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                refreshArticle(++mCurrentPage);
            }
        });
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_news);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        mNewsAdapter = new NewsAdapter(mNewsResultEntities);
        recyclerView.setAdapter(mNewsAdapter);
        refreshArticle(++mCurrentPage);
    }

    private void refreshArticle(int page) {
        Observable<List<NewsResultEntity>> observable  =
                //这里的
                Observable.just(page).subscribeOn(Schedulers.io()).flatMap(new Function<Integer, ObservableSource<List<NewsResultEntity>>>() {
            @Override
            public ObservableSource<List<NewsResultEntity>> apply(Integer page) throws Exception {
                Observable<NewsEntity> androidNews = getObservable("Android", page);
                Observable<NewsEntity> iosNews = getObservable("iOS", page);
                //根据 我的见解是  把两个集合合拼
                return Observable.zip(androidNews, iosNews, new BiFunction<NewsEntity, NewsEntity, List<NewsResultEntity>>() {
                    @Override
                    public List<NewsResultEntity> apply(NewsEntity androidEntity, NewsEntity iosEntity) throws Exception {
                        List<NewsResultEntity> result = new ArrayList<>();
                        result.addAll(androidEntity.getResults());
                        result.addAll(iosEntity.getResults());
                        return result;
                    }
                });
            }
        });

        DisposableObserver<List<NewsResultEntity>> disposable  = new DisposableObserver<List<NewsResultEntity>>() {
            @Override
            public void onNext(List<NewsResultEntity> value) {
                //这是一个主要的集合
                mNewsResultEntities.clear();
                mNewsResultEntities.addAll(value);
                mNewsAdapter.notifyDataSetChanged();

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        };
        observable.observeOn(AndroidSchedulers.mainThread()).subscribe(disposable);
        mCompositeDisposable.add(disposable);
    }


    private Observable<NewsEntity> getObservable(String category, int page){
        NewsApi newsApi = new Retrofit.Builder().baseUrl("http://gank.io").addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create()).build().create(NewsApi.class);
        return newsApi.getNews(category, 10, page);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }
}
