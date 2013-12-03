/*   Copyright 2013 Juan Rada-Vilela

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */
package com.fuzzylite.term;

import static com.fuzzylite.Op.str;

/**
 *
 * @author jcrada
 */
public class Constant extends Term {

    protected double value;

    public Constant() {
        this("");
    }

    public Constant(String name) {
        this(name, Double.NaN);
    }

    public Constant(String name, double value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public double membership(double x) {
        return this.value;
    }

    @Override
    public String toString() {
        return Constant.class.getSimpleName() + " (" + str(this.value) + ")";
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    @Override
    public void configure(double[] parameters) {
        int required = 1;
        if (parameters.length < required) {
            throw new RuntimeException(String.format(
                    "[configuration error] term <%s> requires <%d> parameters",
                    this.getClass().getSimpleName(), required));
        }
        setValue(parameters[0]);
    }
}
