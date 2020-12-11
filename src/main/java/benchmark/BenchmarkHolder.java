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

    @Param({"2", "4", "6", "8", "10", "15", "20", "100", "1000", "10000"})
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
            if (Boolean.TRUE.equals(record.isMale())) {
                ++checkPairMap.get(record.getClassifier()).sumOfMale;
            }
            if (Boolean.TRUE.equals(record.isFemale())) {
                ++checkPairMap.get(record.getClassifier()).sumOfFemale;
            }
        }
        return result;
    }

    @Benchmark
    public List<String> for_loop_with_two_map() {
        Map<String, Integer> maleMap = new HashMap<>();
        Map<String, Integer> femaleMap = new HashMap<>();
        List<String> result = new ArrayList<>();
        for (Record record : TESTING_DATA_SET) {
            if (!maleMap.containsKey(record.getClassifier()) && !femaleMap.containsKey(record.getClassifier())) {
                maleMap.put(record.getClassifier(), 0);
                femaleMap.put(record.getClassifier(), 0);
            }
            if (result.contains(record.getClassifier())) {
                continue;
            }
            if (maleMap.get(record.getClassifier()) > 1 || femaleMap.get(record.getClassifier()) > 1) {
                result.add(record.getClassifier());
                continue;
            }
            if (Boolean.TRUE.equals(record.isMale())) {
                int male = maleMap.get(record.getClassifier());
                maleMap.put(record.getClassifier(), ++male);
            }
            if (Boolean.TRUE.equals(record.isFemale())) {
                int female = femaleMap.get(record.getClassifier());
                femaleMap.put(record.getClassifier(), ++female);
            }
        }
        return result;
    }

    @Benchmark
    public List<String> pure_for_loop() {
        List<String> tempMale = new ArrayList<>();
        List<String > tempFemale = new ArrayList<>();
        List<String> result = new ArrayList<>();
        for (Record record : TESTING_DATA_SET) {
            if (result.contains(record.getClassifier())) {
                continue;
            }
            if (Collections.frequency(tempMale, record.getClassifier()) > 1
                    || Collections.frequency(tempFemale, record.getClassifier()) > 1) {
                result.add(record.getClassifier());
                tempFemale.remove(record.getClassifier());
                tempMale.remove(record.getClassifier());
                continue;
            }
            if (Boolean.TRUE.equals(record.isMale())) {
                tempMale.add(record.getClassifier());
            }
            if (Boolean.TRUE.equals(record.isFemale())) {
                tempFemale.add(record.getClassifier());
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
