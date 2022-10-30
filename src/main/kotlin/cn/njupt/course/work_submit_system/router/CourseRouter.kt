package cn.njupt.course.work_submit_system.router

import cn.njupt.course.work_submit_system.entity.CourseRecord
import cn.njupt.course.work_submit_system.entity.LoginUser
import cn.njupt.course.work_submit_system.mapper.CourseMapper
import cn.njupt.course.work_submit_system.util.RoleLimitHandler
import cn.njupt.course.work_submit_system.util.RoleLimitHandler.Companion.currentUser
import cn.njupt.course.work_submit_system.util.suspendHandler
import io.vertx.core.impl.NoStackTraceThrowable
import io.vertx.ext.web.Router

class CourseRouter(router: Router,private val courseMapper: CourseMapper) {
  companion object{
    const val prefix = "/course"
  }
  init {
      getCourseAll(router)
    subscribeCourse(router)
  }
  fun getCourseAll(router: Router){
    router.get(prefix)
      .handler(RoleLimitHandler(LoginUser.Role.TEACHER,LoginUser.Role.STUDENT))
      .suspendHandler {
        val user = it.currentUser()
        val role = user.role
        when(role){
          LoginUser.Role.TEACHER -> courseMapper.selectCoursesByTeacherId(user.id)
          LoginUser.Role.STUDENT -> courseMapper.selectCourseRecordByStudent(user.id)
          else -> listOf()
        }
      }
  }

  fun subscribeCourse(router: Router){
    router.post("$prefix/:courseId")
      .handler(RoleLimitHandler(LoginUser.Role.STUDENT))
      .suspendHandler {
        var courseId = it.pathParam("courseId").toInt()
        var studentId = it.currentUser().id
        if (courseMapper.selectCoursesById(courseId) == null) throw NoStackTraceThrowable("不存在的课程id")

        var hasSub = courseMapper.selectCourseRecordByStudent(studentId)
          .find { courseRecordDisplay -> courseRecordDisplay.courseId == courseId } != null
        if (hasSub) throw NoStackTraceThrowable("不要重复选课")
        val cr = CourseRecord()
        cr.courseId = courseId
        cr.studentId = studentId
        courseMapper.insertCourseRecord(cr)
      }
  }

}
