package cn.njupt.course.work_submit_system.mapper

import cn.njupt.course.work_submit_system.util.use
import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.templates.RowMapper
import io.vertx.sqlclient.templates.SqlTemplate

class NormMapper(private val client : MySQLPool) {

  suspend fun checkPaperBelongTeacher(paperId: Int, teacherId: Int): Boolean {
    return client.connection.await()
      .use {
        val teacherIdDB = SqlTemplate.forQuery(
          this, """
            SELECT teacher_id FROM course WHERE id = (
                  SELECT course_id FROM homework WHERE id = (select homework_id from paper where id = #{id}))
                  """
        )
          .mapTo(RowMapper { it.getInteger("teacher_id") })
          .execute(mapOf("id" to paperId)).await().firstOrNull()
        teacherId == teacherIdDB
      }
  }

  suspend fun checkHomeworkBelongTeacher(homeworkId: Int, teacherId: Int) = client.connection.await()
    .use {
      val teacherIdDB = SqlTemplate.forQuery(
        this, """
            SELECT teacher_id FROM course WHERE id = (
                  SELECT course_id FROM homework WHERE id = #{homeworkId})
                  """
      )
        .mapTo(RowMapper { it.getInteger("teacher_id") })
        .execute(mapOf("homeworkId" to homeworkId)).await().firstOrNull()
      teacherId == teacherIdDB
    }
}

