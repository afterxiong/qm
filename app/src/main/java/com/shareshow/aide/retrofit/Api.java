package com.shareshow.aide.retrofit;


import com.shareshow.aide.retrofit.entity.AmplyNotify;
import com.shareshow.aide.retrofit.entity.DeptMember;
import com.shareshow.aide.retrofit.entity.OrgAndDept;
import com.shareshow.aide.retrofit.entity.StudyMaterialsVisitingMaterials;
import com.shareshow.aide.retrofit.entity.Team;
import com.shareshow.aide.retrofit.entity.TeamAudioData;
import com.shareshow.aide.retrofit.entity.UploadIds;
import com.shareshow.aide.retrofit.entity.UserInfo;
import com.shareshow.aide.retrofit.entity.VisitRecord;
import com.shareshow.db.Adertisement;
import com.shareshow.db.DeviceIds;

import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by xiongchengguang on 2017/12/19.
 */

public interface Api {


    @GET("register.form")
    Observable<UserInfo> getPhoneByUser(@Query("phone") String phone);

    @GET("getOrgAndDeptByPhone.form")
    Observable<List<OrgAndDept>> getOrgAndDeptByPhone(@Query("phone") String phone);

    @GET("getOrgAndDeptByPhone.form")
    Observable<ResponseBody> getOrgAndDeptByPhoneString(@Query("phone") String phone);


    @GET("getOrgAndDept.form")
    Observable<List<OrgAndDept>> getTerminalidsByInfo(@Query("dbiTerminalids") String dbiTerminalids);


    @GET("register.form")
    Observable<UserInfo> register(@Query("phoneDevNo") String phoneDevNo,
                                  @Query("name") String name,
                                  @Query("phone") String phone,
                                  @Query("orgid") String orgid,
                                  @Query("deptid") String deptid);

    @FormUrlEncoded
    @POST("register.form")
    Observable<ResponseBody> registerString(@Field("phoneDevNo") String phoneDevNo,
                                            @Field("name") String name,
                                            @Field("phone") String phone,
                                            @Field("orgid") String orgid,
                                            @Field("deptid") String deptid);


    @GET("mobileLogin.form")
    Observable<UserInfo> mobileLogin(@Query("phone") String phone);

    @FormUrlEncoded
    @POST("updateUserRigsterInfo.form")
    Observable<UserInfo> updataUserInfo(@Field("phone") String phone,
                                        @Field("name") String name,
                                        @Field("orgid") String orgid,
                                        @Field("deptid") String deptid);

    @GET("register.form")
    Observable<ResponseBody> login(@Query("phone") String phone);


    @GET("register.form")
    Observable<UserInfo> checkRegister(@Query("phone") String phone);


    @Multipart
    @POST("uploadVisitRecord.form")
    Observable<UserInfo> submitVisitRecord(@PartMap() Map<String, RequestBody> map,
                                           @Part List<MultipartBody.Part> part);

    /**
     * 拜访文件上传  阿里云OSS
     *
     * @param userId
     * @param phone
     * @param timeStart
     * @param timeEnd
     * @param name
     * @param addresss
     * @param gps
     * @param content
     * @param url
     * @return
     */
    @FormUrlEncoded
    @POST("visitRecord.form")
    Observable<UserInfo> visitRecord(@Field("userId") String userId,
                                     @Field("phone") String phone,
                                     @Field("timeStart") Long timeStart,
                                     @Field("timeEnd") Long timeEnd,
                                     @Field("name") String name,
                                     @Field("addresss") String addresss,
                                     @Field("gps") String gps,
                                     @Field("content") String content,
                                     @Field("url") String url);


    @FormUrlEncoded
    @POST("addCheckMorningVoice.form")
    Observable<UserInfo> uploadFileMorning(@Field("date") String date,
                                               @Field("userId") String userId,
                                               @Field("audiotime") String audiotime,
                                               @Field("phone") String phone,
                                               @Field("url") String url,
                                               @Field("fileName") String fileName);

    @Multipart
    @POST("uploadCheckMorningVoice.form")
    Observable<ResponseBody> uploadFilePlayeRecoed(@Part("date") RequestBody date,
                                                   @Part MultipartBody.Part part,
                                                   @Part("PhoneNum") RequestBody description,
                                                   @Part("userId") RequestBody userId,
                                                   @Part("audiotime") RequestBody audioTime);

    @GET("devGetVisitFile.form")
    Observable<ResponseBody> devGetVisitFileString(@Query("phoneNumber") String phoneNumber);

