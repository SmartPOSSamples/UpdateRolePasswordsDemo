package com.wizarpos.wizarviewagentassistant.aidl;
interface IModifyAdminPwdService{
   boolean modifyAdminPwd(String oldPwd, String newPwd);
   boolean isAdminPwd(String pwd);
//   @Deprecated
   boolean reset(String pwd);// system has deprecated this method.
   boolean forceModifyAdminPwd(String newPwd);

   boolean isUserPwd(String pwd);//USER_PWD
   boolean forceModifyUserPwd(String newPwd);//USER_PWD_MODIFY
   boolean enableUserLogin(boolean eneable);//USER_PWD_MODIFY
}