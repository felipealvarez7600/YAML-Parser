


package pt.isel

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.assertThrows
import pt.isel.test.Classroom
import pt.isel.test.NewClassroom
import pt.isel.test.Student
import pt.isel.test.NewStudent
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse


class YamlParserReflectTest {

    @BeforeEach
    fun resetCounts() {
        YamlToDetails.count = 0
        YamlToDate.count = 0
    }

    @Test fun parseStudentWithMissingProperties() {
        val yaml = """
                name: Maria Candida
                from: Oleiros"""
        assertThrows<IllegalArgumentException> {
            YamlParserReflect.yamlParser(Student::class).parseObject(yaml.reader())
        }
    }
    @Test fun parseStudent() {
        val yaml = """
                name: Maria Candida
                nr: 873435
                from: Oleiros"""
        val st = YamlParserReflect.yamlParser(Student::class).parseObject(yaml.reader())
        assertEquals("Maria Candida", st.name)
        assertEquals(873435, st.nr)
        assertEquals("Oleiros", st.from)
    }
    @Test fun parseStudentWithAddress() {
        val yaml = """
                name: Maria Candida
                nr: 873435
                address:
                  street: Rua Rosa
                  nr: 78
                  city: Lisbon
                from: Oleiros"""
        val st = YamlParserReflect.yamlParser(Student::class).parseObject(yaml.reader())
        assertEquals("Maria Candida", st.name)
        assertEquals(873435, st.nr)
        assertEquals("Oleiros", st.from)
        assertEquals("Rua Rosa", st.address?.street)
        assertEquals(78, st.address?.nr)
        assertEquals("Lisbon", st.address?.city)
    }

    @Test
    fun parseSequenceOfStrings() {
        val seq = YamlParserReflect.yamlParser(String::class)
            .parseList(yamlWithImplicitValuesOfStrings.reader())
            .iterator()
        assertEquals("Ola", seq.next())
        assertEquals("Maria Carmen", seq.next())
        assertEquals("Lisboa Capital", seq.next())
        assertFalse { seq.hasNext() }
    }

    @Test
    fun parseSequenceOfStringsLazy() {
        val seq = YamlParserReflect.yamlParser(String::class)
            .parseSequence(yamlWithImplicitValuesOfStrings.reader())
            .iterator()
        assertEquals("Ola", seq.next())
        assertEquals("Maria Carmen", seq.next())
        assertEquals("Lisboa Capital", seq.next())
        assertFalse { seq.hasNext() }
    }

    @Test
    fun parseSequenceOfInts() {
        val seq = YamlParserReflect.yamlParser(Int::class)
            .parseList(yamlWithImplicitValuesOfInts.reader())
            .iterator()
        assertEquals(1, seq.next())
        assertEquals(2, seq.next())
        assertEquals(3, seq.next())
        assertFalse { seq.hasNext() }
    }

    @Test
    fun parseSequenceOfIntsLazy() {
        val seq = YamlParserReflect.yamlParser(Int::class)
            .parseSequence(yamlWithImplicitValuesOfInts.reader())
            .iterator()
        assertEquals(1, seq.next())
        assertEquals(2, seq.next())
        assertEquals(3, seq.next())
        assertFalse { seq.hasNext() }
    }

