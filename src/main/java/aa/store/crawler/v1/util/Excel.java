package aa.store.crawler.v1.util;

import aa.store.crawler.v1.config.CrawlerConfig;
import aa.store.crawler.v1.model.store.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class Excel {

    private final CrawlerConfig config;

    public Map<String, List<Store>> getStoreListFromDatabase() throws IOException {

        String filePath = config.getFiles().getDatabase();

        InputStream inputStream = new FileInputStream(filePath);

        Workbook workbook = WorkbookFactory.create(inputStream);

        Map<String, List<Store>> database = new HashMap<>();
        for(int i = 0; i < workbook.getNumberOfSheets(); i ++) {
            List<Store> stores = new ArrayList<Store>();
            Sheet sheet = workbook.getSheetAt(i);
            Iterator<Row> rowItr = sheet.iterator();

            while (rowItr.hasNext()) {
                Store store = new Store();
                Row row = rowItr.next();
                if (row.getRowNum() == 0) {
                    continue;
                }
                Iterator<Cell> cellItr = row.cellIterator();
                while (cellItr.hasNext()) {
                    Cell cell = cellItr.next();
                    int index = cell.getColumnIndex();
                    switch (index) {
                        case 0:
                            store.setPlaceName(getValueFromCell(cell));
                            break;
                        case 1:
                            store.setCategory(getValueFromCell(cell));
                            break;
                        case 2:
                            store.setTel(getValueFromCell(cell));
                            break;
                        case 3:
                            store.setBusinessHour(getValueFromCell(cell));
                            break;
                        case 4:
                            store.setHomepage(getValueFromCell(cell));
                            break;
                        case 5:
                            store.setDescription(getValueFromCell(cell));
                            break;
                        case 6:
                            store.setConvenience(getValueFromCell(cell));
                            break;
                        case 7:
                            store.setShortAddress(getValueFromCell(cell));
                            break;
                        case 8:
                            store.setAddress(getValueFromCell(cell));
                            break;
                        case 9:
                            store.setRoadAddress(getValueFromCell(cell));
                            break;
                        case 10:
                            store.setLatitude(getValueFromCell(cell));
                            break;
                        case 11:
                            store.setLongitude(getValueFromCell(cell));
                            break;
                        case 12:
                            store.setMapUrl(getValueFromCell(cell));
                            break;
                        case 13:
                            store.setPostTitle(getValueFromCell(cell));
                            break;
                        case 14:
                            store.setPostUrl(getValueFromCell(cell));
                            break;
                        case 15:
                            store.setCount(getValueFromCell(cell));
                            break;
                    }
                }
                stores.add(store);
            }
            database.put(sheet.getSheetName(), stores);
        }

        return database;
    }

    private static String getValueFromCell(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return String.valueOf(cell.getDateCellValue());
                }
                return String.valueOf(cell.getNumericCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }


}

