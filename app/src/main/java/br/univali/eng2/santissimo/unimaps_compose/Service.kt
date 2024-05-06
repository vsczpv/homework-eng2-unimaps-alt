package br.univali.eng2.santissimo.unimaps_compose

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import java.time.LocalTime
import kotlin.math.min
import kotlin.random.Random

object ServiceControl {

	var loadedServices: MutableMap<Int, Service> = HashMap()

	init {
		for (id in 0..8) {
			loadedServices.put(id,
				Service(
					id           = id,
					name         = "Tentação do Mate",
					location     = "C2",
					complement   = "110",
					openTime     = LocalTime.of(1, 0),
					closedTime   = LocalTime.of(Random.nextInt(17, 23), 30),
					peakTime     = LocalTime.of(12, 15),
					catergory    = Service.ServiceCatergory.Food,
					type         = Service.ServiceType.Pizzaplace,
					capacity     = 20,
					rating       = 9,
					commentCount = 12
				)
			)
		}
	}

	// TODO
	fun fetchServiceById(id: Int): Service? {
		return loadedServices[id]
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

	enum class ServiceStatus
	{
		Open,
		Closed
	}

	enum class ServiceCatergory
	{
		Food,
		Sanitary,
		Other
	}

	enum class ServiceType
	{
		Pizzaplace
	}

}