package model;

public class Record {
    Boolean male;
    Boolean female;
    String classifier;
    String unitName;

    public Record(String classifier, String unitName) {
        this.classifier = classifier;
        this.unitName = unitName;
    }

    public boolean isMale() {
        return male != null && male;
    }

    public boolean isFemale() {
        return female != null && female;
    }

    public String getClassifier() {
        return classifier;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setMale(Boolean male) {
        this.male = male;
    }

    public void setFemale(Boolean female) {
        this.female = female;
    }
}
