package knbit.emm.parser;

import knbit.emm.model.Student;

import java.util.List;

public interface ParsingTimetablesAlgorithm {
    List<Student> parse(TermsHolder currentTerms);
}
