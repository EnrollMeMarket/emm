package knbit.emm.utilities;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import knbit.emm.model.*;
import knbit.emm.repository.DoneSwapRepository;
import knbit.emm.repository.MarketRepository;
import knbit.emm.service.MarketPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import javax.persistence.NoResultException;
import java.io.*;
import java.sql.Time;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;


@Service
public class ReportGenerator {

    private final String llamaPhotoPath = "images/llama_blue.png";
    private final String fontPath = "fonts/arialuni.ttf";
    private final String tmpFolder = "tmp/";

    private static final String NR_CELL_VALUE = "Nr";
    private static final String FIRST_STUDENT_NAME_CELL_VALUE = "#1 Name";
    private static final String FIRST_CLASS_CELL_VALUE = "#1 Class";
    private static final String SECOND_STUDENT_NAME_CELL_VALUE = "#2 Name";
    private static final String SECOND_CLASS_CELL_VALUE = "#2 Class";

    private static final int LLAMA_PHOTO_SIDE_SIZE = 35;
    private static final int AUTHOR_AND_DATE_FONT_SIZE = 8;
    private static final int TITLE_FONT_SIZE = 22;
    private static final int PARAGRAPH_TABLE_FONT_SIZE = 10;

    private static final float CLASS_COLUMN_PERCENT_WIDTH = 0.37f;
    private static final float STUDENT_NAME_COLUMN_PERCENT_WIDTH = 0.11f;
    private static final float COUNTER_COLUMN_PERCENT_WIDTH = 0.04f;

    private final BaseFont arialUnicodeFont;
    private final Font authorAndDateFont;
    private final Font titleFont;

    private final MarketRepository marketRepository;
    private final DoneSwapRepository doneSwapRepository;
    private final MarketPermissionService marketPermissionService;

    private LocalDateTime createReportTime;

