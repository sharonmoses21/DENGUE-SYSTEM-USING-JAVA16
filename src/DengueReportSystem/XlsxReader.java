package DengueReportSystem;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class XlsxReader{
    //Helpers for reading xlsx
    private FileInputStream fis;
    private XSSFWorkbook wb;
    //Helpers for cloning the list in combineList()
    private ListCloner listCloner;
    //Target file
    private String targetFilePath;
    
    //Constructor
    public XlsxReader(String targetFilePath) {
        //Initialising
        listCloner = new ListCloner();
        this.targetFilePath = targetFilePath;
        try {
            fis = new FileInputStream(this.targetFilePath);
            wb = new XSSFWorkbook(fis);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    //Get all data from the given filepath	
    public List<Area> getAllData() throws Exception{
        //Assign a number for different filepath, because different format
        int indicator = targetFilePath.contains("2014") ? 3 : 4;
        List<Area> areaList = new ArrayList<>();

        //get first sheet in xlxs
        XSSFSheet sheet = wb.getSheetAt(0);

        //for each row in the sheet
        for (Row row : sheet) {
            int rowNum = row.getRowNum();
            if (rowNum > indicator && rowNum < (indicator + 12)) {
                //Create a map for storing the year and number of cases
                LinkedHashMap<Integer, Integer> yearsAndCasesMap = new LinkedHashMap<>();
                //Different file, different starting year
                int year = targetFilePath.contains("2014") ? 2014 : 2017;

                //for each cell in a row
                for (Cell cell : row) {
                    int cellNum = cell.getColumnIndex();

                    //Add the (Year,Number of cases) in the map
                    if (cellNum > 1 && cellNum < 5) {
                        yearsAndCasesMap.put(//Year, Number of cases
                                year,
                                (int) cell.getNumericCellValue()
                        );
                        year++;
                    }
                }

                //Add the name and map of an object to the list
                areaList.add(new Area(//Name, LinkedHashMap
                        row.getCell(1).getStringCellValue().trim(),
                        yearsAndCasesMap
                ));
            }
        }

        return areaList;
    }

    //Combine two list into a list
    public List<Area> combineList(List<Area> firstList, List<Area> secondList){
        //Deep clone to avoid modifying the the firstList 
        List<Area> combineList = listCloner.cloneList(firstList.iterator());
        combineList.stream().forEach((area) -> {
            area.getCasesList().putAll(
                    secondList.stream()
                            .filter(areaInSecondList -> areaInSecondList.getName().equals(area.getName()))
                            .findFirst()
                            .get()
                            .getCasesList()
            );
        });

        return combineList;
    }
}