    //拜访资料
    @GET("devGetVisitFile.form")
    Observable<ResponseBody> devGetVisitData(@Query("phoneNumber") String phoneNumber);


    //5.2获取培训资料（熊荡）
    @GET("devGetStudyFile.form")
    Observable<ResponseBody> devGetStudyFile(@Query("phoneNumber") String phoneNumber);

    //获取学习资料和培训资料
    @GET("devGetStudyAndVisit.form")
    Observable<List<StudyMaterialsVisitingMaterials>> devGetStudyAndVisit(@Query("phoneNumber") String phoneNumber);

    //获取学习资料和培训资料
    @GET("devUpdateVisitAndStudyFileReceive.form")
    Observable<ResponseBody> devUpdateVisitAndStudyFileReceive(@Query("phoneNumber") String phoneNumber,
                                                               @Query("planId") String planId,
                                                               @Query("planType") String planType);

    //获取公告通知
    @GET("devGetNotification.form")
    Observable<List<AmplyNotify>> devGetNotification(@Query("phoneNumber") String phoneNumber);


    @GET("bindDev.form")
    Observable<UserInfo> bindingDevice(@Query("dbiTerminalids") String dbiTerminalids,
                                       @Query("phone") String phone,
                                       @Query("phoneDevNo") String phoneDevNo);


    @GET("updateVisitFileReceive.form")
    Observable<ResponseBody> updateVisitFileSate(@Query("phoneNumber") String phoneNumber,
                                                 @Query("vfrVpId") String vfrVpId);

    @GET("updateStudyFileReceive.form")
    Observable<ResponseBody> updateStudyFileSate(@Query("phoneNumber") String phoneNumber,
                                                 @Query("sfrSpId") String sfrSpId);


    /**
     * 解除绑定
     *
     * @param dbiTerminalids
     * @return
     */
    @GET("relieveDevBind.form")
    Observable<ResponseBody> relieveDevBind(@Query("dbiTerminalids") String dbiTerminalids);


    @POST("SMS_VerifiCode.form")
    Observable<ResponseBody> getTestCode(@Query("phoneNumber") String phoneNumber);


    @POST("changePhone.form")
    Observable<UserInfo> changeUserPhone(@Query("oldPhone") String oldPhone,
                                         @Query("newPhone") String newPhone);

    //查看拜访记录
    @GET("listVisitRecord.form")
    Observable<VisitRecord> getVisitTrack(@Query("userIds") String userId,
                                          @Query("date") String date);


    /**
     * @param userIds 用户Id
     * @param date    日期筛选
     * @param vrId    最后一条客户拜访记录Id
     * @param flag    查询顺序 1 查询新记录，0查询旧记录
     * @param num     分页查询数量
     * @return
     */
    @GET("devlistVisitRecord.form")
    Observable<VisitRecord> getDevVistiTrackList(@Query("userIds") String userIds,
                                                 @Query("date") String date,
                                                 @Query("vrId") String vrId,
                                                 @Query("flag") Integer flag,
                                                 @Query("num") Integer num);


    @GET("selectDevByPhone.form")
    Observable<DeviceIds> getBindIds(@Query("phone") String phoneNumber);

    @GET("selectDevByPhone.form")
    Observable<ResponseBody> selectDevByPhone(@Query("phone") String phoneNumber);


    @GET("devSetNotification.form")
    Observable<ResponseBody> devSetNotification(@Query("phoneNumber") String phoneNumber, @Query("notiSendId") String notiSndId);

    @Streaming
    @GET
    Observable<ResponseBody> donwnload(@Url String url);

    //创建团队
    @FormUrlEncoded
    @POST("addTeam.form")
    Observable<UserInfo> createTeam(@Field("userId") String userId, @Field("name") String name);

    //获取团队成员列表
    @POST("teamMember.form")
    Observable<Team> teamMember(@Query("teamId") String teamId);


    //解散团队
    @POST("delTeam.form")
    Observable<Team> deleteTeam(@Query("teamId") String teamId);

    //退出团队
    @POST("leftTeam.form")
    Observable<Team> leftTeam(@Query("userId") String userId);

    //加入团队
    @GET("joinTeam.form")
    Observable<UserInfo> addTeam(@Query("userId") String userId,
                                 @Query("teamId") String teamId);

    //获取用户信息
    @GET("getRegisterUserInfo.form")
    Observable<UserInfo> getRegisterUserInfo(@Query("userId") String userId);

