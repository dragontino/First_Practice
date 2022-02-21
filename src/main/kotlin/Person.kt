
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class Person(var name: String, var age: Int, val gender: Gender) {

    constructor(name: String, age: Int, gender: String): this(
        name,
        age,
        when (gender) {
            "М", "M", "m", "м" -> Gender.Male
            "Ж", "F", "f", "ж" -> Gender.Female
            else -> Gender.NotDefined
        }
    )

    companion object {
        fun fromJson(jsonString: String) =
            Json.decodeFromString(serializer(), jsonString)
    }

    fun toJson() =
        Json.encodeToJsonElement(serializer(), this)

    operator fun get(property: String) = when(property) {
        "name" -> name
        "age" -> age.toString()
        "gender" -> gender.name
        else -> null
    }
}



enum class Gender {
    Male,
    Female,
    NotDefined
}