/*
 Copyright © 2010-2016 by FuzzyLite Limited.
 All rights reserved.

 This file is part of jfuzzylite™.

 jfuzzylite™ is free software: you can redistribute it and/or modify it under
 the terms of the FuzzyLite License included with the software.

 You should have received a copy of the FuzzyLite License along with 
 jfuzzylite™. If not, see <http://www.fuzzylite.com/license/>.

 fuzzylite® is a registered trademark of FuzzyLite Limited.
 jfuzzylite™ is a trademark of FuzzyLite Limited.

 */
package com.fuzzylite.imex;

import com.fuzzylite.Engine;
import com.fuzzylite.Op;
import static com.fuzzylite.Op.str;
import com.fuzzylite.activation.Activation;
import com.fuzzylite.defuzzifier.Defuzzifier;
import com.fuzzylite.defuzzifier.IntegralDefuzzifier;
import com.fuzzylite.defuzzifier.WeightedDefuzzifier;
import com.fuzzylite.norm.Norm;
import com.fuzzylite.rule.Rule;
import com.fuzzylite.rule.RuleBlock;
import com.fuzzylite.term.Discrete;
import com.fuzzylite.term.Function;
import com.fuzzylite.term.Linear;
import com.fuzzylite.term.Term;
import com.fuzzylite.variable.InputVariable;
import com.fuzzylite.variable.OutputVariable;
import java.util.regex.Pattern;

public class JavaExporter extends Exporter {

    private boolean usingVariableNames;

    public JavaExporter() {
        this(true);
    }

    public JavaExporter(boolean usingVariableNames) {
        this.usingVariableNames = usingVariableNames;
    }

    public boolean isUsingVariableNames() {
        return usingVariableNames;
    }

    public void setUsingVariableNames(boolean usingVariableNames) {
        this.usingVariableNames = usingVariableNames;
    }

    @Override
    public String toString(Engine engine) {
        StringBuilder result = new StringBuilder();

        result.append("Engine engine = new Engine();\n");
        result.append(String.format(
                "engine.setName(\"%s\");\n", engine.getName()));

        result.append("\n");

        for (InputVariable inputVariable : engine.getInputVariables()) {
            result.append(toString(inputVariable, engine)).append("\n");
        }

        for (OutputVariable outputVariable : engine.getOutputVariables()) {
            result.append(toString(outputVariable, engine)).append("\n");
        }

        for (RuleBlock ruleBlock : engine.getRuleBlocks()) {
            result.append(toString(ruleBlock, engine)).append("\n");
        }

        return result.toString();
    }

    public String toString(InputVariable inputVariable, Engine engine) {
        String name;
        if (isUsingVariableNames()) {
            name = Op.validName(inputVariable.getName());
        } else {
            name = "inputVariable";
            if (engine.numberOfInputVariables() > 1) {
                name += engine.getInputVariables().indexOf(inputVariable) + 1;
            }
        }

        StringBuilder result = new StringBuilder();
        result.append(String.format(
                "InputVariable %s = new InputVariable();\n", name));
        result.append(String.format(
                "%s.setEnabled(%s);\n", name, String.valueOf(inputVariable.isEnabled())));
        result.append(String.format(
                "%s.setName(\"%s\");\n", name, inputVariable.getName()));
        result.append(String.format(
                "%s.setRange(%s, %s);\n", name,
                toString(inputVariable.getMinimum()), toString(inputVariable.getMaximum())));
        result.append(String.format(
                "%s.setLockValueInRange(%s);\n", name, String.valueOf(inputVariable.isLockValueInRange())));
        for (Term term : inputVariable.getTerms()) {
            result.append(String.format("%s.addTerm(%s);\n", name, toString(term)));
        }
        result.append(String.format("engine.addInputVariable(%s);\n", name));
        return result.toString();
    }

    public String toString(OutputVariable outputVariable, Engine engine) {
        String name;
        if (isUsingVariableNames()) {
            name = Op.validName(outputVariable.getName());
        } else {
            name = "outputVariable";
            if (engine.numberOfOutputVariables() > 1) {
                name += engine.getOutputVariables().indexOf(outputVariable) + 1;
            }
        }
        StringBuilder result = new StringBuilder();
        result.append(String.format(
                "OutputVariable %s = new OutputVariable();\n", name));
        result.append(String.format(
                "%s.setEnabled(%s);\n", name, String.valueOf(outputVariable.isEnabled())));
        result.append(String.format(
                "%s.setName(\"%s\");\n", name, outputVariable.getName()));
        result.append(String.format(
                "%s.setRange(%s, %s);\n", name,
                toString(outputVariable.getMinimum()), toString(outputVariable.getMaximum())));
        result.append(String.format(
                "%s.setLockValueInRange(%s);\n", name, String.valueOf(outputVariable.isLockValueInRange())));
        result.append(String.format(
                "%s.fuzzyOutput().setAggregation(%s);\n",
                name, toString(outputVariable.fuzzyOutput().getAggregation())));
        result.append(String.format(
                "%s.setDefuzzifier(%s);\n", name,
                toString(outputVariable.getDefuzzifier())));
        result.append(String.format(
                "%s.setDefaultValue(%s);\n", name,
                toString(outputVariable.getDefaultValue())));
        result.append(String.format(
                "%s.setLockPreviousValue(%s);\n", name,
                outputVariable.isLockPreviousValue()));
        for (Term term : outputVariable.getTerms()) {
            result.append(String.format("%s.addTerm(%s);\n",
                    name, toString(term)));
        }
        result.append(String.format(
                "engine.addOutputVariable(%s);\n", name));
        return result.toString();
    }

