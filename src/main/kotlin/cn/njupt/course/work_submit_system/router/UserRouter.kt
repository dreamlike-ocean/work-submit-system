package cn.njupt.course.work_submit_system.router

import cn.njupt.course.work_submit_system.entity.LoginUser
import cn.njupt.course.work_submit_system.mapper.LoginUserMapper
import cn.njupt.course.work_submit_system.util.RoleLimitHandler.Companion.currentUser
import cn.njupt.course.work_submit_system.util.RoleLimitHandler.Companion.setCurrentUser
import cn.njupt.course.work_submit_system.util.suspendHandler
import io.vertx.core.impl.NoStackTraceThrowable
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler

class UserRouter(val userMapper: LoginUserMapper,router: Router) {
  init {
    login(router)
    currentUser(router)
  }

  fun login(router: Router){
    router.post("/login")
      .handler(BodyHandler.create(false))
      .suspendHandler {
        val user = it.body().asPojo(LoginUser::class.java)
        if (!user.loginCheck()) throw NoStackTraceThrowable("不合法的参数")

        val userDB = userMapper.selectByUsername(user.username)

        if (userDB == null || userDB.password != user.password) throw NoStackTraceThrowable("当前用户不存在")
        it.setCurrentUser(userDB)
        "登录成功"
      }
  }

  fun currentUser(router: Router){
    router.get("/currentUser")
      .suspendHandler { it ->
        var user = it.currentUser() ?: throw NoStackTraceThrowable("无权访问")
        user.also {
          it.password = "********"
        }

      }
  }


}
