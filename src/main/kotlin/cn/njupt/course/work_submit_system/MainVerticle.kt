package cn.njupt.course.work_submit_system

import cn.njupt.course.work_submit_system.mapper.*
import cn.njupt.course.work_submit_system.router.*
import cn.njupt.course.work_submit_system.util.Response
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import io.vertx.core.Vertx
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.SessionHandler
import io.vertx.ext.web.sstore.LocalSessionStore
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.mysqlclient.mySQLConnectOptionsOf
import io.vertx.kotlin.sqlclient.poolOptionsOf
import io.vertx.mysqlclient.MySQLPool
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.system.exitProcess

suspend fun main(args: Array<String>){
  if (args.size != 2){
    println("args小于2")
    exitProcess(-1)
  }
  var vertx = Vertx.vertx()

  var mySQLPool = MySQLPool.pool(
    vertx,
    mySQLConnectOptionsOf(
      user = "root",
      password = args[1],
      host = "localhost",
      port = 3306,
      database = "worksubmit"
    ),
    poolOptionsOf(maxSize = 1)
  )
  val userMapper = LoginUserMapper(mySQLPool)
  val router = Router.router(vertx)
  router.route()
    .handler(SessionHandler.create(LocalSessionStore.create(vertx)))
    .failureHandler {
      it.failure().printStackTrace()
      it.json(Response(null,500,it.failure().message?:""))
    }

  var javaTimeModule = JavaTimeModule()
  javaTimeModule.addSerializer(LocalDateTime::class.java, LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
  javaTimeModule.addDeserializer(LocalDateTime::class.java,
    LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
  )

  DatabindCodec.mapper().registerModule(javaTimeModule)
  DatabindCodec.prettyMapper().registerModule(javaTimeModule)
  var courseMapper = CourseMapper(mySQLPool)
  val paperMapper = PaperMapper(mySQLPool)


  AdminRouter(router,userMapper, courseMapper)
  UserRouter(userMapper,router)
  HomeworkRouter(router, HomeworkMapper(mySQLPool),courseMapper,paperMapper, NormMapper(mySQLPool))
  CourseRouter(router,courseMapper)
  FileRouter(router)

  var server = vertx.createHttpServer()
    .requestHandler(router)
    .listen(args[0].toInt()).await()

  println("server is listening on ${server.actualPort()}")


}
