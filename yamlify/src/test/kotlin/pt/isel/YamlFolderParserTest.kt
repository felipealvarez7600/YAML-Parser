import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pt.isel.AbstractYamlParser
import pt.isel.YamlFolderParser
import pt.isel.YamlParserReflect
import pt.isel.test.NewStudent
import pt.isel.test.Student
import pt.isel.yamlWithImplicitValuesOfInts
import java.io.File
import kotlin.reflect.KClass

class YamlFolderParserTest {
    @Test
    fun testFolderStudentEager() {
        val folderPath = "resources/students"
        val parser = YamlParserReflect.yamlParser(Student::class)
        val folderParser = YamlFolderParser(parser)

        val objects = folderParser.parseFolderEager(folderPath).iterator()
        assertStudentsInSequence(objects)
    }

    @Test
    fun testFolderIntLazy() {
        val folderPath = "resources/int_sequence"
        val parser = YamlParserReflect.yamlParser(Int::class)
        val folderParser = YamlFolderParser(parser)

        val objects = folderParser.parseFolderLazy(folderPath).iterator()

        kotlin.test.assertEquals(1, objects.next())
        kotlin.test.assertEquals(2, objects.next())
        kotlin.test.assertEquals(4, objects.next())
        kotlin.test.assertEquals(8, objects.next())
        kotlin.test.assertEquals(16, objects.next())
        kotlin.test.assertEquals(32, objects.next())
        kotlin.test.assertEquals(64, objects.next())
        kotlin.test.assertFalse { objects.hasNext() }
    }

    @Test
    fun testFolderStringLazy() {
        val folderPath = "resources/string_sequence"
        val parser = YamlParserReflect.yamlParser(String::class)
        val folderParser = YamlFolderParser(parser)

        val objects = folderParser.parseFolderLazy(folderPath).iterator()

        kotlin.test.assertEquals("Rabanete", objects.next())
        kotlin.test.assertEquals("Cogumelo", objects.next())
        kotlin.test.assertEquals("Nabo", objects.next())
        kotlin.test.assertEquals("Couve", objects.next())
        kotlin.test.assertEquals("Alface", objects.next())
        kotlin.test.assertEquals("Batata", objects.next())
        kotlin.test.assertEquals("Cenoura", objects.next())
        kotlin.test.assertFalse { objects.hasNext() }
    }

    @Test
    fun testFolderStudentSequenceLazy() {
        val folderPath = "resources/students_sequence"
        val parser = YamlParserReflect.yamlParser(Student::class)
        val folderParser = YamlFolderParser(parser)

        val objects = folderParser.parseFolderLazy(folderPath).iterator()
        assertStudentsInSequence(objects)
    }

    @Test
    fun testFolderStudentAnnotationEager() {
        val folderPath = "resources/new_students"
        val parser = YamlParserReflect.yamlParser(NewStudent::class)
        val folderParser = YamlFolderParser(parser)

        val objects = folderParser.parseFolderEager(folderPath).iterator()
        assertStudentsInSequenceWithAnnotation(objects)
    }

    @Test
    fun testFolderStudentAnnotationLazy() {
        val folderPath = "resources/new_students_sequence"
        val parser = YamlParserReflect.yamlParser(NewStudent::class)
        val folderParser = YamlFolderParser(parser)

        val objects = folderParser.parseFolderLazy(folderPath).iterator()
        assertStudentsInSequenceWithAnnotation(objects)
    }



    private fun assertStudentsInSequenceWithAnnotation(seq: Iterator<NewStudent>) {
        val st1 = seq.next()
        kotlin.test.assertEquals("Maria Candida", st1.name)
        kotlin.test.assertEquals(873435, st1.nr)
        kotlin.test.assertEquals("Oleiros", st1.from)
        kotlin.test.assertEquals(26, st1.birth?.dayOfMonth)
        kotlin.test.assertEquals(5, st1.birth?.month?.value)
        kotlin.test.assertEquals(2004, st1.birth?.year)
        kotlin.test.assertEquals(16, st1.details?.age)
        kotlin.test.assertEquals(162, st1.details?.height)
        kotlin.test.assertEquals(null, st1.details?.year)
        kotlin.test.assertEquals(false, st1.details?.asFinished)
        val st2 = seq.next()
        kotlin.test.assertEquals("Jose Carioca", st2.name)
        kotlin.test.assertEquals(1214398, st2.nr)
        kotlin.test.assertEquals("Tamega", st2.from)
        val st3 = seq.next()
        kotlin.test.assertEquals("Pedro Ferreira", st3.name)
        kotlin.test.assertEquals(238992, st3.nr)
        kotlin.test.assertEquals("Covilhã", st3.from)
        val st4 = seq.next()
        kotlin.test.assertEquals("Carlos Serra", st4.name)
        kotlin.test.assertEquals(203941, st4.nr)
        kotlin.test.assertEquals("Rua Actor Vale", st4.address?.street)
        kotlin.test.assertEquals(14, st4.address?.nr)
        kotlin.test.assertEquals("Lisbon", st4.address?.city)
        kotlin.test.assertEquals("Alameda", st4.from)
        val st5 = seq.next()
        kotlin.test.assertEquals("Paula Martins", st5.name)
        kotlin.test.assertEquals(952304, st5.nr)
        kotlin.test.assertEquals("Amadora", st5.from)
    }

    private fun assertStudentsInSequence(seq: Iterator<Student>) {
        val st1 = seq.next()
        kotlin.test.assertEquals("Maria Candida", st1.name)
        kotlin.test.assertEquals(873435, st1.nr)
        kotlin.test.assertEquals("Rua Rosa", st1.address?.street)
        kotlin.test.assertEquals(78, st1.address?.nr)
        kotlin.test.assertEquals("Lisbon", st1.address?.city)
        kotlin.test.assertEquals("Oleiros", st1.from)
        val st2 = seq.next()
        kotlin.test.assertEquals("Jose Carioca", st2.name)
        kotlin.test.assertEquals(1214398, st2.nr)
        kotlin.test.assertEquals("Tamega", st2.from)
        val st3 = seq.next()
        kotlin.test.assertEquals("Pedro Ferreira", st3.name)
        kotlin.test.assertEquals(238992, st3.nr)
        kotlin.test.assertEquals("Covilhã", st3.from)
        val st4 = seq.next()
        kotlin.test.assertEquals("Carlos Serra", st4.name)
        kotlin.test.assertEquals(203941, st4.nr)
        kotlin.test.assertEquals("Rua Actor Vale", st4.address?.street)
        kotlin.test.assertEquals(14, st4.address?.nr)
        kotlin.test.assertEquals("Lisbon", st4.address?.city)
        kotlin.test.assertEquals("Alameda", st4.from)
        val st5 = seq.next()
        kotlin.test.assertEquals("Paula Martins", st5.name)
        kotlin.test.assertEquals(952304, st5.nr)
        kotlin.test.assertEquals("Amadora", st5.from)
    }
}