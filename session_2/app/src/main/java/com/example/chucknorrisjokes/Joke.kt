package com.example.chucknorrisjokes

import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class Joke(val icon_url: String, val id: String, val url: String, val value: String)

fun main() {
    // Json also has .Default configuration which provides more reasonable settings,
    // but is subject to change in future versions
    val json = Json(JsonConfiguration.Stable)
    // serializing objects
    val jsonData = json.stringify(Joke.serializer(), Joke("icon", "id", "url", "value"))
    // serializing lists
    val jsonList = json.stringify(Joke.serializer().list, listOf(Joke("icon", "id", "url", "value")))
    println(jsonData) // {"a": 42, "b": "42"}
    println(jsonList) // [{"a": 42, "b": "42"}]

    // parsing data back
    val obj = json.parse(
        Joke.serializer(), """{
          "icon_url" : "https://assets.chucknorris.host/img/avatar/chuck-norris.png",
          "id" : "kvbADxuyS36ug4MJ7KMBMA",
          "url" : "https://api.chucknorris.io/jokes/kvbADxuyS36ug4MJ7KMBMA",
          "value" : "Chuck Norris puts mustard gas on his hotdogs."
        }"""
    ) // b is optional since it has default value
    println(obj) // Data(a=42, b="42")
}
