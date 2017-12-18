package example.weisente.top.rxjavastudy.demo1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import example.weisente.top.rxjavastudy.R;
import example.weisente.top.rxjavastudy.entity.NewsAdapter;
import example.weisente.top.rxjavastudy.entity.NewsResultEntity;
import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;

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
        Observable.just(page).subscribeOn(page)
    }
}
