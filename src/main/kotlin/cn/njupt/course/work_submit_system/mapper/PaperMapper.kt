package cn.njupt.course.work_submit_system.mapper

import cn.njupt.course.work_submit_system.entity.Homework
import cn.njupt.course.work_submit_system.entity.Paper
import cn.njupt.course.work_submit_system.util.use
import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.templates.RowMapper
import io.vertx.sqlclient.templates.SqlTemplate
import io.vertx.sqlclient.templates.TupleMapper

class PaperMapper(private val client:MySQLPool) {

  companion object {
    val paperRowMapper = RowMapper {
      var paper = Paper()
      paper.id = it.getInteger("id")
      paper.result = Paper.Result.valueOf(it.getString("result"))
      paper.studentId = it.getInteger("student_id")
      paper.questionId = it.getInteger("question_id")
      paper.createTime = it.getLocalDateTime("create_time")
      paper.answer = it.getString("answer")
      paper.homeworkId = it.getInteger("homework_id")
      paper
    }


    val paperFromMapper = TupleMapper.mapper<Paper> {
      var res = mutableMapOf<String, Any>()
      if (it.id != null) res["id"] = it.id
      res["result"] = it.result.name
      res["studentId"] = it.studentId
      res["questionId"] = it.questionId
      res["createTime"] = it.createTime
      res["answer"] = it.answer
      res["homeworkId"] = it.homeworkId
      res
    }


  }


  suspend fun insertPaper(paper: Paper) = client.connection.await()
    .use {
      //language=sql
      SqlTemplate.forUpdate(this,"INSERT INTO paper (answer, `result`, student_id,question_id,homework_id, create_time) VALUES (#{answer}, #{result}, #{studentId},#{questionId}, #{homeworkId}, #{createTime}) ")
        .mapFrom(paperFromMapper)
        .execute(paper).await()
        .rowCount()
    }

  suspend fun selectByHomeworkId(homeworkId: Int) = client.connection.await().use {
    SqlTemplate.forQuery(this,"""
      SELECT * FROM paper WHERE homework_id = #{id}
    """.trimIndent())
      .mapTo(paperRowMapper)
      .execute(mapOf("id" to homeworkId)).await()
      .toList()
  }

  suspend fun insertPaperBatch(papers:List<Paper>) = client.connection.await()
    .use {
      val transaction = begin().await()
      val rowCount = try {
      SqlTemplate.forUpdate(
        this,
        "INSERT INTO paper (answer, `result`, student_id,question_id,homework_id, create_time) VALUES (#{answer}, #{result}, #{studentId},#{questionId}, #{homeworkId}, #{createTime}) "
      )
        .mapFrom(paperFromMapper)
        .executeBatch(papers).await()
        .rowCount()
    } catch (e: Exception) {
       transaction.rollback()
        throw  e
    }
      transaction.commit().await()

      rowCount
    }


  suspend fun countPaper(homeworkId :Int,studentId :Int) = client.connection.await().use {
  //language=SQL
    SqlTemplate.forUpdate(this,"SELECT COUNT(*) as count FROM paper WHERE homework_id = #{homeworkId} and student_id = #{studentId}")
      .mapTo(RowMapper { it.getInteger("count") })
      .execute(mapOf("homeworkId" to homeworkId,"studentId" to studentId)).await().first()
  }


  suspend fun updatePaperResult(paperId :Int,result: Paper.Result)=
   client.connection.await().use {
    //language=sql
      SqlTemplate.forUpdate(this,"""
        UPDATE paper SET `result` = #{result} WHERE id = #{id}
      """.trimIndent())
        .execute(mapOf("result" to result.name,"id" to paperId)).await()
        .rowCount()
  }
}
