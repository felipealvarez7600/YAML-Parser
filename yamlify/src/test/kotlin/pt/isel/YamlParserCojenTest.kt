package pt.isel

import pt.isel.test.*
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class YamlParserCojenTest {

    @Test
    fun parseStudent() {
        val yaml = """
                name: Maria Candida
                nr: 873435
                from: Oleiros"""
        val st = YamlParserCojen.yamlParser(Student::class, 3).parseObject(yaml.reader())
        assertEquals("Maria Candida", st.name)
        assertEquals(873435, st.nr)
        assertEquals("Oleiros", st.from)
    }
    @Test
    fun parseStudentWithAddress() {
        val yaml = """
                name: Maria Candida
                nr: 873435
                address:
                  street: Rua Rosa
                  nr: 78
                  city: Lisbon
                from: Oleiros"""
        val st = YamlParserCojen.yamlParser(StudentWithAddress::class, 4).parseObject(yaml.reader())
        assertEquals("Maria Candida", st.name)
        assertEquals(873435, st.nr)
        assertEquals("Oleiros", st.from)
        assertEquals("Rua Rosa", st.address?.street)
        assertEquals(78, st.address?.nr)
        assertEquals("Lisbon", st.address?.city)
    }

    @Test
    fun parseSequenceOfStrings() {
        val yaml = """
            - Ola
            - Maria Carmen
            - Lisboa Capital
        """
        val seq = YamlParserCojen.yamlParser(String::class)
            .parseList(yaml.reader())
            .iterator()
        assertEquals("Ola", seq.next())
        assertEquals("Maria Carmen", seq.next())
        assertEquals("Lisboa Capital", seq.next())
        assertFalse { seq.hasNext() }
    }

    @Test
    fun parseSequenceOfInts() {
        val yaml = """
            - 1
            - 2
            - 3
        """
        val seq = YamlParserCojen.yamlParser(Int::class)
            .parseList(yaml.reader())
            .iterator()
        assertEquals(1, seq.next())
        assertEquals(2, seq.next())
        assertEquals(3, seq.next())
        assertFalse { seq.hasNext() }
    }
    @Test
    fun parseSequenceOfStudents(){
        val yaml = """
            -
              name: Maria Candida
              nr: 873435
              from: Oleiros
            - 
              name: Jose Carioca
              nr: 1214398
              from: Tamega
        """
        val seq = YamlParserCojen.yamlParser(Student::class, 3)
            .parseList(yaml.reader())
            .iterator()
        val st1 = seq.next()
        assertEquals("Maria Candida", st1.name)
        assertEquals(873435, st1.nr)
        assertEquals("Oleiros", st1.from)
        val st2 = seq.next()
        assertEquals("Jose Carioca", st2.name)
        assertEquals(1214398, st2.nr)
        assertEquals("Tamega", st2.from)
        assertFalse { seq.hasNext() }
    }
    @Test
    fun parseSequenceOfStudentsWithAddresses() {
        val yaml = """
            -
              name: Maria Candida
              nr: 873435
              address:
                street: Rua Rosa
                nr: 78
                city: Lisbon
              from: Oleiros
            - 
              name: Jose Carioca
              nr: 1214398
              address:
                street: Rua Azul
                nr: 12
                city: Porto
              from: Tamega
        """
        val seq = YamlParserCojen.yamlParser(Student::class, 4)
            .parseList(yaml.reader())
            .iterator()
        val st1 = seq.next()
        assertEquals("Maria Candida", st1.name)
        assertEquals(873435, st1.nr)
        assertEquals("Oleiros", st1.from)
        assertEquals("Rua Rosa", st1.address?.street)
        assertEquals(78, st1.address?.nr)
        assertEquals("Lisbon", st1.address?.city)
        val st2 = seq.next()
        assertEquals("Jose Carioca", st2.name)
        assertEquals(1214398, st2.nr)
        assertEquals("Tamega", st2.from)
        assertEquals("Rua Azul", st2.address?.street)
        assertEquals(12, st2.address?.nr)
        assertEquals("Porto", st2.address?.city)
        assertFalse { seq.hasNext() }
    }
    @Test
    fun parseSequenceOfStudentsWithAddressesAndGrades() {
        val seq = YamlParserCojen.yamlParser(Student::class, 5)
            .parseList(yamlSequenceOfStudents.reader())
            .iterator()
        assertStudentsInSequence(seq)
    }
    @Test
    fun parseClassroom() {
        val yaml = """
          id: i45
          students: $yamlSequenceOfStudents
        """.trimIndent()
        val cr = YamlParserCojen.yamlParser(Classroom::class)
            .parseObject(yaml.reader())
        assertEquals("i45", cr.id)
        assertStudentsInSequence(cr.students.iterator())
    }
    private fun assertStudentsInSequence(seq: Iterator<Student>) {
        val st1 = seq.next()
        assertEquals("Maria Candida", st1.name)
        assertEquals(873435, st1.nr)
        assertEquals("Oleiros", st1.from)
        assertEquals("Rua Rosa", st1.address?.street)
        assertEquals(78, st1.address?.nr)
        assertEquals("Lisbon", st1.address?.city)
        val grades1 = st1.grades.iterator()
        val g1 = grades1.next()
        assertEquals("LAE", g1.subject)
        assertEquals(18, g1.classification)
        val g2 = grades1.next()
        assertEquals("PDM", g2.subject)
        assertEquals(15, g2.classification)
        val g3 = grades1.next()
        assertEquals("PC", g3.subject)
        assertEquals(19, g3.classification)
        assertFalse { grades1.hasNext() }
        val st2 = seq.next()
        assertEquals("Jose Carioca", st2.name)
        assertEquals(1214398, st2.nr)
        assertEquals("Tamega", st2.from)
        assertEquals("Rua Azul", st2.address?.street)
        assertEquals(12, st2.address?.nr)
        assertEquals("Porto", st2.address?.city)
        val grades2 = st2.grades.iterator()
        val g4 = grades2.next()
        assertEquals("TDS", g4.subject)
        assertEquals(20, g4.classification)
        val g5 = grades2.next()
        assertEquals("LAE", g5.subject)
        assertEquals(18, g5.classification)
        assertFalse { grades2.hasNext() }
        assertFalse { seq.hasNext() }
    }

    @Test
    fun parseStudentWithAnnotation(){
        val yaml = """
                name: Maria Candida
                nr: 873435
                address:
                  street: Rua Rosa
                  nr: 78
                  city: Lisbon
                city of birth: Oleiros"""
        val st = YamlParserCojen.yamlParser(StudentYaml::class, 4).parseObject(yaml.reader())
        assertEquals("Maria Candida", st.name)
        assertEquals(873435, st.nr)
        assertEquals("Oleiros", st.from)
        assertEquals("Rua Rosa", st.address?.street)
        assertEquals(78, st.address?.nr)
        assertEquals("Lisbon", st.address?.city)
    }

    @Test
    fun parseAnnotationConvert() {
        val yaml = """
                name: Maria Candida
                nr: 873435
                city of birth: Oleiros
                address:
                  street: Rua Rosa
                  nr: 78
                  city: Lisbon
                grades:
                  - 
                    subject: LAE
                    classification: 18
                  -
                    subject: PDM
                    classification: 15
                  -
                    subject: PC
                    classification: 19
                birth:
                  year: 2004
                  month: 05
                  day: 26
                details:
                  age: 16
                  height: 162
                  asFinished: false
            """
        val st = YamlParserCojen.yamlParser(NewStudent::class, 7).parseObject(yaml.reader())
        assertEquals("Maria Candida", st.name)
        assertEquals(873435, st.nr)
        assertEquals("Oleiros", st.from)
        assertEquals(26, st.birth?.dayOfMonth)
        assertEquals(5, st.birth?.month?.value)
        assertEquals(2004, st.birth?.year)
        assertEquals(16, st.details?.age)
        assertEquals(162, st.details?.height)
        assertEquals(null, st.details?.year)
        assertEquals(false, st.details?.asFinished)

    }

    @Test
    fun parseSequenceOfStudents1() {
        val yaml = """
            - 
              name: Maria Candida
              nr: 873435
              from: Oleiros
            - 
              name: Jose Carioca
              nr: 1214398
              from: Tamega
        """
        val seq =
            YamlParserCojen.yamlParser(Student::class, 3)
                .parseSequence(yaml.reader())
                .iterator()
        val st1 = seq.next()
        assertEquals("Maria Candida", st1.name)
        assertEquals(873435, st1.nr)
        assertEquals("Oleiros", st1.from)
        val st2 = seq.next()
        assertEquals("Jose Carioca", st2.name)
        assertEquals(1214398, st2.nr)
        assertEquals("Tamega", st2.from)
    }

    @Test
    fun parseSequenceOfStudentsWithConvertCount() {
        val yaml = yamlSequenceOfStudentsWithThings
        val seq = YamlParserCojen.yamlParser(NewStudent::class, 7)
            .parseSequence(yaml.reader())
            .iterator()

        assertEquals(0, YamlToDetails.count)
        assertEquals(0, YamlToDate.count)

        val st1 = seq.next()
        assertEquals("Maria Candida", st1.name)
        assertEquals(873435, st1.nr)
        assertEquals("Oleiros", st1.from)
        assertEquals(26, st1.birth?.dayOfMonth)
        assertEquals(5, st1.birth?.month?.value)
        assertEquals(2004, st1.birth?.year)
        assertEquals(16, st1.details?.age)
        assertEquals(162, st1.details?.height)
        assertEquals(false, st1.details?.asFinished)

        assertEquals(1, YamlToDetails.count)
        assertEquals(1, YamlToDate.count)

        val st2 = seq.next()
        assertEquals("Antonio Candida", st2.name)
        assertEquals(456758, st2.nr)
        assertEquals("Santo Amaro", st2.from)
        assertEquals(23, st2.birth?.dayOfMonth)
        assertEquals(10, st2.birth?.month?.value)
        assertEquals(2007, st2.birth?.year)
        assertEquals(56, st2.details?.age)
        assertEquals(135, st2.details?.height)
        assertEquals(true, st2.details?.asFinished)

        assertEquals(2, YamlToDetails.count)
        assertEquals(2, YamlToDate.count)

    }

    private val yamlSequenceOfStudentsWithThings = """
            - 
              name: Maria Candida
              nr: 873435
              city of birth: Oleiros
              address:
                street: Rua Rosa
                nr: 78
                city: Lisbon
              grades:
                - 
                  subject: LAE
                  classification: 18
                -
                  subject: PDM
                  classification: 15
                -
                  subject: PC
                  classification: 19
              birth:
                year: 2004
                month: 05
                day: 26
              details:
                age: 16
                height: 162
                asFinished: false
            - 
              name: Antonio Candida
              nr: 456758
              city of birth: Santo Amaro
              address:
                street: Rua Rosa
                nr: 78
                city: Lisboa
              grades:
                - 
                  subject: LAE
                  classification: 18
                -
                  subject: PDM
                  classification: 15
                -
                  subject: PC
                  classification: 19
              birth:
                year: 2007
                month: 10
                day: 23
              details:
                age: 56
                height: 135
                asFinished: true
        """.trimIndent()
}