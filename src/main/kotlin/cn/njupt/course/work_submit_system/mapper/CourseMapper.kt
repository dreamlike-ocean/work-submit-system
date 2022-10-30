package cn.njupt.course.work_submit_system.mapper

import cn.njupt.course.work_submit_system.entity.Course
import cn.njupt.course.work_submit_system.entity.CourseRecord
import cn.njupt.course.work_submit_system.entity.CourseRecordDisplay
import cn.njupt.course.work_submit_system.util.use
import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.templates.RowMapper
import io.vertx.sqlclient.templates.SqlTemplate
import io.vertx.sqlclient.templates.TupleMapper

class CourseMapper(private val client:MySQLPool) {
  companion object{
    val courseRowMapper = RowMapper {
      var course = Course()
      course.id = it.getInteger("id")
      course.teacherId = it.getInteger("teacher_id")
      course.desc = it.getString("desc")
      course.name = it.getString("name")
      course.creatTime = it.getLocalDateTime("create_time")
      course
    }


    val courseFromMapper = TupleMapper.mapper<Course> {
      var res = mutableMapOf<String, Any>()
      if (it.id != null) res["id"] = it.id
      res["teacherId"] = it.teacherId
      res["desc"] = it.desc
      res["name"] = it.name
      res["createTime"] = it.creatTime
      res
    }

    val courseRecordRowMapper = RowMapper {
      var course = CourseRecord()
      course.id = it.getInteger("id")
      course.studentId = it.getInteger("student_id")
      course.courseId = it.getInteger("course_id")
      course.creatTime = it.getLocalDateTime("create_time")
      course
    }

    val CourseDisplayRowMapper = RowMapper {
      var course = CourseRecordDisplay()
      course.id = it.getInteger("id")
      course.studentId = it.getInteger("student_id")
      course.courseId = it.getInteger("course_id")
      course.courseName = it.getString("course_name")
      course.creatTime = it.getLocalDateTime("create_time")
      course
    }


    val courseRecordFromMapper = TupleMapper.mapper<CourseRecord> {
      var res = mutableMapOf<String, Any>()
      if (it.id != null) res["id"] = it.id
      res["studentId"] = it.studentId
      res["courseId"] = it.courseId
      res["createTime"] = it.creatTime
      res
    }
  }


  suspend fun insertCourse(course: Course)=
    client.connection.await()
      .use {
        //language=sql
        SqlTemplate.forUpdate(this,"INSERT INTO course (teacher_id,`desc`,name,create_time) VALUES (#{teacherId},#{desc},#{name},#{createTime})")
          .mapFrom(courseFromMapper)
          .execute(course).await().rowCount()
      }



  suspend fun selectCoursesByTeacherId(id : Int) = client.connection.await().use {
    //language=sql
    SqlTemplate.forQuery(this,"SELECT * FROM course where teacher_id = #{id}")
      .mapTo(courseRowMapper)
      .execute(mapOf("id" to id)).await()
      .toList()
  }

  suspend fun selectCoursesById(id : Int) = client.connection.await().use {
    //language=sql
    SqlTemplate.forQuery(this,"SELECT * FROM course where id = #{id}")
      .mapTo(courseRowMapper)
      .execute(mapOf("id" to id)).await()
      .firstOrNull()
  }


  suspend fun insertCourseRecord(courseRecord: CourseRecord) = client.connection.await()
    .use {
      //language=sql
      SqlTemplate.forUpdate(this,"INSERT INTO course_record (student_id,course_id,create_time) VALUES (#{studentId},#{courseId},#{createTime})")
        .mapFrom(courseRecordFromMapper)
        .execute(courseRecord).await().rowCount()
    }

  suspend fun selectCourseRecordByStudent(studentId :Int) = client.connection.await()
    .use {
      //language=sql
      SqlTemplate.forQuery(this,"""
        SELECT cr.*,c.name course_name FROM course_record cr
        LEFT JOIN course c on c.id = cr.course_id
        WHERE cr.student_id = #{studentId}
      """.trimIndent())
        .mapTo(CourseDisplayRowMapper)
        .execute(mapOf("studentId" to studentId)).await()
        .toList()
    }




}
