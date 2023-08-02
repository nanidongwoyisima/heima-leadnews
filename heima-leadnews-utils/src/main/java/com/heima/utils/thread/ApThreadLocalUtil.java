package com.heima.utils.thread;

import com.heima.model.user.pojos.ApUser;
import com.heima.model.wemedia.pojos.WmUser;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: 你的名字
 * @Date: 2023/07/12/10:03
 * @Description:
 */
public class ApThreadLocalUtil {

    private final  static ThreadLocal<ApUser> Ap_USER_THREAD_LOCAL=new ThreadLocal<>();

    //添加用户
    public static void  setUser(ApUser apUser){
        Ap_USER_THREAD_LOCAL.set(apUser);
    }

    /**
     *
      * @Description
     * @author enna
     * @date 2023/7/12 10:05
     * @param
     // 获取用户
     */
    public static ApUser getUser(){
        return Ap_USER_THREAD_LOCAL.get();
    }
    /**
     *
      * @Description
     * @author enna
     * @date 2023/7/12 10:07
     * 清理用户
     */
    public  static  void clear(){
        Ap_USER_THREAD_LOCAL.remove();
    }

}
