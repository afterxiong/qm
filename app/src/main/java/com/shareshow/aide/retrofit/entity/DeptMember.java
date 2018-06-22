package com.shareshow.aide.retrofit.entity;

import java.util.List;

/**
 * Created by xiongchengguang on 2018/3/20.
 */

public class DeptMember {

    /**
     * returnCode : 1
     * message : 成功
     * data : null
     * datas : [{"urbrId":"3f9f1460-023e-49e6-9483-61c09f004549","userId":"35b095d1-4a08-47b6-aec0-5cea0ea9f7b0","userName":"熊承光","userPhone":"13971518451","orgId":"06094f5e-94d5-4c43-90da-6f1002933cc0","orgName":"后台测试公司","deptId":"fe549200-74b6-4846-8883-2efaf9581c7a","deptName":"后台测试公司部门","createTime":"2018-03-16 09:50:18","responsible":0,"forbidden":0},{"urbrId":"425fa8cc-e577-40a2-a0ca-7607086d0ea9","userId":"3f9f1460-023e-49e6-9483-61c09f004549","userName":"xiongdang","userPhone":"13125181087","orgId":"06094f5e-94d5-4c43-90da-6f1002933cc0","orgName":"后台测试公司","deptId":"fe549200-74b6-4846-8883-2efaf9581c7a","deptName":"后台测试公司部门","createTime":"2018-03-07 15:23:01","responsible":0,"forbidden":0},{"urbrId":null,"userId":"3f9f1460-023e-49e6-9483-61c09f004549","userName":"xiongdang","userPhone":"13125181087","orgId":"06094f5e-94d5-4c43-90da-6f1002933cc0","orgName":"后台测试公司","deptId":"fe549200-74b6-4846-8883-2efaf9581c7a","deptName":"后台测试公司部门","createTime":"2018-03-07 15:23:01","responsible":1,"forbidden":0}]
     * status : true
     */

    private int returnCode;
    private String message;
    private Object data;
    private boolean status;
    private List<DeptMemberInfo> datas;

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<DeptMemberInfo> getDatas() {
        return datas;
    }

    public void setDatas(List<DeptMemberInfo> datas) {
        this.datas = datas;
    }

    public static class DeptMemberInfo {
        /**
         * urbrId : 3f9f1460-023e-49e6-9483-61c09f004549
         * userId : 35b095d1-4a08-47b6-aec0-5cea0ea9f7b0
         * userName : 熊承光
         * userPhone : 13971518451
         * orgId : 06094f5e-94d5-4c43-90da-6f1002933cc0
         * orgName : 后台测试公司
         * deptId : fe549200-74b6-4846-8883-2efaf9581c7a
         * deptName : 后台测试公司部门
         * createTime : 2018-03-16 09:50:18
         * responsible : 0
         * forbidden : 0
         */

        private String urbrId;
        private String userId;
        private String userName;
        private String userPhone;
        private String orgId;
        private String orgName;
        private String deptId;
        private String deptName;
        private String createTime;
        private int responsible;
        private int forbidden;

        public String getUrbrId() {
            return urbrId;
        }

        public void setUrbrId(String urbrId) {
            this.urbrId = urbrId;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserPhone() {
            return userPhone;
        }

        public void setUserPhone(String userPhone) {
            this.userPhone = userPhone;
        }

        public String getOrgId() {
            return orgId;
        }

        public void setOrgId(String orgId) {
            this.orgId = orgId;
        }

        public String getOrgName() {
            return orgName;
        }

        public void setOrgName(String orgName) {
            this.orgName = orgName;
        }

        public String getDeptId() {
            return deptId;
        }

        public void setDeptId(String deptId) {
            this.deptId = deptId;
        }

        public String getDeptName() {
            return deptName;
        }

        public void setDeptName(String deptName) {
            this.deptName = deptName;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public int getResponsible() {
            return responsible;
        }

        public void setResponsible(int responsible) {
            this.responsible = responsible;
        }

        public int getForbidden() {
            return forbidden;
        }

        public void setForbidden(int forbidden) {
            this.forbidden = forbidden;
        }
    }
}
