import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import pt.isel.*
import pt.isel.test.NewStudent
import pt.isel.test.Student
import pt.isel.test.StudentSimple
import pt.isel.test.StudentWithAddress
import java.io.File
import kotlin.reflect.KClass

class YamlFolderParserTest {
    @Test
    fun testFolderRefStudentEager() {
        val folderPath = "resources/reflect/students"
        val parser = YamlParserReflect.yamlParser(Student::class)
        val folderParser = YamlFolderParser(parser)

        val objects = folderParser.parseFolderEager(folderPath).iterator()
        assertStudents(objects)
    }

    @Test
    fun testFolderRefStudentLazy() {
        val folderPath = "resources/reflect/students_sequence"
        val parser = YamlParserReflect.yamlParser(Student::class)
        val folderParser = YamlFolderParser(parser)

        val objects = folderParser.parseFolderLazy(folderPath).iterator()
        assertStudents(objects)
    }

    @Test
    fun testFolderRefIntLazy() {
        val folderPath = "resources/reflect/int_sequence"
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
    fun testFolderRefStringLazy() {
        val folderPath = "resources/reflect/string_sequence"
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
    fun testFolderRefStudentAnnotationEager() {
        val folderPath = "resources/reflect/new_students"
        val parser = YamlParserReflect.yamlParser(NewStudent::class)
        val folderParser = YamlFolderParser(parser)

        val objects = folderParser.parseFolderEager(folderPath).iterator()
        assertStudentsWithAnnotation(objects)
    }

    @Test
    fun testFolderRefStudentAnnotationLazy() {
        val folderPath = "resources/reflect/new_students_sequence"
        val parser = YamlParserReflect.yamlParser(NewStudent::class)
        val folderParser = YamlFolderParser(parser)

        val objects = folderParser.parseFolderLazy(folderPath).iterator()
        assertStudentsWithAnnotation(objects)
    }

    @Test
    fun testFolderCojStudentSimpleEager() {
        val folderPath = "resources/cojen/students"
        val parser = YamlParserCojen.yamlParser(StudentSimple::class, 3)
        val folderParser = YamlFolderParser(parser)

        val objects = folderParser.parseFolderEager(folderPath).iterator()
        assertStudentsSimple(objects)
    }

    @Test
    fun testFolderCojStudentSimpleLazy() {
        val folderPath = "resources/cojen/students_sequence"
        val parser = YamlParserCojen.yamlParser(StudentSimple::class, 3)
        val folderParser = YamlFolderParser(parser)

        val objects = folderParser.parseFolderLazy(folderPath).iterator()
        assertStudentsSimple(objects)
    }

    @Test
    fun testFolderCojStudentAddressEager() {
        val folderPath = "resources/cojen/students_address"
        val parser = YamlParserCojen.yamlParser(StudentWithAddress::class, 4)
        val folderParser = YamlFolderParser(parser)

        val objects = folderParser.parseFolderEager(folderPath).iterator()
        assertStudentsAddress(objects)
    }
    @Test
    fun testFolderCojStudentAddressLazy() {
        val folderPath = "resources/cojen/students_address_sequence"
        val parser = YamlParserCojen.yamlParser(StudentWithAddress::class, 4)
        val folderParser = YamlFolderParser(parser)

        val objects = folderParser.parseFolderLazy(folderPath).iterator()
        assertStudentsAddress(objects)
    }

    @Test
    fun testFolderCojStudentAddressGradeEager() {
        val folderPath = "resources/cojen/students_addressgrade"
        val parser = YamlParserCojen.yamlParser(Student::class, 5)
        val folderParser = YamlFolderParser(parser)

        val objects = folderParser.parseFolderEager(folderPath).iterator()
        assertStudentsAddressGrade(objects)
    }

    @Test
    fun testFolderCojStudentAddressGradeLazy() {
        val folderPath = "resources/cojen/students_addressgrade_sequence"
        val parser = YamlParserCojen.yamlParser(Student::class, 5)
        val folderParser = YamlFolderParser(parser)

        val objects = folderParser.parseFolderLazy(folderPath).iterator()
        assertStudentsAddressGrade(objects)
    }

    @Test
    fun testFolderCojIntLazy() {
        val folderPath = "resources/cojen/int_sequence"
        val parser = YamlParserCojen.yamlParser(Int::class)
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
    fun testFolderCojStringLazy() {
        val folderPath = "resources/cojen/string_sequence"
        val parser = YamlParserCojen.yamlParser(String::class)
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
    fun testFolderCojStudentAnnotationEager() {
        val folderPath = "resources/cojen/new_students"
        val parser = YamlParserCojen.yamlParser(NewStudent::class)
        val folderParser = YamlFolderParser(parser)

        val objects = folderParser.parseFolderEager(folderPath).iterator()
        assertCojStudentsWithAnnotation(objects)
    }

    @Test
    fun testFolderCojStudentAnnotationLazy() {
        val folderPath = "resources/cojen/new_students_sequence"
        val parser = YamlParserCojen.yamlParser(NewStudent::class)
        val folderParser = YamlFolderParser(parser)

        val objects = folderParser.parseFolderLazy(folderPath).iterator()
        assertCojStudentsWithAnnotation(objects)
    }

    private fun assertStudents(seq: Iterator<Student>) {
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
        val grades4 = st4.grades.iterator()
        val g1 = grades4.next()
        kotlin.test.assertEquals("LAE", g1.subject)
        kotlin.test.assertEquals(18, g1.classification)
        val g2 = grades4.next()
        kotlin.test.assertEquals("PDM", g2.subject)
        kotlin.test.assertEquals(15, g2.classification)
        val g3 = grades4.next()
        kotlin.test.assertEquals("PC", g3.subject)
        kotlin.test.assertEquals(19, g3.classification)
        kotlin.test.assertFalse { grades4.hasNext() }
        val st5 = seq.next()
        kotlin.test.assertEquals("Paula Martins", st5.name)
        kotlin.test.assertEquals(952304, st5.nr)
        kotlin.test.assertEquals("Amadora", st5.from)
    }

    private fun assertStudentsSimple(seq: Iterator<StudentSimple>) {
        val st1 = seq.next()
        kotlin.test.assertEquals("Maria Candida", st1.name)
        kotlin.test.assertEquals(873435, st1.nr)
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
        kotlin.test.assertEquals("Alameda", st4.from)
        val st5 = seq.next()
        kotlin.test.assertEquals("Paula Martins", st5.name)
        kotlin.test.assertEquals(952304, st5.nr)
        kotlin.test.assertEquals("Amadora", st5.from)
    }

    private fun assertStudentsAddress(seq: Iterator<StudentWithAddress>) {
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
        kotlin.test.assertEquals("Rua Doutor Barroso", st2.address?.street)
        kotlin.test.assertEquals(13, st2.address?.nr)
        kotlin.test.assertEquals("Vila Real", st2.address?.city)
        kotlin.test.assertEquals("Tamega", st2.from)
        val st3 = seq.next()
        kotlin.test.assertEquals("Pedro Ferreira", st3.name)
        kotlin.test.assertEquals(238992, st3.nr)
        kotlin.test.assertEquals("Rua do Armiense", st3.address?.street)
        kotlin.test.assertEquals(16, st3.address?.nr)
        kotlin.test.assertEquals("Castelo Branco", st3.address?.city)
        kotlin.test.assertEquals("Covilhã", st3.from)
        val st4 = seq.next()
        kotlin.test.assertEquals("Carlos Serra", st4.name)
        kotlin.test.assertEquals(203941, st4.nr)
        kotlin.test.assertEquals("Rua Carlos Mardel", st4.address?.street)
        kotlin.test.assertEquals(23, st4.address?.nr)
        kotlin.test.assertEquals("Lisbon", st4.address?.city)
        kotlin.test.assertEquals("Alameda", st4.from)
        val st5 = seq.next()
        kotlin.test.assertEquals("Paula Martins", st5.name)
        kotlin.test.assertEquals(952304, st5.nr)
        kotlin.test.assertEquals("Rua das Galegas", st5.address?.street)
        kotlin.test.assertEquals(3, st5.address?.nr)
        kotlin.test.assertEquals("Lisbon", st5.address?.city)
        kotlin.test.assertEquals("Amadora", st5.from)
    }
    private fun assertStudentsAddressGrade(seq: Iterator<Student>) {
        val st1 = seq.next()
        kotlin.test.assertEquals("Maria Candida", st1.name)
        kotlin.test.assertEquals(873435, st1.nr)
        kotlin.test.assertEquals("Rua Rosa", st1.address?.street)
        kotlin.test.assertEquals(78, st1.address?.nr)
        kotlin.test.assertEquals("Lisbon", st1.address?.city)
        kotlin.test.assertEquals("Oleiros", st1.from)
        val grades1 = st1.grades.iterator()
        val g11 = grades1.next()
        kotlin.test.assertEquals("LAE", g11.subject)
        kotlin.test.assertEquals(18, g11.classification)
        val g12 = grades1.next()
        kotlin.test.assertEquals("PDM", g12.subject)
        kotlin.test.assertEquals(15, g12.classification)
        val g13 = grades1.next()
        kotlin.test.assertEquals("PC", g13.subject)
        kotlin.test.assertEquals(19, g13.classification)
        kotlin.test.assertFalse { grades1.hasNext() }
        val st2 = seq.next()
        kotlin.test.assertEquals("Jose Carioca", st2.name)
        kotlin.test.assertEquals(1214398, st2.nr)
        kotlin.test.assertEquals("Rua Doutor Barroso", st2.address?.street)
        kotlin.test.assertEquals(13, st2.address?.nr)
        kotlin.test.assertEquals("Vila Real", st2.address?.city)
        kotlin.test.assertEquals("Tamega", st2.from)
        val grades2 = st2.grades.iterator()
        val g21 = grades2.next()
        kotlin.test.assertEquals("AED", g21.subject)
        kotlin.test.assertEquals(13, g21.classification)
        val g22 = grades2.next()
        kotlin.test.assertEquals("PG", g22.subject)
        kotlin.test.assertEquals(14, g22.classification)
        val g23 = grades2.next()
        kotlin.test.assertEquals("ALGA", g23.subject)
        kotlin.test.assertEquals(17, g23.classification)
        kotlin.test.assertFalse { grades2.hasNext() }
        val st3 = seq.next()
        kotlin.test.assertEquals("Pedro Ferreira", st3.name)
        kotlin.test.assertEquals(238992, st3.nr)
        kotlin.test.assertEquals("Rua do Armiense", st3.address?.street)
        kotlin.test.assertEquals(16, st3.address?.nr)
        kotlin.test.assertEquals("Castelo Branco", st3.address?.city)
        kotlin.test.assertEquals("Covilhã", st3.from)
        val grades3 = st3.grades.iterator()
        val g31 = grades3.next()
        kotlin.test.assertEquals("CD", g31.subject)
        kotlin.test.assertEquals(10, g31.classification)
        val g32 = grades3.next()
        kotlin.test.assertEquals("TMD", g32.subject)
        kotlin.test.assertEquals(19, g32.classification)
        kotlin.test.assertFalse { grades3.hasNext() }
        val st4 = seq.next()
        kotlin.test.assertEquals("Carlos Serra", st4.name)
        kotlin.test.assertEquals(203941, st4.nr)
        kotlin.test.assertEquals("Rua Carlos Mardel", st4.address?.street)
        kotlin.test.assertEquals(23, st4.address?.nr)
        kotlin.test.assertEquals("Lisbon", st4.address?.city)
        kotlin.test.assertEquals("Alameda", st4.from)
        val grades4 = st4.grades.iterator()
        val g41 = grades4.next()
        kotlin.test.assertEquals("LS", g41.subject)
        kotlin.test.assertEquals(16, g41.classification)
        kotlin.test.assertFalse { grades4.hasNext() }
    }

    private fun assertStudentsWithAnnotation(seq: Iterator<NewStudent>) {
        val st1 = seq.next()
        kotlin.test.assertEquals("Maria Candida", st1.name)
        kotlin.test.assertEquals(873435, st1.nr)
        kotlin.test.assertEquals("Rua Rosa", st1.address?.street)
        kotlin.test.assertEquals(78, st1.address?.nr)
        kotlin.test.assertEquals("Lisbon", st1.address?.city)
        kotlin.test.assertEquals("Oleiros", st1.from)
        kotlin.test.assertEquals(26, st1.birth?.dayOfMonth)
        kotlin.test.assertEquals(5, st1.birth?.month?.value)
        kotlin.test.assertEquals(2004, st1.birth?.year)
        kotlin.test.assertEquals(16, st1.details?.age)
        kotlin.test.assertEquals(162, st1.details?.height)
        kotlin.test.assertEquals(false, st1.details?.asFinished)
        val grades1 = st1.grades.iterator()
        val g11 = grades1.next()
        kotlin.test.assertEquals("LAE", g11.subject)
        kotlin.test.assertEquals(18, g11.classification)
        val g12 = grades1.next()
        kotlin.test.assertEquals("PDM", g12.subject)
        kotlin.test.assertEquals(15, g12.classification)
        val g13 = grades1.next()
        kotlin.test.assertEquals("PC", g13.subject)
        kotlin.test.assertEquals(19, g13.classification)
        kotlin.test.assertFalse { grades1.hasNext() }

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
        val grades4 = st4.grades.iterator()
        val g41 = grades4.next()
        kotlin.test.assertEquals("LAE", g41.subject)
        kotlin.test.assertEquals(12, g41.classification)
        val g42 = grades4.next()
        kotlin.test.assertEquals("PDM", g42.subject)
        kotlin.test.assertEquals(15, g42.classification)
        val g43 = grades4.next()
        kotlin.test.assertEquals("PC", g43.subject)
        kotlin.test.assertEquals(16, g43.classification)
        kotlin.test.assertFalse { grades1.hasNext() }
    }

    private fun assertCojStudentsWithAnnotation(seq: Iterator<NewStudent>) {
        val st1 = seq.next()
        kotlin.test.assertEquals("Maria Candida", st1.name)
        kotlin.test.assertEquals(873435, st1.nr)
        kotlin.test.assertEquals("Rua Rosa", st1.address?.street)
        kotlin.test.assertEquals(78, st1.address?.nr)
        kotlin.test.assertEquals("Lisbon", st1.address?.city)
        kotlin.test.assertEquals("Oleiros", st1.from)
        val grades1 = st1.grades.iterator()
        val g11 = grades1.next()
        kotlin.test.assertEquals("LAE", g11.subject)
        kotlin.test.assertEquals(18, g11.classification)
        val g12 = grades1.next()
        kotlin.test.assertEquals("PDM", g12.subject)
        kotlin.test.assertEquals(15, g12.classification)
        val g13 = grades1.next()
        kotlin.test.assertEquals("PC", g13.subject)
        kotlin.test.assertEquals(19, g13.classification)
        kotlin.test.assertFalse { grades1.hasNext() }
        kotlin.test.assertEquals(26, st1.birth?.dayOfMonth)
        kotlin.test.assertEquals(5, st1.birth?.month?.value)
        kotlin.test.assertEquals(2004, st1.birth?.year)
        kotlin.test.assertEquals(16, st1.details?.age)
        kotlin.test.assertEquals(162, st1.details?.height)
        kotlin.test.assertEquals(false, st1.details?.asFinished)

        val st2 = seq.next()
        kotlin.test.assertEquals("Jose Carioca", st2.name)
        kotlin.test.assertEquals(1214398, st2.nr)
        kotlin.test.assertEquals("Rua Doutor Barroso", st2.address?.street)
        kotlin.test.assertEquals(13, st2.address?.nr)
        kotlin.test.assertEquals("Vila Real", st2.address?.city)
        kotlin.test.assertEquals("Tamega", st2.from)
        val grades2 = st2.grades.iterator()
        val g21 = grades2.next()
        kotlin.test.assertEquals("AED", g21.subject)
        kotlin.test.assertEquals(13, g21.classification)
        val g22 = grades2.next()
        kotlin.test.assertEquals("PG", g22.subject)
        kotlin.test.assertEquals(14, g22.classification)
        val g23 = grades2.next()
        kotlin.test.assertEquals("ALGA", g23.subject)
        kotlin.test.assertEquals(17, g23.classification)
        kotlin.test.assertFalse { grades2.hasNext() }
        val st3 = seq.next()
        kotlin.test.assertEquals("Pedro Ferreira", st3.name)
        kotlin.test.assertEquals(238992, st3.nr)
        kotlin.test.assertEquals("Rua do Armiense", st3.address?.street)
        kotlin.test.assertEquals(16, st3.address?.nr)
        kotlin.test.assertEquals("Castelo Branco", st3.address?.city)
        kotlin.test.assertEquals("Covilhã", st3.from)
        val grades3 = st3.grades.iterator()
        val g31 = grades3.next()
        kotlin.test.assertEquals("CD", g31.subject)
        kotlin.test.assertEquals(10, g31.classification)
        val g32 = grades3.next()
        kotlin.test.assertEquals("TMD", g32.subject)
        kotlin.test.assertEquals(19, g32.classification)
        kotlin.test.assertFalse { grades3.hasNext() }
        val st4 = seq.next()
        kotlin.test.assertEquals("Carlos Serra", st4.name)
        kotlin.test.assertEquals(203941, st4.nr)
        kotlin.test.assertEquals("Rua Carlos Mardel", st4.address?.street)
        kotlin.test.assertEquals(23, st4.address?.nr)
        kotlin.test.assertEquals("Lisbon", st4.address?.city)
        kotlin.test.assertEquals("Alameda", st4.from)
        val grades4 = st4.grades.iterator()
        val g41 = grades4.next()
        kotlin.test.assertEquals("LS", g41.subject)
        kotlin.test.assertEquals(16, g41.classification)
        kotlin.test.assertFalse { grades4.hasNext() }
    }
}