package cn.njupt.course.work_submit_system.router

import cn.njupt.course.work_submit_system.entity.LoginUser
import cn.njupt.course.work_submit_system.entity.OnceHomework
import cn.njupt.course.work_submit_system.entity.Paper
import cn.njupt.course.work_submit_system.entity.PaperVo
import cn.njupt.course.work_submit_system.entity.WaitPaper
import cn.njupt.course.work_submit_system.mapper.CourseMapper
import cn.njupt.course.work_submit_system.mapper.HomeworkMapper
import cn.njupt.course.work_submit_system.mapper.NormMapper
import cn.njupt.course.work_submit_system.mapper.PaperMapper
import cn.njupt.course.work_submit_system.util.RoleLimitHandler
import cn.njupt.course.work_submit_system.util.RoleLimitHandler.Companion.currentUser
import cn.njupt.course.work_submit_system.util.suspendHandler
import com.fasterxml.jackson.module.kotlin.readValue
import io.vertx.core.impl.NoStackTraceThrowable
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.ext.web.handler.SessionHandler


class HomeworkRouter(
  router: Router,
  val homeworkMapper: HomeworkMapper,
  val courseMapper: CourseMapper,
  val paperMapper: PaperMapper,
  val normMapper: NormMapper
) {
  companion object {
    const val prefix = "/homework"
  }

  init {
    publishHomework(router)
    submitPaper(router)
    getHomeworks(router)
    getHomeworkDetail(router)
    modifyPaper(router)
    getPapersByHomeworkId(router)
    getMyPaperByHomeworkId(router)
  }


  fun publishHomework(router: Router) {
    router.post(prefix)
      .handler(BodyHandler.create(false))
      .handler(RoleLimitHandler(LoginUser.Role.TEACHER))
      .suspendHandler {
        val onceHomework = it.body().asPojo(OnceHomework::class.java)

        if (onceHomework == null || !onceHomework.check()) throw NoStackTraceThrowable("不合法的参数")

        val course = courseMapper.selectCoursesById(onceHomework.homework.courseId)
        if (course == null || course.teacherId != it.currentUser().id) throw NoStackTraceThrowable("当前课程不属于该老师!")

        homeworkMapper.insertOnceHomework(onceHomework)
      }

  }

  fun getHomeworks(router: Router) {
    router.get("$prefix/course/:courseId")
      .suspendHandler {
        homeworkMapper.selectHomeworkByCourseId(it.pathParam("courseId").toInt())
      }
  }


  fun getHomeworkDetail(router: Router) {
    router.get("$prefix/detail/:homeworkId")
      .suspendHandler {
        val onceHomework = homeworkMapper.getHomeworkDetailById(it.pathParam("homeworkId").toInt())
        if (it.currentUser() == null || it.currentUser().role != LoginUser.Role.TEACHER) {
          onceHomework?.questions?.forEach { it.answer = null }
        }
        onceHomework
      }
  }


  fun submitPaper(router: Router) {
    router.post("$prefix/paper")
      .handler(BodyHandler.create(false))
      .handler(RoleLimitHandler(LoginUser.Role.STUDENT))
      .suspendHandler {
        val studentId = it.currentUser().id
        val papers = it.body().asJsonArray().map { s -> DatabindCodec.mapper().convertValue(s, Paper::class.java) }
        if (papers.isEmpty() || papers.find { !it.check() } != null) {
          throw NoStackTraceThrowable("不合法的参数")
        }
        val homeworkId = papers.get(0).homeworkId


        val onceHomework = homeworkMapper.getHomeworkDetailById(homeworkId)
        if (onceHomework == null) {
          throw NoStackTraceThrowable("不存在的作业")
        }

        var countPaper = paperMapper.countPaper(homeworkId, studentId)
        if (countPaper != 0) {
          throw NoStackTraceThrowable("已经完成的作业")
        }

        papers.forEach { it.studentId = studentId }
        val questions = onceHomework.questions.associateBy { it.id }
        var paper = papers.associateBy { it.questionId }
        //校验
        papers.forEach {
          if (!questions.containsKey(it.questionId)) {
            throw NoStackTraceThrowable("不存在的问题呢")
          }
        }
        onceHomework.questions.forEach {
          if (!paper.containsKey(it.id)) {
            throw NoStackTraceThrowable("存在未作答的问题")
          }
        }
        //自动审批
        papers.forEach {
          val question = questions[it.questionId]!!

          if (question.type.isAutoCheck) {
            if (question.answer == it.answer)
              it.result = Paper.Result.RIGHT
            else
              it.result = Paper.Result.WRONG
          } else {
            it.result = Paper.Result.WAIT
          }
        }
        paperMapper.insertPaperBatch(papers)

      }
  }


  fun modifyPaper(router: Router) {
    router.put("$prefix/paper")
      .handler(BodyHandler.create())
      .handler(RoleLimitHandler(LoginUser.Role.TEACHER))
      .suspendHandler {
        val teacherId = it.currentUser().id
        var waitPaper = it.body().asPojo(WaitPaper::class.java)
        if (!waitPaper.check()) throw NoStackTraceThrowable("非法的参数")

        if (!normMapper.checkPaperBelongTeacher(waitPaper.paperId, teacherId)) {
          throw NoStackTraceThrowable("禁止批改其他教师的作业")
        }
        paperMapper.updatePaperResult(waitPaper.paperId, waitPaper.result)
      }
  }

  fun getPapersByHomeworkId(router: Router) {
    router.get("$prefix/papers/:homeworkId")
      .handler(RoleLimitHandler(LoginUser.Role.TEACHER))
      .suspendHandler {
        val teacherId = it.currentUser().id
        val homeworkId = it.pathParam("homeworkId").toInt()
        if (!normMapper.checkHomeworkBelongTeacher(homeworkId, teacherId)) {
          throw NoStackTraceThrowable("禁止查看别的教师布置的作业")
        }
        paperMapper.selectByHomeworkId(homeworkId).groupBy { it.studentId }
          .map { pair -> PaperVo().also {
            it.userId = pair.key
            it.papers = pair.value
          } }
      }
  }


  fun getMyPaperByHomeworkId(router: Router){
    router.get("$prefix/paper/:homeworkId")
      .handler(RoleLimitHandler(LoginUser.Role.STUDENT))
      .suspendHandler {
        val studentId = it.currentUser().id
        val homeworkId = it.pathParam("homeworkId").toInt()

        paperMapper.selectByHomeworkId(homeworkId).filter { it.studentId == studentId }
          .sortedBy { it.id }
      }
  }





}
