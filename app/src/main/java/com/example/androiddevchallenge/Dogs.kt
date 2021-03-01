package com.example.androiddevchallenge

import androidx.annotation.DrawableRes
import java.util.*

val dogNames = listOf(
    "Bella", "Charlie", "Luna", "Lucy", "Max", "Bailey", "Cooper", "Daisy", "Sadie", "Molly", "Buddy",
    "Lola", "Stella", "Tucker", "Bentley", "Zoey", "Harley", "Maggie", "Riley", "Bear", "Sophie",
    "Duke", "Jax"
).shuffled(Random(1234))

enum class Puppy(
    val dogName: String,
    val photographName: String,
    val link: String,
    val race: DogRace,
    @DrawableRes val smallRes: Int
) {
    Rebecca(dogNames[0], "Rebecca De Bautista", "https://unsplash.com/@ilcocoloco", DogRace.AIREDALE_TERRIER, R.drawable.rebecca_de_bautista_6eofcm4qea4_unsplash),
    Tammy(dogNames[1], "Tammy Gann", "https://unsplash.com/@tammynaz", DogRace.AIREDALE_TERRIER, R.drawable.tammy_gann__yr96i_pjhk_unsplash),
    Berkay(dogNames[2], "Berkay Gumustekin", "https://unsplash.com/@berkaygumustekin", DogRace.GOLDEN_RETRIEVER, R.drawable.berkay_gumustekin_ngqyo2ayyne_unsplash),
    Patrick(dogNames[3], "Patrick Kool", "https://unsplash.com/@patrick62", DogRace.CAVALIER_KING, R.drawable.patrick_kool_06efqvjkib8_unsplash),
    Joyce(dogNames[4], "Joyce Zuijdwegt", "https://unsplash.com/@angryyoungpoor", DogRace.CHIHUAHUA, R.drawable.joyce_zuijdwegt_gqydwlkye0s_unsplash)
}
