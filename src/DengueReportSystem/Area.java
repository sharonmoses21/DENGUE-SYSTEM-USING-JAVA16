package DengueReportSystem;

import java.util.LinkedHashMap;

public class Area {
    private String name;
    private LinkedHashMap<Integer,Integer> casesMap;

    public Area(String name, LinkedHashMap<Integer, Integer> casesList) {
        this.name = name;
        this.casesMap = casesList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LinkedHashMap<Integer, Integer> getCasesList() {
        return casesMap;
    }

    public void setCasesList(LinkedHashMap<Integer, Integer> casesList) {
        this.casesMap = casesList;
    }

    @Override
    public String toString() {
        return "Area{" + "name=" + name + ", casesList=" + casesMap + '}';
    }
}
