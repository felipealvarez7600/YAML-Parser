# JMH Results

## Benchmarking Environment


| Benchmark                                  | Mode  | Cnt | Score   | Error    | Units  |
|--------------------------------------------|-------|-----|---------|----------|--------|
| YamlParserAccountBenchmark.accountBaseline | thrpt | 4   | 403.896 | ± 21.142 | ops/ms |
| YamlParserAccountBenchmark.accountDynamic  | thrpt | 4   | 406.553 | ± 29.146 | ops/ms |
| YamlParserAccountBenchmark.accountReflect  | thrpt | 4   | 355.827 | ± 10.245 | ops/ms |
| YamlParserStudentBenchmark.studentBaseline | thrpt | 4   | 251.747 | ± 15.561 | ops/ms |
| YamlParserStudentBenchmark.studentDynamic  | thrpt | 4   | 272.200 | ± 41.568 | ops/ms |
| YamlParserStudentBenchmark.studentReflect  | thrpt | 4   | 217.954 | ± 21.224 | ops/ms |