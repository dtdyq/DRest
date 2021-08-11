

package rest.controller.plugin.eximport.excel;

import jxl.Workbook;
import jxl.read.biff.BiffException;

import java.io.File;
import java.io.IOException;

public class ExcelTest {



    public static void main(String[] args) throws IOException, BiffException {
        Workbook book = Workbook.getWorkbook(new File(""));
        book.getSheets()[0].getName();
    }
}
