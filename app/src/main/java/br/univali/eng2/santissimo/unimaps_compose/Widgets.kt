package br.univali.eng2.santissimo.unimaps_compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class Widgets {

	companion object {

		@JvmStatic
		@Composable
		fun CommentCard(comment: Service.CommentControl.Comment, onClick: () -> Unit = {}) {
			OutlinedCard(
				modifier = Modifier
					.fillMaxWidth()
					.padding(8.dp)
					.height(120.dp),
//					.clickable(onClick = onClick),
				colors = CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.onPrimary
				),
				border = BorderStroke(1.dp, Color.Black),
				elevation = CardDefaults.cardElevation(
					defaultElevation = 6.dp
				)
			)
			{
				Text(
					text = comment.body,
					modifier = Modifier
						.padding(16.dp),
					overflow = TextOverflow.Ellipsis
				)
			}
		}

		@JvmStatic
		@Composable
		fun MapCardButton(background: Int, onClick: () -> Unit = {}) {
			OutlinedCard(
				modifier = Modifier
					.fillMaxWidth()
					.padding(8.dp)
					.height(120.dp)
					.clickable(onClick = onClick),
				colors = CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.onPrimary
				),
				border = BorderStroke(1.dp, Color.Black),
				elevation = CardDefaults.cardElevation(
					defaultElevation = 6.dp
				)
			)
			{
				Box (modifier = Modifier.fillMaxSize()) {
					Image(
						painter            = painterResource(id = background),
						contentScale       = ContentScale.Crop,
						contentDescription = null,
						modifier = Modifier
							.fillMaxSize()
					)
				}
			}
		}

		@JvmStatic
		@Composable
		fun CatergoryCardButton(name: String, background: Int, subs: List<String>, onClick: () -> Unit = {}) {
			OutlinedCard(
				modifier = Modifier
					.fillMaxWidth()
					.padding(8.dp)
					.height(120.dp)
					.clickable(onClick = onClick),
				colors = CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.onPrimary
				),
				border = BorderStroke(1.dp, Color.Black),
				elevation = CardDefaults.cardElevation(
					defaultElevation = 6.dp
				)
			)
			{
				Box (modifier = Modifier.fillMaxSize()) {
					Image(
						painter            = painterResource(id = background),
						contentScale       = ContentScale.Crop,
						contentDescription = null,
						modifier = Modifier
							.fillMaxSize()
					)
					Column (
						modifier = Modifier
							.fillMaxSize()
							.background(
								brush = Brush.horizontalGradient(
									0.0f to Color(0x00000000),
									1.0f to Color(0xa0000000)
								)
							)
					) {}
					Row (
						modifier = Modifier
							.fillMaxSize()
							.padding(start = 8.dp, end = 8.dp, top = 12.dp),
						verticalAlignment = Alignment.Top,
						horizontalArrangement = Arrangement.SpaceBetween
					) {
						Text(
							text = name,
							style = MaterialTheme.typography.titleLarge.copy(
								shadow = Shadow(
									color = Color.Black,
									blurRadius = 16f
								)
							),
							color = Color.White
						)
						Column {
							for (sub in subs)
								Text(
									text = sub,
									style = MaterialTheme.typography.titleMedium.copy(
										shadow = Shadow(
											color = Color.Black,
											blurRadius = 16f
										)
									),
									color = Color.White
								)
						}
					}
				}
			}
		}

		@JvmStatic
		@Composable
		fun ServiceCardButton(service: Service, onClick: () -> Unit = {}) {
			val open = service.status == Service.ServiceStatus.Open
			OutlinedCard(
				modifier = Modifier
					.fillMaxWidth()
					.padding(8.dp)
					.height(120.dp)
					.clickable(onClick = onClick),
				colors = CardDefaults.cardColors(
					containerColor = MaterialTheme.colorScheme.onPrimary,
				),
				border = BorderStroke(1.dp, Color.Black),
				elevation = CardDefaults.cardElevation(
					defaultElevation = 6.dp
				)
			) {
				Box {
					Image(
							painter = painterResource(id = R.drawable.mate_defbanner),
							contentScale = ContentScale.Fit,
							contentDescription = null
					)
					Column (
						modifier = Modifier
							.fillMaxSize()
							.background(
								brush = Brush.linearGradient(
									0.0f to Color(0x00000000),
									0.4f to Color.White,
									1.0f to Color.White
								)
							)
					){}
					Column (
						modifier = Modifier
							.fillMaxWidth()
							.fillMaxHeight(),
						verticalArrangement = Arrangement.SpaceEvenly
					)  {
						Text(
							text = service.name,
							modifier = Modifier
								.padding(start = 16.dp),
							textAlign = TextAlign.Center,
							style = MaterialTheme.typography.titleLarge.copy(
								shadow = Shadow(
									color = Color.White,
									blurRadius = 16f
								),
							),
							color = Color.Black,
						)
						Text(
							text = if (open)
								"Fecha: ${service.closedTime}"
							else
								"Abre: ${service.openTime}",
							modifier = Modifier
								.padding(end = 16.dp)
								.fillMaxWidth(),
							textAlign = TextAlign.End,
							style = MaterialTheme.typography.titleSmall
						)
						Row (
							horizontalArrangement = Arrangement.End,
							modifier = Modifier
								.fillMaxWidth()
						) {
							Text(
								text  = if (open) "Aberto" else "Fechado",
								style = MaterialTheme.typography.titleSmall,
								modifier = Modifier
									.background(
										if (open) Color.Green else Color.Red,
										shape = CircleShape
									)
									.padding(4.dp)
							)
							Spacer(
								modifier = Modifier.padding(start = 64.dp)
							)
							Text(
								text = "Local: ${service.location}",
								modifier = Modifier
									.padding(end = 16.dp),
								textAlign = TextAlign.End,
								style = MaterialTheme.typography.titleSmall
							)
						}
					}
				}
			}
		}

		@JvmStatic
		@Composable
		fun MugshotButton(imageId: Int, ringColor: Color, onClick: () -> Unit = {}) {
			Image(
				painter = painterResource(imageId),
				contentDescription = "Mugshot",
				modifier = Modifier
					.size(94.dp)
					.padding(8.dp)
					.clip(CircleShape)
					.border(BorderStroke(4.dp, ringColor), CircleShape)
					.clickable(onClick = onClick)
			)
		}

		@JvmStatic
		@Composable
		fun ServiceInfoRoundbox(text: String, vpad: Dp = 0.dp) {
			Text(
				textAlign = TextAlign.Start,
				text = text,
				modifier = Modifier
					.padding(top = vpad)
					.border(
						BorderStroke(1.dp, Color.Black),
						RoundedCornerShape(8.dp)
					)
					.shadow(4.dp, RoundedCornerShape(8.dp))
					.background(
						Color.White,
						RoundedCornerShape(8.dp)
					)
					.padding(start = 6.dp, end = 6.dp)
			)
		}

		@JvmStatic
		@Composable
		fun ServiceInfoList(entries: List<Pair<String,String>>) {
			Row (
				modifier = Modifier
			){

				Column (
					modifier = Modifier
						.width(96.dp)
						.padding(vertical = 6.dp)
				){
					for (e in entries)
						Text(
							text = "${e.first}: ",
							modifier = Modifier
								.padding(4.dp)
						)
				}
				Column (
					modifier = Modifier
						.padding(top = 6.dp)
				){
					for (e in entries)
						ServiceInfoRoundbox(e.second, 6.dp)
				}

			}
		}

		@JvmStatic
		@Composable
		fun ServiceInfoBit(label: String, info: String) {
			Row (
				modifier = Modifier
					.padding(vertical = 4.dp)
			) {
				Text(
					text = "$label: ",
					modifier = Modifier
						.padding(4.dp)
				)
				ServiceInfoRoundbox(info, 4.dp)
			}
		}
	}
}