package DengueReportSystem;

import JPLConnection.JPLConnection;
import org.jpl7.Term;

import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GUISecondPage extends javax.swing.JFrame {
    //Variable for GUI draggable
    private int mousepX, mousepY;

    //List to store the original data source
    private List<Area> orgDataSource;

    //Map for storing the total cases per area(Question 3)
    private LinkedHashMap<String,Integer> sumPerAreaMap;
    //Map for storing the sorted total cases per area(Part 2:Prolog)
    private LinkedHashMap<String,Term> sortedWithPrologResultMap;
    
    public GUISecondPage(List<Area> orgDataSource,List<Area> newAreaListFromHome) {
        try{
            //Initialising
            initComponents();
            sortedWithPrologResultMap = new LinkedHashMap<>();

            //Do not need deep clone
            //because this list will only be used for passing back to home GUI
            this.orgDataSource = orgDataSource;

            //Question 3
            sumPerAreaMap = calcSumPerAreaForAll.apply(newAreaListFromHome);
            bindUnsortedDataToTable(sumPerAreaMap);

            //Question 4
            bindLowestCasesDataToTable(findLowestCases.apply(sumPerAreaMap));

            //Question 5
            Optional<Integer> highestCasesValue = findHighestCasesValue.apply(sumPerAreaMap);
            highestCasesValue.ifPresent(value -> bindHighestCasesDataToTable(findHighestCasesMap.apply(sumPerAreaMap, value)));

        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    
    /****************Question 3*******************/
    //Calculate the total cases for an area from 2014-2019 using recursion
    private BiFunction<LinkedHashMap<Integer, Integer>, Integer, Integer> calcSumForAnArea
            = (casesMap, year) -> year < 2020
                    //If year is less than 2020, then continue recursion
                    //Else, return 0 and stop the recursive call
                    ? this.calcSumForAnArea.apply(casesMap, year + 1) + casesMap.get(year)
                    : 0;
    
    //Create a map for storing the data(Total cases for each area)
    private Function<List<Area>, LinkedHashMap<String, Integer>> calcSumPerAreaForAll = (areaList) -> {
        //Create a new map to store the key(Area Name) and values(Sum Per Area)
        LinkedHashMap<String, Integer> completeSumPerAreaMap = new LinkedHashMap<>();
        //Calculate and store the result in the map.
        areaList.stream().forEach((area) -> 
                completeSumPerAreaMap.put(area.getName(), calcSumForAnArea.apply(area.getCasesList(), 2014))
        );
        return completeSumPerAreaMap;
    };
    
    
    /****************Question 4*******************/
    //Find the area that have the lowest total cases per area
    private Function<LinkedHashMap<String,Integer>, Map<String,Integer>> findLowestCases =(sumPerAreaMap)->{
        //using min() to compare and find the lowest case value
        return sumPerAreaMap
                .entrySet()
                .stream()
                .min((elem1,elem2)-> elem1.getValue() < elem2.getValue()? -1:1)
                .stream()
                .collect(Collectors.toMap(elem -> elem.getKey(),elem ->elem.getValue()));
    };
    
    
    /****************Question 5*******************/
    //Find the highest value of the total cases per area
    private Function<LinkedHashMap<String, Integer>, Optional<Integer>> findHighestCasesValue = (sumPerAreaMap) -> {
        //Filter out the Pahang(Total)
        //then compare using reduce()
        return sumPerAreaMap.entrySet().stream()
                .filter(entry -> !"Pahang(Total)".equals(entry.getKey()))
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> entry.getValue(),
                        (x, y) -> x,
                        LinkedHashMap::new))
                .values().stream()
                .reduce((e1, e2) -> e1 > e2 ? e1 : e2);
    };
   
    //Find the area that have the highest value
    private BiFunction<LinkedHashMap<String, Integer>, Integer, LinkedHashMap<String, Integer>> 
        findHighestCasesMap = (sumPerAreaMap, highestValue) -> {
        //find the area that containing the same value as the highestValue
        return sumPerAreaMap
                .entrySet()
                .stream()
                .filter(entry -> highestValue.equals(entry.getValue()))
                .collect(Collectors.toMap(
                        entry -> entry.getKey(),
                        entry -> entry.getValue(),
                        (x, y) -> x,
                        LinkedHashMap::new));
    };
    
    
    /***********Part 2: Prolog***********/
    //convert the map to List<String> for the purpose of constructing the query
    private Function<LinkedHashMap<String, Integer>, List<String>> convertMapToStringList = (sumPerAreaMap) -> {
        //Create a new ArrayList
        List<String> stringList = new ArrayList<>();
        //Filter out the Pahang(Total)
        //Then, add into the stringList with certain format
        sumPerAreaMap.entrySet().stream()
                .filter(entry -> !"Pahang(Total)".equals(entry.getKey()))
                .forEach(entry -> {
                    stringList.add("('" + entry.getKey() + "'," + entry.getValue() + ")");
                });
        return stringList;
    };
    
    //Construct the full sorting query by adding in the stringList
    private Function<List<String>, String> constructQuery = (stringList) -> {
        return "quicksort(" + stringList + ",SortedResult).";
    };

    
    /****************GUI*******************/
    /****************Question 3*******************/
    //Bind unsorted data to the table
    private void bindUnsortedDataToTable(LinkedHashMap<String,Integer> sumPerAreaList){
        //Get the table model 
        DefaultTableModel model = (DefaultTableModel)tbl_total_cases_per_area.getModel();
        
        //Remove the original data in the table
        model.setRowCount(0);
        //Create an object array
        Object[] cell = new Object[2];
        //Adding the data into the table row
        sumPerAreaList.entrySet().stream().forEach((area)->{
            cell[0] = area.getKey();
            cell[1] = area.getValue();
            
            model.addRow(cell);
        });
        
        //Highlight the last row of the table
        tbl_total_cases_per_area.addRowSelectionInterval(11, 11);
    }
    
    /****************Part 2:Prolog*******************/
    //Bind sorted data to the table
    private void bindSortedDataToTable(LinkedHashMap<String,Term> sortedMap){
        //Get the table model 
        DefaultTableModel model = (DefaultTableModel)tbl_total_cases_per_area.getModel();
        
        //Remove the original data in the table
        model.setRowCount(0);
        //Create an object array
        Object[] cell = new Object[2];
        //Adding the data into the table row
        sortedMap.entrySet().stream().forEach((area)->{
            cell[0] = area.getKey();
            cell[1] = area.getValue();
            
            model.addRow(cell);
        });
    }
    
    
    /****************Question 4*******************/
    //Bind lowest value data to the table
    private void bindLowestCasesDataToTable(Map<String,Integer> lowestCasesMap){
        //Get the table model 
        DefaultTableModel model = (DefaultTableModel)tbl_lowest_cases.getModel();
        
        //Create an object array
        Object[] cell = new Object[2];
        //Adding the data into the table row
        lowestCasesMap.entrySet().stream().forEach((area) -> {
                
            cell[0] = area.getKey();
            cell[1] = area.getValue();
            
            model.addRow(cell);
        });
    }
    
    /****************Question 5*******************/
    //Bind highest value data to the table
    private void bindHighestCasesDataToTable(LinkedHashMap<String,Integer> highestCasesMap){
        //Get the table model 
        DefaultTableModel model = (DefaultTableModel)tbl_highest_cases.getModel();

        //Create an object array
        Object[] cell = new Object[2];
        //Adding the data into the table row
        highestCasesMap.entrySet().stream().forEach((area) -> {
                
            cell[0] = area.getKey();
            cell[1] = area.getValue();
            
            model.addRow(cell);
        });
    }
        
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel_bg = new javax.swing.JPanel();
        panel_menu = new javax.swing.JPanel();
        btn_allCases = new javax.swing.JPanel();
        lbl_allCases_icon = new javax.swing.JLabel();
        lbl_allCases = new javax.swing.JLabel();
        btn_areaCases = new javax.swing.JPanel();
        lbl_areaCases_icon = new javax.swing.JLabel();
        lbl_areaCases = new javax.swing.JLabel();
        sprt_mainTitle = new javax.swing.JSeparator();
        lbl_mainTitle = new javax.swing.JLabel();
        panel_title = new javax.swing.JPanel();
        lbl_title = new javax.swing.JLabel();
        jsp_total_cases_per_area = new javax.swing.JScrollPane();
        tbl_total_cases_per_area = new javax.swing.JTable();
        jsp_lowest_case = new javax.swing.JScrollPane();
        tbl_lowest_cases = new javax.swing.JTable();
        lbl_lowest_cases = new javax.swing.JLabel();
        jsp_highest_cases = new javax.swing.JScrollPane();
        tbl_highest_cases = new javax.swing.JTable();
        lbl_highest_cases = new javax.swing.JLabel();
        lbl_closeBtn = new javax.swing.JLabel();
        panel_unsort = new javax.swing.JPanel();
        lbl_unsortBtn = new javax.swing.JLabel();
        panel_sort = new javax.swing.JPanel();
        lbl_sortBtn = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setLocationByPlatform(true);
        setUndecorated(true);
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                formMouseDragged(evt);
            }
        });
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });

        panel_bg.setBackground(new java.awt.Color(247, 247, 247));
        panel_bg.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        panel_menu.setBackground(new java.awt.Color(54, 33, 89));
        panel_menu.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        btn_allCases.setBackground(new java.awt.Color(64, 43, 100));
        btn_allCases.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btn_allCasesMouseClicked(evt);
            }
        });

        lbl_allCases_icon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_allCases_icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconImages/Cases_Icon.png"))); // NOI18N

        lbl_allCases.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_allCases.setForeground(new java.awt.Color(255, 255, 255));
        lbl_allCases.setText("All Dengue Cases");

        javax.swing.GroupLayout btn_allCasesLayout = new javax.swing.GroupLayout(btn_allCases);
        btn_allCases.setLayout(btn_allCasesLayout);
        btn_allCasesLayout.setHorizontalGroup(
            btn_allCasesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btn_allCasesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_allCases_icon, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbl_allCases)
                .addContainerGap(52, Short.MAX_VALUE))
        );
        btn_allCasesLayout.setVerticalGroup(
            btn_allCasesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btn_allCasesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(btn_allCasesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl_allCases)
                    .addComponent(lbl_allCases_icon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel_menu.add(btn_allCases, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 70, 220, -1));

        btn_areaCases.setBackground(new java.awt.Color(85, 65, 118));

        lbl_areaCases_icon.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_areaCases_icon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconImages/PinPoint_Icon.png"))); // NOI18N

        lbl_areaCases.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lbl_areaCases.setForeground(new java.awt.Color(255, 255, 255));
        lbl_areaCases.setText("Dengue Cases per Area");

        javax.swing.GroupLayout btn_areaCasesLayout = new javax.swing.GroupLayout(btn_areaCases);
        btn_areaCases.setLayout(btn_areaCasesLayout);
        btn_areaCasesLayout.setHorizontalGroup(
            btn_areaCasesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btn_areaCasesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_areaCases_icon, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lbl_areaCases)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        btn_areaCasesLayout.setVerticalGroup(
            btn_areaCasesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(btn_areaCasesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl_areaCases)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(lbl_areaCases_icon, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        panel_menu.add(btn_areaCases, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 110, 220, -1));

        sprt_mainTitle.setPreferredSize(new java.awt.Dimension(50, 5));
        sprt_mainTitle.setRequestFocusEnabled(false);
        panel_menu.add(sprt_mainTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 42, 180, 10));

        lbl_mainTitle.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        lbl_mainTitle.setForeground(new java.awt.Color(255, 255, 255));
        lbl_mainTitle.setText("Dengue Report");
        panel_menu.add(lbl_mainTitle, new org.netbeans.lib.awtextra.AbsoluteConstraints(23, 10, -1, -1));

        panel_bg.add(panel_menu, new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 0, 221, 480));

        panel_title.setBackground(new java.awt.Color(110, 89, 222));

        lbl_title.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        lbl_title.setForeground(new java.awt.Color(255, 255, 255));
        lbl_title.setText("Total Dengue Cases per Area from 2014-2019");
        lbl_title.setToolTipText("");

        javax.swing.GroupLayout panel_titleLayout = new javax.swing.GroupLayout(panel_title);
        panel_title.setLayout(panel_titleLayout);
        panel_titleLayout.setHorizontalGroup(
            panel_titleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_titleLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(lbl_title)
                .addContainerGap(183, Short.MAX_VALUE))
        );
        panel_titleLayout.setVerticalGroup(
            panel_titleLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_titleLayout.createSequentialGroup()
                .addGap(23, 23, 23)
                .addComponent(lbl_title)
                .addContainerGap(25, Short.MAX_VALUE))
        );

        panel_bg.add(panel_title, new org.netbeans.lib.awtextra.AbsoluteConstraints(215, 35, 690, 80));

        jsp_total_cases_per_area.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jsp_total_cases_per_area.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jsp_total_cases_per_area.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        tbl_total_cases_per_area.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        tbl_total_cases_per_area.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tbl_total_cases_per_area.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tbl_total_cases_per_area.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Area", "Total Cases"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbl_total_cases_per_area.setGridColor(new java.awt.Color(255, 255, 255));
        tbl_total_cases_per_area.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tbl_total_cases_per_area.setRowHeight(25);
        tbl_total_cases_per_area.setSelectionBackground(new java.awt.Color(204, 204, 255));
        tbl_total_cases_per_area.setShowGrid(true);
        jsp_total_cases_per_area.setViewportView(tbl_total_cases_per_area);
        if (tbl_total_cases_per_area.getColumnModel().getColumnCount() > 0) {
            tbl_total_cases_per_area.getColumnModel().getColumn(0).setResizable(false);
            tbl_total_cases_per_area.getColumnModel().getColumn(0).setPreferredWidth(10);
            tbl_total_cases_per_area.getColumnModel().getColumn(1).setResizable(false);
            tbl_total_cases_per_area.getColumnModel().getColumn(1).setPreferredWidth(10);
        }

        panel_bg.add(jsp_total_cases_per_area, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 130, 320, 325));

        jsp_lowest_case.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jsp_lowest_case.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        tbl_lowest_cases.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        tbl_lowest_cases.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tbl_lowest_cases.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tbl_lowest_cases.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Area", "Total Cases"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbl_lowest_cases.setGridColor(new java.awt.Color(255, 255, 255));
        tbl_lowest_cases.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tbl_lowest_cases.setRowHeight(25);
        tbl_lowest_cases.setSelectionBackground(new java.awt.Color(122, 72, 221));
        tbl_lowest_cases.setShowGrid(true);
        jsp_lowest_case.setViewportView(tbl_lowest_cases);
        if (tbl_lowest_cases.getColumnModel().getColumnCount() > 0) {
            tbl_lowest_cases.getColumnModel().getColumn(0).setResizable(false);
            tbl_lowest_cases.getColumnModel().getColumn(0).setPreferredWidth(10);
            tbl_lowest_cases.getColumnModel().getColumn(1).setResizable(false);
            tbl_lowest_cases.getColumnModel().getColumn(1).setPreferredWidth(10);
        }

        panel_bg.add(jsp_lowest_case, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 160, 300, 80));

        lbl_lowest_cases.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_lowest_cases.setText("Area of the Lowest Cases:");
        panel_bg.add(lbl_lowest_cases, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 130, -1, -1));

        jsp_highest_cases.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        jsp_highest_cases.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        jsp_highest_cases.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

        tbl_highest_cases.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        tbl_highest_cases.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        tbl_highest_cases.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        tbl_highest_cases.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Area", "Total Cases"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                true, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbl_highest_cases.setGridColor(new java.awt.Color(255, 255, 255));
        tbl_highest_cases.setIntercellSpacing(new java.awt.Dimension(0, 0));
        tbl_highest_cases.setRowHeight(25);
        tbl_highest_cases.setSelectionBackground(new java.awt.Color(122, 72, 221));
        tbl_highest_cases.setShowGrid(true);
        jsp_highest_cases.setViewportView(tbl_highest_cases);
        if (tbl_highest_cases.getColumnModel().getColumnCount() > 0) {
            tbl_highest_cases.getColumnModel().getColumn(0).setResizable(false);
            tbl_highest_cases.getColumnModel().getColumn(0).setPreferredWidth(10);
            tbl_highest_cases.getColumnModel().getColumn(1).setResizable(false);
            tbl_highest_cases.getColumnModel().getColumn(1).setPreferredWidth(10);
        }

        panel_bg.add(jsp_highest_cases, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 300, 300, 80));

        lbl_highest_cases.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_highest_cases.setText("Area of the Highest Cases:");
        panel_bg.add(lbl_highest_cases, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 270, -1, -1));

        lbl_closeBtn.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_closeBtn.setForeground(new java.awt.Color(54, 33, 89));
        lbl_closeBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_closeBtn.setText("X");
        lbl_closeBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                lbl_closeBtnMousePressed(evt);
            }
        });
        panel_bg.add(lbl_closeBtn, new org.netbeans.lib.awtextra.AbsoluteConstraints(876, 0, 30, 30));

        panel_unsort.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        panel_unsort.setForeground(new java.awt.Color(204, 204, 204));
        panel_unsort.setEnabled(false);
        panel_unsort.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panel_unsortMouseClicked(evt);
            }
        });

        lbl_unsortBtn.setBackground(new java.awt.Color(0, 0, 0));
        lbl_unsortBtn.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_unsortBtn.setForeground(java.awt.Color.gray);
        lbl_unsortBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_unsortBtn.setText("Unsort");

        javax.swing.GroupLayout panel_unsortLayout = new javax.swing.GroupLayout(panel_unsort);
        panel_unsort.setLayout(panel_unsortLayout);
        panel_unsortLayout.setHorizontalGroup(
            panel_unsortLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel_unsortLayout.createSequentialGroup()
                .addContainerGap(30, Short.MAX_VALUE)
                .addComponent(lbl_unsortBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28))
        );
        panel_unsortLayout.setVerticalGroup(
            panel_unsortLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbl_unsortBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
        );

        panel_bg.add(panel_unsort, new org.netbeans.lib.awtextra.AbsoluteConstraints(750, 415, 140, 40));

        panel_sort.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(204, 204, 204), 1, true));
        panel_sort.setForeground(new java.awt.Color(204, 204, 204));
        panel_sort.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                panel_sortMouseClicked(evt);
            }
        });

        lbl_sortBtn.setBackground(new java.awt.Color(0, 0, 0));
        lbl_sortBtn.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lbl_sortBtn.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lbl_sortBtn.setText("Sort");

        javax.swing.GroupLayout panel_sortLayout = new javax.swing.GroupLayout(panel_sort);
        panel_sort.setLayout(panel_sortLayout);
        panel_sortLayout.setHorizontalGroup(
            panel_sortLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel_sortLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(lbl_sortBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );
        panel_sortLayout.setVerticalGroup(
            panel_sortLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(lbl_sortBtn, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
        );

        panel_bg.add(panel_sort, new org.netbeans.lib.awtextra.AbsoluteConstraints(590, 415, 140, 40));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel_bg, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(panel_bg, javax.swing.GroupLayout.PREFERRED_SIZE, 471, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        setSize(new java.awt.Dimension(906, 471));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void lbl_closeBtnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbl_closeBtnMousePressed
        //Exit button
        System.exit(0);
    }//GEN-LAST:event_lbl_closeBtnMousePressed

    private void btn_allCasesMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btn_allCasesMouseClicked
        //Direct to all cases page
        GUIHomePage home = new GUIHomePage(orgDataSource);
        home.setVisible(true);
        this.setVisible(false);
        
    }//GEN-LAST:event_btn_allCasesMouseClicked

    private void formMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseDragged
        //Draggable
        int coordinateX = evt.getXOnScreen();
        int coordinateY = evt.getYOnScreen();
        this.setLocation(coordinateX-mousepX,coordinateY-mousepY);
    }//GEN-LAST:event_formMouseDragged

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        // Draggable - get X and Y 
        mousepX = evt.getX();
        mousepY = evt.getY();
    }//GEN-LAST:event_formMousePressed

    private void panel_unsortMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_unsortMouseClicked
        try{
            //Display the sumPerAreaMap to the table
            this.bindUnsortedDataToTable(sumPerAreaMap);
            
        }catch(Exception e){e.printStackTrace();}
        
        //Controlling the GUI button
        panel_unsort.setEnabled(false);
        lbl_unsortBtn.setForeground(Color.GRAY);
        panel_sort.setEnabled(true);
        lbl_sortBtn.setForeground(Color.BLACK);
    }//GEN-LAST:event_panel_unsortMouseClicked

    private void panel_sortMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_panel_sortMouseClicked
        try{
            //Convert the sumPerAreaMap to list and 
            //combine it in constructing the query text
            List<String> stringList = convertMapToStringList.apply(sumPerAreaMap);
            String queryText = constructQuery.apply(stringList);
            System.out.println(queryText);
            //Java-Prolog Connectivity
            //Consult and check the connection
            JPLConnection conn = new JPLConnection();
            conn.consult();
            //Send query and get the sorting result
            Term sortingResult = conn.sortingQuery(queryText);
            //Convert the sorting result to a map for displaying on GUI
            sortedWithPrologResultMap = conn.convertSortingResultToMap(sortingResult, sortedWithPrologResultMap);

            //Display the sorted resultMap to the table
            this.bindSortedDataToTable(sortedWithPrologResultMap);

            //Controlling the GUI button
            panel_sort.setEnabled(false);
            lbl_sortBtn.setForeground(Color.GRAY);
            panel_unsort.setEnabled(true);
            lbl_unsortBtn.setForeground(Color.BLACK);
  
        }catch(Exception e)
        {
            e.printStackTrace();
        }
    }//GEN-LAST:event_panel_sortMouseClicked
    
    /**
     * @param args the command line arguments
     */
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel btn_allCases;
    private javax.swing.JPanel btn_areaCases;
    private javax.swing.JScrollPane jsp_highest_cases;
    private javax.swing.JScrollPane jsp_lowest_case;
    private javax.swing.JScrollPane jsp_total_cases_per_area;
    private javax.swing.JLabel lbl_allCases;
    private javax.swing.JLabel lbl_allCases_icon;
    private javax.swing.JLabel lbl_areaCases;
    private javax.swing.JLabel lbl_areaCases_icon;
    private javax.swing.JLabel lbl_closeBtn;
    private javax.swing.JLabel lbl_highest_cases;
    private javax.swing.JLabel lbl_lowest_cases;
    private javax.swing.JLabel lbl_mainTitle;
    private javax.swing.JLabel lbl_sortBtn;
    private javax.swing.JLabel lbl_title;
    private javax.swing.JLabel lbl_unsortBtn;
    private javax.swing.JPanel panel_bg;
    private javax.swing.JPanel panel_menu;
    private javax.swing.JPanel panel_sort;
    private javax.swing.JPanel panel_title;
    private javax.swing.JPanel panel_unsort;
    private javax.swing.JSeparator sprt_mainTitle;
    private javax.swing.JTable tbl_highest_cases;
    private javax.swing.JTable tbl_lowest_cases;
    private javax.swing.JTable tbl_total_cases_per_area;
    // End of variables declaration//GEN-END:variables
}
