package com.techster_media.cesa.SendNotificationPack;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorization:key=AAAAoeiBXfQ:APA91bFFgu3ixDtd7fIvekIzfwxZnuQkHudzsQcUvzh2qQnWtyYbQQC3OynBeW63CpUvkoqm62ctzjq6Oyg1xuLXnZZo1K3bs0Elqanum6BFjxOJ2H0e_GSnv1QRjAOZLKEdIl7asVzT" // Your server key refer to video for finding your server key
            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifcation(@Body NotificationSender body);
}

