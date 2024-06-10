package br.univali.eng2.santissimo.unimaps_compose

import android.os.AsyncTask
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.net.URLConnection
import java.time.LocalTime
import kotlin.random.Random

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
		val real_path = "http://192.168.1.110:8000" + this.path

		val urlConn:   URLConnection
		var bufReader: BufferedReader? = null

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

object ServiceControl {

	var loadedServices: MutableMap<Int, EncapsulatedService> = HashMap()

	init {

		val serviceIdsResult = ServiceIdsLoadTask().execute().get()!!

		val count = serviceIdsResult.length()

		for (index in 0..< count) {
			val objid = serviceIdsResult.getJSONObject(index).getInt("id_servico")
			ServiceControl.loadedServices.put(objid,
				EncapsulatedService(ServiceLoadTask(objid).execute(), objid)
			)
		}

	}

	fun fetchServiceById(id: Int): Service? {
		return loadedServices[id]!!.getService()
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
			val obj = this.loadtask!!.get().getJSONObject(0) ?: return null
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
				commentCount = 0
			)
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
		class Comment(val body: String)

		val comments = mutableStateListOf<Comment>()
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
	}
	enum class ServiceType
	{
		Pizzaplace
	}

}