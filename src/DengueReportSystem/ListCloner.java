package DengueReportSystem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class ListCloner {
    
    //Immutability
    //Deep clone the list and object in the dataSource, 
    //prevent changing the original dataSource
    List<Area> cloneList(Iterator<Area> orgListIterator) {
        List<Area> cloneList = new ArrayList<>();

            orgListIterator.forEachRemaining(orgArea -> {
                cloneList.add(new Area(orgArea.getName(), 
                        new LinkedHashMap<>(orgArea.getCasesList())));
            });

        return cloneList;
    };
}
