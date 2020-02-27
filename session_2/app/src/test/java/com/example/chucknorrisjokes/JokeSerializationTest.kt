package com.example.chucknorrisjokes

import com.lefevrej.chucknorrisjokes.Joke
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.junit.Assert
import org.junit.Test

/**
 * /!\ THIS FILE MUST NOT BE EDITED /!\
 */
class JokeSerializationTest {
    private val baseJson =
        """{"categories":[],"created_at":"2020-01-05 13:42:26.766831","icon_url":"https://assets.chucknorris.host/img/avatar/chuck-norris.png","id":"pyNXTV7WThiNLRykGsQmrg","updated_at":"2020-01-05 13:42:26.766831","url":"https://api.chucknorris.io/jokes/pyNXTV7WThiNLRykGsQmrg","value":"The hills are alive with the sound of Chuck Norris' dong slapping against his legs while he walks."}"""

    private val baseJoke = Joke(
        categories = listOf(),
        createdAt = "2020-01-05 13:42:26.766831",
        iconUrl = "https://assets.chucknorris.host/img/avatar/chuck-norris.png",
        id = "pyNXTV7WThiNLRykGsQmrg",
        updatedAt = "2020-01-05 13:42:26.766831",
        url = "https://api.chucknorris.io/jokes/pyNXTV7WThiNLRykGsQmrg",
        value = "The hills are alive with the sound of Chuck Norris' dong slapping against his legs while he walks."
    )

    @Test
    fun `serialization is correct`() {
        val json = Json(JsonConfiguration.Stable).stringify(Joke.serializer(), baseJoke)
        Assert.assertEquals(baseJson, json)
    }

    @Test
    fun `deserialization is correct`() {
        val joke = Json(JsonConfiguration.Stable).parse(Joke.serializer(), baseJson)
        Assert.assertEquals(baseJoke, joke)
    }
}