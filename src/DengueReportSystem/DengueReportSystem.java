package DengueReportSystem;

import java.util.List;

public class DengueReportSystem {
    //Variables for reading the xlxs files
    private static final String FIRST_FILE_PATH = "C:\\Users\\Sharon\\Downloads\\Dengue\\Dengue\\src\\xlxs Files\\statistik-kes-denggi-di-negeri-pahang-bagi-tempoh-2014-2017.xlsx";
    private static final String SECOND_FILE_PATH = "C:\\Users\\Sharon\\Downloads\\Dengue\\Dengue\\src\\xlxs Files\\statistik-kes-denggi-di-negeri-pahang-bagi-tempoh-2018-2019.xlsx";
    private static XlsxReader reader;
    //Variable for storing the combine list
    private static List<Area> combinedDataSource;



    public static void main(String[] args){
        try{
            /****************Question 6 (Read Excel File)*******************/
            //Read xlxs files
            reader = new XlsxReader(FIRST_FILE_PATH);
            List<Area> firstDataSource = reader.getAllData();

            reader = new XlsxReader(SECOND_FILE_PATH);
            List<Area> secondDataSource = reader.getAllData();

            //Combine two list to a list
            combinedDataSource = reader.combineList(firstDataSource,secondDataSource);

            //Pass data to GUI HomePage
            GUIHomePage home = new GUIHomePage(combinedDataSource);
            home.setVisible(true);
            
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