    @Test
    fun parseSequenceOfStudents(){
        val seq = YamlParserReflect.yamlParser(Student::class)
            .parseList(yamlSequenceOfStudentsWithDefaultParameters.reader())
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
    fun parseSequenceOfStudentsLazy(){
        val seq = YamlParserReflect.yamlParser(Student::class)
            .parseSequence(yamlSequenceOfStudentsWithDefaultParameters.reader())
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
        val seq = YamlParserReflect.yamlParser(Student::class)
            .parseList(yamlSequenceOfStudentsWithAddress.reader())
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
    fun parseSequenceOfStudentsWithAddressesLazy() {
        val seq = YamlParserReflect.yamlParser(Student::class)
            .parseSequence(yamlSequenceOfStudentsWithAddress.reader())
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
        val seq = YamlParserReflect.yamlParser(Student::class)
            .parseList(yamlSequenceOfStudents.reader())
            .iterator()
        assertStudentsInSequence(seq)
    }

    @Test
    fun parseSequenceOfStudentsWithAddressesAndGradesLazy() {
        val seq = YamlParserReflect.yamlParser(Student::class)
            .parseSequence(yamlSequenceOfStudents.reader())
            .iterator()
        assertStudentsInSequence(seq)
    }
    @Test fun parseClassroom() {
        val yaml = """
          id: i45
          students: $yamlSequenceOfStudents
        """.trimIndent()
        val cr = YamlParserReflect.yamlParser(Classroom::class)
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

    private fun assertStudents2InSequence(seq: Iterator<NewStudent>) {
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

    @Test fun parseSequenceOfStudentsWithAddressesAndGradesWithAnnotation() {
        val seq = YamlParserReflect.yamlParser(NewStudent::class)
            .parseList(yamlSequenceOfStudents2WithAnnotation.reader())
            .iterator()
        assertStudents2InSequence(seq)
    }

    @Test fun parseSequenceOfStudentsWithAddressesAndGradesWithAnnotationLazy() {
        val seq = YamlParserReflect.yamlParser(NewStudent::class)
            .parseSequence(yamlSequenceOfStudents2WithAnnotation.reader())
            .iterator()
        assertStudents2InSequence(seq)
    }

    @Test fun parseClassroomWithAnnotation() {
        val yaml = """
          id: i45
          newStudents: $yamlSequenceOfStudents2WithAnnotation
        """.trimIndent()
        val cr = YamlParserReflect.yamlParser(NewClassroom::class)
            .parseObject(yaml.reader())
        assertEquals("i45", cr.id)
        assertStudents2InSequence(cr.newStudents.iterator())
    }

    @Test fun parseStudentWithAnnotation() {
        val yaml = """
                name: Maria Candida
                nr: 873435
                city of birth: Oleiros"""
        val st = YamlParserReflect.yamlParser(NewStudent::class).parseObject(yaml.reader())
        assertEquals("Maria Candida", st.name)
        assertEquals(873435, st.nr)
        assertEquals("Oleiros", st.from)
    }
    @Test fun parseStudentWithAddressWithAnnotation() {
        val yaml = """
                name: Maria Candida
                nr: 873435
                address:
                  street: Rua Rosa
                  nr: 78
                  city: Lisbon
                city of birth: Oleiros"""
        val st = YamlParserReflect.yamlParser(NewStudent::class).parseObject(yaml.reader())
        assertEquals("Maria Candida", st.name)
        assertEquals(873435, st.nr)
        assertEquals("Oleiros", st.from)
        assertEquals("Rua Rosa", st.address?.street)
        assertEquals(78, st.address?.nr)
        assertEquals("Lisbon", st.address?.city)
    }

    @Test fun parseSequenceOfStudentsWithAnnotation() {
        val seq = YamlParserReflect.yamlParser(NewStudent::class)
            .parseList(yamlSequenceOfStudentsWithAnnotationYamlArg.reader())
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

    @Test fun parseSequenceOfStudentsWithAnnotationLazy() {
        val seq = YamlParserReflect.yamlParser(NewStudent::class)
            .parseSequence(yamlSequenceOfStudentsWithAnnotationYamlArg.reader())
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

    @Test fun parseSequenceOfStudentsWithAddressesWithAnnotation() {
        val seq = YamlParserReflect.yamlParser(NewStudent::class)
            .parseList(yamlSequenceOfStudentsWithAddressAndWithAnnotationYamlArg.reader())
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
    fun parseBirthWithAnnotation() {
        val yaml = """
                name: Maria Candida
                nr: 873435
                city of birth: Oleiros
                birth:
                    year: 2004
                    month: 05
                    day: 26
            """
        val st = YamlParserReflect.yamlParser(NewStudent::class).parseObject(yaml.reader())
        assertEquals("Maria Candida", st.name)
        assertEquals(873435, st.nr)
        assertEquals("Oleiros", st.from)
        assertEquals(26, st.birth?.dayOfMonth)
        assertEquals(5, st.birth?.month?.value)
        assertEquals(2004, st.birth?.year)
    }

    @Test
    fun parseSubjectsWithAnnotation() {
        val yaml = """
                name: Maria Candida
                nr: 873435
                city of birth: Oleiros
                details:
                    age: 16
                    height: 162
                    asFinished: false
            """
        val st = YamlParserReflect.yamlParser(NewStudent::class).parseObject(yaml.reader())
        assertEquals("Maria Candida", st.name)
        assertEquals(873435, st.nr)
        assertEquals("Oleiros", st.from)
        assertEquals(16, st.details?.age)
        assertEquals(162, st.details?.height)
        assertEquals(null, st.details?.year)
        assertEquals(false, st.details?.asFinished)
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
        val st = YamlParserReflect.yamlParser(NewStudent::class).parseObject(yaml.reader())
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

//    @Test
//    fun parseURLWithAnnotation() {
//        val yaml = """
//                name: Maria Candida
//                nr: 873435
//                city of birth: Oleiros
//                url: http://marican.com:1698/loja/couves
//            """
//        val st = YamlParserReflect.yamlParser(NewStudent::class).parseObject(yaml.reader())
//        assertEquals("Maria Candida", st.name)
//        assertEquals(873435, st.nr)
//        assertEquals("Oleiros", st.from)
//        assertEquals("http", st.url?.protocol)
//        assertEquals("marican.com", st.url?.host)
//        assertEquals(1698, st.url?.port)
//        assertEquals("/loja/couves", st.url?.path)
//        assertEquals(null, st.url?.query)
//    }

    @Test
    fun parseSequenceOfStudentsWithConvertCountEager() {
        val seq = YamlParserReflect.yamlParser(NewStudent::class)
            .parseList(yamlSequenceOfStudentsWithYamlConvertAnnotation.reader())
            .iterator()

        assertEquals(4, YamlToDetails.count)
        assertEquals(4, YamlToDate.count)

        assertStudentsInSequenceFirstIteration(seq)

        assertStudentsInSequenceSecondIteration(seq)
    }

    @Test
    fun parseSequenceOfStudentsWithConvertCountLazy() {
        val yaml = yamlSequenceOfStudentsWithYamlConvertAnnotation
        val seq = YamlParserReflect.yamlParser(NewStudent::class)
            .parseSequence(yaml.reader())
            .iterator()

        assertEquals(0, YamlToDetails.count)
        assertEquals(0, YamlToDate.count)

        assertStudentsInSequenceFirstIteration(seq)

        assertEquals(1, YamlToDetails.count)
        assertEquals(1, YamlToDate.count)

        assertStudentsInSequenceSecondIteration(seq)

        assertEquals(2, YamlToDetails.count)
        assertEquals(2, YamlToDate.count)

    }

    private fun assertStudentsInSequenceFirstIteration(seq: Iterator<NewStudent>) {
        val st = seq.next()
        assertEquals("Maria Candida", st.name)
        assertEquals(873435, st.nr)
        assertEquals("Oleiros", st.from)
        assertEquals(26, st.birth?.dayOfMonth)
        assertEquals(5, st.birth?.month?.value)
        assertEquals(2004, st.birth?.year)
        assertEquals(16, st.details?.age)
        assertEquals(162, st.details?.height)
        assertEquals(false, st.details?.asFinished)
    }

    private fun assertStudentsInSequenceSecondIteration(seq: Iterator<NewStudent>) {
        val st = seq.next()
        assertEquals("Antonio Candida", st.name)
        assertEquals(456758, st.nr)
        assertEquals("Santo Amaro", st.from)
        assertEquals(23, st.birth?.dayOfMonth)
        assertEquals(10, st.birth?.month?.value)
        assertEquals(2007, st.birth?.year)
        assertEquals(56, st.details?.age)
        assertEquals(135, st.details?.height)
        assertEquals(true, st.details?.asFinished)
    }
}

const val yamlSequenceOfStudents = """
            -
              name: Maria Candida
              nr: 873435
              address:
                street: Rua Rosa
                nr: 78
                city: Lisbon
              from: Oleiros
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
            - 
              name: Jose Carioca
              nr: 1214398
              address:
                street: Rua Azul
                nr: 12
                city: Porto
              from: Tamega
              grades:
                -
                  subject: TDS
                  classification: 20
                - 
                  subject: LAE
                  classification: 18
        """

const val yamlSequenceOfStudents2WithAnnotation = """
            -
              name: Maria Candida
              nr: 873435
              address:
                street: Rua Rosa
                nr: 78
                city: Lisbon
              city of birth: Oleiros
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
            - 
              name: Jose Carioca
              nr: 1214398
              address:
                street: Rua Azul
                nr: 12
                city: Porto
              city of birth: Tamega
              grades:
                -
                  subject: TDS
                  classification: 20
                - 
                  subject: LAE
                  classification: 18
        """

const val yamlSequenceOfStudentsWithAnnotationYamlArg = """
            -
              name: Maria Candida
              nr: 873435
              city of birth: Oleiros
            - 
              name: Jose Carioca
              nr: 1214398
              city of birth: Tamega
        """

const val yamlSequenceOfStudentsWithAddressAndWithAnnotationYamlArg = """
            -
              name: Maria Candida
              nr: 873435
              address:
                street: Rua Rosa
                nr: 78
                city: Lisbon
              city of birth: Oleiros
            - 
              name: Jose Carioca
              nr: 1214398
              address:
                street: Rua Azul
                nr: 12
                city: Porto
              city of birth: Tamega
        """