package knbit.emm.controller;

import com.itextpdf.text.DocumentException;
import io.jsonwebtoken.ExpiredJwtException;
import knbit.emm.algo.builder.GraphBuilder;
import knbit.emm.utilities.ReportGenerator;
import knbit.emm.utilities.TokenUtils;
import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

@Controller
public class ReportController {
    private final ReportGenerator reportGenerator;
    private static final Logger logger = Logger.getLogger(GraphBuilder.class.getName());


    @Autowired
    public ReportController(ReportGenerator reportGenerator) {
        this.reportGenerator = reportGenerator;
    }

    @RequestMapping(value = "/api/report/{marketName}", method = RequestMethod.GET)
    public void generateReport(@PathVariable String marketName,
                                                 @RequestHeader(value = "Authorization") String token,
                                                 HttpServletResponse response
    ) {
        try {
            if (!TokenUtils.verifyStarostaOrAdmin(token)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } catch (ExpiredJwtException e) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }

        try {
            String userId = TokenUtils.getStudentId(token);
            File report = reportGenerator.generate(marketName, userId);
            response.setContentType("application/pdf");
            response.setStatus(HttpServletResponse.SC_OK);

            try (InputStream inputStream = new FileInputStream(report)) {
                IOUtils.copy(inputStream, response.getOutputStream());
            }

        } catch (ParseException | SecurityException | IOException e) {
            logger.warn(e.getStackTrace());
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        } catch (DocumentException | NoResultException e) {
            logger.warn(e.getStackTrace());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
