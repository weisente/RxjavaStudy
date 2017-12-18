package example.weisente.top.rxjavastudy.entity;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface WeatherApi {

    @GET("adat/sk/{cityId}.html")
    public Observable<WeatherEntity> getWeather(@Path("cityId") long cityId);
}