    //获取广告
    @GET("getAdCurrentForDevice.form")
    Observable<Adertisement> getAdCurrentForDevice(@Query("phoneNumber") String phoneNumber, @Query("adType") String adType);

    //上传广告更新状态
    @POST("setAdReceiverByDevice.form")
    Observable<ResponseBody> upAdReceiverByDevice(@Query("dbiTerminalids") String dbiTerminalids);

    //上传设备广告播放记录
    @POST("devAdPlayRecord.form")
    Observable<ResponseBody> upDevAdPlayRecord(@Query("liString") String liString);

    //上传设备使用数据
    @FormUrlEncoded
    @POST("devUseInfo.form")
    Observable<ResponseBody> upDevUseInfo(@Field("liString") String liString);

    //上传盒子APP使用数据
    @POST("devAppUse.form")
    Observable<ResponseBody> upDevAppUse(@Query("liString") String liString);

    //上传拜访资料使用数据
    @POST("visitFilePlayRecord.form")
    Observable<ResponseBody> upVisitFilePlayRecord(@Query("liString") String liString);

    //上传学习资料使用数据
    @POST("studyFilePlayRecord.form")
    Observable<ResponseBody> upStudyFilePlayRecord(@Query("liString") String liString);

    //获取团队录音文件
    @POST("listCheckMorning.form")
    Observable<TeamAudioData> listCheckMorning(@Query("userId") String userId,
                                               @Query("phone") String phone,
                                               @Query("date") String date,
                                               @Query("flag") Integer flag


    );

    //获取全部的录音
    @GET("devlistCheckMorning.form")
    Observable<TeamAudioData> devListCheckMoring(@Query("userIds") String userIds,
                                                 @Query("date") String date,
                                                 @Query("vrId") String vrId,
                                                 @Query("flag") Integer flag,
                                                 @Query("num") Integer num
    );

    @GET("devlistCheckMorning1.form")
    Observable<TeamAudioData> getDateFilterMorningRecord(@Query("userId") String userIds,
                                                         @Query("startDate") String startDate,
                                                         @Query("endDate") String endDate
    );

    /**
     * 查询拜访记录
     *
     * @param userId    用户Id
     * @param startDate 开始时间
     * @param startDate 结束时间
     */
    @GET("devlistVisitRecord2.form")
    Observable<VisitRecord> getSyncVisitRecord(@Query("userId") String userId,
                                               @Query("startDate") String startDate,
                                               @Query("endDate") String endDate);

    /**
     * 查询部门员工列表
     *
     * @param deptId
     * @return
     */
    @GET("devListUserRegisterBandOrgRequest.form")
    Observable<DeptMember> getDeptMemberList(@Query("deptId") String deptId);

    /**
     * 扫描二维码绑定组织信息
     *
     * @param userId       绑定用户Id
     * @param orgId        组织Id
     * @param deptId       部门Id
     * @param qrCodeUserId 二维码提供者用户Id
     * @return
     */
    @GET("devUserRegisterBandOrg.form")
    Observable<UserInfo> getDevUserRegisterBandOrg(@Query("userId") String userId,
                                                   @Query("orgId") String orgId,
                                                   @Query("deptId") String deptId,
                                                   @Query("qrCodeUserId") String qrCodeUserId);

    /**
     * 审核用户加入组织
     *
     * @param urbrId
     * @param flag
     * @return
     */
    @GET("devApproveUserRegisterBandOrg.form")
    Observable<UserInfo> userCheck(@Query("urbrId") String urbrId,
                                   @Query("flag") Integer flag);


    /**
     * 文件上传接口   2018-04-04
     *
     * @return
     */
    @Multipart
    @POST("dfsFileUpload.form")
    Observable<UploadIds> uploadFile(@Part() List<MultipartBody.Part> parts);

    /**
     * 拜访记录提交   文件系统和信息系统分开  2018-04-04
     *
     * @param userId
     * @param phone
     * @param timeStart
     * @param timeEnd
     * @param name
     * @param addresss
     * @param gps
     * @param content
     * @param url
     * @return
     */
    @POST("visitRecord1.form")
    Observable<UploadIds> setSubmitVisitData(@Query("userId") String userId,
                                             @Query("phone") String phone,
                                             @Query("timeStart") Long timeStart,
                                             @Query("timeEnd") Long timeEnd,
                                             @Query("name") String name,
                                             @Query("addresss") String addresss,
                                             @Query("gps") String gps,
                                             @Query("content") String content,
                                             @Query("url") String url);
}
