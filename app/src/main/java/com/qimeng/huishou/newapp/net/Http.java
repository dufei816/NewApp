package com.qimeng.huishou.newapp.net;

import com.qimeng.huishou.newapp.entity.User;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface Http {

    /**
     * 根据传输的二维码值，后端判断二维码值的真伪
     *
     * @param code
     * @param clas
     * @return
     */
    @GET("getUser.jsp")
    Observable<User> getUser(@Query("code") String code, @Query("class") String clas);


    /**
     * 根据学生操作的瓶子、纸分类，数量进行上传保存
     *
     * @param code
     * @param clas
     * @param num
     * @return
     */
    @GET("uploadIntegral.jsp")
    Observable<User> uploadIntegral(@Query("jqbh") String jqbh, @Query("code") String code, @Query("classtype") String clas, @Query("num") String num);


    /**
     * 获取机器端信息，定期上传（比如15分钟或半小时）
     *
     * @param msg
     * @return
     */
    @GET("uploadMacInf.jsp")
    Observable<User> uploadMacInf(@Query("msg") String msg);


    /**
     * 设置机器编号
     *
     * @param bh
     * @param jqm
     * @return
     */
    @GET("updateJqm.jsp")
    Observable<User> updateJqm(@Query("bh") String bh, @Query("jqm") String jqm);


    /**
     * 操作响应
     *
     * @param bh
     * @param jqm
     * @param jg
     * @return
     */
    @GET("pushxy.jsp")
    Observable<User> pushxy(@Query("bh") String bh, @Query("bt") String jqm, @Query("nr") String nr, @Query("jg") boolean jg);


    //文件Retrofit下载
    @Streaming
    @GET
    Observable<ResponseBody> retrofitDownloadFile(@Url String fileUrl);


}