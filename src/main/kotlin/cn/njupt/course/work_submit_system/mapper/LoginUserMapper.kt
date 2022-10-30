package cn.njupt.course.work_submit_system.mapper

import cn.njupt.course.work_submit_system.entity.LoginUser
import cn.njupt.course.work_submit_system.entity.LoginUser.Role
import cn.njupt.course.work_submit_system.util.getParams
import cn.njupt.course.work_submit_system.util.use
import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.templates.RowMapper
import io.vertx.sqlclient.templates.SqlTemplate
class LoginUserMapper(private val client :MySQLPool) {
  companion object{
     val userMapper  = RowMapper {
      val user = LoginUser()
       user.id = it.getInteger("id")
       user.username = it.getString("username")
       user.password = it.getString("password")
       user.role = Role.valueOf(it.getString("role"))
      user
    }
  }

  suspend fun selectByUsername(username:String): LoginUser?{
    return client.connection.await()
      .use {
        SqlTemplate.forQuery(this, "SELECT * FROM login_user where username =#{username}")
          .mapTo(userMapper)
          .execute(mapOf("username" to username))
          .await().firstOrNull()
      }
  }

  suspend fun selectById(id:Int): LoginUser? {
    return client.connection.await()
      .use {
        SqlTemplate.forQuery(this, "SELECT * FROM login_user where id =#{id}")
          .mapTo(userMapper)
          .execute(mapOf("id" to id))
          .await().firstOrNull()
      }
  }

  suspend fun selectByUsername(usernames :List<String>):List<LoginUser> {
    if (usernames.isEmpty()) return listOf()
    var params = usernames
      .mapIndexed { index, _ -> "#{username$index}" }
    //language=sql
    val sql = "SELECT * FROM login_user WHERE username in (${params.joinToString(",")})"
    return client.connection.await().use {
      SqlTemplate.forQuery(this,sql)
        .mapTo(userMapper)
        .execute(getParams(usernames,"username")).await()
        .toList()
    }
  }

  suspend fun insertUser(users:List<LoginUser>):Int{
    val waitInsert = mutableListOf<LoginUser>()
    waitInsert.addAll(users)
    val exist = selectByUsername(users.map { it.username }).map { it.username }.toSet()
    waitInsert.removeAll { exist.contains(it.username) }
    if (waitInsert.isEmpty()) return 0
    //language=sql
    val sql = """
      INSERT INTO login_user (username,password,role) VALUES ${convertInsertSql(users)}
    """.trimIndent()

    return client.connection.await().use {
      SqlTemplate.forUpdate(this,sql)
        .execute(getInsertParam(users))
        .await()
        .rowCount()
    }
  }


  private fun convertInsertSql(users:List<LoginUser>) =  List(users.size) { index -> "(#{username$index},#{password$index},#{role$index})"}.joinToString(",")

  private fun getInsertParam(users:List<LoginUser>) = users
    .flatMapIndexed{index,user-> listOf("username$index" to user.username,"password$index" to user.password,"role$index" to user.role.name) }
    .toMap()
}
