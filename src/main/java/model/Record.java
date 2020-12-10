package model;

public class Record {
    Boolean male, female;
    String classifier, unitName;

    public Record(String classifier, String unitName) {
        this.classifier = classifier;
        this.unitName = unitName;
    }

    public Boolean isMale() {
        return male != null ? male : false;
    }

    public Boolean isFemale() {
        return female != null ? female : false;
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
