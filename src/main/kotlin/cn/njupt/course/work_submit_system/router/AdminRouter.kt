package cn.njupt.course.work_submit_system.router

import cn.njupt.course.work_submit_system.entity.Course
import cn.njupt.course.work_submit_system.entity.LoginUser
import cn.njupt.course.work_submit_system.mapper.CourseMapper
import cn.njupt.course.work_submit_system.mapper.LoginUserMapper
import cn.njupt.course.work_submit_system.util.RoleLimitHandler
import cn.njupt.course.work_submit_system.util.RoleLimitHandler.Companion.currentUser
import cn.njupt.course.work_submit_system.util.suspendHandler
import io.vertx.core.impl.NoStackTraceThrowable
import io.vertx.core.json.JsonArray
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.core.json.jackson.JacksonCodec
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler


class AdminRouter(router: Router,private val userMapper: LoginUserMapper,private val courseMapper: CourseMapper) {
  companion object{
    const val prefix = "/admin"
  }

  init {
    registerUsers(router)
    registerCourse(router)
    getUser(router)
  }




  fun registerUsers(router: Router){
    router.post("$prefix/registerUser")
      .handler(BodyHandler.create(false))
      .handler(RoleLimitHandler(listOf(LoginUser.Role.ADMIN)))
      .suspendHandler {
        val users = it.body().asJsonArray().map { s -> DatabindCodec.mapper().convertValue(s, LoginUser::class.java) }

        if (users.find { !it.check() } != null) {
          throw NoStackTraceThrowable("不合法的参数")
        }

        userMapper.insertUser(users)
      }
  }

  fun registerCourse(router: Router){
    router.post("$prefix/registerCourse")
      .handler(BodyHandler.create(false))
      .handler(RoleLimitHandler(listOf(LoginUser.Role.ADMIN)))
      .suspendHandler {
        val course = it.body().asPojo(Course::class.java)
        if(course == null || !course.check()){
          throw NoStackTraceThrowable("不合法的参数")
        }
        val teacher = userMapper.selectById(course.teacherId)
        if (teacher == null || teacher.role != LoginUser.Role.TEACHER) {
          throw NoStackTraceThrowable("对应id不为教师")
        }
        courseMapper.insertCourse(course)
      }
  }

  fun getUser(router: Router){
    router.get("$prefix/user/:id")
      .handler(RoleLimitHandler(LoginUser.Role.ADMIN,LoginUser.Role.TEACHER))
      .suspendHandler {
        var id = it.pathParam("id").toInt()
        var role = it.currentUser().role
        var user = userMapper.selectById(id)
        user?.also {
          if (role!=LoginUser.Role.ADMIN){
            user.password = "************"
          }
        }
      }
  }
}
