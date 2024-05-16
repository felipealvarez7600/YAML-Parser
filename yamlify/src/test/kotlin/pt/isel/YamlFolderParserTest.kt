import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pt.isel.AbstractYamlParser
import pt.isel.YamlFolderParser
import pt.isel.YamlParserReflect
import pt.isel.test.Student
import java.io.File
import kotlin.reflect.KClass

class YamlFolderParserTest {
    @Test
    fun testParseFolderEager() {
        val folderPath = "folder"
        val parser = YamlParserReflect(Student::class)
        val folderParser = YamlFolderParser(parser)

        val objects = folderParser.parseFolderEager(folderPath)
        assert(objects.isNotEmpty())
        objects.forEach { println(it) }
    }
}