    @Autowired
    public ReportGenerator(
            MarketRepository marketRepository,
            DoneSwapRepository doneSwapRepository,
            MarketPermissionService marketPermissionService
    ) throws IOException, DocumentException {
        this.marketRepository = marketRepository;
        this.doneSwapRepository = doneSwapRepository;
        this.marketPermissionService = marketPermissionService;

        arialUnicodeFont = BaseFont.createFont(fontPath, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        authorAndDateFont = new Font(arialUnicodeFont, AUTHOR_AND_DATE_FONT_SIZE, Font.ITALIC);
        titleFont = new Font(arialUnicodeFont, TITLE_FONT_SIZE, Font.BOLD);
    }

    public File generate(String marketName, String userId) throws IOException, DocumentException, ParseException {

        Market market = marketRepository.findOne(marketName);

        if (market == null) {
            throw new NoResultException();
        }

        if (!marketPermissionService.hasUserPermissionToSeeOrEditMarket(market, userId)) {
            throw new SecurityException();
        }

        createReportTime = LocalDateTime.now();

        Document document = new Document();
        String filePath = generateFilePath(marketName);

        File file = new File(filePath);
        file.getParentFile().mkdirs();

        if (!file.createNewFile()) {
            throw new IOException("This file cannot be created!");
        }

        OutputStream fileOutputStream = new FileOutputStream(file, false);
        PdfWriter.getInstance(document, fileOutputStream);

        document.open();

        generateMetaData(document, marketName, userId);
        generateHeader(document, market, userId);

        for (Course course : market.getCourses()) {
            generateSwapsForCourse(document, course);
        }

        document.close();

        return file;
    }

    private String generateFilePath(String marketName) throws ParseException {
        String dateTitleValue = createReportTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd_hh-mm"));
        String formattedReportTitle = marketName.replace(" ", "-");


        return tmpFolder + formattedReportTitle + "_" + dateTitleValue + ".pdf";
    }

    private void generateMetaData(Document document, String marketName, String userId) {
        document.addTitle("Report - " + marketName);
        document.addAuthor(userId);
        document.addCreator("Enroll Me Market");
        document.addCreationDate();
    }

    private void generateHeader(Document document, Market market, String userId) throws DocumentException, IOException {

        int headerColumns = 2;
        PdfPTable headerTable = new PdfPTable(headerColumns);
        PdfPCell imageCell = new PdfPCell();
        PdfPCell authorAndDateCell = new PdfPCell();

        String formattedHeaderCreatedDate = createReportTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + "r.";

        Paragraph authorAndDateParagraph = new Paragraph("Generated " + formattedHeaderCreatedDate + " by " + userId, authorAndDateFont);
        authorAndDateParagraph.setAlignment(Element.ALIGN_RIGHT);


        ClassLoader classLoader = getClass().getClassLoader();
        InputStream imageStream = classLoader.getResourceAsStream(llamaPhotoPath);

        Image llamaImage = Image.getInstance(StreamUtils.copyToByteArray(imageStream));
        llamaImage.scaleAbsolute(LLAMA_PHOTO_SIDE_SIZE, LLAMA_PHOTO_SIDE_SIZE);

        int offset = 0;

        imageCell.addElement(new Chunk(llamaImage, offset, offset));
        imageCell.setBorder(Rectangle.NO_BORDER);
        imageCell.setHorizontalAlignment(Element.ALIGN_LEFT);

        authorAndDateCell.addElement(authorAndDateParagraph);
        authorAndDateCell.setBorder(Rectangle.NO_BORDER);
        authorAndDateCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        headerTable.addCell(imageCell);
        headerTable.addCell(authorAndDateCell);
        headerTable.setWidthPercentage(100.0f);

        Paragraph reportTitle = new Paragraph(market.getName() + " - " + generateSwapsAmount(doneSwapRepository.countByCourse_Market(market)), titleFont);
        reportTitle.setAlignment(Element.ALIGN_CENTER);
        reportTitle.setSpacingAfter(35.0f);

        document.add(headerTable);
        document.add(reportTitle);
    }

    private void generateSwapsForCourse(Document document, Course course) throws DocumentException {

        String courseName = course.getTitle();
        List<DoneSwap> doneSwaps = doneSwapRepository.findByCourse(course);
        doneSwaps.sort(Comparator.comparing(swap -> swap.getSwapOne().getGive().getClassId()));

        String swapsText = generateSwapsAmount(doneSwaps.size());

        Paragraph swapsParagraph = new Paragraph(courseName + " - " + swapsText, new Font(arialUnicodeFont));
        swapsParagraph.setSpacingAfter(10.0f);

        document.add(swapsParagraph);

        if (doneSwaps.isEmpty()) {
            //no swaps in this course, we won't generate a table, just info
            return;
        }

        PdfPTable courseSwapsTable = new PdfPTable(5);
        courseSwapsTable.setWidthPercentage(100);
        courseSwapsTable.setWidths(new float[]{
                COUNTER_COLUMN_PERCENT_WIDTH,
                STUDENT_NAME_COLUMN_PERCENT_WIDTH,
                CLASS_COLUMN_PERCENT_WIDTH,
                STUDENT_NAME_COLUMN_PERCENT_WIDTH,
                CLASS_COLUMN_PERCENT_WIDTH
        });
        courseSwapsTable.setSpacingAfter(13.0f);

        int counter = 0;
        boolean isEven = (counter % 2) == 0;

        generateCourseSwapsTableHeader(courseSwapsTable, isEven);

        for (DoneSwap doneSwap : doneSwaps) {

            counter++;
            isEven = (counter % 2) == 0;

            generateSwapRow(courseSwapsTable, counter, isEven, doneSwap.getSwapOne(), doneSwap.getSwapTwo());
        }

        document.add(courseSwapsTable);
    }

    private String generateSwapsAmount(int swaps) {

        if (swaps == 0) {
            return "No swaps";
        }

        if (swaps == 1) {
            return "1 swap";
        }

        return swaps + " swaps";

    }

    private void generateCourseSwapsTableHeader(PdfPTable courseSwapsTable, boolean isEven) {
        addNewTextCell(NR_CELL_VALUE, courseSwapsTable, isEven);
        addNewTextCell(FIRST_STUDENT_NAME_CELL_VALUE, courseSwapsTable, isEven);
        addNewTextCell(FIRST_CLASS_CELL_VALUE, courseSwapsTable, isEven);
        addNewTextCell(SECOND_STUDENT_NAME_CELL_VALUE, courseSwapsTable, isEven);
        addNewTextCell(SECOND_CLASS_CELL_VALUE, courseSwapsTable, isEven);
    }

    private void generateSwapRow(PdfPTable courseSwapsTable, int counter, boolean isEven, Swap swapOne, Swap swapTwo) {
        addNewTextCell(String.valueOf(counter), courseSwapsTable, isEven);
        addNewTextCell(swapOne.getStudent().getStudentId(), courseSwapsTable, isEven);
        addNewTextCell(buildClassValue(swapOne), courseSwapsTable, isEven);
        addNewTextCell(swapTwo.getStudent().getStudentId(), courseSwapsTable, isEven);
        addNewTextCell(buildClassValue(swapTwo), courseSwapsTable, isEven);
    }

    private void addNewTextCell(String cellValue, PdfPTable table, boolean darkerColor) {
        PdfPCell cell = new PdfPCell();
        cell.setPaddingBottom(5.0f);
        cell.setPaddingTop(-5.0f);
        cell.setVerticalAlignment(Element.ALIGN_TOP);

        BaseColor color = darkerColor ? BaseColor.LIGHT_GRAY : new BaseColor(220, 220, 220);

        cell.addElement(new Paragraph(cellValue, new Font(arialUnicodeFont, PARAGRAPH_TABLE_FONT_SIZE)));
        cell.setBackgroundColor(color);
        table.addCell(cell);
    }

    private String buildClassValue(Swap swap) {

        UClass classGiven = swap.getGive();

        StringBuilder classValueBuilder = new StringBuilder();
        classValueBuilder.append(classGiven.getHost()).append(" - ")
                .append(classGiven.getWeekday()).append(" ");

        if (!"all".equalsIgnoreCase(classGiven.getWeek())) {
            classValueBuilder.append(classGiven.getWeek()).append(" ");
        }

        classValueBuilder.append(getTrimmedTimeForData(classGiven.getBegTime())).append(" - ")
                .append(getTrimmedTimeForData(classGiven.getEndTime()));

        return classValueBuilder.toString();
    }

    private String getTrimmedTimeForData(Time time) {
        //this tiny substring returns good date value, without milliseconds (we have data with ms, ex. 09:35:00 -> 09:35)
        return time.toString().substring(0, 5);
    }
}
