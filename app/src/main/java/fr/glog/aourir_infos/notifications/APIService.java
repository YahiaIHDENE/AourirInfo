package fr.glog.aourir_infos.Notifications;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {

    @Headers({
                "Content-Type:application/json",
                "Authorization:AAAASXCah3g:APA91bEyUjjJsroyzUP6PkJfeu5BcBGirU-M9KhQwL7MBo1xK97RdulDPvlib1Dj8s1E28b73iy99N03B_oBQNp7NcOgSg9EdbBh6Th_7nn45lFTtWCZjYz4CeTm9Q_jA7owABpiN_yj"
        })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