    public String toString(RuleBlock ruleBlock, Engine engine) {
        String name = "ruleBlock";
        if (engine.numberOfRuleBlocks() > 1) {
            name += engine.getRuleBlocks().indexOf(ruleBlock) + 1;
        }
        StringBuilder result = new StringBuilder();
        result.append(String.format("RuleBlock %s = new RuleBlock();\n", name));
        result.append(String.format(
                "%s.setEnabled(%s);\n", name, String.valueOf(ruleBlock.isEnabled())));
        result.append(String.format(
                "%s.setName(\"%s\");\n", name, ruleBlock.getName()));
        result.append(String.format(
                "%s.setConjunction(%s);\n", name, toString(ruleBlock.getConjunction())));
        result.append(String.format(
                "%s.setDisjunction(%s);\n", name, toString(ruleBlock.getDisjunction())));
        result.append(String.format(
                "%s.setImplication(%s);\n", name, toString(ruleBlock.getImplication())));
        result.append(String.format(
                "%s.setActivation(%s);\n", name, toString(ruleBlock.getActivation())));

        for (Rule rule : ruleBlock.getRules()) {
            result.append(String.format("%s.addRule(Rule.parse(\"%s\", engine));\n",
                    name, rule.getText()));
        }
        result.append(String.format(
                "engine.addRuleBlock(%s);\n", name));
        return result.toString();
    }

    public String toString(Term term) {
        if (term == null) {
            return "null";
        }
        if (term instanceof Discrete) {
            Discrete t = (Discrete) term;
            String result = String.format("%s.create(\"%s\", %s)",
                    Discrete.class.getSimpleName(), term.getName(),
                    Op.join(Discrete.toList(t.getXY()), ", "));
            return result;
        }
        if (term instanceof Function) {
            Function t = (Function) term;
            String result = String.format("%s.create(\"%s\", \"%s\", engine)",
                    Function.class.getSimpleName(), term.getName(),
                    t.getFormula());
            return result;
        }
        if (term instanceof Linear) {
            Linear t = (Linear) term;
            String result = String.format("%s.create(\"%s\", engine, %s)",
                    Linear.class.getSimpleName(), term.getName(),
                    Op.join(t.getCoefficients(), ", "));
            return result;
        }

        String result = String.format("new %s(\"%s\", %s)",
                term.getClass().getSimpleName(), term.getName(),
                term.parameters().replaceAll(Pattern.quote(" "), ", "));
        return result;
    }

    public String toString(Defuzzifier defuzzifier) {
        if (defuzzifier == null) {
            return "null";
        }
        if (defuzzifier instanceof IntegralDefuzzifier) {
            return String.format("new %s(%d)",
                    defuzzifier.getClass().getSimpleName(),
                    ((IntegralDefuzzifier) defuzzifier).getResolution());
        }
        if (defuzzifier instanceof WeightedDefuzzifier) {
            return String.format("new %s(\"%s\")",
                    defuzzifier.getClass().getSimpleName(),
                    ((WeightedDefuzzifier) defuzzifier).getType());
        }
        return String.format("new %s()", defuzzifier.getClass().getSimpleName());
    }

    public String toString(Activation activation) {
        if (activation == null) {
            return "fl:null";
        }
        String parameters = activation.parameters().trim();
        if (parameters.isEmpty()) {
            return "new " + activation.getClass().getSimpleName() + "()";
        }
        return "new " + activation.getClass().getSimpleName()
                + String.format("\"%s\"", parameters);
    }

    public String toString(Norm norm) {
        if (norm == null) {
            return "null";
        }
        return String.format("new %s()", norm.getClass().getSimpleName());
    }

    public String toString(double value) {
        if (Double.isNaN(value)) {
            return "Double.NaN";
        } else if (Double.isInfinite(value)) {
            return value > 0 ? "Double.POSITIVE_INFINITY"
                    : "Double.NEGATIVE_INFINITY";
        }
        return str(value);
    }

    @Override
    public JavaExporter clone() throws CloneNotSupportedException {
        return (JavaExporter) super.clone();
    }

}
