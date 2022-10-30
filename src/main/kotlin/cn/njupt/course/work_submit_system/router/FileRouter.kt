package cn.njupt.course.work_submit_system.router

import cn.njupt.course.work_submit_system.util.suspendHandler
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.vertx.kotlin.coroutines.await
import java.nio.file.Files
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

class FileRouter(router: Router) {

  companion object{
    const val dict = "homeworkFile"
    const val prefix = "/file"
  }
  init {
      uploadFile(router)
    getFile(router)
  }


  fun uploadFile(router: Router){
    router.post(prefix)
      .handler(BodyHandler.create(dict))
      .suspendHandler {
        var fileUploads = it.fileUploads()
        fileUploads.map{   it.uploadedFileName().substring(dict.length+1) }
      }
  }

  fun getFile(router: Router){
    router.get("$prefix/:filename")
      .handler {rc ->
        rc.response().sendFile("$dict/${rc.pathParam("filename")}")
          .onFailure{ rc.fail(it) }
      }
  }


}
