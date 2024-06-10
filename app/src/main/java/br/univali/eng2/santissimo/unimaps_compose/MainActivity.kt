package br.univali.eng2.santissimo.unimaps_compose

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import br.univali.eng2.santissimo.unimaps_compose.ui.theme.UNIMAPSComposeTheme
import androidx.compose.ui.unit.dp
import java.util.prefs.Preferences

object Globals {
	var backendAddress: String = "http://192.168.1.110:8000"
}

object FavoriteControl {

	var hasLoaded = false
	val favorites = mutableStateMapOf<Int, Service>()
	fun addFavorite(service: Service)  = favorites.put(service.id, service)

	fun commitStore(store: SharedPreferences?) {
		if (store == null)   return
		if (!ServiceControl.isLoaded) return
		var favs = mutableSetOf<String>()
		for (fav in favorites) {
			favs = favs.plus(fav.value.id.toString()).toMutableSet()
		}
		Log.i("FavoriteControl", "saving: " + favs.toString())
		store.edit().putStringSet("br.univali.eng2.santissimo.unimaps_compose.fav", favs).commit()
	}

	fun loadStore(store: SharedPreferences?) {
		if (store == null) return
		val isfv = store.getStringSet("br.univali.eng2.santissimo.unimaps_compose.fav", mutableSetOf<String>())
			?.map { it.toInt() }!!
		Log.i("FavoriteControl", "loading: " + isfv.toString())
		for (nf in isfv) {
			addFavorite(ServiceControl.fetchServiceById(nf) ?: continue)
		}
	}
}

object RecentsControl {

	val recents = mutableStateListOf<Service>()
	fun addRecent(service: Service) {
		if (recents.find { it.id == service.id } == null) {
			recents.add(service)
			if (recents.count() > 4) {
				recents.removeRange(0, 1)
			}
		}
	}

}

class MainActivity : ComponentActivity() {

	var favoriteStore: SharedPreferences? = null

	override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
		this.favoriteStore = this.getSharedPreferences("br.univali.eng2.santissimo.unimaps_compose", Context.MODE_PRIVATE)
		FavoriteControl.loadStore(favoriteStore)
        setContent {
            MainUI(this)
        }
	}

	override fun onPause() {
		super.onPause()
		Log.d("MainActivity", "Saving!")
		FavoriteControl.commitStore(favoriteStore)
	}
}

@Preview(showBackground = true)
@Composable
fun MainUI(atv: MainActivity? = MainActivity()) {

	val favorites = remember { FavoriteControl.favorites }
	val recents   = remember { RecentsControl.recents }

    UNIMAPSComposeTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier
	            .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            LazyColumn (
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
	                .fillMaxWidth()
	                .padding(8.dp)
            ) {
				item {
					Column {
						Text(
							text     = "Mapa",
							style    = MaterialTheme.typography.titleLarge,
							modifier = Modifier
								.padding(16.dp),
						)
	                    Widgets.MapCardButton(
							background = R.drawable.map_placeholder,
		                ) {
		                    Toast.makeText(atv!!.baseContext, "To Be Implemented", Toast.LENGTH_SHORT).show()
	                    }
					}
				}
	            /* Favoritos */
	            item {
					Column (
						modifier = Modifier
							.height(180.dp)
					){
						Text(
							text = "Favoritos",
							style = MaterialTheme.typography.titleLarge,
							modifier = Modifier
								.padding(16.dp)
								.fillMaxWidth()
						)
						if (favorites.size == 0) {
							Text(
								text = "Você não tem nenhum favorito",
								color = Color.Gray,
								modifier = Modifier
									.padding(top = 34.dp)
									.fillMaxWidth(),
								textAlign = TextAlign.Center
							)
						}
						else LazyRow(
							horizontalArrangement = Arrangement.Start,
							modifier = Modifier
								.fillMaxWidth()
						) {
							for (fav in favorites) {
								item {
									Widgets.MugshotButton(
										service = fav.value,
										if (fav.value.status == Service.ServiceStatus.Open)
											Color.Green
										else
											Color.Red,
									) {
										val navi = Intent(atv!!.baseContext, ServiceActivity::class.java)
										navi.putExtra("service", fav.value.id)
										atv.startActivity(navi)
									}
								}
							}
						}
					}
	            }
	            /* Recentes */
	            item {
		            Column (
			            modifier = Modifier
				            .height(180.dp)
		            ){
			            Text(
				            text = "Recentes",
				            style = MaterialTheme.typography.titleLarge,
				            modifier = Modifier
					            .padding(16.dp)
					            .fillMaxWidth()
			            )
			            if (recents.size == 0) {
				            Text(
					            text = "Você ainda não visualisou nenhum serviço",
					            color = Color.Gray,
					            modifier = Modifier
						            .padding(top = 34.dp)
						            .fillMaxWidth(),
					            textAlign = TextAlign.Center
				            )
			            }
			            else LazyRow(
				            horizontalArrangement = Arrangement.Start,
				            modifier = Modifier
					            .fillMaxWidth()
			            ) {
				            for (rct in recents) {
					            item {
						            Widgets.MugshotButton(
							            service = rct,
							            if (rct.status == Service.ServiceStatus.Open)
								            Color.Green
							            else
								            Color.Red,
						            ) {
							            val navi = Intent(atv!!.baseContext, ServiceActivity::class.java)
							            navi.putExtra("service", rct.id)
							            atv.startActivity(navi)
						            }
					            }
				            }
			            }
		            }
	            }
	            item {
		            Column {
						Text(
							text = "Categorias",
							style = MaterialTheme.typography.titleLarge,
							modifier = Modifier
								.padding(16.dp)
						)
						for (name in listOf("Alimentação", "Sanitários", "Outros"))
						{

							val callback = {
								val navi = Intent(atv!!.baseContext, CatergoryActivity::class.java)
								navi.putExtra("catergory", name)
								atv.startActivity(navi)
							}

							val (background, subs) = when (name) {
								"Alimentação"  -> Pair(
									R.drawable.cat_banner_food, listOf(
										"Lanchonetes",
										"Pizzarias"
									)
								)
								"Sanitários" -> Pair(
									R.drawable.cat_banner_restroom, listOf(
										"Banheiros"
									)
								)
								"Outros" -> Pair(
									R.drawable.cat_banner_other, listOf(
										"Bancos",
										"Bibliotéca",
										"Academias",
										"Farmácias"
									)
								)
								else -> throw Exception("Inexistent Catergory")
							}

							Widgets.CatergoryCardButton(name, background, subs, callback)
						}
		            }
	            }
	        }
	    }
    }
}