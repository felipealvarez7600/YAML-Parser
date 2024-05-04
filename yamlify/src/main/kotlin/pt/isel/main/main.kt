package pt.isel.main

import pt.isel.YamlParserReflect
import pt.isel.cojen.types.Student2

fun main() {
    val yamlSequenceOfStudents = """
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
    val seq = YamlParserReflect.yamlParser(Student2::class)
//        .parseList(yamlSequenceOfStudents.reader())
//        .iterator()
    repeat(10000) {
        seq.parseList(yamlSequenceOfStudents.reader())
        //assertStudentsInSequence(seq)
    }
//    val seq = YamlParserReflect.yamlParser(Student2::class)
//        .parseList(yamlSequenceOfStudents.reader())
//        .iterator()
//    assertStudentsInSequence(seq)
}