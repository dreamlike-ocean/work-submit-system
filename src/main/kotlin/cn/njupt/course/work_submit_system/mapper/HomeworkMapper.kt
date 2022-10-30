package cn.njupt.course.work_submit_system.mapper

import cn.njupt.course.work_submit_system.entity.*
import cn.njupt.course.work_submit_system.util.use
import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLClient
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.templates.RowMapper
import io.vertx.sqlclient.templates.SqlTemplate
import io.vertx.sqlclient.templates.TupleMapper

class HomeworkMapper(private val client: MySQLPool) {

  companion object {
    val homeworkRowMapper = RowMapper {
      var homework = Homework()
      homework.id = it.getInteger("id")
      homework.courseId = it.getInteger("course_id")
      homework.creatTime = it.getLocalDateTime("create_time")
      homework.desc = it.getString("desc")
      homework
    }

    val questionRowMapper = RowMapper {
      var question = Question()
      question.id = it.getInteger("id")
      question.homeworkId = it.getInteger("homework_id")
      question.desc = it.getString("desc")
      question.answer = it.getString("answer")
      question.type = Question.QuestionType.valueOf(it.getString("type"))
      question
    }


    val homeworkFromMapper = TupleMapper.mapper<Homework> {
      var res = mutableMapOf<String, Any>()
      if (it.id != null) res["id"] = it.id
      res["desc"] = it.desc
      res["courseId"] = it.courseId
      res["createTime"] = it.creatTime
      res
    }


  }


  suspend fun insertHomework(homework: Homework) = client.connection.await()
    .use {
      //language=sql
      var lastInsertId = SqlTemplate.forUpdate(
        this,
        "INSERT INTO homework (course_id,`desc`,create_time) VALUES (#{courseId},#{desc},#{createTime})"
      )
        .mapFrom(homeworkFromMapper)
        .execute(homework).await().property(MySQLClient.LAST_INSERTED_ID)
      homework.id = lastInsertId.toInt()
      lastInsertId
    }

  suspend fun selectHomeworkByCourseId(courseId: Int, desc: Boolean = true) = client.connection.await()
    .use {
      SqlTemplate.forQuery(
        this, """
        SELECT * FROM homework WHERE course_id = #{courseId}
        ORDER BY id ${if (desc) "desc" else ""}
      """.trimIndent()
      )
        .mapTo(homeworkRowMapper)
        .execute(mapOf("courseId" to courseId)).await()
        .toList()
    }


  suspend fun insertQuestions(questions: List<Question>) = client.connection.await()
    .use {
      val sql = """
      INSERT INTO question (`desc`, answer, homework_id, type)  VALUES ${convertInsertSql(questions)}
    """.trimIndent()
      SqlTemplate.forUpdate(this, sql)
        .execute(getInsertParam(questions))
        .await()
        .rowCount()
    }


  suspend fun insertOnceHomework(onceHomework: OnceHomework) {
    var connection = client.connection.await()
    connection.use {
      var transaction = begin().await()

      try {
        var lastInsertId = SqlTemplate.forUpdate(
          this,
          "INSERT INTO homework (course_id,`desc`,create_time) VALUES (#{courseId},#{desc},#{createTime})"
        )
          .mapFrom(homeworkFromMapper)
          .execute(onceHomework.homework).await().property(MySQLClient.LAST_INSERTED_ID)
        onceHomework.homework.id = lastInsertId.toInt()
        onceHomework.questions.forEach { q -> q.homeworkId = lastInsertId.toInt() }

        val sql = """
      INSERT INTO question (`desc`, answer, homework_id, type)  VALUES ${convertInsertSql(onceHomework.questions)}
    """.trimIndent()
        SqlTemplate.forUpdate(this, sql)
          .execute(getInsertParam(onceHomework.questions))
          .await()
          .rowCount()
      } catch (t: Throwable) {
        transaction.rollback().await()
        throw t
      }
      transaction.commit()
      onceHomework.homework.id
    }
  }

  suspend fun getHomeworkDetailById(id: Int) = client.connection.await().use {
    val homework = SqlTemplate.forQuery(this, "SELECT * FROM homework WHERE id = #{id}")
      .mapTo(homeworkRowMapper)
      .execute(mapOf("id" to id)).await().firstOrNull()
    if (homework == null) {
      return@use null
    }

    var questions = SqlTemplate.forQuery(this, "SELECT * FROM question WHERE homework_id = #{id}")
      .mapTo(questionRowMapper)
      .execute(mapOf("id" to id)).await().toList()

    OnceHomework(homework, questions)
  }


  private fun convertInsertSql(questions: List<Question>) =
    List(questions.size) { index -> "(#{desc$index},#{answer$index},#{homework_id$index},#{type$index})" }.joinToString(
      ","
    )

  private fun getInsertParam(questions: List<Question>) = questions
    .flatMapIndexed { index, question ->
      listOf(
        "desc$index" to question.desc,
        "answer$index" to question.answer,
        "homework_id$index" to question.homeworkId,
        "type$index" to question.type.name
      )
    }
    .toMap()
}
