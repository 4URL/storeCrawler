package aa.store.crawler.v1.util;

import aa.store.crawler.v1.config.CrawlerConfig;
import aa.store.crawler.v1.model.store.NewSheet;
import aa.store.crawler.v1.model.store.Sheets;
import aa.store.crawler.v1.model.store.Store;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExcelUtil {

    private final CrawlerConfig config;

    public Map<String, List<Store>> getStoreListFromDatabase() throws IOException {

        String filePath = config.getFiles().getDatabase();

        InputStream inputStream = new FileInputStream(filePath);

        Workbook workbook = WorkbookFactory.create(inputStream);

        Map<String, List<Store>> database = new HashMap<>();
//        for(int i = 0; i < workbook.getNumberOfSheets(); i ++) {
        for(Sheets sheetName : Sheets.values()) {
            List<Store> stores = new ArrayList<Store>();
            Sheet sheet = workbook.getSheet(sheetName.getSheetName());
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
                            store.setCount((int) Math.round(Double.valueOf(getValueFromCell(cell))));
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

    public void createResult(Map<String, List<Store>> database) {
        log.info("Excel Create : {}", database.size());
        //.xlsx 확장자 지원
        XSSFWorkbook xssfWb = new XSSFWorkbook(); // .xlsx
        try {
            for(Sheets sheet : Sheets.values()) {
                // 없는 시트인 경우 Continue
                if(database.get(sheet.getSheetName()) == null) continue;
                makeSheet(xssfWb, database, sheet.getSheetName());
            }
            for(NewSheet sheet : NewSheet.values()) {
                // 없는 시트인 경우 Continue
                if(database.get(sheet.getSheetName()) == null) continue;
                makeSheet(xssfWb, database, sheet.getSheetName());
            }
            String localFile = makeExcelFileName(config.getFiles().getResult());
            File file = new File(localFile);
            FileOutputStream fos = new FileOutputStream(file);
            xssfWb.write(fos);

            if (xssfWb != null) xssfWb.close();
            if (fos != null) fos.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }finally{

        }
    }

    private void makeSheet(XSSFWorkbook xssfWb, Map<String, List<Store>> database, String sheetName) {
        //헤더용 폰트 스타일
        XSSFFont font = xssfWb.createFont();
        font.setFontName(HSSFFont.FONT_ARIAL); //폰트스타일
        font.setFontHeightInPoints((short) 14); //폰트크기
        font.setBold(true); //Bold 유무

        //테이블 타이틀 스타일
        CellStyle cellStyleTitle = xssfWb.createCellStyle();
        cellStyleTitle.setFont(font); // cellStle에 font를 적용
        cellStyleTitle.setAlignment(HorizontalAlignment.CENTER); // 정렬

        CellStyle cellStyleBody = xssfWb.createCellStyle();
        cellStyleBody.setAlignment(HorizontalAlignment.LEFT);

        XSSFSheet xssfSheet = null; // .xlsx
        XSSFRow xssfRow = null; // .xlsx
        XSSFCell xssfCell = null;// .xlsx

        int rowNo = 0; // 행 갯수
        // 워크북 생성
        xssfSheet = xssfWb.createSheet(sheetName); // 워크시트 이름

        xssfSheet.setColumnWidth(0, (xssfSheet.getColumnWidth(3)) + 2048); // 3번째 컬럼 넓이 조절
        xssfSheet.setColumnWidth(1, (xssfSheet.getColumnWidth(3)) + 2048); // 3번째 컬럼 넓이 조절
        xssfSheet.setColumnWidth(2, (xssfSheet.getColumnWidth(3)) + 2048); // 3번째 컬럼 넓이 조절
        xssfSheet.setColumnWidth(3, (xssfSheet.getColumnWidth(3)) + 2048); // 3번째 컬럼 넓이 조절
        xssfSheet.setColumnWidth(4, (xssfSheet.getColumnWidth(3)) + 2048); // 3번째 컬럼 넓이 조절
        xssfSheet.setColumnWidth(5, (xssfSheet.getColumnWidth(3)) + 2048); // 3번째 컬럼 넓이 조절
        xssfSheet.setColumnWidth(6, (xssfSheet.getColumnWidth(3)) + 2048); // 3번째 컬럼 넓이 조절
        xssfSheet.setColumnWidth(7, (xssfSheet.getColumnWidth(3)) + 2048); // 3번째 컬럼 넓이 조절
        xssfSheet.setColumnWidth(8, (xssfSheet.getColumnWidth(3)) + 2048); // 3번째 컬럼 넓이 조절
        xssfSheet.setColumnWidth(9, (xssfSheet.getColumnWidth(3)) + 2048); // 3번째 컬럼 넓이 조절
        xssfSheet.setColumnWidth(10, (xssfSheet.getColumnWidth(3)) + 2048); // 3번째 컬럼 넓이 조절
        xssfSheet.setColumnWidth(11, (xssfSheet.getColumnWidth(3)) + 2048); // 3번째 컬럼 넓이 조절
        xssfSheet.setColumnWidth(12, (xssfSheet.getColumnWidth(3)) + 2048); // 3번째 컬럼 넓이 조절
        xssfSheet.setColumnWidth(13, (xssfSheet.getColumnWidth(3)) + 2048); // 3번째 컬럼 넓이 조절
        xssfSheet.setColumnWidth(14, (xssfSheet.getColumnWidth(3)) + 2048); // 3번째 컬럼 넓이 조절
        xssfSheet.setColumnWidth(15, (xssfSheet.getColumnWidth(4)) + 2048); // 4번째 컬럼 넓이 조절

        //타이틀 생성
        xssfRow = xssfSheet.createRow(rowNo++); //행 객체 추가
        xssfCell = xssfRow.createCell(0);
        xssfCell.setCellStyle(cellStyleTitle);
        xssfCell.setCellValue("업체명");
        xssfCell = xssfRow.createCell(1);
        xssfCell.setCellStyle(cellStyleTitle);
        xssfCell.setCellValue("카테고리");
        xssfCell = xssfRow.createCell(2);
        xssfCell.setCellStyle(cellStyleTitle);
        xssfCell.setCellValue("전화번호");
        xssfCell = xssfRow.createCell(3);
        xssfCell.setCellStyle(cellStyleTitle);
        xssfCell.setCellValue("영업시간");
        xssfCell = xssfRow.createCell(4);
        xssfCell.setCellStyle(cellStyleTitle);
        xssfCell.setCellValue("홈페이지");
        xssfCell = xssfRow.createCell(5);
        xssfCell.setCellStyle(cellStyleTitle);
        xssfCell.setCellValue("설명");
        xssfCell = xssfRow.createCell(6);
        xssfCell.setCellStyle(cellStyleTitle);
        xssfCell.setCellValue("편의시설");
        xssfCell = xssfRow.createCell(7);
        xssfCell.setCellStyle(cellStyleTitle);
        xssfCell.setCellValue("짧은 주소");
        xssfCell = xssfRow.createCell(8);
        xssfCell.setCellStyle(cellStyleTitle);
        xssfCell.setCellValue("주소");
        xssfCell = xssfRow.createCell(9);
        xssfCell.setCellStyle(cellStyleTitle);
        xssfCell.setCellValue("도로명 주소");
        xssfCell = xssfRow.createCell(10);
        xssfCell.setCellStyle(cellStyleTitle);
        xssfCell.setCellValue("위도");
        xssfCell = xssfRow.createCell(11);
        xssfCell.setCellStyle(cellStyleTitle);
        xssfCell.setCellValue("경도");
        xssfCell = xssfRow.createCell(12);
        xssfCell.setCellStyle(cellStyleTitle);
        xssfCell.setCellValue("지도 URL");
        xssfCell = xssfRow.createCell(13);
        xssfCell.setCellStyle(cellStyleTitle);
        xssfCell.setCellValue("포스트 제목");
        xssfCell = xssfRow.createCell(14);
        xssfCell.setCellStyle(cellStyleTitle);
        xssfCell.setCellValue("포스트 URL");
        xssfCell = xssfRow.createCell(15);
        xssfCell.setCellStyle(cellStyleTitle);
        xssfCell.setCellValue("포스트 개수");

        for (Store store : database.get(sheetName)) {

            //헤더 생성
            xssfRow = xssfSheet.createRow(rowNo++); //헤더 01
            xssfCell = xssfRow.createCell(0);
            xssfCell.setCellStyle(cellStyleBody);
            xssfCell.setCellValue(store.getPlaceName());
            xssfCell = xssfRow.createCell(1);
            xssfCell.setCellStyle(cellStyleBody);
            xssfCell.setCellValue(store.getCategory());
            xssfCell = xssfRow.createCell(2);
            xssfCell.setCellStyle(cellStyleBody);
            xssfCell.setCellValue(store.getTel());
            xssfCell = xssfRow.createCell(3);
            xssfCell.setCellStyle(cellStyleBody);
            xssfCell.setCellValue(store.getBusinessHour());
            xssfCell = xssfRow.createCell(4);
            xssfCell.setCellStyle(cellStyleBody);
            xssfCell.setCellValue(store.getHomepage());
            xssfCell = xssfRow.createCell(5);
            xssfCell.setCellStyle(cellStyleBody);
            xssfCell.setCellValue(store.getDescription());
            xssfCell = xssfRow.createCell(6);
            xssfCell.setCellStyle(cellStyleBody);
            xssfCell.setCellValue(store.getConvenience());
            xssfCell = xssfRow.createCell(7);
            xssfCell.setCellStyle(cellStyleBody);
            xssfCell.setCellValue(store.getShortAddress());
            xssfCell = xssfRow.createCell(8);
            xssfCell.setCellStyle(cellStyleBody);
            xssfCell.setCellValue(store.getAddress());
            xssfCell = xssfRow.createCell(9);
            xssfCell.setCellStyle(cellStyleBody);
            xssfCell.setCellValue(store.getRoadAddress());
            xssfCell = xssfRow.createCell(10);
            xssfCell.setCellStyle(cellStyleBody);
            xssfCell.setCellValue(store.getLatitude());
            xssfCell = xssfRow.createCell(11);
            xssfCell.setCellStyle(cellStyleBody);
            xssfCell.setCellValue(store.getLongitude());
            xssfCell = xssfRow.createCell(12);
            xssfCell.setCellStyle(cellStyleBody);
            xssfCell.setCellValue(store.getMapUrl());
            xssfCell = xssfRow.createCell(13);
            xssfCell.setCellStyle(cellStyleBody);
            xssfCell.setCellValue(store.getPostTitle());
            xssfCell = xssfRow.createCell(14);
            xssfCell.setCellStyle(cellStyleBody);
            xssfCell.setCellValue(store.getPostUrl());
            xssfCell = xssfRow.createCell(15);
            xssfCell.setCellStyle(cellStyleBody);
            xssfCell.setCellValue(store.getCount());
        }
    }

    private String makeExcelFileName(String originalName) {
        return originalName.split("\\.")[0]
                + "_"
                + DateTimeFormat.forPattern("yyyyMMddHHmmss").print(DateTime.now())
                + "."
                + originalName.split("\\.")[1];
    }
}

