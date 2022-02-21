
import kotlinx.serialization.json.JsonElement
import java.io.File.listRoots
import java.nio.file.Path
import java.util.*
import javax.swing.filechooser.FileSystemView
import kotlin.io.path.*

val input = Scanner(System.`in`)

fun main() {
    println("Выберите команду из списка ниже и введите её номер")
    println("------Информация о дисках-----")
    println("1. Вывести информацию о дисках\n")
    println("-------Работа с файлами-------")
    println("2. Создать файл")
    println("3. Записать строку в файл")
    println("4. Вывести полное содержимое файла")
    println("5. Вывести определённую строку на экран")
    println("6. Удалить файл\n")
    println("--------Работа с JSON---------")
    println("7. Выполнить сериализацию и записать в файл")
    println("8. Вывести содержимое файла")
    println("9. Вывести конкретное свойство")
    println("10. Удалить файл")
    print(">>>  ")

    when (input.nextInt()) {
        1 -> disksInfo()
        2 -> {
            if (File().create().exists())
                println("Файл успешно создан!")
            else
                println("Не удалось создать файл")
        }
        3 -> {
            println("Введите текст для записи в файл")
            File().write(readLine() ?: "")
        }
        4 -> {
            val file = File()
            val content = file.readAll()

            if (content == null) println("Не удалось открыть файл")
            else println(
                """
                |Содержимое файла "${file.filename}":
                |${file.readAll()}
                """.trimMargin()
            )
        }
        5 -> {
            println("Введите номер строки (начиная с 1)")
            val numberLine = input.nextInt()
            val file = File()
            val line = file.readLine(numberLine)

            if (line == null) println("Не удалось открыть файл")
            else println(
                """
                |$line строка файла "${file.filename}":
                |${file.readLine(numberLine - 1)}
                |""".trimMargin()
            )
        }
        6 -> {
            if (File().delete())
                println("Удаление завершено")
            else
                println("Не удалось удалить файл")
        }
        7 -> {
            println("Создание объекта Person:")
            print("Введите имя  >>>  ")
            val name = input.next()
            print("Введите возраст  >>>  ")
            val age = input.nextInt()
            print("Введите пол (М - мужской, Ж - женский)  >>>  ")
            val genderString = input.next()

            val person = Person(name, age, genderString)
            val json = Json()

            json.write(person.toJson())

            println("Данные успешно сериализированы и записаны в файл ${json.filename}!")
        }
        8 -> {
            val string = Json().readAll()

            if (string == null) {
                println("Не удалось открыть файл")
                return
            }
            val person = Person.fromJson(string)
            println(person.toString())
        }
        9 -> {
            println("Введите название свойства класса Person (name, age или gender)")
            val property = input.next().lowercase()

            println(Json().readProperty(property) ?: "Не удалось открыть файл")
        }
        10 -> {
            if (Json().delete())
                println("Удаление завершено")
            else
                println("Не удалось удалить файл")
        }
    }
}



fun disksInfo() {
    val fsv = FileSystemView.getFileSystemView()
    val roots = listRoots()

    roots.forEach {
        println("Название: $it")
        println("Тип: ${fsv.getSystemTypeDescription(it)}")
        println("Объём диска: ${it.totalSpace} байт")
        println("Свободное пространство: ${it.freeSpace} байт")
    }
}




open class File(name: String = "") {

    val filename: String = if (name.isEmpty()) {
        println("Введите имя файла")
        input.next()
    } else name

    private val filePath =
        "C:\\Users\\petro\\IdeaProjects\\operating_systems\\src\\main\\resources\\${filename}"

    fun create() =
        Path(filePath).createFile()

    fun write(text: String) =
        getFile().appendText("$text\n")

    fun readAll() =
        checkFile()?.readText()

    fun readLine(numberLine: Int): String? {
        val lines = checkFile()?.readLines() ?: return null
        return if (numberLine >= lines.size || numberLine < 0) ""
        else lines[numberLine]
    }

    fun delete() =
        Path(filePath).deleteIfExists()



    protected fun getFile(): Path =
        checkFile() ?: create()

    private fun checkFile(): Path? {
        val path = Path(filePath)
        return if (path.exists())
            path
        else
            null
    }
}


class Json: File("Person.json") {

    fun write(jsonElement: JsonElement) =
        getFile().writeText(jsonElement.toString())

    fun readProperty(property: String): String? {
        val all = readAll() ?: return null
        val value = Person.fromJson(all)[property] ?: return "Такого свойства нет!"

        return "$property = $value"
    }
}