package benchmark;

import model.Pair;
import model.Record;
import org.apache.commons.lang3.RandomStringUtils;
import org.openjdk.jmh.annotations.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@State(Scope.Benchmark)
@Fork(value = 1, warmups = 2)
@Warmup(iterations = 10, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, timeUnit = TimeUnit.MILLISECONDS)
@BenchmarkMode(Mode.Throughput)
public class BenchmarkHolder {

    @Param({"2", "4", "6", "8", "10", "15", "20"})
    public int numberOfRecordClassifiers;

    public List<Record> TESTING_DATA_SET;

    @Setup
    public void setup() {
        TESTING_DATA_SET = createData();
    }

    @Benchmark
    public List<String> pure_stream_api() {
        Map<String, List<Record>> filtered = TESTING_DATA_SET.stream()
                .filter(record -> record.isMale() || record.isFemale())
                .collect(Collectors.groupingBy(Record::getClassifier));
        return filtered.entrySet().stream()
                .filter(entry -> entry.getValue().stream().filter(Record::isMale).count() > 1
                        || entry.getValue().stream().filter(Record::isFemale).count() > 1)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Benchmark
    public List<String> for_loop_with_map_pair() {
        Map<String, Pair> checkPairMap = new HashMap<>();
        List<String> result = new ArrayList<>();
        for (Record record : TESTING_DATA_SET) {
            if (!checkPairMap.containsKey(record.getClassifier())) {
                checkPairMap.put(record.getClassifier(), new Pair());
            }
            if (checkPairMap.get(record.getClassifier()).sumOfMale > 1
                    || checkPairMap.get(record.getClassifier()).sumOfFemale > 1) {
                result.add(record.getClassifier());
                continue;
            }
            if (record.isMale()) {
                ++checkPairMap.get(record.getClassifier()).sumOfMale;
            }
            if (record.isFemale()) {
                ++checkPairMap.get(record.getClassifier()).sumOfFemale;
            }
        }
        return result;
    }

    private List<Record> createData() {
        Random random = new Random();
        String[] classifiers = new String[numberOfRecordClassifiers];
        List<Record> data = new ArrayList<>();

        for (int i = 0; i < classifiers.length; i++) {
            classifiers[i] = RandomStringUtils.random(9);
        }

        for (int i = 0; i < numberOfRecordClassifiers * 200; i++) {
            boolean bothNull = random.nextBoolean();
            boolean nullValue = random.nextBoolean();
            Record record = new Record(
                    classifiers[random.nextInt(classifiers.length)],
                    RandomStringUtils.random(30));
            if (bothNull) {
                record.setMale(null);
                record.setFemale(null);
            } else {
                if (nullValue) {
                    record.setMale(null);
                    record.setFemale(true);
                } else {
                    record.setMale(true);
                    record.setFemale(null);
                }
            }
            data.add(record);
        }
        return data;
    }
}
