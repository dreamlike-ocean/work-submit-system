package cn.njupt.course.work_submit_system.util

import cn.njupt.course.work_submit_system.entity.LoginUser
import cn.njupt.course.work_submit_system.entity.LoginUser.Role
import io.vertx.core.Handler
import io.vertx.core.impl.NoStackTraceThrowable
import io.vertx.ext.web.RoutingContext

class RoleLimitHandler(private val limit:List<Role>) :Handler<RoutingContext>{

  constructor(vararg roles: Role):this(listOf(*roles))

  companion object{
    const val SESSION_KEY = "SESSION_USER"

    fun RoutingContext.currentUser() = session().get<LoginUser>(SESSION_KEY)

    fun RoutingContext.setCurrentUser(user :LoginUser) {
      session().put(SESSION_KEY,user)
    }
  }
  override fun handle(rc: RoutingContext) {
    var user = rc.session().get<LoginUser>(SESSION_KEY)
    if (user == null || !limit.contains(user.role)){
      rc.fail(NoStackTraceThrowable("无权访问"))
      return
    }
    rc.next()
  }
}
