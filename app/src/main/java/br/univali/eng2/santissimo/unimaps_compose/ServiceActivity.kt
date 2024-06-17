package br.univali.eng2.santissimo.unimaps_compose

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import br.univali.eng2.santissimo.unimaps_compose.ui.theme.UNIMAPSComposeTheme
import coil.compose.AsyncImage
import coil.request.ImageRequest

class ServiceActivity : ComponentActivity() {
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		val serviceId = intent.extras!!.getInt("service")
		val service   = ServiceControl.fetchServiceById(serviceId)!!
		setContent {
			ServiceUI(this, service)
		}
	}
}

fun serviceCatergoryName(type: Service.ServiceCatergory): String {
	return when (type) {
		Service.ServiceCatergory.Food -> "Alimentação"
		Service.ServiceCatergory.Sanitary -> "Sanitários"
		Service.ServiceCatergory.Other -> "Outros"
	}
}

fun serviceTypeToName(type: Service.ServiceType): String {
	return when (type) {
		Service.ServiceType.Pizzaplace -> "Pizzaria"
		Service.ServiceType.Cafeteria -> "Cafeteria"
		Service.ServiceType.Restaurant -> "Restaurante"
		Service.ServiceType.Juicestand -> "Estande de Suco"
		Service.ServiceType.Snackbar -> "Lanchonete"
		Service.ServiceType.Bathroom -> "Banheiro"
		Service.ServiceType.Drugstore -> "Farmácia"
		Service.ServiceType.Market -> "Mercado"
		Service.ServiceType.Stationery -> "Papelaria"
		Service.ServiceType.Other -> "Outro"
	}
}

