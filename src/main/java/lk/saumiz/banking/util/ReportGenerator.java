package lk.saumiz.banking.util;

import lk.saumiz.banking.entity.Transaction;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Compiles the AccountStatement.jrxml template (found on the classpath under /reports)
 * at runtime, fills it with account details + transaction history, and exports a PDF.
 *
 * Compiling the .jrxml on every call is slightly slower than pre-compiling to .jasper,
 * but keeps the project simple to build (no extra Maven step) - fine for this project's scale.
 */
public class ReportGenerator {

    private ReportGenerator() {}

    public static File generateAccountStatementPdf(String accountNo,
                                                     String customerName,
                                                     String accountType,
                                                     BigDecimal currentBalance,
                                                     List<Transaction> transactions,
                                                     File outputDir) throws JRException {

        try (InputStream jrxmlStream = ReportGenerator.class.getResourceAsStream("/reports/AccountStatement.jrxml")) {
            if (jrxmlStream == null) {
                throw new JRException("Could not find /reports/AccountStatement.jrxml on the classpath");
            }

            JasperReport report = JasperCompileManager.compileReport(jrxmlStream);

            Map<String, Object> params = new HashMap<>();
            params.put("ACCOUNT_NO", accountNo);
            params.put("CUSTOMER_NAME", customerName);
            params.put("ACCOUNT_TYPE", accountType);
            params.put("CURRENT_BALANCE", currentBalance);
            params.put("GENERATED_DATE", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(transactions);

            JasperPrint print = JasperFillManager.fillReport(report, params, dataSource);

            if (!outputDir.exists()) outputDir.mkdirs();
            String fileName = "Statement_" + accountNo + "_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
            File outputFile = new File(outputDir, fileName);

            JasperExportManager.exportReportToPdfFile(print, outputFile.getAbsolutePath());
            return outputFile;
        } catch (Exception e) {
            if (e instanceof JRException) throw (JRException) e;
            throw new JRException("Report generation failed: " + e.getMessage(), e);
        }
    }
}
