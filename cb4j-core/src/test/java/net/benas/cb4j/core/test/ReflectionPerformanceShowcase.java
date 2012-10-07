/*
 * The MIT License
 *
 *  Copyright (c) 2012, benas (md.benhassine@gmail.com)
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *  THE SOFTWARE.
 */

package net.benas.cb4j.core.test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;


/**
 * This class is a show case of performance issues when using Java Reflection API to create objects.<br/>
 * Many techniques exist to map CSV records to java objects : Mapping CSV headers to POJO properties, using annotation on POJO properties, using XML mapping metadata, etc. But all these techniques require Java Reflection API to dynamically introspect bean properties at runtime and populate them.<br/>
 * CB4J does not provide any default CSV to Object mapping code for performance reason.<br/>
 * Writing the mapping logic explicitly by the user (by implementing {@link net.benas.cb4j.core.api.RecordMapper}) is not costly (seriously!) and results in about 500x faster code as demonstrated in this show case!<br/>
 * See "Drawbacks of Reflection" section in Java's official documentation : <a href="http://docs.oracle.com/javase/tutorial/reflect/index.html">Java Trail: The Reflection API</a>
 * @author benas (md.benhassine@gmail.com)
 */
public class ReflectionPerformanceShowcase {

    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {

        Greeting g1 = getGreetingUsingReflection();
        System.out.println("g1 = " + g1);

        Greeting g2 = getGreetingFromMapper();
        System.out.println("g2 = " + g2);

        /**
         * Creating objects using Java Reflection API
         */
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
             Greeting g = getGreetingUsingReflection();
            // process greeting object
        }
        long reflectionEstimatedExecutionTime = System.currentTimeMillis() - startTime;
        System.out.println("Time elapsed using reflection = " + reflectionEstimatedExecutionTime + " ms");

        /**
         * Creating objects using user defined mapper
         */
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000000; i++) {
            Greeting g = getGreetingFromMapper();
            // process greeting object
        }
        long noReflectionEstimatedExecutionTime = System.currentTimeMillis() - startTime;
        System.out.println("Time elapsed without using reflection = " + noReflectionEstimatedExecutionTime + " ms");

        System.out.println("Conclusion : using reflection is " + (reflectionEstimatedExecutionTime / noReflectionEstimatedExecutionTime) + " times slower than not using it!");
    }

    /*
    * This method does what would a DefaultRecordMapper do
    */
    private static Greeting getGreetingUsingReflection() throws ClassNotFoundException, IllegalAccessException, InstantiationException, InvocationTargetException {

        Class c = Class.forName(Greeting.class.getName());
        Greeting greeting = (Greeting) c.newInstance();

        List<String> csvHeaders = new ArrayList<String>();
        csvHeaders.add("firstName"); csvHeaders.add("lastName");

        for(String header : csvHeaders){
            String setterName = "set" + header.substring(0,1).toUpperCase() + header.substring(1);//javabean naming convention
            for( final Method method : Greeting.class.getMethods() ) {
                if (method.getName().equals(setterName)){
                    method.invoke(greeting,"someData");
                }
            }
        }
        return greeting;
    }

    /*
     * This method does what would a GreetingMapper do
     */
    private static Greeting getGreetingFromMapper() {
        Greeting greeting = new Greeting();

        greeting.setFirstName("someData");
        greeting.setLastName("someData");

        return greeting;
    }

}

class Greeting {

    private String firstName;

    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "Greeting{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}