fun serviceItineraryToString(service: Service): String {
	return service.openTime.toString() + " até " + service.closedTime.toString()
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ServiceUI(atv: ServiceActivity = ServiceActivity(), service: Service = ServiceControl.fetchServiceById(0)!!) {

	val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
	val favorites = remember { FavoriteControl.favorites }
	val hasBeenVisited = remember { mutableStateOf(false) }

//	val rtg = remember { service.rating }

	if (!hasBeenVisited.value) {
		RecentsControl.addRecent(service)
		hasBeenVisited.value = true
	}

	UNIMAPSComposeTheme {
		// A surface container using the 'background' color from the theme
		Surface(
			modifier = Modifier.fillMaxSize(),
			color = MaterialTheme.colorScheme.background
		) {
			Box {
				AsyncImage(
					model = ImageRequest.Builder(LocalContext.current)
						.data("${Globals.backendAddress}/service/${service.id}/banner.png")
						.crossfade(true)
						.build(),
					contentDescription = null,
					contentScale = ContentScale.FillWidth,
					modifier = Modifier.fillMaxWidth(),
					onError = { error -> Log.e("ServiceActivity", "Async ", error.result.throwable)}
				)
				Scaffold(
					containerColor = Color.Transparent,
					modifier = Modifier
						.nestedScroll(scrollBehavior.nestedScrollConnection),
					topBar = {
						LargeTopAppBar(
							colors = TopAppBarDefaults.topAppBarColors(
								containerColor = Color.Transparent,
								titleContentColor = MaterialTheme.colorScheme.onPrimary
							),
							title = {
								Text(
									service.name,
									maxLines = 1,
									overflow = TextOverflow.Ellipsis,
									style = MaterialTheme.typography.headlineLarge.copy(
										shadow = Shadow(
											color = Color.Black,
											blurRadius = 16f
										),
									),
								)
							},
							navigationIcon = {
								IconButton(onClick = {
									atv.finish()
								}) {
									Icon(
										imageVector = Icons.Filled.ArrowBack,
										tint = Color.White,
										modifier = Modifier
											.background(MaterialTheme.colorScheme.primary, CircleShape)
											.padding(8.dp),
										contentDescription = null
									)
								}
							},
							actions = {
								IconButton(onClick = {
									if (favorites[service.id] != null) {
										favorites.remove(service.id)
										Toast.makeText(atv.baseContext, "Desfavoritado!", Toast.LENGTH_SHORT).show()
									}
									else {
										FavoriteControl.addFavorite(service)
										Toast.makeText(atv.baseContext, "Favoritado!", Toast.LENGTH_SHORT).show()
									}
									FavoriteControl.commitStore(atv.getSharedPreferences("br.univali.eng2.santissimo.unimaps_compose", Context.MODE_PRIVATE))
								}) {
									Icon(
										imageVector = if (favorites.containsKey(service.id))
											Icons.Filled.Favorite
										else
											Icons.Filled.FavoriteBorder,
										tint = Color.White,
										modifier = Modifier
											.background(MaterialTheme.colorScheme.primary, CircleShape)
											.padding(8.dp),
										contentDescription = null
									)
								}
							},
							scrollBehavior = scrollBehavior,
							modifier = Modifier
								.background(
									brush = Brush.verticalGradient(
										0.0f to Color(0x00000000),
										1.0f to Color.Black.copy(alpha = 0.8f)
									)
								)
							)
					}
				) { paddingValues ->
					Column(
						modifier = Modifier
							.padding(paddingValues)
							.background(Color.White)
							.padding(horizontal = 16.dp)
							.padding(top = 16.dp),
					) {
						val entries = listOf(
							Pair("Categoria", serviceCatergoryName(service.catergory)),
							Pair("Tipo", serviceTypeToName(service.type)),
							Pair("Horário", serviceItineraryToString(service))
						)
						Widgets.ServiceInfoList(entries)
						Row(
							horizontalArrangement = Arrangement.SpaceBetween,
							modifier = Modifier
								.fillMaxWidth()
								.padding(end = 4.dp)
						) {
							Widgets.ServiceInfoBit(
								label = "Pico",
								info = service.peakTime.toString()
							)
							Widgets.ServiceInfoBit(
								label = "Capacidade",
								info = service.capacity.toString()
							)
						}

						OutlinedButton(
							onClick = {
								Toast.makeText(atv.baseContext, "To Be Implemented", Toast.LENGTH_SHORT).show()
							},
							modifier = Modifier
								.padding(top = 16.dp)
						) {
							Text(
								text = "Catálogo",
								style = MaterialTheme.typography.titleLarge,
								color = Color.Black,
								modifier = Modifier
									.padding(end = 4.dp)
							)
							Icon(
								imageVector = Icons.Filled.KeyboardArrowRight,
								contentDescription = null,
								tint = Color.Black,
								modifier = Modifier
									.size(24.dp)
							)
						}

						Row(
							modifier = Modifier
								.padding(top = 16.dp),
							verticalAlignment = Alignment.CenterVertically
						) {
							/* Não existe RatingBar para Compose */
							AndroidView(
								modifier = Modifier
									.width(240.dp)
									.scale(0.8f)
									.offset(x = -24.dp),
								factory = { context ->
									android.widget.RatingBar(context).apply {
										this.numStars = 5
										this.rating = (service.rating.toFloat() / 10f) * 5f
										this.setIsIndicator(true)
									}
								}
							)
							Text(
								text = "${service.commentCount} Avaliações",
								modifier = Modifier
									.offset(y = -2.dp),
							)
						}

						Text(
							text = "Local:",
							style = MaterialTheme.typography.titleLarge,
							modifier = Modifier
								.padding(top = 16.dp, start = 8.dp)
						)

						Text(
							text = "${service.location}: Próximo à sala ${service.complement}.",
							style = MaterialTheme.typography.titleMedium,
							modifier = Modifier
								.padding(start = 8.dp)
						)

						Widgets.MapCardButton(background = R.drawable.map_placeholder) {
							Toast.makeText(atv.baseContext, "To Be Implemented", Toast.LENGTH_SHORT).show()
						}

						OutlinedButton(
							onClick = {
								val navi = Intent(atv.baseContext, CommentActivity::class.java)
								navi.putExtra("service", service.id)
								atv.startActivity(navi)
							},
							modifier = Modifier
								.padding(top = 16.dp)
						) {
							Text(
								text = "Comentários",
								style = MaterialTheme.typography.titleLarge,
								color = Color.Black,
								modifier = Modifier
									.padding(end = 4.dp)
							)
							Icon(
								imageVector = Icons.Filled.KeyboardArrowRight,
								contentDescription = null,
								tint = Color.Black,
								modifier = Modifier
									.size(24.dp)
							)
						}
					}
				}
			}
		}
	}
}