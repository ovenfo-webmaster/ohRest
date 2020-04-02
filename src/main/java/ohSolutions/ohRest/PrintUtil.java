package ohSolutions.ohRest;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.List;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.event.PrintJobAdapter;

public class PrintUtil implements Printable {

    @Override
    public int print(Graphics g, PageFormat pf, int page)
            throws PrinterException {
        if (page > 0) { /* We have only one page, and 'page' is zero-based */
            return NO_SUCH_PAGE;
        }

        /*
         * User (0,0) is typically outside the imageable area, so we must
         * translate by the X and Y values in the PageFormat to avoid clipping
         */
        Graphics2D g2d = (Graphics2D) g;
        g2d.translate(pf.getImageableX(), pf.getImageableY());
        /* Now we perform our rendering */

        g.setFont(new Font("Roman", 0, 8));
        g.drawString("Hello world !", 0, 10);

        return PAGE_EXISTS;
    }
    
    public List<String> getPrinters(){

        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

        PrintService printServices[] = PrintServiceLookup.lookupPrintServices(
                flavor, pras);

        List<String> printerList = new ArrayList<String>();
        for(PrintService printerService: printServices){
            printerList.add( printerService.getName());
        }

        return printerList;
    }
    
    public void printText(String printerName, String text) throws Exception{
    	printText(printerName, text, null);
    }
    
    public void printText(String printerName, String text, PrintJobAdapter callback) throws Exception{
    	printString(printerName, text+"\n\n\n\n\n", callback);
		printBytes(printerName, new byte[] { 0x1d, 'V', 1 }, callback);
    }

    public void printString(String printerName, String text, PrintJobAdapter callback) throws Exception {

        // find the printService of name printerName
        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

        PrintService printService[] = PrintServiceLookup.lookupPrintServices(
                flavor, pras);
        PrintService service = findPrintService(printerName, printService);
        
        if(service == null) {
        	throw new Exception("Impresora '"+printerName+"' no configurada en el servidor");
        }

        DocPrintJob job = service.createPrintJob();
        if(callback != null) {
        	job.addPrintJobListener(callback);
        }
        
        byte[] bytes;
        bytes = text.getBytes("CP437");
        
        Doc doc = new SimpleDoc(bytes, flavor, null);
        job.print(doc, null);

    }

    public void printBytes(String printerName, byte[] bytes, PrintJobAdapter callback) throws Exception {

        DocFlavor flavor = DocFlavor.BYTE_ARRAY.AUTOSENSE;
        PrintRequestAttributeSet pras = new HashPrintRequestAttributeSet();

        PrintService printService[] = PrintServiceLookup.lookupPrintServices(
                flavor, pras);
        PrintService service = findPrintService(printerName, printService);

        if(service == null) {
        	throw new Exception("Impresora '"+printerName+"' no configurada en el servidor");
        }
        
        DocPrintJob job = service.createPrintJob();
        if(callback != null) {
        	job.addPrintJobListener(callback);
        }
        
        Doc doc = new SimpleDoc(bytes, flavor, null);
        job.print(doc, null);
    }

    private PrintService findPrintService(String printerName,
            PrintService[] services) {
        for (PrintService service : services) {
            if (service.getName().equalsIgnoreCase(printerName)) {
                return service;
            }
        }

        return null;
    }
}