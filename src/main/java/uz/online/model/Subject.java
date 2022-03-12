package uz.online.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Data


public class Subject {

    private Integer id;
    private String name;
    private double mark;
    private double testPrice;
    private List<Test> tests = new ArrayList<>();

    public Subject(String name, double mark, double testPrice, List<Test> tests) {
        this.name = name;
        this.mark = mark;
        this.testPrice = testPrice;
        this.tests = tests;
    }

    public Subject(String name) {
        this.name = name;
    }
}
