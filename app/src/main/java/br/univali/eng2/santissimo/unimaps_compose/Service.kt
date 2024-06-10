package br.univali.eng2.santissimo.unimaps_compose

import android.os.AsyncTask
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.platform.LocalContext
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.net.URLConnection
import java.time.LocalTime
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.*

open class RESTLoadTask(val path: String) : AsyncTask<Void, Void, JSONArray>() {
	override fun onPostExecute(result: JSONArray?) {
		try {
			Log.i("RESTLoadTask", "Data Fetched:" + result.toString())
		}
		catch (e: JSONException) {
			Log.e("RestLoadTask", "Data Not Fetched", e)
		}
	}
	override fun doInBackground(vararg params: Void?): JSONArray? {
		val real_path = Globals.backendAddress + this.path

		val urlConn:   URLConnection
		var bufReader: BufferedReader? = null

		Log.i("UNIMAPS RESTLoadTask", "About to load $path")

		try {
			val url = URL(real_path)
			urlConn = url.openConnection()
			bufReader = BufferedReader(InputStreamReader(urlConn.getInputStream()))

			val stringBuffer = StringBuffer()
			var line: String?

			while (true) {
				line = bufReader.readLine()
				if (line == null) break
				stringBuffer.append(line)
			}

			return JSONArray(stringBuffer.toString())
		}
		catch (e: Exception) {
			Log.e("RESTLoadTask", "", e)
			return null
		}
		finally {
			if (bufReader != null)
			{
				try {
					bufReader.close()
				} catch (e: IOException) {
					e.printStackTrace()
				}
			}
		}
	}
}

class ServiceLoadTask(val id: Int) : RESTLoadTask("/service/$id")
class ServiceIdsLoadTask() : RESTLoadTask("/service")

class CommentIdsLoadTask(val serviceId: Int) : RESTLoadTask("/service/$serviceId/comments")
class CommentLoadTask(val commentId: Int) : RESTLoadTask("/comment/$commentId")

object ServiceControl {

	var isLoaded = false

	var loadedServices: MutableMap<Int, EncapsulatedService> = HashMap()

	init {
		bootstrapNet()
	}

	private fun bootstrapNet() {

		val serviceIdsResult = ServiceIdsLoadTask().execute().get()

		if (serviceIdsResult == null) {
			Executors.newSingleThreadScheduledExecutor().schedule({
				bootstrapNet()
			}, 2, SECONDS)
			return
		}

		this.isLoaded = true

		val count = serviceIdsResult.length()

		for (index in 0..<count) {
			val objid = serviceIdsResult.getJSONObject(index).getInt("id_servico")
			ServiceControl.loadedServices.put(
				objid,
				EncapsulatedService(ServiceLoadTask(objid).execute(), objid)
			)
		}

	}

	fun fetchServiceById(id: Int): Service? {
		return loadedServices[id]?.getService()
	}
}

class EncapsulatedService {

	private val serviceId: Int
	private var service: Service?       = null
	private var loadtask: AsyncTask<Void, Void, JSONArray>? = null

	constructor(service: Service) {
		this.loadtask  = null
		this.service   = service
		this.serviceId = service.id
	}

	constructor(loadtask: AsyncTask<Void, Void, JSONArray>, id: Int) {
		this.service   = null
		this.loadtask  = loadtask
		this.serviceId = id
	}

	fun getService(): Service? {
		if (this.loadtask != null) {

			val obj = this.loadtask?.get()?.getJSONObject(0) ?: return null

			val commentsIdsResult = CommentIdsLoadTask(this.serviceId).execute().get()!!
			val commentCount = commentsIdsResult.length()

			this.service = Service(
				id           = this.serviceId,
				name         = obj.getString("nome"),
				location     = obj.getString("local"),
				complement   = obj.getString("complemento"),
				openTime     = LocalTime.parse(obj.getString("horario_aberto")),
				closedTime   = LocalTime.parse(obj.getString("horario_fechado")),
				peakTime     = LocalTime.parse(obj.getString("horario_pico")),
				catergory    = Service.catergoryFromInt(obj.getInt("idf_categoria")),
				type         = Service.ServiceType.Pizzaplace,
				capacity     = obj.getInt("capacidade"),
				rating       = obj.getInt("nota"),
				commentCount = commentCount
			)

			Log.d("UNIMAPS: Service", "count: " + commentCount)

			for (index in 0 ..< commentCount) {
				val objid = commentsIdsResult.getJSONObject(index).getInt("id_comentario")
				this.service!!.comments.comments.add(
					Service.CommentControl.EncapsulatedComment(CommentLoadTask(objid).execute())
				)
			}

			this.loadtask = null
		}
		return this.service
	}
}

class Service(
	val id: Int,
	val name: String,
	val location: String,
	val complement: String,
	val openTime: LocalTime,
	val closedTime: LocalTime,
	val peakTime: LocalTime,
	val status: ServiceStatus = if
	(
		LocalTime.now() >= openTime &&
		LocalTime.now() <  closedTime
	)
	ServiceStatus.Open else ServiceStatus.Closed,
	val catergory: ServiceCatergory,
	val type: ServiceType,
	val capacity: Int,
	val rating: Int,
	val commentCount: Int
) {

	class CommentControl(val parent: Service) {
		class Comment(val body: String, val uname: String, val rating: Int, val uid: Int)

		class EncapsulatedComment {

			private var comment: Comment? = null
			private var loadtask: AsyncTask<Void, Void, JSONArray>? = null

			constructor(comment: Comment) {
				this.comment  = comment
				this.loadtask = null
			}

			constructor(loadtask: AsyncTask<Void, Void, JSONArray>) {
				this.loadtask = loadtask
				this.comment  = null
			}

			fun getComment(): Comment? {

				if (this.loadtask != null) {
					val obj = this.loadtask!!.get().getJSONObject(0) ?: return null
					this.comment = Comment(
						body   = obj.getString("conteudo"),
						uname  = obj.getString("nome"),
						rating = obj.getInt("avaliacao"),
						uid    = obj.getInt("idf_usuario")
					)
					this.loadtask = null
				}

				return this.comment
			}
		}

		val comments = mutableStateListOf<EncapsulatedComment>()
	}

	var comments = CommentControl(this)

	enum class ServiceStatus {
		Open,
		Closed
	}

	enum class ServiceCatergory {
		Food,
		Sanitary,
		Other
	}

	companion object
	{
		@JvmStatic
		fun catergoryFromInt(num: Int): ServiceCatergory {
			return when (num) {
				1 -> {
					ServiceCatergory.Food
				}

				2 -> {
					ServiceCatergory.Sanitary
				}

				3 -> {
					ServiceCatergory.Other
				}
				else -> {
					ServiceCatergory.Other
				}
			}
		}
		@JvmStatic
		fun catergoryFromString(str: String): ServiceCatergory {
			return when (str) {
				"Alimentação" -> {
					ServiceCatergory.Food
				}
				"Sanitários" -> {
					ServiceCatergory.Sanitary
				}
				"Outros" -> {
					ServiceCatergory.Other
				}
				else -> {
					ServiceCatergory.Other
				}
			}
		}
	}
	enum class ServiceType
	{
		Pizzaplace
	}

}