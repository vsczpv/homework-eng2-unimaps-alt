package br.univali.eng2.santissimo.unimaps_compose

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import br.univali.eng2.santissimo.unimaps_compose.ui.theme.UNIMAPSComposeTheme
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainUI(this)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainUI(atv: MainActivity? = null) {
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
		                )
					}
				}
	            items (2) {
		            Column {
						Text(
							text = if (it == 0) "Favoritos" else "Recentes",
							style = MaterialTheme.typography.titleLarge,
							modifier = Modifier
								.padding(16.dp)
						)
		                LazyRow (
		                    horizontalArrangement = Arrangement.Start,
		                    modifier = Modifier
		                        .fillMaxWidth()
		                ) {
							items (5) {
								Widgets.MugshotButton(
									imageId = R.drawable.ic_launcher_background
								)
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