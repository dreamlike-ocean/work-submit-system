package cn.njupt.course.work_submit_system.util

import cn.njupt.course.work_submit_system.entity.Homework
import cn.njupt.course.work_submit_system.entity.OnceHomework
import cn.njupt.course.work_submit_system.entity.Question
import cn.njupt.course.work_submit_system.mapper.CourseMapper
import cn.njupt.course.work_submit_system.mapper.HomeworkMapper
import io.vertx.core.Vertx
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.kotlin.mysqlclient.mySQLConnectOptionsOf
import io.vertx.kotlin.sqlclient.poolOptionsOf
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.SqlConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


data class Response( val body:Any?, val statusCode : Int, val msg:String)

suspend fun <T> SqlConnection.use(fn :suspend SqlConnection.()->T):T{

  try {
    val res = fn(this)
    close()
    return res
  } catch (e: Exception) {
    close()
    throw e
  }
}


fun <T> Route.suspendHandler(fn : suspend  (RoutingContext) -> T){
  handler {
    CoroutineScope(it.vertx().dispatcher() as CoroutineContext).launch {
      try {
        var res = fn(it)
        it.json(Response(res,200,""))
      } catch (e: Throwable) {
        it.fail(e)
      }
    }
  }
}

fun getParams(params :List<String>,prefix:String):Map<String,String>{
  return params
    .mapIndexed { index, content -> "$prefix$index" to content }.toMap()
}

